# CardDemo Domain Decomposition

## Overview

This document identifies bounded contexts within the CardDemo application by analyzing program-to-copybook dependencies, JCL job groupings, and shared vs. isolated data files. Each bounded context maps to a candidate microservice or module, with extraction seams rated by difficulty.

---

## Methodology

1. **Copybook Sharing Analysis** — Programs that COPY the same data-structure copybooks operate on the same domain entities and belong to the same bounded context.
2. **JCL Job Grouping** — JCL jobs that reference the same programs or data files in their steps reveal operational domain boundaries.
3. **Data File Isolation** — VSAM files accessed by a single domain vs. shared across multiple domains determine coupling strength and extraction difficulty.

---

## Bounded Contexts

### BC-1: Identity & Access Management

**Programs:**
| Program | Type | Function | LOC |
|---|---|---|---|
| COSGN00C | CICS | Sign-on screen | 260 |
| COADM01C | CICS | Admin menu | 288 |
| COUSR00C | CICS | List users | 695 |
| COUSR01C | CICS | Add user | 299 |
| COUSR02C | CICS | Update user | 414 |
| COUSR03C | CICS | Delete user | 359 |

**Shared Copybooks:**
| Copybook | Used By | Description |
|---|---|---|
| CSUSR01Y | COSGN00C, COUSR00C, COUSR01C, COUSR02C, COUSR03C | User security record (80 bytes) |
| COCOM01Y | All CICS programs | COMMAREA — used for inter-program navigation |
| COADM02Y | COADM01C | Admin menu option definitions |
| COMEN02Y | COMEN01C | User menu option definitions |

**Data Files:**
| File | Format | Record Length | Access | Isolation |
|---|---|---|---|---|
| USRSEC | VSAM KSDS | 80 | Read/Write | **Isolated** — only used by this domain |

**JCL Jobs:**
- `DUSRSECJ` — Initialize USRSEC VSAM file
- `RACFCMDS` (sample) — RACF security setup

**Candidate Microservice:** `user-service`
- REST API: `/api/users` (CRUD), `/api/auth/login`, `/api/auth/token`
- Database: Users table (migrated from USRSEC)
- Authentication: JWT/OAuth2 token management

---

### BC-2: Account Management

**Programs:**
| Program | Type | Function | LOC |
|---|---|---|---|
| COACTVWC | CICS | View account details | 941 |
| COACTUPC | CICS | Update account | 4,236 |
| CBACT01C | Batch | Read/export account data | 430 |

**Shared Copybooks:**
| Copybook | Used By (within domain) | Also Used By (cross-domain) | Description |
|---|---|---|---|
| CVACT01Y | COACTVWC, COACTUPC, CBACT01C | CBTRN01C, CBTRN02C, CBACT04C, CBSTM03B, COBIL00C, COTRN02C, CBEXPORT | Account record (300 bytes) |
| CVACT03Y | COACTVWC, COACTUPC | CBACT03C, CBTRN01C, CBTRN02C, CBTRN03C, CBACT04C, CBSTM03B, COBIL00C, CBEXPORT | Card XREF record (50 bytes) |
| CSUTLDWY | COACTUPC | CSUTLDTC | Date validation working storage |

**Data Files:**
| File | Format | Record Length | Access | Isolation |
|---|---|---|---|---|
| ACCTDAT | VSAM KSDS | 300 | Read/Write | **Shared** — 10+ programs across 4 domains |
| CCXREF | VSAM KSDS | 50 | Read/Write | **Shared** — 9+ programs across 4 domains |
| CXACAIX | VSAM AIX | — | Read (alternate index on CCXREF) | **Shared** — used by Account, Card, Bill Payment |

**JCL Jobs:**
- `ACCTFILE` — Load account database
- `XREFFILE` — Load card-account xref
- `READACCT` — Read/verify account data
- `READXREF` — Read/verify xref data

