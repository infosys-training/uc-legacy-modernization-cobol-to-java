# HOTSPOT REPORT — CardDemo COBOL Estate

> **Purpose:** Rank programs by complexity to guide modernization sequencing
> **Methodology:** Weighted composite score across 5 metrics (LOC, copybooks, I/O, branching density, inter-program dependencies)

---

## 1. Scoring Methodology

Each program is scored across five dimensions, normalized to 0–100 and weighted:

| Metric | Weight | What It Measures | Source |
|--------|--------|-----------------|--------|
| **Lines of Code** | 25% | Raw size / maintenance burden | `wc -l` |
| **Copybooks Referenced** | 20% | Data coupling / interface complexity | `grep -c 'COPY '` |
| **I/O Operations** | 20% | File/DB2/CICS data access count | `grep -c 'READ\|WRITE\|REWRITE\|DELETE\|START\|OPEN\|CLOSE'` + EXEC CICS/SQL |
| **Business Logic Density** | 20% | IF/EVALUATE nesting depth | `grep -c 'IF \|EVALUATE '` |
| **Inter-Program Dependencies** | 15% | CALL/XCTL/LINK coupling | `grep -c 'CALL \|EXEC CICS\|EXEC SQL\|CBLTDLI'` |

**Composite Score** = Σ (normalized_metric × weight) × 100

---

## 2. Top 10 Programs by Composite Score

| Rank | Program | LOC | Copies | I/O Ops | IF/EVAL | Deps | Composite | Classification |
|------|---------|-----|--------|---------|---------|------|-----------|---------------|
| **1** | **COACTUPC.cbl** | 4,236 | 58 | 18 + CICS | 174 | 17 | **74.8** | CICS Online |
| **2** | **COTRTLIC.cbl** | 2,098 | 12 | 27 + SQL | 16 | 29 | **42.6** | CICS/DB2 |
| **3** | **COTRTUPC.cbl** | 1,702 | 15 | 30 + SQL | 26 | 19 | **38.5** | CICS/DB2 |
| **4** | **COCRDUPC.cbl** | 1,560 | 16 | 5 + CICS | 88 | 12 | **36.1** | CICS Online |
| **5** | **COCRDLIC.cbl** | 1,459 | 14 | 9 + CICS | 77 | 21 | **34.7** | CICS Online |
| **6** | **COPAUA0C.cbl** | 1,026 | 17 | 14 + MQ | 36 | 20 | **30.3** | CICS/IMS/MQ |
| **7** | **COPAUS0C.cbl** | 1,032 | 15 | 1 + CICS | 36 | 10 | **27.4** | CICS/IMS/BMS |
| **8** | **CBSTM03A.CBL** | 924 | 5 | 118 | 20 | 15 | **28.2** | Batch |
| **9** | **COACTVWC.cbl** | 941 | 16 | 4 + CICS | 38 | 15 | **26.8** | CICS Online |
| **10** | **COCRDSLC.cbl** | 887 | 16 | 4 + CICS | 41 | 14 | **25.9** | CICS Online |

---

## 3. Detailed Metric Rankings

### 3.1 By Lines of Code

| Rank | Program | LOC | Classification |
|------|---------|-----|---------------|
| 1 | COACTUPC.cbl | 4,236 | CICS |
| 2 | COTRTLIC.cbl | 2,098 | CICS/DB2 |
| 3 | COTRTUPC.cbl | 1,702 | CICS/DB2 |
| 4 | COCRDUPC.cbl | 1,560 | CICS |
| 5 | COCRDLIC.cbl | 1,459 | CICS |
| 6 | COPAUS0C.cbl | 1,032 | CICS/IMS |
| 7 | COPAUA0C.cbl | 1,026 | CICS/IMS/MQ |
| 8 | COACTVWC.cbl | 941 | CICS |
| 9 | CBSTM03A.CBL | 924 | Batch |
| 10 | COCRDSLC.cbl | 887 | CICS |

**Observations:**
- COACTUPC is 2× larger than the next program and 6.8× the average (622 LOC)
- 8 programs exceed 1,000 LOC (18% of estate), containing 48% of total code
- Top 3 programs alone contain 26.6% of all COBOL code

### 3.2 By Copybooks Referenced

