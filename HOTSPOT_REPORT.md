# Hotspot Report — CardDemo COBOL Estate

> **Total Programs:** 44 | **Total LOC:** 30,175 | **Average LOC:** 686

---

## 1. Lines of Code — Top 10

| Rank | Program | LOC | Classification | Sub-app |
|------|---------|-----|---------------|---------|
| 1 | COACTUPC.cbl | 4,236 | Online (CICS) | Main |
| 2 | COTRTLIC.cbl | 2,098 | Online (CICS+DB2) | Transaction Type DB2 |
| 3 | COTRTUPC.cbl | 1,702 | Online (CICS+DB2) | Transaction Type DB2 |
| 4 | COCRDUPC.cbl | 1,560 | Online (CICS) | Main |
| 5 | COCRDLIC.cbl | 1,459 | Online (CICS) | Main |
| 6 | COPAUS0C.cbl | 1,032 | Online (CICS+IMS) | Auth IMS/DB2/MQ |
| 7 | COPAUA0C.cbl | 1,026 | Online (CICS+IMS+DB2+MQ) | Auth IMS/DB2/MQ |
| 8 | COACTVWC.cbl | 941 | Online (CICS) | Main |
| 9 | CBSTM03A.CBL | 924 | Batch | Main |
| 10 | COCRDSLC.cbl | 887 | Online (CICS) | Main |

**Observation:** 8 of the top 10 are CICS online programs. All 8 programs exceeding 1,000 LOC are online. The batch programs are generally simpler and shorter.

---

## 2. Number of Unique Copybooks Referenced — Top 10

Counted from actual `COPY copybook-name` statements (de-duplicated). Excludes CICS system copybooks (DFHAID, DFHBMSCA) from the counts below since they are universal.

| Rank | Program | Unique Business Copybooks | Total COPY Statements (incl. system + REPLACING) | Notes |
|------|---------|--------------------------|------------------------------------------------|-------|
| 1 | COACTUPC.cbl | 18 | 113+ | Includes CSSETATY used 113+ times via COPY REPLACING |
| 2 | COTRN02C.cbl | 12 | 24 | Transaction add with type/category validation |
| 3 | COACTVWC.cbl | 11 | 22 | Account view with multi-file lookup |
| 4 | COCRDSLC.cbl | 10 | 22 | Card view with customer/account lookup |
| 5 | COCRDUPC.cbl | 10 | 22 | Card update |
| 6 | COBIL00C.cbl | 9 | 18 | Bill payment |
| 7 | COTRTLIC.cbl | 9 | 18 | Transaction type list (DB2) |
| 8 | COTRTUPC.cbl | 9 | 20 | Transaction type update (DB2) |
| 9 | COTRN00C.cbl | 9 | 18 | Transaction list |
| 10 | CBTRN03C.cbl | 5 | 10 | Transaction report generation |

**Observation:** COACTUPC dwarfs all others with its COPY REPLACING pattern — the 113+ `COPY CSSETATY` statements each set screen attributes for individual BMS fields.

---

## 3. Number of I/O Operations — Top 10

Counted: EXEC CICS READ/WRITE/REWRITE/DELETE/STARTBR/READNEXT/READPREV/ENDBR, EXEC SQL, file READ/WRITE, EXEC DLI calls, MQ operations.

| Rank | Program | I/O Count | I/O Types | Notes |
|------|---------|-----------|-----------|-------|
| 1 | CBSTM03A.CBL | 115 | VSAM READ (XREF, CUST, ACCT), STARTBR/READNEXT/ENDBR (TRNX), Sequential WRITE (STMT, HTML) | Highest I/O — statement generation iterates all cards |
| 2 | COACTUPC.cbl | 28 | CICS READ/REWRITE (ACCT, CARD, CUST, XREF), STARTBR/READNEXT/READPREV/ENDBR | Multi-file update with browse |
| 3 | COPAUA0C.cbl | 22 | IMS GU/SCHD/TERM, DB2 INSERT, MQ MQOPEN/MQGET/MQPUT1 | Spans 3 subsystems |
| 4 | COACTVWC.cbl | 18 | CICS READ (ACCT, CUST, XREF), STARTBR/READNEXT/READPREV/ENDBR (CARD) | Multi-file read with browse |
| 5 | COCRDLIC.cbl | 14 | CICS STARTBR/READNEXT/READPREV/ENDBR (CARD), READ | Paginated card browse |
| 6 | CBTRN02C.cbl | 13 | Sequential READ (DALYTRAN), VSAM READ/WRITE/REWRITE (TRANSACT, ACCT, XREF, TCATBALF) | Core batch posting |
| 7 | COPAUS0C.cbl | 12 | IMS GU/GNP, CICS SEND/RECEIVE | Auth summary browse |
| 8 | CBTRN03C.cbl | 11 | Sequential READ (TRANFILE, DATEPARM), VSAM READ (XREF, TRANTYPE, TRANCATG), WRITE (TRANREPT) | Report generation |
| 9 | COCRDUPC.cbl | 10 | CICS READ/REWRITE (CARD, ACCT), SEND/RECEIVE | Card update |
| 10 | COTRN02C.cbl | 10 | CICS WRITE (TRANSACT), READ (XREF, CARD, TRANTYPE, TRANCATG) | Transaction add |

