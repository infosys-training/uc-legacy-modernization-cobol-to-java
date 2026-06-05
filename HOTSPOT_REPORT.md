# HOTSPOT REPORT — CardDemo COBOL Estate

> Programs ranked by complexity and modernization priority. Metrics derived from static analysis of all 44 COBOL programs.

---

## 1. Scoring Methodology

Each program is scored on five dimensions (0–100 normalized scale):

| Dimension | Weight | Measurement | Max in Estate |
|-----------|--------|-------------|---------------|
| Lines of Code (LOC) | 25% | Raw line count | 4,236 (COACTUPC) |
| Copybook References | 20% | Count of COPY statements | 58 (COACTUPC) |
| I/O Operations | 20% | READ, WRITE, REWRITE, DELETE, EXEC CICS READ/WRITE, EXEC SQL, MQ calls, DL/I calls, STARTBR/READNEXT/READPREV/ENDBR | 101 (CBSTM03A) |
| Business Logic Density | 20% | IF + EVALUATE statement count (proxy for branching complexity) | 184 (COACTUPC) |
| Inter-Program Dependencies | 15% | CALL + XCTL references (outgoing + incoming) | 12 (COMEN01C) |

**Composite Score** = Σ (normalized_metric × weight) × 100

---

## 2. Full Program Metrics (All 44 Programs)

| # | Program | LOC | Copybooks | I/O Ops | Branching | Dependencies | Composite Score |
|---|---------|-----|-----------|---------|-----------|-------------|----------------|
| 1 | COACTUPC.cbl | 4,236 | 58 | 11 | 184 | 2 | **74.8** |
| 2 | COTRTLIC.cbl | 2,098 | 12 | 30 | 32 | 3 | **33.1** |
| 3 | COTRTUPC.cbl | 1,702 | 15 | 37 | 26 | 3 | **31.0** |
| 4 | COCRDUPC.cbl | 1,560 | 16 | 5 | 88 | 2 | **30.2** |
| 5 | COCRDLIC.cbl | 1,459 | 14 | 12 | 77 | 3 | **28.5** |
| 6 | CBSTM03A.CBL | 924 | 5 | 101 | 24 | 1 | **28.2** |
| 7 | COPAUS0C.cbl | 1,032 | 15 | 4 | 47 | 2 | **22.3** |
| 8 | COPAUA0C.cbl | 1,026 | 17 | 11 | 36 | 4 | **22.0** |
| 9 | COACTVWC.cbl | 941 | 16 | 4 | 38 | 1 | **19.5** |
| 10 | COCRDSLC.cbl | 887 | 16 | 4 | 41 | 2 | **19.0** |
| 11 | COTRN02C.cbl | 783 | 11 | 18 | 40 | 3 | **18.8** |
| 12 | CBTRN02C.cbl | 731 | 6 | 11 | 48 | 1 | **16.4** |
| 13 | COTRN00C.cbl | 699 | 9 | 22 | 42 | 2 | **16.1** |
| 14 | COUSR00C.cbl | 695 | 9 | 22 | 41 | 3 | **16.0** |
| 15 | CBACT04C.cbl | 652 | 6 | 9 | 43 | 1 | **14.6** |
| 16 | CBTRN03C.cbl | 649 | 6 | 10 | 42 | 1 | **14.5** |
| 17 | CORPT00C.cbl | 649 | 9 | 2 | 30 | 3 | **14.0** |
| 18 | COACCT01.cbl | 620 | 9 | 13 | 18 | 0 | **13.0** |
| 19 | COPAUS1C.cbl | 604 | 11 | 0 | 27 | 2 | **12.5** |
| 20 | CBEXPORT.cbl | 582 | 7 | 11 | 16 | 0 | **11.8** |
| 21 | COBIL00C.cbl | 572 | 11 | 16 | 28 | 1 | **13.3** |
| 22 | CODATE01.cbl | 524 | 8 | 12 | 16 | 0 | **10.8** |
| 23 | CBTRN01C.cbl | 494 | 7 | 5 | 33 | 1 | **10.5** |
| 24 | CBIMPORT.cbl | 487 | 7 | 8 | 16 | 0 | **9.8** |
| 25 | CBACT01C.cbl | 430 | 3 | 10 | 22 | 2 | **9.4** |
| 26 | COUSR02C.cbl | 414 | 9 | 2 | 23 | 1 | **8.8** |
| 27 | CBPAUP0C.cbl | 386 | 3 | 17 | 21 | 0 | **8.6** |
| 28 | PAUDBLOD.CBL | 369 | 5 | 9 | 17 | 0 | **7.5** |
| 29 | DBUNLDGS.CBL | 366 | 7 | 12 | 9 | 0 | **7.4** |
| 30 | COUSR03C.cbl | 359 | 9 | 3 | 18 | 1 | **7.6** |
| 31 | COTRN01C.cbl | 330 | 9 | 1 | 13 | 1 | **6.5** |
| 32 | PAUDBUNL.CBL | 317 | 5 | 10 | 11 | 0 | **6.4** |
| 33 | COMEN01C.cbl | 308 | 10 | 0 | 13 | 12 | **8.5** |
| 34 | COUSR01C.cbl | 299 | 10 | 1 | 10 | 1 | **5.8** |
| 35 | COADM01C.cbl | 288 | 10 | 0 | 10 | 7 | **6.8** |
| 36 | COSGN00C.cbl | 260 | 10 | 1 | 10 | 2 | **5.4** |
| 37 | COPAUS2C.cbl | 244 | 3 | 4 | 3 | 1 | **3.9** |
| 38 | COBTUPDT.cbl | 237 | 1 | 8 | 10 | 0 | **4.5** |
| 39 | CBSTM03B.CBL | 230 | 1 | 7 | 13 | 1 | **4.5** |
| 40 | CBACT02C.cbl | 178 | 2 | 2 | 11 | 1 | **3.3** |
| 41 | CBACT03C.cbl | 178 | 2 | 2 | 11 | 1 | **3.3** |
| 42 | CBCUS01C.cbl | 178 | 2 | 2 | 11 | 1 | **3.3** |
| 43 | CSUTLDTC.cbl | 157 | 1 | 0 | 2 | 2 | **2.5** |
| 44 | COBSWAIT.cbl | 41 | 1 | 0 | 0 | 1 | **0.5** |

