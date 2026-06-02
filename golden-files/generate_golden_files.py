#!/usr/bin/env python3
"""
Generate golden-file JSON representations of all ASCII data files
in app/data/ASCII/ using the field layouts defined in the copybooks.

Each output JSON contains:
- metadata: file name, record length, record count, copybook source
- fields: array of field definitions
- records: array of parsed record objects
"""

import json
import os
import sys
from decimal import Decimal, ROUND_HALF_UP

# Overpunch decode tables
POSITIVE_OVERPUNCH = {'{': 0, 'A': 1, 'B': 2, 'C': 3, 'D': 4,
                      'E': 5, 'F': 6, 'G': 7, 'H': 8, 'I': 9}
NEGATIVE_OVERPUNCH = {'}': 0, 'J': 1, 'K': 2, 'L': 3, 'M': 4,
                      'N': 5, 'O': 6, 'P': 7, 'Q': 8, 'R': 9}


def decode_overpunch(raw: str, scale: int) -> str:
    """Decode a COBOL trailing-overpunch signed numeric field to a decimal string."""
    if not raw or raw.strip() == '':
        return "0"
    last_char = raw[-1]
    digits_before = raw[:-1]
    
    if last_char in POSITIVE_OVERPUNCH:
        last_digit = POSITIVE_OVERPUNCH[last_char]
        sign = ''
    elif last_char in NEGATIVE_OVERPUNCH:
        last_digit = NEGATIVE_OVERPUNCH[last_char]
        sign = '-'
    elif last_char.isdigit():
        # No overpunch, plain unsigned
        all_digits = raw
        d = Decimal(all_digits)
        if scale > 0:
            d = d * Decimal(10) ** (-scale)
        return str(d)
    else:
        return raw.strip()
    
    all_digits = digits_before + str(last_digit)
    d = Decimal(all_digits)
    if scale > 0:
        d = d * Decimal(10) ** (-scale)
    if sign == '-' and d != 0:
        d = -d
    return str(d)


class FieldDef:
    """Definition of a single field in a COBOL record."""
    def __init__(self, name, offset, length, pic, field_type, scale=0, business_meaning=""):
        self.name = name
        self.offset = offset
        self.length = length
        self.pic = pic
        self.field_type = field_type  # 'alphanumeric', 'numeric_display', 'signed_decimal', 'filler'
        self.scale = scale
        self.business_meaning = business_meaning
    
    def to_dict(self):
        d = {
            "name": self.name,
            "offset": self.offset,
            "length": self.length,
            "pic": self.pic,
            "type": self.field_type,
            "business_meaning": self.business_meaning
        }
        if self.scale > 0:
            d["decimal_scale"] = self.scale
        return d


def parse_record(line, fields):
    """Parse a fixed-width line into a dict using field definitions."""
    record = {}
    for f in fields:
        if f.field_type == 'filler':
            continue
        raw = line[f.offset:f.offset + f.length]
        
        if f.field_type == 'signed_decimal':
            record[f.name] = decode_overpunch(raw, f.scale)
        elif f.field_type == 'numeric_display':
            record[f.name] = raw.strip()
        elif f.field_type == 'alphanumeric':
            record[f.name] = raw.rstrip()
        else:
            record[f.name] = raw.rstrip()
    return record


# ===== Layout Definitions =====

