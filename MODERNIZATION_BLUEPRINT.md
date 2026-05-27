# CardDemo Modernization Blueprint

## Executive Summary

CardDemo is a mainframe credit card management system comprising **44 COBOL programs** (~20,650 LOC in the core module), **60+ copybooks**, **55 JCL jobs**, and **17 BMS screen maps**. The application runs on CICS with VSAM KSDS data stores and optional DB2/IMS/MQ extensions. This blueprint evaluates four modernization strategies for each functional area and recommends the optimal approach.

---

## Application Inventory Summary

| Category | Count | Notes |
|---|---|---|
| CICS Online Programs | 16 | UI-driven, COMMAREA-coupled |
| Batch Programs | 12 | File I/O, sequential processing |
| Batch Subroutines | 2 | CBSTM03B (statement file I/O), CSUTLDTC (date utility) |
| Optional Module Programs | 16 | Authorization (IMS/DB2/MQ), Transaction Type (DB2), VSAM/MQ |
| Copybooks (core) | 22 | Shared data structures |
| Copybooks (BMS) | 16 | Screen map layouts |
| Copybooks (optional) | 15 | Module-specific structures |
| JCL Jobs | 55 | Init, batch processing, utilities |
| BMS Maps | 17 | 3270 screen definitions |
| VSAM Data Files | 9 | KSDS with AIX |
| Assembler Modules | 2 | MVSWAIT (timer), COBDATFT (date format) |

---

## Functional Areas

### 1. Authentication & User Security

**Programs:** COSGN00C (260 LOC)
**Copybooks:** CSUSR01Y, COCOM01Y, COSGN00 (BMS)
**Data Stores:** USRSEC (VSAM KSDS — 80-byte records)
**Complexity:** Low

| Strategy | Assessment |
|---|---|
| **(a) Strangler** | Wrap with REST API for login/token. Straightforward — single VSAM file, simple read-verify logic. |
| **(b) Replatform** | Move to AWS M2 as-is. Low value — security model is weak (plaintext passwords in VSAM). |
| **(c) Refactor** | Add password hashing in COBOL. Possible but adds maintenance burden to a dead-end technology. |
| **(d) Rewrite** | Replace with modern identity provider (OAuth2/OIDC, Spring Security, Keycloak). |

**Recommendation: (d) Rewrite** — Replace with a modern identity provider.
- **Justification:** The current security model stores plaintext passwords in a flat VSAM file with no encryption, hashing, or token management. No amount of wrapping or replatforming fixes this fundamental deficiency. A modern identity service (Spring Security + JWT or Keycloak) is the correct foundation for the modernized system. This is also the lowest-risk area to rewrite given its isolation from business logic.

---

### 2. User Administration (Admin Domain)

**Programs:** COADM01C (288 LOC), COUSR00C (695 LOC), COUSR01C (299 LOC), COUSR02C (414 LOC), COUSR03C (359 LOC)
**Copybooks:** COADM02Y, COCOM01Y, CSUSR01Y, COUSR00–03 (BMS)
**Data Stores:** USRSEC (VSAM KSDS)
**Complexity:** Low–Medium

| Strategy | Assessment |
|---|---|
| **(a) Strangler** | Expose CRUD via APIs, let new UI call them. Clean seam — only touches USRSEC file. |
| **(b) Replatform** | Keep as CICS screens on cloud runtime. Low value for admin functions used infrequently. |
| **(c) Refactor** | Restructure COBOL for better maintainability. Marginal benefit for simple CRUD. |
| **(d) Rewrite** | Build as a microservice with REST API + modern UI. Standard CRUD pattern, well-understood. |

**Recommendation: (d) Rewrite** — Build as part of the Identity/User Management microservice.
- **Justification:** User administration is simple CRUD on a single VSAM file (USRSEC). The programs are small (total ~2,055 LOC) with no complex business logic. Rewriting as a Spring Boot REST service with a modern web UI is faster than wrapping CICS screens. This pairs naturally with the Authentication rewrite as a single User Management bounded context.

---

### 3. Account Management

**Programs:** COACTVWC (941 LOC), COACTUPC (4,236 LOC)
**Copybooks:** CVACT01Y (account), CVACT02Y (card), CVACT03Y (xref), COCOM01Y, CSUTLDWY (date), COACTVW/COACTUP (BMS)
**Data Stores:** ACCTDAT (VSAM KSDS — 300-byte records), CARDDAT (VSAM KSDS — 150-byte), CCXREF (VSAM KSDS — 50-byte), CXACAIX (VSAM AIX)
**Complexity:** High — **HIGHEST RISK AREA IN THE CODEBASE**

**Program-Level Risk Assessment:**

| Program | LOC | Risk | Key Concern |
|---|---|---|---|
| COACTUPC | 4,236 | **Critical** | Largest program; 25+ undocumented validation rules embedded in 88-level conditions and REDEFINES overlays |
| COACTVWC | 941 | Medium | Read-only; simpler but shares all data dependencies |

