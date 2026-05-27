# CardDemo Domain Decomposition

## Overview

This document identifies bounded contexts within the CardDemo application by analyzing three dimensions of coupling:
1. **Copybook sharing** — which programs include the same data structure definitions
2. **JCL job grouping** — which programs execute together in batch chains
3. **Data file sharing** — which VSAM files are read/written by which programs

Each bounded context maps to a candidate microservice or module in the target architecture.

---

## Coupling Analysis

### Copybook Dependency Matrix

The following matrix shows which copybooks are used by which programs. Copybooks that are shared across many programs indicate cross-cutting concerns; copybooks shared by a few programs indicate a bounded context.

#### Universally Shared (Cross-Cutting)
These copybooks appear in nearly all CICS programs and represent framework-level concerns, not domain boundaries:

| Copybook | Used By | Purpose |
|----------|---------|---------|
| COCOM01Y | 17 programs | COMMAREA structure (inter-program communication) |
| COTTL01Y | 17 programs | Screen title/header layout |
| CSDAT01Y | 17 programs | Date display fields |
| CSMSG01Y | 17 programs | Message display fields |
| DFHAID | 17 programs | CICS attention identifier definitions |
| DFHBMSCA | 17 programs | BMS character attribute definitions |
| CSSETATY | 14 programs (via REPLACING) | BMS field attribute setting |

#### Domain-Specific Copybook Clusters

**Cluster A — Account/Card/Customer Core:**
| Copybook | Programs | Entity |
|----------|----------|--------|
| CVACT01Y (Account Record) | COACTVWC, COACTUPC, COBIL00C, COTRN02C, COCRDSLC, COCRDUPC, CBACT01C, CBACT04C, CBTRN01C, CBTRN02C, CBEXPORT, CBIMPORT, CBSTM03A | Account |
| CVACT02Y (Card Record) | COACTVWC, COCRDLIC, COCRDSLC, COCRDUPC, CBACT02C, CBEXPORT, CBIMPORT | Card |
| CVACT03Y (Card XRef) | COACTVWC, COACTUPC, COBIL00C, COTRN02C, COCRDSLC, COCRDUPC, CBACT03C, CBACT04C, CBTRN01C, CBTRN02C, CBTRN03C, CBEXPORT, CBIMPORT, CBSTM03A | Cross-Reference |
| CVCUS01Y (Customer Record) | COACTVWC, COACTUPC, COCRDSLC, COCRDUPC, CBCUS01C, CBTRN01C, CBEXPORT, CBIMPORT | Customer |
| CVCRD01Y (Card Work Areas) | COACTUPC, COACTVWC, COCRDLIC, COCRDSLC, COCRDUPC | Card (CICS work) |

**Cluster B — Transaction Domain:**
| Copybook | Programs | Entity |
|----------|----------|--------|
| CVTRA05Y (Transaction Record) | COTRN00C, COTRN01C, COTRN02C, COBIL00C, CORPT00C, CBTRN01C, CBTRN02C, CBTRN03C, CBACT04C, CBEXPORT, CBIMPORT | Transaction |
| CVTRA06Y (Daily Transaction) | CBTRN01C, CBTRN02C | Daily Transaction |
| CVTRA01Y (Category Balance) | CBACT04C, CBTRN02C | Transaction Category Balance |
| CVTRA02Y (Disclosure Groups) | CBACT04C | Disclosure |
| CVTRA03Y (Transaction Types) | CBTRN03C | Transaction Type |
| CVTRA04Y (Transaction Categories) | CBTRN03C | Transaction Category |
| CVTRA07Y (Report Headers) | CBTRN03C | Reporting |

**Cluster C — User Security:**
| Copybook | Programs | Entity |
|----------|----------|--------|
| CSUSR01Y (User Security Record) | COSGN00C, COUSR00C, COUSR01C, COUSR02C, COUSR03C, COMEN01C, COADM01C, COACTUPC, COACTVWC, COCRDLIC, COCRDSLC, COCRDUPC | User/Security |

**Cluster D — Navigation/Menu:**
| Copybook | Programs | Entity |
|----------|----------|--------|
| COMEN02Y (User Menu Options) | COMEN01C | Menu Config |
| COADM02Y (Admin Menu Options) | COADM01C | Menu Config |

