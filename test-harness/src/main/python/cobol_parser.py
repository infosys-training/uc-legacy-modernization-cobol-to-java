"""
Parse fixed-width COBOL data files based on copybook PIC clause definitions.

Each copybook layout is defined as a list of field specs:
    (field_name, pic_clause, length, field_type)

field_type is one of:
    'numeric'       — unsigned integer (PIC 9(n))
    'signed_decimal'— signed with implied decimal (PIC S9(n)V99)
    'alpha'         — alphanumeric (PIC X(n))
    'filler'        — ignored padding bytes
"""

from __future__ import annotations

import json
import sys
from dataclasses import dataclass, field
from datetime import datetime, timezone
from decimal import Decimal, InvalidOperation
from pathlib import Path
from typing import Any


# ── Overpunch sign table ──────────────────────────────────────────────
POSITIVE_OVERPUNCH = {"{": 0, "A": 1, "B": 2, "C": 3, "D": 4,
                      "E": 5, "F": 6, "G": 7, "H": 8, "I": 9}
NEGATIVE_OVERPUNCH = {"}": 0, "J": 1, "K": 2, "L": 3, "M": 4,
                      "N": 5, "O": 6, "P": 7, "Q": 8, "R": 9}


@dataclass
class FieldSpec:
    name: str
    length: int
    field_type: str  # 'numeric', 'signed_decimal', 'alpha', 'filler'
    scale: int = 0   # decimal places (digits after V)


# ── Copybook layouts ─────────────────────────────────────────────────

ACCOUNT_LAYOUT: list[FieldSpec] = [
    FieldSpec("ACCT-ID", 11, "numeric"),
    FieldSpec("ACCT-ACTIVE-STATUS", 1, "alpha"),
    FieldSpec("ACCT-CURR-BAL", 12, "signed_decimal", scale=2),
    FieldSpec("ACCT-CREDIT-LIMIT", 12, "signed_decimal", scale=2),
    FieldSpec("ACCT-CASH-CREDIT-LIMIT", 12, "signed_decimal", scale=2),
    FieldSpec("ACCT-OPEN-DATE", 10, "alpha"),
    FieldSpec("ACCT-EXPIRAION-DATE", 10, "alpha"),
    FieldSpec("ACCT-REISSUE-DATE", 10, "alpha"),
    FieldSpec("ACCT-CURR-CYC-CREDIT", 12, "signed_decimal", scale=2),
    FieldSpec("ACCT-CURR-CYC-DEBIT", 12, "signed_decimal", scale=2),
    FieldSpec("ACCT-ADDR-ZIP", 10, "alpha"),
    FieldSpec("ACCT-GROUP-ID", 10, "alpha"),
    FieldSpec("FILLER", 178, "filler"),
]

CARD_LAYOUT: list[FieldSpec] = [
    FieldSpec("CARD-NUM", 16, "alpha"),
    FieldSpec("CARD-ACCT-ID", 11, "numeric"),
    FieldSpec("CARD-CVV-CD", 3, "numeric"),
    FieldSpec("CARD-EMBOSSED-NAME", 50, "alpha"),
    FieldSpec("CARD-EXPIRAION-DATE", 10, "alpha"),
    FieldSpec("CARD-ACTIVE-STATUS", 1, "alpha"),
    FieldSpec("FILLER", 59, "filler"),
]

CARD_XREF_LAYOUT: list[FieldSpec] = [
    FieldSpec("XREF-CARD-NUM", 16, "alpha"),
    FieldSpec("XREF-CUST-ID", 9, "numeric"),
    FieldSpec("XREF-ACCT-ID", 11, "numeric"),
    FieldSpec("FILLER", 14, "filler"),
]

