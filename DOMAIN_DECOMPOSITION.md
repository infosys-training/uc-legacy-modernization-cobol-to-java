# CardDemo Domain Decomposition

## 1. Methodology

Bounded contexts are identified by analyzing three coupling vectors:
1. **Copybook sharing** — programs that COPY the same data structures operate on the same domain entities.
2. **JCL job grouping** — jobs that execute in sequence or share DD statements form processing pipelines.
3. **Data file affinity** — VSAM files and datasets that are read/written by the same set of programs define data ownership boundaries.

Programs that share all three vectors belong to the same bounded context. Programs that share only copybooks (read-only consumers) represent integration points between contexts.

---

## 2. Copybook Sharing Matrix

The following matrix shows which copybooks create the strongest coupling clusters:

### Cluster A — Account-Card-Customer Core
| Copybook | Domain Entity | Used By (programs) |
|:---------|:-------------|:-------------------|
| CVACT01Y | Account record | CBACT01C, CBACT04C, CBEXPORT, CBIMPORT, CBSTM03A, CBTRN01C, CBTRN02C, COACTUPC, COACTVWC, COBIL00C, COCRDUPC, COCRDSLC*, COTRN02C |
| CVACT02Y | Card record | CBACT02C, CBEXPORT, CBIMPORT, CBTRN01C, COACTVWC, COCRDLIC, COCRDSLC, COCRDUPC |
| CVACT03Y | Card-Account XREF | CBACT03C, CBACT04C, CBEXPORT, CBIMPORT, CBSTM03A, CBTRN01C, CBTRN02C, CBTRN03C, COACTUPC, COACTVWC, COBIL00C, COCRDSLC*, COCRDUPC*, COTRN02C |
| CVCUS01Y | Customer record | CBCUS01C, CBEXPORT, CBIMPORT, CBTRN01C, COACTUPC, COACTVWC, COCRDSLC, COCRDUPC |
| CVCRD01Y | Card display | COACTUPC, COACTVWC, COCRDLIC, COCRDSLC, COCRDUPC |

### Cluster B — Transaction Processing
| Copybook | Domain Entity | Used By (programs) |
|:---------|:-------------|:-------------------|
| CVTRA05Y | Transaction record | CBACT04C, CBEXPORT, CBIMPORT, CBTRN01C, CBTRN02C, CBTRN03C, COBIL00C, CORPT00C, COTRN00C, COTRN01C, COTRN02C |
| CVTRA06Y | Daily transaction | CBTRN01C, CBTRN02C |
| CVTRA01Y | Category balance | CBACT04C, CBTRN02C |
| CVTRA02Y | Disclosure group | CBACT04C |
| CVTRA03Y | Transaction type | CBTRN03C |
| CVTRA04Y | Transaction category | CBTRN03C |
| CVTRA07Y | Transaction report | CBTRN03C |

### Cluster C — User Security
| Copybook | Domain Entity | Used By (programs) |
|:---------|:-------------|:-------------------|
| CSUSR01Y | User security record | COADM01C, COACTUPC, COACTVWC, COCRDLIC, COCRDSLC, COCRDUPC, COMEN01C, COSGN00C, COUSR00C, COUSR01C, COUSR02C, COUSR03C |

### Cluster D — UI Framework (shared infrastructure)
| Copybook | Purpose | Used By (programs) |
|:---------|:--------|:-------------------|
| COCOM01Y | COMMAREA (inter-program comm) | All 17 CICS programs |
| COTTL01Y | Screen title line | All 17 CICS programs |
| CSDAT01Y | Date area | All 17 CICS programs |
| CSMSG01Y | Message area | All 17 CICS programs |
| DFHAID / DFHBMSCA | CICS system copies | All 17 CICS programs |

### Cluster E — Export/Import
| Copybook | Domain Entity | Used By (programs) |
|:---------|:-------------|:-------------------|
| CVEXPORT | Multi-record export layout | CBEXPORT, CBIMPORT |

---

## 3. JCL Job Grouping Analysis

### Daily Cycle (Control-M: DAILY-TransactionBackup)
```
CLOSEFIL → TRANBKP → WAITSTEP → OPENFIL
```
- **Purpose:** Nightly batch window — close VSAM files from CICS, back up transaction data, wait, reopen.
- **Data flow:** TRANSACT VSAM → TRANSACT.BKUP GDG
- **Associated processing jobs (run between CLOSE/OPEN):**
  - POSTTRAN (CBTRN02C) — post daily transactions
  - DALYREJS — capture rejected transactions