**Embedded Business Rules in COACTUPC (undocumented — must be reverse-engineered):**

| Rule | Location (approx. lines) | Description | Migration Danger |
|---|---|---|---|
| SSN Validation | lines 117–146 | 3-part SSN with IRS exclusion: area ≠ 000, 666, 900–999. Uses REDEFINES to overlay alphanumeric/numeric views. Three independent flags (PART1, PART2, PART3). | High — `INVALID-SSN-PART1 VALUES 0, 666, 900 THRU 999` is a regulatory rule; missing it = compliance violation |
| US Phone Format | lines 82–115 | 15-char field parsed via REDEFINES into area code (3) + exchange (3) + number (4) with delimiters. Each part validated independently. Format `(xxx)xxx-xxxx` enforced by overlay, not procedural code. | High — format enforcement is implicit in the REDEFINES structure, not in IF statements; easy to miss |
| Date Validation | line 166 | COPY 'CSUTLDWY' + CALL to CSUTLDTC which invokes z/OS `CEEDAYS` API for Lilian date conversion. Validates dates including leap years. | High — CEEDAYS is IBM LE-specific; exact valid-date ranges may differ from `java.time` |
| Credit Limit Check | within PROCESS-UPDATE | `ACCT-CURR-BAL` must not exceed `ACCT-CREDIT-LIMIT` and `ACCT-CASH-CREDIT-LIMIT` | Medium — straightforward but must handle COMP-3 signed decimals |
| Signed Number Validation | lines 56–80 | Generic framework using 88-level conditions with LOW-VALUES/blanks/'0' states for valid/invalid/blank field detection | Medium — the 3-state flag pattern (blank/invalid/valid) is reused throughout; must replicate exactly |
| Mandatory Field Checks | throughout | Multiple fields checked for SPACES and LOW-VALUES before processing | Low — straightforward null checks |
| Yes/No Flag Validation | throughout | 88-level conditions for Y/N fields with specific error messages per field | Low — simple but numerous |

**Cross-Domain Data Dependencies:**

| VSAM File | Access | Programs | Sharing Domains |
|---|---|---|---|
| ACCTDAT | Read/Write | COACTVWC, COACTUPC, CBACT01C | Card, Transaction (online+batch), Bill Payment, Interest Calc, Statement Gen, Export — **6 other domains** |
| CCXREF | Read/Write | COACTVWC, COACTUPC | Card, Transaction, Bill Payment, Interest Calc, Statement Gen, Export — **6 other domains** |
| CXACAIX | Read | COACTUPC | Card, Bill Payment — **2 other domains** |
| CARDDAT | Read | COACTUPC | Card, Transaction (batch), Interest Calc — **3 other domains** |

| Strategy | Assessment |
|---|---|
| **(a) Strangler** | Wrap CICS programs with APIs, incrementally replace. Feasible but COACTUPC's 25+ validation rules must be extracted carefully. The read-only view (COACTVWC) can be wrapped immediately. |
| **(b) Replatform** | Move to AWS M2. Preserves complex validation but delays modernization of the most tightly coupled data store (ACCTDAT accessed by 15+ programs). |
| **(c) Refactor** | Restructure COACTUPC (4,236 LOC with deep validation logic). Significant effort, unclear benefit — the interleaving of BMS screen I/O with validation makes separation difficult in COBOL. |
| **(d) Rewrite** | Translate to Java/Kotlin service. Highest long-term value but highest immediate risk — 25+ validation rules to replicate perfectly. |

**Recommendation: (a) Strangler Pattern** — Wrap with APIs first, then incrementally rewrite.
- **Justification:** COACTUPC is the **single riskiest program to migrate** — 4,236 LOC with 25+ undocumented validation rules, 4 shared VSAM files, and z/OS-specific API dependencies (CEEDAYS). The validation rules use COBOL-specific patterns (88-level conditions, REDEFINES overlays, COMP-3 signed arithmetic) that cannot be auto-translated reliably. The strangler pattern allows the team to:
  1. **Phase 2a:** Expose read operations (COACTVWC) as `GET /api/accounts/{id}` — low risk, immediate value
  2. **Phase 2b:** Wrap COACTUPC writes, initially delegating to the CICS program via COMMAREA adapter
  3. **Phase 3:** Incrementally replace validation rules in Java, validated by shadow-mode comparison (run both COBOL and Java validation on every request, compare results, alert on discrepancies)
  4. **Phase 3 gate:** Only cut over when shadow-mode shows 0 discrepancies for 30 days
- **Critical prerequisite:** Before any rewrite, generate a validation rules catalog and 500+ golden test cases from the COBOL program covering every 88-level condition path.

---

### 4. Credit Card Management

