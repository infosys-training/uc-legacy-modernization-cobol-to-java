# CardDemo Modernization Blueprint

> Strategy evaluation for each functional area of the CardDemo COBOL estate (44 programs, ~27,350 LOC).

---

## 1. Strategy Definitions

| Strategy | Description | Best When |
|----------|-------------|-----------|
| **Strangler** | Wrap existing COBOL with APIs; incrementally replace modules while both run in parallel | High data coupling, need for zero-downtime migration, complex business rules that need time to validate |
| **Replatform** | Keep COBOL source, move to cloud runtime (e.g., Micro Focus, GnuCOBOL on Linux) | Low business value from rewriting, stable code, limited team capacity |
| **Refactor** | Restructure COBOL for maintainability without language change | Code will remain COBOL long-term, only needs improved structure |
| **Rewrite** | Translate to modern language (Java/Spring Boot) with new architecture | Clear business rules, self-contained modules, team has Java expertise, high ROI from modernization |

### Evaluation Criteria

Each area is scored on four factors:

| Factor | Weight | Description |
|--------|--------|-------------|
| Business Logic Complexity | 30% | How dense and intertwined is the logic? Higher → favor Strangler over Rewrite |
| Data Coupling | 25% | How many shared files/tables? Higher → harder to extract cleanly |
| Team Skill Availability | 20% | Java/Spring expertise available? Higher → favor Rewrite |
| Risk Tolerance | 25% | Can the business tolerate temporary dual systems? Lower → favor Replatform |

---

## 2. Functional Area Analysis

### 2.1 Account Management

**Programs:** COACTUPC (4,236 LOC), COACTVWC (941 LOC), COACCT01 (620 LOC) — **5,797 LOC total**

| Factor | Assessment | Score |
|--------|-----------|-------|
| Business Logic Complexity | **Very High** — COACTUPC is the #1 hotspot (185 branching statements, 58 copybooks). Exhaustive field validation: date (century/month/day/leap year), SSN, phone (NANPA), state code, ZIP prefix, amount limits. Uses 3× COPY REPLACING macros. | 9/10 |
| Data Coupling | **High** — Touches 3 VSAM files in a single operation (ACCTFILE, CARDXREF, CUSTFILE). ACCTFILE is written by 5 programs across 4 contexts. | 8/10 |
| Team Skill Availability | Java/Spring team can handle this, but validation extraction requires deep COBOL reading | 6/10 |
| Risk Tolerance | **Low** — Account data is the core financial record; errors here have direct monetary impact | 3/10 |

**Recommended Strategy: Strangler Pattern**

**Justification:** COACTUPC alone contains 15.5% of all COBOL in the estate and the densest validation logic. A rewrite carries high regression risk because the 185 branching paths create an enormous test matrix. The Strangler approach allows:
1. Wrap ACCTFILE access with a REST API facade first
2. Extract validation rules into a shared Java `AccountValidationService` while COBOL still handles production traffic
3. Run shadow mode (COBOL primary, Java shadow) for 2+ weeks to validate parity
4. Cut over only after output comparison passes at >99.99% match rate
5. COACTVWC (read-only) can be rewritten directly once the API facade exists

**Temporary bridge needed:** API gateway routing CICS transactions to either COBOL or Java based on feature flags. Dual-write to VSAM + PostgreSQL with SQS FIFO sync during transition.

---

### 2.2 Card Management

**Programs:** COCRDLIC (1,459 LOC), COCRDSLC (887 LOC), COCRDUPC (1,560 LOC) — **3,906 LOC total**

| Factor | Assessment | Score |
|--------|-----------|-------|
| Business Logic Complexity | **High** — COCRDUPC has 163 branching statements (2nd highest density). COCRDLIC implements the VSAM browse pattern (STARTBR/READNEXT/READPREV/ENDBR) used in 5+ programs. | 7/10 |
| Data Coupling | **Medium** — Reads CARDFILE + CARDXREF. CARDFILE has only 2 writers (COCRDUPC, CBIMPORT). | 5/10 |
| Team Skill Availability | Standard CRUD patterns; Spring Data pagination is a direct analog for the browse pattern | 8/10 |
| Risk Tolerance | **Medium** — Card updates are less frequent than transaction posting; errors are correctable | 6/10 |

