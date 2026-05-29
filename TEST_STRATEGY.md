# Migration Testing Strategy

## 1. Executive Summary

### Purpose

This document defines a comprehensive testing strategy for migrating the CardDemo application's 31 COBOL programs to Java. The strategy ensures functional equivalence between legacy COBOL execution and the target Java implementation through multiple validation approaches.

### Programs Breakdown

| Category | Count | Prefix/Pattern | Description |
|----------|-------|----------------|-------------|
| Batch programs | 8 | CB* | File processing, reporting, data export/import |
| Online/CICS programs | 16 | CO* | Interactive screen-based transaction processing |
| Statement programs | 3 | CBSTM* | Account statement generation and formatting |
| Utility programs | 3 | CSUTLDTC, CBEXPORT, CBIMPORT | Date conversion, data export, data import |
| Wait program | 1 | COBSWAIT | Timer utility (parm in centiseconds) |

### Scope

This strategy covers the core `app/` directory only:
- `app/cbl/` — 31 COBOL source programs (+ 13 additional modules)
- `app/cpy/` — 30 copybooks defining data structures
- `app/jcl/` — 38 JCL job definitions
- `app/data/` — 9 ASCII data files and corresponding EBCDIC files

---

## 2. Testing Approaches

The migration testing strategy is built on four complementary pillars that together provide high confidence in functional equivalence.

### A. Golden-File Testing

Golden-file testing establishes a canonical reference for every record in the CardDemo data files. Because the data files are small (7–300 records each), golden files include **ALL** records — no sampling is needed.

#### Approach

1. Parse the 9 ASCII data files against their corresponding copybook PIC layouts
2. Generate JSON golden-reference files for every record
3. Use the golden files as fixtures in JUnit 5 tests to validate the Java parser output

#### COBOL Sign Encoding for ASCII

Signed numeric fields (PIC S9(n)Vnn) use overpunch encoding in the trailing digit position:

| Character | Sign | Digit |
|-----------|------|-------|
| `{` | +0 | `}` | -0 |
| `A` | +1 | `J` | -1 |
| `B` | +2 | `K` | -2 |
| `C` | +3 | `L` | -3 |
| `D` | +4 | `M` | -4 |
| `E` | +5 | `N` | -5 |
| `F` | +6 | `O` | -6 |
| `G` | +7 | `P` | -7 |
| `H` | +8 | `Q` | -8 |
| `I` | +9 | `R` | -9 |

#### Copybook-to-File Mapping

| Data File | Copybook | Record Name | Declared RECLN | Actual Line Length | Notes |
|-----------|----------|-------------|----------------|-------------------|-------|
| acctdata | CVACT01Y.cpy | ACCOUNT-RECORD | 300 | 300 | — |
| carddata | CVACT02Y.cpy | CARD-RECORD | 150 | 150 | — |
| cardxref | CVACT03Y.cpy | CARD-XREF-RECORD | 50 | 36 | No trailing padding to 50 |
| custdata | CVCUS01Y.cpy | CUSTOMER-RECORD | 500 | 500 | — |
| dailytran | CVTRA06Y.cpy | DALYTRAN-RECORD | 350 | 350 | — |
| discgrp | CVTRA02Y.cpy | DIS-GROUP-RECORD | 50 | 50 | — |
| tcatbal | CVTRA01Y.cpy | TRAN-CAT-BAL-RECORD | 50 | 51 | Actual line length is 51 chars |
| trancatg | CVTRA04Y.cpy | TRAN-CAT-RECORD | 60 | 61 | Actual line length is 61 chars |
| trantype | CVTRA03Y.cpy | TRAN-TYPE-RECORD | 60 | 61 | Actual line length is 61 chars |

#### Key Field Layouts