**Programs:** COCRDLIC (1,459 LOC), COCRDSLC (887 LOC), COCRDUPC (1,560 LOC)
**Copybooks:** CVACT02Y (card), CVACT03Y (xref), CVACT01Y (account), CVCRD01Y, COCOM01Y, COCRDLI/COCRDSL/COCRDUP (BMS)
**Data Stores:** CARDDAT, CCXREF, CXACAIX, ACCTDAT (all VSAM KSDS)
**Complexity:** High

**Program-Level Risk Assessment:**

| Program | LOC | Risk | Key Concern |
|---|---|---|---|
| COCRDLIC | 1,459 | **High** | Dual-mode VSAM browse (admin: all cards via CARDDAT; user: by account via CXACAIX alternate index). Pagination state management via first/last key tracking. |
| COCRDUPC | 1,560 | **High** | Card update with own validation rules (card number, expiration, CVV, status changes). BMS-interleaved business logic similar to COACTUPC pattern. |
| COCRDSLC | 887 | Medium | Read-only card detail view with CCXREF and ACCTDAT lookups. |

**COCRDLIC Browse Complexity:**
COCRDLIC implements two completely different browse algorithms depending on user type:
- **Admin mode:** STARTBR on CARDDAT KSDS, READNEXT through all cards, page forward/backward by saving first/last card number per page
- **User mode:** STARTBR on CXACAIX (alternate index on CCXREF by account ID), READNEXT through xref records, then READ each card from CARDDAT
- Edge cases: empty result set, single-page result, last page with fewer records — all handled via CICS RESP code checking
- The exact ordering of results depends on VSAM key sequence (EBCDIC collation) which may differ from SQL `ORDER BY` if character set encoding differs

**Cross-Domain Data Dependencies:**

| VSAM File | Access | Sharing Domains |
|---|---|---|
| CARDDAT | Read/Write | Transaction (batch, card validation), Interest Calc — **2 other domains** |
| CCXREF | Read/Write | Account, Transaction, Bill Payment, Interest Calc, Statement, Export — **6 other domains** |
| ACCTDAT | Read only | Account (owner), Transaction, Bill Payment, Interest Calc, Statement, Export — **6 other domains** |

| Strategy | Assessment |
|---|---|
| **(a) Strangler** | Wrap list/view/update as APIs. Good fit — card operations are well-defined but share data stores with Account domain. COCRDLIC's dual-mode browse is the main challenge. |
| **(b) Replatform** | Move to cloud runtime. Delays necessary decoupling from account data. |
| **(c) Refactor** | Improve COBOL structure. Large programs with UI-interleaved business logic make this expensive. |
| **(d) Rewrite** | Build as a microservice. Clean domain but shares VSAM files with Account domain. |

**Recommendation: (a) Strangler Pattern** — Co-evolve with Account Management.
- **Justification:** Credit Card Management is deeply coupled to Account Management through shared VSAM files (CARDDAT, CCXREF, ACCTDAT, CXACAIX). COCRDLIC's dual-mode browse with CXACAIX alternate index positioning is complex state management — the admin vs. user mode uses completely different VSAM access paths. COCRDUPC contains its own set of validation rules with 88-level flags similar to COACTUPC. The programs are large (total ~3,906 LOC) and contain UI logic interleaved with business rules.
- **Strangler API shape:**
  - `GET /api/cards?page={n}&size=10` (admin) or `GET /api/accounts/{id}/cards?page={n}&size=10` (user) — replaces COCRDLIC's dual-mode browse
  - `GET /api/cards/{number}` — replaces COCRDSLC
  - `PUT /api/cards/{number}` — replaces COCRDUPC (wrap initially, then rewrite validation)
- **Must co-migrate with Account domain** because CCXREF ownership is shared.

---

### 5. Transaction Processing (Online)

**Programs:** COTRN00C (699 LOC), COTRN01C (330 LOC), COTRN02C (783 LOC)
**Copybooks:** CVTRA05Y (transaction), CVACT01Y, CVACT03Y, COCOM01Y, COTRN00–02 (BMS)
**Data Stores:** TRANSACT (VSAM KSDS — 350-byte), ACCTDAT, CCXREF, CXACAIX
**Complexity:** Medium–High

**Program-Level Risk Assessment:**

| Program | LOC | Risk | Key Concern |
|---|---|---|---|
| COTRN02C | 783 | **High** | Transaction creation writes to TRANSACT AND updates ACCTDAT balance — two-file operation with no ACID guarantee. Also reads CCXREF + CXACAIX for card-to-account resolution. |
| COTRN00C | 699 | Medium | List transactions with VSAM browse/pagination. Same pattern as COCRDLIC but single-mode. |
| COTRN01C | 330 | Low | Read-only transaction view by key. Straightforward. |

**COTRN02C Transaction Creation Flow (cross-domain writes):**
```
1. Read CCXREF by card number → get account ID, customer ID
2. Read ACCTDAT by account ID → validate active, check credit limit
3. Read CXACAIX → alternate index validation
4. Generate TRAN-ID (next sequential key in TRANSACT)
5. Write new record to TRANSACT (350 bytes)
6. Update ACCT-CURR-BAL in ACCTDAT (+= transaction amount)
   ⚠️ Steps 5 and 6 are NOT atomic — if step 6 fails, orphan
   transaction exists with no balance update
```

