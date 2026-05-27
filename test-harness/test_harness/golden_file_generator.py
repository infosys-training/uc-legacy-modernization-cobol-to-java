"""
Golden file generator for CardDemo ASCII data files.

Parses each fixed-width data file using copybook layouts and produces
structured JSON with field documentation.
"""

import json
from decimal import Decimal
from pathlib import Path
from typing import Any

from test_harness.copybook_layouts import FILE_LAYOUTS
from test_harness.parser import parse_file


# Field documentation for each copybook
FIELD_DOCS = {
    "acctdata": {
        "ACCT-ID": "Account identifier (11-digit numeric)",
        "ACCT-ACTIVE-STATUS": "Account status: Y=Active, N=Inactive",
        "ACCT-CURR-BAL": "Current account balance (signed, 2 decimal places)",
        "ACCT-CREDIT-LIMIT": "Maximum credit limit",
        "ACCT-CASH-CREDIT-LIMIT": "Maximum cash advance limit",
        "ACCT-OPEN-DATE": "Account opening date (YYYY-MM-DD)",
        "ACCT-EXPIRAION-DATE": "Account expiration date (YYYY-MM-DD)",
        "ACCT-REISSUE-DATE": "Last card reissue date (YYYY-MM-DD)",
        "ACCT-CURR-CYC-CREDIT": "Current billing cycle credit total",
        "ACCT-CURR-CYC-DEBIT": "Current billing cycle debit total",
        "ACCT-ADDR-ZIP": "Account holder ZIP code",
        "ACCT-GROUP-ID": "Disclosure/rate group identifier",
    },
    "carddata": {
        "CARD-NUM": "Credit card number (16-character)",
        "CARD-ACCT-ID": "Associated account ID (11-digit numeric)",
        "CARD-CVV-CD": "Card verification value (3-digit)",
        "CARD-EMBOSSED-NAME": "Name embossed on card",
        "CARD-EXPIRAION-DATE": "Card expiration date (YYYY-MM-DD)",
        "CARD-ACTIVE-STATUS": "Card status: Y=Active, N=Inactive",
    },
    "cardxref": {
        "XREF-CARD-NUM": "Card number (foreign key to CARDDAT)",
        "XREF-CUST-ID": "Customer ID (foreign key to CUSTDAT)",
        "XREF-ACCT-ID": "Account ID (foreign key to ACCTDAT)",
    },
    "custdata": {
        "CUST-ID": "Customer identifier (9-digit numeric)",
        "CUST-FIRST-NAME": "Customer first name",
        "CUST-MIDDLE-NAME": "Customer middle name",
        "CUST-LAST-NAME": "Customer last name",
        "CUST-ADDR-LINE-1": "Address line 1",
        "CUST-ADDR-LINE-2": "Address line 2",
        "CUST-ADDR-LINE-3": "City/locality",
        "CUST-ADDR-STATE-CD": "US state code (2-char)",
        "CUST-ADDR-COUNTRY-CD": "Country code (3-char, e.g. USA)",
        "CUST-ADDR-ZIP": "ZIP/postal code",
        "CUST-PHONE-NUM-1": "Primary phone ((xxx)xxx-xxxx)",
        "CUST-PHONE-NUM-2": "Secondary phone",
        "CUST-SSN": "Social Security Number (9-digit)",
        "CUST-GOVT-ISSUED-ID": "Government-issued ID number",
        "CUST-DOB-YYYY-MM-DD": "Date of birth (YYYY-MM-DD)",
        "CUST-EFT-ACCOUNT-ID": "EFT/bank account ID",
        "CUST-PRI-CARD-HOLDER-IND": "Primary card holder indicator (Y/N)",
        "CUST-FICO-CREDIT-SCORE": "FICO credit score (3-digit)",
    },
    "dailytran": {
        "DALYTRAN-ID": "Transaction ID (16-character)",
        "DALYTRAN-TYPE-CD": "Transaction type code (2-char, FK to TRANTYPE)",
        "DALYTRAN-CAT-CD": "Transaction category code (4-digit, FK to TRANCATG)",
        "DALYTRAN-SOURCE": "Transaction source (e.g. POS TERM, OPERATOR)",
        "DALYTRAN-DESC": "Transaction description",
        "DALYTRAN-AMT": "Transaction amount (signed, 2 decimal places)",
        "DALYTRAN-MERCHANT-ID": "Merchant identifier (9-digit)",
        "DALYTRAN-MERCHANT-NAME": "Merchant name",
        "DALYTRAN-MERCHANT-CITY": "Merchant city",
        "DALYTRAN-MERCHANT-ZIP": "Merchant ZIP code",
        "DALYTRAN-CARD-NUM": "Card number (FK to CARDXREF)",
        "DALYTRAN-ORIG-TS": "Original transaction timestamp",
        "DALYTRAN-PROC-TS": "Processing timestamp",
    },
    "discgrp": {
        "DIS-ACCT-GROUP-ID": "Account group identifier (matches ACCT-GROUP-ID)",
        "DIS-TRAN-TYPE-CD": "Transaction type code for this rate",
        "DIS-TRAN-CAT-CD": "Transaction category code for this rate",
        "DIS-INT-RATE": "Annual interest rate (signed, 2 decimal places, e.g. 15.00 = 15%)",
    },
    "tcatbal": {
        "TRANCAT-ACCT-ID": "Account ID (FK to ACCTDAT)",
        "TRANCAT-TYPE-CD": "Transaction type code",
        "TRANCAT-CD": "Transaction category code",
        "TRAN-CAT-BAL": "Category balance (signed, 2 decimal places)",
    },
    "trancatg": {
        "TRAN-TYPE-CD": "Transaction type code (FK to TRANTYPE)",
        "TRAN-CAT-CD": "Transaction category code",
        "TRAN-CAT-TYPE-DESC": "Category description",
    },
    "trantype": {
        "TRAN-TYPE": "Transaction type code (2-char key, e.g. 01=Purchase, 02=Payment)",
        "TRAN-TYPE-DESC": "Transaction type description",
    },
}


