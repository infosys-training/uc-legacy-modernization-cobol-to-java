# Hotspot Report — CardDemo COBOL Estate

## Overview

This report ranks programs by complexity metrics to identify modernization hotspots — the programs that will benefit most from early migration and carry the highest risk if left unchanged. Rankings are based on five dimensions: lines of code, copybook count, I/O operation density, business logic density (IF/EVALUATE statements), and inter-program dependencies.

---

## 1. Top 10 Programs by Lines of Code

| Rank | Program | LOC | Classification | Sub-App |
|------|---------|-----|----------------|---------|
| 1 | COACTUPC.cbl | 4,236 | Online (CICS) | Main |
| 2 | COTRTLIC.cbl | 2,098 | Online (CICS/Db2) | Tran Type |
| 3 | COTRTUPC.cbl | 1,702 | Online (CICS/Db2) | Tran Type |
| 4 | COCRDUPC.cbl | 1,560 | Online (CICS) | Main |
| 5 | COCRDLIC.cbl | 1,459 | Online (CICS) | Main |
| 6 | COPAUS0C.cbl | 1,032 | Online (CICS/IMS) | Auth |
| 7 | COPAUA0C.cbl | 1,026 | Online (CICS/MQ/IMS) | Auth |
| 8 | COACTVWC.cbl | 941 | Online (CICS) | Main |
| 9 | CBSTM03A.CBL | 924 | Batch | Main |
| 10 | COCRDSLC.cbl | 887 | Online (CICS) | Main |

---

## 2. Top 10 Programs by Copybook References

| Rank | Program | Copybook Count | Key Copybooks |
|------|---------|----------------|---------------|
| 1 | COACTUPC.cbl | 58 | CSSETATY (×30+), CSLKPCDY, CSUTLDWY, CSUTLDPY, CVCRD01Y |
| 2 | COPAUA0C.cbl | 17 | CMQODV, CMQMDV, CMQV, CCPAURQY, CCPAURLY, CIPAUDTY |
| 3 | COACTVWC.cbl | 16 | CVCRD01Y, CVACT01Y/02Y/03Y, CVCUS01Y, CSSTRPFY |
| 4 | COCRDSLC.cbl | 16 | CVCRD01Y, CVACT01Y/02Y/03Y, CVCUS01Y |
| 5 | COCRDUPC.cbl | 16 | CVCRD01Y, CVACT01Y/02Y/03Y, CVCUS01Y |
| 6 | COPAUS0C.cbl | 15 | CIPAUSMY, CIPAUDTY, CVACT01Y/02Y/03Y |
| 7 | COTRTUPC.cbl | 15 | DCLTRTYP, DCLTRCAT (Db2 INCLUDE) |
| 8 | COCRDLIC.cbl | 14 | CVCRD01Y, CVACT02Y, CSSTRPFY |
| 9 | COTRTLIC.cbl | 12 | CSDB2RWY, SQLCA, DCLTRTYP |
| 10 | COTRN02C.cbl | 11 | CVTRA05Y, CVACT01Y, CVACT03Y |

---

## 3. Top 10 Programs by I/O Operations

Counts include: READ, WRITE, REWRITE, OPEN, CLOSE, STARTBR, READNEXT, READPREV, ENDBR, DELETE, EXEC CICS, EXEC SQL, MQOPEN/MQGET/MQPUT/MQCLOSE, CBLTDLI.

| Rank | Program | I/O Count | Dominant I/O Type |
|------|---------|-----------|-------------------|
| 1 | CBSTM03A.CBL | 147 | File OPEN/READ/WRITE (×13 CALL CBSTM03B) |
| 2 | COTRTLIC.cbl | 134 | EXEC CICS + EXEC SQL (cursor fetch loop) |
| 3 | COACTUPC.cbl | 122 | EXEC CICS SEND/RECEIVE MAP, READ/REWRITE |
| 4 | COTRTUPC.cbl | 88 | EXEC CICS + EXEC SQL |
| 5 | CBTRN03C.cbl | 73 | File READ (5 files) + WRITE report |
| 6 | COCRDLIC.cbl | 54 | EXEC CICS STARTBR/READNEXT/ENDBR |
| 7 | CBTRN02C.cbl | 53 | File READ/WRITE/REWRITE (6 files) |
| 8 | CBTRN01C.cbl | 53 | File READ (6 files) |
| 9 | COACCT01.cbl | 50 | MQOPEN/MQGET/MQPUT/MQCLOSE + EXEC CICS READ |
| 10 | COPAUA0C.cbl | 49 | MQGET/MQPUT1 + EXEC CICS READ (3 files) |

---

## 4. Top 10 Programs by Business Logic Density (IF/EVALUATE Count)

