# HOTSPOT REPORT — CardDemo COBOL Estate

> Generated from analysis of `uc-legacy-modernization-cobol-to-java/app/`

---

## 1. Top 10 Programs — Composite Hotspot Ranking

Programs are ranked by a weighted composite score across five dimensions. Each metric is normalized to a 0–10 scale; the composite score is the sum.

| Rank | Program | LOC | Copybooks | I/O Ops | Logic Density (IF/EVALUATE) | Inter-Program Deps | Composite Score |
|------|---------|-----|-----------|---------|----------------------------|-------------------|-----------------|
| **1** | **COACTUPC.cbl** | 4,236 | 58 | 11 | 175 | 15+ (files + XCTL) | **48.2** |
| **2** | **COTRTLIC.cbl** | 2,098 | 10 | 27 | 103 | 6 (DB2 + CICS) | **33.5** |
| **3** | **COCRDUPC.cbl** | 1,560 | 16 | 8 | 163 | 8 (files + XCTL) | **31.4** |
| **4** | **COTRTUPC.cbl** | 1,702 | 10 | 7 | 124 | 6 (DB2 + CICS) | **30.8** |
| **5** | **COCRDLIC.cbl** | 1,459 | 14 | 11 | 138 | 6 (files + XCTL) | **29.6** |
| **6** | **CBTRN02C.cbl** | 731 | 5 | 23 | 93 | 6 (files) | **26.3** |
| **7** | **CBACT04C.cbl** | 652 | 5 | 19 | 86 | 5 (files) | **24.7** |
| **8** | **COPAUA0C.cbl** | 1,026 | 17 | 21 | 61 | 5 (MQ + IMS + CICS) | **24.1** |
| **9** | **CBSTM03A.CBL** | 924 | 4 | 108 | 18 | 5 (CALL + files) | **23.8** |
| **10** | **CBTRN03C.cbl** | 649 | 5 | 22 | 79 | 6 (files) | **22.9** |

---

## 2. Detailed Metric Breakdown

### 2.1 Lines of Code (LOC)

| Rank | Program | LOC | Classification |
|------|---------|-----|----------------|
| 1 | COACTUPC.cbl | 4,236 | Online (CICS) |
| 2 | COTRTLIC.cbl | 2,098 | Online (CICS/DB2) |
| 3 | COTRTUPC.cbl | 1,702 | Online (CICS/DB2) |
| 4 | COCRDUPC.cbl | 1,560 | Online (CICS) |
| 5 | COCRDLIC.cbl | 1,459 | Online (CICS) |
| 6 | COPAUS0C.cbl | 1,032 | Online (CICS/IMS) |
| 7 | COPAUA0C.cbl | 1,026 | Online (CICS/IMS/MQ) |
| 8 | COACTVWC.cbl | 941 | Online (CICS) |
| 9 | CBSTM03A.CBL | 924 | Batch |
| 10 | COCRDSLC.cbl | 887 | Online (CICS) |

### 2.2 Copybook References

| Rank | Program | # Copybooks | Key Copybooks |
|------|---------|-------------|---------------|
| 1 | COACTUPC.cbl | 58 | CSUTLDWY, CSLKPCDY, CSSETATY + 12 standard |
| 2 | COPAUA0C.cbl | 17 | 6 MQ copybooks + CCPAURQY |
| 3 | COACTVWC.cbl | 16 | CSSTRPFY + standard set |
| 4 | COCRDSLC.cbl | 16 | CSSTRPFY + standard set |
| 5 | COCRDUPC.cbl | 16 | CSSTRPFY + standard set |
| 6 | COPAUS0C.cbl | 15 | IMS + standard set |
| 7 | COCRDLIC.cbl | 14 | CSSTRPFY + standard set |
| 8 | COBIL00C.cbl | 11 | Standard + account/transaction |
| 9 | COPAUS1C.cbl | 11 | IMS + standard set |
| 10 | COTRN02C.cbl | 11 | Standard + account/transaction |

### 2.3 I/O Operations

