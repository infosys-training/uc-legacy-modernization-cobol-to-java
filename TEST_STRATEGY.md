# Migration Test Strategy

## Overview

This document defines the testing approach for migrating the CardDemo COBOL application to Java. It covers golden-file tests, differential tests, batch reconciliation, and contract tests. The goal is to guarantee that the Java implementation produces **byte-for-byte identical output** to the COBOL version for all supported input scenarios.

---

## 1. Golden-File Tests

### Purpose
Capture known-good outputs from the COBOL system (or verified sample data) and use them as immutable reference baselines. Every Java implementation must produce output that matches these golden files exactly.

### Programs to Capture Outputs For

| Program | Input Files | Output Files | Record Length |
|---------|------------|--------------|--------------|
| CBACT01C | ACCTFILE (300-byte KSDS) | OUTFILE (107-byte FB), ARRYFILE (110-byte FB), VBRCFILE (84-byte VB) | Variable |
| CBACT02C | CARDFILE (150-byte KSDS) | OUTFILE (sequential dump) | 150 |
| CBACT03C | CARDXREF (50-byte KSDS) | OUTFILE (sequential dump) | 50 |
| CBACT04C | TCATBALF, XREFFILE, ACCTFILE, DISCGRP | TRANSACT (350-byte), ACCTFILE (updated balances) | 300/350 |
| CBCUS01C | CUSTFILE (500-byte KSDS) | OUTFILE (sequential dump) | 500 |
| CBTRN02C | DALYTRAN, TRANFILE, XREFFILE, ACCTFILE, TCATBALF | DALYREJS (430-byte GDG), updated ACCTFILE, updated TCATBALF | Variable |
| CBTRN03C | TRANFILE, CARDXREF, TRANTYPE, TRANCATG | TRANREPT (133-byte report) | 133 |
| CBSTM03A/B | TRNXFILE, XREFFILE, ACCTFILE, CUSTFILE | STMTFILE (80-byte), HTMLFILE (100-byte) | 80/100 |
| CBEXPORT | CUSTFILE, ACCTFILE, XREFFILE, TRANSACT, CARDFILE | EXPFILE (500-byte KSDS) | 500 |
| CBIMPORT | EXPFILE (500-byte) | CUSTOUT, ACCTOUT, XREFOUT, TRNXOUT, ERROUT | Variable |

### Input Selection Strategy

1. **Real sample data** (primary): Use the 9 ASCII data files in `app/data/ASCII/` as the primary test corpus. These contain 50 records each for most entities.
2. **Edge-case synthetic data**: Generate additional records targeting:
   - Zero-value monetary fields (triggers debit substitution in CBACT01C)
   - Negative balances (overpunch `}`, `J`-`R` characters)
   - Maximum-value fields (PIC S9(10)V99 = 9,999,999,999.99)
   - Empty/blank optional fields (FILLER regions)
   - Boundary dates (leap years, century boundaries)
3. **Minimal smoke data**: 1-2 records per entity for fast CI feedback loops.

### Comparison Approach

```
golden-files/
  acctdata.json          # Structured parse of acctdata.txt
  carddata.json          # Structured parse of carddata.txt
  cardxref.json          # Structured parse of cardxref.txt
  custdata.json          # Structured parse of custdata.txt
  dailytran.json         # Structured parse of dailytran.txt
  discgrp.json           # Structured parse of discgrp.txt
  tcatbal.json           # Structured parse of tcatbal.txt
  trancatg.json          # Structured parse of trancatg.txt
  trantype.json          # Structured parse of trantype.txt
```

**Comparison steps:**
1. Parse COBOL output file using the copybook-defined field layout.
2. Parse Java output file using the same layout.
3. Compare field-by-field, reporting mismatches with: field name, byte position, expected value, actual value.
4. For binary fields (COMP-3), compare raw byte arrays hex-encoded.
5. For numeric DISPLAY fields, compare both the raw string and the decoded BigDecimal value.

### Acceptance Criteria
- 100% field-by-field match for all records in all output files.
- Byte-level match for COMP-3 packed decimal fields (sign nibble must match: 0x0C/0x0D).
- Record count in output file must equal record count in input file (for 1:1 programs).
- Total file size must equal `record_count * record_length` for FB files.

---

## 2. Differential Tests

### Purpose
Run the COBOL (reference) and Java (candidate) implementations side-by-side with identical inputs and compare their outputs programmatically.

### Architecture

