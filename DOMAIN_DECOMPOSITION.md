# Domain Decomposition

## Overview

This document identifies bounded contexts within the CardDemo application by analyzing three coupling dimensions:
1. **Copybook sharing** — programs using the same data structures
2. **JCL job grouping** — batch jobs that execute together in pipelines
3. **Data file sharing** — which VSAM files are shared vs. isolated

Each bounded context maps to a candidate microservice with defined extraction seams rated by difficulty.

---

## Bounded Context Identification

### Method: Affinity Analysis

Programs are grouped by analyzing three coupling signals:

| Signal | Weight | Rationale |
|--------|--------|-----------|
| Shared copybooks (data structures) | 40% | Programs sharing copybooks operate on the same domain model |
| Shared VSAM files (data stores) | 35% | Programs reading/writing the same files have data coupling |
| CICS XCTL chains (navigation) | 25% | Programs transferring control share user workflow context |

---

## Identified Bounded Contexts

### Context 1: Identity & Access Management

| Attribute | Value |
|-----------|-------|
| **Programs** | COSGN00C, COADM01C, COMEN01C, COUSR00C, COUSR01C, COUSR02C, COUSR03C |
| **LOC** | 3,897 |
| **Shared copybooks** | COCOM01Y, CSUSR01Y, COTTL01Y, CSDAT01Y, CSMSG01Y |
| **Data owned** | USRSEC (VSAM KSDS) — user credentials and role definitions |
| **External reads** | None |
| **External writes** | None |

**Why this is a bounded context:**
- All programs share CSUSR01Y (user security record layout)
- USRSEC file is exclusively accessed by these programs
- XCTL chain: COSGN00C → COADM01C → COUSR00C/01C/02C/03C
- No other domain reads or writes user data

**Candidate microservice:** `user-auth-service`
- Spring Security with JWT tokens
- Owns `users` table (PostgreSQL)
- Exposes: POST /login, GET/POST/PUT/DELETE /users

---

### Context 2: Account Management

| Attribute | Value |
|-----------|-------|
| **Programs** | COACTUPC, COACTVWC, COACCT01 |
| **LOC** | 5,497 |
| **Shared copybooks** | CVACT01Y, CVCUS01Y, CVCRD01Y, CVACT03Y, CSSETATY, CSLKPCDY, CSUTLDPY, CSUTLDWY |
| **Data owned** | ACCTFILE (accounts), CUSTFILE (customers) |
| **External reads from** | CARDXREF (shared with Card context) |
| **External writes to** | None beyond owned files |

**Why this is a bounded context:**
- Owns the core financial entities (Account + Customer)
- COACTUPC/COACTVWC share 8 validation copybooks not used elsewhere
- CSLKPCDY (state/ZIP lookup) and CSUTLDPY (date validation) are consumed exclusively here
- COACCT01 (MQ account inquiry) is the async interface to this domain

**Candidate microservice:** `account-service`
- Owns `accounts`, `customers` tables
- Contains extracted validation library (state, ZIP, phone, SSN, date)
- Exposes: GET/PUT /accounts/{id}, GET/PUT /customers/{id}

---

### Context 3: Credit Card Management

| Attribute | Value |
|-----------|-------|
| **Programs** | COCRDLIC, COCRDSLC, COCRDUPC |
| **LOC** | 3,810 |
| **Shared copybooks** | CVACT02Y, CVCRD01Y, CVACT03Y, CVCUS01Y, CSMSG02Y, CSSTRPFY |
| **Data owned** | CARDFILE (card records) |
| **External reads from** | ACCTFILE (via account lookup), CARDXREF (cross-reference) |
| **External writes to** | CARDXREF (card-account linking) |

**Why this is a bounded context:**
- CVACT02Y (card record) and CVCRD01Y (card detail) are the defining data structures
- All three programs share identical copybook sets (15 each)
- XCTL chain: COCRDLIC → COCRDSLC/COCRDUPC (list → detail → update)
- Card data has clear read/write ownership

**Candidate microservice:** `card-service`
- Owns `cards`, `card_xref` tables
- Exposes: GET /cards, GET /cards/{id}, PUT /cards/{id}
- Depends on: `account-service` for account validation

---

### Context 4: Transaction Processing