| Rank | Program | # I/O Ops | Types |
|------|---------|-----------|-------|
| 1 | CBSTM03A.CBL | 108 | File OPEN/READ/WRITE/CLOSE + CALL CBSTM03B (multi-file) |
| 2 | COTRTLIC.cbl | 27 | EXEC SQL (cursor), EXEC CICS SEND/RECEIVE |
| 3 | CBTRN02C.cbl | 23 | 6 VSAM files: READ/WRITE/REWRITE |
| 4 | CBEXPORT.cbl | 23 | 5 input VSAM + 1 output VSAM |
| 5 | CBTRN03C.cbl | 22 | 5 VSAM read + 1 report write |
| 6 | CBIMPORT.cbl | 22 | 1 input VSAM + 6 output sequential |
| 7 | COPAUA0C.cbl | 21 | MQ OPEN/GET + CICS READ + DLI calls |
| 8 | CBACT04C.cbl | 19 | 4 VSAM read + 1 write + 1 I-O |
| 9 | DBUNLDGS.CBL | 17 | CBLTDLI GN/GNP/ISRT calls |
| 10 | CBTRN01C.cbl | 17 | 6 VSAM reads |

### 2.4 Business Logic Density (IF/EVALUATE Statements)

| Rank | Program | # IF/EVALUATE | Context |
|------|---------|--------------|---------|
| 1 | COACTUPC.cbl | 175 | Field-level validation for all account and customer fields |
| 2 | COCRDUPC.cbl | 163 | Card data validation and update logic |
| 3 | COCRDLIC.cbl | 138 | Paging logic, selection validation, display formatting |
| 4 | COTRTUPC.cbl | 124 | DB2 CRUD logic, error handling, screen state management |
| 5 | COTRTLIC.cbl | 103 | Cursor management, paging, delete/update confirmation |
| 6 | CBTRN02C.cbl | 93 | Transaction validation, cross-ref lookup, posting rules |
| 7 | CBACT04C.cbl | 86 | Interest calculation rules, fee computation |
| 8 | CBTRN03C.cbl | 79 | Date filtering, report formatting, totaling logic |
| 9 | COCRDSLC.cbl | 77 | Display formatting, data retrieval, error handling |
| 10 | COACTVWC.cbl | 66 | Multi-file lookup, data correlation, display formatting |

### 2.5 Inter-Program Dependencies

| Rank | Program | # Dependencies | Details |
|------|---------|---------------|---------|
| 1 | COACTUPC.cbl | 15+ | Reads 3 VSAM files, writes 2, transfers from/to menu + multiple screens, references 15 copybooks |
| 2 | CBTRN02C.cbl | 6 | Reads/writes 6 VSAM files (cross-cut across account, transaction, xref, category balance) |
| 3 | CBTRN03C.cbl | 6 | Reads 5 VSAM files + 1 parm file, writes report |
| 4 | CBTRN01C.cbl | 6 | Reads 6 VSAM files for transaction lookup |
| 5 | CBSTM03A.CBL | 5 | Reads 4 VSAM files + CALLs CBSTM03B for file ops |
| 6 | COPAUA0C.cbl | 5 | MQ (3 calls) + CICS files (2) + IMS DLI |
| 7 | CBEXPORT.cbl | 5 | Reads 5 VSAM files, writes 1 export file |
| 8 | COBIL00C.cbl | 4 | Reads 2 files, writes 2, updates account |
| 9 | COCRDLIC.cbl | 3 | STARTBR/READNEXT on cards, XCTL to menu and detail |
| 10 | COTRN02C.cbl | 4 | Reads xref/acct files, writes transaction, XCTL |

---

## 3. Modernization Priority Recommendations

### Tier 1 — Modernize First (Highest Impact)

| Priority | Program | Rationale |
|----------|---------|-----------|
| **1** | **COACTUPC.cbl** (4,236 LOC) | **Largest program by far** — account update is a core business function used daily. Contains extensive field-level validation (175 IF/EVALUATE), 58 copybook includes, and touches 3 VSAM files. High maintenance risk due to size and complexity. The embedded validation logic (dates, state codes, phone area codes) can be extracted into reusable service components. Modernizing this unlocks the most business value per effort. |
| **2** | **CBTRN02C.cbl** (731 LOC) | **Core batch transaction posting** — the most critical batch program. Processes daily transactions, validates against cross-references, updates account balances, and manages category balances. Touches 6 different files with complex business rules (93 IF/EVALUATE). Failure in this program stops the entire daily cycle. Modernizing it to a service with proper error handling and restartability would significantly improve operational resilience. |
| **3** | **CBACT04C.cbl** (652 LOC) | **Interest calculation engine** — performs interest and fee computation using disclosure group rates. This is core financial logic that would benefit from extraction into a testable, auditable calculation service. Complex business rules (86 IF/EVALUATE) with monetary implications make correctness critical. |