---

## 4. Business Logic Density — Top 10

Composite metric: EVALUATE + IF + COMPUTE + PERFORM VARYING counts.

| Rank | Program | EVALUATE | IF | COMPUTE | PERFORM VARYING | Total | Notes |
|------|---------|----------|-----|---------|-----------------|-------|-------|
| 1 | COACTUPC.cbl | 28 | 164 | 4 | 5 | 201 | Exhaustive field validation; nested IF depth ~5 |
| 2 | COCRDUPC.cbl | 16 | 72 | 0 | 0 | 88 | Card update validation |
| 3 | COTRTLIC.cbl | 18 | 48 | 2 | 2 | 70 | DB2 cursor pagination with EVALUATE |
| 4 | COTRTUPC.cbl | 14 | 42 | 0 | 2 | 58 | DB2 CRUD with cascading deletes |
| 5 | COACTVWC.cbl | 18 | 33 | 4 | 2 | 57 | Account view with multi-entity lookup |
| 6 | COPAUS0C.cbl | 8 | 33 | 0 | 0 | 41 | IMS navigation logic |
| 7 | COCRDLIC.cbl | 10 | 26 | 0 | 2 | 38 | Paginated browse with direction logic |
| 8 | CBTRN02C.cbl | 8 | 25 | 2 | 0 | 35 | Transaction posting with balance updates |
| 9 | CORPT00C.cbl | 6 | 14 | 2 | 4 | 26 | Report JCL construction with TDQ writes |
| 10 | COUSR00C.cbl | 16 | 25 | 4 | 2 | 47 | User list with paginated browse |

---

## 5. Inter-Program Dependencies — Top 10

Counted: outgoing CALLs + XCTL + LINK + programs that call this one (inbound).

| Rank | Program | Outgoing | Inbound | Total | Details |
|------|---------|----------|---------|-------|---------|
| 1 | COMEN01C | 11 | 12 | 23 | XCTLs to 11 function programs; called back by 10 + COSGN00C + COADM01C |
| 2 | COADM01C | 6 | 7 | 13 | XCTLs to 6 admin programs; called by COSGN00C + 6 returns |
| 3 | COACTUPC | 2 | 2 | 4 | XCTL back to COMEN01C; calls CSUTLDTC; called from COMEN01C |
| 4 | CBSTM03A | 1 | 1 | 2 | CALLs CBSTM03B; called from CREASTMT JCL |
| 5 | COPAUS0C | 1 | 2 | 3 | XCTLs to COPAUS1C; called from COMEN01C + COPAUS2C |
| 6 | COPAUS1C | 1 | 1 | 2 | XCTLs to COPAUS2C; called from COPAUS0C |
| 7 | COPAUS2C | 1 | 1 | 2 | XCTLs to COPAUS0C; called from COPAUS1C |
| 8 | COSGN00C | 2 | 0 | 2 | XCTLs to COMEN01C or COADM01C; entry point (no caller) |
| 9 | CBACT01C | 1 | 1 | 2 | CALLs COBDATFT; called from READACCT JCL |
| 10 | COTRTLIC | 1 | 1 | 2 | XCTL back to COADM01C; called from COADM01C |

---

## 6. Modernization Priority Matrix

### 6.1 Scoring Criteria (1–5 scale)

| Dimension | 1 (Low) | 3 (Medium) | 5 (High) |
|-----------|---------|------------|----------|
| **Complexity** | < 200 LOC, simple flow | 500–1000 LOC, moderate branching | > 1500 LOC, deep nesting, multiple patterns |
| **Business Criticality** | Utility/diagnostic | Data maintenance | Core business process (transactions, accounts) |
| **Dependency Count** | 0–1 dependencies | 2–4 dependencies | 5+ dependencies or hub program |
| **I/O Diversity** | Single file type | 2–3 file types | 4+ types (VSAM + DB2 + IMS + MQ) |

### 6.2 Program Scores