| Attribute | Value |
|-----------|-------|
| **Programs** | COTRN00C, COTRN01C, COTRN02C, COBIL00C (online); CBTRN01C, CBTRN02C (batch) |
| **LOC** | 3,920 |
| **Shared copybooks** | CVTRA05Y, CVTRA06Y, CVTRA01Y, CVACT01Y, CVACT03Y |
| **Data owned** | TRANSACT, DALYTRAN, TCATBALF |
| **External reads from** | ACCTFILE, CARDXREF, TRANTYPE, TRANCATG, DISCGRP |
| **External writes to** | ACCTFILE (balance updates via CBTRN02C) |

**Why this is a bounded context:**
- CVTRA05Y (transaction record) is the core entity shared by all 6 programs
- DALYTRAN (daily pending) → TRANSACT (master) flow is internal to this domain
- CBTRN02C (batch posting) is the critical pipeline program
- Bill payment (COBIL00C) creates transactions — same domain

**Candidate microservice:** `transaction-service`
- Owns `transactions`, `daily_transactions`, `tran_cat_balances` tables
- Batch component: Spring Batch job replacing CBTRN02C
- Exposes: GET /transactions, POST /transactions, POST /payments
- Depends on: `account-service` (balance updates), `transaction-type-service` (type lookup)

---

### Context 5: Transaction Type Configuration (DB2)

| Attribute | Value |
|-----------|-------|
| **Programs** | COTRTLIC, COTRTUPC, COBTUPDT |
| **LOC** | 3,800 |
| **Shared copybooks** | CSDB2RWY, CSDB2RPY, DCLTRTYP, DCLTRCAT |
| **Data owned** | DB2 TRANSACTION_TYPE, TRANSACTION_CATEGORY |
| **External reads from** | None |
| **External writes to** | None |

**Why this is a bounded context:**
- Completely isolated DB2 subsystem
- Unique copybook set (CSDB2RWY/RPY) not shared with any other domain
- DCLTRTYP/DCLTRCAT are DB2 DCLGEN outputs — pure schema definitions
- No VSAM file dependencies

**Candidate microservice:** `transaction-type-service`
- Owns `transaction_types`, `transaction_categories` tables
- Exposes: GET/POST/PUT/DELETE /transaction-types, /transaction-categories
- Zero dependencies on other services

---

### Context 6: Authorization Decision Engine

| Attribute | Value |
|-----------|-------|
| **Programs** | COPAUA0C, COPAUS0C, COPAUS1C, COPAUS2C (online); CBPAUP0C, PAUDBLOD, PAUDBUNL, DBUNLDGS (batch) |
| **LOC** | 3,640 |
| **Shared copybooks** | CIPAUSMY, CIPAUDTY, CCPAURQY, CCPAURLY, CCPAUERY, IMSFUNCS, PAUTBPCB, MQ copybooks |
| **Data owned** | IMS DBPAUTP0 (auth_summary + auth_detail segments), DB2 AUTHFRDS, MQ queues |
| **External reads from** | CVACT01Y, CVACT03Y, CVCUS01Y (account/customer lookups) |
| **External writes to** | None |

**Why this is a bounded context:**
- Unique technology stack: IMS + MQ + DB2 (no other context uses IMS or MQ)
- 9 exclusive copybooks (CIPAUSMY, CIPAUDTY, CCPAU*, IMSFUNCS, PCBs)
- XCTL chain: COPAUS0C → COPAUS1C → COPAUS2C (must migrate as unit)
- MQ message flow: Auth Request → COPAUA0C → Auth Reply (async protocol)

**Candidate microservice:** `authorization-service`
- Owns `auth_summaries`, `auth_details`, `fraud_flags` tables
- Async interface: SQS for request/reply pattern
- Exposes: POST /authorizations, GET /authorizations/{id}
- Depends on: `account-service` (account lookup for auth decision)

---

### Context 7: Reporting & Statements

| Attribute | Value |
|-----------|-------|
| **Programs** | CORPT00C (online trigger), CBSTM03A/B (statement generation), CBTRN03C (transaction report) |
| **LOC** | 2,210 |
| **Shared copybooks** | CVTRA05Y, CVACT01Y, CVACT03Y, CVCUS01Y |
| **Data owned** | Output files only (STMT-FILE, HTML-FILE, REPTFILE) |
| **External reads from** | TRANSACT, ACCTFILE, CARDXREF, CUSTFILE, TRANTYPE |
| **External writes to** | None (read-only consumers) |