- **ACCOUNT-RECORD** (CVACT01Y): ACCT-ID PIC 9(11), ACCT-CURR-BAL PIC S9(10)V99, ACCT-CREDIT-LIMIT PIC S9(10)V99, plus dates, zip, group ID, 178-byte FILLER
- **CARD-RECORD** (CVACT02Y): CARD-NUM PIC X(16), CARD-ACCT-ID PIC 9(11), CARD-CVV-CD PIC 9(03), CARD-EMBOSSED-NAME PIC X(50), dates, status, 59-byte FILLER
- **CARD-XREF-RECORD** (CVACT03Y): XREF-CARD-NUM PIC X(16), XREF-CUST-ID PIC 9(09), XREF-ACCT-ID PIC 9(11), 14-byte FILLER
- **CUSTOMER-RECORD** (CVCUS01Y): CUST-ID PIC 9(09), name fields, address fields, SSN PIC 9(09), FICO score PIC 9(03), 168-byte FILLER
- **DALYTRAN-RECORD** (CVTRA06Y): DALYTRAN-ID PIC X(16), DALYTRAN-AMT PIC S9(09)V99, merchant info, card num, timestamps, 20-byte FILLER
- **DIS-GROUP-RECORD** (CVTRA02Y): DIS-ACCT-GROUP-ID PIC X(10), DIS-TRAN-TYPE-CD PIC X(02), DIS-TRAN-CAT-CD PIC 9(04), DIS-INT-RATE PIC S9(04)V99, 28-byte FILLER
- **TRAN-CAT-BAL-RECORD** (CVTRA01Y): TRANCAT-ACCT-ID PIC 9(11), TRANCAT-TYPE-CD PIC X(02), TRANCAT-CD PIC 9(04), TRAN-CAT-BAL PIC S9(09)V99, 22-byte FILLER
- **TRAN-CAT-RECORD** (CVTRA04Y): TRAN-TYPE-CD PIC X(02), TRAN-CAT-CD PIC 9(04), TRAN-CAT-TYPE-DESC PIC X(50), 4-byte FILLER
- **TRAN-TYPE-RECORD** (CVTRA03Y): TRAN-TYPE PIC X(02), TRAN-TYPE-DESC PIC X(50), 8-byte FILLER

---

### B. Differential Testing

Differential testing runs both the COBOL programs and their Java equivalents against the same input data, then compares outputs field-by-field.

#### Tolerance Rules

| Rule | Description |
|------|-------------|
| Trailing spaces | Ignore — COBOL PIC X fields are padded with spaces |
| Leading zeros on numerics | Normalize — PIC 9(n) fields have leading zeros |
| Decimal precision | COBOL PIC S9(n)V99 may differ from Java BigDecimal rounding at edges |
| FILLER fields | Skip entirely — no business meaning |

#### Batch Program Differential Test Plan

| Program | Function | Input Files | Output | Test Approach |
|---------|----------|-------------|--------|---------------|
| CBACT01C | Read/write account file | acctdata (KSDS) | Print file | Compare printed records field-by-field |
| CBACT02C | Read/print card data | carddata (KSDS) | Print file | Compare printed records field-by-field |
| CBACT03C | Read/print cross-reference | cardxref (KSDS) | Print file | Compare printed records field-by-field |
| CBACT04C | Interest calculation | tcatbal, discgrp (KSDS) | Updated tcatbal | Compare balances with decimal tolerance |
| CBCUS01C | Read/print customer data | custdata (KSDS) | Print file | Compare printed records field-by-field |
| CBEXPORT | Export for branch migration | custdata, acctdata, cardxref, dailytran | Export file | Compare multi-record export output |
| CBIMPORT | Import from branch migration | Export file | custdata, acctdata, cardxref, dailytran | Compare split normalized files |
| CBTRN01C | Post daily transactions | dailytran, acctdata, tcatbal | Updated files | Compare account balances and category totals |
| CBTRN02C | Combine transactions | Transaction files | Combined file | Compare combined output record-by-record |
| CBTRN03C | Transaction report | Transaction files | Report file | Compare report totals and line items |

#### Execution Model

1. Run COBOL program via GnuCOBOL (or mainframe emulator) with known input
2. Capture all output files and report data
3. Run equivalent Java program with identical input
4. Execute field-by-field comparison applying tolerance rules
5. Report any differences exceeding tolerance thresholds

---

### C. Reconciliation Testing

Reconciliation testing validates data integrity across the entire processing pipeline.

#### Record Count Validation

- For each batch job: count input records vs. output records
- Verify totals match (or match expected transformation rules, e.g., filtered records)
- Use record count from each file as baseline: acctdata=50, carddata=50, cardxref=50, custdata=50, dailytran=300, discgrp=51, tcatbal=50, trancatg=18, trantype=7

#### Numeric Sum Validation

