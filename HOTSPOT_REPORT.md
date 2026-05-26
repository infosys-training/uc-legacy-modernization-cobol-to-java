# Hotspot Report

Top-10 most complex programs ranked by composite complexity (lines, copybooks, file I/O, nesting depth, special constructs) with modernization recommendations.

---

## Top 10 Complexity Hotspots

### 1. COACTUPC.cbl — Account Update (Online/CICS)

| Metric | Value |
|--------|-------|
| Lines | 4,236 |
| Copybooks | 13+ (CSUTLDWY, CVCRD01Y, CSLKPCDY, COTTL01Y, COACTUP, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT01Y, CVACT03Y, CVCUS01Y, COCOM01Y, CSSETATY ×13) |
| VSAM Files | 5 (ACCTDAT, CUSTDAT, CARDDAT, CARDAIX, CXACAIX) |
| Trans ID | CAUP |
| Key Complexity | Deep EVALUATE/IF nesting for field-by-field validation; COPY CSSETATY REPLACING used 13 times to set BMS attributes; reads customer/account/card data, validates all fields, performs updates with rollback logic; phone area code validation via CSLKPCDY (370+ values); largest single program in the estate |

**RECOMMEND FIRST:** Split into Account Update + Customer Update microservices. The program handles both account and customer entity updates in a single monolith with interleaved validation logic.

---

### 2. COTRTLIC.cbl — Transaction Type List (Online/CICS+DB2)

| Metric | Value |
|--------|-------|
| Lines | 2,098 |
| Copybooks | 10 (CVCRD01Y, COCOM01Y, COTTL01Y, COTRTLI, CSDAT01Y, CSMSG01Y, CSUSR01Y, CVACT02Y, CSSTRPFY, CSDB2RWY) |
| DB2 Operations | 6+ (cursor-based SELECT, UPDATE, INSERT, DELETE with SYNCPOINT) |
| Trans ID | CCLI (DB2 sub-app) |
| Key Complexity | Dual technology stack (CICS + DB2); cursor-based pagination over DB2 result sets; SYNCPOINT management for transactional consistency; includes SQL INCLUDE for DCLTRTYP/SQLCA |

---

### 3. COTRTUPC.cbl — Transaction Type Update (Online/CICS+DB2)

| Metric | Value |
|--------|-------|
| Lines | 1,702 |
| Copybooks | 12 (CSUTLDWY, CVCRD01Y, COTTL01Y, COTRTUP, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, COCOM01Y, CSSETATY, CSSTRPFY) |
| DB2 Operations | 7+ (SELECT, UPDATE, INSERT, DELETE with SYNCPOINT after each DML) |
| Trans ID | CCUP (DB2 sub-app) |
| Key Complexity | Dual technology stack; COPY CSSETATY REPLACING for BMS attribute setting; field-by-field validation with date editing (CSUTLDWY); DB2 SYNCPOINT after every DML statement; ABEND handling |

---

### 4. COCRDUPC.cbl — Credit Card Update (Online/CICS)

| Metric | Value |
|--------|-------|
| Lines | 1,560 |
| Copybooks | 11 (CVCRD01Y, COCOM01Y, COTTL01Y, COCRDUP, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT02Y, CVCUS01Y, CSSTRPFY) |
| VSAM Files | 1 R/W (CARDDAT with lock/rewrite pattern) |
| Trans ID | CCUP |
| Key Complexity | Lock/read/validate/rewrite pattern on CARDDAT; card number validation; expiration date validation; field-level BMS attribute management; customer data cross-reference lookups |

---

### 5. COCRDLIC.cbl — Credit Card List (Online/CICS)

| Metric | Value |
|--------|-------|
| Lines | 1,459 |
| Copybooks | 10 (CVCRD01Y, COCOM01Y, COTTL01Y, COCRDLI, CSDAT01Y, CSMSG01Y, CSUSR01Y, CVACT02Y, CSSTRPFY) |
| VSAM Files | 2 (CARDDAT, CARDAIX) |
| Trans ID | CCLI |
| Key Complexity | Forward/backward VSAM browse (STARTBR/READNEXT); pagination state management; XCTL to detail (COCRDSLC) and update (COCRDUPC) programs; complex screen population logic |