**Cross-Domain Data Dependencies:**

| VSAM File | Access in Online Trans | Access in Batch Trans | Sharing Domains |
|---|---|---|---|
| TRANSACT | Read/Write | Read/Write | Bill Payment, Reports, Statement — **3 other domains** |
| ACCTDAT | Read/Write (balance) | Read/Write (balance) | Account (owner), Card, Bill Payment, Interest, Statement, Export — **6 other domains** |
| CCXREF | Read | Read | Account, Card, Bill Payment, Interest, Statement, Export — **6 other domains** |
| CXACAIX | Read | — | Account, Card, Bill Payment — **3 other domains** |

| Strategy | Assessment |
|---|---|
| **(a) Strangler** | Wrap list/view/add as APIs. Transaction creation (COTRN02C) updates both TRANSACT and ACCTDAT, requiring careful coordination. |
| **(b) Replatform** | Move to cloud runtime. Preserves VSAM-based transaction storage, which is the main modernization target. |
| **(c) Refactor** | Restructure for clarity. Moderate value — logic is fairly well-organized already. |
| **(d) Rewrite** | Build as an event-driven transaction service. Highest value — enables proper ACID guarantees, audit trail, scalability. |

**Recommendation: (d) Rewrite** — after Account/Card domains are stabilized via strangler.
- **Justification:** Transaction processing is the core revenue-generating capability. COTRN02C creates transactions by writing to TRANSACT VSAM and updating account balances in ACCTDAT — a two-phase operation with **no ACID guarantees** in the VSAM model (if the ACCTDAT write fails after TRANSACT is written, you have an orphan transaction). Rewriting this as a service with a relational database provides proper transactional integrity within a single DB transaction.
- **Strangler API shape (if extracted first with shared DB approach):**
  - `GET /api/transactions?cardNumber={num}&page={n}&size=10` — replaces COTRN00C browse
  - `GET /api/transactions/{tranId}` — replaces COTRN01C
  - `POST /api/transactions` — replaces COTRN02C; internally does: resolve card → validate account → check credit limit → create transaction + update balance atomically
- **If extracted first:** Must use shared PostgreSQL database (Option A from analysis) since `account-service` and `card-service` don't exist yet. Cross-domain reads become SQL JOINs rather than API calls. See DOMAIN_DECOMPOSITION.md for full extraction guide.

---

### 6. Transaction Processing (Batch)

**Programs:** CBTRN01C (494 LOC), CBTRN02C (731 LOC), CBTRN03C (649 LOC)
**Copybooks:** CVTRA05Y, CVTRA06Y (daily tran), CVTRA01Y (cat balance), CVTRA02Y (disc group), CVTRA03Y (tran type), CVTRA04Y (tran category), CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y
**Data Stores:** DALYTRAN (sequential), TRANSACT, ACCTDAT, CARDDAT, CUSTDAT, CCXREF, TCATBALF, DISCGRP, TRANCATG, TRANTYPE, DALYREJS (sequential)
**Complexity:** High — **MOST DATA-COUPLED AREA IN THE SYSTEM**

**Program-Level Risk Assessment:**

| Program | LOC | Risk | Files Touched | Key Concern |
|---|---|---|---|---|
| CBTRN02C | 731 | **Critical** | 6 (DALYTRAN, TRANSACT, CCXREF, ACCTDAT, TCATBALF, DALYREJS) | Most data-coupled program. Rejection logic based on VSAM READ return codes (status '23' = not found) rather than explicit business rules. |
| CBTRN01C | 494 | **High** | 7 (DALYTRAN, TRANSACT, CCXREF, ACCTDAT, CARDDAT, CUSTDAT, DALYREJS) | Validates card, account, AND customer before posting. Broader validation than CBTRN02C. |
| CBTRN03C | 649 | Medium | 4 (TRANSACT, CCXREF, TRANTYPE, TRANCATG) | Report generation with multi-file joins. Output format must match exactly. |

**CBTRN02C Rejection Logic (implicit — must be reverse-engineered):**
```
For each record in DALYTRAN:
  READ CCXREF by card number
    IF status = '23' (not found) → WRITE to DALYREJS (rejected)
  READ ACCTDAT by account ID (from xref)
    IF status = '23' → WRITE to DALYREJS
    IF ACCT-ACTIVE-STATUS ≠ 'Y' → WRITE to DALYREJS
  IF valid:
    WRITE to TRANSACT
    UPDATE ACCT-CURR-BAL in ACCTDAT
    UPDATE TCATBALF category balance

⚠️ The rejection reason is NOT stored — the rejected record is written
as-is to DALYREJS. In Java, rejection reasons should be added.
⚠️ Account balance update is PIC S9(10)V99 (COMP-3) — truncation
semantics, not rounding.
```