**Candidate Microservice:** `account-service`
- REST API: `/api/accounts/{id}` (GET, PUT), `/api/accounts/{id}/cards` (GET)
- Database: Accounts table, CardXref table (migrated from ACCTDAT, CCXREF)
- Events: `AccountUpdated`, `AccountBalanceChanged`

---

### BC-3: Credit Card Management

**Programs:**
| Program | Type | Function | LOC |
|---|---|---|---|
| COCRDLIC | CICS | List credit cards | 1,459 |
| COCRDSLC | CICS | View card details | 887 |
| COCRDUPC | CICS | Update card details | 1,560 |
| CBACT02C | Batch | Read/print card data | 178 |

**Shared Copybooks:**
| Copybook | Used By (within domain) | Also Used By (cross-domain) | Description |
|---|---|---|---|
| CVACT02Y | COCRDLIC, COCRDSLC, COCRDUPC, CBACT02C | CBTRN01C, CBACT04C, CBSTM03B | Card record (150 bytes) |
| CVACT03Y | COCRDLIC, COCRDSLC, COCRDUPC | (see BC-2) | Card XREF record (50 bytes) |
| CVACT01Y | COCRDLIC, COCRDSLC, COCRDUPC | (see BC-2) | Account record (300 bytes) |
| CVCRD01Y | COCRDUPC | — | Card detail structure |

**Data Files:**
| File | Format | Record Length | Access | Isolation |
|---|---|---|---|---|
| CARDDAT | VSAM KSDS | 150 | Read/Write | **Shared** — also used by Transaction batch |
| CCXREF | VSAM KSDS | 50 | Read/Write | **Shared** — see BC-2 |
| ACCTDAT | VSAM KSDS | 300 | Read only (from Card domain) | **Shared** — see BC-2 |

**JCL Jobs:**
- `CARDFILE` — Load card database
- `READCARD` — Read/verify card data

**Candidate Microservice:** `card-service`
- REST API: `/api/cards` (GET list), `/api/cards/{number}` (GET, PUT)
- Database: Cards table (migrated from CARDDAT)
- Dependencies: Calls `account-service` for account lookups (replaces direct ACCTDAT reads)

---

### BC-4: Transaction Processing

**Programs:**
| Program | Type | Function | LOC |
|---|---|---|---|
| COTRN00C | CICS | List transactions | 699 |
| COTRN01C | CICS | View transaction | 330 |
| COTRN02C | CICS | Add transaction | 783 |
| CBTRN01C | Batch | Post daily transactions | 494 |
| CBTRN02C | Batch | Post daily trans (extended — with rejections/balance) | 731 |
| CBTRN03C | Batch | Print transaction detail report | 649 |

**Shared Copybooks:**
| Copybook | Used By (within domain) | Also Used By (cross-domain) | Description |
|---|---|---|---|
| CVTRA05Y | COTRN00C, COTRN01C, COTRN02C, CBTRN01C, CBTRN02C, CBTRN03C | COBIL00C, CBSTM03B, CBEXPORT | Transaction record (350 bytes) |
| CVTRA06Y | CBTRN01C, CBTRN02C | — | Daily transaction record (350 bytes) |
| CVTRA01Y | CBTRN02C | CBACT04C | Transaction category balance (50 bytes) |
| CVTRA03Y | CBTRN03C | — | Transaction type (60 bytes) |
| CVTRA04Y | CBTRN03C | — | Transaction category type (60 bytes) |
| CVACT01Y | CBTRN01C, CBTRN02C, COTRN02C | (see BC-2) | Account record |
| CVACT02Y | CBTRN01C | (see BC-3) | Card record |
| CVACT03Y | CBTRN01C, CBTRN02C, CBTRN03C | (see BC-2) | Card XREF |
| CVCUS01Y | CBTRN01C | (see BC-5) | Customer record |

