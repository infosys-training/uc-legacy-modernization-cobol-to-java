# Hotspot Report — CardDemo COBOL Estate

This report ranks the top 10 programs by complexity metrics and recommends a modernization priority order.

---

## 1. Ranking by Lines of Code

| Rank | Program | LOC | Classification | Purpose |
|------|---------|-----|----------------|---------|
| 1 | COACTUPC.cbl | 4,236 | CICS Online | Account Update |
| 2 | COTRTLIC.cbl | 2,098 | CICS/Db2 | Transaction Type List |
| 3 | COTRTUPC.cbl | 1,702 | CICS/Db2 | Transaction Type Update |
| 4 | COCRDUPC.cbl | 1,560 | CICS Online | Credit Card Update |
| 5 | COCRDLIC.cbl | 1,459 | CICS Online | Credit Card List |
| 6 | COPAUS0C.cbl | 1,032 | CICS/IMS | Auth Summary View |
| 7 | COPAUA0C.cbl | 1,026 | CICS/IMS/MQ | Auth Decision Engine |
| 8 | COACTVWC.cbl | 941 | CICS Online | Account View |
| 9 | CBSTM03A.CBL | 924 | Batch | Statement Generation |
| 10 | COCRDSLC.cbl | 887 | CICS Online | Credit Card View |

**Total LOC across all 44 programs: ~29,174**

---

## 2. Ranking by Number of Copybooks Referenced

| Rank | Program | Copybook Count | Key Copybooks |
|------|---------|---------------|---------------|
| 1 | COACTUPC.cbl | 18 | COCOM01Y, CVACT01Y, CVACT03Y, CVCRD01Y, CVCUS01Y, CSSETATY, CSUTLDPY, CSUTLDWY, CSLKPCDY + 9 more |
| 2 | COPAUA0C.cbl | 15 | CCPAURQY, CCPAURLY, CIPAUSMY, CIPAUDTY, CMQV, CVACT01Y, CVACT03Y, CVCUS01Y + 7 MQ copybooks |
| 3 | COACTVWC.cbl | 15 | CVACT01Y, CVACT02Y, CVACT03Y, CVCRD01Y, CVCUS01Y, CSSTRPFY + 9 more |
| 4 | COCRDUPC.cbl | 13 | CVACT02Y, CVCRD01Y, CVCUS01Y, CSSTRPFY + 9 more |
| 5 | COPAUS0C.cbl | 13 | CIPAUSMY, CIPAUDTY, CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y + 7 more |
| 6 | COCRDSLC.cbl | 13 | CVACT02Y, CVCRD01Y, CVCUS01Y, CSSTRPFY + 9 more |
| 7 | COTRTUPC.cbl | 13 | CVCRD01Y, CSSETATY, CSSTRPFY, CSUTLDWY + 9 more |
| 8 | COCRDLIC.cbl | 11 | CVACT02Y, CVCRD01Y, CSSTRPFY + 8 more |
| 9 | COPAUS1C.cbl | 10 | CIPAUSMY, CIPAUDTY, COCOM01Y + 7 more |
| 10 | COTRN02C.cbl | 11 | CVACT01Y, CVACT03Y, CVTRA05Y + 8 more |

---

## 3. Ranking by Number of I/O Operations

I/O operations include: READ, WRITE, REWRITE, DELETE, OPEN, CLOSE, STARTBR, READNEXT, READPREV, ENDBR, EXEC SQL, EXEC DLI, MQOPEN, MQGET, MQPUT.

| Rank | Program | I/O Count | Key Operations |
|------|---------|-----------|----------------|
| 1 | CBSTM03A.CBL | 158 | Multiple file OPEN/CLOSE/READ/WRITE for statement generation |
| 2 | COTRTLIC.cbl | 142 | Db2 SELECT, DELETE; CICS SEND/RECEIVE MAP; cursor-based pagination |
| 3 | COACTUPC.cbl | 113 | CICS READ/REWRITE on 4 VSAM files; SEND/RECEIVE MAP |
| 4 | CBTRN03C.cbl | 81 | READ 5 files (trans, xref, trantype, trancatg, dateparm); WRITE report |
| 5 | COTRTUPC.cbl | 77 | Db2 INSERT/UPDATE/SELECT; CICS SEND/RECEIVE MAP |
| 6 | CBTRN01C.cbl | 61 | READ 4 files; I-O on ACCT + TRAN master |
| 7 | CBTRN02C.cbl | 61 | READ daily trans; I-O on ACCT, TRAN, TCATBAL; WRITE rejects |
| 8 | CBEXPORT.cbl | 53 | READ 5 VSAM files; WRITE export sequential |
| 9 | CBACT04C.cbl | 52 | READ 4 files; I-O on ACCTFILE |
| 10 | COPAUA0C.cbl | 51 | MQOPEN/GET/PUT1/CLOSE; CICS READ; DLI GU/REPL/ISRT |

---

## 4. Ranking by Business Logic Density (EVALUATE + IF Nesting)

This metric combines EVALUATE statement count and IF statement count as proxies for decision complexity and branching depth.