**Recommended Strategy: Rewrite**

**Justification:** The card suite is a clean CRUD domain with moderate coupling. CARDFILE has only 2 writers, making dual-write manageable. The VSAM browse pattern (STARTBR/READNEXT/READPREV/ENDBR) maps directly to Spring Data `Pageable`/`Slice<>`. Rewriting establishes the `AbstractBrowseController<T>` pattern reusable by transaction list, user list, and authorization list screens.

**Key migration action:** Create the browse abstraction first — it's the single most reusable pattern across the estate.

---

### 2.3 Transaction Processing

**Programs:** COTRN00C (699 LOC), COTRN01C (330 LOC), COTRN02C (783 LOC), CBTRN01C (494 LOC), CBTRN02C (731 LOC), COBIL00C (572 LOC) — **3,609 LOC total**

| Factor | Assessment | Score |
|--------|-----------|-------|
| Business Logic Complexity | **Medium-High** — CBTRN02C (transaction posting) updates 4 files atomically (TRANSACT, ACCTFILE, TCATBALF, DALYREJS). COTRN02C validates cards and generates timestamps. | 7/10 |
| Data Coupling | **Very High** — TRANSACT VSAM is written by 4 programs, read by 5. CBTRN02C also updates ACCTFILE and TCATBALF. Cross-domain coupling with Account. | 9/10 |
| Team Skill Availability | Batch posting logic maps to Spring Batch; online CRUD is standard | 7/10 |
| Risk Tolerance | **Low** — Transaction posting directly affects account balances; errors compound | 3/10 |

**Recommended Strategy: Strangler Pattern (online) + Rewrite (batch)**

**Justification:** Split approach because the online and batch components have different risk profiles:
- **Online (COTRN00C/01C/02C, COBIL00C):** Strangler — wrap TRANSACT access with an API, incrementally replace screens. COBIL00C (bill payment) updates ACCTFILE and must coordinate with Account Management migration.
- **Batch (CBTRN01C, CBTRN02C):** Rewrite as Spring Batch jobs — self-contained sequential file processing with clear input/output contracts. Validate by comparing VSAM output files byte-for-byte.

**Critical constraint:** CBTRN02C must not migrate until ACCTFILE dual-write is stable (depends on Account Management Strangler being in place).

---

### 2.4 Transaction Type Management (DB2)

**Programs:** COTRTLIC (2,098 LOC), COTRTUPC (1,702 LOC), COBTUPDT (237 LOC) — **4,037 LOC total**

| Factor | Assessment | Score |
|--------|-----------|-------|
| Business Logic Complexity | **Medium** — DB2 cursor-based pagination, cascading delete logic. Complex screen state management but well-contained. | 6/10 |
| Data Coupling | **Low** — Reads/writes only DB2 transaction type and category tables. No VSAM file dependencies. | 2/10 |
| Team Skill Availability | **High** — Already uses SQL; translates almost directly to JPA | 9/10 |
| Risk Tolerance | **High** — Reference data, not transactional. Errors are easily correctable. | 8/10 |

**Recommended Strategy: Rewrite**

**Justification:** Ideal first-mover candidate. Already uses DB2 SQL, so the database layer requires minimal translation (DB2 → PostgreSQL/H2). Low coupling — no VSAM dependencies. The EXEC SQL statements map directly to Spring Data JPA repositories. Cascading delete in COTRTUPC becomes `@OneToMany(cascade = CascadeType.ALL)`. Proves the DB2 migration pattern for the rest of the estate.

---

### 2.5 Authorization (IMS/DB2/MQ)

**Programs:** COPAUA0C (1,026 LOC), COPAUS0C (1,032 LOC), COPAUS1C (604 LOC), COPAUS2C (244 LOC), CBPAUP0C (386 LOC), PAUDBLOD (369 LOC), PAUDBUNL (317 LOC), DBUNLDGS (366 LOC) — **4,344 LOC total**

