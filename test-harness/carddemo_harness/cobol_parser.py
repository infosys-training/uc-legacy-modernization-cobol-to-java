"""Parser utility for COBOL fixed-width data files based on copybook PIC clause definitions.

Handles:
- Fixed-width field extraction by byte offset and length
- Trailing overpunch decoding for PIC S9(n)V99 signed decimal fields
- Numeric field parsing for PIC 9(n) unsigned integer fields
- Alphanumeric field extraction with trailing space trimming
"""

from decimal import Decimal, InvalidOperation
from pathlib import Path
from typing import Any

from .copybook_layouts import FieldDef, RecordLayout, FILE_LAYOUTS


# Overpunch character → (digit, sign_multiplier)
OVERPUNCH_MAP: dict[str, tuple[int, int]] = {
    "{": (0,  1), "A": (1,  1), "B": (2,  1), "C": (3,  1), "D": (4,  1),
    "E": (5,  1), "F": (6,  1), "G": (7,  1), "H": (8,  1), "I": (9,  1),
    "}": (0, -1), "J": (1, -1), "K": (2, -1), "L": (3, -1), "M": (4, -1),
    "N": (5, -1), "O": (6, -1), "P": (7, -1), "Q": (8, -1), "R": (9, -1),
}


def decode_overpunch(raw: str, scale: int) -> Decimal:
    """Decode a COBOL DISPLAY-format signed numeric field with trailing overpunch.

    Args:
        raw: The raw string from the fixed-width record (e.g., '00000001940{')
        scale: Number of implied decimal places (digits after V in PIC clause)

    Returns:
        Decoded Decimal value (e.g., Decimal('194.00'))
    """
    if not raw or raw.isspace():
        return Decimal("0")

    last_char = raw[-1]
    entry = OVERPUNCH_MAP.get(last_char)

    if entry is not None:
        digit, sign = entry
        digits = raw[:-1] + str(digit)
    else:
        digits = raw
        sign = 1

    try:
        value = Decimal(digits)
    except InvalidOperation:
        return Decimal("0.00") if scale == 2 else Decimal("0")

    if scale > 0:
        # Use quantize to preserve decimal places (e.g., 194.00 not 194)
        divisor = Decimal(10) ** scale
        value = value / divisor
        quantizer = Decimal("0." + "0" * scale)
        value = value.quantize(quantizer)

    return value if sign > 0 else -value


def parse_field(raw: str, field: FieldDef) -> Any:
    """Parse a single field value from its raw string representation.

    Returns:
        - Decimal for signed_decimal fields
        - int for numeric fields
        - str (trimmed) for alphanumeric fields
    """
    if field.data_type == "signed_decimal":
        return decode_overpunch(raw, field.scale)
    elif field.data_type == "numeric":
        stripped = raw.strip()
        if not stripped or not stripped.isdigit():
            return 0
        return int(stripped)
    else:
        return raw.rstrip()


def parse_record(line: str, layout: RecordLayout) -> dict[str, Any]:
    """Parse a single fixed-width record line into a dictionary of field values.

    Args:
        line: The raw record line (may include trailing newline/CR)
        layout: The RecordLayout defining field positions

    Returns:
        Dictionary mapping field names to parsed values.
        FILLER fields are excluded.
    """
    result: dict[str, Any] = {}

    for field in layout.fields:
        if field.name == "FILLER":
            continue

        end = field.offset + field.length
        if end > len(line):
            raw = line[field.offset:].ljust(field.length)
        else:
            raw = line[field.offset:end]

        result[field.name] = parse_field(raw, field)

    return result


def parse_file(file_path: Path, layout: RecordLayout) -> list[dict[str, Any]]:
    """Parse an entire COBOL fixed-width data file.

    Args:
        file_path: Path to the ASCII data file
        layout: The RecordLayout defining the file's record structure

    Returns:
        List of parsed record dictionaries
    """
    records: list[dict[str, Any]] = []

    with open(file_path, "r", encoding="ascii", errors="replace") as f:
        for line in f:
            # Strip trailing newline/CR but preserve record content
            line = line.rstrip("\n").rstrip("\r")
            if not line or line.isspace():
                continue
            records.append(parse_record(line, layout))

    return records


def parse_data_file(file_path: Path) -> list[dict[str, Any]]:
    """Parse a CardDemo data file, auto-detecting layout from filename.

    Args:
        file_path: Path to the ASCII data file (must match a known filename)

    Returns:
        List of parsed record dictionaries

    Raises:
        ValueError: If the filename doesn't match any known layout
    """
    filename = file_path.name
    layout = FILE_LAYOUTS.get(filename)
    if layout is None:
        raise ValueError(
            f"Unknown data file: {filename}. "
            f"Known files: {', '.join(FILE_LAYOUTS.keys())}"
        )
    return parse_file(file_path, layout)
