# HOTSPOT REPORT — CardDemo COBOL Estate

> Complexity ranking and modernization priority analysis for 44 COBOL programs.

---

## 1. Top 10 Programs by Lines of Code

| Rank | Program | LOC | Classification | Module |
|------|---------|-----|----------------|--------|
| 1 | COACTUPC.cbl | 4,236 | Online (CICS) | Core |
| 2 | COTRTLIC.cbl | 2,098 | Online (CICS/DB2) | Tran-Type-DB2 |
| 3 | COTRTUPC.cbl | 1,702 | Online (CICS/DB2) | Tran-Type-DB2 |
| 4 | COCRDUPC.cbl | 1,560 | Online (CICS) | Core |
| 5 | COCRDLIC.cbl | 1,459 | Online (CICS) | Core |
| 6 | COPAUS0C.cbl | 1,032 | Online (CICS/IMS) | Auth-IMS |
| 7 | COPAUA0C.cbl | 1,026 | Online (CICS/MQ) | Auth-IMS |
| 8 | COACTVWC.cbl | 941 | Online (CICS) | Core |
| 9 | CBSTM03A.CBL | 924 | Batch | Core |
| 10 | COCRDSLC.cbl | 887 | Online (CICS) | Core |

**Total LOC across all 44 programs: 30,175**

---

## 2. Top 10 Programs by Copybook References

| Rank | Program | Copybooks | Key Copybooks |
|------|---------|-----------|---------------|
| 1 | COACTUPC.cbl | 56 | COCOM01Y, CSLKPCDY, CSUTLDPY, CVACT01Y, CVCRD01Y + 10 BMS/system |
| 2 | COPAUA0C.cbl | 16 | CCPAURQY, CCPAURLY, CMQV*, CVACT01Y, CVCUS01Y |
| 3 | COACTVWC.cbl | 15 | COCOM01Y, CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y |
| 4 | COTRTUPC.cbl | 14 | COCOM01Y, CVCRD01Y, CSSETATY, CSMSG02Y |
| 5 | COPAUS0C.cbl | 14 | CIPAUDTY, CIPAUSMY, CVACT01Y, CVACT02Y, CVACT03Y |
| 6 | COCRDSLC.cbl | 13 | COCOM01Y, CVACT02Y, CVCRD01Y, CVCUS01Y |
| 7 | COCRDUPC.cbl | 13 | COCOM01Y, CVACT02Y, CVCRD01Y, CVCUS01Y |
| 8 | COTRTLIC.cbl | 11 | COCOM01Y, CVACT02Y, CVCRD01Y |
| 9 | COCRDLIC.cbl | 11 | COCOM01Y, CVACT02Y, CVCRD01Y |
| 10 | COBIL00C.cbl | 10 | COCOM01Y, CVACT01Y, CVACT03Y, CVTRA05Y |

Note: counts include BMS map copybooks (DFHAID, DFHBMSCA) and system includes.

---

## 3. Top 10 Programs by I/O Operations

I/O operations include: READ, WRITE, REWRITE, DELETE, OPEN, CLOSE, EXEC CICS READ/WRITE/STARTBR/READNEXT/READPREV/ENDBR.

| Rank | Program | I/O Ops | I/O Details |
|------|---------|---------|-------------|
| 1 | CBSTM03A.CBL | 117 | Massive file processing — reads transactions, cross-refs, accounts, customers; writes statement + HTML |
| 2 | CBTRN02C.cbl | 23 | Daily transaction posting: reads transactions + cross-refs, writes rejects + category balance |
| 3 | CBEXPORT.cbl | 22 | Reads 5 master files, writes 1 export file |
| 4 | CBIMPORT.cbl | 22 | Reads 1 export file, writes 6 output files (5 masters + errors) |
| 5 | CBTRN03C.cbl | 21 | Reads 4 files (transactions, cross-refs, types, categories), writes report |
| 6 | COTRTUPC.cbl | 21 | DB2 INSERT/UPDATE/DELETE/SELECT operations on transaction type tables |
| 7 | COACCT01.cbl | 20 | MQ GET/PUT + VSAM account reads |
| 8 | CBACT04C.cbl | 19 | Interest calculation: reads 4 files, rewrites accounts, writes transactions |
| 9 | CODATE01.cbl | 18 | MQ GET/PUT date service |
| 10 | CBTRN01C.cbl | 17 | Transaction posting: reads 5 files, writes 1 |

---

## 4. Top 10 Programs by Business Logic Density

Business logic density = EVALUATE statements + IF statements (branching complexity).

