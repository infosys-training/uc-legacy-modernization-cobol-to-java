# CardDemo Domain Decomposition

> Bounded context identification, candidate microservice mapping, and extraction seam analysis for the CardDemo COBOL estate.

---

## 1. Methodology

Bounded contexts are identified by analyzing three coupling dimensions:

1. **Copybook sharing** — Programs that share the same data-structure copybooks operate on the same domain entities
2. **JCL job grouping** — JCL jobs that execute programs together or share datasets form a processing pipeline
3. **Data file isolation** — VSAM files that are written by a single domain vs. shared across domains indicate natural vs. forced boundaries

---

## 2. Bounded Contexts

### 2.1 Account Context

**Core Entity:** Account (CVACT01Y — ACCTDATA.VSAM.KSDS)

| Attribute | Value |
|-----------|-------|
| Programs | COACTUPC (4,236 LOC), COACTVWC (941), COACCT01 (620), CBACT01C (430), CBACT04C (652) |
| Total LOC | 6,879 |
| Owned Data | ACCTDATA.VSAM.KSDS, TCATBALF.VSAM.KSDS, DISCGRP.VSAM.KSDS |
| Shared Copybooks | CVACT01Y (account record), CVTRA01Y (category balance), CVTRA02Y (disclosure group) |
| Internal Coupling | CBACT04C reads TCATBALF + DISCGRP + ACCTFILE and rewrites ACCTFILE (interest calculation is internal to this context) |

**Why this is a bounded context:** Account data (balance, limits, status, dates) is the primary concern. Interest calculation (CBACT04C) and category balance tracking (TCATBALF) are account-centric operations. COACCT01 (MQ adapter) provides an external query interface for account data.

**External consumers of Account data:** Transaction Processing (balance updates), Card Management (account lookup for card context), Bill Payment (balance debit), Reporting (statement account info).

---

### 2.2 Card Context

**Core Entity:** Card (CVACT02Y — CARDDATA.VSAM.KSDS) + Cross-Reference (CVACT03Y — CARDXREF.VSAM.KSDS)

| Attribute | Value |
|-----------|-------|
| Programs | COCRDLIC (1,459 LOC), COCRDSLC (887), COCRDUPC (1,560), CBACT02C (178), CBACT03C (178) |
| Total LOC | 4,262 |
| Owned Data | CARDDATA.VSAM.KSDS, CARDXREF.VSAM.KSDS |
| Shared Copybooks | CVACT02Y (card record), CVACT03Y (card-xref record) |
| Internal Coupling | All 3 online programs read CARDXREF to resolve card→account; COCRDSLC joins card+account+customer for display |

**Why this is a bounded context:** Card lifecycle (issuance, status, expiration) is self-contained. The card cross-reference (CVACT03Y) is the universal join table linking cards → accounts → customers. While CARDXREF is *read* by 14 programs, it is only *written* by CBIMPORT (bulk load).

**Boundary decision — CARDXREF ownership:** CARDXREF is the most-accessed dataset (14 programs). It logically belongs to the Card context because it describes the card-to-account-to-customer relationship. Other contexts access it via a Card Service API.

---

### 2.3 Transaction Context

**Core Entity:** Transaction (CVTRA05Y — TRANSACT.VSAM.KSDS)

| Attribute | Value |
|-----------|-------|
| Programs | COTRN00C (699 LOC), COTRN01C (330), COTRN02C (783), CBTRN01C (494), CBTRN02C (731), COBIL00C (572) |
| Total LOC | 3,609 |
| Owned Data | TRANSACT.VSAM.KSDS, DALYTRAN.PS (daily input feed) |
| Shared Copybooks | CVTRA05Y (transaction record), CVTRA06Y (daily transaction), CVTRA03Y (type), CVTRA04Y (category) |
| JCL Pipeline | POSTTRAN.jcl (CBTRN02C) → COMBTRAN.jcl (merge) → TRANBKP.jcl (backup) |

