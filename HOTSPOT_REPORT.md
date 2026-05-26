# HOTSPOT REPORT

## Overview

This report ranks programs by complexity, coupling, and modernization priority. Metrics are computed from source code analysis across the entire CardDemo COBOL estate (44 programs).

---

## 1. Top 10 Programs by Lines of Code

| Rank | Program | LOC | Sub-Application |
|------|---------|-----|-----------------|
| 1 | COACTUPC.cbl | 4,236 | Main |
| 2 | COTRTLIC.cbl | 2,098 | Transaction Type DB2 |
| 3 | COTRTUPC.cbl | 1,702 | Transaction Type DB2 |
| 4 | COCRDUPC.cbl | 1,560 | Main |
| 5 | COCRDLIC.cbl | 1,459 | Main |
| 6 | COPAUS0C.cbl | 1,032 | Authorization IMS/DB2/MQ |
| 7 | COPAUA0C.cbl | 1,026 | Authorization IMS/DB2/MQ |
| 8 | COACTVWC.cbl | 941 | Main |
| 9 | CBSTM03A.CBL | 924 | Main |
| 10 | COCRDSLC.cbl | 887 | Main |

---

## 2. Top 10 Programs by Copybooks Referenced

| Rank | Program | # Copybooks | Copybooks |
|------|---------|-------------|-----------|
| 1 | COACTUPC.cbl | 18 | COCOM01Y, COACTUP, COTTL01Y, CSDAT01Y, CSLKPCDY, CSMSG01Y, CSMSG02Y, CSUSR01Y, CSUTLDPY, CVACT01Y, CVACT03Y, CVCRD01Y, CVCUS01Y, DFHAID, DFHBMSCA, CSSETATY, CSSTRPFY, CSUTLDWY |
| 2 | COPAUA0C.cbl | 17 | CCPAUERY, CCPAURLY, CCPAURQY, CIPAUDTY, CIPAUSMY, CVACT01Y, CVACT03Y, CVCUS01Y, CMQGMOV, CMQMDV, CMQODV, CMQPMOV, CMQTML, CMQV + more |
| 3 | COCRDSLC.cbl | 12 | COCOM01Y, COCRDSL, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT02Y, CVCRD01Y, CVCUS01Y, DFHAID, DFHBMSCA, CSSTRPFY |
| 4 | COCRDUPC.cbl | 12 | COCOM01Y, COCRDUP, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT02Y, CVCRD01Y, CVCUS01Y, DFHAID, DFHBMSCA, CSSTRPFY |
| 5 | COACTVWC.cbl | 15 | COCOM01Y, COACTVW, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT01Y, CVACT02Y, CVACT03Y, CVCRD01Y, CVCUS01Y, DFHAID, DFHBMSCA, CSSTRPFY |
| 6 | COPAUS0C.cbl | 15 | CIPAUDTY, CIPAUSMY, COCOM01Y, COPAU00, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y, DFHAID, DFHBMSCA |
| 7 | COTRTUPC.cbl | 15 | COCOM01Y, CVCRD01Y, DFHBMSCA, DFHAID, COTTL01Y, COTRTUP, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CSSETATY, CSSTRPFY, CSUTLDWY |
| 8 | COTRTLIC.cbl | 12 | COCOM01Y, CVCRD01Y, DFHBMSCA, DFHAID, COTTL01Y, COTRTLI, CSDAT01Y, CSMSG01Y, CSUSR01Y, CVACT02Y, CSSTRPFY |
| 9 | COBIL00C.cbl | 11 | COCOM01Y, COBIL00, COTTL01Y, CSDAT01Y, CSMSG01Y, CVACT01Y, CVACT03Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 10 | COTRN02C.cbl | 11 | COCOM01Y, COTRN02, COTTL01Y, CSDAT01Y, CSMSG01Y, CVACT01Y, CVACT03Y, CVTRA05Y, DFHAID, DFHBMSCA |

---

## 3. Top 10 Programs by I/O Operations

I/O operations = file SELECT/FD + EXEC CICS READ/WRITE/REWRITE/DELETE/STARTBR + EXEC SQL + MQ calls.

| Rank | Program | # I/O Ops | Details |
|------|---------|-----------|---------|
| 1 | COACTUPC.cbl | 10+ | CICS READ/REWRITE on 4 VSAM files + SEND/RECEIVE MAP |
| 2 | CBEXPORT.cbl | 12 | 6 SELECT/FD (5 input + 1 output) |
| 3 | CBIMPORT.cbl | 14 | 7 SELECT/FD (1 input + 5 output + 1 error) |
| 4 | CBTRN01C.cbl | 12 | 6 SELECT/FD (5 input, 1 output) |
| 5 | CBTRN02C.cbl | 12 | 6 SELECT/FD (3 input, 3 output) |
| 6 | CBTRN03C.cbl | 12 | 6 SELECT/FD (5 input, 1 output) |
| 7 | COBIL00C.cbl | 12 | CICS READ/REWRITE/WRITE + STARTBR/READPREV/ENDBR |
| 8 | COPAUA0C.cbl | 10 | MQ OPEN/GET/PUT1/CLOSE + CICS READ (3 files) |
| 9 | CBACT04C.cbl | 10 | 5 SELECT/FD (4 input, 1 output) |
| 10 | CBACT01C.cbl | 8 | 4 SELECT/FD (1 input, 3 output) |

