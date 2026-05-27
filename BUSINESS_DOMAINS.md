# BUSINESS DOMAINS — CardDemo COBOL Estate

## Overview

The CardDemo application is a multi-tier mainframe credit card management system spanning **6 core business domains** and **2 cross-cutting sub-applications**. This document maps each domain to its programs, data structures, and organizational boundaries.

---

## 1. Account Management

**Scope:** Account lifecycle — viewing, updating, interest calculation, and data extraction.

| Program | Classification | Function |
|---------|---------------|----------|
| COACTUPC.cbl | Online (CICS) | Account update with full field-level validation (4,236 LOC — largest program) |
| COACTVWC.cbl | Online (CICS) | Account view — read-only display of account, card, and customer details |
| CBACT01C.cbl | Batch | Account data extraction to flat files (fixed, array, variable-length formats) |
| CBACT04C.cbl | Batch | Interest calculation — reads category balances and disclosure groups, updates account balances |

**Core Data Structures:**
- `CVACT01Y.cpy` — Account master record (RECLN 300): ID, status, balances, credit limits, dates, cycle totals
- `CVACT03Y.cpy` — Card cross-reference record (RECLN 50): links card → customer → account

**VSAM Files:** ACCTFILE (KSDS), TCATBALF (category balances), DISCGRP (disclosure/interest rates)

**Key Business Rules:**
- Account balance updated during transaction posting (CBTRN02C) and interest calculation (CBACT04C)
- Credit limit and cash limit validated during account update (COACTUPC)
- Date fields validated via CSUTLDPY/CSUTLDWY date validation procedures
- ZIP code and state validated via CSLKPCDY lookup tables

---

## 2. Credit Card Management

**Scope:** Card lifecycle — listing, viewing, and updating credit card records.

| Program | Classification | Function |
|---------|---------------|----------|
| COCRDLIC.cbl | Online (CICS) | Credit card list — paginated browse via VSAM STARTBR/READNEXT/READPREV |
| COCRDSLC.cbl | Online (CICS) | Credit card detail view — displays card with linked account and customer info |
| COCRDUPC.cbl | Online (CICS) | Credit card update — modify status, embossed name, expiration date |
| CBACT02C.cbl | Batch | Card data extraction/display |
| CBACT03C.cbl | Batch | Cross-reference data extraction/display |

**Core Data Structures:**
- `CVACT02Y.cpy` — Card master record (RECLN 150): card number, CVV, embossed name, expiration, status
- `CVACT03Y.cpy` — Card cross-reference record: the central linkage entity (card → customer → account)

**VSAM Files:** CARDDATA (KSDS + AIX for alternate index), CARDXREF (KSDS + AIX)

**Key Business Rules:**
- Card number is the primary key linking to transactions, accounts, and customers
- Cross-reference (CVACT03Y) is the most critical shared structure — used by 10 programs across domains
- Card status changes in COCRDUPC affect transaction authorization eligibility

---

## 3. Transaction Processing

**Scope:** Full transaction lifecycle — online entry, daily batch validation, posting, and reporting.

| Program | Classification | Function |
|---------|---------------|----------|
| COTRN00C.cbl | Online (CICS) | Transaction list — paginated browse of transaction master |
| COTRN01C.cbl | Online (CICS) | Transaction view — display individual transaction details |
| COTRN02C.cbl | Online (CICS) | Transaction add — enter new transactions with validation |
| CBTRN01C.cbl | Batch | Daily transaction validation — validates against xref, account, customer, card, and master |
| CBTRN02C.cbl | Batch | Transaction posting — updates account balances, category balances; writes to master; rejects invalid |
| CBTRN03C.cbl | Batch | Transaction report — daily report with type/category lookups and date-range filtering |
| COBIL00C.cbl | Online (CICS) | Bill payment — processes payments against credit card accounts |