| Rank | Program | EVALUATE | IF | Total | Nesting Density (per 100 LOC) |
|------|---------|----------|----|-------|------------------------------|
| 1 | COACTUPC.cbl | 10 | 164 | 174 | 4.11 |
| 2 | COCRDUPC.cbl | 16 | 72 | 88 | 5.64 |
| 3 | COCRDLIC.cbl | 18 | 59 | 77 | 5.28 |
| 4 | CBTRN02C.cbl | 0 | 48 | 48 | 6.57 |
| 5 | CBACT04C.cbl | 0 | 43 | 43 | 6.60 |
| 6 | CBTRN03C.cbl | 4 | 38 | 42 | 6.47 |
| 7 | COACTVWC.cbl | 10 | 28 | 38 | 4.04 |
| 8 | CBTRN01C.cbl | 0 | 33 | 33 | 6.68 |
| 9 | COCRDSLC.cbl | 8 | 33 | 41 | 4.62 |
| 10 | COPAUS0C.cbl | 11 | 25 | 36 | 3.49 |

---

## 5. Top 10 Programs by Inter-Program Dependencies

Dependencies = XCTL/LINK targets + CALL targets + programs that call/transfer to this program.

| Rank | Program | Dep. Count | Role |
|------|---------|------------|------|
| 1 | COACTUPC.cbl | 8 | Called from menu (COMEN01C); XCTLs to any program via COMMAREA; reads 3 VSAM files; calls CSUTLDTC |
| 2 | CBSTM03A.CBL | 6 | Called from CREASTMT JCL; calls CBSTM03B; reads 4 VSAM files |
| 3 | COACTVWC.cbl | 6 | Called from menu; reads 4 VSAM files; XCTLs to dynamic target |
| 4 | COSGN00C.cbl | 5 | Entry point; XCTLs to COADM01C or COMEN01C; reads USRSEC |
| 5 | COCRDLIC.cbl | 5 | Called from menu; XCTLs to COMEN01C, COCRDSLC, COCRDUPC |
| 6 | COPAUA0C.cbl | 5 | MQ message handler; calls MQOPEN/MQGET/MQPUT1/MQCLOSE; reads 3 VSAM |
| 7 | CBEXPORT.cbl | 5 | Called from CBEXPORT JCL; reads 5 master VSAM files |
| 8 | CBIMPORT.cbl | 5 | Called from CBIMPORT JCL; writes 6 output files |
| 9 | CBTRN02C.cbl | 5 | Called from POSTTRAN JCL; reads 4 files, writes 2 |
| 10 | CBACT04C.cbl | 5 | Called from INTCALC JCL; reads 4 files, rewrites 1 |

---

## 6. Composite Complexity Score

Weighted composite score combining all 5 dimensions:

| Weight | Dimension | Rationale |
|--------|-----------|-----------|
| 25% | Lines of Code | Size directly impacts modernization effort |
| 20% | Copybook References | Data coupling complexity |
| 20% | I/O Operations | Integration points that must be re-implemented |
| 20% | Business Logic Density | Branching/decision complexity to unit-test |
| 15% | Inter-Program Dependencies | Coordination and interface complexity |

### Composite Ranking

| Rank | Program | LOC Score | Copy Score | I/O Score | Logic Score | Dep Score | **Composite** |
|------|---------|-----------|-----------|-----------|-------------|-----------|---------------|
| 1 | **COACTUPC.cbl** | 100.0 | 100.0 | 6.0 | 100.0 | 100.0 | **82.4** |
| 2 | **CBSTM03A.CBL** | 21.8 | 7.1 | 100.0 | 11.5 | 75.0 | **41.7** |
| 3 | **COCRDUPC.cbl** | 36.8 | 23.2 | 2.6 | 50.6 | 37.5 | **30.9** |
| 4 | **COCRDLIC.cbl** | 34.4 | 19.6 | 8.5 | 44.3 | 62.5 | **33.2** |
| 5 | **COTRTLIC.cbl** | 49.5 | 19.6 | 10.3 | 9.2 | 37.5 | **26.3** |
| 6 | **CBTRN02C.cbl** | 17.2 | 8.9 | 19.7 | 27.6 | 62.5 | **25.9** |
| 7 | **COPAUA0C.cbl** | 24.2 | 28.6 | 12.8 | 20.7 | 62.5 | **29.3** |
| 8 | **COACTVWC.cbl** | 22.2 | 26.8 | 2.6 | 21.8 | 75.0 | **28.8** |
| 9 | **COTRTUPC.cbl** | 40.2 | 25.0 | 17.9 | 14.9 | 37.5 | **28.0** |
| 10 | **CBACT04C.cbl** | 15.4 | 8.9 | 16.2 | 24.7 | 62.5 | **24.2** |

---

## 7. Modernization Priority Recommendations

### Tier 1 — Modernize First (Highest Impact)

#### 1. COACTUPC.cbl — Account Update (Priority: CRITICAL)

- **Why first:** Highest composite score (82.4). At 4,236 LOC, it is the single largest program — nearly 14% of the entire codebase. It touches 56 copybooks and has 174 branching statements (10 EVALUATEs + 164 IFs), making it the most complex program to maintain and the highest-risk for defects.
- **Approach:** Decompose into smaller services: account validation, account update, date validation, address validation. The embedded date validation (CSUTLDPY) and lookup code validation (CSLKPCDY) are natural extraction candidates.
- **Dependencies:** Reads ACCTDATA, CARDDATA, CUSTDATA VSAM files via CICS. Must ensure VSAM-to-database migration is complete before modernizing.