---

### 6. COPAUA0C.cbl — Authorization Processor (Online/CICS+IMS+MQ)

| Metric | Value |
|--------|-------|
| Lines | 1,026 |
| Copybooks | 16 (CMQODV, CMQMDV ×2, CMQV, CMQTML, CMQPMOV, CMQGMOV, CCPAURQY, CCPAURLY, CCPAUERY, CIPAUSMY, CIPAUDTY, CVACT03Y, CVACT01Y, CVCUS01Y) |
| Technologies | 3 (CICS + IMS DL/I + MQ Series) |
| Key Complexity | Triple technology stack — highest integration complexity; MQ OPEN/GET/PUT1 for request/reply messaging; IMS DL/I GU/REPL/ISRT/SCHD/TERM for database operations; CICS READ for VSAM lookups; complex authorization decision logic with fraud detection |

**RECOMMEND EARLY:** Hardest to auto-convert due to three-way technology integration (CICS + IMS + MQ). Requires manual decomposition into API gateway + message broker + database service.

---

### 7. COPAUS0C.cbl — Pending Auth Summary (Online/CICS+IMS)

| Metric | Value |
|--------|-------|
| Lines | 1,032 |
| Copybooks | 14 (COCOM01Y, COPAU00, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y, CIPAUSMY, CIPAUDTY, DFHAID, DFHBMSCA) |
| Technologies | 2 (CICS + IMS DL/I) |
| Key Complexity | IMS DL/I GNP-based pagination through hierarchical segments; DLI SCHD/TERM for PSB scheduling; SYNCPOINT management; complex screen mapping with multiple entity data |

---

### 8. COACTVWC.cbl — Account View (Online/CICS)

| Metric | Value |
|--------|-------|
| Lines | 941 |
| Copybooks | 14 (CVCRD01Y, COCOM01Y, COTTL01Y, COACTVW, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y, CSSTRPFY, DFHBMSCA, DFHAID) |
| VSAM Files | 3 reads (ACCTDAT, CUSTDAT, CXACAIX) |
| Trans ID | CAVW |
| Key Complexity | Multi-entity view joining account + customer + card data via cross-reference; ABEND handling; PF key mapping; complex map population |

---

### 9. CBSTM03A.CBL — Statement Generation (Batch)

| Metric | Value |
|--------|-------|
| Lines | 924 |
| Copybooks | 4 (COSTM01, CVACT03Y, CUSTREC, CVACT01Y) |
| Output | STMTFILE (plain text) + HTMLFILE (HTML) |
| Key Complexity | Uses PSA/TCB/TIOT addressing (low-level z/OS control block navigation); ALTER/GO TO statements (non-structured flow control); 2D arrays for statement formatting; 10 CALL invocations to CBSTM03B subroutine for different file operations; generates both plain text and HTML output |

**RECOMMEND EARLY (Phase 3):** Hardest to auto-convert due to ALTER/GO TO and PSA/TCB/TIOT addressing. These are low-level z/OS constructs with no direct Java equivalent. Requires manual rewrite.

---

### 10. COTRN02C.cbl — Transaction Add (Online/CICS)

| Metric | Value |
|--------|-------|
| Lines | 783 |
| Copybooks | 10 (COCOM01Y, COTRN02, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, CVACT01Y, CVACT03Y, DFHAID, DFHBMSCA) |
| VSAM Files | R CXACAIX, CCXREF; W TRANSACT |
| Trans ID | CT02 |
| Key Complexity | Extensive field-by-field validation for all transaction fields; CALL CSUTLDTC for date validation (2 invocations); card-to-account cross-reference verification; transaction record construction and write |

---

## Additional Notable Programs (Honorable Mentions)

| Rank | Program | Lines | Key Factor |
|------|---------|------:|------------|
| 11 | CBTRN02C.cbl | 731 | 6 VSAM files, validation pipeline, account balance updates, reject handling |
| 12 | CBTRN03C.cbl | 649 | 6 input files, complex report layout with page/account/grand totals |
| 13 | CORPT00C.cbl | 649 | JCL generation + TDQ write + dual CSUTLDTC CALLs |
| 14 | CBACT04C.cbl | 652 | 5 VSAM files, interest calculation with disclosure group lookups |
| 15 | COPAUS1C.cbl | 604 | IMS DL/I + CICS dual stack, GU/GNP/REPL operations |
| 16 | CBEXPORT.cbl | 582 | 5 input files, multi-record-type output with COMP/COMP-3 fields |