| Program | LOC | Complexity | Business Criticality | Dependency Count | I/O Diversity | **Total (20)** |
|---------|-----|-----------|---------------------|-----------------|---------------|----------------|
| **COACTUPC.cbl** | 4,236 | 5 | 5 | 3 | 4 | **17** |
| **CBSTM03A.CBL** | 924 | 4 | 5 | 2 | 4 | **15** |
| **CBTRN02C.cbl** | 731 | 4 | 5 | 2 | 4 | **15** |
| **COPAUA0C.cbl** | 1,026 | 4 | 4 | 2 | 5 | **15** |
| **COCRDLIC.cbl** | 1,459 | 4 | 4 | 3 | 3 | **14** |
| COTRTLIC.cbl | 2,098 | 5 | 3 | 2 | 3 | 13 |
| COTRTUPC.cbl | 1,702 | 5 | 3 | 2 | 3 | 13 |
| COCRDUPC.cbl | 1,560 | 4 | 4 | 2 | 3 | 13 |
| COACTVWC.cbl | 941 | 3 | 4 | 3 | 3 | 13 |
| COSGN00C.cbl | 260 | 2 | 5 | 2 | 2 | 11 |
| COBIL00C.cbl | 572 | 3 | 5 | 2 | 3 | 13 |
| CBACT04C.cbl | 652 | 3 | 5 | 1 | 3 | 12 |
| COMEN01C.cbl | 308 | 2 | 3 | 5 | 1 | 11 |
| COPAUS0C.cbl | 1,032 | 4 | 3 | 2 | 3 | 12 |

### 6.3 Top 5 Modernization Recommendations

#### 1. COACTUPC.cbl — Account Update (Score: 17/20)

**Justification:** The single largest and most complex program in the estate (4,236 LOC, 164 IF statements, 28 EVALUATE blocks). Contains exhaustive field-level validation logic (date, SSN, phone area code, state, ZIP) that should be extracted into reusable validation services. The COPY REPLACING macro pattern (113+ uses of CSSETATY) makes this program hard to maintain and understand. Touches 3 VSAM files (account, card, customer, xref).

**Approach:**
- Extract validation logic into a Java `AccountValidationService` with individual validators
- Convert COPY REPLACING screen attribute pattern to a UI framework (React/Angular form validation)
- Split into Account View, Account Edit, and Validation Service microservices
- Migrate VSAM access to Spring Data JPA repositories

#### 2. CBSTM03A.CBL — Statement Generation (Score: 15/20)

**Justification:** Highest I/O operation count (115 ops) in the estate. Self-contained batch program with no CICS dependency — the easiest candidate to prove the migration approach works. Generates both text and HTML output, making it a natural fit for modern template engines. Already delegates I/O to CBSTM03B, showing a separation-of-concerns pattern.

**Approach:**
- Spring Batch `Job` with `ItemReader` (XREF → Customer → Account → Transactions) and `ItemWriter` (statement output)
- Thymeleaf templates for HTML statement generation
- Replace VSAM sequential browse with JPA/JDBC cursor-based reading
- Can run alongside legacy system during migration transition

#### 3. CBTRN02C.cbl — Transaction Posting (Score: 15/20)

**Justification:** Core business process — posts daily transactions to the master file, updates account balances, and maintains category balance totals. Critical to the daily batch cycle (POSTTRAN step). Touches 5 data stores (DALYTRAN input, TRANSACT, ACCTFILE, XREFFILE, TCATBALF).

**Approach:**
- Spring Batch step: read daily transactions → validate → post to master → update balances
- Implement idempotent posting with transaction IDs to prevent double-posting
- Use `@Transactional` for atomic balance updates (replacing VSAM REWRITE)
- Integrate with existing category balance logic from CBACT04C

#### 4. COPAUA0C.cbl — Authorization Decision (Score: 15/20)

**Justification:** The most architecturally complex program — spans IMS (DL/I calls for PAUTH database), DB2 (fraud reporting), and MQ (authorization message exchange) in a single CICS transaction. This is the key integration point for the authorization subsystem. The COPAUS0C → COPAUS1C → COPAUS2C chain should be migrated as a unit.

**Approach:**
- Spring JMS for MQ message handling (request/response)
- IMS hierarchy (Summary → Detail) maps to 2 JPA entities with `@OneToMany`
- DB2 fraud table already relational — direct JPA migration
- Event-driven architecture using Spring Integration or Apache Kafka

#### 5. COCRDLIC.cbl — Credit Card List (Score: 14/20)

**Justification:** Demonstrates the paginated browse pattern (STARTBR/READNEXT/READPREV/ENDBR) that appears in 5+ programs (COCRDLIC, COACTVWC, COTRN00C, COUSR00C, COPAUS0C). Modernizing this creates a reusable pattern (`AbstractListController`) for all other browse programs.

