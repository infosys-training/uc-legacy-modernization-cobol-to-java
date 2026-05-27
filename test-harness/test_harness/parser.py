"""
Parser utility for COBOL fixed-width data files.

Reads files based on copybook PIC clause definitions and produces
structured Python dicts (serializable to JSON).
"""

from decimal import Decimal, InvalidOperation
from pathlib import Path
from typing import Optional

from test_harness.copybook_layouts import FILE_LAYOUTS

# Sign overpunch mapping: trailing character -> (digit, is_negative)
_POSITIVE_OVERPUNCH = {
    "{": 0, "A": 1, "B": 2, "C": 3, "D": 4,
    "E": 5, "F": 6, "G": 7, "H": 8, "I": 9,
}
_NEGATIVE_OVERPUNCH = {
    "}": 0, "J": 1, "K": 2, "L": 3, "M": 4,
    "N": 5, "O": 6, "P": 7, "Q": 8, "R": 9,
}


def decode_zoned_decimal(raw: str, pic: str) -> Decimal:
    """Parse a COBOL zoned-decimal field with trailing sign overpunch.

    Supports PIC clauses like 'S9(10)V99', 'S9(9)V99', 'S9(4)V99'.
    """
    if not raw or raw.isspace():
        return Decimal("0")

    # Determine decimal places from PIC clause
    decimal_digits = 0
    if "V" in pic:
        after_v = pic.split("V")[1]
        # Extract digits count: V99 -> 2, V9(4) -> 4
        if "(" in after_v:
            decimal_digits = int(after_v.split("(")[1].rstrip(")"))
        else:
            decimal_digits = sum(1 for c in after_v if c == "9")

    last_char = raw[-1]
    negative = False

    if last_char in _POSITIVE_OVERPUNCH:
        last_digit = _POSITIVE_OVERPUNCH[last_char]
        negative = False
    elif last_char in _NEGATIVE_OVERPUNCH:
        last_digit = _NEGATIVE_OVERPUNCH[last_char]
        negative = True
    elif last_char.isdigit():
        last_digit = int(last_char)
        negative = False
    else:
        raise ValueError(f"Invalid overpunch character: '{last_char}' in '{raw}'")

    digits = raw[:-1] + str(last_digit)

    if decimal_digits > 0:
        integer_part = digits[: len(digits) - decimal_digits]
        decimal_part = digits[len(digits) - decimal_digits :]
        numeric_str = f"{integer_part}.{decimal_part}"
    else:
        numeric_str = digits

    if negative:
        numeric_str = "-" + numeric_str

    try:
        return Decimal(numeric_str)
    except InvalidOperation:
        raise ValueError(f"Cannot parse '{raw}' as decimal: constructed '{numeric_str}'")


def parse_field(raw: str, pic: str) -> str | Decimal:
    """Parse a single field value based on its PIC clause."""
    if pic == "FILLER":
        return raw

    if pic.startswith("S9") and "V" in pic:
        return decode_zoned_decimal(raw, pic)

    if pic.startswith("9("):
        stripped = raw.strip()
        if not stripped:
            return "0"
        return stripped

    # Alphanumeric: strip trailing spaces
    return raw.rstrip()


def parse_record(line: str, layout: list) -> dict:
    """Parse a single fixed-width record into a dict based on the layout."""
    record = {}
    pos = 0
    for field_name, pic, length in layout:
        raw = line[pos : pos + length] if pos + length <= len(line) else line[pos:].ljust(length)
        pos += length

        if pic == "FILLER":
            continue

        record[field_name] = parse_field(raw, pic)

    return record


def parse_file(filepath: Path, layout: list) -> list[dict]:
    """Parse an entire fixed-width data file into a list of record dicts."""
    records = []
    with open(filepath, "r", encoding="ascii", errors="replace") as f:
        for line_num, line in enumerate(f, 1):
            line = line.rstrip("\n").rstrip("\r")
            if not line:
                continue
            try:
                record = parse_record(line, layout)
                record["_line_number"] = line_num
                records.append(record)
            except Exception as e:
                raise ValueError(f"Error parsing {filepath.name} line {line_num}: {e}") from e
    return records


def parse_data_file(filepath: Path, layout_name: Optional[str] = None) -> list[dict]:
    """Parse a data file, auto-detecting layout from filename if not specified."""
    if layout_name is None:
        layout_name = filepath.stem
    layout = FILE_LAYOUTS.get(layout_name)
    if layout is None:
        raise ValueError(
            f"Unknown layout '{layout_name}'. Available: {list(FILE_LAYOUTS.keys())}"
        )
    return parse_file(filepath, layout)