- Sum all account balances (ACCT-CURR-BAL) before and after processing
- Sum all transaction amounts (DALYTRAN-AMT) and verify against balance changes
- Validate interest calculations match expected rates from discgrp

#### Cross-Reference Integrity

Verify referential integrity using the XREF file (cardxref):
- Every XREF-CARD-NUM must exist in carddata (CARD-NUM)
- Every XREF-ACCT-ID must exist in acctdata (ACCT-ID)
- Every XREF-CUST-ID must exist in custdata (CUST-ID)
- Card → Account → Customer chain must be complete and consistent

#### JCL Job Data Flows

The 38 JCL jobs in `app/jcl/` define the batch processing pipeline. Key data flows to validate:

| Job Category | Jobs | Data Flow |
|-------------|------|-----------|
| File open/close | OPENFIL, CLOSEFIL | KSDS file state management |
| Account processing | ACCTFILE, READACCT | Account data read/write |
| Card processing | CARDFILE, READCARD | Card data read/write |
| Customer processing | CUSTFILE, READCUST, DEFCUST | Customer data lifecycle |
| Cross-reference | XREFFILE, READXREF | Card-Account-Customer linkage |
| Transaction processing | TRANFILE, POSTTRAN, COMBTRAN, DALYREJS | Transaction pipeline |
| Transaction reporting | TRANREPT, TRANBKP, TRANIDX, TRANCATG, TRANTYPE | Reporting and categorization |
| Category/balance | TCATBALF, PRTCATBL, DISCGRP | Balance and disclosure |
| Interest | INTCALC | Interest rate computation |
| Statement | CREASTMT, REPTFILE | Statement generation |
| Export/Import | CBEXPORT, CBIMPORT | Branch migration |
| Infrastructure | DEFGDGB, DEFGDGD, ESDSRRDS, WAITSTEP | GDG, ESDS/RRDS, timer |
| Security | DUSRSECJ, CBADMCDJ | User admin |
| Utility | FTPJCL, INTRDRJ1, INTRDRJ2, TXT2PDF1 | FTP, internal reader, PDF |

---

### D. Contract Testing

Contract testing ensures the migrated CICS online programs preserve their external interfaces when exposed as REST APIs.

#### Screen-to-REST Mapping

Each CICS BMS map-based program will be exposed as one or more REST endpoints:

| COBOL Program | Function | Proposed REST Endpoint |
|---------------|----------|----------------------|
| COSGN00C | Sign-on | POST /api/auth/login |
| COMEN01C | Main menu | GET /api/menu |
| COADM01C | Admin menu | GET /api/admin/menu |
| COACTVWC | Account view | GET /api/accounts/{id} |
| COACTUPC | Account update | PUT /api/accounts/{id} |
| COCRDLIC | List credit cards | GET /api/cards |
| COCRDSLC | Card detail view | GET /api/cards/{id} |
| COCRDUPC | Card update | PUT /api/cards/{id} |
| COBIL00C | Bill payment | POST /api/payments |
| COTRN00C | List transactions | GET /api/transactions |
| COTRN01C | View transaction | GET /api/transactions/{id} |
| COTRN02C | Add transaction | POST /api/transactions |
| CORPT00C | Transaction reports | POST /api/reports/transactions |
| COUSR00C | List users | GET /api/users |
| COUSR01C | Add user | POST /api/users |
| COUSR02C | Update user | PUT /api/users/{id} |
| COUSR03C | Delete user | DELETE /api/users/{id} |

#### COMMAREA Data Contracts

- Each CICS program communicates via COMMAREA (defined in COCOM01Y.cpy)
- Java DTOs must preserve the same field names, types, and sizes
- Validation: serialize Java DTO to fixed-length format and compare byte-for-byte with expected COMMAREA layout
- Null handling: COBOL spaces → Java null/empty string (configurable per field)

#### API Contract Validation

- OpenAPI/Swagger specification generated from Java controllers
- Contract tests verify:
  - Request/response field names match COMMAREA/copybook fields
  - Data types are compatible (PIC 9 → integer/long, PIC S9V99 → BigDecimal, PIC X → String)
  - Required/optional field semantics preserved
  - Error responses map to CICS RESP/RESP2 codes

---

## 3. Test Infrastructure

### Technology Stack