**Why this is a bounded context:** Transaction lifecycle (create, post, query) is a distinct workflow. The daily batch pipeline (DALYTRAN → posting → master) is self-contained in its orchestration. Bill payment (COBIL00C) creates transactions and updates account balance — it sits at the boundary between Transaction and Account contexts.

**Cross-context coupling:** CBTRN02C (posting) writes to ACCTFILE (Account context) and TCATBALF (Account context) during posting. This is the **tightest cross-domain coupling in the estate**.

---

### 2.4 Transaction Type Context (DB2)

**Core Entity:** Transaction Type (DB2 table) + Transaction Category (DB2 table)

| Attribute | Value |
|-----------|-------|
| Programs | COTRTLIC (2,098 LOC), COTRTUPC (1,702), COBTUPDT (237) |
| Total LOC | 4,037 |
| Owned Data | DB2 TRANSACTION_TYPE table, DB2 TRANSACTION_CATEGORY table |
| Shared Copybooks | CSDB2RWY (DB2 return codes) |
| JCL | DEFGDGD.jcl (DB2 backup/define) |

**Why this is a bounded context:** Completely isolated. Uses DB2 (not VSAM), has no file-level coupling with any other context. Reference data consumed by Transaction and Reporting contexts via type/category code lookups.

---

### 2.5 Authorization Context (IMS/MQ)

**Core Entity:** Authorization Summary + Detail (IMS hierarchical segments)

| Attribute | Value |
|-----------|-------|
| Programs | COPAUA0C (1,026 LOC), COPAUS0C (1,032), COPAUS1C (604), COPAUS2C (244), CBPAUP0C (386), PAUDBLOD (369), PAUDBUNL (317), DBUNLDGS (366) |
| Total LOC | 4,344 |
| Owned Data | IMS AUTH_SUMMARY segment, IMS AUTH_DETAIL segment, MQ request/response queues |
| Own Copybooks | CIPAUSMY (auth summary), CIPAUDTY (auth detail), CCPAUERY/CCPAURLY/CCPAURQY (MQ messages), IMSFUNCS, PADFLPCB/PASFLPCB/PAUTBPCB (IMS PCBs) |
| JCL | PAUDBLOD.jcl (IMS load), PAUDBUNL.jcl (IMS unload), INTRDRJ1/J2.JCL (IMS file creation) |

**Why this is a bounded context:** Physically isolated subsystem with its own data store (IMS), messaging layer (MQ), and copybooks. The only cross-context coupling is COPAUS0C reading ACCTFILE and CUSTFILE for display enrichment (lookup, not write).

**Internal structure:** COPAUS0C → COPAUS1C → COPAUS2C form a CICS LINK chain (not XCTL) — they share COMMAREA and must migrate as a single unit.

---

### 2.6 User Security Context

**Core Entity:** Security User (CSUSR01Y — USRSEC.VSAM.KSDS)

| Attribute | Value |
|-----------|-------|
| Programs | COSGN00C (260 LOC), COUSR00C (695), COUSR01C (299), COUSR02C (414), COUSR03C (359) |
| Total LOC | 2,027 |
| Owned Data | USRSEC.VSAM.KSDS |
| Shared Copybooks | CSUSR01Y (user record) |
| JCL | DUSRSECJ.jcl (user file setup) |

**Why this is a bounded context:** Completely isolated data store. USRSEC is read/written only by these 5 programs + the menu hubs (which check user type for routing). No other context writes to USRSEC.

---

### 2.7 Navigation Context (Thin — Eliminate in Target)

**Programs:** COADM01C (288 LOC), COMEN01C (308 LOC) — **596 LOC total**

| Attribute | Value |
|-----------|-------|
| Owned Data | None — pure routing |
| Shared Copybooks | COADM02Y (admin menu options), COMEN02Y (user menu options) |

**Why this is a bounded context (to eliminate):** Menu hubs are pure CICS XCTL routing tables. They have no business logic and no data. In a REST API architecture, their function is replaced by API gateway routing. They should be **eliminated, not migrated**.

---

### 2.8 Reporting Context

**Programs:** CBSTM03A (924 LOC), CBSTM03B (230), CBTRN03C (649), CORPT00C (649) — **2,452 LOC total**