---

## Modernization Priority Recommendation

### Phase 1: Core Batch Processing (Highest Business Value)

| Program | Rationale | Estimated Effort |
|---------|-----------|-----------------|
| **CBTRN02C.cbl** (731 lines) | Core daily transaction posting; validates inputs, updates balances, manages rejects. Heart of the batch cycle. | Medium — straightforward procedural logic, no special constructs |
| **CBACT04C.cbl** (652 lines) | Interest calculation; reads disclosure groups and category balances, updates accounts. Critical financial logic. | Medium — mathematical logic, multi-file joins |

### Phase 2: Largest Online Program (Split Required)

| Program | Rationale | Estimated Effort |
|---------|-----------|-----------------|
| **COACTUPC.cbl** (4,236 lines) | Largest program; handles account + customer updates in one monolith. Split into Account Update Service + Customer Update Service. | High — needs functional decomposition, 5 VSAM files, 13+ copybooks |
| **COCRDUPC.cbl** (1,560 lines) | Card update with lock/rewrite pattern. Convert to Card Management Service. | Medium — single-entity focus |

### Phase 3: Statement Generation (Manual Intervention Required)

| Program | Rationale | Estimated Effort |
|---------|-----------|-----------------|
| **CBSTM03A.CBL** (924 lines) | Uses PSA/TCB/TIOT addressing, ALTER/GO TO — no direct translation. Must be manually rewritten. | High — z/OS-specific constructs require manual rewrite |
| **CBSTM03B.CBL** (230 lines) | File I/O subroutine for CBSTM03A; tightly coupled. | Low — straightforward file operations, rewrite alongside CBSTM03A |

### Phase 4: Remaining CRUD Screens (Straightforward Conversion)

| Programs | Rationale | Estimated Effort |
|----------|-----------|-----------------|
| COUSR00C–03C (4 programs) | User CRUD — straightforward screen-to-REST API conversion | Low |
| COTRN00C, COTRN01C (2 programs) | Transaction list/view — read-only, map to GET endpoints | Low |
| COCRDLIC, COCRDSLC (2 programs) | Card list/view — read-only with pagination | Low-Medium |
| COSGN00C, COMEN01C, COADM01C | Navigation/auth — replace with modern auth framework | Low |
| COBIL00C (572 lines) | Bill payment — single-purpose, maps to Payment Service | Medium |
| CORPT00C (649 lines) | Report submission — convert to async job submission API | Medium |

### Phase 5: Sub-Application Programs

| Sub-Application | Programs | Rationale |
|----------------|----------|-----------|
| Authorization (IMS/DB2/MQ) | 8 programs | Most complex integration; requires IMS→relational DB migration + MQ→modern messaging migration |
| Transaction Type (DB2) | 3 programs | Already on DB2; simplest sub-app to convert |
| VSAM/MQ | 2 programs | MQ integration layer; convert to REST/messaging adapter |

---

## Complexity Metrics Summary

| Metric | Count |
|--------|------:|
| Programs > 1,000 lines | 8 |
| Programs using 3+ technologies | 1 (COPAUA0C: CICS+IMS+MQ) |
| Programs using DB2 | 4 (COTRTLIC, COTRTUPC, COBTUPDT, COPAUS2C) |
| Programs using IMS DL/I | 6 (CBPAUP0C, COPAUA0C, COPAUS0C, COPAUS1C, DBUNLDGS, PAUDBLOD, PAUDBUNL) |
| Programs using MQ | 4 (COPAUA0C, COACCT01, CODATE01) |
| Programs with ALTER/GO TO | 1 (CBSTM03A) |
| Programs accessing 5+ files | 5 (COACTUPC, CBTRN01C, CBTRN02C, CBEXPORT, CBIMPORT) |
| Total programs requiring manual intervention | 2 (CBSTM03A, COPAUA0C) |
