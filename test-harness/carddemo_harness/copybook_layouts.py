"""Copybook-based record layout definitions for all CardDemo data files.

Each layout is a list of (field_name, pic_clause, byte_length, data_type, business_meaning)
tuples. The byte_length is the DISPLAY-format length (what appears in ASCII files).

Overpunch-encoded signed fields (PIC S9(n)V99) use trailing overpunch characters:
  Positive: { = 0, A-I = 1-9
  Negative: } = 0, J-R = 1-9
"""

from dataclasses import dataclass
from typing import Optional


@dataclass(frozen=True)
class FieldDef:
    """Definition of a single field in a COBOL record layout."""
    name: str
    pic_clause: str
    offset: int           # 0-based byte offset
    length: int           # byte length in DISPLAY format
    data_type: str        # 'numeric', 'signed_decimal', 'alphanumeric'
    scale: int            # decimal places (digits after V), 0 for integers
    business_meaning: str


@dataclass(frozen=True)
class RecordLayout:
    """Complete record layout for a COBOL data file."""
    copybook: str
    entity: str
    record_length: int
    key_field: str | tuple  # single field name or tuple of field names for composite keys
    fields: tuple  # tuple of FieldDef

    @property
    def key_fields(self) -> tuple:
        """Return key field(s) as a tuple, even if single."""
        if isinstance(self.key_field, tuple):
            return self.key_field
        return (self.key_field,)


# ---------------------------------------------------------------------------
# CVACT01Y — Account Record (RECLN 300)
# ---------------------------------------------------------------------------
ACCOUNT_LAYOUT = RecordLayout(
    copybook="CVACT01Y",
    entity="Account",
    record_length=300,
    key_field="ACCT-ID",
    fields=(
        FieldDef("ACCT-ID",                "PIC 9(11)",      0,  11, "numeric",         0, "Account identifier (primary key)"),
        FieldDef("ACCT-ACTIVE-STATUS",     "PIC X(01)",     11,   1, "alphanumeric",    0, "Account status: Y=active, N=inactive"),
        FieldDef("ACCT-CURR-BAL",          "PIC S9(10)V99", 12,  12, "signed_decimal",  2, "Current account balance"),
        FieldDef("ACCT-CREDIT-LIMIT",      "PIC S9(10)V99", 24,  12, "signed_decimal",  2, "Credit limit"),
        FieldDef("ACCT-CASH-CREDIT-LIMIT", "PIC S9(10)V99", 36,  12, "signed_decimal",  2, "Cash advance credit limit"),
        FieldDef("ACCT-OPEN-DATE",         "PIC X(10)",     48,  10, "alphanumeric",    0, "Account open date (YYYY-MM-DD)"),
        FieldDef("ACCT-EXPIRAION-DATE",    "PIC X(10)",     58,  10, "alphanumeric",    0, "Account expiration date (YYYY-MM-DD)"),
        FieldDef("ACCT-REISSUE-DATE",      "PIC X(10)",     68,  10, "alphanumeric",    0, "Card reissue date (YYYY-MM-DD)"),
        FieldDef("ACCT-CURR-CYC-CREDIT",   "PIC S9(10)V99", 78,  12, "signed_decimal",  2, "Current cycle credit total"),
        FieldDef("ACCT-CURR-CYC-DEBIT",    "PIC S9(10)V99", 90,  12, "signed_decimal",  2, "Current cycle debit total"),
        FieldDef("ACCT-ADDR-ZIP",          "PIC X(10)",    102,  10, "alphanumeric",    0, "Account holder ZIP code"),
        FieldDef("ACCT-GROUP-ID",          "PIC X(10)",    112,  10, "alphanumeric",    0, "Disclosure/interest rate group ID"),
        FieldDef("FILLER",                 "PIC X(178)",   122, 178, "alphanumeric",    0, "Reserved filler space"),
    ),
)