#### 2. CBSTM03A.CBL — Account Statements (Priority: HIGH)

- **Why:** Second-highest composite score (41.7). Has 117 I/O operations — the most in the entire estate. It is a batch pipeline anchor (called by CREASTMT.JCL) that reads 4 VSAM files and produces both text and HTML output. It also CALLs subroutine CBSTM03B, so these two programs must be modernized together.
- **Approach:** Convert to a reporting microservice. The HTML generation is a natural fit for a modern templating engine. The SORT pre-processing step (CREASTMT.JCL STEP010) can be replaced with SQL ORDER BY.

#### 3. COCRDLIC.cbl + COCRDUPC.cbl — Credit Card List & Update (Priority: HIGH)

- **Why:** Together these represent 3,019 LOC with high branching complexity (77 + 88 = 165 logic branches). COCRDLIC has 18 CICS operations (the most of any program) due to VSAM browse (STARTBR/READNEXT/READPREV/ENDBR) which is particularly complex to re-implement. COCRDUPC has 16 EVALUATE statements for screen field validation.
- **Approach:** Replace VSAM browse with paginated database queries. Consolidate the list→view→update flow into a single CRUD service with REST endpoints.

### Tier 2 — Modernize Second (Medium Impact)

#### 4. COTRTLIC.cbl + COTRTUPC.cbl — Transaction Type Maintenance (Priority: MEDIUM-HIGH)

- **Why:** Already using DB2 (not VSAM), which simplifies data layer migration. At 3,800 combined LOC with CICS BMS screen processing, these are good candidates for early wins since the data access layer (SQL) is already close to modern patterns.
- **Approach:** Direct migration from COBOL SQL to Java/Spring JDBC or JPA. The BMS screens map naturally to HTML forms.

#### 5. CBTRN02C.cbl + CBTRN03C.cbl — Transaction Posting & Reporting (Priority: MEDIUM)

- **Why:** Core batch pipeline programs with 1,380 combined LOC. CBTRN02C is the daily posting engine that updates category balances and generates reject files. CBTRN03C generates transaction reports. Both are called from JCL jobs and use multiple VSAM files.
- **Approach:** Convert to Spring Batch jobs with database reads/writes.

#### 6. COPAUA0C.cbl + COPAUS0C.cbl — Authorization (Priority: MEDIUM)

- **Why:** These programs span all three middleware layers (CICS + IMS + MQ), making them the most architecturally complex. COPAUA0C uses MQ for message-driven authorization decisions. COPAUS0C uses IMS DL/I calls for browsing pending authorizations.
- **Approach:** Replace MQ integration with modern messaging (Kafka/SQS). Replace IMS DB access with relational DB. Most complex technically but can be deferred since auth is a separable subsystem.

### Tier 3 — Modernize Last (Lower Priority)

#### 7. COACTVWC.cbl + COCRDSLC.cbl — View Screens (Priority: LOW)

- **Why:** Read-only CICS screens. Lower risk since they don't modify data. 1,828 combined LOC but simpler business logic.
- **Approach:** Can be auto-converted to REST GET endpoints with minimal manual intervention.

#### 8. Batch Utilities — CBACT01C, CBACT02C, CBACT03C, CBCUS01C (Priority: LOW)

- **Why:** Simple file readers (178–430 LOC each). Used for diagnostic dumps and data verification. May become unnecessary after migration to a database with query tools.
- **Approach:** Replace with SQL queries or monitoring dashboards.

#### 9. CBEXPORT.cbl + CBIMPORT.cbl — Branch Migration (Priority: LOW)

- **Why:** One-time or infrequent migration utility. 1,069 combined LOC. May not be needed post-modernization if data is centralized.
- **Approach:** Replace with ETL tools or database replication.

---

## 8. Risk Factors for Modernization

| Risk | Impact | Mitigation |
|------|--------|------------|
| **VSAM Close-Process-Open pattern** | Batch jobs require exclusive file access; CICS files must be closed/reopened | Replace with database transactions; eliminates contention |
| **Dynamic XCTL via COMMAREA** | Program routing is data-driven (CDEMO-TO-PROGRAM field) | Map to URL routing or service registry in modern architecture |
| **EVALUATE/IF nesting in COACTUPC** | 174 branching paths create a combinatorial testing challenge | Decompose first; add comprehensive unit tests before migrating |
| **BMS screen state management** | CICS pseudo-conversational pattern ties screen state to program logic | Replace with stateless REST + client-side state management |
| **IMS DL/I calls** | Hierarchical database access in auth subsystem (CBLTDLI) | Migrate IMS DB to relational DB first, then convert programs |
| **MQ integration** | COPAUA0C uses MQGET/MQPUT for real-time auth | Replace with cloud messaging (SQS, Kafka) with same message contracts |
| **GDG (Generation Data Groups)** | Versioned file management for reports and backups | Replace with timestamped files in object storage |

---

*Generated: 2026-05-27 | Source: `uc-legacy-modernization-cobol-to-java`*