class DecimalEncoder(json.JSONEncoder):
    """JSON encoder that handles Decimal values."""
    def default(self, obj: Any) -> Any:
        if isinstance(obj, Decimal):
            return str(obj)
        return super().default(obj)


def generate_golden_file(
    data_file: Path, output_dir: Path, layout_name: str | None = None
) -> Path:
    """Parse a single data file and write its JSON golden file."""
    if layout_name is None:
        layout_name = data_file.stem

    layout = FILE_LAYOUTS.get(layout_name)
    if layout is None:
        raise ValueError(f"No layout for '{layout_name}'")

    records = parse_file(data_file, layout)
    field_docs = FIELD_DOCS.get(layout_name, {})

    # Build copybook layout spec
    copybook_spec = []
    pos = 1
    for field_name, pic, length in layout:
        if pic != "FILLER":
            copybook_spec.append({
                "field": field_name,
                "pic": pic,
                "start_byte": pos,
                "length": length,
                "description": field_docs.get(field_name, ""),
            })
        pos += length

    output = {
        "file": data_file.name,
        "copybook_layout": layout_name,
        "record_count": len(records),
        "field_definitions": copybook_spec,
        "records": records,
    }

    output_path = output_dir / f"{layout_name}.json"
    with open(output_path, "w") as f:
        json.dump(output, f, indent=2, cls=DecimalEncoder)

    return output_path


def generate_all(data_dir: Path, output_dir: Path) -> list[Path]:
    """Generate golden files for all recognized data files in a directory."""
    output_dir.mkdir(parents=True, exist_ok=True)
    generated = []

    for data_file in sorted(data_dir.glob("*.txt")):
        layout_name = data_file.stem
        if layout_name in FILE_LAYOUTS:
            path = generate_golden_file(data_file, output_dir, layout_name)
            generated.append(path)
            print(f"Generated: {path.name} ({FILE_LAYOUTS[layout_name].__len__()} fields)")

    return generated