| Rank | Program | EVALUATE | IF | Total Decision Points | Decisions/100 LOC |
|------|---------|----------|----|-----------------------|-------------------|
| 1 | COACTUPC.cbl | 20 | 174 | 194 | 4.58 |
| 2 | COCRDUPC.cbl | 16 | 151 | 167 | 10.71 |
| 3 | COCRDLIC.cbl | 18 | 131 | 149 | 10.21 |
| 4 | CBTRN02C.cbl | 0 | 93 | 93 | 12.72 |
| 5 | CBACT04C.cbl | 0 | 86 | 86 | 13.19 |
| 6 | CBTRN03C.cbl | 4 | 75 | 79 | 12.17 |
| 7 | COCRDSLC.cbl | 8 | 72 | 80 | 9.02 |
| 8 | COACTVWC.cbl | 10 | 60 | 70 | 7.44 |
| 9 | COPAUA0C.cbl | 10 | 52 | 62 | 6.04 |
| 10 | COTRTLIC.cbl | 32 | 0* | 32 | 1.53 |

*Note: COTRTLIC uses 32 EVALUATE WHEN clauses instead of IF statements for its Db2 SQLCODE error handling, resulting in a misleadingly low IF count. The actual decision complexity is high.*

---

## 5. Ranking by Inter-Program Dependencies

Dependencies include: CALL/LINK/XCTL targets, programs that call this program, JCL invocations, and shared data files with other programs.

| Rank | Program | Shared Files | Called By | Calls Out | COMMAREA Users | Total Coupling |
|------|---------|-------------|-----------|-----------|----------------|---------------|
| 1 | COACTUPC.cbl | 4 (ACCTDAT, CARDDAT, CUSTDAT, CARDAIX) | 2 (menus) | 0 | Yes (COMMAREA) | High — touches most master files |
| 2 | COPAUA0C.cbl | 3 (CARDXREF, ACCTDAT, CUSTDAT) | 0 | 4 (MQ + DLI) | Yes (DLI PSB) | High — MQ, IMS, CICS, VSAM |
| 3 | CBTRN02C.cbl | 5 (DALYTRAN, XREFFILE, ACCTFILE, TRANFILE, TCATBALF, DALYREJS) | 1 (JCL) | 0 | N/A | High — core batch posting |
| 4 | CBACT04C.cbl | 5 (TCATBALF, XREFFILE, DISCGRP, ACCTFILE, TRANSACT) | 1 (JCL) | 0 | N/A | High — reads most reference files |
| 5 | CBEXPORT.cbl | 6 (CUSTFILE, ACCTFILE, XREFFILE, TRANSACT, CARDFILE, EXPFILE) | 1 (JCL) | 0 | N/A | High — reads all master files |
| 6 | CBTRN01C.cbl | 6 (DALYTRAN, CUSTFILE, XREFFILE, CARDFILE, ACCTFILE, TRANFILE) | 0 | 0 | N/A | Medium — original posting logic |
| 7 | CBSTM03A.CBL | 1 | 0 | 1 (CBSTM03B) | N/A | Medium — driver-subroutine pair |
| 8 | CBTRN03C.cbl | 5 (TRANFILE, CARDXREF, TRANTYPE, TRANCATG, DATEPARM, TRANREPT) | 1 (JCL) | 0 | N/A | Medium — reporting |
| 9 | COTRN02C.cbl | 3 (ACCTDAT, CARDXREF, TRANSACT) | 1 (menu) | 1 (CSUTLDTC) | Yes | Medium |
| 10 | COBIL00C.cbl | 2 (ACCTDAT, TRANSACT) | 1 (menu) | 0 | Yes | Medium |

---

## 6. Composite Hotspot Score

Combining all five metrics with weighted scoring (LOC: 20%, Copybooks: 15%, I/O: 20%, Logic Density: 25%, Dependencies: 20%):

| Rank | Program | LOC | Copy | I/O | Logic | Deps | Composite Score | Classification |
|------|---------|-----|------|-----|-------|------|----------------|----------------|
| **1** | **COACTUPC.cbl** | 4,236 | 18 | 113 | 194 | High | **97/100** | CICS Online |
| **2** | **CBTRN02C.cbl** | 731 | 6 | 61 | 93 | High | **72/100** | Batch |
| **3** | **COPAUA0C.cbl** | 1,026 | 15 | 51 | 62 | High | **70/100** | CICS/IMS/MQ |
| **4** | **COTRTLIC.cbl** | 2,098 | 12 | 142 | 32 | Medium | **68/100** | CICS/Db2 |
| **5** | **COCRDUPC.cbl** | 1,560 | 13 | 23 | 167 | Medium | **66/100** | CICS Online |
| **6** | **CBACT04C.cbl** | 652 | 6 | 52 | 86 | High | **65/100** | Batch |
| **7** | **COCRDLIC.cbl** | 1,459 | 11 | 44 | 149 | Medium | **64/100** | CICS Online |
| **8** | **CBSTM03A.CBL** | 924 | 5 | 158 | 24 | Medium | **60/100** | Batch |
| **9** | **COTRTUPC.cbl** | 1,702 | 13 | 77 | 26 | Medium | **58/100** | CICS/Db2 |
| **10** | **CBTRN03C.cbl** | 649 | 6 | 81 | 79 | Medium | **56/100** | Batch |