**Data Files:**
| File | Format | Record Length | Access | Isolation |
|---|---|---|---|---|
| TRANSACT | VSAM KSDS | 350 | Read/Write | **Shared** — also used by Bill Payment, Reports, Statement |
| DALYTRAN | Sequential | 350 | Read (batch input) | **Semi-isolated** — only used by batch posting |
| DALYREJS | Sequential | 350 | Write (rejected trans) | **Isolated** — output only |
| ACCTDAT | VSAM KSDS | 300 | Read/Write (balance update) | **Shared** — see BC-2 |
| CARDDAT | VSAM KSDS | 150 | Read (card validation) | **Shared** — see BC-3 |
| CUSTDAT | VSAM KSDS | 500 | Read (customer validation) | **Shared** — see BC-5 |
| CCXREF | VSAM KSDS | 50 | Read (card-to-account lookup) | **Shared** — see BC-2 |
| TCATBALF | VSAM KSDS | 50 | Read/Write (category balance) | **Shared** — also used by Interest Calc |
| TRANTYPE | VSAM KSDS | 60 | Read (reference data) | **Isolated** to Transaction domain |
| TRANCATG | VSAM KSDS | 60 | Read (reference data) | **Isolated** to Transaction domain |

**JCL Jobs:**
- `POSTTRAN` — Post daily transactions (runs CBTRN01C or CBTRN02C)
- `COMBTRAN` — Combine transaction files
- `TRANREPT` — Generate transaction reports (runs CBTRN03C)
- `DALYREJS` — Process daily rejections
- `TRANBKP` — Backup transaction file
- `TRANFILE` — Initialize TRANSACT VSAM
- `TRANIDX` — Build transaction indexes
- `TRANTYPE` — Load transaction type reference data
- `TRANCATG` — Load transaction category reference data
- `TCATBALF` — Load transaction category balance file
- `PRTCATBL` — Print category balance report
- `WAITSTEP` — Timer wait utility for batch scheduling

**Candidate Microservice:** `transaction-service`
- REST API: `/api/transactions` (GET list, POST), `/api/transactions/{id}` (GET)
- Batch API: `/api/batch/post-daily` (triggers daily posting job)
- Database: Transactions table, DailyTransactions (staging), RejectedTransactions
- Events: `TransactionPosted`, `TransactionRejected`, `DailyPostingCompleted`
- Dependencies: Calls `account-service` for balance updates, `card-service` for card validation

---

### BC-5: Customer Management

**Programs:**
| Program | Type | Function | LOC |
|---|---|---|---|
| CBCUS01C | Batch | Read/print customer data | 178 |

**Shared Copybooks:**
| Copybook | Used By (within domain) | Also Used By (cross-domain) | Description |
|---|---|---|---|
| CVCUS01Y | CBCUS01C | CBTRN01C, CBSTM03B, CBEXPORT, CBIMPORT | Customer record (500 bytes) |

**Data Files:**
| File | Format | Record Length | Access | Isolation |
|---|---|---|---|---|
| CUSTDAT | VSAM KSDS | 500 | Read (batch and statement gen) | **Shared** — used by Transaction Posting and Statement Gen |

**JCL Jobs:**
- `CUSTFILE` — Load customer database
- `READCUST` — Read/verify customer data
- `DEFCUST` — Define customer VSAM cluster

**Candidate Microservice:** `customer-service`
- REST API: `/api/customers/{id}` (GET)
- Database: Customers table (migrated from CUSTDAT)
- Note: Currently no online CICS program for customer CRUD; the COMEN01C menu does not include a customer management option. Customer data is populated via batch load.

---

### BC-6: Bill Payment

**Programs:**
| Program | Type | Function | LOC |
|---|---|---|---|
| COBIL00C | CICS | Pay account balance | 572 |

**Shared Copybooks:**
| Copybook | Used By (within domain) | Also Used By (cross-domain) | Description |
|---|---|---|---|
| CVACT01Y | COBIL00C | (see BC-2) | Account record |
| CVACT03Y | COBIL00C | (see BC-2) | Card XREF |
| CVTRA05Y | COBIL00C | (see BC-4) | Transaction record |