| Rank | Program | IF/EVALUATE Count | Nesting Context |
|------|---------|-------------------|-----------------|
| 1 | COACTUPC.cbl | 174 | Deep nesting in field validation — validates every account update field with CSSETATY template |
| 2 | COCRDUPC.cbl | 80 | Card update field validation — card number, expiry, name, status |
| 3 | COCRDLIC.cbl | 68 | Browse navigation — forward/backward paging, filter logic, boundary checks |
| 4 | CBTRN02C.cbl | 48 | Transaction posting — validates card/account status, amount limits, category rules |
| 5 | CBACT04C.cbl | 43 | Interest calculation — rate lookup, category matching, balance thresholds |
| 6 | CBTRN03C.cbl | 40 | Report formatting — page breaks, account breaks, subtotals, header detection |
| 7 | COCRDSLC.cbl | 37 | Card detail display — field formatting, status interpretation |
| 8 | COPAUS0C.cbl | 36 | IMS browse — segment filtering, pagination, error handling |
| 9 | COTRN00C.cbl | 34 | Transaction list — date range filter, pagination |
| 10 | COACTVWC.cbl | 33 | Account view — field formatting, multi-file lookup coordination |

---

## 5. Top 10 Programs by Inter-Program Dependencies

Dependencies counted: outbound CALLs + programs that CALL/XCTL to this program.

| Rank | Program | Outbound CALLs | Inbound (called by) | Total Dependencies |
|------|---------|----------------|---------------------|-------------------|
| 1 | COMEN01C.cbl | 0 (XCTL: 11 programs) | COSGN00C, all return XCTLs | 15+ |
| 2 | COACTUPC.cbl | 0 | COMEN01C (XCTL) | 12 (widest VSAM footprint) |
| 3 | CSUTLDTC.cbl | 1 (CEEDAYS) | CORPT00C, COTRN02C | 4 |
| 4 | CBSTM03A.CBL | 2 (CBSTM03B ×13, CEE3ABD) | CREASTMT.JCL | 4 |
| 5 | CBSTM03B.CBL | 0 | CBSTM03A (×13) | 2 |
| 6 | CBTRN02C.cbl | 1 (CEE3ABD) | POSTTRAN.jcl | 3 |
| 7 | CBTRN03C.cbl | 1 (CEE3ABD) | TRANREPT.jcl | 3 |
| 8 | CBACT04C.cbl | 1 (CEE3ABD) | INTCALC.jcl | 3 |
| 9 | COPAUA0C.cbl | 4 (MQ calls) | CICS transaction | 5 |
| 10 | COACCT01.cbl | 4 (MQ calls) | CICS transaction | 5 |

---

## 6. Composite Hotspot Score

Programs are scored across all five dimensions on a 1–10 scale per dimension (10 = highest complexity). The composite score is the sum.

| Rank | Program | LOC Score | Copybook Score | I/O Score | Logic Score | Dependency Score | **Composite** |
|------|---------|-----------|----------------|-----------|-------------|-----------------|---------------|
| **1** | **COACTUPC.cbl** | 10 | 10 | 9 | 10 | 9 | **48** |
| **2** | **COTRTLIC.cbl** | 8 | 6 | 10 | 3 | 4 | **31** |
| **3** | **COCRDUPC.cbl** | 7 | 8 | 4 | 9 | 5 | **33** |
| **4** | **COCRDLIC.cbl** | 7 | 7 | 6 | 8 | 5 | **33** |
| **5** | **COTRTUPC.cbl** | 8 | 7 | 8 | 3 | 4 | **30** |
| **6** | **CBSTM03A.CBL** | 5 | 3 | 10 | 3 | 7 | **28** |
| **7** | **COPAUA0C.cbl** | 5 | 8 | 5 | 5 | 6 | **29** |
| **8** | **CBTRN02C.cbl** | 4 | 3 | 6 | 7 | 5 | **25** |
| **9** | **CBACT04C.cbl** | 4 | 3 | 5 | 6 | 5 | **23** |
| **10** | **COACTVWC.cbl** | 5 | 8 | 4 | 5 | 4 | **26** |

---

## 7. Modernization Recommendations

### 7.1 Recommended Modernization Order

#### Wave 1 — High-Value Batch Programs (Low Risk, High Impact)

| Priority | Program | Rationale |
|----------|---------|-----------|
| 1 | **CBACT01C** | Simple batch reader with flat-file output. Minimal business logic. Ideal proof-of-concept: converts VSAM reads to JDBC/JPA reads. Tests the data access layer migration pattern. |
| 2 | **CBCUS01C** | Identical pattern to CBACT01C for customer data. Validates the migration pattern across a second entity. |
| 3 | **CBACT02C / CBACT03C** | Same read-and-print pattern for card and cross-reference data. Quick wins to build confidence. |
| 4 | **CSUTLDTC** | Self-contained date utility with no file I/O. Converts to a Java `DateUtils` class. Unblocks CORPT00C and COTRN02C. |

**Wave 1 rationale:** These programs are leaf nodes with no downstream callers, small LOC, and minimal business logic. They establish the VSAM → JDBC migration pattern, copybook → Java POJO conversion, and test infrastructure.

#### Wave 2 — Core Batch Processing