| Attribute | Value |
|-----------|-------|
| Owned Data | STATEMNT.PS, STATEMNT.HTML, REPTFILE (all output-only) |
| Shared Copybooks | COSTM01 (statement layout), CVTRA07Y (report layout) |
| JCL Pipeline | CREASTMT.JCL (SORT → CBSTM03A → CBSTM03B), TRANREPT.jcl (REPROC → SORT → CBTRN03C) |

**Why this is a bounded context:** Read-only consumers of data from Account, Card, Customer, and Transaction contexts. Write only to isolated output files (statements, reports). No feedback writes to any shared data store.

---

### 2.9 Data Migration Context (Temporary)

**Programs:** CBEXPORT (582 LOC), CBIMPORT (487 LOC) — **1,069 LOC total**

| Attribute | Value |
|-----------|-------|
| Data | EXPORT.DATA (intermediary), reads/writes ALL 5 core VSAM files |

**Why this is a bounded context:** Utility programs for data portability. CBIMPORT is the only program that writes to CUSTFILE, CARDXREF, and CARDFILE (bulk load). This context is temporary — it exists to support the VSAM→PostgreSQL migration and is retired afterward.

---

## 3. Candidate Microservice Mapping

| # | Microservice | Bounded Context | Programs | Primary Data Store | API Style |
|---|-------------|----------------|----------|-------------------|-----------|
| 1 | **account-service** | Account | COACTUPC, COACTVWC, COACCT01, CBACT01C, CBACT04C | `accounts`, `tran_cat_balances`, `disclosure_groups` tables | REST + async events |
| 2 | **card-service** | Card | COCRDLIC, COCRDSLC, COCRDUPC, CBACT02C, CBACT03C | `cards`, `card_xref` tables | REST |
| 3 | **transaction-service** | Transaction | COTRN00C/01C/02C, CBTRN01C, CBTRN02C, COBIL00C | `transactions`, `daily_transactions` tables | REST + Spring Batch |
| 4 | **transaction-type-service** | Transaction Type | COTRTLIC, COTRTUPC, COBTUPDT | `transaction_types`, `transaction_categories` tables | REST |
| 5 | **authorization-service** | Authorization | COPAUA0C, COPAUS0C/1C/2C, CBPAUP0C, PAUDBLOD/UNL, DBUNLDGS | `auth_summary`, `auth_detail` tables | REST + SQS |
| 6 | **user-auth-service** | User Security + Navigation | COSGN00C, COUSR00C–03C, COADM01C, COMEN01C | `users` table | REST + JWT |
| 7 | **reporting-service** | Reporting | CBSTM03A/B, CBTRN03C, CORPT00C | Statement/report output (S3/filesystem) | REST trigger + Spring Batch |
| 8 | **data-migration-tools** | Data Migration | CBEXPORT, CBIMPORT | VSAM → PostgreSQL (one-time) | CLI / batch job |

### Shared Libraries (Not Microservices)

| Library | Source | Consumers |
|---------|--------|-----------|
| `carddemo-validation` | CSLKPCDY, CSUTLDPY/CSUTLDWY, CSSETATY | account-service, card-service, transaction-service |
| `carddemo-commons` | COCOM01Y, CSMSG01Y/02Y, CSDAT01Y, COTTL01Y | All services |
| `carddemo-security` | CSUSR01Y | user-auth-service (owns), all services (consumes JWT) |

---

## 4. Extraction Seams

An extraction seam is a point where two bounded contexts interact. Each seam must be bridged during migration.

### 4.1 Seam Inventory