### Tier 2 — Modernize Second (High Value)

| Priority | Program | Rationale |
|----------|---------|-----------|
| **4** | **COTRTLIC.cbl** (2,098 LOC) | Second largest program. CICS/DB2 hybrid that demonstrates cursor-based paging — a pattern that maps well to modern pagination APIs. Already uses SQL, making database layer migration more straightforward. |
| **5** | **COCRDUPC.cbl** (1,560 LOC) | Card update is high-frequency and the second densest program for business logic (163 IF/EVALUATE). Similar structure to COACTUPC — can share modernization patterns and validation framework. |
| **6** | **CBSTM03A.CBL** (924 LOC) | Statement generator has the highest I/O count (108 ops). Uses a CALL-based sub-module pattern (CBSTM03B) that maps naturally to service decomposition. Generates both text and HTML output — a good candidate for template-based modernization. |

### Tier 3 — Modernize Third (Moderate Value)

| Priority | Program | Rationale |
|----------|---------|-----------|
| **7** | **COPAUA0C.cbl** (1,026 LOC) | Authorization processor combines MQ, CICS, and IMS — the most complex integration pattern. Should be modernized as part of an event-driven architecture migration. Requires IMS/MQ expertise for correct conversion. |
| **8** | **CBTRN03C.cbl** (649 LOC) | Report generation can be replaced with modern reporting tools. Moderate complexity but outputs are well-defined (structured report with totals). |
| **9** | **COCRDLIC.cbl** (1,459 LOC) | Card list with CICS browse operations. Standard list/detail pattern that maps to modern UI frameworks. Can be modernized alongside COCRDSLC. |
| **10** | **COTRTUPC.cbl** (1,702 LOC) | DB2 CRUD operations map directly to modern data access patterns. Already has SQL — least effort for data layer conversion. |

### Modernization Strategy Summary

```
                    MODERNIZATION WAVE PLAN
                    
Wave 1 (Foundation):  COACTUPC + CBTRN02C + CBACT04C
   Why: Core business functions, highest complexity,
        greatest risk reduction. Establishes patterns
        for validation, batch processing, and financial
        calculation services.

Wave 2 (Data Layer):  COTRTLIC + COTRTUPC + COCRDUPC
   Why: DB2 programs already have SQL — easiest data
        migration. Card update shares patterns with
        account update from Wave 1.

Wave 3 (Integration): COPAUA0C + CBSTM03A + CBSTM03B
   Why: MQ/IMS integration and statement generation.
        Requires event-driven architecture decisions.

Wave 4 (Remaining):   All other programs
   Why: Smaller programs, simpler patterns. Many are
        read-only or simple CRUD that follow patterns
        established in earlier waves.
```

---

## 4. Risk Factors

| Risk | Affected Programs | Mitigation |
|------|-------------------|------------|
| **No test suite** | All 44 programs | Create comprehensive test data and regression tests before any conversion |
| **Embedded validation** | COACTUPC, COCRDUPC, COTRTUPC | Extract validation into shared service layer |
| **CICS dependency** | 22 online programs | Map CICS operations to REST/event patterns; BMS maps to UI components |
| **IMS/DLI dependency** | 5 auth programs | Requires IMS DB migration strategy (to relational or document store) |
| **MQ dependency** | COPAUA0C, COACCT01, CODATE01 | Map to modern messaging (Kafka, SQS, etc.) |
| **VSAM file dependencies** | 17+ batch programs | VSAM-to-database migration must precede or parallel program conversion |
| **GDG lifecycle** | COMBTRAN, POSTTRAN, INTCALC | Replace GDG pattern with versioned storage or database partitions |
| **ALTER verb usage** | CBSTM03A | ALTER is obsolete and makes control flow unpredictable — refactor first |
| **Tight batch coupling** | CBTRN02C → CBACT04C → COMBTRAN chain | Design for independent restartability in modernized pipeline |
| **Shared copybook impact** | COCOM01Y (18+ consumers) | Any change to communication area affects all CICS programs |