ACCTDATA_FIELDS = [
    FieldDef("ACCT-ID", 0, 11, "9(11)", "numeric_display", 0,
             "Account identifier - primary key"),
    FieldDef("ACCT-ACTIVE-STATUS", 11, 1, "X(01)", "alphanumeric", 0,
             "Account active status: Y=active, N=inactive"),
    FieldDef("ACCT-CURR-BAL", 12, 12, "S9(10)V99", "signed_decimal", 2,
             "Current account balance in dollars"),
    FieldDef("ACCT-CREDIT-LIMIT", 24, 12, "S9(10)V99", "signed_decimal", 2,
             "Maximum credit limit"),
    FieldDef("ACCT-CASH-CREDIT-LIMIT", 36, 12, "S9(10)V99", "signed_decimal", 2,
             "Cash advance credit limit"),
    FieldDef("ACCT-OPEN-DATE", 48, 10, "X(10)", "alphanumeric", 0,
             "Account opening date (YYYY-MM-DD)"),
    FieldDef("ACCT-EXPIRAION-DATE", 58, 10, "X(10)", "alphanumeric", 0,
             "Account expiration date (YYYY-MM-DD)"),
    FieldDef("ACCT-REISSUE-DATE", 68, 10, "X(10)", "alphanumeric", 0,
             "Last card reissue date (YYYY-MM-DD)"),
    FieldDef("ACCT-CURR-CYC-CREDIT", 78, 12, "S9(10)V99", "signed_decimal", 2,
             "Current billing cycle total credits"),
    FieldDef("ACCT-CURR-CYC-DEBIT", 90, 12, "S9(10)V99", "signed_decimal", 2,
             "Current billing cycle total debits"),
    FieldDef("ACCT-ADDR-ZIP", 102, 10, "X(10)", "alphanumeric", 0,
             "Account holder ZIP code"),
    FieldDef("ACCT-GROUP-ID", 112, 10, "X(10)", "alphanumeric", 0,
             "Disclosure/interest rate group identifier"),
    FieldDef("FILLER", 122, 178, "X(178)", "filler", 0, "Padding"),
]

CARDDATA_FIELDS = [
    FieldDef("CARD-NUM", 0, 16, "X(16)", "alphanumeric", 0,
             "16-digit card number"),
    FieldDef("CARD-ACCT-ID", 16, 11, "9(11)", "numeric_display", 0,
             "Associated account ID"),
    FieldDef("CARD-CVV-CD", 27, 3, "9(03)", "numeric_display", 0,
             "3-digit CVV security code"),
    FieldDef("CARD-EMBOSSED-NAME", 30, 50, "X(50)", "alphanumeric", 0,
             "Name embossed on the card"),
    FieldDef("CARD-EXPIRAION-DATE", 80, 10, "X(10)", "alphanumeric", 0,
             "Card expiration date (YYYY-MM-DD)"),
    FieldDef("CARD-ACTIVE-STATUS", 90, 1, "X(01)", "alphanumeric", 0,
             "Card active status: Y=active, N=inactive"),
    FieldDef("FILLER", 91, 59, "X(59)", "filler", 0, "Padding"),
]

CARDXREF_FIELDS = [
    FieldDef("XREF-CARD-NUM", 0, 16, "X(16)", "alphanumeric", 0,
             "Card number - links card to account and customer"),
    FieldDef("XREF-CUST-ID", 16, 9, "9(09)", "numeric_display", 0,
             "Customer ID for this card"),
    FieldDef("XREF-ACCT-ID", 25, 11, "9(11)", "numeric_display", 0,
             "Account ID for this card"),
    FieldDef("FILLER", 36, 14, "X(14)", "filler", 0, "Padding"),
]