### Weekly Cycle (Control-M: WEEKLY-TransactionTypesDBRefresh)
```
MNTTRDB2 → [DisclosureGroupsRefresh: CLOSEFIL → DISCGRP → WAITSTEP → OPENFIL]
            [TransactionTypesDBRefresh: TRANEXTR]
```
- **Purpose:** Refresh reference data from Db2 to VSAM.
- **Data flow:** Db2 TRNTYPE/TRNTYCAT tables → VSAM TRANTYPE/TRANCATG files; DISCGRP PS → VSAM DISCGRP

### Monthly Cycle (Control-M: MONTHLY-InterestCalculation)
```
CLOSEFIL → INTCALC → COMBTRAN → WAITSTEP → OPENFIL
```
- **Purpose:** Compute interest/fees and combine system transactions.
- **Data flow:** TCATBALF + DISCGRP + ACCTDATA + CARDXREF → SYSTRAN GDG; TRANSACT.BKUP + SYSTRAN → merged TRANSACT

### Statement Generation (ad-hoc / CICS-triggered)
```
CREASTMT: IDCAMS setup → SORT → CBSTM03A
TRANREPT: CBTRN03C
```
- **Purpose:** Generate statements (text + HTML) and transaction reports.
- **Data flow:** TRANSACT + XREF + ACCTDATA + CUSTDATA → STATEMNT.PS / STATEMNT.HTML

### Initialization / Data Load (one-time setup)
```
DUSRSECJ → CLOSEFIL → ACCTFILE → CARDFILE → CUSTFILE → XREFFILE → TRANFILE → DISCGRP → TCATBALF → TRANCATG → TRANTYPE → OPENFIL → DEFGDGB
```

---

## 4. Data File Affinity Map

| VSAM File | Primary Owner (Writer) | Consumers (Readers) | Shared? |
|:----------|:----------------------|:---------------------|:--------|
| USRSEC | COUSR00C-03C, COSGN00C | All CICS (session context) | **Isolated** — only user mgmt writes |
| ACCTDATA | COACTUPC, CBTRN02C, CBACT04C | COACTVWC, COBIL00C, CBSTM03A, CBEXPORT, CBIMPORT | **Shared** — written by account mgmt + batch |
| CARDDATA | COCRDUPC (implied) | COCRDLIC, COCRDSLC, CBACT02C, CBEXPORT | **Shared** — read widely |
| CUSTDATA | (loaded via JCL) | COACTVWC, COCRDSLC, CBCUS01C, CBEXPORT, CBSTM03A | **Shared** — read-only at runtime |
| CARDXREF | (loaded via JCL) | Nearly all programs | **Shared** — the central cross-reference hub |
| TRANSACT | COTRN02C (online add), CBTRN02C (batch post) | COTRN00C, COTRN01C, COBIL00C, CORPT00C, CBSTM03A, CBTRN03C | **Shared** — the most accessed file |
| DALYTRAN | (external feed / previous day) | CBTRN01C, CBTRN02C | **Isolated** — input to batch posting only |
| TCATBALF | CBTRN02C (batch post) | CBACT04C | **Semi-isolated** — batch pipeline internal |
| DISCGRP | (loaded via JCL weekly) | CBACT04C | **Isolated** — reference data |
| TRANCATG | (loaded via JCL weekly) | (reference lookups) | **Isolated** — reference data |
| TRANTYPE | (loaded via JCL weekly) | (reference lookups) | **Isolated** — reference data |
| SYSTRAN GDG | CBACT04C (interest calc output) | COMBTRAN (SORT merge) | **Isolated** — pipeline internal |
| EXPORT.DATA | CBEXPORT | CBIMPORT | **Isolated** — migration utility |

---

## 5. Bounded Context Definitions

### BC-1: Identity & Access Management
**Programs:** COSGN00C, COUSR00C, COUSR01C, COUSR02C, COUSR03C
**Exclusive data:** USRSEC VSAM KSDS
**Copybooks owned:** CSUSR01Y (note: read by other contexts for session info)
**Integration surface:** Produces authenticated user context (currently via COMMAREA field `CDEMO-USER-ID`). Other contexts depend on user identity but not on user management operations.

