# CardDemo Migration Test Strategy

## 1. Overview

This document defines the testing approach for migrating the CardDemo COBOL application to Java. The strategy ensures **functional equivalence** between COBOL and Java implementations across all 44 programs, 9 data files, and 46 JCL batch jobs.

### Test Pyramid

```
                    ┌─────────────┐
                    │   E2E       │  Pipeline-level: full batch cycle
                    │ Reconcile   │  produces identical totals
                    ├─────────────┤
                    │ Differential│  Side-by-side COBOL vs Java
                    │   Tests     │  on same input → same output
                    ├─────────────┤
                    │  Contract   │  Record layout / API contract
                    │   Tests     │  validation (schema-level)
                    ├─────────────┤
                    │ Golden-File │  Known-good input → expected
                    │   Tests     │  output snapshot comparison
                    ├─────────────┤
                    │    Unit     │  Per-function, per-class tests
                    │   Tests     │  (already in java-rewrite/)
                    └─────────────┘
```

---

## 2. Golden-File Tests

### Purpose
Capture known-good outputs from the COBOL programs (or verified sample data) and compare Java outputs against these golden references. Any deviation is a regression.

### Programs to Capture Outputs For

| Priority | Program | Type | Golden Input | Golden Output |
|----------|---------|------|--------------|---------------|
| **P0** | CBACT01C | Batch | `acctdata.txt` (50 records) | OUTFILE, ARRYFILE, VBRCFILE |
| **P0** | CBTRN02C | Batch | `dailytran.txt` (300 records) + ACCTFILE + XREFFILE | Updated TRANSACT, TCATBALF, ACCTFILE |
| **P0** | CBACT04C | Batch | TCATBALF + XREFFILE + DISCGRP + ACCTFILE | Updated ACCTFILE (interest applied) |
| **P0** | CBSTM03A | Batch | XREFFILE + CUSTFILE + ACCTFILE + TRANSACT | Statement file (text + HTML) |
| **P1** | CBTRN03C | Batch | TRANSACT + XREFFILE + TRANTYPE | Report file (REPTFILE) |
| **P1** | CBEXPORT | Batch | All VSAM files | Export sequential file |
| **P1** | CBIMPORT | Batch | Export file | Normalized VSAM files |
| **P2** | CBACT02C | Batch | `carddata.txt` | Card output file |
| **P2** | CBACT03C | Batch | `cardxref.txt` | Cross-reference output |
| **P2** | CBCUS01C | Batch | `custdata.txt` | Customer output file |

### Input Sources

All golden inputs come from `app/data/ASCII/`:

| File | Records | Record Length | Copybook | Entity |
|------|---------|--------------|----------|--------|
| `acctdata.txt` | 50 | 300 | CVACT01Y | Account |
| `carddata.txt` | 50 | 150 | CVACT02Y | Card |
| `cardxref.txt` | 50 | 36 | CVACT03Y | Card-Account XREF |
| `custdata.txt` | 50 | 500 | CVCUS01Y | Customer |
| `dailytran.txt` | 300 | 350 | CVTRA06Y | Daily Transaction |
| `discgrp.txt` | 51 | 50 | CVTRA02Y | Disclosure Group |
| `tcatbal.txt` | 50 | 51 | CVTRA01Y | Transaction Category Balance |
| `trancatg.txt` | 18 | 61 | CVTRA04Y | Transaction Category |
| `trantype.txt` | 7 | 61 | CVTRA03Y | Transaction Type |

### Golden-File JSON Representations

Structured JSON versions of each data file are stored in `golden-files/`. Each JSON file contains:
- `metadata`: file name, record count, record length, copybook reference
- `field_definitions`: field name, PIC clause, byte offset, byte length, data type, business meaning
- `records`: array of parsed records with named fields and decoded values

### Comparison Method