**Cross-Domain Data Dependency Matrix:**

| VSAM File | CBTRN01C | CBTRN02C | CBTRN03C | Owner Domain |
|---|---|---|---|---|
| DALYTRAN | Read | Read | — | Transaction (own) |
| TRANSACT | Write | Write | Read | Transaction (own) |
| DALYREJS | Write | Write | — | Transaction (own) |
| TCATBALF | — | Read/Write | — | Transaction (own) / Interest Calc reads |
| TRANTYPE | — | — | Read | Transaction (own) |
| TRANCATG | — | — | Read | Transaction (own) |
| ACCTDAT | Read/Write | Read/Write | — | **Account** (BC-2) |
| CARDDAT | Read | — | — | **Card** (BC-3) |
| CUSTDAT | Read | — | — | **Customer** (BC-5) |
| CCXREF | Read | Read | Read | **Account** (BC-2) |

| Strategy | Assessment |
|---|---|
| **(a) Strangler** | Difficult — batch programs are not callable via API; they are JCL-driven file processors. |
| **(b) Replatform** | Move JCL + COBOL to cloud batch runtime (AWS M2 or Micro Focus). Fastest path. |
| **(c) Refactor** | Improve COBOL structure, add better error handling. Moderate value. |
| **(d) Rewrite** | Translate to Spring Batch / modern ETL. Complex but eliminates mainframe dependency. |

**Recommendation: (b) Replatform first, then (d) Rewrite.**
- **Justification:** CBTRN02C is the most data-coupled program in the system (6 files) and CBTRN01C touches 7 files. The rejection logic is particularly dangerous: transactions are rejected based on VSAM READ status codes ('23' = not found), not explicit business rules — if the Java replacement uses SQL that returns null instead of an exception, the rejection behavior could differ subtly. The batch programs also modify account balances using COMP-3 packed decimal arithmetic with truncation semantics that must be matched exactly in `BigDecimal`.
- **Parallel-run validation is mandatory:** Run COBOL batch and Spring Batch on the same DALYTRAN input, compare: (1) posted transactions (TRANSACT records), (2) rejected transactions (DALYREJS records), (3) account balance changes (ACCTDAT diffs), (4) category balance changes (TCATBALF diffs). Zero discrepancies for 30 consecutive business days before cutover.

---

### 7. Interest Calculation & Financial Reporting

**Programs:** CBACT04C (652 LOC), CBSTM03A (924 LOC), CBSTM03B (230 LOC)
**Copybooks:** CVTRA01Y, CVTRA02Y, CVACT01Y, CVACT03Y, COSTM01, CVCUS01Y
**Data Stores:** TCATBALF, DISCGRP, CCXREF, ACCTDAT, TRANSACT, CUSTDAT
**Complexity:** High — **HIGHEST PRECISION RISK; INTENTIONALLY DIFFICULT TO MIGRATE**

**Program-Level Risk Assessment:**

| Program | LOC | Risk | Key Concern |
|---|---|---|---|
| CBSTM03A | 924 | **Critical** | Intentionally exercises hardest COBOL constructs: ALTER/GO TO (self-modifying control flow), mainframe control block addressing, COMP-3, 2D arrays, subroutine calls. Header explicitly states these are modernization test cases. |
| CBACT04C | 652 | **Critical** | Financial interest calculation with packed decimal (COMP-3). Multi-step rate lookup: CCXREF → ACCTDAT (get group ID) → DISCGRP (get rate by group+type+category) → compute interest on TCATBALF balance. |
| CBSTM03B | 230 | Medium | Subroutine called by CBSTM03A for file I/O. Reads TRANSACT, CCXREF, CUSTDAT, ACCTDAT via LINKAGE SECTION. |

**CBSTM03A Intentional Migration Challenges (from program header):**
```
* Constructs exercised (per source code comments):
*  1. Mainframe Control block addressing    — NO Java equivalent
*  2. Alter and GO TO statements            — Self-modifying control flow
*  3. COMP and COMP-3 variables             — Packed decimal precision
*  4. 2 dimensional array                   — Statement line items
*  5. Call to Subroutine                    — CBSTM03B via LINKAGE SECTION
```
ALTER/GO TO dynamically changes the target of a GO TO statement at runtime — the control flow graph is not statically determinable. Automated translation tools cannot handle this reliably. Only manual rewrite with exhaustive output comparison is safe.

**CBACT04C Interest Rate Lookup Chain (undocumented):**
```
For each CCXREF record:
  Read ACCTDAT by account ID → get ACCT-GROUP-ID
  For each TCATBALF record matching account:
    Read DISCGRP by (GROUP-ID + TRAN-TYPE-CD + TRAN-CAT-CD) → get DIS-INT-RATE
    Interest = TRAN-CAT-BAL × DIS-INT-RATE
    ⚠️ Uses PIC S9(04)V99 for rate, PIC S9(09)V99 for balance
    ⚠️ COBOL truncates intermediate result to target PIC — NOT rounding
    ⚠️ Java BigDecimal MUST use setScale(2, RoundingMode.DOWN) to match
```
Even a 1-cent discrepancy per account compounds across thousands of accounts and can trigger regulatory audit findings.