---

## 7. Modernization Recommendations

### Priority 1 — Modernize First (High Impact, Batch Core)

| Program | Rationale |
|---------|-----------|
| **CBTRN02C** (Post Daily Transactions) | Core batch engine — runs daily, touches 6 files, validates all transactions, updates account balances and category totals. Business-critical and self-contained. Good first candidate because it is batch-only (no CICS dependencies), has clear inputs/outputs, and its correctness is verifiable by comparing account balances. **Risk: High** — any regression breaks daily processing. **Approach:** Rewrite as a Java Spring Batch job with VSAM-to-JDBC adapters. |
| **CBACT04C** (Interest Calculator) | Core financial logic — computes interest using disclosure group rates against category balances. Pure batch, well-defined algorithm, reads from reference files. **Approach:** Extract interest calculation as a pure Java function; wrap in Spring Batch ItemProcessor. |
| **CBTRN03C** (Transaction Report) | Reporting — reads 5 files, produces formatted report. No file mutations. Low risk for regression. **Approach:** Replace with Java report generation (JasperReports or similar). |

### Priority 2 — Modernize Second (High Complexity, Online)

| Program | Rationale |
|---------|-----------|
| **COACTUPC** (Account Update) | Largest and most complex program (4,236 LOC, 194 decision points). Touches 4 VSAM files. However, it is tightly coupled to CICS BMS maps and requires screen-by-screen reimplementation. **Approach:** Decompose into a REST API backend + React/Angular frontend. Split into AccountService, CardService, CustomerService. |
| **COCRDUPC** (Credit Card Update) | Second most complex CICS program by logic density (10.71 decisions/100 LOC). Similar structure to COACTUPC but scoped to card records. **Approach:** Modernize alongside COACTUPC since they share copybooks and VSAM files. |
| **COCRDLIC** (Credit Card List) | High logic density (10.21/100 LOC) due to browse/pagination logic. **Approach:** Replace CICS browse with paginated REST API endpoint backed by a database. |

### Priority 3 — Modernize Third (Integration Layer)

| Program | Rationale |
|---------|-----------|
| **COPAUA0C** (Authorization Decision) | Most architecturally complex — spans CICS, IMS DL/I, MQ, and VSAM. Highest technology coupling (4 subsystems). Should be modernized only after the VSAM and Db2 layers are migrated. **Approach:** Rewrite as a microservice consuming from message queue (Kafka/SQS), querying relational DB, publishing authorization decisions. |
| **COTRTLIC** + **COTRTUPC** (Transaction Type Db2 CRUD) | Already using Db2 — closest to a modern architecture. However, tightly coupled to CICS BMS. **Approach:** Extract Db2 access as JPA/Hibernate entities; build REST CRUD API. |

### Priority 4 — Modernize Last (Supporting Programs)

| Program | Rationale |
|---------|-----------|
| **CBSTM03A/B** (Statements) | High I/O count but straightforward driver-subroutine pattern. Low business logic. **Approach:** Template-based report generation. |
| **CBEXPORT/CBIMPORT** | Data migration utilities — may be replaced entirely by ETL tools (AWS Glue, Informatica). |
| **COUSR00C-03C** (User CRUD) | Simple VSAM CRUD operations. Can be replaced by standard IAM/authentication service. |

### Modernization Dependency Order

```
Phase 1 (Batch Core):     CBTRN02C → CBACT04C → CBTRN03C → CBSTM03A/B
                               │
Phase 2 (Online Core):    COACTUPC → COCRDUPC → COCRDLIC → COACTVWC
                               │
Phase 3 (Integration):    COPAUA0C → COTRTLIC/COTRTUPC → COACCT01/CODATE01
                               │
Phase 4 (Remaining):      COUSR00C-03C → CBEXPORT/CBIMPORT → remaining batch readers
```

### Key Risks

1. **Shared VSAM files** — Batch and online programs share the same VSAM clusters. Modernizing one tier without the other requires a compatibility layer (e.g., VSAM-to-JDBC bridge during transition).
2. **COMMAREA contracts** — CICS programs pass data through the COMMAREA (COCOM01Y). Any change to field layouts affects all 21 programs that reference it.
3. **Close-Process-Open pattern** — The daily batch cycle requires CICS files to be closed before batch runs. Modernizing to a relational database eliminates this constraint but requires careful cut-over planning.
4. **IMS DL/I and MQ** — The authorization module uses IMS hierarchical database access and MQ messaging. These require specialized migration tooling (e.g., AWS M2 Replatform or complete rewrite).
5. **Date handling** — Multiple date formats and validation routines (CODATECN, CSUTLDPY/CSUTLDWY) are shared across programs. Centralizing date handling in a utility library early will reduce risk across all phases.