# ---------------------------------------------------------------------------
# CVACT02Y — Card Record (RECLN 150)
# ---------------------------------------------------------------------------
CARD_LAYOUT = RecordLayout(
    copybook="CVACT02Y",
    entity="Card",
    record_length=150,
    key_field="CARD-NUM",
    fields=(
        FieldDef("CARD-NUM",             "PIC X(16)",  0, 16, "alphanumeric",    0, "Card number (primary key)"),
        FieldDef("CARD-ACCT-ID",         "PIC 9(11)", 16, 11, "numeric",         0, "Associated account ID (FK to CVACT01Y)"),
        FieldDef("CARD-CVV-CD",          "PIC 9(03)", 27,  3, "numeric",         0, "Card verification value (CVV)"),
        FieldDef("CARD-EMBOSSED-NAME",   "PIC X(50)", 30, 50, "alphanumeric",    0, "Cardholder name as embossed on card"),
        FieldDef("CARD-EXPIRAION-DATE",  "PIC X(10)", 80, 10, "alphanumeric",    0, "Card expiration date (YYYY-MM-DD)"),
        FieldDef("CARD-ACTIVE-STATUS",   "PIC X(01)", 90,  1, "alphanumeric",    0, "Card status: Y=active, N=inactive"),
        FieldDef("FILLER",              "PIC X(59)", 91, 59, "alphanumeric",    0, "Reserved filler space"),
    ),
)

# ---------------------------------------------------------------------------
# CVACT03Y — Card-Account Cross-Reference (RECLN 36)
# ---------------------------------------------------------------------------
CARD_XREF_LAYOUT = RecordLayout(
    copybook="CVACT03Y",
    entity="Card-XREF",
    record_length=36,
    key_field="XREF-CARD-NUM",
    fields=(
        FieldDef("XREF-CARD-NUM",  "PIC X(16)",  0, 16, "alphanumeric",  0, "Card number (primary key, FK to CVACT02Y)"),
        FieldDef("XREF-CUST-ID",   "PIC 9(09)", 16,  9, "numeric",       0, "Customer ID (FK to CVCUS01Y)"),
        FieldDef("XREF-ACCT-ID",   "PIC 9(11)", 25, 11, "numeric",       0, "Account ID (FK to CVACT01Y)"),
        FieldDef("FILLER",         "PIC X(14)", 36, 14, "alphanumeric",  0, "Reserved filler space"),
    ),
)