**Candidate microservice: `identity-service`**
- REST API: `/auth/login`, `/users` CRUD
- Data store: PostgreSQL (users table) or integration with corporate IdP
- Publishes: JWT tokens consumed by all other services
- No inbound dependencies from other business services

---

### BC-2: Account Management
**Programs:** COACTVWC, COACTUPC, CBACT01C
**Shared data (owner):** ACCTDATA VSAM KSDS
**Shared data (consumer):** CARDXREF, CUSTDATA, CARDDATA
**Copybooks owned:** CVACT01Y (account record)
**Integration surface:** Provides account data to Transaction Processing, Billing, Card Management, Reporting. Consumes customer data from BC-4 and card cross-reference from BC-3.

**Candidate microservice: `account-service`**
- REST API: `/accounts/{id}`, `/accounts/{id}/update`
- Data store: PostgreSQL (accounts table)
- Consumes: `customer-service` for customer details
- Consumed by: `transaction-service`, `billing-service`, `reporting-service`

---

### BC-3: Card Management
**Programs:** COCRDLIC, COCRDSLC, COCRDUPC
**Shared data (owner):** CARDDATA VSAM KSDS
**Shared data (co-owner):** CARDXREF VSAM KSDS (shared with Account context)
**Copybooks owned:** CVACT02Y (card record), CVCRD01Y (card display), CVACT03Y (cross-reference)
**Integration surface:** Provides card lookup to Transaction Processing and Reporting. Shares CARDXREF bidirectionally with Account Management.

**Candidate microservice: `card-service`**
- REST API: `/cards`, `/cards/{number}`, `/cards/{number}/update`
- Data store: PostgreSQL (cards, card_xref tables)
- Consumes: `account-service`, `customer-service`
- Consumed by: `transaction-service`, `reporting-service`

*Note: CARDXREF is a shared-kernel between BC-2 and BC-3. During decomposition, designate one owner (recommend BC-3) and have BC-2 consume via API.*

---

### BC-4: Customer Management
**Programs:** CBCUS01C (batch read/print)
**Shared data (owner):** CUSTDATA VSAM KSDS
**Copybooks owned:** CVCUS01Y (customer record), CUSTREC (statement variant)
**Integration surface:** Consumed by Account View, Card Detail, Statements, Export.

**Candidate microservice: `customer-service`**
- REST API: `/customers/{id}`
- Data store: PostgreSQL (customers table)
- Consumed by: `account-service`, `card-service`, `reporting-service`
- Note: Currently no online CRUD UI for customers — this is a data service.

---

### BC-5: Transaction Processing
**Programs:**
- Online: COTRN00C (list), COTRN01C (view), COTRN02C (add)
- Batch: CBTRN01C (post daily), CBTRN02C (post + update balances)
**Shared data (owner):** TRANSACT VSAM KSDS, DALYTRAN PS
**Shared data (co-owner):** TCATBALF VSAM KSDS
**Copybooks owned:** CVTRA05Y (transaction), CVTRA06Y (daily transaction), CVTRA01Y (category balance)
**Integration surface:** Writes the most critical data in the system. Consumes ACCTDATA, CARDXREF. Feeds into Interest Calculation and Reporting.

**Candidate microservice: `transaction-service`**
- REST API: `/transactions`, `/transactions/{id}`, POST `/transactions`
- Data store: PostgreSQL (transactions, daily_transactions, category_balances tables)
- Event publishing: Transaction-posted events (replaces file-based batch flow)
- Consumes: `account-service`, `card-service`
- Consumed by: `billing-service`, `reporting-service`, `interest-service`

---

### BC-6: Billing & Payments
**Programs:** COBIL00C (bill payment)
**Shared data (consumer):** ACCTDATA, CARDXREF, TRANSACT
**Copybooks used:** CVACT01Y, CVACT03Y, CVTRA05Y (all from other contexts)
**Integration surface:** Reads account/transaction data, writes payment transactions.

**Candidate microservice: `billing-service`**
- REST API: POST `/payments`
- Data store: Shares transaction store via `transaction-service` API
- Consumes: `account-service`, `transaction-service`
- Note: Thin service — mostly orchestrates a payment transaction write.

---