**Data Files:**
| File | Format | Record Length | Access | Isolation |
|---|---|---|---|---|
| TRANSACT | VSAM KSDS | 350 | Write (create payment transaction) | **Shared** — see BC-4 |
| ACCTDAT | VSAM KSDS | 300 | Read/Write (update balance) | **Shared** — see BC-2 |
| CXACAIX | VSAM AIX | — | Read (account lookup by card) | **Shared** — see BC-2 |

**Candidate Microservice:** Merge into `transaction-service` or standalone `payment-service`
- REST API: `/api/payments` (POST — pay balance)
- Dependencies: `account-service` (balance read/update), `transaction-service` (create payment transaction)

---

### BC-7: Financial Reporting & Interest Calculation

**Programs:**
| Program | Type | Function | LOC |
|---|---|---|---|
| CBACT04C | Batch | Interest calculation | 652 |
| CBSTM03A | Batch | Statement generation (text + HTML) | 924 |
| CBSTM03B | Batch | Statement file I/O subroutine | 230 |
| CORPT00C | CICS | Trigger batch report from online | 649 |

**Shared Copybooks:**
| Copybook | Used By (within domain) | Also Used By (cross-domain) | Description |
|---|---|---|---|
| CVTRA01Y | CBACT04C | CBTRN02C | Transaction category balance |
| CVTRA02Y | CBACT04C | — | Disclosure group (interest rates) |
| COSTM01 | CBSTM03A | — | Statement formatting constants |
| CVCUS01Y | CBSTM03B | (see BC-5) | Customer record |
| CVACT01Y | CBACT04C, CBSTM03B | (see BC-2) | Account record |
| CVACT02Y | CBACT04C | (see BC-3) | Card record |
| CVACT03Y | CBACT04C, CBSTM03B | (see BC-2) | Card XREF |

**Data Files:**
| File | Format | Record Length | Access | Isolation |
|---|---|---|---|---|
| TCATBALF | VSAM KSDS | 50 | Read | **Shared** — also written by Transaction Posting |
| DISCGRP | VSAM KSDS | 50 | Read | **Semi-isolated** — only written by initialization JCL |
| TRANSACT | VSAM KSDS | 350 | Read | **Shared** — see BC-4 |
| ACCTDAT | VSAM KSDS | 300 | Read/Write (interest updates) | **Shared** — see BC-2 |

**JCL Jobs:**
- `INTCALC` — Run interest calculation
- `CREASTMT` — Create account statements
- `REPTFILE` — Generate report files
- `DISCGRP` — Load disclosure group data

**Candidate Microservice:** `reporting-service`
- API: `/api/reports/statements` (async generation), `/api/reports/interest-calc` (trigger)
- Dependencies: Reads from `account-service`, `transaction-service`, `customer-service`

---

### BC-8: Data Migration Utilities

**Programs:**
| Program | Type | Function | LOC |
|---|---|---|---|
| CBEXPORT | Batch | Export customer data for branch migration | 582 |
| CBIMPORT | Batch | Import customer data from export file | 487 |
| COBSWAIT | Batch | Timer utility (calls MVSWAIT assembler) | 41 |
| CSUTLDTC | Batch | Date validation utility (calls CEEDAYS) | 157 |

**Shared Copybooks:**
| Copybook | Used By | Description |
|---|---|---|
| CVEXPORT | CBEXPORT, CBIMPORT | Export file record layout |
| CVCUS01Y | CBEXPORT, CBIMPORT | Customer record |
| CVACT01Y | CBEXPORT | Account record |
| CVACT03Y | CBEXPORT | Card XREF |
| CVTRA05Y | CBEXPORT | Transaction record |
| CSUTLDPY | CSUTLDTC | Date validation parameters |

**Candidate Microservice:** Part of migration tooling (not a permanent service)

---

### BC-9: Authorization Processing (Optional Module)

