"""
Copybook field layout definitions for all CardDemo data files.

Each layout is a list of (field_name, pic_clause, length) tuples.
The pic_clause encodes the COBOL PIC type:
  - '9(n)'       : unsigned numeric (display)
  - 'S9(n)V99'   : signed numeric with 2 implied decimals (zoned decimal with overpunch)
  - 'S9(n)V99:p' : signed numeric with p implied decimals
  - 'X(n)'       : alphanumeric
  - 'FILLER'     : padding (excluded from parsed output)
"""

# CVACT01Y — Account Record (RECLN 300)
ACCOUNT_RECORD = [
    ("ACCT-ID",                "9(11)",      11),
    ("ACCT-ACTIVE-STATUS",     "X(1)",        1),
    ("ACCT-CURR-BAL",          "S9(10)V99",  12),
    ("ACCT-CREDIT-LIMIT",      "S9(10)V99",  12),
    ("ACCT-CASH-CREDIT-LIMIT", "S9(10)V99",  12),
    ("ACCT-OPEN-DATE",         "X(10)",      10),
    ("ACCT-EXPIRAION-DATE",    "X(10)",      10),
    ("ACCT-REISSUE-DATE",      "X(10)",      10),
    ("ACCT-CURR-CYC-CREDIT",   "S9(10)V99",  12),
    ("ACCT-CURR-CYC-DEBIT",    "S9(10)V99",  12),
    ("ACCT-ADDR-ZIP",          "X(10)",      10),
    ("ACCT-GROUP-ID",          "X(10)",      10),
    ("FILLER",                 "FILLER",    178),
]

# CVACT02Y — Card Record (RECLN 150)
CARD_RECORD = [
    ("CARD-NUM",              "X(16)",  16),
    ("CARD-ACCT-ID",          "9(11)",  11),
    ("CARD-CVV-CD",           "9(3)",    3),
    ("CARD-EMBOSSED-NAME",    "X(50)",  50),
    ("CARD-EXPIRAION-DATE",   "X(10)",  10),
    ("CARD-ACTIVE-STATUS",    "X(1)",    1),
    ("FILLER",                "FILLER", 59),
]

# CVACT03Y — Card Cross-Reference (RECLN 50)
CARD_XREF_RECORD = [
    ("XREF-CARD-NUM", "X(16)",  16),
    ("XREF-CUST-ID",  "9(9)",    9),
    ("XREF-ACCT-ID",  "9(11)",  11),
    ("FILLER",         "FILLER", 14),
]

# CVCUS01Y — Customer Record (RECLN 500)
CUSTOMER_RECORD = [
    ("CUST-ID",                  "9(9)",    9),
    ("CUST-FIRST-NAME",          "X(25)",  25),
    ("CUST-MIDDLE-NAME",         "X(25)",  25),
    ("CUST-LAST-NAME",           "X(25)",  25),
    ("CUST-ADDR-LINE-1",         "X(50)",  50),
    ("CUST-ADDR-LINE-2",         "X(50)",  50),
    ("CUST-ADDR-LINE-3",         "X(50)",  50),
    ("CUST-ADDR-STATE-CD",       "X(2)",    2),
    ("CUST-ADDR-COUNTRY-CD",     "X(3)",    3),
    ("CUST-ADDR-ZIP",            "X(10)",  10),
    ("CUST-PHONE-NUM-1",         "X(15)",  15),
    ("CUST-PHONE-NUM-2",         "X(15)",  15),
    ("CUST-SSN",                 "9(9)",    9),
    ("CUST-GOVT-ISSUED-ID",      "X(20)",  20),
    ("CUST-DOB-YYYY-MM-DD",      "X(10)",  10),
    ("CUST-EFT-ACCOUNT-ID",      "X(10)",  10),
    ("CUST-PRI-CARD-HOLDER-IND", "X(1)",    1),
    ("CUST-FICO-CREDIT-SCORE",   "9(3)",    3),
    ("FILLER",                   "FILLER",168),
]