| Rank | Program | Count | Notable Inclusions |
|------|---------|-------|-------------------|
| 1 | COACTUPC.cbl | 58 | 25× CSSETATY (COPY REPLACING), 16 unique copybooks |
| 2 | COPAUA0C.cbl | 17 | 6 MQ copybooks + 5 IMS copybooks + data structures |
| 3 | COACTVWC.cbl | 16 | Full entity set (account, card, customer, xref) |
| 4 | COCRDSLC.cbl | 16 | Card display with customer join |
| 5 | COCRDUPC.cbl | 16 | Card update with customer/account validation |
| 6 | COPAUS0C.cbl | 15 | IMS + CICS + BMS + entity copybooks |
| 7 | COTRTUPC.cbl | 15 | DB2 DCLGENs + CICS + validation |
| 8 | COCRDLIC.cbl | 14 | Card browse with pagination helpers |
| 9 | COTRTLIC.cbl | 12 | DB2 DCLGENs + CICS + screen |
| 10 | COBIL00C.cbl | 11 | Account + transaction + xref for payment |

**Observations:**
- COACTUPC's 58 copybook references are 3.4× more than any other program
- This is driven by 25 instances of `COPY CSSETATY REPLACING` — a macro pattern for setting BMS field attributes when validation fails
- Excluding COPY REPLACING, COACTUPC still leads with 16 unique copybooks

### 3.3 By I/O Operations

| Rank | Program | Count | Breakdown |
|------|---------|-------|-----------|
| 1 | CBSTM03A.CBL | 118 | OPEN/READ/WRITE/CLOSE across 4 input + 2 output files × multiple calls to CBSTM03B |
| 2 | COTRTUPC.cbl | 30 | EXEC SQL (SELECT, UPDATE, DELETE, INSERT) + CICS SEND/RECEIVE |
| 3 | CBEXPORT.cbl | 29 | READ 5 entity files + WRITE 1 multi-record export |
| 4 | CBIMPORT.cbl | 29 | READ 1 export file + WRITE 6 target files + error file |
| 5 | COTRTLIC.cbl | 27 | EXEC SQL cursor operations + CICS SEND/RECEIVE MAP |
| 6 | CBTRN02C.cbl | 24 | READ/REWRITE across 6 files (DALYTRAN, TRANSACT, XREF, ACCOUNT, DALYREJS, TCATBAL) |
| 7 | CBTRN03C.cbl | 23 | READ 4 files + WRITE report with multiple line types |
| 8 | CBACT04C.cbl | 20 | READ 4 files (TCATBAL, XREF, DISCGRP, ACCOUNT) + REWRITE + WRITE |
| 9 | COACCT01.cbl | 20 | 3× MQOPEN/CLOSE + MQGET/MQPUT + CICS READ |
| 10 | COACTUPC.cbl | 18 | CICS READ/REWRITE + SEND/RECEIVE MAP |

**Observations:**
- CBSTM03A has the highest raw I/O count (118) because it delegates all file operations to CBSTM03B, which is called 13 times per card+customer combination
- Batch programs dominate I/O metrics; CICS programs use EXEC CICS commands instead of native COBOL I/O

### 3.4 By Business Logic Density (IF/EVALUATE Count)

| Rank | Program | IF/EVAL | LOC | Density (per 100 LOC) |
|------|---------|---------|-----|----------------------|
| 1 | COACTUPC.cbl | 174 | 4,236 | 4.1 |
| 2 | COCRDUPC.cbl | 88 | 1,560 | 5.6 |
| 3 | COCRDLIC.cbl | 77 | 1,459 | 5.3 |
| 4 | CBTRN02C.cbl | 48 | 731 | 6.6 |
| 5 | CBACT04C.cbl | 43 | 652 | 6.6 |
| 6 | CBTRN03C.cbl | 42 | 649 | 6.5 |
| 7 | COCRDSLC.cbl | 41 | 887 | 4.6 |
| 8 | COACTVWC.cbl | 38 | 941 | 4.0 |
| 9 | COPAUA0C.cbl | 36 | 1,026 | 3.5 |
| 10 | COPAUS0C.cbl | 36 | 1,032 | 3.5 |

**Density-adjusted ranking** (IF/EVAL per 100 LOC):

| Rank | Program | Density | Interpretation |
|------|---------|---------|---------------|
| 1 | CBTRN02C.cbl | 6.6 | Highly conditional — extensive validation in daily transaction posting |
| 2 | CBACT04C.cbl | 6.6 | Complex interest calculation with many conditional rate lookups |
| 3 | CBTRN03C.cbl | 6.5 | Report formatting with many conditional breaks (page/account/grand totals) |
| 4 | COCRDUPC.cbl | 5.6 | Field-level validation on card updates |
| 5 | COCRDLIC.cbl | 5.3 | Paginated browse with forward/backward/boundary conditions |