### BC-7: Interest & Fee Calculation
**Programs:** CBACT04C (interest calculator)
**Shared data (consumer):** TCATBALF, DISCGRP, ACCTDATA, CARDXREF (+ AIX)
**Shared data (owner):** SYSTRAN GDG (system-generated transactions)
**Copybooks owned:** CVTRA02Y (disclosure group)
**Integration surface:** Monthly batch job. Reads balances, applies rules, writes system transactions merged into TRANSACT.

**Candidate microservice: `interest-service` (or scheduled job)**
- Trigger: Monthly cron / event
- Data store: Reads from `transaction-service`, `account-service`; writes system transactions back
- Consumes: `account-service`, `transaction-service`, reference data
- Note: This may be better as a scheduled batch job within the transaction-service domain rather than a standalone microservice.

---

### BC-8: Reporting & Statements
**Programs:** CORPT00C (online trigger), CBSTM03A + CBSTM03B (statement generation), CBTRN03C (transaction report)
**Shared data (consumer):** TRANSACT, CARDXREF, ACCTDATA, CUSTDATA
**Copybooks owned:** COSTM01 (statement layout), CVTRA03Y (tran type), CVTRA04Y (tran category), CVTRA07Y (report layout)
**Integration surface:** Read-only consumer of all master data. Produces output files (PS, HTML).

**Candidate microservice: `reporting-service`**
- REST API: POST `/reports/statement`, POST `/reports/transactions`
- Output: PDF/HTML/CSV (replacing PS and HTML flat files)
- Consumes: `transaction-service`, `account-service`, `card-service`, `customer-service`
- Async: Report generation via job queue (replaces JCL submission from CICS)

---

### BC-9: Data Migration Utilities
**Programs:** CBEXPORT, CBIMPORT
**Exclusive data:** EXPORT.DATA PS (multi-record format)
**Copybooks owned:** CVEXPORT
**Integration surface:** Reads all master files; produces/consumes flat export file.

**Candidate module: `migration-tools` (CLI / batch job)**
- Not a runtime microservice — ETL utility
- Input/output: CSV/JSON format replacing EBCDIC multi-record file
- Consumes: All service APIs for data extraction
- Used for: Branch migration, data seeding, disaster recovery

---

### BC-10: Reference Data Management (Optional DB2 Module)
**Programs:** COTRTUPC, COTRTLIC, COBTUPDT
**Exclusive data:** Db2 TRNTYPE, TRNTYCAT tables
**Copybooks owned:** CSDB2RWY, CSDB2RPY (Db2 module-specific)
**Integration surface:** Maintains reference data consumed by Transaction Processing and Reporting via VSAM extracts.

**Candidate microservice: `reference-data-service`**
- REST API: `/transaction-types`, `/transaction-categories`
- Data store: PostgreSQL (transaction_types, transaction_categories tables)
- Consumed by: `transaction-service`, `reporting-service`
- Replaces: Weekly MNTTRDB2/TRANEXTR batch refresh cycle with real-time API access

---

### BC-11: Authorization Processing (Optional IMS-DB2-MQ Module)
**Programs:** COPAUA0C (MQ trigger), COPAUS0C (summary), COPAUS1C (details), COPAUS2C, CBPAUP0C (batch purge), PAUDBLOD, PAUDBUNL, DBUNLDGS
**Exclusive data:** IMS DB (DBPAUTP0, DBPAUTX0), Db2 (AUTHFRDS)
**Copybooks owned:** CIPAUDTY, CCPAURQY, CCPAURLY, CIPAUSMY, CCPAUERY, IMSFUNCS, PAUTBPCB, PADFLPCB, PASFLPCB
**Integration surface:** MQ request/response for authorization. Self-contained module that reads CARDXREF for card validation.

**Candidate microservice: `authorization-service`**
- Event-driven: Consumes authorization requests from message broker (SQS/Kafka replacing MQ)
- REST API: `/authorizations/pending`, `/authorizations/{id}`
- Data store: PostgreSQL (replacing IMS DB + Db2 tables)
- Consumes: `card-service` (for card validation)
- Publishes: Authorization decision events

---

## 6. Bounded Context Map