**Core Data Structures:**
- `CVTRA05Y.cpy` — Transaction master record (RECLN 350): ID, type, category, amount, merchant info, timestamps
- `CVTRA06Y.cpy` — Daily transaction record (same layout as CVTRA05Y, used as daily input feed)
- `CVTRA03Y.cpy` — Transaction type lookup (RECLN 60): 2-char code + description
- `CVTRA04Y.cpy` — Transaction category lookup (RECLN 60): type + 4-digit category code + description
- `CVTRA01Y.cpy` — Transaction category balance (RECLN 50): running balance per account/type/category
- `CVTRA02Y.cpy` — Disclosure group (RECLN 50): interest rates per account group/type/category
- `CVTRA07Y.cpy` — Report header and detail line structures
- `COSTM01.CPY` — Statement transaction layout (keyed by card+transaction ID)

**VSAM Files:** TRANSACT (KSDS + AIX), TRANTYPE (KSDS), TRANCATG (KSDS), TCATBALF (KSDS), DISCGRP (KSDS), DALYTRAN (input), DALYREJS (GDG output)

**Key Business Rules:**
- Daily transactions (CVTRA06Y) are validated by CBTRN01C before posting
- CBTRN02C is the **central pipeline program** — posts to master, updates account and category balances, generates rejects
- Interest calculation (CBACT04C) uses disclosure group rates (CVTRA02Y) applied per category balance
- Transaction types and categories form a parent-child hierarchy (type → category)

---

## 4. Authorization Processing

**Location:** `app/app-authorization-ims-db2-mq/`

**Scope:** Real-time card authorization via MQ, IMS database storage for pending authorizations, fraud tracking via DB2.

| Program | Classification | Function |
|---------|---------------|----------|
| COPAUA0C.cbl | Online (CICS + MQ) | Authorization processor — receives MQ requests, validates against VSAM, sends MQ response |
| COPAUS0C.cbl | Online (CICS) | Pending authorization summary list — display with account/customer details |
| COPAUS1C.cbl | Online (CICS) | Pending authorization detail — shows transaction-level auth data, LINKs to COPAUS2C |
| COPAUS2C.cbl | Online (CICS + DB2) | Authorization DB2 writer — inserts/updates fraud records in AUTHFRDS table |
| CBPAUP0C.cbl | Batch (IMS) | Pending authorization purge — removes expired auth records from IMS DB |
| DBUNLDGS.CBL | Batch (IMS) | Unload IMS auth DB to GSAM files (summary + detail segments) |
| PAUDBLOD.CBL | Batch (IMS) | Load flat files into IMS authorization DB |
| PAUDBUNL.CBL | Batch (IMS) | Unload IMS auth DB to sequential flat files |

**Core Data Structures:**
- `CIPAUSMY.cpy` — Pending auth summary (IMS segment): account ID, limits, balances, auth counts/amounts
- `CIPAUDTY.cpy` — Pending auth detail (IMS segment): card, merchant, amounts, match/fraud status
- `CCPAURQY.cpy` — Authorization request (MQ message layout)
- `CCPAURLY.cpy` — Authorization response (MQ message layout)
- `CCPAUERY.cpy` — Error log record with severity levels and subsystem codes
- `IMSFUNCS.cpy` — IMS DL/I function codes (GU, GN, ISRT, DLET, etc.)
- `PAUTBPCB.CPY`, `PASFLPCB.CPY`, `PADFLPCB.CPY` — IMS Program Communication Blocks

**Integration Points:** MQ queues (3 pairs: request/response/error), IMS DB (hierarchical), DB2 (AUTHFRDS), CICS VSAM (xref, account, customer)

**Key Business Rules:**
- Authorization validates card against cross-reference, then checks account status and credit limits
- Match status tracks auth lifecycle: Pending → Matched/Declined/Expired
- Fraud flag can be set (Confirmed) or removed on individual authorizations
- Expired authorizations purged by batch job CBPAUP0C

---

## 5. User Security & Administration

**Scope:** User authentication, authorization, and CRUD management for security records.