```
                    +------------------+
                    |  Test Input Data |
                    |  (ASCII files)   |
                    +--------+---------+
                             |
              +--------------+--------------+
              |                             |
     +--------v---------+       +-----------v----------+
     |  COBOL Runtime   |       |  Java Application    |
     |  (GnuCOBOL or    |       |  (java -jar ...)     |
     |   golden-file)   |       |                      |
     +--------+---------+       +-----------+----------+
              |                             |
     +--------v---------+       +-----------v----------+
     |  COBOL Output    |       |  Java Output         |
     |  (binary files)  |       |  (binary files)      |
     +--------+---------+       +-----------+----------+
              |                             |
              +--------------+--------------+
                             |
                    +--------v---------+
                    |  Field-by-Field  |
                    |  Comparator      |
                    +--------+---------+
                             |
                    +--------v---------+
                    |  Diff Report     |
                    |  (JSON/HTML)     |
                    +------------------+
```

### How to Run

Since the COBOL programs require a mainframe environment or GnuCOBOL compilation (and 3 of the 8 batch programs have known compilation issues), the differential test approach works in two modes:

1. **Golden-file mode** (default): Compare Java output against pre-captured golden files. This is the primary mode for CI.
2. **Live differential mode** (optional): If a GnuCOBOL environment is available, compile and run both versions, then compare. This mode is used for validation during migration.

### Side-by-Side Execution Script

```bash
#!/bin/bash
# differential_test.sh <program> <input_dir> <output_dir>
PROGRAM=$1
INPUT_DIR=$2
OUTPUT_DIR=$3

# Run Java version
java -jar test-harness/target/test-harness.jar \
  --program $PROGRAM \
  --input $INPUT_DIR \
  --output $OUTPUT_DIR/java/

# Compare against golden files
python3 test-harness/compare.py \
  --expected golden-files/$PROGRAM/ \
  --actual $OUTPUT_DIR/java/ \
  --layout copybook-layouts/$PROGRAM.json \
  --report $OUTPUT_DIR/diff-report.json
```

### Diff Report Format

```json
{
  "program": "CBACT01C",
  "status": "MISMATCH",
  "total_records": 50,
  "matching_records": 48,
  "mismatched_records": 2,
  "mismatches": [
    {
      "record_number": 23,
      "field": "ACCT-CURR-BAL",
      "byte_position": "12-23",
      "expected": "00000019400{",
      "actual": "00000019400}",
      "expected_decoded": "194.00",
      "actual_decoded": "-194.00",
      "pic_clause": "PIC S9(10)V99"
    }
  ]
}
```

---

## 3. Batch Reconciliation

### Purpose
After each batch run (whether COBOL or Java), verify that processing totals, record counts, and checksums are consistent.

### Reconciliation Checks by Batch Job

#### POSTTRAN (CBTRN02C) — Post Daily Transactions
| Check | Formula | Tolerance |
|-------|---------|-----------|
| Input record count | `wc -l dailytran.txt` | Exact |
| Output record count | Records written to TRANSACT + records written to DALYREJS = input count | Exact |
| Balance update check | For each account: `new_balance = old_balance + sum(credits) - sum(debits)` | 0.01 (rounding) |
| TCATBALF updates | For each (acct, type, cat): `new_cat_bal = old_cat_bal + sum(tran_amounts)` | Exact |
| Rejection count | Count of records in DALYREJS where XREF lookup failed | Logged |

#### INTCALC (CBACT04C) — Interest Calculation
| Check | Formula | Tolerance |
|-------|---------|-----------|
| Accounts processed | Count of records read from TCATBALF (unique accounts) | Exact |
| Interest transactions generated | One TRANSACT record per (account, type, cat) with non-zero balance | Exact |
| Interest formula | `interest = balance * (rate / 1200)` where rate from DISCGRP | 0.01 |
| Account balance update | `new_balance = old_balance + total_interest_for_account` | 0.01 |

#### CREASTMT (CBSTM03A/B) — Create Statements
| Check | Formula | Tolerance |
|-------|---------|-----------|
| Cards processed | Count of unique card numbers in XREFFILE | Exact |
| Statement count | One statement per card with transactions in date range | Exact |
| Statement total | Sum of transaction amounts per card matches statement total line | Exact |

#### TRANREPT (CBTRN03C) — Transaction Report
| Check | Formula | Tolerance |
|-------|---------|-----------|
| Transactions in report | Count matches filtered TRANSACT file (date range + sort) | Exact |
| Report total | Sum of TRAN-AMT in report = sum of TRAN-AMT in filtered input | 0.01 |
| Card count | Distinct card numbers in report matches filtered input | Exact |