```
1. Parse COBOL output using copybook-based fixed-width parser
2. Parse Java output (pipe-delimited or JSON)
3. For each record:
   a. Match by primary key (ACCT-ID, CARD-NUM, TRAN-ID, etc.)
   b. Compare every field value with type-aware comparison:
      - Numeric: BigDecimal equality (no floating-point tolerance)
      - String: exact match after trimming trailing spaces
      - Date: format-normalized comparison (YYYY-MM-DD canonical)
   c. Report mismatches with: field name, byte position, expected, actual
4. Fail if ANY field in ANY record differs
```

---

## 3. Differential Tests

### Purpose
Run both COBOL and Java implementations on the **same input** and compare outputs field-by-field. This is the definitive proof of functional equivalence.

### Architecture

```
                    ┌──────────────┐
                    │  Input Data  │
                    │ (ASCII files)│
                    └──────┬───────┘
                           │
              ┌────────────┴────────────┐
              │                         │
     ┌────────▼────────┐     ┌──────────▼──────────┐
     │  COBOL Program  │     │   Java Program       │
     │  (GnuCOBOL or   │     │   (Spring Batch /    │
     │   baseline file) │     │    standalone JAR)   │
     └────────┬────────┘     └──────────┬──────────┘
              │                         │
     ┌────────▼────────┐     ┌──────────▼──────────┐
     │  COBOL Output   │     │   Java Output        │
     │  (fixed-width)  │     │   (pipe-delimited)   │
     └────────┬────────┘     └──────────┬──────────┘
              │                         │
              └────────────┬────────────┘
                           │
                  ┌────────▼────────┐
                  │  Diff Engine    │
                  │  (field-by-     │
                  │   field compare)│
                  └────────┬────────┘
                           │
                  ┌────────▼────────┐
                  │  Mismatch       │
                  │  Report         │
                  └─────────────────┘
```

### Execution Modes

1. **Baseline Mode** (no COBOL runtime available):
   - Use pre-captured golden files as COBOL output
   - Parse golden JSON and compare against Java output
   - Suitable for CI/CD pipeline integration

2. **Live Mode** (GnuCOBOL available):
   - Compile and run COBOL program with GnuCOBOL
   - Run Java program with same input
   - Compare both outputs in real-time
   - 8 batch programs compile with GnuCOBOL; 16 CICS programs need IBM preprocessor

### Programs Suitable for Live Differential Testing

| Program | GnuCOBOL Compilable | External Deps | Notes |
|---------|-------------------|---------------|-------|
| CBACT01C | Yes | COBDATFT (assembler) | Replace with Java DateConverter |
| CBACT02C | Yes | CEE3ABD (LE abend) | Replace with System.exit |
| CBACT03C | Yes | CEE3ABD | Replace with System.exit |
| CBCUS01C | Yes | CEE3ABD | Replace with System.exit |
| CBTRN01C | Yes | None | Clean compile |
| CBTRN02C | Yes | None | Clean compile |
| CBTRN03C | Yes | None | Clean compile |
| CBSTM03A/B | Yes | None | Statement generation pair |

### Numeric Comparison Rules

COBOL uses fixed-point arithmetic (PIC S9(n)V99). Java must use `BigDecimal` with matching precision:

| COBOL PIC | Java Type | Comparison Rule |
|-----------|-----------|-----------------|
| PIC S9(10)V99 | BigDecimal(12,2) | Exact equality via `compareTo() == 0` |
| PIC S9(09)V99 | BigDecimal(11,2) | Exact equality |
| PIC S9(04)V99 | BigDecimal(6,2) | Exact equality |
| PIC 9(n) | long or BigInteger | Exact equality |
| PIC X(n) | String | Trim trailing spaces, then equals |

**No tolerance allowed for monetary fields.** Sub-penny errors compound across batch runs.

---

## 4. Batch Reconciliation

### Purpose
After each batch run, verify aggregate totals to catch systemic errors that field-level tests might miss (e.g., dropped records, double-processing).