### 3.5 By Inter-Program Dependencies

| Rank | Program | Dep Count | Type of Dependencies |
|------|---------|-----------|---------------------|
| 1 | COTRTLIC.cbl | 29 | EXEC SQL (cursor OPEN/FETCH/CLOSE, SELECT, DELETE) + EXEC CICS (SEND, RECEIVE, XCTL, SYNCPOINT) |
| 2 | COCRDLIC.cbl | 21 | EXEC CICS (STARTBR, READNEXT, READPREV, ENDBR, SEND, RECEIVE, XCTL) |
| 3 | COPAUA0C.cbl | 20 | MQOPEN(3×), MQGET, MQPUT1, MQCLOSE(3×) + EXEC CICS (READ, ASKTIME, FORMATTIME) |
| 4 | COTRTUPC.cbl | 19 | EXEC SQL (SELECT, UPDATE, DELETE, INSERT) + EXEC CICS (SEND, RECEIVE, XCTL, SYNCPOINT, HANDLE ABEND) |
| 5 | COACTUPC.cbl | 17 | EXEC CICS (READ, REWRITE, SEND, RECEIVE, XCTL, RETURN, HANDLE ABEND) |
| 6 | COACTVWC.cbl | 15 | EXEC CICS (READ ×4 files, SEND, RECEIVE, XCTL) |
| 7 | CBSTM03A.CBL | 15 | CALL CBSTM03B (13×), CALL CEE3ABD (2×) |
| 8 | COCRDSLC.cbl | 14 | EXEC CICS (READ ×2, SEND, RECEIVE, XCTL) |
| 9 | COTRN02C.cbl | 13 | EXEC CICS (READ, STARTBR, READPREV, ENDBR, WRITE, SEND, RECEIVE) |
| 10 | COACCT01.cbl | 13 | MQOPEN(3), MQGET, MQPUT(2), MQCLOSE(3), EXEC CICS READ, RETRIEVE |

---

## 4. Modernization Priority Recommendations

### Priority 1 — Modernize First (Highest Impact, Highest Risk)

#### 1. COACTUPC.cbl — Account Update
**Score: 74.8 | LOC: 4,236 | Risk: HIGH**

| Metric | Value | Rank |
|--------|-------|------|
| Lines of Code | 4,236 | #1 (2× next largest) |
| Copybook References | 58 | #1 (25× COPY REPLACING) |
| IF/EVALUATE Stmts | 174 | #1 (absolute) |
| CICS Dependencies | 17 | #5 |
| VSAM Files Touched | 3 | HIGH (account, xref, customer) |

**Why modernize first:**
- Contains the most comprehensive business validation logic in the entire estate (date, SSN, phone area code, state code, ZIP prefix validation)
- The COPY REPLACING macro pattern (CSSETATY ×25) has no Java equivalent — must be converted to parameterized utility methods
- Touches 3 out of 5 core VSAM datasets — most data coupling of any program
- Every other account-related program depends on the same validation rules

**Recommended approach:**
1. Extract validation logic into `AccountValidationService` (reusable by Card/Customer services)
2. Split into 3 components: AccountViewController, AccountEditController, AccountValidationService
3. Convert BMS map (COACTUP) to REST API + frontend form
4. Create comprehensive test suite from existing EBCDIC test data before migration

#### 2. CBSTM03A.CBL — Statement Generation
**Score: 28.2 | LOC: 924 | Risk: LOW**

| Metric | Value | Rank |
|--------|-------|------|
| I/O Operations | 118 | #1 (by far) |
| CALL Dependencies | 15 | #7 (CBSTM03B ×13) |
| Copybook References | 5 | Low |
| IF/EVALUATE | 20 | Low |

**Why modernize early (despite lower composite score):**
- **Best candidate for proving the migration approach** — self-contained batch, no CICS dependency
- Highest I/O count but simple pattern: read entities → format → write report
- Clear Spring Batch mapping: `ItemReader` for each entity file, `ItemProcessor` for formatting, `ItemWriter` for text + HTML output
- CBSTM03B subroutine becomes a Spring `@Service` or is inlined

**Recommended approach:**
1. Spring Batch job with 4 `FlatFileItemReader`s (XREF, CUST, ACCT, TRNX)
2. Statement formatting as `ItemProcessor`
3. Dual `ItemWriter`: plain text + Thymeleaf HTML template
4. Run in parallel with COBOL until validated (batch programs can coexist during transition)