CUSTDATA_FIELDS = [
    FieldDef("CUST-ID", 0, 9, "9(09)", "numeric_display", 0,
             "Customer identifier - primary key"),
    FieldDef("CUST-FIRST-NAME", 9, 25, "X(25)", "alphanumeric", 0,
             "Customer first name"),
    FieldDef("CUST-MIDDLE-NAME", 34, 25, "X(25)", "alphanumeric", 0,
             "Customer middle name"),
    FieldDef("CUST-LAST-NAME", 59, 25, "X(25)", "alphanumeric", 0,
             "Customer last name"),
    FieldDef("CUST-ADDR-LINE-1", 84, 50, "X(50)", "alphanumeric", 0,
             "Address line 1"),
    FieldDef("CUST-ADDR-LINE-2", 134, 50, "X(50)", "alphanumeric", 0,
             "Address line 2"),
    FieldDef("CUST-ADDR-LINE-3", 184, 50, "X(50)", "alphanumeric", 0,
             "City/locality name"),
    FieldDef("CUST-ADDR-STATE-CD", 234, 2, "X(02)", "alphanumeric", 0,
             "US state code (2-letter)"),
    FieldDef("CUST-ADDR-COUNTRY-CD", 236, 3, "X(03)", "alphanumeric", 0,
             "Country code (3-letter)"),
    FieldDef("CUST-ADDR-ZIP", 239, 10, "X(10)", "alphanumeric", 0,
             "ZIP/postal code"),
    FieldDef("CUST-PHONE-NUM-1", 249, 15, "X(15)", "alphanumeric", 0,
             "Primary phone number"),
    FieldDef("CUST-PHONE-NUM-2", 264, 15, "X(15)", "alphanumeric", 0,
             "Secondary phone number"),
    FieldDef("CUST-SSN", 279, 9, "9(09)", "numeric_display", 0,
             "Social Security Number"),
    FieldDef("CUST-GOVT-ISSUED-ID", 288, 20, "X(20)", "alphanumeric", 0,
             "Government-issued ID number"),
    FieldDef("CUST-DOB-YYYY-MM-DD", 308, 10, "X(10)", "alphanumeric", 0,
             "Date of birth (YYYY-MM-DD)"),
    FieldDef("CUST-EFT-ACCOUNT-ID", 318, 10, "X(10)", "alphanumeric", 0,
             "Electronic funds transfer account ID"),
    FieldDef("CUST-PRI-CARD-HOLDER-IND", 328, 1, "X(01)", "alphanumeric", 0,
             "Primary card holder indicator: Y/N"),
    FieldDef("CUST-FICO-CREDIT-SCORE", 329, 3, "9(03)", "numeric_display", 0,
             "FICO credit score (300-850)"),
    FieldDef("FILLER", 332, 168, "X(168)", "filler", 0, "Padding"),
]

DAILYTRAN_FIELDS = [
    FieldDef("TRAN-ID", 0, 16, "X(16)", "alphanumeric", 0,
             "Unique transaction identifier"),
    FieldDef("TRAN-TYPE-CD", 16, 2, "X(02)", "alphanumeric", 0,
             "Transaction type code (01=Purchase, 02=Payment, etc.)"),
    FieldDef("TRAN-CAT-CD", 18, 4, "9(04)", "numeric_display", 0,
             "Transaction category code"),
    FieldDef("TRAN-SOURCE", 22, 10, "X(10)", "alphanumeric", 0,
             "Transaction source (POS TERM, OPERATOR, etc.)"),
    FieldDef("TRAN-DESC", 32, 100, "X(100)", "alphanumeric", 0,
             "Transaction description"),
    FieldDef("TRAN-AMT", 132, 11, "S9(09)V99", "signed_decimal", 2,
             "Transaction amount in dollars"),
    FieldDef("TRAN-MERCHANT-ID", 143, 9, "9(09)", "numeric_display", 0,
             "Merchant identifier"),
    FieldDef("TRAN-MERCHANT-NAME", 152, 50, "X(50)", "alphanumeric", 0,
             "Merchant name"),
    FieldDef("TRAN-MERCHANT-CITY", 202, 50, "X(50)", "alphanumeric", 0,
             "Merchant city"),
    FieldDef("TRAN-MERCHANT-ZIP", 252, 10, "X(10)", "alphanumeric", 0,
             "Merchant ZIP code"),
    FieldDef("TRAN-CARD-NUM", 262, 16, "X(16)", "alphanumeric", 0,
             "Card number used for this transaction"),
    FieldDef("TRAN-ORIG-TS", 278, 26, "X(26)", "alphanumeric", 0,
             "Transaction origination timestamp"),
    FieldDef("TRAN-PROC-TS", 304, 26, "X(26)", "alphanumeric", 0,
             "Transaction processing timestamp"),
    FieldDef("FILLER", 330, 20, "X(20)", "filler", 0, "Padding"),
]