| Program | Classification | Function |
|---------|---------------|----------|
| COSGN00C.cbl | Online (CICS) | Sign-on screen — authenticates against USRSEC file, routes to admin or user menu |
| COADM01C.cbl | Online (CICS) | Admin menu — presents admin-only options (user mgmt + DB2 transaction types) |
| COMEN01C.cbl | Online (CICS) | Main menu — presents 11 user-level options (account, card, transaction, report, bill pay, auth) |
| COUSR00C.cbl | Online (CICS) | User list — paginated browse of security users |
| COUSR01C.cbl | Online (CICS) | User add — create new security user records |
| COUSR02C.cbl | Online (CICS) | User update — modify user name, password, type |
| COUSR03C.cbl | Online (CICS) | User delete — remove security user records |

**Core Data Structures:**
- `CSUSR01Y.cpy` — User security record: ID (8), first/last name, password (8), user type (A/U)
- `COCOM01Y.cpy` — COMMAREA: carries user session context (user ID, type, selected entities) across all programs
- `COADM02Y.cpy` — Admin menu option definitions (6 options → program names)
- `COMEN02Y.cpy` — Main menu option definitions (11 options → program names)

**VSAM Files:** USRSEC (KSDS — user security master)

**Key Business Rules:**
- Two user types: Admin ('A') — access to admin menu + user management; User ('U') — standard menu only
- Sign-on (COSGN00C) reads USRSEC, matches password (plain-text), routes based on user type
- Menu options are defined in copybooks with REDEFINES for array-based lookup

---

## 6. Reporting & Statement Generation

**Scope:** Batch report generation, statement production, and PDF conversion.

| Program | Classification | Function |
|---------|---------------|----------|
| CORPT00C.cbl | Online (CICS) | Report request screen — select report type and date range, submit to TDQ |
| CBTRN03C.cbl | Batch | Daily transaction report — reads master + lookups, generates formatted report |
| CBSTM03A.CBL | Batch | Statement generation — produces text and HTML credit card statements |
| CBSTM03B.CBL | Batch (module) | File I/O subprogram for CBSTM03A — handles open/read/close of data files |

**Core Data Structures:**
- `CVTRA07Y.cpy` — Report headers, detail lines, page/account/grand totals
- `COSTM01.CPY` — Statement transaction layout (keyed by card number + transaction ID)

**Output Files:** TRANREPT (GDG — daily report), STMTFILE (GDG — text statements), HTMLFILE (HTML statements)

**Key Business Rules:**
- Reports filtered by date range (start/end date parameters)
- Statements grouped by card number, sorted by transaction ID
- Report totals accumulated at page, account, and grand total levels
- TXT2PDF1.JCL converts text statements to PDF via TSO REXX

---

## 7. Cross-Cutting Sub-Application: Transaction Type DB2

**Location:** `app/app-transaction-type-db2/`

**Scope:** DB2-based maintenance of transaction type and category reference data (replaces/supplements VSAM-based TRANTYPE/TRANCATG).

| Program | Classification | Function |
|---------|---------------|----------|
| COTRTLIC.cbl | Online (CICS + DB2) | Transaction type list/update — cursor-based pagination, inline update/delete |
| COTRTUPC.cbl | Online (CICS + DB2) | Transaction type CRUD — add/update/delete with field validation |
| COBTUPDT.cbl | Batch (DB2) | Batch loader — reads flat file, inserts/updates DB2 TRANSACTION_TYPE table |

**DB2 Tables:** CARDDEMO.TRANSACTION_TYPE, CARDDEMO.TRANSACTION_TYPE_CATEGORY

---

## 8. Cross-Cutting Sub-Application: VSAM-MQ Services

**Location:** `app/app-vsam-mq/`

**Scope:** MQ-based microservice-style listeners providing CICS data access to external systems.