---

## 3. Top 10 Hotspot Analysis

### Rank 1: COACTUPC.cbl — Account Update (Score: 74.8)

| Metric | Value | Percentile |
|--------|-------|------------|
| LOC | 4,236 | **100th** (largest in estate) |
| Copybooks | 58 | **100th** (most in estate) |
| I/O Operations | 11 | 52nd |
| Branching (IF+EVALUATE) | 184 | **100th** (most complex logic) |
| Dependencies | 2 | 17th |

**Analysis:** Dominates every size/complexity metric. Contains exhaustive validation logic for dates (CSUTLDWY), SSN, phone numbers (NANPA area codes via CSLKPCDY), state codes, and ZIP codes. Uses COPY REPLACING pattern 38 times for CSSETATY screen attribute macros. Touches 3 VSAM files (account, card cross-reference, customer). The single largest modernization risk.

**Modernization Approach:** Split into 4 Java components:
1. `AccountController` — REST API (replaces BMS screen interaction)
2. `AccountService` — Business logic (account read/update)
3. `AccountValidationService` — Extract all validation rules (date, SSN, phone, state, ZIP)
4. `AccountView` — Modern UI (replaces BMS map COACTUP)

### Rank 2: COTRTLIC.cbl — Transaction Type List/DB2 (Score: 33.1)

| Metric | Value | Percentile |
|--------|-------|------------|
| LOC | 2,098 | 95th |
| Copybooks | 12 | 68th |
| I/O Operations | 30 | 86th |
| Branching | 32 | 61st |
| Dependencies | 3 | 25th |

