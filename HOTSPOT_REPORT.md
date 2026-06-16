# HOTSPOT REPORT — CardDemo COBOL Estate

> **Methodology:** Programs ranked by composite score across 5 dimensions: LOC, copybook count, I/O operations, business logic density (IF/EVALUATE statements), and inter-program dependencies.
> **Scoring:** Each dimension normalized 0–20, composite max = 100.

---

## 1. Top 10 Programs by Composite Hotspot Score

| Rank | Program | LOC | Copybooks | I/O Ops | Logic Stmts | Dependencies | Composite Score | Classification |
|------|---------|-----|-----------|---------|-------------|-------------|-----------------|----------------|
| **1** | **COACTUPC.cbl** | 4,236 | 56 | 39 | 194 | 2 CALL + XCTL | **74.8** | CICS Online |
| **2** | **COTRTLIC.cbl** | 2,098 | 11 | 112 | 131 | 3 XCTL | **55.2** | CICS + DB2 |
| **3** | **COTRTUPC.cbl** | 1,702 | 13 | 77 | 134 | 2 XCTL | **50.6** | CICS + DB2 |
| **4** | **COCRDUPC.cbl** | 1,560 | 15 | 23 | 167 | 2 XCTL | **47.1** | CICS Online |
| **5** | **COCRDLIC.cbl** | 1,459 | 13 | 44 | 149 | 6 XCTL | **46.3** | CICS Online |
| **6** | **COPAUA0C.cbl** | 1,026 | 16 | 37 | 62 | 8 (MQ+IMS+CICS) | **42.0** | CICS+IMS+MQ |
| **7** | **COPAUS0C.cbl** | 1,032 | 14 | 20 | 47 | 2 XCTL | **33.5** | CICS + IMS |
| **8** | **COACTVWC.cbl** | 941 | 15 | 19 | 70 | 2 XCTL | **31.8** | CICS Online |
| **9** | **CBTRN02C.cbl** | 731 | 5 | 19 | 93 | 1 | **28.5** | Batch |
| **10** | **CBTRN03C.cbl** | 649 | 5 | 39 | 79 | 1 | **27.2** | Batch |

---

## 2. Dimension Rankings

### 2.1 By Lines of Code

| Rank | Program | LOC | Notes |
|------|---------|-----|-------|
| 1 | COACTUPC | 4,236 | 2× larger than #2; account update with exhaustive validation |
| 2 | COTRTLIC | 2,098 | DB2 cursor-based pagination |
| 3 | COTRTUPC | 1,702 | DB2 update with cascading deletes |
| 4 | COCRDUPC | 1,560 | Card update with multi-file I/O |
| 5 | COCRDLIC | 1,459 | Card list with STARTBR/READNEXT pagination |
| 6 | COPAUS0C | 1,032 | IMS authorization summary browse |
| 7 | COPAUA0C | 1,026 | Authorization decision (MQ+IMS+CICS) |
| 8 | COACTVWC | 941 | Account view (read-only, but complex display) |
| 9 | COCRDSLC | 887 | Card view detail |
| 10 | COTRN02C | 783 | Transaction add with validation |

### 2.2 By Copybook Count

| Rank | Program | Copybooks | Key Copybooks |
|------|---------|-----------|---------------|
| 1 | COACTUPC | 56 | CSSETATY ×13 (COPY REPLACING), CSLKPCDY, CSUTLDWY |
| 2 | COPAUA0C | 16 | 6 MQ copybooks + CIPAUSMY/CIPAUDTY + CVACT01Y/03Y/CUS |
| 3 | COACTVWC | 15 | CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y, CSSTRPFY |
| 4 | COCRDUPC | 15 | CVCRD01Y, CVACT02Y, CVCUS01Y, CSSTRPFY |
| 5 | COCRDSLC | 15 | Same set as COCRDUPC |
| 6 | COPAUS0C | 14 | CIPAUSMY, CIPAUDTY, CVACT01Y-03Y, CVCUS01Y |
| 7 | COCRDLIC | 13 | CVCRD01Y, CVACT02Y, CSSTRPFY |
| 8 | COTRTUPC | 13 | CSSETATY (REPLACING), CSSTRPFY, DB2 INCLUDEs |
| 9 | COUSR00C–03C | 8 each | Standard CICS set: COCOM01Y, DFHAID, DFHBMSCA |
| 10 | COTRN00C | 8 | CVTRA05Y, COTRN00, standard CICS set |

### 2.3 By I/O Operations