**Why this is a bounded context:**
- Pure read-only consumers — never write to source data
- Output files (reports/statements) are terminal artifacts
- CBSTM03A calls CBSTM03B (I/O submodule) — internal composition
- CORPT00C triggers batch reports via JCL internal reader

**Candidate microservice:** `reporting-service`
- Generates statements and reports from read replicas
- Spring Batch + Thymeleaf for HTML output; PDF generation
- Exposes: POST /reports/generate, GET /statements/{id}
- Depends on: Read replicas of `account-service`, `transaction-service`, `card-service` data

---

### Context 8: Data Migration Utilities

| Attribute | Value |
|-----------|-------|
| **Programs** | CBEXPORT (579 LOC), CBIMPORT (484 LOC) |
| **LOC** | 1,063 |
| **Shared copybooks** | CVEXPORT, CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y, CVTRA05Y |
| **Data owned** | Export file (EXPFILE) — proprietary flat format |
| **External reads from** | All VSAM files (ACCTFILE, CARDFILE, CUSTFILE, CARDXREF, TRANSACT) |
| **External writes to** | All VSAM files (CBIMPORT validates and loads) |

**Why this is a bounded context:**
- Cross-cutting utility — touches all files but is not a business domain
- CVEXPORT copybook is unique to this context
- Ad-hoc execution (branch migration scenarios)
- Becomes obsolete once VSAM is decommissioned

**Candidate:** `data-migration-tools` (temporary utility, not a persistent microservice)

---

## Extraction Seams

An extraction seam is a point where two bounded contexts interact. The difficulty rating considers data coupling tightness, transaction consistency requirements, and protocol complexity.

### Seam Matrix

| Seam ID | From Context | To Context | Interaction Type | Shared Data | Difficulty |
|---------|-------------|-----------|-----------------|-------------|------------|
| S1 | Transaction Processing | Account Mgmt | Write (balance update) | ACCTFILE | **Hard** |
| S2 | Credit Card Mgmt | Account Mgmt | Read (account lookup) | ACCTFILE, CARDXREF | **Medium** |
| S3 | Authorization Engine | Account Mgmt | Read (account/customer data) | CVACT01Y, CVCUS01Y structures | **Medium** |
| S4 | Transaction Processing | Transaction Types | Read (type validation) | TRANTYPE, TRANCATG | **Easy** |
| S5 | Reporting | All domains | Read (all files) | All VSAM files | **Easy** |
| S6 | Identity & Access | All online programs | Read (user context) | COMMAREA (COCOM01Y) | **Easy** |
| S7 | Credit Card Mgmt | Transaction Processing | Read (card-to-txn lookup) | CARDXREF | **Medium** |
| S8 | Data Migration | All domains | Read/Write (all files) | All VSAM files | **Hard** |
| S9 | Authorization Engine | Transaction Processing | Async (auth decision) | MQ message queues | **Hard** |
| S10 | Account Mgmt | Credit Card Mgmt | Read (customer details) | CUSTFILE | **Medium** |

---

### Seam Details

#### S1: Transaction → Account (Balance Update) — HARD

```
CBTRN02C (Transaction Posting)
    │
    ├── READ TRANSACT (daily transactions)
    ├── VALIDATE transaction against TRANTYPE
    └── WRITE ACCTFILE (update account balance)  ◄── Cross-domain write
```

**Why Hard:**
- CBTRN02C directly updates ACCTFILE records (balance, last-transaction-date)
- This is a write to another domain's data store
- Must maintain transactional consistency (transaction + balance = atomic)
- During transition: dual-write required (VSAM + PostgreSQL)

**Extraction approach:**
- Replace direct VSAM REWRITE with API call to `account-service`
- Use distributed transaction pattern (Saga) or eventual consistency with SQS FIFO
- Implement reconciliation job (hourly checksum comparison)

---

#### S2: Credit Card → Account (Lookup) — MEDIUM

```
COCRDLIC / COCRDUPC
    │
    ├── READ CARDXREF (card → account mapping)
    └── READ ACCTFILE (account details for display)  ◄── Cross-domain read
```

**Why Medium:**
- Read-only dependency — no transactional complexity
- But high-frequency (every card list/update operation needs account data)
- Data freshness requirements are moderate (display only)