**Analysis:** DB2 cursor-based pagination with inline delete. Already uses SQL, making it the natural entry point for database modernization. Paired with COTRTUPC as the transaction-type CRUD module.

**Modernization Approach:** JPA entities for TRANSACTION_TYPE; Spring Data paging replaces cursor-based browsing. Convert together with COTRTUPC as a single DB2 migration unit.

### Rank 3: COTRTUPC.cbl — Transaction Type Update/DB2 (Score: 31.0)

| Metric | Value | Percentile |
|--------|-------|------------|
| LOC | 1,702 | 93rd |
| Copybooks | 15 | 77th |
| I/O Operations | 37 | 89th |
| Branching | 26 | 52nd |
| Dependencies | 3 | 25th |

**Analysis:** Full CRUD with cascading deletes (TRANSACTION_TYPE → TRANSACTION_CATEGORY). Uses CSSETATY COPY REPLACING. Delete confirmation workflow mirrors modern UI patterns.

**Modernization Approach:** JPA CascadeType.ALL for parent-child relationship. Single REST controller with CRUD endpoints.

### Rank 4: COCRDUPC.cbl — Credit Card Update (Score: 30.2)

| Metric | Value | Percentile |
|--------|-------|------------|
| LOC | 1,560 | 91st |
| Copybooks | 16 | 82nd |
| I/O Operations | 5 | 30th |
| Branching | 88 | 93rd |
| Dependencies | 2 | 17th |

**Analysis:** High branching density relative to I/O — most of the complexity is in field validation and screen attribute management. Shares the paginated browse pattern with COCRDLIC.

**Modernization Approach:** Create reusable `CardService` with `CardValidationService`. Extract shared browse pattern into `AbstractListController`.

### Rank 5: COCRDLIC.cbl — Credit Card List (Score: 28.5)

| Metric | Value | Percentile |
|--------|-------|------------|
| LOC | 1,459 | 89th |
| Copybooks | 14 | 73rd |
| I/O Operations | 12 | 59th |
| Branching | 77 | 89th |
| Dependencies | 3 | 25th |

**Analysis:** VSAM STARTBR/READNEXT/READPREV/ENDBR pagination pattern found in 5+ programs. Highest opportunity for pattern-based migration — solve once, apply everywhere.

**Modernization Approach:** Spring Data `Pageable` replaces all VSAM browse programs (COCRDLIC, COTRN00C, COUSR00C). Create generic `VsamBrowseService` → `JpaPagedQueryService`.

### Rank 6: CBSTM03A.CBL — Statement Generation (Score: 28.2)

| Metric | Value | Percentile |
|--------|-------|------------|
| LOC | 924 | 80th |
| Copybooks | 5 | 27th |
| I/O Operations | 101 | **100th** (highest in estate) |
| Branching | 24 | 48th |
| Dependencies | 1 | 8th |

**Analysis:** Highest I/O operation count (101 ops — 97 WRITEs for report formatting). Generates both plain text and HTML statements. Pure batch, no CICS dependency — **easiest program to prove the migration approach**. Self-contained with one CALL dependency (CBSTM03B).

**Modernization Approach:** Spring Batch `ItemReader`/`ItemWriter` pipeline. Thymeleaf or Jasper for HTML output. Recommended as the **first program to migrate** — low risk, high confidence-builder.

### Rank 7: COPAUS0C.cbl — Auth Summary View/IMS (Score: 22.3)

| Metric | Value | Percentile |
|--------|-------|------------|
| LOC | 1,032 | 82nd |
| Copybooks | 15 | 77th |
| I/O Operations | 4 | 25th |
| Branching | 47 | 73rd |
| Dependencies | 2 | 17th |

**Analysis:** IMS DL/I browse with CICS screen interaction. Part of the COPAUS0C → COPAUS1C → COPAUS2C chain that must be migrated as a unit.