**Programs:**
| Program | Type | Function | LOC |
|---|---|---|---|
| COPAUA0C | CICS | Authorization request processing | ~400 |
| COPAUS0C | CICS | Pending authorization summary | ~500 |
| COPAUS1C | CICS | Pending authorization detail | ~400 |
| COPAUS2C | CICS | Pending authorization purge | ~400 |
| CBPAUP0C | Batch | Batch purge expired authorizations | ~300 |
| PAUDBLOD | Utility | Load IMS database | ~200 |
| PAUDBUNL | Utility | Unload IMS database | ~200 |
| DBUNLDGS | Utility | Unload GSAM | ~150 |

**Unique Data Stores:**
| File | Format | Isolation |
|---|---|---|
| IMS DB (DBPAUTP0) | Hierarchical | **Isolated** — only used by this module |
| IMS DB (DBPAUTX0) | Hierarchical | **Isolated** |
| DB2 table (AUTHFRDS) | Relational | **Isolated** |
| MQ queues | Message | **Isolated** |

**Candidate Microservice:** `authorization-service`
- Fully isolated data stores (IMS + DB2 + MQ)
- Cleanest extraction boundary of any module
- Note: Despite clean data isolation, the IMS/DB2/MQ technology stack makes this the hardest to extract operationally

---

### BC-10: Transaction Type Reference Data (Optional DB2 Module)

**Programs:**
| Program | Type | Function | LOC |
|---|---|---|---|
| COTRTLIC | CICS | List/update transaction types | ~400 |
| COTRTUPC | CICS | Transaction type maintenance | ~400 |
| COBTUPDT | Batch | Batch update transaction types | ~300 |

**Unique Data Stores:**
| File | Format | Isolation |
|---|---|---|
| DB2 TRNTYPE | Relational | **Isolated** — managed only by this module |
| DB2 TRNTYCAT | Relational | **Isolated** — managed only by this module |

**Candidate Microservice:** `reference-data-service` (or merge into `transaction-service`)

---

## Extraction Seams Analysis

### Seam Map

```
┌──────────────────┐     ┌───────────────────┐     ┌──────────────────┐
│  BC-1: Identity   │     │  BC-9: Auth        │     │ BC-10: TranType  │
│  & Access Mgmt    │     │  (IMS/DB2/MQ)      │     │ Ref Data (DB2)   │
│                   │     │                    │     │                  │
│  USRSEC ──────────┤     │  IMS DB ───────────┤     │ DB2 tables ──────┤
│  (isolated)       │     │  DB2 table         │     │ (isolated)       │
│                   │     │  MQ queues         │     │                  │
└──────────────────┘     │  (all isolated)    │     └──────────────────┘
                          └───────────────────┘
                                                      ┌──────────────────┐
┌──────────────────┐                                  │ BC-5: Customer   │
│  BC-8: Migration  │                                  │                  │
│  Utilities        │──── reads ──────────────────────▶│  CUSTDAT         │
│                   │                                  │  (shared-read)   │
└──────────────────┘                                  └─────────┬────────┘
                                                                │ read
                                                                ▼
┌──────────────────────────────────────────────────────────────────────────┐
│                        SHARED DATA CORE                                  │
│                                                                          │
│  ┌─────────┐   ┌─────────┐   ┌─────────┐   ┌──────────┐   ┌─────────┐ │
│  │ ACCTDAT │   │ CARDDAT │   │ CCXREF  │   │ TRANSACT │   │ TCATBALF│ │
│  │ (300B)  │   │ (150B)  │   │ (50B)   │   │ (350B)   │   │ (50B)  │ │
│  └────┬────┘   └────┬────┘   └────┬────┘   └────┬─────┘   └────┬────┘ │
│       │              │             │              │              │       │
│  BC-2 Account   BC-3 Card    BC-2,3,4,6,7   BC-4,6,7       BC-4,7    │
│  BC-3 Card      BC-4 Trans   (6 domains)    (3 domains)    (2 domains)│
│  BC-4 Trans     BC-7 Report                                           │
│  BC-6 Payment                                                          │
│  BC-7 Report                                                           │
│  BC-8 Migration                                                        │
│  (6 domains)                                                           │
└──────────────────────────────────────────────────────────────────────────┘
```