# ---------------------------------------------------------------------------
# CVCUS01Y — Customer Record (RECLN 500)
# ---------------------------------------------------------------------------
CUSTOMER_LAYOUT = RecordLayout(
    copybook="CVCUS01Y",
    entity="Customer",
    record_length=500,
    key_field="CUST-ID",
    fields=(
        FieldDef("CUST-ID",                  "PIC 9(09)",   0,   9, "numeric",       0, "Customer identifier (primary key)"),
        FieldDef("CUST-FIRST-NAME",          "PIC X(25)",   9,  25, "alphanumeric",  0, "Customer first name"),
        FieldDef("CUST-MIDDLE-NAME",         "PIC X(25)",  34,  25, "alphanumeric",  0, "Customer middle name"),
        FieldDef("CUST-LAST-NAME",           "PIC X(25)",  59,  25, "alphanumeric",  0, "Customer last name"),
        FieldDef("CUST-ADDR-LINE-1",         "PIC X(50)",  84,  50, "alphanumeric",  0, "Address line 1"),
        FieldDef("CUST-ADDR-LINE-2",         "PIC X(50)", 134,  50, "alphanumeric",  0, "Address line 2"),
        FieldDef("CUST-ADDR-LINE-3",         "PIC X(50)", 184,  50, "alphanumeric",  0, "Address line 3"),
        FieldDef("CUST-ADDR-STATE-CD",       "PIC X(02)", 234,   2, "alphanumeric",  0, "US state code (2-letter)"),
        FieldDef("CUST-ADDR-COUNTRY-CD",     "PIC X(03)", 236,   3, "alphanumeric",  0, "Country code (3-letter)"),
        FieldDef("CUST-ADDR-ZIP",            "PIC X(10)", 239,  10, "alphanumeric",  0, "ZIP/postal code"),
        FieldDef("CUST-PHONE-NUM-1",         "PIC X(15)", 249,  15, "alphanumeric",  0, "Primary phone number"),
        FieldDef("CUST-PHONE-NUM-2",         "PIC X(15)", 264,  15, "alphanumeric",  0, "Secondary phone number"),
        FieldDef("CUST-SSN",                 "PIC 9(09)", 279,   9, "numeric",       0, "Social Security Number"),
        FieldDef("CUST-GOVT-ISSUED-ID",      "PIC X(20)", 288,  20, "alphanumeric",  0, "Government-issued ID number"),
        FieldDef("CUST-DOB-YYYY-MM-DD",      "PIC X(10)", 308,  10, "alphanumeric",  0, "Date of birth (YYYY-MM-DD)"),
        FieldDef("CUST-EFT-ACCOUNT-ID",      "PIC X(10)", 318,  10, "alphanumeric",  0, "EFT/bank account ID for payments"),
        FieldDef("CUST-PRI-CARD-HOLDER-IND", "PIC X(01)", 328,   1, "alphanumeric",  0, "Primary cardholder indicator: Y/N"),
        FieldDef("CUST-FICO-CREDIT-SCORE",   "PIC 9(03)", 329,   3, "numeric",       0, "FICO credit score (300-850)"),
        FieldDef("FILLER",                   "PIC X(168)",332, 168, "alphanumeric",  0, "Reserved filler space"),
    ),
)

# ---------------------------------------------------------------------------
# CVTRA05Y — Transaction Record (RECLN 350)
# ---------------------------------------------------------------------------
TRANSACTION_LAYOUT = RecordLayout(
    copybook="CVTRA05Y",
    entity="Transaction",
    record_length=350,
    key_field="TRAN-ID",
    fields=(
        FieldDef("TRAN-ID",            "PIC X(16)",      0, 16, "alphanumeric",    0, "Transaction identifier (primary key)"),
        FieldDef("TRAN-TYPE-CD",       "PIC X(02)",     16,  2, "alphanumeric",    0, "Transaction type code (FK to CVTRA03Y)"),
        FieldDef("TRAN-CAT-CD",        "PIC 9(04)",     18,  4, "numeric",         0, "Transaction category code (FK to CVTRA04Y)"),
        FieldDef("TRAN-SOURCE",        "PIC X(10)",     22, 10, "alphanumeric",    0, "Transaction source (POS TERM, OPERATOR, etc.)"),
        FieldDef("TRAN-DESC",          "PIC X(100)",    32,100, "alphanumeric",    0, "Transaction description"),
        FieldDef("TRAN-AMT",           "PIC S9(09)V99",132, 11, "signed_decimal",  2, "Transaction amount (signed)"),
        FieldDef("TRAN-MERCHANT-ID",   "PIC 9(09)",    143,  9, "numeric",         0, "Merchant identifier"),
        FieldDef("TRAN-MERCHANT-NAME", "PIC X(50)",    152, 50, "alphanumeric",    0, "Merchant name"),
        FieldDef("TRAN-MERCHANT-CITY", "PIC X(50)",    202, 50, "alphanumeric",    0, "Merchant city"),
        FieldDef("TRAN-MERCHANT-ZIP",  "PIC X(10)",    252, 10, "alphanumeric",    0, "Merchant ZIP code"),
        FieldDef("TRAN-CARD-NUM",      "PIC X(16)",    262, 16, "alphanumeric",    0, "Card number used for transaction"),
        FieldDef("TRAN-ORIG-TS",       "PIC X(26)",    278, 26, "alphanumeric",    0, "Transaction origination timestamp"),
        FieldDef("TRAN-PROC-TS",       "PIC X(26)",    304, 26, "alphanumeric",    0, "Transaction processing timestamp"),
        FieldDef("FILLER",             "PIC X(20)",    330, 20, "alphanumeric",    0, "Reserved filler space"),
    ),
)