| Component | Technology | Purpose |
|-----------|-----------|---------|
| Language | Java 17+ | Target migration platform |
| Build | Maven | Dependency management and build |
| Test Framework | JUnit 5 | Test execution and assertions |
| Assertions | AssertJ | Fluent field-by-field comparison |
| JSON | Jackson | Golden file serialization/deserialization |
| COBOL Runtime | GnuCOBOL | Reference execution of original programs |

### Project Structure

```
migration-test-harness/
├── golden-files/                    # Golden file generation project
│   ├── pom.xml
│   ├── src/main/java/
│   │   └── com/carddemo/golden/
│   │       ├── CobolRecordParser.java       # Parse ASCII files using PIC layouts
│   │       ├── CopybookLayout.java          # Copybook field definitions
│   │       ├── SignedNumericDecoder.java     # COBOL overpunch sign decoding
│   │       └── GoldenFileGenerator.java     # JSON output generator
│   └── src/main/resources/
│       └── layouts/                          # Copybook layout definitions
├── test-harness/                    # JUnit 5 test project
│   ├── pom.xml
│   ├── src/test/java/
│   │   └── com/carddemo/test/
│   │       ├── golden/                      # Golden file validation tests
│   │       ├── differential/                # Differential comparison tests
│   │       ├── reconciliation/              # Data integrity tests
│   │       └── contract/                    # API contract tests
│   └── src/test/resources/
│       └── golden/                          # Generated JSON golden files
└── test-utilities/                  # Shared test utilities
    ├── pom.xml
    └── src/main/java/
        └── com/carddemo/util/
            ├── CobolRecordParser.java       # Shared record parsing
            ├── FieldByFieldComparator.java  # Tolerance-aware comparison
            ├── RecordCountValidator.java    # Count-based reconciliation
            ├── NumericSumValidator.java     # Sum-based reconciliation
            └── CrossReferenceValidator.java # Referential integrity checks
```

### Key Test Utilities

#### CobolRecordParser
- Reads fixed-length ASCII records using copybook-derived field offsets
- Handles PIC X (alphanumeric), PIC 9 (unsigned numeric), PIC S9V99 (signed decimal)
- Decodes COBOL overpunch sign encoding for signed fields
- Handles actual vs. declared record length discrepancies (e.g., cardxref 36 vs. 50)

#### FieldByFieldComparator
- Compares two record objects field-by-field
- Applies configurable tolerance rules (trailing spaces, leading zeros, decimal precision)
- Skips FILLER fields automatically
- Reports differences with field name, expected value, actual value, and tolerance applied

#### RecordCountValidator
- Counts records in input and output files
- Validates expected record count relationships (1:1, 1:N, filtered)
- Reports mismatches with file names and counts

#### NumericSumValidator
- Sums specified numeric fields across all records in a file
- Compares sums between COBOL output and Java output
- Applies decimal precision tolerance for signed numeric fields

#### CrossReferenceValidator
- Validates referential integrity across related files
- Checks card→account→customer chain completeness
- Reports orphan records and broken references

---

## 4. Data File Inventory

| # | Filename | Copybook | Record Length | Actual Line Length | Record Count | Key Fields |
|---|----------|----------|--------------|-------------------|--------------|------------|
| 1 | acctdata.txt | CVACT01Y.cpy | 300 | 300 | 50 | ACCT-ID (9(11)) |
| 2 | carddata.txt | CVACT02Y.cpy | 150 | 150 | 50 | CARD-NUM (X(16)), CARD-ACCT-ID (9(11)) |
| 3 | cardxref.txt | CVACT03Y.cpy | 50 | 36 | 50 | XREF-CARD-NUM (X(16)), XREF-CUST-ID (9(09)), XREF-ACCT-ID (9(11)) |
| 4 | custdata.txt | CVCUS01Y.cpy | 500 | 500 | 50 | CUST-ID (9(09)) |
| 5 | dailytran.txt | CVTRA06Y.cpy | 350 | 350 | 300 | DALYTRAN-ID (X(16)), DALYTRAN-CARD-NUM (X(16)) |
| 6 | discgrp.txt | CVTRA02Y.cpy | 50 | 50 | 51 | DIS-ACCT-GROUP-ID (X(10)), DIS-TRAN-TYPE-CD (X(02)), DIS-TRAN-CAT-CD (9(04)) |
| 7 | tcatbal.txt | CVTRA01Y.cpy | 50 | 51 | 50 | TRANCAT-ACCT-ID (9(11)), TRANCAT-TYPE-CD (X(02)), TRANCAT-CD (9(04)) |
| 8 | trancatg.txt | CVTRA04Y.cpy | 60 | 61 | 18 | TRAN-TYPE-CD (X(02)), TRAN-CAT-CD (9(04)) |
| 9 | trantype.txt | CVTRA03Y.cpy | 60 | 61 | 7 | TRAN-TYPE (X(02)) |