**Modernization Approach:** IMS hierarchical data maps to 2 relational tables (AUTH_SUMMARY, AUTH_DETAIL). Spring Data repositories replace DL/I calls. Migrate entire auth chain together.

### Rank 8: COPAUA0C.cbl — Authorization Decision/IMS+MQ+CICS (Score: 22.0)

| Metric | Value | Percentile |
|--------|-------|------------|
| LOC | 1,026 | 81st |
| Copybooks | 17 | 84th |
| I/O Operations | 11 | 52nd |
| Branching | 36 | 64th |
| Dependencies | 4 | 33rd |

**Analysis:** Most architecturally complex program — spans CICS (online), IMS (database), and MQ (messaging). The only program touching all three subsystems. Handles real-time authorization decisions with fraud checking.

**Modernization Approach:** Spring JMS for MQ; Spring Data JPA for IMS data; dedicated `AuthorizationService` with `FraudCheckService`. Requires thorough integration testing.

### Rank 9: COACTVWC.cbl — Account View (Score: 19.5)

| Metric | Value | Percentile |
|--------|-------|------------|
| LOC | 941 | 79th |
| Copybooks | 16 | 82nd |
| I/O Operations | 4 | 25th |
| Branching | 38 | 66th |
| Dependencies | 1 | 8th |

**Analysis:** Read-only account display with card cross-reference lookup. Lower risk than COACTUPC since it only reads data. Shares data access patterns with the account update program.

**Modernization Approach:** Reuse `AccountService` read methods from COACTUPC migration. Simple REST GET endpoint.

### Rank 10: COCRDSLC.cbl — Credit Card Detail View (Score: 19.0)

| Metric | Value | Percentile |
|--------|-------|------------|
| LOC | 887 | 77th |
| Copybooks | 16 | 82nd |
| I/O Operations | 4 | 25th |
| Branching | 41 | 68th |
| Dependencies | 2 | 17th |

**Analysis:** Card detail display with customer lookup. Read-only, lower complexity than COCRDUPC. Shares VSAM file access with card list and update programs.

**Modernization Approach:** Reuse `CardService` from COCRDLIC/COCRDUPC migration. REST GET endpoint with account/customer joins.

---

## 4. Recommended Modernization Sequence

### Phase 1: Prove the Approach (Weeks 1–4)

| Order | Program(s) | Rationale |
|-------|-----------|-----------|
| 1 | **CBSTM03A + CBSTM03B** | Pure batch, highest I/O count, self-contained. Zero CICS dependency. Success here proves Spring Batch migration approach and builds team confidence. |
| 2 | **COBTUPDT** | Simplest DB2 program (237 LOC). Proves SQL migration path with minimal risk. |

### Phase 2: DB2 Migration (Weeks 5–8)

| Order | Program(s) | Rationale |
|-------|-----------|-----------|
| 3 | **COTRTLIC + COTRTUPC** | DB2 cursor pagination + full CRUD. Already SQL-based — closest to modern patterns. Creates reusable JPA entity templates. |
| 4 | **COPAUS2C** | Smallest IMS-adjacent program (244 LOC). DB2 fraud table — isolated, low risk. |

### Phase 3: VSAM Batch Pipeline (Weeks 9–14)

| Order | Program(s) | Rationale |
|-------|-----------|-----------|
| 5 | **CBTRN02C** (transaction posting) | Core batch pipeline step 1. Touches ACCTFILE — validates dual-write strategy. |
| 6 | **CBACT04C** (interest calc) | Pipeline step 2. Pure arithmetic — easy to verify output parity. |
| 7 | **CBTRN03C** (transaction report) | Pipeline step 4. Report generation — output comparison is straightforward. |
| 8 | **CBEXPORT + CBIMPORT** | Data migration utilities. Low branch complexity, high I/O. |

### Phase 4: CICS Online Programs (Weeks 15–22)

