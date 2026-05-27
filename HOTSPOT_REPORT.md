# Hotspot Report — CardDemo COBOL Estate

> Ranking the top programs by complexity, and modernization prioritization recommendations.

---

## 1. Top 10 Programs by Lines of Code

| Rank | Program | LOC | Location | Classification |
|------|---------|-----|----------|---------------|
| 1 | COACTUPC | 4,237 | app/cbl/ | Online (CICS) |
| 2 | COTRTLIC | 2,099 | app/app-transaction-type-db2/cbl/ | Online (CICS/DB2) |
| 3 | COTRTUPC | 1,703 | app/app-transaction-type-db2/cbl/ | Online (CICS/DB2) |
| 4 | COCRDUPC | 1,561 | app/cbl/ | Online (CICS) |
| 5 | COCRDLIC | 1,460 | app/cbl/ | Online (CICS) |
| 6 | COPAUS0C | 1,033 | app/app-authorization-ims-db2-mq/cbl/ | Online (CICS/IMS) |
| 7 | COPAUA0C | 1,027 | app/app-authorization-ims-db2-mq/cbl/ | Online (CICS/MQ) |
| 8 | COACTVWC | 942 | app/cbl/ | Online (CICS) |
| 9 | CBSTM03A | 925 | app/cbl/ | Batch |
| 10 | COCRDSLC | 888 | app/cbl/ | Online (CICS) |

---

## 2. Top 10 Programs by Copybooks Referenced

| Rank | Program | Copybook Count | Key Copybooks |
|------|---------|---------------|---------------|
| 1 | COACTUPC | 19 | CVACT01Y, CVACT03Y, CVCUS01Y, CSLKPCDY, CSUTLDPY, CSUTLDWY, CSSETATY (×39), CSSTRPFY, COCOM01Y, CVCRD01Y + BMS/utility |
| 2 | COACTVWC | 15 | CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y, COCOM01Y, CVCRD01Y, CSSTRPFY + BMS/utility |
| 3 | COCRDSLC | 15 | Same data copybooks as COACTVWC + COCRDSL map |
| 4 | COCRDUPC | 15 | Same data copybooks as COCRDSLC + COCRDUP map |
| 5 | COPAUA0C | 14 | CMQ* (MQ headers), CCPAURQY, CCPAURLY, CCPAUERY, CIPAUSMY, CIPAUDTY, CVACT03Y, CVACT01Y, CVCUS01Y |
| 6 | COPAUS0C | 14 | COCOM01Y + IMS/BMS copybooks |
| 7 | COTRTUPC | 14 | CSUTLDWY, CVCRD01Y, DFHBMSCA, DFHAID, COTTL01Y, COTRTUP, CSDAT01Y, CSMSG01Y/02Y, CSUSR01Y, COCOM01Y, CSSETATY, CSSTRPFY |
| 8 | COCRDLIC | 13 | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COCRDLI, CSDAT01Y, CSMSG01Y, CSUSR01Y, CVACT02Y, CSSTRPFY |
| 9 | COTRTLIC | 11 | BMS/DB2 copybooks |
| 10 | COPAUS1C | 10 | IMS/BMS copybooks |

---

## 3. Top 10 Programs by I/O Operations

| Rank | Program | I/O Count | I/O Types |
|------|---------|----------|-----------|
| 1 | CBSTM03A | 97 | Heavy WRITE to STMTFILE and HTMLFILE (statement + HTML generation) |
| 2 | CBEXPORT | 22 | OPEN/READ 5 input files, WRITE export file |
| 3 | CBIMPORT | 21 | READ export file, WRITE to 6 output files + error file |
| 4 | CBTRN02C | 21 | READ DALYTRAN/XREF, I-O ACCTFILE/TCATBALF, WRITE TRANFILE/DALYREJS |
| 5 | CBTRN03C | 18 | READ 5 input files, WRITE report file |
| 6 | CBACT04C | 17 | READ TCATBALF/XREF/DISCGRP, I-O ACCTFILE, WRITE TRANSACT |
| 7 | COTRTLIC | 16 | EXEC SQL + CICS SEND/RECEIVE (DB2 + BMS) |
| 8 | CBTRN01C | 15 | READ from 6 input VSAM files |
| 9 | CBSTM03B | 12 | OPEN/READ 4 input files |
| 10 | COCRDLIC | 12 | CICS STARTBR/READNEXT/READPREV/ENDBR (VSAM browse) |

---

## 4. Top 10 Programs by Business Logic Density

Measured as maximum nesting depth of IF + EVALUATE statements (higher = more complex decision logic).

