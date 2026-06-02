"""
COBOL Fixed-Width Data File Parser

Reads fixed-width COBOL data files based on copybook PIC clause definitions.
Supports:
  - PIC X(n): Alphanumeric fields
  - PIC 9(n): Unsigned numeric display fields
  - PIC S9(n)V99: Signed numeric with trailing overpunch encoding
  - PIC S9(n)V99 COMP-3: Packed decimal fields
  - PIC 9(n) COMP: Binary integer fields
"""

import json
import struct
from dataclasses import dataclass, field as dataclass_field
from decimal import Decimal
from enum import Enum
from pathlib import Path
from typing import Optional


class FieldType(Enum):
    ALPHANUMERIC = "alphanumeric"
    NUMERIC_DISPLAY = "numeric_display"
    SIGNED_DECIMAL = "signed_decimal"
    COMP3_PACKED = "comp3_packed"
    COMP_BINARY = "comp_binary"
    FILLER = "filler"


# Overpunch decode tables
POSITIVE_OVERPUNCH = {
    '{': 0, 'A': 1, 'B': 2, 'C': 3, 'D': 4,
    'E': 5, 'F': 6, 'G': 7, 'H': 8, 'I': 9
}
NEGATIVE_OVERPUNCH = {
    '}': 0, 'J': 1, 'K': 2, 'L': 3, 'M': 4,
    'N': 5, 'O': 6, 'P': 7, 'Q': 8, 'R': 9
}

# Reverse tables for encoding
POSITIVE_ENCODE = {v: k for k, v in POSITIVE_OVERPUNCH.items()}
NEGATIVE_ENCODE = {v: k for k, v in NEGATIVE_OVERPUNCH.items()}


@dataclass
class FieldDefinition:
    """Definition of a single field in a COBOL record layout."""
    name: str
    offset: int
    length: int
    pic: str
    field_type: FieldType
    scale: int = 0
    description: str = ""

    def to_dict(self) -> dict:
        d = {
            "name": self.name,
            "offset": self.offset,
            "length": self.length,
            "pic": self.pic,
            "type": self.field_type.value,
            "description": self.description,
        }
        if self.scale > 0:
            d["decimal_scale"] = self.scale
        return d


@dataclass
class RecordLayout:
    """Complete record layout for a COBOL file."""
    name: str
    record_length: int
    copybook: str
    fields: list[FieldDefinition] = dataclass_field(default_factory=list)

    def data_fields(self) -> list[FieldDefinition]:
        """Return only non-FILLER fields."""
        return [f for f in self.fields if f.field_type != FieldType.FILLER]


def decode_overpunch(raw: str, scale: int) -> Decimal:
    """
    Decode a COBOL trailing-overpunch signed numeric display field.

    The last character encodes both the last digit and the sign.
    PIC S9(10)V99 has 12 display bytes, scale=2 (2 implied decimals).

    Examples:
        "00000001940{" with scale=2 -> Decimal("194.00")
        "00000001940}" with scale=2 -> Decimal("-194.00")
        "00000009190}" with scale=2 -> Decimal("-919.00")
    """
    if not raw or raw.strip() == '':
        return Decimal('0')

    last_char = raw[-1]
    digits_before = raw[:-1]

    if last_char in POSITIVE_OVERPUNCH:
        last_digit = POSITIVE_OVERPUNCH[last_char]
        negative = False
    elif last_char in NEGATIVE_OVERPUNCH:
        last_digit = NEGATIVE_OVERPUNCH[last_char]
        negative = True
    elif last_char.isdigit():
        all_digits = raw
        d = Decimal(all_digits)
        if scale > 0:
            d = d * Decimal(10) ** (-scale)
        return d
    else:
        return Decimal('0')

    all_digits = digits_before + str(last_digit)
    d = Decimal(all_digits)
    if scale > 0:
        d = d * Decimal(10) ** (-scale)
    if negative and d != 0:
        d = -d
    return d


def encode_overpunch(value: Decimal, total_digits: int, scale: int) -> str:
    """
    Encode a Decimal value into COBOL trailing-overpunch format.

    Reverse of decode_overpunch.
    """
    negative = value < 0
    abs_val = abs(value)
    shifted = abs_val * Decimal(10) ** scale
    digits_str = str(int(shifted)).zfill(total_digits)

    if len(digits_str) > total_digits:
        raise ValueError(f"Value {value} exceeds {total_digits} digits")

    last_digit = int(digits_str[-1])
    prefix = digits_str[:-1]

    if negative:
        overpunch = NEGATIVE_ENCODE[last_digit]
    else:
        overpunch = POSITIVE_ENCODE[last_digit]

    return prefix + overpunch


def decode_comp3(data: bytes, scale: int) -> Decimal:
    """
    Decode COBOL COMP-3 (packed decimal) bytes to a Decimal.

    Format: two digits per byte, last nibble is sign (0x0C=+, 0x0D=-).
    """
    digits = []
    negative = False

    for i, byte_val in enumerate(data):
        high = (byte_val >> 4) & 0x0F
        low = byte_val & 0x0F
        if i == len(data) - 1:
            digits.append(str(high))
            negative = (low == 0x0D)
        else:
            digits.append(str(high))
            digits.append(str(low))

    d = Decimal(''.join(digits))
    if scale > 0:
        d = d * Decimal(10) ** (-scale)
    if negative:
        d = -d
    return d