#### CBEXPORT — Data Export
| Check | Formula | Tolerance |
|-------|---------|-----------|
| Total export records | `customers + accounts + xrefs + transactions + cards` | Exact |
| Per-type counts | EXPORT-REC-TYPE counts match source file record counts | Exact |
| Sequence continuity | EXPORT-SEQUENCE-NUM is monotonically increasing | Exact |

#### CBIMPORT — Data Import
| Check | Formula | Tolerance |
|-------|---------|-----------|
| Round-trip integrity | `export_count = import_count + error_count` | Exact |
| Per-type counts | CUSTOUT records = customer exports, ACCTOUT = account exports, etc. | Exact |
| Error file | All errors are documented with rejection reason | Logged |

### Checksum Approach

For each output file, compute:
1. **Record count**: Number of fixed-length records (file size / LRECL).
2. **Numeric field sums**: Sum of each monetary field across all records (using BigDecimal).
3. **Hash checksum**: SHA-256 of the entire output file for byte-level integrity.

```python
def reconcile(file_path, record_length, numeric_fields):
    """Compute reconciliation totals for a fixed-length file."""
    record_count = 0
    field_sums = {f.name: Decimal('0') for f in numeric_fields}
    
    with open(file_path, 'rb') as f:
        while True:
            record = f.read(record_length)
            if not record:
                break
            record_count += 1
            for field in numeric_fields:
                value = parse_field(record, field)
                field_sums[field.name] += value
    
    file_hash = hashlib.sha256(open(file_path, 'rb').read()).hexdigest()
    return ReconciliationResult(record_count, field_sums, file_hash)
```

---

## 4. Contract Tests

### Purpose
Codify the file formats, record layouts, and interface contracts so that any implementation (COBOL or Java) can be validated against the specification.

### File Format Contracts

#### ACCTFILE — Account Master (CVACT01Y.cpy)
```
Record Length: 300 bytes (fixed)
VSAM Type: KSDS
Key: ACCT-ID (bytes 0-10, PIC 9(11))

Field Layout:
  Offset  Length  Field                    PIC Clause       Type
  0       11      ACCT-ID                  9(11)            Numeric display
  11      1       ACCT-ACTIVE-STATUS       X(01)            Alphanumeric
  12      12      ACCT-CURR-BAL            S9(10)V99        Signed decimal (overpunch)
  24      12      ACCT-CREDIT-LIMIT        S9(10)V99        Signed decimal (overpunch)
  36      12      ACCT-CASH-CREDIT-LIMIT   S9(10)V99        Signed decimal (overpunch)
  48      10      ACCT-OPEN-DATE           X(10)            Date (YYYY-MM-DD)
  58      10      ACCT-EXPIRAION-DATE      X(10)            Date (YYYY-MM-DD)
  68      10      ACCT-REISSUE-DATE        X(10)            Date (YYYY-MM-DD)
  78      12      ACCT-CURR-CYC-CREDIT     S9(10)V99        Signed decimal (overpunch)
  90      12      ACCT-CURR-CYC-DEBIT      S9(10)V99        Signed decimal (overpunch)
  102     10      ACCT-ADDR-ZIP            X(10)            Alphanumeric
  112     10      ACCT-GROUP-ID            X(10)            Alphanumeric
  122     178     FILLER                   X(178)           Padding
```

#### CARDFILE — Card Master (CVACT02Y.cpy)
```
Record Length: 150 bytes (fixed)
VSAM Type: KSDS
Key: CARD-NUM (bytes 0-15, PIC X(16))

Field Layout:
  Offset  Length  Field                    PIC Clause       Type
  0       16      CARD-NUM                 X(16)            Alphanumeric
  16      11      CARD-ACCT-ID             9(11)            Numeric display
  27      3       CARD-CVV-CD              9(03)            Numeric display
  30      50      CARD-EMBOSSED-NAME       X(50)            Alphanumeric
  80      10      CARD-EXPIRAION-DATE      X(10)            Date (YYYY-MM-DD)
  90      1       CARD-ACTIVE-STATUS       X(01)            Alphanumeric
  91      59      FILLER                   X(59)            Padding
```

#### CARDXREF — Card Cross-Reference (CVACT03Y.cpy)
```
Record Length: 50 bytes (fixed)
VSAM Type: KSDS
Key: XREF-CARD-NUM (bytes 0-15, PIC X(16))

Field Layout:
  Offset  Length  Field                    PIC Clause       Type
  0       16      XREF-CARD-NUM            X(16)            Alphanumeric
  16      9       XREF-CUST-ID             9(09)            Numeric display
  25      11      XREF-ACCT-ID             9(11)            Numeric display
  36      14      FILLER                   X(14)            Padding
```