| Strategy | Assessment |
|---|---|
| **(a) Strangler** | Not applicable — batch programs with no API surface. |
| **(b) Replatform** | Keep running on cloud. Safe for interest calculations where precision matters. |
| **(c) Refactor** | Improve COBOL — but CBSTM03A was *intentionally designed* to resist refactoring. |
| **(d) Rewrite** | Translate to Java with BigDecimal. Must be validated extensively for numeric precision. |

**Recommendation: (b) Replatform first, then (d) Rewrite with extensive parallel testing.**
- **Justification:** These two programs represent the **highest financial risk** and the **hardest technical migration** in the entire codebase. CBACT04C's interest calculation combines COMP-3 arithmetic with a multi-table lookup chain where COBOL's truncation behavior (not rounding) must be replicated exactly. CBSTM03A was explicitly designed as a modernization challenge — ALTER/GO TO makes the control flow non-deterministic at compile time, and mainframe control block addressing has no Java equivalent.
- **Mandatory controls:**
  1. Replatform first to maintain business continuity (run on AWS M2)
  2. Build Java replacement using `BigDecimal` with `setScale(2, RoundingMode.DOWN)` for ALL financial fields
  3. Parallel-run for **3 full billing cycles** (not just 30 days) — compare interest charges per account to the cent
  4. Statement output diff comparison (text AND HTML) — must match character-for-character
  5. Keep COBOL version on standby for 3 additional billing cycles after cutover

---

### 8. Data Import/Export & Migration

**Programs:** CBEXPORT (582 LOC), CBIMPORT (487 LOC)
**Copybooks:** CVEXPORT, CVCUS01Y, CVACT01Y, CVACT03Y, CVTRA05Y
**Data Stores:** CUSTDAT, ACCTDAT, CCXREF, TRANSACT, EXPORT file
**Complexity:** Medium

| Strategy | Assessment |
|---|---|
| **(a) Strangler** | Wrap as data migration APIs. Possible but batch-oriented. |
| **(b) Replatform** | Keep on cloud runtime. Low value — these are migration utilities. |
| **(c) Refactor** | Clean up COBOL. Marginal value for one-time-use utilities. |
| **(d) Rewrite** | Build as modern ETL pipelines. Ideal for data migration during modernization. |

**Recommendation: (d) Rewrite** — as part of the data migration tooling.
- **Justification:** CBEXPORT and CBIMPORT are branch migration utilities that read/write a multi-record export file format with different record types (customer, account, xref, transaction) and validation checksums. These programs are natural candidates for rewriting as modern ETL jobs (Spring Batch, Apache Spark, or AWS Glue) that will be needed during the modernization itself — to migrate data from VSAM to relational databases. The new ETL tooling serves double duty: it replaces these programs AND provides the data migration pipeline.

---

### 9. Account Data Utilities (Batch Readers)

**Programs:** CBACT01C (430 LOC), CBACT02C (178 LOC), CBACT03C (178 LOC), CBCUS01C (178 LOC)
**Copybooks:** CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y, CODATECN
**Data Stores:** ACCTDAT, CARDDAT, CCXREF, CUSTDAT
**Complexity:** Low

| Strategy | Assessment |
|---|---|
| **(a) Strangler** | Overkill — simple read-and-print utilities. |
| **(b) Replatform** | Minimal value for utility programs. |
| **(c) Refactor** | Not needed — programs are simple. |
| **(d) Rewrite** | Replace with database queries or CLI tools once data is in RDBMS. |

**Recommendation: (d) Rewrite** — replace with database queries or admin tools.
- **Justification:** These are simple sequential-read-and-display utilities used for operational monitoring. CBACT01C is slightly more complex (430 LOC) as it produces multiple output formats including variable-length records and array-based output. Once the underlying data is in a relational database, these programs become unnecessary — a SQL query or admin dashboard replaces them entirely. No special migration effort needed; they retire naturally when the data stores migrate.

---

### 10. Bill Payment

**Programs:** COBIL00C (572 LOC)
**Copybooks:** CVACT01Y, CVACT03Y, CVTRA05Y, COCOM01Y, COBIL00 (BMS)
**Data Stores:** TRANSACT, ACCTDAT, CXACAIX
**Complexity:** Medium

| Strategy | Assessment |
|---|---|
| **(a) Strangler** | Expose as a payment API. Clean business operation with well-defined inputs/outputs. |
| **(b) Replatform** | Move CICS program to cloud. Preserves UI constraints. |
| **(c) Refactor** | Moderate benefit. |
| **(d) Rewrite** | Build as a payment service API. Natural microservice boundary. |