### Reconciliation Checks Per Batch Job

#### POSTTRAN (CBTRN02C) — Transaction Posting
```
PRE-RUN:
  count(DALYTRAN input records)
  sum(DALYTRAN-AMT)  [all daily transactions]
  count(TRANSACT records before)
  sum(ACCT-CURR-BAL) [all accounts before]

POST-RUN:
  count(TRANSACT records after) = count(before) + count(DALYTRAN)
  sum(ACCT-CURR-BAL after) = sum(before) + sum(purchase amounts) - sum(payment amounts)
  sum(TRAN-CAT-BAL changes) = sum(DALYTRAN-AMT) grouped by type+category
  count(rejected transactions) + count(posted transactions) = count(DALYTRAN)
```

#### INTCALC (CBACT04C) — Interest Calculation
```
PRE-RUN:
  sum(ACCT-CURR-BAL) [all accounts]
  sum(TRAN-CAT-BAL)  [all category balances]
  count(active accounts where BAL > 0)

POST-RUN:
  sum(ACCT-CURR-BAL after) = sum(before) + sum(interest charges)
  interest_charged(acct) = ACCT-CURR-BAL * DIS-INT-RATE / 1200  [monthly]
  Every account with positive balance has interest applied
  No interest applied to zero or negative balances
```

#### CREASTMT (CBSTM03A) — Statement Generation
```
POST-RUN:
  count(statements generated) = count(distinct accounts in XREFFILE)
  For each statement:
    sum(transaction amounts) = closing balance - opening balance
    page totals sum to account total
    account totals sum to grand total
  All XREF card numbers appear in statement
```

#### TRANREPT (CBTRN03C) — Transaction Report
```
POST-RUN:
  count(report detail lines) = count(TRANSACT records)
  sum(report amounts) = sum(TRAN-AMT in TRANSACT)
  page totals + account totals + grand total all consistent
  Every transaction type code maps to a valid TRANTYPE record
```

### Cross-File Referential Integrity

These checks should pass before AND after every batch run:

```python
# Every card in CARDXREF has a matching account in ACCTDATA
assert all(xref.acct_id in acctdata.acct_ids for xref in cardxref)

# Every card in CARDXREF has a matching customer in CUSTDATA
assert all(xref.cust_id in custdata.cust_ids for xref in cardxref)

# Every card number in CARDDATA appears in CARDXREF
assert all(card.card_num in cardxref.card_nums for card in carddata)

# Every transaction card number exists in CARDXREF
assert all(tran.card_num in cardxref.card_nums for tran in transactions)

# Every TCATBAL account ID exists in ACCTDATA
assert all(tcatbal.acct_id in acctdata.acct_ids for tcatbal in tcatbal_records)

# Every DISCGRP group ID exists in at least one account
assert all(dg.group_id in acctdata.group_ids for dg in discgrp)

# Every TRANCATG type+category matches a valid TRANTYPE
assert all(tc.type_cd in trantype.type_codes for tc in trancatg)
```

### Checksum Strategy

For each VSAM file, compute before/after:
1. **Record count** — must match expected delta
2. **Numeric field sums** — monetary totals must balance
3. **Key uniqueness** — no duplicate primary keys
4. **Hash digest** — MD5 of sorted key+value pairs for immutable files

---

## 5. Contract Tests

### Purpose
Codify the record layouts, file formats, and interface contracts so that any change to the Java implementation that breaks compatibility is caught immediately.

### File Format Contracts

Each VSAM file has a contract specifying:

| Contract Element | Validation |
|-----------------|------------|
| Record length | Fixed: must match copybook RECLN exactly |
| Field positions | Byte offsets must match copybook layout |
| PIC clause types | Numeric fields must parse as valid overpunch-encoded decimals |
| Key uniqueness | Primary key fields must be unique across all records |
| Value domains | Status fields in {Y, N}, dates in YYYY-MM-DD, etc. |