#### CUSTFILE — Customer Master (CVCUS01Y.cpy)
```
Record Length: 500 bytes (fixed)
VSAM Type: KSDS
Key: CUST-ID (bytes 0-8, PIC 9(09))

Field Layout:
  Offset  Length  Field                       PIC Clause    Type
  0       9       CUST-ID                     9(09)         Numeric display
  9       25      CUST-FIRST-NAME             X(25)         Alphanumeric
  34      25      CUST-MIDDLE-NAME            X(25)         Alphanumeric
  59      25      CUST-LAST-NAME              X(25)         Alphanumeric
  84      50      CUST-ADDR-LINE-1            X(50)         Alphanumeric
  134     50      CUST-ADDR-LINE-2            X(50)         Alphanumeric
  184     50      CUST-ADDR-LINE-3            X(50)         Alphanumeric
  234     2       CUST-ADDR-STATE-CD          X(02)         Alphanumeric
  236     3       CUST-ADDR-COUNTRY-CD        X(03)         Alphanumeric
  239     10      CUST-ADDR-ZIP               X(10)         Alphanumeric
  249     15      CUST-PHONE-NUM-1            X(15)         Alphanumeric
  264     15      CUST-PHONE-NUM-2            X(15)         Alphanumeric
  279     9       CUST-SSN                    9(09)         Numeric display
  288     20      CUST-GOVT-ISSUED-ID         X(20)         Alphanumeric
  308     10      CUST-DOB-YYYY-MM-DD         X(10)         Date
  318     10      CUST-EFT-ACCOUNT-ID         X(10)         Alphanumeric
  328     1       CUST-PRI-CARD-HOLDER-IND    X(01)         Alphanumeric
  329     3       CUST-FICO-CREDIT-SCORE      9(03)         Numeric display
  332     168     FILLER                      X(168)        Padding
```

#### TRANSACT — Transaction File (CVTRA05Y.cpy)
```
Record Length: 350 bytes (fixed)
VSAM Type: KSDS
Key: TRAN-ID (bytes 0-15, PIC X(16))

Field Layout:
  Offset  Length  Field                    PIC Clause       Type
  0       16      TRAN-ID                  X(16)            Alphanumeric
  16      2       TRAN-TYPE-CD             X(02)            Alphanumeric
  18      4       TRAN-CAT-CD              9(04)            Numeric display
  22      10      TRAN-SOURCE              X(10)            Alphanumeric
  32      100     TRAN-DESC                X(100)           Alphanumeric
  132     11      TRAN-AMT                 S9(09)V99        Signed decimal (overpunch)
  143     9       TRAN-MERCHANT-ID         9(09)            Numeric display
  152     50      TRAN-MERCHANT-NAME       X(50)            Alphanumeric
  202     50      TRAN-MERCHANT-CITY       X(50)            Alphanumeric
  252     10      TRAN-MERCHANT-ZIP        X(10)            Alphanumeric
  262     16      TRAN-CARD-NUM            X(16)            Alphanumeric
  278     26      TRAN-ORIG-TS             X(26)            Timestamp
  304     26      TRAN-PROC-TS             X(26)            Timestamp
  330     20      FILLER                   X(20)            Padding
```

#### TRANTYPE — Transaction Type (CVTRA03Y.cpy)
```
Record Length: 60 bytes (fixed)
VSAM Type: KSDS
Key: TRAN-TYPE (bytes 0-1, PIC X(02))

Field Layout:
  Offset  Length  Field                    PIC Clause       Type
  0       2       TRAN-TYPE                X(02)            Alphanumeric
  2       50      TRAN-TYPE-DESC           X(50)            Alphanumeric
  52      8       FILLER                   X(08)            Padding
```

#### TRANCATG — Transaction Category (CVTRA04Y.cpy)
```
Record Length: 60 bytes (fixed)
VSAM Type: KSDS
Key: TRAN-CAT-KEY (bytes 0-5, composite: TRAN-TYPE-CD + TRAN-CAT-CD)

Field Layout:
  Offset  Length  Field                    PIC Clause       Type
  0       2       TRAN-TYPE-CD             X(02)            Alphanumeric
  2       4       TRAN-CAT-CD              9(04)            Numeric display
  6       50      TRAN-CAT-TYPE-DESC       X(50)            Alphanumeric
  56      4       FILLER                   X(04)            Padding
```