### Extraction Seam Ratings

| Seam (From → To) | Interaction Type | Shared Data | Difficulty | Rationale |
|---|---|---|---|---|
| **BC-1 → BC-2/3/4** | COMMAREA (user context passed to all programs) | None (navigation only) | **Easy** | COCOM01Y COMMAREA carries user ID/type for authorization checks. Can be replaced with JWT claims in HTTP headers. |
| **BC-10 → BC-4** | DB2 reference data read by VSAM-based reporting | TRANTYPE/TRANCATG (written by BC-10, read by BC-4 batch) | **Easy** | Reference data is read-only from the consumer side. A simple REST call or database view replaces the VSAM copy. |
| **BC-9 → Core** | MQ message for authorization requests | None (async messaging) | **Easy** | Already decoupled via MQ. Replace MQ with Kafka/SQS with the same message contract. |
| **BC-5 → BC-4/7** | Customer data read during transaction posting and statement generation | CUSTDAT | **Medium** | CUSTDAT is read-only from the consumer side (never written by BC-4 or BC-7). Can be replaced with a REST call to `customer-service`, but batch programs read sequentially which requires a bulk API or database view. |
| **BC-8 → BC-2/3/4/5** | Reads all entity files for export; writes all for import | CUSTDAT, ACCTDAT, CCXREF, TRANSACT | **Medium** | Export reads sequentially across all files. Import writes to all. During migration, these become ETL pipelines that read from VSAM and write to the new RDBMS — a natural transition. |
| **BC-6 → BC-2/4** | Bill Payment reads account via CXACAIX, writes TRANSACT, updates ACCTDAT | ACCTDAT, TRANSACT, CXACAIX | **Medium** | Three shared files, but the operation is a well-defined unit: read balance → create transaction → update balance. Can be expressed as a saga/API composition. |
| **BC-7 → BC-2/4** | Interest calc reads ACCTDAT, TCATBALF, DISCGRP, CCXREF; updates ACCTDAT | ACCTDAT, TCATBALF, DISCGRP, CCXREF | **Hard** | Interest calculation must atomically read category balances and update account records. Batch processing requires bulk access to all account records. |
| **BC-3 → BC-2** | Card programs read/write CCXREF, read ACCTDAT for account context | ACCTDAT, CCXREF, CXACAIX | **Hard** | CCXREF is the join table between cards and accounts. Both domains read and write it. CXACAIX (alternate index) provides account-based lookups. Splitting ownership requires deciding which service owns the xref table. |
| **BC-4 → BC-2/3** | Transaction posting reads CUSTDAT, CARDDAT, CCXREF, updates ACCTDAT and TCATBALF | ACCTDAT, CARDDAT, CCXREF, CUSTDAT, TCATBALF | **Hard** | CBTRN02C touches 6 files in a single batch run. Transaction posting must validate the card (CARDDAT), resolve to an account (CCXREF), verify the customer (CUSTDAT), update the account balance (ACCTDAT), and update category balances (TCATBALF). This is the most tightly coupled seam in the system. |
| **BC-2 ↔ BC-3 ↔ BC-4 (mutual)** | Circular dependency through ACCTDAT/CARDDAT/CCXREF | ACCTDAT, CARDDAT, CCXREF | **Hard** | These three domains form a tightly coupled core. ACCTDAT is accessed by 6 domains, CCXREF by 6 domains. They cannot be extracted independently — they must be decomposed together or sequentially with a shared database as an intermediate step. |

---

## Candidate Microservice Architecture