CUSTOMER_LAYOUT: list[FieldSpec] = [
    FieldSpec("CUST-ID", 9, "numeric"),
    FieldSpec("CUST-FIRST-NAME", 25, "alpha"),
    FieldSpec("CUST-MIDDLE-NAME", 25, "alpha"),
    FieldSpec("CUST-LAST-NAME", 25, "alpha"),
    FieldSpec("CUST-ADDR-LINE-1", 50, "alpha"),
    FieldSpec("CUST-ADDR-LINE-2", 50, "alpha"),
    FieldSpec("CUST-ADDR-LINE-3", 50, "alpha"),
    FieldSpec("CUST-ADDR-STATE-CD", 2, "alpha"),
    FieldSpec("CUST-ADDR-COUNTRY-CD", 3, "alpha"),
    FieldSpec("CUST-ADDR-ZIP", 10, "alpha"),
    FieldSpec("CUST-PHONE-NUM-1", 15, "alpha"),
    FieldSpec("CUST-PHONE-NUM-2", 15, "alpha"),
    FieldSpec("CUST-SSN", 9, "numeric"),
    FieldSpec("CUST-GOVT-ISSUED-ID", 20, "alpha"),
    FieldSpec("CUST-DOB-YYYY-MM-DD", 10, "alpha"),
    FieldSpec("CUST-EFT-ACCOUNT-ID", 10, "alpha"),
    FieldSpec("CUST-PRI-CARD-HOLDER-IND", 1, "alpha"),
    FieldSpec("CUST-FICO-CREDIT-SCORE", 3, "numeric"),
    FieldSpec("FILLER", 168, "filler"),
]

TRANSACTION_LAYOUT: list[FieldSpec] = [
    FieldSpec("TRAN-ID", 16, "alpha"),
    FieldSpec("TRAN-TYPE-CD", 2, "alpha"),
    FieldSpec("TRAN-CAT-CD", 4, "numeric"),
    FieldSpec("TRAN-SOURCE", 10, "alpha"),
    FieldSpec("TRAN-DESC", 100, "alpha"),
    FieldSpec("TRAN-AMT", 11, "signed_decimal", scale=2),
    FieldSpec("TRAN-MERCHANT-ID", 9, "numeric"),
    FieldSpec("TRAN-MERCHANT-NAME", 50, "alpha"),
    FieldSpec("TRAN-MERCHANT-CITY", 50, "alpha"),
    FieldSpec("TRAN-MERCHANT-ZIP", 10, "alpha"),
    FieldSpec("TRAN-CARD-NUM", 16, "alpha"),
    FieldSpec("TRAN-ORIG-TS", 26, "alpha"),
    FieldSpec("TRAN-PROC-TS", 26, "alpha"),
    FieldSpec("FILLER", 20, "filler"),
]

DAILY_TRAN_LAYOUT: list[FieldSpec] = TRANSACTION_LAYOUT  # Same as CVTRA05Y

DISC_GROUP_LAYOUT: list[FieldSpec] = [
    FieldSpec("DIS-ACCT-GROUP-ID", 10, "alpha"),
    FieldSpec("DIS-TRAN-TYPE-CD", 2, "alpha"),
    FieldSpec("DIS-TRAN-CAT-CD", 4, "numeric"),
    FieldSpec("DIS-INT-RATE", 6, "signed_decimal", scale=2),
    FieldSpec("FILLER", 28, "filler"),
]

TRAN_CAT_BAL_LAYOUT: list[FieldSpec] = [
    FieldSpec("TRANCAT-ACCT-ID", 11, "numeric"),
    FieldSpec("TRANCAT-TYPE-CD", 2, "alpha"),
    FieldSpec("TRANCAT-CD", 4, "numeric"),
    FieldSpec("TRAN-CAT-BAL", 11, "signed_decimal", scale=2),
    FieldSpec("FILLER", 22, "filler"),
]

TRAN_CATEGORY_LAYOUT: list[FieldSpec] = [
    FieldSpec("TRAN-TYPE-CD", 2, "alpha"),
    FieldSpec("TRAN-CAT-CD", 4, "numeric"),
    FieldSpec("TRAN-CAT-TYPE-DESC", 50, "alpha"),
    FieldSpec("FILLER", 4, "filler"),
]

TRAN_TYPE_LAYOUT: list[FieldSpec] = [
    FieldSpec("TRAN-TYPE", 2, "alpha"),
    FieldSpec("TRAN-TYPE-DESC", 50, "alpha"),
    FieldSpec("FILLER", 8, "filler"),
]

# ── Layout registry (file name → layout + copybook name) ────────────