**Approach:**
- Create a generic `AbstractPaginatedBrowseController<T>` with Spring Data `Pageable`
- Implement card-specific `CreditCardListController extends AbstractPaginatedBrowseController<Card>`
- Reuse the pattern for Account View, Transaction List, User List, Auth Summary
- Replace BMS screen with REST API + frontend pagination component

---

## 7. Architectural Patterns Identified

### 7.1 Common Patterns Across Programs

| Pattern | Programs Using It | Count | Modernization Impact |
|---------|------------------|-------|---------------------|
| Paginated Browse (STARTBR/READNEXT/READPREV/ENDBR) | COCRDLIC, COACTVWC, COACTUPC, COTRN00C, COUSR00C, COPAUS0C | 6 | Extract into `AbstractPaginatedBrowseController` |
| COMMAREA Navigation (XCTL/LINK) | All CICS programs | 21 | Replace with REST API routing / SPA navigation |
| COPY REPLACING Macros | COACTUPC (CSSETATY ×113), COACTVWC | 2 | Replace with CSS/form validation framework |
| Screen Attribute Setting (CSSETATY) | COACTUPC, COACTVWC | 2 | Convert to HTML/CSS class-based styling |
| Date Validation (CSUTLDPY → CSUTLDTC → CEEDAYS) | COACTUPC | 1 | Java `LocalDate` validation |
| Lookup Tables (CSLKPCDY) | COACTUPC | 1 | Database reference tables or enum classes |
| DB2 Cursor Pagination | COTRTLIC | 1 | JPA `Pageable` with `@Query` |
| DB2 Cascading Delete | COTRTUPC | 1 | JPA `@OneToMany(cascade = CascadeType.ALL)` |
| REPRO Backup/Restore (REPROC proc) | TRANBKP, PRTCATBL, TRANREPT | 3 | Database backup strategies |
| TDQ Internal Reader (WRITEQ TD → JCL) | CORPT00C | 1 | REST API trigger for batch jobs |

### 7.2 Programs by Estimated Migration Effort

| Effort | Programs | Count | Description |
|--------|----------|-------|-------------|
| **Low (1–2 weeks)** | COBSWAIT, CSUTLDTC, CBACT02C, CBACT03C, CBCUS01C, COSGN00C, COUSR01C, COUSR03C, COADM01C, COMEN01C, COTRN01C, CBSTM03B, COBTUPDT, CODATE01, PAUDBUNL, COPAUS2C | 16 | < 400 LOC, simple logic, few I/O |
| **Medium (2–4 weeks)** | CBACT01C, CBTRN01C, CBEXPORT, CBIMPORT, CBACT04C, COTRN02C, COBIL00C, CORPT00C, COUSR00C, COUSR02C, COCRDSLC, CBTRN03C, COPAUS1C, CBPAUP0C, PAUDBLOD, DBUNLDGS, COACCT01 | 17 | 400–900 LOC, moderate complexity |
| **High (4–8 weeks)** | COACTVWC, CBSTM03A, CBTRN02C, COPAUA0C, COPAUS0C, COCRDLIC, COCRDUPC, COTRTLIC, COTRTUPC | 9 | 900–2100 LOC, complex logic or multi-subsystem |
| **Very High (8+ weeks)** | COACTUPC | 1 | 4,236 LOC, recommend splitting into 3+ services |
| **Already Relational** | COTRTLIC, COTRTUPC, COBTUPDT | 3 | DB2 programs — SQL translates with minimal changes |

### 7.3 Risk Assessment

| Risk | Impact | Programs Affected | Mitigation |
|------|--------|------------------|------------|
| COPY REPLACING complexity | High — 113+ macro expansions in single program | COACTUPC | Pre-expand macros before migration; generate validation code |
| IMS hierarchy to relational mapping | Medium — parent-child segment model | COPAUA0C, COPAUS0C, COPAUS1C, CBPAUP0C | Map to 2 JPA entities: `AuthSummary` (1) → `AuthDetail` (N) |
| MQ message format changes | Medium — fixed-length COBOL records | COPAUA0C, COACCT01, CODATE01 | Define DTOs matching existing message formats |
| GDG (Generation Data Group) versioning | Low — sequential backup versioning | TRANBKP, COMBTRAN, TRANREPT, PRTCATBL | Replace with timestamped file names or database versioning |
| BMS screen-to-UI mapping | High — 21 BMS maps with field-level attributes | All CICS programs | Generate REST APIs; build new frontend |
| VSAM key structure differences | Medium — compound keys, AIX | TCATBALF, TRANCATG, XREFFILE, TRANSACT | Map to JPA composite keys (`@IdClass` or `@EmbeddedId`) |