| # | Seam | Context A | Context B | Interaction Pattern | Current Mechanism | Target Mechanism |
|---|------|-----------|-----------|--------------------|--------------------|-----------------|
| S1 | Card→Account lookup | Card | Account | Card programs read ACCTFILE for account details | Direct VSAM READ | REST API call to account-service |
| S2 | Transaction→Account balance update | Transaction | Account | CBTRN02C updates ACCTFILE balance during posting | Direct VSAM REWRITE | Async event (SQS) → account-service updates balance |
| S3 | Transaction→Card validation | Transaction | Card | COTRN02C, CBTRN02C read CARDXREF for card validation | Direct VSAM READ | REST API call to card-service |
| S4 | Bill Payment→Account debit | Transaction | Account | COBIL00C reads/rewrites ACCTFILE | Direct VSAM REWRITE | Sync REST call to account-service (within saga) |
| S5 | Auth→Account+Customer display | Authorization | Account + Card | COPAUS0C reads ACCTFILE, CUSTFILE for enrichment | Direct VSAM READ | REST API calls to account-service, card-service |
| S6 | Reporting→All entities | Reporting | Account, Card, Transaction | CBSTM03A reads ACCTFILE, CUSTFILE, XREFFILE, TRANSACT | Direct VSAM READ | Database views or read replicas (same PostgreSQL) |
| S7 | Transaction→TranType lookup | Transaction | Transaction Type | CBTRN03C reads TRANTYPE, TRANCATG | Direct VSAM READ | REST API call to transaction-type-service (cacheable) |
| S8 | Sign-on→User validation | Navigation | User Security | COSGN00C reads USRSEC for login | Direct VSAM READ | JWT token issuance by user-auth-service |
| S9 | Interest calc→Account+Disclosure | Account (internal) | Account (internal) | CBACT04C reads TCATBALF, DISCGRP, ACCTFILE | Direct VSAM READ/REWRITE | Internal to account-service (no seam in target) |
| S10 | Export→All entities | Data Migration | All | CBEXPORT reads all 5 VSAM files | Direct VSAM READ | One-time SQL export (retired post-migration) |

### 4.2 Extraction Difficulty Rating

| Seam | Difficulty | Rationale |
|------|-----------|-----------|
| **S1: Card→Account lookup** | **Easy** | Read-only. Replace VSAM READ with REST GET. No write coordination needed. |
| **S2: Transaction→Account balance update** | **Hard** | Write coupling across domains. CBTRN02C atomically updates TRANSACT + ACCTFILE + TCATBALF in a single unit of work. In microservices, this requires a saga or event-driven eventual consistency. Must handle partial failures. |
| **S3: Transaction→Card validation** | **Easy** | Read-only lookup. CARDXREF is a simple key-value lookup (card → account+customer). Cache-friendly. |
| **S4: Bill Payment→Account debit** | **Hard** | Synchronous write across domains — creates a transaction AND debits account balance. Requires distributed transaction or saga pattern with compensation logic. |
| **S5: Auth→Account+Customer display** | **Easy** | Read-only enrichment. Authorization screens display account/customer info but don't modify it. Simple REST calls. |
| **S6: Reporting→All entities** | **Medium** | Read-only but high-volume. Statement generation reads 4 entity files sequentially. In microservices, use database read replicas or materialized views to avoid N+1 API calls. |
| **S7: Transaction→TranType lookup** | **Easy** | Read-only reference data. Transaction type/category rarely change. Perfect candidate for a cache (Redis/in-memory) with long TTL. |
| **S8: Sign-on→User validation** | **Easy** | Replace with stateless JWT. No ongoing data coupling after token issuance. |
| **S9: Interest calc (internal)** | **N/A** | Internal to account-service — not a cross-service seam. |
| **S10: Export→All entities** | **Medium** | One-time migration utility. High fan-out (reads all 5 files) but not a runtime concern. Build as a dedicated batch job with direct database access. |

### 4.3 Difficulty Summary

```
Easy:   S1, S3, S5, S7, S8  — 5 seams (read-only or stateless)
Medium: S6, S10             — 2 seams (high-volume reads or one-time)
Hard:   S2, S4              — 2 seams (cross-domain writes requiring sagas)
N/A:    S9                  — 1 seam (internal)
```

**Key insight:** 70% of seams are easy (read-only). The two hard seams (S2: transaction posting balance update, S4: bill payment account debit) both involve **writing to ACCTFILE from the Transaction context**. These two seams should drive the migration sequencing — Account Management must have a stable API facade before Transaction Processing can migrate.

---

## 5. Data File Isolation Analysis