**Cluster E — Data Migration:**
| Copybook | Programs | Entity |
|----------|----------|--------|
| CVEXPORT (Multi-record layout) | CBEXPORT, CBIMPORT | Export/Import |

**Cluster F — Statement Generation:**
| Copybook | Programs | Entity |
|----------|----------|--------|
| COSTM01 (Statement layout) | CBSTM03A | Statement |
| CUSTREC (Customer record alt) | CBSTM03A | Customer (alt) |

---

### JCL Job Chain Analysis

#### Daily — Transaction Backup
```
CLOSEFIL → TRANBKP → WAITSTEP → OPENFIL
```
- **CLOSEFIL**: Closes CICS VSAM files (SET FILE CLOSED via CEMT)
- **TRANBKP**: IDCAMS REPRO of TRANSACT VSAM → GDG sequential backup, then DELETE/DEFINE/REPRO to reload with daily transactions merged
- **WAITSTEP**: Invokes COBSWAIT to pause between batch steps
- **OPENFIL**: Reopens CICS VSAM files (SET FILE OPEN via CEMT)

**Data touched:** TRANSACT, DALYTRAN (all transaction-domain)

#### Weekly — Reference Data Refresh
```
MNTTRDB2 → [DisclosureGroupsRefresh: CLOSEFIL → DISCGRP → WAITSTEP → OPENFIL]
                                     + [TransactionTypesDBRefresh: TRANEXTR]
```
- **MNTTRDB2**: Db2 transaction type maintenance
- **DISCGRP**: IDCAMS REPRO for Disclosure Group VSAM from sequential
- **TRANEXTR**: Extracts transaction types from Db2 to VSAM

**Data touched:** DISCGRP, TRANTYPE, TRANCATG (reference data domain)

#### Monthly — Interest Calculation
```
CLOSEFIL → INTCALC → COMBTRAN → WAITSTEP → OPENFIL
```
- **INTCALC**: Runs CBACT04C with date parameter against TCATBALF, CCXREF (via AIX), ACCTDAT, DISCGRP; outputs interest transactions to GDG
- **COMBTRAN**: Merges interest-generated transactions into transaction master

**Data touched:** TCATBALF, CCXREF, ACCTDAT, DISCGRP, TRANSACT (crosses account + transaction + reference data)

#### Ad-Hoc — Reporting
```
TRANREPT: REPROC (VSAM→sequential) → SORT (filter/sort by date/card) → CBTRN03C (format report)
```
**Data touched:** TRANSACT → sequential GDG → sorted subset → formatted report

#### Ad-Hoc — Data Migration
```
CBEXPORT: Reads all entity files → writes multi-record sequential export
CBIMPORT: Reads sequential export → loads all entity files
```
**Data touched:** ALL entity VSAM files (ACCTDAT, CARDDAT, CUSTDAT, CCXREF, TRANSACT)

---

### Data File Sharing Matrix

| VSAM File | Online Programs | Batch Programs | Access |
|-----------|----------------|----------------|--------|
| **ACCTDAT** | COACTVWC(R), COACTUPC(RW), COBIL00C(RW), COTRN02C(R) | CBACT01C(R), CBACT04C(RW), CBTRN01C(RW), CBTRN02C(RW), CBEXPORT(R), CBIMPORT(W), CBSTM03A(R) | Heavy sharing |
| **CARDDAT** | COACTVWC(R), COCRDLIC(R), COCRDSLC(R), COCRDUPC(RW) | CBACT02C(R), CBEXPORT(R), CBIMPORT(W) | Moderate sharing |
| **CARDAIX** | COCRDLIC(R) | — | Isolated |
| **CCXREF** | COACTVWC(R), COACTUPC(R), COBIL00C(R), COTRN02C(R), COCRDSLC(R), COCRDUPC(R) | CBACT03C(R), CBACT04C(R), CBTRN01C(R), CBTRN02C(R), CBEXPORT(R), CBIMPORT(W), CBSTM03A(R) | Heaviest sharing |
| **CXACAIX** | — | CBACT04C(R) | Isolated |
| **CUSTDAT** | COACTVWC(R), COACTUPC(R), COCRDSLC(R), COCRDUPC(R) | CBCUS01C(R), CBEXPORT(R), CBIMPORT(W), CBSTM03A(R) | Moderate sharing |
| **TRANSACT** | COTRN00C(R), COTRN01C(R), COTRN02C(RW), COBIL00C(RW) | CBTRN02C(RW), CBTRN03C(R), CBEXPORT(R), CBIMPORT(W) | Heavy sharing |
| **DALYTRAN** | — | CBTRN01C(R), CBTRN02C(R) | Batch-only |
| **USRSEC** | COSGN00C(R), COUSR00C(R), COUSR01C(RW), COUSR02C(RW), COUSR03C(RW) | — | Isolated domain |
| **TCATBALF** | — | CBACT04C(RW), CBTRN02C(RW) | Batch-only |
| **DISCGRP** | — | CBACT04C(R) | Batch-only, ref data |
| **TRANCATG** | — | CBTRN03C(R) | Batch-only, ref data |
| **TRANTYPE** | — | CBTRN03C(R) | Batch-only, ref data |