| Program | Classification | Function |
|---------|---------------|----------|
| COACCT01.cbl | Online (CICS + MQ) | Account inquiry service — receives MQ request, reads account VSAM, returns MQ response |
| CODATE01.cbl | Online (CICS + MQ) | Date/time service — receives MQ request, returns system date/time via MQ |

**MQ Queues:** 3 queue pairs per service (request/response/error)

---

## 9. Most-Shared Copybooks — Core Data Models

These copybooks represent the foundational data structures shared across the most programs. They are the core models that any modernization effort must address first.

| Rank | Copybook | # Programs | Category | What It Defines |
|------|----------|-----------|----------|-----------------|
| 1 | **COCOM01Y** | 18 | System | COMMAREA — inter-program session context (user, account, card, customer selection) |
| 2 | **DFHAID** | 16 | System | CICS Attention ID byte constants (ENTER, CLEAR, PF keys) |
| 3 | **DFHBMSCA** | 16 | System | BMS screen attribute constants (color, protection, intensity) |
| 4 | **COTTL01Y** | 16 | System | Screen title constants ("AWS Mainframe Modernization — CardDemo") |
| 5 | **CSDAT01Y** | 16 | System | Date/time working storage (current date, formatted strings, timestamps) |
| 6 | **CSMSG01Y** | 16 | System | Common screen messages (thank-you, invalid key) |
| 7 | **CSUSR01Y** | 11 | Security | User security record — ID, name, password, type (Admin/User) |
| 8 | **CVACT03Y** | 10 | **Core Entity** | Card cross-reference — the **central linkage** between cards, customers, and accounts |
| 9 | **CVACT01Y** | 9 | **Core Entity** | Account master — balances, limits, dates, cycle totals |
| 10 | **CVCRD01Y** | 8 | Online Infrastructure | Card work areas — AID handling, next-program routing, error/return messages |
| 11 | **CVCUS01Y** | 7 | **Core Entity** | Customer master — name, address, SSN, FICO score, phone numbers |
| 12 | **CVTRA05Y** | 7 | **Core Entity** | Transaction master — ID, type, category, amount, merchant, timestamps |
| 13 | **CVACT02Y** | 6 | **Core Entity** | Card master — card number, CVV, embossed name, expiration, status |
| 14 | **CIPAUSMY** | 5 | Authorization | Pending auth summary (IMS segment) — limits, balances, auth counts |
| 15 | **CIPAUDTY** | 5 | Authorization | Pending auth detail (IMS segment) — card, merchant, fraud status |

### Core Entity Relationship Model

```
┌──────────────────┐          ┌──────────────────┐
│  CUSTOMER        │          │  ACCOUNT         │
│  (CVCUS01Y)      │          │  (CVACT01Y)      │
│  PK: CUST-ID     │          │  PK: ACCT-ID     │
└────────┬─────────┘          └────────┬─────────┘
         │                             │
         │    ┌──────────────────┐     │
         └────┤  CARD XREF       ├─────┘
              │  (CVACT03Y)      │
              │  PK: XREF-CARD-NUM│
              │  FK: XREF-CUST-ID │
              │  FK: XREF-ACCT-ID │
              └────────┬─────────┘
                       │
              ┌────────┴─────────┐
              │  CARD            │
              │  (CVACT02Y)      │
              │  PK: CARD-NUM    │
              │  FK: CARD-ACCT-ID│
              └────────┬─────────┘
                       │
              ┌────────┴─────────┐
              │  TRANSACTION     │
              │  (CVTRA05Y)      │
              │  PK: TRAN-ID     │
              │  FK: TRAN-CARD-NUM│
              └──────────────────┘
```

### Modernization Implication

The 6 core entity copybooks (CVACT03Y, CVACT01Y, CVCUS01Y, CVACT02Y, CVTRA05Y, COCOM01Y) should be converted to domain model classes/DTOs first, as they form the data backbone referenced by 80%+ of all programs. COCOM01Y (COMMAREA) should become a session/context service, and CVACT03Y (cross-reference) should become a relationship/join managed by the persistence layer rather than a separate entity.