LAYOUT_REGISTRY: dict[str, tuple[list[FieldSpec], str, int]] = {
    "acctdata.txt":  (ACCOUNT_LAYOUT, "CVACT01Y", 300),
    "carddata.txt":  (CARD_LAYOUT, "CVACT02Y", 150),
    "cardxref.txt":  (CARD_XREF_LAYOUT, "CVACT03Y", 50),
    "custdata.txt":  (CUSTOMER_LAYOUT, "CVCUS01Y", 500),
    "dailytran.txt": (DAILY_TRAN_LAYOUT, "CVTRA06Y", 350),
    "discgrp.txt":   (DISC_GROUP_LAYOUT, "CVTRA02Y", 50),
    "tcatbal.txt":   (TRAN_CAT_BAL_LAYOUT, "CVTRA01Y", 50),
    "trancatg.txt":  (TRAN_CATEGORY_LAYOUT, "CVTRA04Y", 60),
    "trantype.txt":  (TRAN_TYPE_LAYOUT, "CVTRA03Y", 60),
}


def decode_signed_decimal(raw: str, scale: int) -> str:
    """Decode a COBOL DISPLAY-format signed decimal with trailing overpunch.

    Returns a string decimal representation (e.g., "194.00", "-194.00").
    """
    if not raw or raw.isspace():
        return "0." + "0" * scale

    last_char = raw[-1]
    digits_before = raw[:-1]

    sign = 1
    last_digit = 0

    if last_char in POSITIVE_OVERPUNCH:
        last_digit = POSITIVE_OVERPUNCH[last_char]
        sign = 1
    elif last_char in NEGATIVE_OVERPUNCH:
        last_digit = NEGATIVE_OVERPUNCH[last_char]
        sign = -1
    elif last_char.isdigit():
        last_digit = int(last_char)
        sign = 1
    else:
        last_digit = 0
        sign = 1

    full_digits = digits_before + str(last_digit)

    try:
        int_value = int(full_digits)
    except ValueError:
        return "0." + "0" * scale

    d = Decimal(int_value)
    if scale > 0:
        d = d / (10 ** scale)
    if sign < 0:
        d = -d

    if scale > 0:
        fmt = f"{{:.{scale}f}}"
        return fmt.format(d)
    return str(d)


def parse_record(line: str, layout: list[FieldSpec], record_length: int) -> dict[str, Any]:
    """Parse a single fixed-width record into a dict of field values."""
    if len(line) < record_length:
        line = line.ljust(record_length)

    pos = 0
    fields: dict[str, Any] = {}

    for spec in layout:
        raw = line[pos:pos + spec.length]
        pos += spec.length

        if spec.field_type == "filler":
            continue

        if spec.field_type == "numeric":
            stripped = raw.strip()
            if stripped and stripped.isdigit():
                fields[spec.name] = int(stripped)
            else:
                fields[spec.name] = 0

        elif spec.field_type == "signed_decimal":
            fields[spec.name] = decode_signed_decimal(raw, spec.scale)

        elif spec.field_type == "alpha":
            fields[spec.name] = raw.strip()

    return fields


def parse_file(file_path: Path) -> dict[str, Any]:
    """Parse an entire COBOL data file and return structured JSON-ready dict."""
    filename = file_path.name
    if filename not in LAYOUT_REGISTRY:
        raise ValueError(f"No layout defined for {filename}. "
                         f"Known files: {', '.join(LAYOUT_REGISTRY.keys())}")

    layout, copybook, record_length = LAYOUT_REGISTRY[filename]

    records: list[dict[str, Any]] = []
    with open(file_path, "r", encoding="ascii", errors="replace") as f:
        for i, line in enumerate(f, start=1):
            line = line.rstrip("\n").rstrip("\r")
            if not line:
                continue
            parsed = parse_record(line, layout, record_length)
            parsed["_record_number"] = i
            records.append(parsed)

    return {
        "metadata": {
            "source_file": filename,
            "copybook": copybook,
            "record_length": record_length,
            "record_count": len(records),
            "generated_at": datetime.now(timezone.utc).isoformat(),
        },
        "records": records,
    }


def main() -> None:
    """CLI entry point: parse a data file and write JSON to stdout or file."""
    if len(sys.argv) < 2:
        print("Usage: python cobol_parser.py <data_file> [output_json]")
        print(f"Supported files: {', '.join(LAYOUT_REGISTRY.keys())}")
        sys.exit(1)

    input_path = Path(sys.argv[1])
    result = parse_file(input_path)

    output = json.dumps(result, indent=2, default=str)

    if len(sys.argv) >= 3:
        out_path = Path(sys.argv[2])
        out_path.write_text(output, encoding="utf-8")
        print(f"Written {result['metadata']['record_count']} records to {out_path}")
    else:
        print(output)


if __name__ == "__main__":
    main()