# ---------------------------------------------------------------------------
# CVTRA06Y — Daily Transaction Record (RECLN 350, same layout as CVTRA05Y)
# ---------------------------------------------------------------------------
DAILY_TRAN_LAYOUT = RecordLayout(
    copybook="CVTRA06Y",
    entity="DailyTransaction",
    record_length=350,
    key_field="DALYTRAN-ID",
    fields=(
        FieldDef("DALYTRAN-ID",            "PIC X(16)",      0, 16, "alphanumeric",    0, "Daily transaction ID"),
        FieldDef("DALYTRAN-TYPE-CD",       "PIC X(02)",     16,  2, "alphanumeric",    0, "Transaction type code"),
        FieldDef("DALYTRAN-CAT-CD",        "PIC 9(04)",     18,  4, "numeric",         0, "Transaction category code"),
        FieldDef("DALYTRAN-SOURCE",        "PIC X(10)",     22, 10, "alphanumeric",    0, "Transaction source"),
        FieldDef("DALYTRAN-DESC",          "PIC X(100)",    32,100, "alphanumeric",    0, "Transaction description"),
        FieldDef("DALYTRAN-AMT",           "PIC S9(09)V99",132, 11, "signed_decimal",  2, "Transaction amount (signed)"),
        FieldDef("DALYTRAN-MERCHANT-ID",   "PIC 9(09)",    143,  9, "numeric",         0, "Merchant identifier"),
        FieldDef("DALYTRAN-MERCHANT-NAME", "PIC X(50)",    152, 50, "alphanumeric",    0, "Merchant name"),
        FieldDef("DALYTRAN-MERCHANT-CITY", "PIC X(50)",    202, 50, "alphanumeric",    0, "Merchant city"),
        FieldDef("DALYTRAN-MERCHANT-ZIP",  "PIC X(10)",    252, 10, "alphanumeric",    0, "Merchant ZIP code"),
        FieldDef("DALYTRAN-CARD-NUM",      "PIC X(16)",    262, 16, "alphanumeric",    0, "Card number"),
        FieldDef("DALYTRAN-ORIG-TS",       "PIC X(26)",    278, 26, "alphanumeric",    0, "Origination timestamp"),
        FieldDef("DALYTRAN-PROC-TS",       "PIC X(26)",    304, 26, "alphanumeric",    0, "Processing timestamp"),
        FieldDef("FILLER",                 "PIC X(20)",    330, 20, "alphanumeric",    0, "Reserved filler space"),
    ),
)

# ---------------------------------------------------------------------------
# CVTRA01Y — Transaction Category Balance (RECLN 51)
# ---------------------------------------------------------------------------
TRAN_CAT_BAL_LAYOUT = RecordLayout(
    copybook="CVTRA01Y",
    entity="TransactionCategoryBalance",
    record_length=51,
    key_field="TRANCAT-ACCT-ID",
    fields=(
        FieldDef("TRANCAT-ACCT-ID",  "PIC 9(11)",      0, 11, "numeric",        0, "Account ID (part of composite key)"),
        FieldDef("TRANCAT-TYPE-CD",  "PIC X(02)",     11,  2, "alphanumeric",   0, "Transaction type code (part of composite key)"),
        FieldDef("TRANCAT-CD",       "PIC 9(04)",     13,  4, "numeric",        0, "Category code (part of composite key)"),
        FieldDef("TRAN-CAT-BAL",     "PIC S9(09)V99", 17, 11, "signed_decimal", 2, "Category balance amount"),
        FieldDef("FILLER",           "PIC X(22)",     28, 22, "alphanumeric",   0, "Reserved filler space"),
    ),
)