---

## 4. Top 10 Programs by Business Logic Density

Business logic density = (EVALUATE statements + IF statements) / LOC × 1000 (normalized per 1000 lines).

| Rank | Program | EVALUATE | IF | Total | LOC | Density |
|------|---------|----------|----|-------|-----|---------|
| 1 | COACTUPC.cbl | 20 | 164 | 184 | 4,236 | 43.4 |
| 2 | COCRDLIC.cbl | 18 | 59 | 77 | 1,459 | 52.8 |
| 3 | CBTRN02C.cbl | 0 | 48 | 48 | 731 | 65.7 |
| 4 | COCRDUPC.cbl | 16 | 72 | 88 | 1,560 | 56.4 |
| 5 | CBACT04C.cbl | 0 | 43 | 43 | 652 | 66.0 |
| 6 | CBTRN03C.cbl | 4 | 38 | 42 | 649 | 64.7 |
| 7 | COTRN02C.cbl | 26 | 14 | 40 | 783 | 51.1 |
| 8 | COBIL00C.cbl | 18 | 10 | 28 | 572 | 48.9 |
| 9 | COPAUS0C.cbl | 22 | 25 | 47 | 1,032 | 45.5 |
| 10 | COCRDSLC.cbl | 8 | 33 | 41 | 887 | 46.2 |

---

## 5. Top 10 Programs by Inter-Program Dependencies

Dependencies = programs that CALL/XCTL to this program + programs this program CALL/XCTLs + shared VSAM files.

| Rank | Program | Inbound Deps | Outbound Deps | Shared Files | Total Score |
|------|---------|-------------|---------------|--------------|-------------|
| 1 | COACTUPC.cbl | 1 (COMEN01C) | 1 (COMEN01C) | 4 (ACCT, CARD, CUST, XREF) | High |
| 2 | CBTRN02C.cbl | 1 (POSTTRAN JCL) | 1 (CEE3ABD) | 6 (DALYTRAN, XREF, ACCT, TRAN, REJS, TCATBAL) | High |
| 3 | CBSTM03A.CBL | 1 (CREASTMT JCL) | 1 (CBSTM03B) | 4 (via CBSTM03B) | High |
| 4 | COPAUA0C.cbl | 1 (MQ trigger) | 4 (MQOPEN/GET/PUT/CLOSE) | 3 (ACCT, XREF, CUST) | High |
| 5 | CBEXPORT.cbl | 1 (CBEXPORT JCL) | 1 (CEE3ABD) | 6 (CUST, ACCT, XREF, TRAN, CARD, EXP) | Medium-High |
| 6 | CBTRN01C.cbl | 0 | 1 (CEE3ABD) | 6 (DALYTRAN, ACCT, CARD, CUST, XREF, TRAN) | Medium-High |
| 7 | CBACT04C.cbl | 1 (INTCALC JCL) | 1 (CEE3ABD) | 5 (ACCT, XREF, TRAN, DISCGRP, TCATBAL) | Medium-High |
| 8 | COTRN02C.cbl | 1 (COMEN01C) | 2 (CSUTLDTC, COMEN01C) | 3 (TRAN, ACCT, XREF) | Medium |
| 9 | COBIL00C.cbl | 1 (COMEN01C) | 1 (COMEN01C) | 3 (ACCT, TRAN, XREF) | Medium |
| 10 | COCRDLIC.cbl | 1 (COMEN01C) | 3 (COCRDSLC, COCRDUPC, COMEN01C) | 1 (CARDDAT) | Medium |

---

## 6. Composite Hotspot Score

Weighted composite: LOC (20%) + Copybooks (15%) + I/O (20%) + Logic Density (25%) + Dependencies (20%)

| Rank | Program | LOC | Cpyb | I/O | Density | Deps | **Composite** | Type |
|------|---------|-----|------|-----|---------|------|---------------|------|
| **1** | **COACTUPC.cbl** | 4,236 | 18 | 10+ | 43.4 | High | **97/100** | Online |
| **2** | **CBTRN02C.cbl** | 731 | 6 | 12 | 65.7 | High | **78/100** | Batch |
| **3** | **COPAUA0C.cbl** | 1,026 | 17 | 10 | 35.0 | High | **75/100** | Online+MQ |
| **4** | **COCRDUPC.cbl** | 1,560 | 12 | 8+ | 56.4 | Med | **73/100** | Online |
| **5** | **COCRDLIC.cbl** | 1,459 | 12 | 8+ | 52.8 | Med | **71/100** | Online |
| **6** | **CBEXPORT.cbl** | 582 | 7 | 12 | 27.5 | Med-Hi | **68/100** | Batch |
| **7** | **CBACT04C.cbl** | 652 | 6 | 10 | 66.0 | Med-Hi | **67/100** | Batch |
| **8** | **CBSTM03A.CBL** | 924 | 5 | 4* | 26.0 | High | **65/100** | Batch |
| **9** | **COTRTLIC.cbl** | 2,098 | 12 | DB2 | 15.3 | Med | **63/100** | Online+DB2 |
| **10** | **COBIL00C.cbl** | 572 | 11 | 12 | 48.9 | Med | **62/100** | Online |