---

### Priority 2 — High Complexity, Contained Scope

#### 3. COTRTLIC.cbl — Transaction Type List (DB2)
**Score: 42.6 | LOC: 2,098 | Risk: MEDIUM**

**Why:**
- Already uses DB2 SQL — natural entry point for database modernization
- Cursor-based pagination maps directly to Spring Data `Pageable`
- Highest inter-program dependency count (29) due to heavy EXEC SQL + EXEC CICS usage
- Self-contained with its pair COTRTUPC — migrate as a unit

#### 4. COTRTUPC.cbl — Transaction Type Update (DB2)
**Score: 38.5 | LOC: 1,702 | Risk: MEDIUM**

**Why:**
- Cascading delete logic (delete type → delete all categories) needs careful transaction management
- EXEC SQL translates almost directly to JPA `@Repository` methods
- DCLTRTYP/DCLTRCAT DB2 table declarations become JPA `@Entity` classes with minimal transformation

**Recommended approach for both:**
1. JPA entities for `TransactionType` and `TransactionCategory`
2. Spring Data repositories with `Pageable` for list pagination
3. Service layer for cascading delete logic
4. REST controllers replacing CICS SEND/RECEIVE MAP

#### 5. COCRDLIC.cbl / COCRDUPC.cbl — Credit Card List & Update
**Score: 34.7 / 36.1 | LOC: 1,459 / 1,560 | Risk: MEDIUM**

**Why:**
- The paginated browse pattern (STARTBR/READNEXT/READPREV/ENDBR) appears in 5+ programs
- Creating an abstract pagination controller here establishes a reusable pattern for:
  - COTRN00C (transaction list)
  - COUSR00C (user list)
  - COPAUS0C (auth summary)
  - COTRTLIC (tran type list)

**Recommended approach:**
1. Create `AbstractListController<T>` with Spring Data `Pageable` support
2. Implement `CardController extends AbstractListController<Card>`
3. This becomes the template for all subsequent list program migrations

---

### Priority 3 — Cross-Subsystem Integration (Highest Architectural Complexity)

#### 6. COPAUA0C.cbl — Authorization Decision (IMS + MQ + CICS)
**Score: 30.3 | LOC: 1,026 | Risk: HIGH**

**Why:**
- Spans 3 middleware subsystems — most architecturally complex integration point
- COPAUS0C/1C/2C form a chain that must be migrated as a unit
- MQ request/reply pattern maps well to Spring JMS or Spring Cloud Stream
- IMS segment hierarchy maps to 2 relational tables (summary + detail)

**Recommended approach:**
1. Spring JMS for MQ queue processing (`@JmsListener`)
2. IMS PAUT database → 2 PostgreSQL tables (`auth_summary`, `auth_detail`)
3. CICS READ operations → Spring Data JPA lookups
4. Migrate all 4 auth programs (COPAUA0C, COPAUS0C, COPAUS1C, COPAUS2C) together

---

### Priority 4 — Modernize Last (Simple Batch Programs)

#### 7–10. CBTRN02C, CBACT04C, CBTRN03C, CBEXPORT/CBIMPORT
**Score: 15–22 | Risk: LOW**

| Program | LOC | Key Feature | Spring Batch Pattern |
|---------|-----|-------------|---------------------|
| CBTRN02C | 731 | Post daily transactions, validate, reject | `Step` with `ItemReader` + validation `ItemProcessor` + `ItemWriter` |
| CBACT04C | 652 | Interest calculation with rate lookups | `Step` with multi-source reader + calculation processor |
| CBTRN03C | 649 | Transaction report with break totals | `Step` with SORT pre-step + formatted report writer |
| CBEXPORT | 582 | Multi-entity export to single file | `Step` with multi-reader + `FlatFileItemWriter` |
| CBIMPORT | 487 | Single file split to multiple entities | `Step` with `FlatFileItemReader` + routing processor + multi-writer |

**Why modernize last:**
- Standard sequential file processing — lowest architectural risk
- No CICS dependency — can run in parallel with modernized online components during transition
- Well-defined input/output boundaries make testing straightforward
- These map almost 1:1 to Spring Batch `Chunk` or `Tasklet` steps

---

## 5. Risk Summary Matrix