**Extraction approach:**
- REST call: `GET /accounts/{acctId}` from `card-service` to `account-service`
- Cache account data in card-service (TTL: 60s) to reduce latency
- CARDXREF ownership decision: assign to card-service (it's the primary consumer)

---

#### S4: Transaction → Transaction Types (Validation) — EASY

```
CBTRN02C / COTRN02C
    │
    └── READ TRANTYPE (validate transaction type code exists)  ◄── Simple lookup
```

**Why Easy:**
- Pure reference data lookup (type code → description)
- Data changes infrequently (admin-only updates)
- No write coupling
- Already on DB2 — migrates cleanly to separate service

**Extraction approach:**
- REST call: `GET /transaction-types/{code}` with aggressive caching (TTL: 24h)
- Or: replicate type data to transaction-service as read-only table (CQRS)

---

#### S5: Reporting → All Domains (Read-Only) — EASY

```
CBSTM03A / CBTRN03C / CORPT00C
    │
    ├── READ ACCTFILE
    ├── READ TRANSACT
    ├── READ CARDXREF
    ├── READ CUSTFILE
    └── WRITE output files (reports/statements)
```

**Why Easy:**
- Strictly read-only — no write coupling
- Reports are eventually consistent by nature (batch runs)
- Can read from database replicas with acceptable lag

**Extraction approach:**
- Reporting service reads from read replicas of all other service databases
- Event-driven: subscribe to domain events for real-time reporting (optional)
- Zero impact on source domains

---

#### S6: Identity → All Online Programs (Session Context) — EASY

```
COSGN00C
    │
    └── EXEC CICS XCTL COMMAREA(CARDDEMO-COMMAREA)  ◄── Session state passed
            │
            ├── COMEN01C (user ID, role in COMMAREA)
            ├── COACTUPC (reads CDEMO-USER-ID from COMMAREA)
            └── ... all online programs
```

**Why Easy:**
- COMMAREA is session state, not persistent data
- Maps directly to JWT token claims (user ID, role, session context)
- No data store coupling — purely in-memory protocol

**Extraction approach:**
- Replace COMMAREA with JWT token containing: userId, userType, fromProgram, pgmContext
- All services validate JWT via shared library (no inter-service call needed)

---

#### S9: Authorization → Transaction (Async Decision) — HARD

```
Transaction Program
    │
    └── MQPUT1 (Auth Request Queue)
            │
            ▼
    COPAUA0C (Authorization Decision)
            │
            ├── IMS DL/I lookup (auth history)
            ├── DB2 lookup (fraud flags)
            └── MQPUT1 (Auth Reply Queue)
                    │
                    ▼
            Transaction Program (receives decision)
```

**Why Hard:**
- Asynchronous request/reply pattern with message correlation
- Crosses three protocols: MQ → IMS → DB2
- Real-time latency requirements (auth decisions must be fast)
- Message format is fixed-length COBOL records (CCPAURQY/CCPAURLY)

**Extraction approach:**
- Phase 1: SQS adapter alongside MQ (dual-publish)
- Phase 2: Java authorization service consumes from SQS, queries PostgreSQL
- Phase 3: Message format migration (COBOL records → JSON)
- Must maintain sub-second response time throughout

---

## Copybook-to-Context Mapping

| Copybook | Context(s) | Sharing Type |
|----------|-----------|--------------|
| COCOM01Y | All online (17 programs) | Framework — shared library |
| COTTL01Y | All online (17 programs) | Framework — shared library |
| CSDAT01Y | All online (17 programs) | Framework — shared library |
| CSMSG01Y | All online (17 programs) | Framework — shared library |
| CSUSR01Y | Identity (12 programs) | Context-owned |
| CVACT01Y | Account, Card, Transaction, Reporting (14 programs) | **Cross-context** |
| CVACT02Y | Card (7 programs) | Context-owned |
| CVACT03Y | Account, Card, Transaction, Auth (14 programs) | **Cross-context** |
| CVCUS01Y | Account, Card, Auth (8 programs) | **Cross-context** |
| CVCRD01Y | Card (5 programs) | Context-owned |
| CVTRA05Y | Transaction, Reporting (11 programs) | Context-owned + read share |
| CIPAUSMY/CIPAUDTY | Authorization (8 programs) | Context-exclusive |
| CSDB2RWY/CSDB2RPY | Transaction Types (2 programs) | Context-exclusive |
| CVEXPORT | Data Migration (2 programs) | Context-exclusive |

**Key insight:** CVACT01Y (account record) and CVACT03Y (cross-reference) are the most shared structures — they define the primary coupling seam between Account, Card, Transaction, and Authorization contexts.

---

## Data File Ownership

| File | Owner Context | Readers | Writers | Isolation Score |
|------|--------------|---------|---------|-----------------|
| USRSEC | Identity & Access | 7 | 4 | **High** (exclusive) |
| ACCTFILE | Account Mgmt | 11 | 5 | **Low** (highly shared) |
| CUSTFILE | Account Mgmt | 7 | 1 | Medium |
| CARDFILE | Credit Card | 9 | 2 | Medium |
| CARDXREF | Credit Card | 6 | 1 | Medium |
| TRANSACT | Transaction | 21 | 2 | **Low** (most shared file) |
| DALYTRAN | Transaction | 2 | 1 | **High** (exclusive) |
| TCATBALF | Transaction | 2 | 1 | **High** (exclusive) |
| TRANTYPE | Transaction Types | 3 | 1 | **High** (exclusive) |
| TRANCATG | Transaction Types | 1 | 0 | **High** (exclusive) |
| DISCGRP | Transaction | 1 | 0 | **High** (exclusive) |
| IMS DB | Authorization | 8 | 4 | **High** (exclusive) |
| DB2 tables | Auth + Tran Types | 4 | 3 | **High** (per-context) |
| MQ queues | Authorization | 4 | 2 | **High** (exclusive) |

---

## Target Microservice Architecture

```
┌─────────────────────────────────────────────────────────────────────┐
│                        API Gateway (Spring Cloud)                     │
└────────┬──────────┬──────────┬──────────┬──────────┬────────────────┘
         │          │          │          │          │
   ┌─────▼────┐ ┌──▼───┐ ┌───▼────┐ ┌───▼───┐ ┌───▼──────┐
   │user-auth │ │card  │ │account │ │trans- │ │tran-type │
   │-service  │ │-svc  │ │-service│ │action │ │-service  │
   └──────────┘ └──┬───┘ └───┬────┘ │-svc   │ └──────────┘
                    │         │      └───┬───┘
                    └────►────┘          │
                                    ┌────▼────┐
                              ┌─────┤  SQS    ├─────┐
                              │     └─────────┘     │
                         ┌────▼─────┐         ┌────▼────────┐
                         │authorize │         │reporting    │
                         │-service  │         │-service     │
                         └──────────┘         └─────────────┘
```

---

## JCL Job Groupings (Batch Context Clusters)

| Cluster | Jobs | Context | Scheduling |
|---------|------|---------|-----------|
| Daily Transaction Pipeline | POSTTRAN → INTCALC → CREASTMT → TRANRPT | Transaction + Reporting | Daily sequential chain |
| Data Setup (IDCAMS) | ACCTFILE, CARDFILE, CUSTFILE, CARDXREF, TRANTYPE, TRANCATG, DISCGRP, TCATBAL | All (infrastructure) | One-time/on-demand |
| Branch Migration | EXPDATA, IMPDATA | Data Migration | Weekly ad-hoc |
| Authorization Batch | PAUDBLOD, PAUDBUNL, DBUNLDGS, CBPAUP0C | Authorization | Daily (purge expired) |
| CICS System | CICSDEF, DFHCSDUP, NEWCOPY | Infrastructure | On-demand |
| Reference Data | DISCGRP-LOAD, TRANTYPE-LOAD, TRANCATG-LOAD | Transaction Types | On-demand |

---

## Extraction Priority (Based on Isolation + Value)

| Priority | Context | Isolation Score | Business Value | Extraction Order |
|----------|---------|----------------|---------------|-----------------|
| 1 | Transaction Types (DB2) | Very High | Medium | First — zero dependencies |
| 2 | Identity & Access | High | High | Second — enables auth for all services |
| 3 | Reporting & Statements | High (read-only) | Medium | Third — no write coupling |
| 4 | Credit Card Mgmt | Medium | High | Fourth — moderate coupling |
| 5 | Account Management | Low | Very High | Fifth — high coupling but high value |
| 6 | Transaction Processing | Low | Very High | Sixth — 21-program dependency |
| 7 | Authorization Engine | High (exclusive) but complex | High | Last — multi-protocol complexity |
| — | Data Migration | N/A | Temporary | Parallel — replatform and retire |