DISCGRP_FIELDS = [
    FieldDef("DIS-ACCT-GROUP-ID", 0, 10, "X(10)", "alphanumeric", 0,
             "Account group ID for disclosure rates"),
    FieldDef("DIS-TRAN-TYPE-CD", 10, 2, "X(02)", "alphanumeric", 0,
             "Transaction type code"),
    FieldDef("DIS-TRAN-CAT-CD", 12, 4, "9(04)", "numeric_display", 0,
             "Transaction category code"),
    FieldDef("DIS-INT-RATE", 16, 6, "S9(04)V99", "signed_decimal", 2,
             "Annual interest rate for this group/type/category"),
    FieldDef("FILLER", 22, 28, "X(28)", "filler", 0, "Padding"),
]

TCATBAL_FIELDS = [
    FieldDef("TRANCAT-ACCT-ID", 0, 11, "9(11)", "numeric_display", 0,
             "Account ID for category balance"),
    FieldDef("TRANCAT-TYPE-CD", 11, 2, "X(02)", "alphanumeric", 0,
             "Transaction type code"),
    FieldDef("TRANCAT-CD", 13, 4, "9(04)", "numeric_display", 0,
             "Transaction category code"),
    FieldDef("TRAN-CAT-BAL", 17, 11, "S9(09)V99", "signed_decimal", 2,
             "Running balance for this account/type/category combination"),
    FieldDef("FILLER", 28, 22, "X(22)", "filler", 0, "Padding"),
]

TRANCATG_FIELDS = [
    FieldDef("TRAN-TYPE-CD", 0, 2, "X(02)", "alphanumeric", 0,
             "Transaction type code"),
    FieldDef("TRAN-CAT-CD", 2, 4, "9(04)", "numeric_display", 0,
             "Transaction category code"),
    FieldDef("TRAN-CAT-TYPE-DESC", 6, 50, "X(50)", "alphanumeric", 0,
             "Description of this transaction category"),
    FieldDef("FILLER", 56, 4, "X(04)", "filler", 0, "Padding"),
]

TRANTYPE_FIELDS = [
    FieldDef("TRAN-TYPE", 0, 2, "X(02)", "alphanumeric", 0,
             "Transaction type code (01=Purchase, 02=Payment, 03=Return, etc.)"),
    FieldDef("TRAN-TYPE-DESC", 2, 50, "X(50)", "alphanumeric", 0,
             "Human-readable transaction type description"),
    FieldDef("FILLER", 52, 8, "X(08)", "filler", 0, "Padding"),
]

# Map file names to their layouts and copybook sources
FILE_LAYOUTS = {
    "acctdata.txt": {
        "fields": ACCTDATA_FIELDS,
        "record_length": 300,
        "copybook": "CVACT01Y.cpy",
        "entity": "Account",
        "description": "Account master file - contains all credit card account records"
    },
    "carddata.txt": {
        "fields": CARDDATA_FIELDS,
        "record_length": 150,
        "copybook": "CVACT02Y.cpy",
        "entity": "Card",
        "description": "Card master file - contains all credit/debit card records"
    },
    "cardxref.txt": {
        "fields": CARDXREF_FIELDS,
        "record_length": 36,
        "copybook": "CVACT03Y.cpy",
        "entity": "Card Cross-Reference",
        "description": "Cross-reference linking cards to customers and accounts"
    },
    "custdata.txt": {
        "fields": CUSTDATA_FIELDS,
        "record_length": 500,
        "copybook": "CVCUS01Y.cpy",
        "entity": "Customer",
        "description": "Customer master file - contains personal and contact information"
    },
    "dailytran.txt": {
        "fields": DAILYTRAN_FIELDS,
        "record_length": 350,
        "copybook": "CVTRA05Y.cpy",
        "entity": "Transaction",
        "description": "Daily transaction file - input to the posting batch job"
    },
    "discgrp.txt": {
        "fields": DISCGRP_FIELDS,
        "record_length": 50,
        "copybook": "CVTRA02Y.cpy",
        "entity": "Disclosure Group",
        "description": "Interest rate disclosure groups by account group, type, and category"
    },
    "tcatbal.txt": {
        "fields": TCATBAL_FIELDS,
        "record_length": 50,
        "copybook": "CVTRA01Y.cpy",
        "entity": "Transaction Category Balance",
        "description": "Running balance per account/transaction-type/category combination"
    },
    "trancatg.txt": {
        "fields": TRANCATG_FIELDS,
        "record_length": 60,
        "copybook": "CVTRA04Y.cpy",
        "entity": "Transaction Category",
        "description": "Lookup table mapping transaction type+category to descriptions"
    },
    "trantype.txt": {
        "fields": TRANTYPE_FIELDS,
        "record_length": 60,
        "copybook": "CVTRA03Y.cpy",
        "entity": "Transaction Type",
        "description": "Lookup table mapping transaction type codes to descriptions"
    },
}