---

## Bounded Contexts

Based on the convergence of copybook clusters, JCL job chains, and data file sharing, we identify six bounded contexts:

### BC1: Identity & Access Management

**Scope:** Authentication, authorization, and user lifecycle management.

| Attribute | Details |
|-----------|---------|
| **Programs** | COSGN00C, COUSR00C, COUSR01C, COUSR02C, COUSR03C |
| **Data Stores** | USRSEC (exclusive owner) |
| **Copybooks (domain)** | CSUSR01Y |
| **BMS Maps** | COSGN00, COUSR00–03 |
| **Integration Points** | Read-only by: COMEN01C, COADM01C (role check), all CICS programs (COMMAREA user type) |
| **Data Isolation** | **High** — USRSEC is not accessed by any batch program |

**Candidate Microservice: `identity-service`**
- REST API: `/auth/login`, `/users` CRUD
- Database: Dedicated `users` table (or external IAM provider)
- Publishes: JWT tokens consumed by all other services
- Consumes: Nothing

---

### BC2: Account & Customer Management

**Scope:** Account lifecycle, customer data, and the card-to-account cross-reference that links the core entities.

| Attribute | Details |
|-----------|---------|
| **Programs** | COACTVWC, COACTUPC, CBACT01C, CBCUS01C |
| **Data Stores** | ACCTDAT (primary owner), CUSTDAT (primary owner), CCXREF (primary owner), CXACAIX |
| **Copybooks (domain)** | CVACT01Y, CVCUS01Y, CVACT03Y, CVCRD01Y, CSLKPCDY |
| **BMS Maps** | COACTVW, COACTUP |
| **Integration Points** | Read by: Credit Card context (BC3), Transaction context (BC4), Billing context (BC5), Interest Calc (BC4 batch), Reporting (BC6), Export/Import |
| **Data Isolation** | **Low** — ACCTDAT and CCXREF are the most widely shared files |

**Candidate Microservice: `account-service`**
- REST API: `/accounts`, `/customers`, `/accounts/{id}/cards` (cross-reference)
- Database: `accounts`, `customers`, `card_xref` tables (PostgreSQL)
- Publishes: Account balance change events
- Consumes: Nothing directly (other services call its API)

> **Design Note:** CCXREF is the gravitational center of the data model, joining Cards↔Accounts↔Customers. It belongs in the Account context because account is the aggregate root (cards and customers reference accounts). Other bounded contexts read it via the Account Service API.

---

### BC3: Credit Card Management

**Scope:** Card issuance, listing, viewing, and updating card details.

| Attribute | Details |
|-----------|---------|
| **Programs** | COCRDLIC, COCRDSLC, COCRDUPC, CBACT02C, CBACT03C |
| **Data Stores** | CARDDAT (primary owner), CARDAIX |
| **Copybooks (domain)** | CVACT02Y, CVCRD01Y |
| **BMS Maps** | COCRDLI, COCRDSL, COCRDUP |
| **Integration Points** | Reads CCXREF, ACCTDAT, CUSTDAT from BC2 |
| **Data Isolation** | **Medium** — CARDDAT is primary, but reads BC2 data for display |

**Candidate Microservice: `card-service`**
- REST API: `/cards`, `/cards/{num}`, `/cards/search`
- Database: `cards` table
- Publishes: Card status change events
- Consumes: `account-service` API (for account/customer context on card views)

> **Design Note:** In the current system, COCRDSLC and COCRDUPC read account, customer, and xref data to display alongside card details. In the microservice architecture, these become API calls to `account-service`. The CARDAIX alternate index becomes a standard database secondary index.