| Factor | Assessment | Score |
|--------|-----------|-------|
| Business Logic Complexity | **High** — COPAUA0C spans 3 subsystems (CICS + MQ + IMS DL/I). IMS hierarchical navigation (GU, GN, GNP) has no direct SQL equivalent. | 8/10 |
| Data Coupling | **Medium** — IMS data is isolated from main VSAM files. MQ queues are a clear integration boundary. COPAUS0C reads ACCTFILE and CUSTFILE for display context. | 5/10 |
| Team Skill Availability | **Low** — IMS DL/I expertise is rare. MQ-to-SQS translation requires message format mapping (EBCDIC → UTF-8 JSON). | 3/10 |
| Risk Tolerance | **Medium** — Authorization is real-time but the IMS data can be replayed from flat files (PAUDBLOD/PAUDBUNL) | 5/10 |

**Recommended Strategy: Strangler Pattern**

**Justification:** The 3-subsystem span (CICS + MQ + IMS) makes a direct rewrite high-risk. The Strangler approach:
1. First, migrate IMS hierarchical data to 2 relational tables (AUTH_SUMMARY, AUTH_DETAIL) using PAUDBUNL to export and a Java loader to import
2. Replace MQ with SQS adapter — build a translation layer (COBOL record → JSON) and run both in parallel for 1 week
3. Replace COPAUS0C/1C/2C (CICS LINK chain) as a unit — they share COMMAREA and must migrate together
4. COPAUA0C (MQ adapter) migrates last once both IMS and MQ replacements are proven

**Must migrate as a unit:** COPAUS0C → COPAUS1C → COPAUS2C (EXEC CICS LINK chain, shared COMMAREA).

---

### 2.6 User Security & Administration

**Programs:** COSGN00C (260 LOC), COADM01C (288 LOC), COMEN01C (308 LOC), COUSR00C (695 LOC), COUSR01C (299 LOC), COUSR02C (414 LOC), COUSR03C (359 LOC) — **2,623 LOC total**

| Factor | Assessment | Score |
|--------|-----------|-------|
| Business Logic Complexity | **Low** — Simple CRUD on USRSEC file. Sign-on is basic user/password check. Menu hubs are just XCTL routing tables. | 3/10 |
| Data Coupling | **Low** — USRSEC is an isolated VSAM file with 6 programs, no cross-domain writes. | 2/10 |
| Team Skill Availability | **High** — Spring Security + JWT is well-understood by Java teams | 9/10 |
| Risk Tolerance | **High** — User management is low-frequency admin; sign-on can use parallel auth during transition | 8/10 |

**Recommended Strategy: Rewrite**

**Justification:** Quick win. Low complexity (3/10), low coupling (2/10), and the current implementation has a **critical security vulnerability** — passwords stored in plain text (PIC X(08) in CSUSR01Y). Migration to Spring Security with bcrypt hashing is not just modernization but a security fix. COADM01C and COMEN01C (menu hubs) are eliminated entirely in a REST API architecture — they become API routes.

**Security note:** Do NOT migrate plain-text passwords. Require password reset for all users at cutover.

---

### 2.7 Reporting & Statements

**Programs:** CBSTM03A (924 LOC), CBSTM03B (230 LOC), CBTRN03C (649 LOC), CORPT00C (649 LOC) — **2,452 LOC total**

| Factor | Assessment | Score |
|--------|-----------|-------|
| Business Logic Complexity | **Medium** — CBSTM03A has the highest I/O count in the estate (115 operations). Statement generation logic reads 4 files and produces text + HTML output. | 6/10 |
| Data Coupling | **Medium** — Reads ACCTFILE, CUSTFILE, XREFFILE, TRANSACT (all shared). But writes only to isolated statement/report output files. | 5/10 |
| Team Skill Availability | **High** — Spring Batch + Thymeleaf/JasperReports is standard Java stack | 9/10 |
| Risk Tolerance | **High** — Reports are read-only outputs; errors don't corrupt data. Easy to validate by comparing output files. | 9/10 |

**Recommended Strategy: Rewrite**

**Justification:** **Best candidate for first migration (pilot).** Self-contained batch with no CICS, clear file inputs and file outputs, and easy validation (compare output files character-by-character). CBSTM03A→CBSTM03B CALL dependency is simple (caller/callee). Proves the Spring Batch pattern for the rest of the batch estate. CORPT00C (CICS report request screen) submits batch JCL — in the new architecture, this becomes a REST endpoint that triggers a Spring Batch job.