```
┌─────────────────────────────────────────────────────────────────────┐
│                        CardDemo System                              │
│                                                                     │
│  ┌──────────────┐    JWT/Session    ┌──────────────────────────┐   │
│  │  BC-1         │◄────────────────►│  All Other Contexts      │   │
│  │  Identity &   │                  │  (consume auth tokens)   │   │
│  │  Access Mgmt  │                  └──────────────────────────┘   │
│  └──────────────┘                                                   │
│                                                                     │
│  ┌──────────────┐  API  ┌──────────────┐  API  ┌──────────────┐   │
│  │  BC-4         │◄─────│  BC-2         │◄─────│  BC-3         │   │
│  │  Customer     │      │  Account      │      │  Card         │   │
│  │  Management   │      │  Management   │      │  Management   │   │
│  └──────┬───────┘      └──────┬───────┘      └──────┬───────┘   │
│         │                     │                      │            │
│         │    ┌────────────────┼──────────────────────┘            │
│         │    │                │                                    │
│         ▼    ▼                ▼                                    │
│  ┌──────────────┐      ┌──────────────┐      ┌──────────────┐   │
│  │  BC-5         │─────►│  BC-7         │      │  BC-6         │   │
│  │  Transaction  │      │  Interest &   │      │  Billing &    │   │
│  │  Processing   │      │  Fee Calc     │      │  Payments     │   │
│  └──────┬───────┘      └──────────────┘      └──────────────┘   │
│         │                                                         │
│         ▼                                                         │
│  ┌──────────────┐      ┌──────────────┐      ┌──────────────┐   │
│  │  BC-8         │      │  BC-10        │      │  BC-11        │   │
│  │  Reporting &  │◄─────│  Reference    │      │  Authorization│   │
│  │  Statements   │      │  Data Mgmt   │      │  (IMS/MQ)     │   │
│  └──────────────┘      └──────────────┘      └──────────────┘   │
│                                                                     │
│  ┌──────────────┐                                                  │
│  │  BC-9         │  (utility — not a runtime service)              │
│  │  Migration    │                                                  │
│  │  Tools        │                                                  │
│  └──────────────┘                                                  │
└─────────────────────────────────────────────────────────────────────┘
```

---

## 7. Shared Kernel & Anti-Corruption Layers

### Shared Kernel: CARDXREF
The cross-reference file (CVACT03Y) is used by 14+ programs across 5 bounded contexts. It links Card Number ↔ Account ID ↔ Customer ID.

**Migration strategy:** Designate BC-3 (Card Management) as the owner. Expose a lookup API. Other contexts call this API instead of reading the VSAM file directly. During transition, a CDC bridge keeps the VSAM and PostgreSQL copies synchronized.

### Anti-Corruption Layer: COMMAREA → API Gateway
The COMMAREA structure (COCOM01Y) currently carries session state, navigation context, and data between all CICS programs. In the modernized architecture, this is replaced by:
- **Session state** → JWT token (BC-1)
- **Navigation context** → Frontend routing (React/Angular)
- **Inter-service data** → API request/response payloads

### Anti-Corruption Layer: VSAM ↔ Database Bridge
During phased migration, programs on VSAM and services on PostgreSQL must stay synchronized. Implement a bidirectional sync adapter:
- **VSAM → DB:** File change polling or CICS exit triggers
- **DB → VSAM:** Scheduled flat file export (mirrors current TRANEXTR pattern)

---

## 8. Microservice Summary

| Bounded Context | Candidate Service | Data Store | API Style | Priority |
|:----------------|:-----------------|:-----------|:----------|:---------|
| BC-1: Identity | `identity-service` | PostgreSQL / IdP | REST + JWT | Phase 1 |
| BC-2: Account | `account-service` | PostgreSQL | REST | Phase 2 |
| BC-3: Card | `card-service` | PostgreSQL | REST | Phase 3 |
| BC-4: Customer | `customer-service` | PostgreSQL | REST | Phase 2 |
| BC-5: Transaction | `transaction-service` | PostgreSQL | REST + Events | Phase 2 |
| BC-6: Billing | `billing-service` | Via transaction-service | REST | Phase 3 |
| BC-7: Interest | `interest-service` | Scheduled job | Event/Cron | Phase 4 |
| BC-8: Reporting | `reporting-service` | Read replicas | REST + Async | Phase 3 |
| BC-9: Migration | `migration-tools` | N/A (ETL) | CLI | Phase 1 |
| BC-10: Ref Data | `reference-data-service` | PostgreSQL | REST | Phase 3 |
| BC-11: Authorization | `authorization-service` | PostgreSQL | Event-driven | Phase 3 |