def encode_comp3(value: Decimal, total_digits: int, scale: int) -> bytes:
    """
    Encode a Decimal value into COBOL COMP-3 (packed decimal) bytes.

    Reverse of decode_comp3.
    """
    negative = value < 0
    abs_val = abs(value)
    shifted = abs_val * Decimal(10) ** scale
    digits_str = str(int(shifted)).zfill(total_digits)

    total_nibbles = total_digits + 1  # digits + sign nibble
    byte_length = (total_nibbles + 1) // 2
    result = bytearray(byte_length)

    total_slots = byte_length * 2
    nibble_start = total_slots - total_digits - 1

    for i, ch in enumerate(digits_str):
        digit_val = int(ch)
        slot = nibble_start + i
        byte_pos = slot // 2
        if slot % 2 == 0:
            result[byte_pos] |= (digit_val << 4)
        else:
            result[byte_pos] |= digit_val

    sign_nibble = 0x0D if negative else 0x0C
    result[-1] = (result[-1] & 0xF0) | sign_nibble

    return bytes(result)


def decode_comp_binary(data: bytes) -> int:
    """Decode COBOL COMP (binary) big-endian integer."""
    if len(data) == 2:
        return struct.unpack('>H', data)[0]
    elif len(data) == 4:
        return struct.unpack('>I', data)[0]
    elif len(data) == 8:
        return struct.unpack('>Q', data)[0]
    else:
        return int.from_bytes(data, byteorder='big')


def parse_field(line: str, raw_bytes: Optional[bytes],
                field_def: FieldDefinition) -> str | Decimal | int:
    """Parse a single field from a record line/bytes based on its definition."""
    if field_def.field_type == FieldType.FILLER:
        return None

    if field_def.field_type == FieldType.COMP3_PACKED:
        if raw_bytes is None:
            raise ValueError("Binary data required for COMP-3 fields")
        chunk = raw_bytes[field_def.offset:field_def.offset + field_def.length]
        return decode_comp3(chunk, field_def.scale)

    if field_def.field_type == FieldType.COMP_BINARY:
        if raw_bytes is None:
            raise ValueError("Binary data required for COMP fields")
        chunk = raw_bytes[field_def.offset:field_def.offset + field_def.length]
        return decode_comp_binary(chunk)

    raw = line[field_def.offset:field_def.offset + field_def.length]

    if field_def.field_type == FieldType.SIGNED_DECIMAL:
        return decode_overpunch(raw, field_def.scale)
    elif field_def.field_type == FieldType.NUMERIC_DISPLAY:
        return raw.strip()
    elif field_def.field_type == FieldType.ALPHANUMERIC:
        return raw.rstrip()
    else:
        return raw.rstrip()


def parse_record(line: str, layout: RecordLayout,
                 raw_bytes: Optional[bytes] = None) -> dict:
    """Parse a complete fixed-width record into a dict of field values."""
    record = {}
    for f in layout.fields:
        if f.field_type == FieldType.FILLER:
            continue
        val = parse_field(line, raw_bytes, f)
        if isinstance(val, Decimal):
            record[f.name] = str(val)
        else:
            record[f.name] = val
    return record


def parse_file(filepath: str | Path, layout: RecordLayout,
               binary: bool = False) -> list[dict]:
    """
    Parse an entire fixed-width COBOL data file.

    Args:
        filepath: Path to the data file.
        layout: Record layout defining field positions and types.
        binary: If True, read as binary (needed for COMP-3/COMP fields).

    Returns:
        List of parsed record dicts.
    """
    filepath = Path(filepath)
    records = []

    if binary:
        with open(filepath, 'rb') as f:
            while True:
                raw = f.read(layout.record_length)
                if not raw or len(raw) < layout.record_length:
                    break
                line = raw.decode('ascii', errors='replace')
                record = parse_record(line, layout, raw_bytes=raw)
                record["_record_number"] = len(records) + 1
                records.append(record)
    else:
        with open(filepath, 'r', encoding='ascii', errors='replace') as f:
            for line_num, line in enumerate(f, 1):
                line = line.rstrip('\n').rstrip('\r')
                if len(line) < layout.record_length:
                    line = line.ljust(layout.record_length)
                record = parse_record(line, layout)
                record["_record_number"] = line_num
                records.append(record)

    return records


def parse_file_to_json(filepath: str | Path, layout: RecordLayout,
                       output_path: str | Path, binary: bool = False) -> dict:
    """Parse a data file and write the result as JSON."""
    records = parse_file(filepath, layout, binary)

    result = {
        "metadata": {
            "source_file": str(Path(filepath).name),
            "record_length": layout.record_length,
            "record_count": len(records),
            "copybook": layout.copybook,
        },
        "field_definitions": [f.to_dict() for f in layout.data_fields()],
        "records": records,
    }

    with open(output_path, 'w') as f:
        json.dump(result, f, indent=2, default=str)

    return result