---

### BC4: Transaction Processing

**Scope:** Transaction entry, listing, viewing, daily batch posting, category balance management, and interest calculation.

| Attribute | Details |
|-----------|---------|
| **Programs** | COTRN00C, COTRN01C, COTRN02C, CBTRN01C, CBTRN02C, CBACT04C |
| **Data Stores** | TRANSACT (primary owner), DALYTRAN, TCATBALF, DISCGRP |
| **Copybooks (domain)** | CVTRA05Y, CVTRA06Y, CVTRA01Y, CVTRA02Y |
| **BMS Maps** | COTRN00, COTRN01, COTRN02 |
| **JCL Jobs** | POSTTRAN, INTCALC, COMBTRAN, TRANBKP |
| **Scheduling** | Daily backup chain, Monthly interest calculation chain |
| **Integration Points** | Reads CCXREF, ACCTDAT from BC2; writes interest transactions to ACCTDAT |
| **Data Isolation** | **Medium** — TRANSACT, DALYTRAN, TCATBALF are exclusive; DISCGRP shared with reference data |

**Candidate Microservice: `transaction-service`**
- REST API: `/transactions`, `/transactions/{id}`, `/transactions/daily`
- Database: `transactions`, `daily_transactions`, `category_balances` tables
- Event consumers: Daily posting pipeline, monthly interest calculation
- Publishes: Transaction created/posted events
- Consumes: `account-service` API (for card→account resolution via xref)

> **Design Note:** Interest calculation (CBACT04C) crosses the Account and Transaction boundaries — it reads category balances and disclosure groups, then writes interest charges to accounts. In the target architecture, this becomes a scheduled job within `transaction-service` that calls `account-service` to apply balance updates. The DISCGRP reference data moves to the Reference Data context (BC6) and is consumed via API.

---

### BC5: Billing & Payments

**Scope:** Bill payment processing (pay account balance in full or partial).

| Attribute | Details |
|-----------|---------|
| **Programs** | COBIL00C |
| **Data Stores** | None exclusively owned; reads/writes ACCTDAT, CCXREF, TRANSACT |
| **Copybooks (domain)** | Reuses CVACT01Y, CVACT03Y, CVTRA05Y |
| **BMS Maps** | COBIL00 |
| **Integration Points** | Heavy dependency on BC2 (Account) and BC4 (Transaction) |
| **Data Isolation** | **None** — purely orchestrates BC2 and BC4 data |

**Candidate Module: Part of `transaction-service` or thin `payment-service`**
- REST API: `/payments`
- Database: None (or payments audit log)
- Consumes: `account-service` API (read balance), `transaction-service` API (create payment transaction)

> **Design Note:** COBIL00C is a thin orchestrator with no exclusive data. It could be a separate microservice for payment processing compliance and audit requirements, or a module within `transaction-service`. The decision depends on whether the organization needs independent payment processing scalability and audit trails.

---

### BC6: Reporting, Statements & Reference Data

**Scope:** Transaction reporting, statement generation, and management of reference data (transaction types, categories, disclosure groups).

| Attribute | Details |
|-----------|---------|
| **Programs** | CORPT00C, CBTRN03C, CBSTM03A, CBSTM03B, PRTCATBL JCL |
| **Data Stores** | TRANCATG (owner), TRANTYPE (owner), DISCGRP (shared with BC4) |
| **Copybooks (domain)** | CVTRA03Y, CVTRA04Y, CVTRA07Y, COSTM01, CUSTREC |
| **BMS Maps** | CORPT00 |
| **JCL Jobs** | TRANREPT (uses SORT + REPROC proc), CREASTMT, DISCGRP, TRANCATG, TRANTYPE |
| **Scheduling** | Weekly reference data refresh (MNTTRDB2 → DISCGRP/TRANEXTR) |
| **Integration Points** | Reads TRANSACT from BC4, ACCTDAT/CUSTDAT/CCXREF from BC2 |
| **Data Isolation** | **Medium** — reference data files are exclusive; reads heavily from other contexts |

**Candidate Microservices:**
1. **`reporting-service`**
   - REST API: `/reports/transactions`, `/statements`
   - Database: Read replicas of transaction and account data
   - Consumes: `transaction-service` and `account-service` APIs (or reads from shared read model)