### 5.1 File Ownership Matrix

| VSAM File | Owner Context | Writers | Reader Contexts | Isolation Level |
|-----------|--------------|---------|-----------------|-----------------|
| ACCTDATA | Account | 5 programs (Account + Transaction) | Account, Card, Transaction, Auth, Reporting | **Shared-Write (CRITICAL)** |
| CARDDATA | Card | 2 programs (Card + Import) | Card, Account, Reporting | Mostly Isolated |
| CARDXREF | Card | 1 program (Import only) | Account, Card, Transaction, Auth, Reporting | Read-Shared, Write-Isolated |
| CUSTDATA | Account* | 1 program (Import only) | Account, Card, Auth, Reporting | Read-Shared, Write-Isolated |
| TRANSACT | Transaction | 4 programs (Transaction) | Transaction, Reporting | Mostly Isolated |
| USRSEC | User Security | 3 programs (User CRUD) | User Security | **Fully Isolated** |
| TCATBALF | Account | 2 programs (Account + Transaction) | Account | Shared-Write |
| DISCGRP | Account | 0 (setup only) | Account | Fully Isolated (reference) |
| TRANTYPE | Transaction Type | 0 (DB2, not VSAM) | Transaction, Reporting | Fully Isolated (reference) |
| TRANCATG | Transaction Type | 0 (DB2, not VSAM) | Transaction, Reporting | Fully Isolated (reference) |

*Customer data is conceptually its own entity but has no dedicated programs — it's always accessed in the context of account operations.

### 5.2 Isolation Implications

- **USRSEC, DISCGRP, TRANTYPE, TRANCATG:** Fully isolated — can be extracted with zero cross-context impact.
- **CARDDATA, CARDXREF, CUSTDATA:** Write-isolated (only bulk import writes). Easy to extract — other contexts become API consumers.
- **TRANSACT:** Mostly isolated — written only by Transaction context programs. Reporting reads but doesn't write.
- **ACCTDATA:** **The most shared file in the estate.** Written by Account (COACTUPC, CBACT04C), Transaction (CBTRN02C, COBIL00C), and Import (CBIMPORT). This is the primary source of extraction difficulty.
- **TCATBALF:** Shared between Account and Transaction contexts during posting — secondary shared-write concern.

---

## 6. Entity Relationship Across Contexts

```
┌──────────────────────┐         ┌──────────────────────┐
│   ACCOUNT CONTEXT    │         │    CARD CONTEXT       │
│                      │         │                       │
│  Account (CVACT01Y)  │◄────────│  Card-XREF (CVACT03Y)│
│  TranCatBal(CVTRA01Y)│         │  Card (CVACT02Y)     │
│  DiscGroup(CVTRA02Y) │         │                       │
│  Customer (CVCUS01Y) │◄────────│  XREF-CUST-ID        │
└──────────┬───────────┘         └───────────┬───────────┘
           │ S2: balance update               │ S3: card validation
           │ S4: bill pay debit               │
           ▼                                  ▼
┌──────────────────────┐         ┌──────────────────────┐
│ TRANSACTION CONTEXT  │         │  TRAN TYPE CONTEXT   │
│                      │────────►│                       │
│  Transaction(CVTRA05Y│  S7     │  TranType (CVTRA03Y) │
│  DailyTran (CVTRA06Y)│         │  TranCat  (CVTRA04Y) │
└──────────┬───────────┘         └───────────────────────┘
           │ S6: reporting reads
           ▼
┌──────────────────────┐         ┌──────────────────────┐
│  REPORTING CONTEXT   │         │    AUTH CONTEXT       │
│                      │         │                       │
│  Statements (output) │         │  AuthSummary (IMS)    │
│  Reports (output)    │         │  AuthDetail  (IMS)    │
│                      │         │  MQ Queues            │
└──────────────────────┘         └───────────────────────┘
                                          │ S5: acct/cust lookup
                                          ▼
                                 ┌──────────────────────┐
                                 │  USER SEC CONTEXT    │
                                 │                       │
                                 │  User (CSUSR01Y)     │
                                 └───────────────────────┘
```