```
                    ┌─────────────┐
                    │  API Gateway │
                    └──────┬──────┘
                           │
          ┌────────────────┼────────────────┐
          │                │                │
   ┌──────▼──────┐  ┌─────▼──────┐  ┌──────▼──────┐
   │ user-service │  │ card-      │  │ transaction-│
   │              │  │ service    │  │ service     │
   │ • Auth/Login │  │ • List     │  │ • List/View │
   │ • User CRUD  │  │ • View     │  │ • Add       │
   │ • JWT tokens │  │ • Update   │  │ • Batch Post│
   └──────────────┘  └─────┬──────┘  │ • Reports   │
                           │         │ • Bill Pay  │
                     ┌─────▼──────┐  └──────┬──────┘
                     │ account-   │         │
                     │ service    │◄────────┘
                     │ • View     │
                     │ • Update   │
                     │ • Balance  │
                     └─────┬──────┘
                           │
                    ┌──────▼──────┐  ┌──────────────┐  ┌───────────────┐
                    │ customer-   │  │ reporting-    │  │ authorization-│
                    │ service     │  │ service       │  │ service       │
                    │ • Lookup    │  │ • Statements  │  │ • Auth Req    │
                    └─────────────┘  │ • Interest    │  │ • Pending     │
                                     └───────────────┘  └───────────────┘

                    ┌──────────────┐
                    │ reference-   │
                    │ data-service │
                    │ • Tran Types │
                    │ • Categories │
                    │ • Disc Groups│
                    └──────────────┘
```

---

## Data Ownership Mapping

| Data Entity | Source File | Owner Service | Consumers |
|---|---|---|---|
| User/Security | USRSEC | `user-service` | All (auth context) |
| Account | ACCTDAT | `account-service` | card, transaction, reporting, payment |
| Card | CARDDAT | `card-service` | transaction (validation) |
| Card-Account XREF | CCXREF + CXACAIX | `account-service` | card, transaction, payment, reporting |
| Customer | CUSTDAT | `customer-service` | transaction (validation), reporting |
| Transaction | TRANSACT | `transaction-service` | reporting, payment |
| Daily Transaction | DALYTRAN | `transaction-service` | (batch input only) |
| Rejected Trans | DALYREJS | `transaction-service` | (batch output only) |
| Category Balance | TCATBALF | `transaction-service` | reporting (interest calc) |
| Disclosure Group | DISCGRP | `reference-data-service` | reporting (interest calc) |
| Transaction Type | TRANTYPE | `reference-data-service` | reporting |
| Transaction Category | TRANCATG | `reference-data-service` | reporting |
| IMS Auth DB | DBPAUTP0/X0 | `authorization-service` | (isolated) |
| DB2 Auth Table | AUTHFRDS | `authorization-service` | (isolated) |
| DB2 Tran Type | TRNTYPE/TRNTYCAT | `reference-data-service` | (isolated) |

---

## Key Findings

1. **The Shared Data Core is the primary extraction challenge.** ACCTDAT and CCXREF are accessed by 6+ bounded contexts. Any extraction strategy must address this shared-state problem — either through a shared database (interim), event-driven synchronization, or API-mediated access.

2. **Three domains have clean extraction boundaries:** BC-1 (Identity), BC-9 (Authorization), and BC-10 (Transaction Type Reference Data) each have isolated data stores and can be extracted independently.

3. **COCOM01Y (COMMAREA) is the universal coupling mechanism.** Every CICS program includes this copybook and passes it through CICS RETURN/XCTL. In the modern architecture, this becomes the JWT token payload + request context.

4. **Batch programs create the hardest seams.** CBTRN02C accesses 6 files in a single job step. CBSTM03B reads across 4 domains. These bulk-access patterns don't map naturally to microservice APIs and may require a shared database or data lake during the transition.

5. **The CXACAIX alternate index is a hidden coupling point.** Multiple programs use this VSAM AIX to look up accounts by card-cross-reference account ID. This lookup pattern must be preserved in the new architecture (likely as a secondary index or JOIN in the relational database).