2. **`reference-data-service`**
   - REST API: `/reference/transaction-types`, `/reference/categories`, `/reference/disclosure-groups`
   - Database: `transaction_types`, `transaction_categories`, `disclosure_groups` tables
   - Publishes: Reference data change events
   - Consumes: Nothing

---

### Cross-Context: Data Migration

**Programs:** CBEXPORT, CBIMPORT
**Scope:** Bulk data export/import across all entity types for branch migration.

This is not a bounded context but a cross-cutting infrastructure concern. In the target architecture, it becomes:
- An ETL pipeline in AWS Glue or Spring Batch
- Reads from all microservice databases (or their events)
- Produces standard-format exports (JSON/CSV/Parquet)

---

## Bounded Context Map

```
┌─────────────────────────────────────────────────────────────────────────┐
│                         CardDemo System                                │
│                                                                        │
│  ┌──────────────┐    authenticates    ┌──────────────────────────────┐ │
│  │ BC1: Identity │◄──────────────────│ BC9: Frontend (SPA)           │ │
│  │  & Access     │    all requests    │  (replaces COMEN01C/COADM01C │ │
│  │               │                    │   + BMS maps)                 │ │
│  └──────────────┘                    └──────┬───────────────────────┘ │
│                                              │ calls                   │
│                              ┌───────────────┼───────────────┐        │
│                              ▼               ▼               ▼        │
│                   ┌─────────────────┐ ┌────────────┐ ┌────────────┐  │
│                   │ BC2: Account &   │ │ BC3: Card  │ │ BC4: Txn   │  │
│                   │  Customer Mgmt   │ │ Management │ │ Processing │  │
│                   │                  │ │            │ │            │  │
│                   │ ACCTDAT,CUSTDAT, │ │ CARDDAT,   │ │ TRANSACT,  │  │
│                   │ CCXREF           │ │ CARDAIX    │ │ DALYTRAN,  │  │
│                   └────────┬─────────┘ └─────┬──────┘ │ TCATBALF   │  │
│                            │                  │        └─────┬──────┘  │
│                            │   reads xref     │              │         │
│                            ◄──────────────────┘              │         │
│                            │   reads account                 │         │
│                            ◄─────────────────────────────────┘         │
│                            │                                           │
│                   ┌────────┴─────────┐              ┌──────────────┐  │
│                   │ BC5: Billing &   │              │ BC6: Reporting│  │
│                   │  Payments        │              │ & Ref Data    │  │
│                   │                  │              │               │  │
│                   │ (orchestrates    │              │ TRANCATG,     │  │
│                   │  BC2 + BC4)      │              │ TRANTYPE,     │  │
│                   └──────────────────┘              │ DISCGRP       │  │
│                                                     └───────────────┘  │
└─────────────────────────────────────────────────────────────────────────┘
```

---

## Candidate Microservice Summary

| Microservice | Bounded Context | Programs Migrated | Primary Database Tables | API Count (est.) |
|-------------|-----------------|-------------------|------------------------|-----------------|
| `identity-service` | BC1 | 5 CICS programs | `users`, `roles` | 6 |
| `account-service` | BC2 | 2 CICS + 2 batch | `accounts`, `customers`, `card_xref` | 10 |
| `card-service` | BC3 | 3 CICS + 2 batch | `cards` | 6 |
| `transaction-service` | BC4 + BC5 | 4 CICS + 3 batch | `transactions`, `daily_transactions`, `category_balances` | 12 |
| `reporting-service` | BC6 (reports) | 1 CICS + 3 batch | Read replicas / materialized views | 4 |
| `reference-data-service` | BC6 (ref data) | Optional Db2 programs | `transaction_types`, `categories`, `disclosure_groups` | 6 |
| **Frontend SPA** | Navigation | COMEN01C, COADM01C | — | — |

**Total:** 6 microservices + 1 frontend application replacing 30+ COBOL programs and 38 JCL jobs.

---

## Data Ownership Rules

1. Each microservice owns its database tables exclusively (no shared databases).
2. Cross-context data access occurs only via synchronous API calls or asynchronous events.
3. The `card_xref` table (currently CCXREF) lives in `account-service` because Account is the aggregate root.
4. Read-heavy cross-context queries (e.g., reporting) use materialized views populated by events, not direct database access.
5. Reference data is published as events on change; consumers cache locally.