---

## 5. Program Inventory

| # | Program | Type | Description | Migration Priority |
|---|---------|------|-------------|-------------------|
| 1 | CBACT01C | Batch | Read account file and write to output files | High |
| 2 | CBACT02C | Batch | Read and print card data file | High |
| 3 | CBACT03C | Batch | Read and print account cross-reference file | High |
| 4 | CBACT04C | Batch | Interest calculator — reads tcatbal and discgrp | High |
| 5 | CBCUS01C | Batch | Read and print customer data file | High |
| 6 | CBEXPORT | Batch | Export customer data for branch migration | Medium |
| 7 | CBIMPORT | Batch | Import customer data from branch migration export | Medium |
| 8 | CBTRN01C | Batch | Post daily transactions to accounts | High |
| 9 | CBTRN02C | Batch | Combine transaction files | High |
| 10 | CBTRN03C | Batch | Transaction reporting | High |
| 11 | CBSTM03A | Batch (Statement) | Print account statements (text and HTML) | Medium |
| 12 | CBSTM03B | Batch (Statement) | File processing subroutine for statement report | Medium |
| 13 | COBSWAIT | Batch (Wait) | Utility to wait (parameter in centiseconds) | Low |
| 14 | CSUTLDTC | Batch (Utility) | Date conversion utility (CEEDAYS) | Medium |
| 15 | COSGN00C | Online/CICS | Sign-on screen for CardDemo application | High |
| 16 | COMEN01C | Online/CICS | Main menu for regular users | High |
| 17 | COADM01C | Online/CICS | Admin menu for admin users | High |
| 18 | COACTVWC | Online/CICS | Accept and process account view request | High |
| 19 | COACTUPC | Online/CICS | Accept and process account update | High |
| 20 | COBIL00C | Online/CICS | Bill payment — pay account balance | High |
| 21 | COCRDLIC | Online/CICS | List credit cards | High |
| 22 | COCRDSLC | Online/CICS | View credit card detail | High |
| 23 | COCRDUPC | Online/CICS | Update credit card detail | High |
| 24 | COTRN00C | Online/CICS | List transactions from TRANSACT file | High |
| 25 | COTRN01C | Online/CICS | View a transaction | High |
| 26 | COTRN02C | Online/CICS | Add a new transaction | High |
| 27 | CORPT00C | Online/CICS | Print transaction reports (submit batch) | Medium |
| 28 | COUSR00C | Online/CICS | List all users from USRSEC file | Medium |
| 29 | COUSR01C | Online/CICS | Add a new regular/admin user | Medium |
| 30 | COUSR02C | Online/CICS | Update a user in USRSEC file | Medium |
| 31 | COUSR03C | Online/CICS | Delete a user from USRSEC file | Medium |

---

## 6. Future Scope

The following sub-applications are out of scope for the initial migration but are planned for subsequent phases.

### app-authorization-ims-db2-mq/

- **Description**: Authorization subsystem using IMS DB, Db2, and MQ messaging
- **Technology**: IMS DB/DC, Db2 SQL, IBM MQ
- **Testing approach**: TBD — will require IMS and MQ emulation or containerized test environment
- **Dependencies**: Requires MQ broker and IMS database setup

### app-transaction-type-db2/

- **Description**: Transaction type management subsystem using Db2
- **Technology**: COBOL with embedded SQL, Db2
- **Testing approach**: TBD — will require Db2 instance (or compatible database) for integration testing
- **Dependencies**: Db2 schema definitions, stored procedures

### app-vsam-mq/

- **Description**: VSAM-based processing with MQ integration
- **Technology**: VSAM (KSDS/ESDS/RRDS), IBM MQ
- **Testing approach**: TBD — VSAM file operations can be simulated with indexed file I/O; MQ requires broker
- **Dependencies**: MQ broker configuration, VSAM cluster definitions