| Program | LOC Risk | Data Coupling | Logic Complexity | Subsystem Risk | Overall |
|---------|---------|--------------|-----------------|---------------|---------|
| COACTUPC | 🔴 HIGH | 🔴 3 VSAM files | 🔴 174 branches | 🟡 CICS only | 🔴 **HIGH** |
| COTRTLIC | 🟡 MED | 🟡 DB2 tables | 🟢 LOW | 🟡 CICS+DB2 | 🟡 **MEDIUM** |
| COTRTUPC | 🟡 MED | 🟡 DB2 + cascading | 🟡 MED | 🟡 CICS+DB2 | 🟡 **MEDIUM** |
| COPAUA0C | 🟡 MED | 🟡 IMS + MQ + VSAM | 🟡 MED | 🔴 3 subsystems | 🔴 **HIGH** |
| CBSTM03A | 🟢 LOW | 🟡 4 input files | 🟢 LOW | 🟢 Batch only | 🟢 **LOW** |
| CBTRN02C | 🟢 LOW | 🟡 6 files | 🟡 HIGH density | 🟢 Batch only | 🟢 **LOW** |
| CBACT04C | 🟢 LOW | 🟡 4 files | 🟡 HIGH density | 🟢 Batch only | 🟢 **LOW** |

---

## 6. Modernization Sequencing Timeline

```
Phase 1 (Months 1–3): Foundation & Proof of Concept
├── CBSTM03A/B → Spring Batch (proves batch migration pattern)
├── COTRTLIC + COTRTUPC → Spring Data JPA (proves DB2 migration)
└── Extract COACTUPC validation logic → shared ValidationService

Phase 2 (Months 3–5): Online Core
├── COACTUPC → Account microservice (split into 3 components)
├── COCRDLIC + COCRDSLC + COCRDUPC → Card microservice
│   └── AbstractListController pattern established
└── COTRN00C + COTRN01C + COTRN02C → Transaction microservice

Phase 3 (Months 5–7): Cross-Subsystem & Remaining Online
├── COPAUA0C + COPAUS0C–2C → Authorization microservice (IMS+MQ+DB2)
├── COUSR00C–03C + COSGN00C → User/Auth microservice
├── COBIL00C → Payment service
└── CORPT00C + COADM01C + COMEN01C → UI shell / navigation

Phase 4 (Months 7–9): Batch Programs & Data Migration
├── CBTRN02C → Spring Batch (daily posting)
├── CBACT04C → Spring Batch (interest calculation)
├── CBTRN03C → Spring Batch (reporting)
├── CBEXPORT + CBIMPORT → Data migration service
└── Remaining batch utilities (CBACT01C–03C, CBCUS01C)
```

---

## 7. Key Technical Considerations

### Patterns Requiring Special Handling

| Pattern | Occurrences | Migration Challenge |
|---------|-------------|-------------------|
| COPY REPLACING (macro) | CSSETATY ×25 in COACTUPC, CSUTLDPY in 3+ programs | No Java equivalent; convert to parameterized methods |
| VSAM KSDS browse (STARTBR/READNEXT/READPREV/ENDBR) | 5 programs | Spring Data `Pageable` with cursor-based pagination |
| COMMAREA (inter-program data passing) | All 21 CICS programs | REST API DTOs / service method parameters |
| BMS SEND/RECEIVE MAP | 21 programs × 21 maps | REST API + React/Angular frontend |
| IMS DL/I (hierarchical) | 7 programs | Flatten to 2 relational tables |
| MQ request/reply | 3 programs | Spring JMS `@JmsListener` + `JmsTemplate` |
| GDG versioning | 4 JCL jobs | Timestamped file naming or DB-based versioning |
| CEE3ABD (LE abend) | 8 batch programs | Java exception handling (`try-catch`) |
| Plain-text password (CSUSR01Y) | COUSR01C, COSGN00C | Spring Security with bcrypt hashing |

### Estate-Wide Metrics Summary

| Metric | Value |
|--------|-------|
| Total LOC | 30,175 |
| Average LOC per program | 686 |
| Median LOC | 524 |
| Programs > 1,000 LOC | 8 (18%) |
| Programs with > 10 copybooks | 15 (34%) |
| Unique VSAM datasets | 10 |
| GDG datasets | 4 |
| External runtime dependencies | 4 (COBDATFT, CEE3ABD, CEEDAYS, MVSWAIT) |
| DB2 tables | 2 (TR_TYPE, TR_CAT) |
| IMS databases | 1 (PAUT with summary+detail segments) |
| MQ queues | 3+ (request, reply, dead-letter per service) |