**Recommendation: (a) Strangler Pattern** — Expose as a Payment API, then rewrite.
- **Justification:** Bill Payment has a clean functional boundary — it reads an account balance, creates a payment transaction, and updates the account balance. However, it shares data stores with Transaction Processing and Account Management (TRANSACT, ACCTDAT, CXACAIX). The strangler pattern allows exposing this as a modern payment API while the underlying data access still goes through VSAM, then migrating to direct database access once the Account domain's data store is modernized.

---

### 11. Transaction Reports (Online Trigger)

**Programs:** CORPT00C (649 LOC)
**Copybooks:** CVTRA05Y, COCOM01Y, CORPT00 (BMS)
**Data Stores:** TRANSACT (read), Extra-partition TDQ (write — triggers batch JCL)
**Complexity:** Medium

| Strategy | Assessment |
|---|---|
| **(a) Strangler** | Expose as a report-generation API endpoint. |
| **(b) Replatform** | Move to cloud. TDQ/internal reader mechanism is mainframe-specific. |
| **(c) Refactor** | Marginal value. |
| **(d) Rewrite** | Build as an async report service triggered by API call. |

**Recommendation: (d) Rewrite** — as an async report generation service.
- **Justification:** CORPT00C is unique in that it bridges online and batch processing — it uses an extra-partition TDQ to submit batch JCL from within a CICS transaction. This mechanism (TDQ → internal reader → JCL submission) is deeply mainframe-specific and has no direct cloud equivalent. Rewriting as an async report service (API call triggers a background job) is the cleanest approach. This pairs with the batch Transaction Processing rewrite.

---

### 12. Optional: Authorization Module (IMS/DB2/MQ)

**Programs:** COPAUA0C, COPAUS0C, COPAUS1C, COPAUS2C (CICS), CBPAUP0C (batch), PAUDBLOD, PAUDBUNL, DBUNLDGS (utilities)
**Copybooks:** CIPAUDTY, CIPAUSMY, CCPAURQY, CCPAURLY, CCPAUERY, COPAU00–01 (BMS), IMSFUNCS, PCBs
**Data Stores:** IMS DB (DBPAUTP0, DBPAUTX0), DB2 tables (AUTHFRDS), MQ queues
**Complexity:** Very High

| Strategy | Assessment |
|---|---|
| **(a) Strangler** | Very difficult — IMS/DB2/MQ integration creates deep platform coupling. |
| **(b) Replatform** | Requires IMS and MQ runtime support in cloud environment. AWS M2 supports this. |
| **(c) Refactor** | Marginal value — complexity is inherent in IMS/MQ integration. |
| **(d) Rewrite** | Build as a modern authorization service with RDBMS + message queue (Kafka/SQS). |

**Recommendation: (d) Rewrite** — but defer to a later phase.
- **Justification:** The Authorization module is the most platform-dependent component, requiring IMS hierarchical database, DB2, and MQ — three middleware dependencies beyond what the core application needs. Replatforming requires provisioning all three middleware services in the cloud. Rewriting as a modern authorization service (Spring Boot + PostgreSQL/Aurora + Kafka/SQS) eliminates all three dependencies. However, the effort is substantial and this module is optional/additive, so it should be deferred until the core CardDemo migration is complete.

---

### 13. Optional: Transaction Type Management (DB2)

**Programs:** COTRTLIC (CICS), COTRTUPC (CICS), COBTUPDT (batch)
**Copybooks:** CSDB2RPY, CSDB2RWY, COTRTLI/COTRTUP (BMS), DCLTRTYP, DCLTRCAT (DCL)
**Data Stores:** DB2 tables (TRNTYPE, TRNTYCAT)
**Complexity:** Medium

| Strategy | Assessment |
|---|---|
| **(a) Strangler** | Wrap DB2 CRUD operations with APIs. Relatively clean since DB2 access is already SQL-based. |
| **(b) Replatform** | Move CICS+DB2 to cloud. Requires DB2 runtime. |
| **(c) Refactor** | Minimal value. |
| **(d) Rewrite** | Build as a reference data microservice with REST API. |

**Recommendation: (d) Rewrite** — as a Reference Data microservice.
- **Justification:** Transaction Type Management is already SQL-based (DB2 cursors, INSERT/UPDATE/DELETE) making it the easiest domain to translate to a modern RDBMS-backed service. The DCL copybooks (DCLTRTYP, DCLTRCAT) essentially define the database schema, providing a direct mapping to JPA entities. This is a good "quick win" for the modernization team to build confidence.

---

## Strategy Summary Matrix