---

### 2.8 Data Migration Tools

**Programs:** CBEXPORT (582 LOC), CBIMPORT (487 LOC) — **1,069 LOC total**

| Factor | Assessment | Score |
|--------|-----------|-------|
| Business Logic Complexity | **Low** — Sequential read/write with record-type dispatching | 3/10 |
| Data Coupling | **High** — CBEXPORT reads ALL 5 core VSAM files; CBIMPORT writes to all | 7/10 |
| Team Skill Availability | High — straightforward file processing | 9/10 |
| Risk Tolerance | **High** — Migration tools, not production processing | 9/10 |

**Recommended Strategy: Rewrite → Retire**

**Justification:** Rewrite as a one-time Java utility for the VSAM-to-PostgreSQL data migration. Once all data is migrated to the relational database, these tools are retired. They serve no purpose in the target architecture.

---

## 3. Strategy Summary Matrix

| Functional Area | LOC | Strategy | Risk | Priority | Depends On |
|----------------|-----|----------|------|----------|------------|
| Reporting & Statements | 2,452 | **Rewrite** | Low | **Phase 1 (Pilot)** | — |
| Transaction Type (DB2) | 4,037 | **Rewrite** | Low | **Phase 1** | — |
| User Security & Admin | 2,623 | **Rewrite** | Low | **Phase 2** | — |
| Card Management | 3,906 | **Rewrite** | Medium | **Phase 2** | — |
| Account Management | 5,797 | **Strangler** | High | **Phase 3** | Card Mgmt browse pattern |
| Transaction Processing | 3,609 | **Strangler + Rewrite** | High | **Phase 3** | Account Mgmt API facade |
| Authorization (IMS/MQ) | 4,344 | **Strangler** | High | **Phase 4** | MQ + IMS replacements |
| Data Migration Tools | 1,069 | **Rewrite → Retire** | Low | **Phase 5** | All data migrated |

### Why Not Replatform or Refactor?

- **Replatform** is not recommended for any area because CardDemo has active business logic that benefits from modernization (REST APIs, modern security, cloud-native patterns). The COBOL runtime itself (CICS, VSAM, IMS) is the constraint — keeping COBOL on a cloud runtime doesn't address the fundamental architectural limitations.
- **Refactor** is not recommended because the goal is language migration to Java. Restructuring COBOL would be effort spent on a dead-end path. The one exception: if the migration timeline extends beyond 12 months, refactoring COACTUPC to separate validation from screen logic within COBOL could reduce interim maintenance cost.

---

## 4. Cross-Cutting Concerns

### 4.1 Shared Validation Library

Extract from COACTUPC + CSLKPCDY + CSUTLDPY into `carddemo-validation`:
- `DateValidator` — CCYYMMDD format (century, month, day, leap year, DOB-in-past)
- `PhoneValidator` — NANPA area code lookup (~350 codes)
- `AddressValidator` — State code (50 + DC/territories), ZIP prefix cross-reference
- `SSNValidator` — 9-digit numeric format

### 4.2 Data Access Layer

Replace VSAM KSDS with PostgreSQL + Spring Data JPA:
- 10 VSAM files → 10 database tables (matching VSAM key structures)
- VSAM STARTBR/READNEXT/READPREV/ENDBR → Spring Data `Pageable` with cursor-based pagination
- VSAM READ/REWRITE → JPA `findById()` + `save()` with optimistic locking

### 4.3 Monetary Arithmetic

**MANDATORY:** All COBOL PIC S9(n)V99 fields → `java.math.BigDecimal(precision, scale=2)` with `RoundingMode.HALF_UP`. Never use `double` or `float` for monetary values. Create `MoneyUtils` utility class.

### 4.4 COMMAREA → API Contracts

COCOM01Y (CICS COMMAREA) becomes REST API DTOs:
- `CDEMO-GENERAL-INFO` → JWT claims (user ID, user type)
- `CDEMO-CUSTOMER-INFO` → `CustomerDTO`
- `CDEMO-ACCOUNT-INFO` → `AccountDTO`
- `CDEMO-CARD-INFO` → `CardDTO`
- Navigation state (FROM-PROGRAM, TO-PROGRAM) → eliminated; REST is stateless