#### DISCGRP — Disclosure Group (CVTRA02Y.cpy)
```
Record Length: 50 bytes (fixed)
VSAM Type: KSDS
Key: DIS-GROUP-KEY (bytes 0-15, composite: DIS-ACCT-GROUP-ID + DIS-TRAN-TYPE-CD + DIS-TRAN-CAT-CD)

Field Layout:
  Offset  Length  Field                    PIC Clause       Type
  0       10      DIS-ACCT-GROUP-ID        X(10)            Alphanumeric
  10      2       DIS-TRAN-TYPE-CD         X(02)            Alphanumeric
  12      4       DIS-TRAN-CAT-CD          9(04)            Numeric display
  16      6       DIS-INT-RATE             S9(04)V99        Signed decimal (overpunch)
  22      28      FILLER                   X(28)            Padding
```

#### TCATBALF — Transaction Category Balance (CVTRA01Y.cpy)
```
Record Length: 50 bytes (fixed)
VSAM Type: KSDS
Key: TRAN-CAT-KEY (bytes 0-16, composite: TRANCAT-ACCT-ID + TRANCAT-TYPE-CD + TRANCAT-CD)

Field Layout:
  Offset  Length  Field                    PIC Clause       Type
  0       11      TRANCAT-ACCT-ID          9(11)            Numeric display
  11      2       TRANCAT-TYPE-CD          X(02)            Alphanumeric
  13      4       TRANCAT-CD               9(04)            Numeric display
  17      11      TRAN-CAT-BAL             S9(09)V99        Signed decimal (overpunch)
  28      22      FILLER                   X(22)            Padding
```

### Interface Contracts Between Programs

| Producer | Consumer | Shared Data | Contract |
|----------|----------|-------------|----------|
| CBTRN02C | CBACT04C | TCATBALF | Category balance records keyed by (acct, type, cat) |
| CBTRN02C | CBTRN03C | TRANSACT | Transaction records with populated TRAN-PROC-TS |
| CBACT04C | CBSTM03A | ACCTFILE | Updated account balances including interest |
| Any | CARDXREF | XREFFILE | Card-to-account-to-customer linkage |
| CBEXPORT | CBIMPORT | EXPFILE | 500-byte multi-type export records per CVEXPORT.cpy |

### Encoding Rules

1. **Overpunch (trailing sign)**: Last byte of PIC S9(n)V99 DISPLAY fields encodes both the last digit and sign:
   - Positive: `{`=0, `A`=1, `B`=2, `C`=3, `D`=4, `E`=5, `F`=6, `G`=7, `H`=8, `I`=9
   - Negative: `}`=0, `J`=1, `K`=2, `L`=3, `M`=4, `N`=5, `O`=6, `P`=7, `Q`=8, `R`=9
2. **COMP-3 (packed decimal)**: Two digits per byte, sign in last nibble (0x0C=positive, 0x0D=negative).
3. **COMP (binary)**: Big-endian binary integer. PIC 9(9) COMP = 4 bytes.
4. **Alphanumeric (PIC X)**: Left-justified, space-padded on the right.
5. **Numeric display (PIC 9)**: Right-justified, zero-padded on the left.

---

## 5. Test Execution Strategy

### CI Pipeline Integration

```
Phase 1: Unit Tests (< 30s)
  - Copybook parser correctness
  - Overpunch encode/decode round-trip
  - COMP-3 encode/decode round-trip
  - Date formatting

Phase 2: Golden-File Tests (< 2min)
  - Parse all 9 ASCII data files
  - Compare against golden JSON references
  - Verify record counts and field sums

Phase 3: Integration Tests (< 5min)
  - Run each migrated Java batch program with sample data
  - Compare output files against golden baselines
  - Run reconciliation checks (counts, sums, checksums)

Phase 4: Cross-Reference Integrity (< 1min)
  - Every card in cardxref.txt has a matching account in acctdata.txt
  - Every card in carddata.txt has an entry in cardxref.txt
  - Every transaction card number exists in cardxref.txt
  - Customer IDs in cardxref.txt exist in custdata.txt
```

### Test Data Management

- Golden files are version-controlled in `golden-files/`.
- Test data is version-controlled in `app/data/ASCII/`.
- When COBOL business logic changes, golden files must be regenerated.
- Golden file generation is documented and repeatable.

### Regression Test Triggers

Run the full test suite when:
- Any Java source file in a migrated program changes.
- Any copybook layout definition changes.
- Golden files are updated.
- New batch programs are migrated.