# ---------------------------------------------------------------------------
# CVTRA02Y — Disclosure Group (RECLN 50)
# ---------------------------------------------------------------------------
DISC_GROUP_LAYOUT = RecordLayout(
    copybook="CVTRA02Y",
    entity="DisclosureGroup",
    record_length=50,
    key_field=("DIS-ACCT-GROUP-ID", "DIS-TRAN-TYPE-CD", "DIS-TRAN-CAT-CD"),
    fields=(
        FieldDef("DIS-ACCT-GROUP-ID", "PIC X(10)",      0, 10, "alphanumeric",   0, "Account group ID (part of composite key)"),
        FieldDef("DIS-TRAN-TYPE-CD",  "PIC X(02)",     10,  2, "alphanumeric",   0, "Transaction type code (part of composite key)"),
        FieldDef("DIS-TRAN-CAT-CD",   "PIC 9(04)",     12,  4, "numeric",        0, "Transaction category code (part of composite key)"),
        FieldDef("DIS-INT-RATE",      "PIC S9(04)V99", 16,  6, "signed_decimal", 2, "Interest rate (annual percentage)"),
        FieldDef("FILLER",            "PIC X(28)",     22, 28, "alphanumeric",   0, "Reserved filler space"),
    ),
)

# ---------------------------------------------------------------------------
# CVTRA03Y — Transaction Type (RECLN 60)
# ---------------------------------------------------------------------------
TRAN_TYPE_LAYOUT = RecordLayout(
    copybook="CVTRA03Y",
    entity="TransactionType",
    record_length=60,
    key_field="TRAN-TYPE",
    fields=(
        FieldDef("TRAN-TYPE",      "PIC X(02)",   0,  2, "alphanumeric", 0, "Transaction type code (primary key)"),
        FieldDef("TRAN-TYPE-DESC", "PIC X(50)",   2, 50, "alphanumeric", 0, "Transaction type description"),
        FieldDef("FILLER",         "PIC X(08)",  52,  8, "alphanumeric", 0, "Reserved filler space"),
    ),
)

# ---------------------------------------------------------------------------
# CVTRA04Y — Transaction Category (RECLN 60)
# ---------------------------------------------------------------------------
TRAN_CATEGORY_LAYOUT = RecordLayout(
    copybook="CVTRA04Y",
    entity="TransactionCategory",
    record_length=60,
    key_field=("TRAN-TYPE-CD", "TRAN-CAT-CD"),
    fields=(
        FieldDef("TRAN-TYPE-CD",       "PIC X(02)",   0,  2, "alphanumeric", 0, "Transaction type code (part of composite key)"),
        FieldDef("TRAN-CAT-CD",        "PIC 9(04)",   2,  4, "numeric",      0, "Category code (part of composite key)"),
        FieldDef("TRAN-CAT-TYPE-DESC", "PIC X(50)",   6, 50, "alphanumeric", 0, "Category description"),
        FieldDef("FILLER",             "PIC X(04)",  56,  4, "alphanumeric", 0, "Reserved filler space"),
    ),
)

# ---------------------------------------------------------------------------
# Map file names to layouts
# ---------------------------------------------------------------------------
FILE_LAYOUTS: dict[str, RecordLayout] = {
    "acctdata.txt":  ACCOUNT_LAYOUT,
    "carddata.txt":  CARD_LAYOUT,
    "cardxref.txt":  CARD_XREF_LAYOUT,
    "custdata.txt":  CUSTOMER_LAYOUT,
    "dailytran.txt": DAILY_TRAN_LAYOUT,
    "discgrp.txt":   DISC_GROUP_LAYOUT,
    "tcatbal.txt":   TRAN_CAT_BAL_LAYOUT,
    "trancatg.txt":  TRAN_CATEGORY_LAYOUT,
    "trantype.txt":  TRAN_TYPE_LAYOUT,
}