# CVTRA05Y — Transaction Record (RECLN 350)
TRANSACTION_RECORD = [
    ("TRAN-ID",            "X(16)",      16),
    ("TRAN-TYPE-CD",       "X(2)",        2),
    ("TRAN-CAT-CD",        "9(4)",        4),
    ("TRAN-SOURCE",        "X(10)",      10),
    ("TRAN-DESC",          "X(100)",    100),
    ("TRAN-AMT",           "S9(9)V99",   11),
    ("TRAN-MERCHANT-ID",   "9(9)",        9),
    ("TRAN-MERCHANT-NAME", "X(50)",      50),
    ("TRAN-MERCHANT-CITY", "X(50)",      50),
    ("TRAN-MERCHANT-ZIP",  "X(10)",      10),
    ("TRAN-CARD-NUM",      "X(16)",      16),
    ("TRAN-ORIG-TS",       "X(26)",      26),
    ("TRAN-PROC-TS",       "X(26)",      26),
    ("FILLER",             "FILLER",     20),
]

# CVTRA06Y — Daily Transaction Record (RECLN 350, same layout as CVTRA05Y)
DAILY_TRANSACTION_RECORD = [
    ("DALYTRAN-ID",            "X(16)",      16),
    ("DALYTRAN-TYPE-CD",       "X(2)",        2),
    ("DALYTRAN-CAT-CD",        "9(4)",        4),
    ("DALYTRAN-SOURCE",        "X(10)",      10),
    ("DALYTRAN-DESC",          "X(100)",    100),
    ("DALYTRAN-AMT",           "S9(9)V99",   11),
    ("DALYTRAN-MERCHANT-ID",   "9(9)",        9),
    ("DALYTRAN-MERCHANT-NAME", "X(50)",      50),
    ("DALYTRAN-MERCHANT-CITY", "X(50)",      50),
    ("DALYTRAN-MERCHANT-ZIP",  "X(10)",      10),
    ("DALYTRAN-CARD-NUM",      "X(16)",      16),
    ("DALYTRAN-ORIG-TS",       "X(26)",      26),
    ("DALYTRAN-PROC-TS",       "X(26)",      26),
    ("FILLER",                 "FILLER",     20),
]

# CVTRA01Y — Transaction Category Balance (RECLN 50)
TRAN_CAT_BAL_RECORD = [
    ("TRANCAT-ACCT-ID",  "9(11)",     11),
    ("TRANCAT-TYPE-CD",  "X(2)",       2),
    ("TRANCAT-CD",       "9(4)",       4),
    ("TRAN-CAT-BAL",     "S9(9)V99",  11),
    ("FILLER",           "FILLER",    22),
]

# CVTRA02Y — Disclosure Group (RECLN 50)
DISCLOSURE_GROUP_RECORD = [
    ("DIS-ACCT-GROUP-ID", "X(10)",     10),
    ("DIS-TRAN-TYPE-CD",  "X(2)",       2),
    ("DIS-TRAN-CAT-CD",   "9(4)",       4),
    ("DIS-INT-RATE",       "S9(4)V99",   6),
    ("FILLER",             "FILLER",    28),
]

# CVTRA03Y — Transaction Type (RECLN 60)
TRAN_TYPE_RECORD = [
    ("TRAN-TYPE",      "X(2)",   2),
    ("TRAN-TYPE-DESC", "X(50)", 50),
    ("FILLER",         "FILLER", 8),
]

# CVTRA04Y — Transaction Category (RECLN 60)
TRAN_CATEGORY_RECORD = [
    ("TRAN-TYPE-CD",       "X(2)",   2),
    ("TRAN-CAT-CD",        "9(4)",   4),
    ("TRAN-CAT-TYPE-DESC", "X(50)", 50),
    ("FILLER",             "FILLER", 4),
]

# Map file basenames to their layouts
FILE_LAYOUTS = {
    "acctdata":  ACCOUNT_RECORD,
    "carddata":  CARD_RECORD,
    "cardxref":  CARD_XREF_RECORD,
    "custdata":  CUSTOMER_RECORD,
    "dailytran": DAILY_TRANSACTION_RECORD,
    "discgrp":   DISCLOSURE_GROUP_RECORD,
    "tcatbal":   TRAN_CAT_BAL_RECORD,
    "trancatg":  TRAN_CATEGORY_RECORD,
    "trantype":  TRAN_TYPE_RECORD,
}