| Rank | Program | IF Depth | EVALUATE Depth | Combined | Dominant Logic |
|------|---------|----------|---------------|----------|---------------|
| 1 | COACTUPC | 165 | 10 | 175 | Field validation (39× CSSETATY REPLACING for every screen field), account update rules |
| 2 | COTRTLIC | 100 | 16 | 116 | DB2 cursor navigation, list pagination, type/category filtering |
| 3 | COCRDUPC | 75 | 8 | 83 | Card update validation, cross-reference verification |
| 4 | COTRTUPC | 58 | 13 | 71 | DB2 transaction type CRUD operations |
| 5 | COCRDLIC | 61 | 9 | 70 | Card list browse with multi-key search |
| 6 | CBTRN02C | 48 | 0 | 48 | Transaction posting rules, balance validation, reject logic |
| 7 | CBACT04C | 43 | 0 | 43 | Interest calculation rules, rate lookup, multiple file correlation |
| 8 | CBTRN03C | 38 | 2 | 40 | Report formatting, page breaks, totaling |
| 9 | COCRDSLC | 36 | 4 | 40 | Card detail view with cross-file lookups |
| 10 | COPAUS0C | 25 | 11 | 36 | Authorization message list with browse |

---

## 5. Top 10 Programs by Inter-Program Dependencies

Counting how many other programs or JCL jobs reference each program.

| Rank | Program | Inbound References | Referenced By |
|------|---------|-------------------|--------------|
| 1 | COSGN00C | 14 | COADM01C, COMEN01C, COPAUS0C, CORPT00C, COTRN02C, COUSR00-03C, CBADMCDJ (CSD) |
| 2 | COMEN01C | 13 | COPAUS0C, COACTVWC, COCRDLIC, COCRDSLC, COCRDUPC, CORPT00C, COTRN00C, COTRN02C |
| 3 | COADM01C | 8 | COTRTUPC, COTRTLIC, COUSR00-03C, COSGN00C |
| 4 | COCRDLIC | 5 | COACTVWC, COCRDSLC, COCRDUPC, COACTUPC, CSD |
| 5 | COCRDSLC | 5 | COACTVWC, COCRDLIC, COCRDUPC, COACTUPC, CSD |
| 6 | COCRDUPC | 4 | COACTVWC, COCRDLIC, COACTUPC, CSD |
| 7 | COPAUS0C | 3 | COPAUS1C, COMEN01C, CSD |
| 8 | COTRN00C | 3 | COTRN01C, CBADMCDJ, CSD |
| 9 | CSUTLDTC | 2 | CORPT00C, COTRN02C |
| 10 | CBSTM03B | 1 | CBSTM03A |

---

## 6. Composite Hotspot Score

Weighted composite ranking considering all dimensions:

| Rank | Program | LOC | Copy | I/O | Logic | Deps | **Composite** | Classification |
|------|---------|-----|------|-----|-------|------|-----------|---------------|
| **1** | **COACTUPC** | 4,237 | 19 | ~14 | 175 | 2 | **★★★★★** | Online (CICS) |
| **2** | **CBTRN02C** | 732 | 5 | 21 | 48 | 1 | **★★★★☆** | Batch |
| **3** | **COCRDUPC** | 1,561 | 15 | ~8 | 83 | 4 | **★★★★☆** | Online (CICS) |
| **4** | **COTRTLIC** | 2,099 | 11 | 16 | 116 | 2 | **★★★★☆** | Online (CICS/DB2) |
| **5** | **COCRDLIC** | 1,460 | 13 | 12 | 70 | 5 | **★★★☆☆** | Online (CICS) |
| **6** | **CBACT04C** | 653 | 5 | 17 | 43 | 1 | **★★★☆☆** | Batch |
| **7** | **CBSTM03A** | 925 | 4 | 97 | 22 | 1 | **★★★☆☆** | Batch |
| **8** | **COPAUA0C** | 1,027 | 14 | ~6 | 28 | 1 | **★★★☆☆** | Online (CICS/MQ) |
| **9** | **COTRTUPC** | 1,703 | 14 | ~8 | 71 | 2 | **★★★☆☆** | Online (CICS/DB2) |
| **10** | **CBTRN03C** | 650 | 5 | 18 | 40 | 1 | **★★☆☆☆** | Batch |

---

## 7. Modernization Recommendations

### Priority 1: Core Batch Pipeline (Modernize First)

| Program | Rationale | Suggested Approach |
|---------|-----------|-------------------|
| **CBTRN02C** | Heart of daily transaction processing. Updates 6 VSAM files, complex validation. All downstream reports depend on it. | Decompose into microservices: (1) Transaction Validator, (2) Balance Updater, (3) Category Tracker. Replace VSAM with relational DB. |
| **CBACT04C** | Monthly interest calculation — critical financial logic. Multi-file correlation with complex rate lookups. | Extract as standalone calculation service. Replace DISCGRP/TCATBALF VSAM with DB tables. Add unit tests for rate calculations. |
| **CBSTM03A/B** | Statement generation with highest I/O count (97 ops). Paired subroutine pattern. | Convert to templated document generator (PDF/HTML). Replace file I/O with database queries. |

**Why first:** These batch programs contain the core financial business rules (transaction posting, interest calculation, statement generation). They have clearly defined inputs/outputs with no CICS UI coupling, making them the cleanest extraction candidates. Modernizing them first also enables replacing the VSAM-to-VSAM batch pipeline with a database-backed approach.

### Priority 2: Data Access Layer