| Functional Area | LOC | Recommendation | Phase | Risk |
|---|---|---|---|---|
| Authentication & Security | 260 | Rewrite | 1 | Low |
| User Administration | 2,055 | Rewrite | 1 | Low |
| Transaction Type Mgmt (DB2) | ~800 | Rewrite | 1 | Low |
| Account Data Utilities | 964 | Retire/Rewrite | 1 | Low |
| Data Import/Export | 1,069 | Rewrite (ETL) | 2 | Low |
| Transaction Reports (Online) | 649 | Rewrite | 2 | Medium |
| Account Management | 5,177 | Strangler → Rewrite | 2–3 | High |
| Credit Card Management | 3,906 | Strangler → Rewrite | 2–3 | High |
| Bill Payment | 572 | Strangler → Rewrite | 3 | Medium |
| Transaction Processing (Online) | 1,812 | Rewrite | 3 | High |
| Transaction Processing (Batch) | 1,874 | Replatform → Rewrite | 3–4 | High |
| Interest Calc & Reporting | 1,806 | Replatform → Rewrite | 4 | Very High |
| Authorization (IMS/DB2/MQ) | ~2,500 | Rewrite (deferred) | 5 | Very High |

---

## Key Decision Factors

### Business Logic Complexity (Ranked by Migration Difficulty)

| Rank | Program | LOC | Key Risk | Documentation Level |
|---|---|---|---|---|
| 1 | COACTUPC | 4,236 | 25+ undocumented validation rules (SSN/IRS exclusions, phone format via REDEFINES, CEEDAYS date validation) | **None** — rules in 88-level conditions |
| 2 | CBSTM03A | 924 | Intentionally difficult: ALTER/GO TO, mainframe control blocks, COMP-3, 2D arrays | **Minimal** — header lists constructs |
| 3 | CBACT04C | 652 | COMP-3 interest calculation with multi-step rate lookup chain; truncation (not rounding) semantics | **None** — rate lookup chain is implicit |
| 4 | CBTRN02C | 731 | 6-file batch posting; rejection based on VSAM status codes, not explicit business rules | **None** — rejection is implicit |
| 5 | COCRDLIC | 1,459 | Dual-mode VSAM browse (admin vs. user) with CXACAIX alternate index; EBCDIC collation dependencies | **None** |
| 6 | COCRDUPC | 1,560 | Card validation rules with BMS-interleaved logic; similar pattern to COACTUPC | **None** |
| 7 | CBTRN01C | 494 | 7-file batch posting with card, account, AND customer validation | **None** |
| 8 | COTRN02C | 783 | Non-atomic two-file write (TRANSACT + ACCTDAT balance update) | **None** |
| 9 | COBIL00C | 572 | Payment interaction with pending transactions; unclear balance calculation timing | **None** |
| 10 | All others | Various | Low complexity — straightforward CRUD, reads, or utilities | Low |

### Data Coupling (VSAM File Access Heatmap)

| VSAM File | Owner | Total Accessor Programs | Accessor Domains | Extraction Difficulty |
|---|---|---|---|---|
| ACCTDAT | Account | 15+ | Account, Card, Trans (online+batch), Bill Pay, Interest, Statement, Export | **Critical** — most shared file |
| CCXREF | Account | 10+ | Account, Card, Trans, Bill Pay, Interest, Statement, Export | **Critical** |
| TRANSACT | Transaction | 8+ | Transaction, Bill Pay, Reports, Statement, Export | **High** |
| CARDDAT | Card | 5+ | Card, Trans (batch), Interest Calc | **Medium** |
| CUSTDAT | Customer | 4+ | Customer, Trans (batch), Statement, Export/Import | **Medium** |
| TCATBALF | Transaction | 3 | Transaction (batch), Interest Calc | **Low** |
| USRSEC | Identity | 6 | Identity only | **Isolated** |
| DISCGRP | Reference | 1 writer + 1 reader | Reference, Interest Calc | **Isolated** |
| TRANTYPE | Reference | 1 writer + 1 reader | Reference, Transaction (report) | **Isolated** |
| TRANCATG | Reference | 1 writer + 1 reader | Reference, Transaction (report) | **Isolated** |

### Team Skill Availability
- **Java/Spring Boot:** Assumed available for the "to-Java" target
- **COBOL (critical path):** Needed throughout Phases 1–4 for: (a) reverse-engineering COACTUPC validation rules, (b) maintaining replatformed runtime, (c) diagnosing parallel-run discrepancies, (d) understanding COMP-3/packed decimal behavior
- **Mainframe Ops (JCL/CICS/VSAM):** Critical for Phase 0 replatform, Phase 2 dual-write sync, and Phase 4 batch sequencing
- **Risk:** COBOL skill attrition during the 48–60 week migration is the #8 risk in the RISK_REGISTER — knowledge capture must happen before Phase 1

### Risk Tolerance
- **Low-risk areas first:** Authentication, User Admin, Reference Data, Utilities — all have isolated data stores
- **High-risk areas last:** Financial calculations (CBACT04C — precision risk), batch transaction posting (CBTRN02C — 6-file coupling), statement generation (CBSTM03A — ALTER/GO TO)
- **Parallel-run validation:** Essential for ALL financial processing domains. Minimum durations: 30 days for transaction posting, 3 billing cycles for interest calculation, character-for-character output comparison for statements