| Rank | Program | I/O Count | Types |
|------|---------|-----------|-------|
| 1 | COTRTLIC | 112 | DB2: 6 EXEC SQL (DECLARE/OPEN/FETCH/SELECT/DELETE); CICS MAP I/O |
| 2 | COTRTUPC | 77 | DB2: 5 EXEC SQL; CICS MAP I/O; cascading deletes |
| 3 | COCRDLIC | 44 | STARTBR, READNEXT ×N, READPREV ×N, ENDBR (CARDFILE) |
| 4 | CBTRN03C | 39 | Sequential READ (TRANSACT, XREFFILE, TRANTYPE, TRANCATG), WRITE (REPTFILE) |
| 5 | COACTUPC | 39 | READ (3 files), REWRITE (2 files), SEND/RECEIVE MAP |
| 6 | CBPAUP0C | 39 | DLI GN, GNP, DLET, CHKP — IMS hierarchical traversal |
| 7 | COPAUA0C | 37 | MQOPEN, MQGET, MQPUT1 + DLI SCHD/GU/TERM + CICS READ ×3 |
| 8 | CBEXPORT | 32 | READ 5 VSAM files, WRITE 1 export file |
| 9 | COTRN02C | 28 | READ (CARDXREF, ACCTFILE), STARTBR/READPREV/ENDBR + WRITE (TRANSACT) |
| 10 | COBIL00C | 27 | READ/REWRITE (ACCTFILE), browse + WRITE (TRANSACT), READ (CARDXREF) |

### 2.4 By Business Logic Density (IF/EVALUATE Statements)

| Rank | Program | Stmts | Density (stmts/100 LOC) | Notes |
|------|---------|-------|------------------------|-------|
| 1 | COACTUPC | 194 | 4.6 | Exhaustive field validation (date, SSN, phone, state, ZIP) |
| 2 | COCRDUPC | 167 | 10.7 | Card field validation + update logic |
| 3 | COCRDLIC | 149 | 10.2 | Pagination edge cases, filter logic |
| 4 | COTRTUPC | 134 | 7.9 | Delete confirmation, update validation, DB2 error handling |
| 5 | COTRTLIC | 131 | 6.2 | Cursor state management, row selection/deletion |
| 6 | CBTRN02C | 93 | 12.7 | Transaction posting validation (highest density in batch) |
| 7 | CBACT04C | 86 | 13.2 | Interest calculation logic (highest batch density) |
| 8 | COCRDSLC | 80 | 9.0 | Card display formatting and error handling |
| 9 | CBTRN03C | 79 | 12.2 | Report break logic (page/account/grand totals) |
| 10 | COACTVWC | 70 | 7.4 | Multi-file read orchestration + display formatting |

### 2.5 By Inter-Program Dependencies

| Rank | Program | Dep Count | Dependency Type |
|------|---------|-----------|-----------------|
| 1 | COMEN01C | 11 | XCTL to all user menu targets (hub) |
| 2 | COPAUA0C | 8 | MQOPEN + MQGET + MQPUT1 + DLI SCHD/GU/TERM + CICS READ ×3 |
| 3 | COADM01C | 6 | XCTL to 6 admin targets |
| 4 | COCRDLIC | 6 | XCTL to COCRDSLC, COCRDUPC, back to COMEN01C |
| 5 | COACTUPC | 2 | CALL CSUTLDTC (date validation) ×2 |
| 6 | COTRN02C (online) | 3 | CALL CSUTLDTC ×2 + XCTL |
| 7 | COUSR00C | 3 | XCTL to COUSR02C, COUSR03C, COADM01C |
| 8 | COTRTLIC | 3 | XCTL to COTRTUPC, COADM01C |
| 9 | CORPT00C | 3 | WRITEQ TD (JCL submit) + XCTL + CALL CSUTLDTC |
| 10 | COBIL00C | 1 | XCTL back to COMEN01C |

---

## 3. Complexity Indicators

### Programs Using COPY REPLACING (Macro-like Code Generation)

| Program | Copybook | Times Used | Effect |
|---------|----------|-----------|--------|
| COACTUPC | CSSETATY | 13× | Generates ~130 lines of screen attribute-setting code |
| COTRTUPC | CSSETATY | 3× | Generates ~30 lines |

### Programs Spanning Multiple Subsystems

| Program | Subsystems | Risk Level |
|---------|-----------|------------|
| COPAUA0C | CICS + IMS + MQ + DB2 | **CRITICAL** — spans all 4 subsystems |
| COPAUS0C | CICS + IMS | HIGH |
| COPAUS1C | CICS + IMS | HIGH |
| COPAUS2C | CICS + IMS + DB2 | HIGH |
| COTRTLIC | CICS + DB2 | MEDIUM |
| COTRTUPC | CICS + DB2 | MEDIUM |

---

## 4. Modernization Priority Recommendations

### Phase 1 — Quick Wins (Weeks 1–5)

| Priority | Program(s) | Rationale |
|----------|-----------|-----------|
| **P1-A** | COSGN00C, COUSR00C–03C, COMEN01C, COADM01C (3,897 LOC) | **User/Auth service** — low complexity, isolated from business data. Clear REST API mapping (login, user CRUD). Builds team confidence. Plain-text password remediation is a security win. |
| **P1-B** | CBTRN03C, CORPT00C (1,298 LOC) | **Reporting service** — self-contained batch, no CICS dependency for CBTRN03C. Pure read-only, zero risk to production data. Ideal pilot for Spring Batch. |