def generate_golden_file(data_dir, output_dir, filename, layout):
    """Parse a single ASCII data file and produce a golden JSON reference."""
    filepath = os.path.join(data_dir, filename)
    if not os.path.exists(filepath):
        print(f"  SKIP: {filename} not found")
        return
    
    fields = layout["fields"]
    records = []
    
    with open(filepath, 'r', encoding='ascii', errors='replace') as f:
        for line_num, line in enumerate(f, 1):
            line = line.rstrip('\n').rstrip('\r')
            # Pad to record length if shorter
            if len(line) < layout["record_length"]:
                line = line.ljust(layout["record_length"])
            record = parse_record(line, fields)
            record["_record_number"] = line_num
            records.append(record)
    
    # Compute summary statistics for numeric fields
    summaries = {}
    for field in fields:
        if field.field_type == 'signed_decimal':
            total = Decimal('0')
            min_val = None
            max_val = None
            for rec in records:
                try:
                    val = Decimal(rec[field.name])
                    total += val
                    if min_val is None or val < min_val:
                        min_val = val
                    if max_val is None or val > max_val:
                        max_val = val
                except Exception:
                    pass
            summaries[field.name] = {
                "sum": str(total),
                "min": str(min_val) if min_val is not None else None,
                "max": str(max_val) if max_val is not None else None
            }
    
    golden = {
        "metadata": {
            "source_file": filename,
            "copybook": layout["copybook"],
            "entity": layout["entity"],
            "description": layout["description"],
            "record_length": layout["record_length"],
            "record_count": len(records),
            "file_size_bytes": os.path.getsize(filepath)
        },
        "field_definitions": [f.to_dict() for f in fields if f.field_type != 'filler'],
        "numeric_summaries": summaries,
        "records": records
    }
    
    output_file = os.path.join(output_dir, filename.replace('.txt', '.json'))
    with open(output_file, 'w') as f:
        json.dump(golden, f, indent=2, default=str)
    
    print(f"  OK: {filename} -> {os.path.basename(output_file)} "
          f"({len(records)} records)")


def main():
    repo_root = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
    data_dir = os.path.join(repo_root, "app", "data", "ASCII")
    output_dir = os.path.dirname(os.path.abspath(__file__))
    
    if not os.path.exists(data_dir):
        print(f"ERROR: Data directory not found: {data_dir}")
        sys.exit(1)
    
    print("Generating golden-file JSON references...")
    print(f"  Source: {data_dir}")
    print(f"  Output: {output_dir}")
    print()
    
    for filename, layout in FILE_LAYOUTS.items():
        generate_golden_file(data_dir, output_dir, filename, layout)
    
    print()
    print("Done. Golden files generated successfully.")


if __name__ == "__main__":
    main()