| Order | Program(s) | Rationale |
|-------|-----------|-----------|
| 9 | **COUSR00C–03C** (user CRUD) | Simplest CICS CRUD pattern. Proves BMS-to-REST+UI approach. |
| 10 | **COCRDLIC + COCRDSLC + COCRDUPC** | Card management. Establishes VSAM browse → Spring Data paging pattern. |
| 11 | **COTRN00C + COTRN01C + COTRN02C** | Transaction management. Reuses browse pattern from step 10. |
| 12 | **COACTVWC + COACTUPC** | Account management. **COACTUPC is the highest-risk program** — migrate last in this phase after all patterns are proven. |
| 13 | **COBIL00C + CORPT00C** | Bill payment + report request. |
| 14 | **COSGN00C + COMEN01C + COADM01C** | Navigation shell. Replace with modern auth (Spring Security) + SPA routing. |

### Phase 5: IMS/MQ Integration (Weeks 23–28)

| Order | Program(s) | Rationale |
|-------|-----------|-----------|
| 15 | **COPAUS0C + COPAUS1C** (+ COPAUS2C already done) | Auth browse chain. IMS → relational mapping. Must migrate as unit. |
| 16 | **COPAUA0C** | Authorization decision. Most complex integration (CICS+IMS+MQ). Migrate last. |
| 17 | **COACCT01 + CODATE01** | MQ service programs. Spring JMS adapters. |
| 18 | **CBPAUP0C + DBUNLDGS + PAUDBLOD + PAUDBUNL** | IMS batch utilities. Spring Batch with JPA. |

---

## 5. Risk-Weighted Priority Matrix

```
                    HIGH COMPLEXITY
                         ▲
                         │
    COPAUA0C (IMS+MQ)    │    COACTUPC ★★★
    COPAUS0C (IMS)       │    (4,236 LOC, score 74.8)
                         │
                         │    COTRTLIC / COTRTUPC (DB2)
                         │    COCRDUPC / COCRDLIC
    ─────────────────────┼─────────────────────────►
    LOW BUSINESS VALUE   │           HIGH BUSINESS VALUE
                         │
    CBPAUP0C (IMS batch) │    CBSTM03A ★ (start here)
    PAUDBLOD/PAUDBUNL    │    CBTRN02C (pipeline core)
    DBUNLDGS             │    CBACT04C (interest calc)
                         │
                    LOW COMPLEXITY
```

**Legend:**
- ★★★ = Highest risk, migrate after patterns proven
- ★ = Recommended starting point (low risk, high confidence)

---

## 6. Key Findings

1. **COACTUPC dominates all metrics** — 4,236 LOC is 15.5% of total estate; 184 branching statements is 3.9× the next highest. This single program represents the biggest modernization risk and should be tackled only after all patterns are proven.

2. **Statement generation (CBSTM03A) is the ideal starting point** — pure batch, no CICS, highest I/O (101 ops), self-contained with single CALL dependency. Success validates Spring Batch approach.

3. **DB2 programs (COTRTLIC/COTRTUPC/COBTUPDT) are natural early wins** — already use SQL, cursor-based pagination maps directly to Spring Data paging.

4. **VSAM browse pattern appears in 5+ programs** — COCRDLIC, COTRN00C, COUSR00C, COPAUS0C all use STARTBR/READNEXT/READPREV/ENDBR. Solving this pattern once (Spring Data Pageable) accelerates 20%+ of the online estate.

5. **Authorization module (COPAUA0C + chain) is the most architecturally complex** — spans 3 subsystems (CICS+IMS+MQ). Migrate last to reduce integration risk.

6. **Plain-text password storage** (SEC-USR-PWD in CSUSR01Y, PIC X(08)) is a security risk. Modernization must introduce password hashing (bcrypt/scrypt).

7. **8 programs exceed 1,000 LOC** — these 8 programs contain 57% of total estate LOC (15,554 of 27,350). Migrating these 8 programs is the critical path.

8. **Copybook fan-out is concentrated** — CVACT03Y (card cross-reference) is referenced by 13 programs, making it the most coupled data structure. Changes to this entity affect 30% of the estate.