*CBSTM03A delegates file I/O to CBSTM03B subroutine (11 CALL invocations).

---

## 7. Modernization Recommendations

### Priority 1: COACTUPC.cbl (Account Update)
**Why first:**
- Largest program in the estate (4,236 LOC) — highest maintenance cost
- Most complex business logic (164 IF + 20 EVALUATE statements)
- Touches 4 core VSAM files (accounts, cards, customers, cross-ref)
- 18 copybook dependencies — high coupling
- Contains extensive field-level validation (phone, state, ZIP, date)
- **Modernization approach:** Decompose into microservices: AccountValidationService, AccountUpdateService, AddressValidationService

### Priority 2: CBTRN02C.cbl (Post Transactions)
**Why second:**
- Core of the daily batch pipeline — highest operational impact
- Reads/writes 6 different files in a single run
- Dense conditional logic (65.7 per 1000 LOC)
- Financial calculations (balance updates, category tracking)
- **Modernization approach:** Event-driven transaction processing service with idempotent operations

### Priority 3: COPAUA0C.cbl (Authorization Decision)
**Why third:**
- Real-time authorization — latency-sensitive
- Multi-technology integration (CICS + MQ + VSAM)
- Complex decision logic (approve/decline/refer)
- **Modernization approach:** Stateless authorization microservice with async messaging

### Priority 4: COCRDUPC.cbl (Credit Card Update)
**Why fourth:**
- Second-largest main program (1,560 LOC)
- High logic density (56.4) with extensive validation
- Similar pattern to COACTUPC — can share validation components
- **Modernization approach:** Extract shared validation library; CardUpdateService

### Priority 5: CBACT04C.cbl (Interest Calculator)
**Why fifth:**
- Core financial logic — highest business risk if incorrect
- Reads 5 files, performs compound calculations
- Relatively self-contained batch program — clean extraction boundary
- **Modernization approach:** Dedicated InterestCalculationService with rule engine

---

## 8. Modernization Strategy Summary

```
┌─────────────────────────────────────────────────────────────────────┐
│                  RECOMMENDED MODERNIZATION WAVES                      │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│  Wave 1 — Batch Programs (Lower Risk)                               │
│  ─────────────────────────────────────                               │
│  • CBTRN02C (Transaction Posting)                                    │
│  • CBACT04C (Interest Calculation)                                   │
│  • CBEXPORT/CBIMPORT (Data Migration)                               │
│  • CBSTM03A/B (Statement Generation)                                │
│  Rationale: Self-contained, testable offline, clear I/O boundaries  │
│                                                                      │
│  Wave 2 — Online CRUD Programs                                      │
│  ─────────────────────────────────                                   │
│  • COACTUPC (Account Update)                                         │
│  • COCRDUPC (Card Update)                                            │
│  • COCRDLIC/COCRDSLC (Card List/View)                               │
│  • COUSR00C-03C (User Management)                                    │
│  Rationale: Extract shared validation; convert to REST APIs          │
│                                                                      │
│  Wave 3 — Integration Programs                                      │
│  ─────────────────────────────────                                   │
│  • COPAUA0C (Authorization via MQ)                                   │
│  • COACCT01/CODATE01 (VSAM+MQ services)                            │
│  • COTRTLIC/COTRTUPC (DB2 maintenance)                              │
│  Rationale: Requires middleware replacement (MQ→Kafka, IMS→RDBMS)   │
│                                                                      │
│  Wave 4 — Navigation & Framework                                    │
│  ──────────────────────────────────                                  │
│  • COSGN00C (Signon) → OAuth/OIDC                                   │
│  • COMEN01C/COADM01C (Menus) → Web UI routing                      │
│  • CORPT00C (Report submission) → Scheduler/queue                   │
│  Rationale: Depends on Waves 1-3 being complete                      │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

---

## 9. Risk Factors

| Risk | Programs Affected | Mitigation |
|------|-------------------|------------|
| Shared VSAM files → data coupling | All 44 programs | Introduce data access layer first |
| COMMAREA navigation pattern | 22 CICS programs | Map to API gateway routing |
| COMP-3/COMP fields in auth data | CIPAUDTY, CIPAUSMY | Careful numeric conversion testing |
| IMS hierarchical DB | PAUDBLOD, PAUDBUNL, DBUNLDGS | Migrate to relational schema |
| BMS map dependencies | 16+ online programs | Replace with web forms |
| CSLKPCDY validation tables | COACTUPC primarily | Extract to reference data service |
| Date format inconsistencies | CUSTREC vs CVCUS01Y | Standardize on ISO 8601 |