### Phase 2 — DB2 Programs (Weeks 5–12)

| Priority | Program(s) | Rationale |
|----------|-----------|-----------|
| **P2-A** | COTRTLIC, COTRTUPC, COBTUPDT (4,037 LOC) | **Transaction Type service** — already uses DB2 SQL. Most natural path to JPA. Cursor-based pagination maps to Spring Data Pageable. Cascading deletes map to JPA CascadeType.REMOVE. |
| **P2-B** | COCRDLIC, COCRDSLC, COCRDUPC (3,906 LOC) | **Card service** — paginated browse pattern (STARTBR/READNEXT/READPREV/ENDBR) repeats in 5+ programs. Building the `AbstractListController` pattern here pays off immediately. |

### Phase 3 — Core Business Logic (Weeks 12–22)

| Priority | Program(s) | Rationale |
|----------|-----------|-----------|
| **P3-A** | COACTUPC, COACTVWC (5,177 LOC) | **Account service** — highest-complexity program (COACTUPC score 74.8). Recommend splitting into: AccountService, AccountValidationService (date/SSN/phone/state/ZIP), AccountController (REST), AccountView (React/Angular). Generate 10,000+ test cases before rewriting. Run shadow mode for 2 weeks. |
| **P3-B** | COTRN00C, COTRN01C, COTRN02C, COBIL00C, CBTRN01C, CBTRN02C (3,920 LOC) | **Transaction service** — mix of online + batch. CBTRN02C (posting) is the critical batch path. Careful sequencing: online screens first, then batch posting. |

### Phase 4 — IMS/MQ Integration (Weeks 22–30)

| Priority | Program(s) | Rationale |
|----------|-----------|-----------|
| **P4** | COPAUA0C, COPAUS0C–2C, CBPAUP0C (3,292 LOC) | **Authorization service** — highest architectural risk (4 subsystems in COPAUA0C). IMS DL/I → JPA hierarchy mapping. MQ → SQS FIFO. Must migrate as a unit. Run parallel with existing IMS for 1+ week. |

### Phase 5 — Batch Pipeline & Data Migration (Weeks 30–35)

| Priority | Program(s) | Rationale |
|----------|-----------|-----------|
| **P5-A** | CBACT04C (652 LOC) | **Interest calculator** — Spring Batch with BigDecimal arithmetic. Must preserve exact COBOL decimal semantics. |
| **P5-B** | CBEXPORT, CBIMPORT (1,069 LOC) | **Data migration tools** — replatform to Spring Batch, then retire after migration. |
| **P5-C** | CBACT01C–03C, CBCUS01C (964 LOC) | **File readers** — simple VSAM dump utilities. Lowest priority; may be retired entirely. |

---

## 5. Risk Summary

| Risk Factor | Programs Affected | Mitigation |
|-------------|------------------|------------|
| **COACTUPC size** (4,236 LOC = 15% of estate in one program) | 1 | Decompose into 4 services; test exhaustively before migration |
| **COPY REPLACING** (macro expansion, no Java equivalent) | 2 (COACTUPC, COTRTUPC) | Convert to parameterized utility methods |
| **IMS hierarchy** (DL/I calls with no SQL equivalent) | 5 | Map IMS segments to 2 relational tables (summary + detail) |
| **MQ messaging** (real-time auth flow) | 1 (COPAUA0C) | Spring JMS adapter; SQS FIFO for ordering; parallel run 1 week |
| **Plain-text passwords** | All users (CSUSR01Y) | Implement bcrypt hashing immediately (security P0) |
| **VSAM dual-write** during transition | 13+ programs (ACCTFILE) | SQS FIFO sync; hourly reconciliation; single source of truth per entity |
| **Batch SLA preservation** | CBTRN02C, CBACT04C, CBTRN03C | JdbcBatchItemWriter batch=1,000; target ≤ 110% of COBOL runtime |

---

## 6. Estimated Effort

| Phase | Programs | LOC | Team Weeks | Notes |
|-------|----------|-----|------------|-------|
| P1: Auth + Reports | 9 | 5,195 | 4–5 | Quick win; builds patterns |
| P2: DB2 + Cards | 6 | 7,943 | 5–7 | DB2→JPA natural path |
| P3: Accounts + Txns | 8 | 9,097 | 8–10 | Highest complexity phase |
| P4: IMS/MQ Auth | 5 | 3,292 | 6–8 | Multi-subsystem risk |
| P5: Batch + Migration | 7 | 2,685 | 4–5 | Retire after migration |
| Decommission | — | — | 2–3 | Parallel run, cutover |
| **Total** | **35** | **28,212** | **32–42** | 4-person team, ~8 months |