### Record Layout Contracts (from Copybooks)

```
CVACT01Y (Account):    RECLN=300, KEY=ACCT-ID[0:11]
CVACT02Y (Card):       RECLN=150, KEY=CARD-NUM[0:16]
CVACT03Y (Card-XREF):  RECLN=36,  KEY=XREF-CARD-NUM[0:16]
CVCUS01Y (Customer):   RECLN=500, KEY=CUST-ID[0:9]
CVTRA05Y (Transaction): RECLN=350, KEY=TRAN-ID[0:16]
CVTRA06Y (Daily Tran): RECLN=350, KEY=DALYTRAN-ID[0:16]
CVTRA01Y (Cat Balance): RECLN=51,  KEY=ACCT-ID[0:11]+TYPE-CD[11:13]+CAT-CD[13:17]
CVTRA02Y (Disc Group): RECLN=50,  KEY=GROUP-ID[0:10]+TYPE-CD[10:12]+CAT-CD[12:16]
CVTRA03Y (Tran Type):  RECLN=60,  KEY=TRAN-TYPE[0:2]
CVTRA04Y (Tran Catg):  RECLN=60,  KEY=TYPE-CD[0:2]+CAT-CD[2:6]
```

### Interface Contracts

#### CICS COMMAREA Contract (COCOM01Y)
The COMMAREA is the inter-program data passing mechanism. The Java equivalent (API DTOs) must preserve:
- All field names and sizes
- Navigation state (program name, return code, selected account/card/transaction)
- Error message structure

#### Assembler Replacement Contracts

| Assembler | Java Replacement | Contract |
|-----------|-----------------|----------|
| COBDATFT | `DateConverter` | Input: YYYY-MM-DD (type 2) → Output: YYYYMMDD (type 2). Input: YYYYMMDD (type 1) → Output: YYYY-MM-DD (type 1) |
| CEE3ABD | `System.exit(999)` | Abnormal termination with return code |
| CEEDAYS | `java.time.LocalDate` | Lilian day number ↔ calendar date conversion |
| MVSWAIT | `Thread.sleep()` | Wait for specified duration |

### Validation Rules Contract (from CSLKPCDY)

These validation rules must be preserved in Java:
- **State codes**: 50 valid US state codes (AL, AK, AZ, ...)
- **ZIP prefixes**: Valid 5-digit ZIP ranges per state
- **Phone area codes**: NANPA-compliant area codes (200–999, excluding reserved)

---

## 6. Test Execution Pipeline

### CI/CD Integration

```yaml
# Suggested GitHub Actions workflow
migration-tests:
  steps:
    - name: Contract Tests
      run: python test-harness/run_tests.py --suite contract

    - name: Golden-File Tests
      run: python test-harness/run_tests.py --suite golden

    - name: Reconciliation Checks
      run: python test-harness/run_tests.py --suite reconciliation

    - name: Differential Tests (if COBOL baseline available)
      run: python test-harness/run_tests.py --suite differential
```

### Test Data Management

- **Golden files** are committed to `golden-files/` as JSON
- **Test inputs** reference `app/data/ASCII/` (already in repo)
- **COBOL baselines** are captured once and stored as golden outputs
- **No production data** in the repo — all synthetic (50 accounts, 300 transactions)

### Failure Triage

| Failure Type | Likely Cause | Action |
|-------------|--------------|--------|
| Field mismatch (numeric) | BigDecimal rounding or overpunch parsing | Check `CobolDecimalParser`, verify PIC clause scale |
| Field mismatch (date) | Date format conversion error | Check `DateConverter` type codes |
| Record count mismatch | EOF handling or blank-line skipping | Check record reader loop termination |
| Referential integrity | Missing XREF or orphaned records | Check data load order and key generation |
| Checksum mismatch | Field ordering or padding difference | Compare raw bytes at mismatch position |