| Priority | Program | Rationale |
|----------|---------|-----------|
| 5 | **CBTRN02C** | Daily transaction posting — highest-value batch program. Updates 3 VSAM files, complex validation logic (48 IF/EVALUATE). Requires ACID transaction support in Java. |
| 6 | **CBACT04C** | Interest calculator — reads 4 files, updates 2. Core financial logic. Must preserve exact decimal arithmetic (COMP-3 → BigDecimal). |
| 7 | **CBTRN03C** | Report generator — reads 5 reference files. Tests multi-file join pattern in Java. |
| 8 | **CBSTM03A/B** | Statement generation (driver + subroutine). Tests CALL-based decomposition → Java method extraction and the GSAM/file-output pattern. |

**Wave 2 rationale:** These programs implement the core daily batch cycle. Modernizing them enables retirement of the VSAM-based batch pipeline. The SORT steps in JCL map to Java Stream/SQL ORDER BY operations.

#### Wave 3 — CICS Online Programs (High Complexity, Requires UI Migration)

| Priority | Program | Rationale |
|----------|---------|-----------|
| 9 | **COSGN00C** | Sign-on — simple entry point. Maps to Spring Security. Low LOC but unlocks the entire online workflow. |
| 10 | **COMEN01C / COADM01C** | Menu programs. Map to navigation controllers/routers. Low logic, high connectivity. |
| 11 | **COTRN00C / COTRN01C** | Transaction list/view — medium complexity. Tests the CICS STARTBR/READNEXT pattern → JPA pagination. |
| 12 | **COACTVWC** | Account view — read-only with multi-file lookup. Tests the join pattern in REST API form. |
| 13 | **COCRDUPC** | Card update — high complexity (80 IF/EVALUATE, 16 copybooks). Requires careful field-by-field validation migration. |
| 14 | **COACTUPC** | **Last in wave** — highest complexity program (4,236 LOC, 58 copybooks, 174 IF/EVALUATE). Migrating this program is the capstone that proves the CICS → Spring MVC/REST migration is complete. |

**Wave 3 rationale:** CICS programs require a UI framework decision (Spring MVC, React, Angular). BMS maps translate to HTML forms. EXEC CICS READ/WRITE maps to JPA repositories. COMMAREA-based navigation maps to session state or JWT tokens.

#### Wave 4 — Sub-Applications (Specialized Middleware)

| Priority | Program | Rationale |
|----------|---------|-----------|
| 15 | **COBTUPDT** | Batch Db2 — simplest Db2 program. Validates EXEC SQL → JPA/JDBC migration. |
| 16 | **COTRTLIC / COTRTUPC** | CICS+Db2 — complex cursor-based screens. Requires Db2 → PostgreSQL/MySQL schema migration. |
| 17 | **COPAUA0C** | MQ+IMS+CICS hybrid. Maps to Spring JMS + Spring Data. Highest middleware complexity. |
| 18 | **IMS batch programs** | CBPAUP0C, DBUNLDGS, PAUDBLOD, PAUDBUNL — IMS DL/I → JPA with hierarchical model. |

**Wave 4 rationale:** These programs depend on IBM middleware (IMS DB, MQ, Db2). Each requires a middleware replacement decision. Defer until the core VSAM/CICS patterns are proven.

### 7.2 Key Risk Factors

| Risk | Affected Programs | Mitigation |
|------|------------------|------------|
| **Decimal precision** | CBACT04C, CBTRN02C, COBIL00C | Use `java.math.BigDecimal` for all monetary fields. COMP-3 → BigDecimal conversion must preserve scale. |
| **File locking semantics** | CBTRN02C (I-O mode), COACTUPC (REWRITE) | VSAM record-level locking → database row-level locking (SELECT FOR UPDATE). |
| **BMS map translation** | All 20 CICS programs | BMS maps → HTML/JSP templates. DFHBMSCA attribute bytes → CSS classes. |
| **COMMAREA state** | All CICS programs via COCOM01Y | CICS pseudo-conversational state → HTTP session or JWT. |
| **IMS hierarchical model** | Auth sub-app (8 programs) | IMS parent/child segments → JPA @OneToMany with composite keys. |
| **MQ integration** | COPAUA0C, COACCT01, CODATE01 | MQGET/MQPUT → Spring JMS or Apache Kafka. |
| **COPY REPLACING** | COACTUPC (30+ CSSETATY copies) | Template expansion → Java generics or validation framework (Bean Validation). |
| **GDG datasets** | TRANBKP, POSTTRAN, TRANREPT, INTCALC | GDG versioning → file naming with timestamps or database versioned tables. |

### 7.3 Modernization Metrics Target

| Metric | Current (COBOL) | Target (Java) |
|--------|-----------------|---------------|
| Total LOC | ~30,175 | ~15,000–20,000 (Java is more concise) |
| Programs | 44 | ~30 Java classes + framework |
| Copybooks | 38 | ~15 POJOs/DTOs |
| VSAM files | 10 | 10 database tables |
| JCL jobs | 46 | Spring Batch jobs or cron tasks |
| BMS maps | ~20 | HTML/React components |
| Db2 tables | 3 | Migrate as-is or merge |
| IMS DB | 1 (PAUTHDB) | 2–3 relational tables |