| Program | Rationale | Suggested Approach |
|---------|-----------|-------------------|
| **CBEXPORT/CBIMPORT** | Branch migration — well-defined ETL pattern with all 5 entities. | Replace with modern ETL framework. This pair already documents the complete data model via CVEXPORT copybook. |
| **CBTRN03C** | Report generation reads 5 reference files. | Replace with reporting service using SQL queries. |
| **CBTRN01C** | Transaction validation (pre-posting). Reads 6 files. | Merge validation logic into the CBTRN02C replacement service. |

**Why second:** These programs primarily perform data transformation and validation. They depend on the same VSAM files that Priority 1 will migrate to a relational database, so they benefit from that migration.

### Priority 3: Online CICS Programs (Largest Complexity)

| Program | Rationale | Suggested Approach |
|---------|-----------|-------------------|
| **COACTUPC** | Largest program (4,237 lines). 39 field-validation patterns via COPY REPLACING. Extreme nesting (IF depth 165). | Decompose into: (1) Account form component, (2) Validation service (replace CSLKPCDY lookup tables), (3) Account update API. |
| **COCRDUPC** | Card update — 1,561 lines, cross-references 4 VSAM files. | Extract card CRUD into a Card Service API. |
| **COCRDLIC** | Card list/search — complex browse logic. | Convert to paginated REST API with search. |
| **COACTVWC** | Account view — 15 copybooks, reads 3 VSAM datasets. | Convert to read-only Account View API. |

**Why third:** CICS programs require replacing the 3270 UI, BMS maps, and COMMAREA-based navigation — the most complex migration surface. However, by this point, the underlying data access will already be modernized.

### Priority 4: Sub-Application Programs

| Sub-App | Programs | Rationale |
|---------|----------|-----------|
| Authorization (IMS/DB2/MQ) | COPAUA0C, COPAUS0C, COPAUS1C, COPAUS2C, CBPAUP0C | Requires IMS DB and MQ — heaviest middleware dependency. Convert to event-driven microservice with a relational DB. |
| Transaction Type (DB2) | COTRTLIC, COTRTUPC, COBTUPDT | Already on DB2 — migrate SQL to modern RDBMS. Simplest DB2 extraction. |
| VSAM-MQ | COACCT01, CODATE01 | MQ-based services — convert to REST APIs or async messaging (SQS/SNS). |

**Why last:** These programs have the most complex middleware dependencies (IMS, MQ, DB2) and are semi-independent sub-applications that can be migrated after the core system.

### Priority 5: Shared Utilities and Navigation

| Program | Rationale |
|---------|-----------|
| COSGN00C, COMEN01C, COADM01C | Navigation shell — replace with web application routing. Highest inbound references (14, 13, 8). |
| CSUTLDTC | Date utility — replace with standard library (java.time, etc.). |
| COBSWAIT | System wait — eliminate in modern async architecture. |

---

## 8. Risk Assessment

### High-Risk Programs (require careful migration)

| Program | Risk Factor |
|---------|------------|
| COACTUPC | Largest program. 39 COPY REPLACING patterns generate ~1,500 lines of attribute-setting code. Field-by-field validation logic is deeply embedded. |
| CBTRN02C | Critical path — transaction posting affects account balances. Must maintain exact financial precision (PIC S9(10)V99). |
| CBACT04C | Interest calculation with multi-table correlation. Rounding and rate-application rules must be preserved exactly. |
| COPAUA0C | Real-time authorization via MQ. Latency-sensitive. Must handle approve/decline within SLA. |

### Low-Risk Programs (straightforward migration)

| Program | Risk Factor |
|---------|------------|
| CBACT01C/02C/03C | Simple read-and-print utilities. Well-contained. |
| CBCUS01C | Single-file reader. Minimal logic. |
| COBSWAIT | 42-line wait wrapper. |
| COUSR01C/02C/03C | Simple CRUD against single VSAM file. |
| CBEXPORT/CBIMPORT | Well-structured ETL with clear record types. |

---

## 9. Key Observations

1. **COACTUPC is the mega-program**: At 4,237 lines with IF nesting depth of 165, it is by far the most complex single program. It uses COPY REPLACING 39 times to generate field-level validation code — a pattern that will need to be replaced with a data-driven validation framework.

2. **The batch pipeline is the backbone**: The daily CLOSEFIL → POSTTRAN → TRANBKP → WAITSTEP → OPENFIL flow processes all daily transactions. This is the single most impactful pipeline to modernize.

3. **VSAM is the central data store**: 9 KSDS datasets with 3+ alternate indexes form the entire persistence layer. Migrating to a relational database unlocks all other modernization.

4. **Copybook reuse is high**: COCOM01Y (COMMAREA) is used by 16+ programs. CVACT01Y (Account Record) is used by 11+ programs. These copybooks essentially define the system's data model and API contracts.

5. **MQ and IMS are isolated**: The authorization and account-inquiry MQ programs are contained in sub-applications with clean boundaries, making them independent migration candidates.

6. **No test suite exists**: There are no automated tests. Modernization should include building comprehensive test coverage from the existing data files in `app/data/` before modifying any logic.
