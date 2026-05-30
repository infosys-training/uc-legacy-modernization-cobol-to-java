# Domain Decomposition — CardDemo COBOL Estate

> Bounded contexts identified from copybook sharing, JCL groupings, and data file coupling — mapped to candidate microservices.

---

## 1. Methodology

Bounded contexts are derived from three coupling dimensions:

| Dimension | Signal | Tool |
|-----------|--------|------|
| **Data coupling** | Programs sharing the same copybooks (data structures) | COPY statement analysis |
| **File coupling** | Programs reading/writing the same VSAM datasets | JCL DD + I/O statement analysis |
| **Control coupling** | Programs calling each other (CALL/XCTL/LINK) | Call graph analysis |

Programs with high coupling across all three dimensions belong to the same bounded context. Programs with minimal coupling can be extracted into separate services.

---

## 2. Copybook Sharing Matrix

### Core Business Copybooks — Shared Across Contexts

| Copybook | Entity | Used By (Programs) | Contexts Sharing |
|----------|--------|--------------------|------------------|
| CVACT01Y | Account | CBACT01C, CBACT04C, CBEXPORT, CBIMPORT, CBTRN01C, CBTRN02C, COACTUPC, COACTVWC, COBIL00C, COCRDSLC, COCRDUPC, COTRN02C, COPAUA0C, COPAUS0C | Account, Card, Transaction, Auth, Migration |
| CVACT02Y | Card | CBACT02C, CBEXPORT, CBIMPORT, CBTRN01C, COACTVWC, COCRDLIC, COCRDSLC, COCRDUPC, COPAUS0C, COTRTLIC | Card, Transaction, Auth, TranType |
| CVACT03Y | Card-XREF | CBACT03C, CBACT04C, CBEXPORT, CBIMPORT, CBTRN01C, CBTRN02C, CBTRN03C, COACTUPC, COACTVWC, COBIL00C, COCRDSLC, COCRDUPC, COTRN02C, COPAUA0C, COPAUS0C | Account, Card, Transaction, Auth |
| CVCUS01Y | Customer | CBCUS01C, CBEXPORT, CBIMPORT, CBTRN01C, COACTUPC, COACTVWC, COCRDSLC, COCRDUPC, COPAUA0C, COPAUS0C | Account, Card, Transaction, Auth |
| CVTRA05Y | Transaction | CBACT04C, CBEXPORT, CBIMPORT, CBTRN01C, CBTRN02C, CBTRN03C, COBIL00C, CORPT00C, COTRN00C, COTRN01C, COTRN02C | Transaction, Reporting, BillPay |
| COCOM01Y | Framework | All 21 CICS programs | All online contexts (framework, not business) |

### Domain-Specific Copybooks — Isolated Within Contexts

| Copybook | Entity | Used By | Context |
|----------|--------|---------|---------|
| CSUSR01Y | User Security | COSGN00C, COUSR00C–03C, COACTUPC, COACTVWC, COCRDLIC, COMEN01C | User Admin (primary); others for display only |
| CVTRA01Y | Tran Cat Balance | CBACT04C, CBTRN02C | Transaction Processing (batch only) |
| CVTRA02Y | Disclosure Group | CBACT04C | Interest Calculation (isolated) |
| CVTRA03Y/04Y | Tran Type/Category | CBTRN03C | Reporting (isolated) |
| CVTRA06Y | Daily Transaction | CBTRN01C, CBTRN02C | Transaction Processing (batch only) |
| CVTRA07Y | Tran Report | CBTRN03C | Reporting (isolated) |
| CVEXPORT | Export Layout | CBEXPORT, CBIMPORT | Data Migration (isolated) |
| CIPAUSMY/CIPAUDTY | IMS Auth Segments | COPAUA0C, COPAUS0C–2C, CBPAUP0C, PAUDBLOD, PAUDBUNL, DBUNLDGS | Authorization (isolated) |
| CMQODV/CMQMDV/CMQV | MQ Descriptors | COPAUA0C, COACCT01, CODATE01 | Authorization + MQ Integration (isolated) |
| DCLTRTYP/DCLTRCAT | DB2 Declarations | COTRTLIC, COTRTUPC | Transaction Type Admin (isolated) |
| COSTM01/CUSTREC | Statement Fields | CBSTM03A | Statement Generation (isolated) |

---

## 3. Data File Coupling Matrix

| VSAM Dataset | Writers | Readers | Bounded Contexts Affected |
|-------------|---------|---------|---------------------------|
| ACCTFILE | CBACT04C, COACTUPC, COBIL00C, CBTRN02C, CBIMPORT | 13 programs | Account, Transaction, BillPay, Migration |
| CARDFILE | COCRDUPC, CBIMPORT | 8 programs | Card, Migration |
| CUSTFILE | CBIMPORT | 6 programs | (Read-only from most contexts; written only by Migration) |
| CARDXREF | CBIMPORT | 11 programs | (Reference data — read by all, written only by Migration) |
| TRANSACT | CBTRN02C, CBIMPORT | 5 programs | Transaction, Reporting, Migration |
| TCATBALF | CBTRN02C | CBACT04C | Transaction → Interest Calc pipeline |
| DALYTRAN | (external input) | CBTRN01C, CBTRN02C | Transaction Processing (input only) |
| USRSEC | COUSR00C–03C | COSGN00C | User Admin (fully isolated) |
| DISCGRP | (JCL IDCAMS) | CBACT04C | Interest Calculation (isolated) |
| IMS DB | PAUDBLOD (load), COPAUS1C (update) | COPAUA0C, COPAUS0C, CBPAUP0C, PAUDBUNL | Authorization (fully isolated) |
| DB2 Tables | COTRTUPC (insert/update/delete), COPAUS2C (insert) | COTRTLIC (select) | Tran Type Admin + Fraud Marking |
| MQ Queues | COPAUA0C (PUT) | COPAUA0C (GET) | Authorization (isolated) |

### Key Observations for Context Boundaries

1. **ACCTFILE is the most shared resource** — written by 5 programs across 4 contexts. This is the primary coupling point and must have a single-owner service with well-defined APIs.
2. **CARDXREF is read by 11 programs** but written only by CBIMPORT — it's reference data that can be exposed via a read-only API.
3. **USRSEC is fully isolated** — only User Admin programs touch it. Clean boundary.
4. **IMS DB is fully isolated** — only Authorization programs access it. Clean boundary.
5. **DB2 tables are semi-isolated** — Transaction Type Admin owns them; only COPAUS2C (fraud marking) writes across the boundary.
6. **TRANSACT file** is written by Transaction Processing and read by Reporting — this is a producer-consumer relationship (event-driven candidate).

---

## 4. JCL Job Groupings

### Group 1: Data Setup (Infrastructure)
| JCL Job | Programs | Datasets | Notes |
|---------|----------|----------|-------|
| ACCTFILE.jcl | IDCAMS | ACCTDATA VSAM KSDS | Cluster definition |
| CARDFILE.jcl | IDCAMS, SDSF | CARDDATA VSAM KSDS | Cluster definition |
| CUSTFILE.jcl | IDCAMS, SDSF | CUSTDATA VSAM KSDS | Cluster definition |
| XREFFILE.jcl | IDCAMS | CARDXREF VSAM KSDS | Cluster definition |
| TRANFILE.jcl | IDCAMS, SDSF | TRANSACT VSAM KSDS | Cluster definition |
| TCATBALF.jcl | IDCAMS | TCATBALF VSAM KSDS | Cluster definition |
| DISCGRP.jcl | IDCAMS | DISCGRP VSAM KSDS | Cluster definition |
| TRANTYPE.jcl | IDCAMS | TRANTYPE VSAM KSDS | Cluster definition |
| TRANCATG.jcl | IDCAMS | TRANCATG VSAM KSDS | Cluster definition |
| DUSRSECJ.jcl | IDCAMS, IEBGENER | USRSEC VSAM KSDS | User security file |
| DALYREJS.jcl | IDCAMS | DALYREJS | Rejected transactions |
| REPTFILE.jcl | IDCAMS | REPTFILE | Report output |

**→ Maps to: Database DDL migrations (Flyway/Liquibase)**

### Group 2: Daily Batch Pipeline (Core Business)
| JCL Job | Programs | Input Datasets | Output Datasets |
|---------|----------|----------------|-----------------|
| POSTTRAN.jcl | CBTRN02C | DALYTRAN, ACCTDATA, CARDXREF | TRANSACT, DALYREJS, TCATBALF, ACCTDATA |
| INTCALC.jcl | CBACT04C | TCATBALF, CARDXREF, DISCGRP, ACCTDATA | TRANSACT (GDG), ACCTDATA |
| CREASTMT.JCL | CBSTM03A, SORT | ACCTDATA, CARDXREF, TRANSACT | STATEMNT.PS, STATEMNT.HTML |
| TRANREPT.jcl | CBTRN03C, SORT | TRANSACT, CARDXREF, TRANTYPE, TRANCATG | TRANSACT.DALY (report) |

**→ Maps to: `DailyBatchPipeline` (Spring Batch Job with 4 Steps, orchestrated by AWS Step Functions)**

### Group 3: Data Migration Jobs
| JCL Job | Programs | Function |
|---------|----------|----------|
| CBEXPORT.jcl | CBEXPORT | Export all VSAM → sequential file |
| CBIMPORT.jcl | CBIMPORT | Import sequential → VSAM files |

**→ Maps to: `DataMigrationService` (Spring Batch) — temporary, retire after cutover**

### Group 4: Utility/Diagnostic Jobs
| JCL Job | Programs | Function |
|---------|----------|----------|
| READACCT.jcl | CBACT01C | Read and split account file |
| READCARD.jcl | CBACT02C | Read and print card file |
| READCUST.jcl | CBCUS01C | Read and print customer file |
| READXREF.jcl | CBACT03C | Read and print xref file |
| WAITSTEP.jcl | COBSWAIT | Wait/delay utility |

**→ Maps to: Replaced by SQL queries / admin panel (no service equivalent)**

### Group 5: Backup/GDG Management
| JCL Job | Programs | Function |
|---------|----------|----------|
| TRANBKP.jcl | IDCAMS | Transaction file backup to GDG |
| DEFGDGB.jcl | IDCAMS, IEBGENER | Define GDG bases for backups |
| DEFGDGD.jcl | IDCAMS, IEBGENER | Define GDG for reference data |
| COMBTRAN.jcl | IDCAMS, SORT | Combine daily + backup transactions |
| PRTCATBL.jcl | SORT | Print category balance report |

**→ Maps to: Database backup policies (RDS automated backups, pg_dump schedules)**

### Group 6: IMS Administration
| JCL Job | Programs | Function |
|---------|----------|----------|
| PAUDBLOD | PAUDBLOD | IMS database load |
| PAUDBUNL | PAUDBUNL | IMS database unload |
| DBUNLDGS | DBUNLDGS | GSAM unload |
| CBPAUP0C | CBPAUP0C | Expired auth purge |

**→ Maps to: `AuthorizationDataService` — PostgreSQL maintenance jobs**

---

## 5. Bounded Contexts & Candidate Microservices

### Context 1: Account Management

| Attribute | Detail |
|-----------|--------|
| **Programs** | COACTUPC (4,236), COACTVWC (941) |
| **Copybooks** | CVACT01Y, CVACT03Y, CVCUS01Y, CVCRD01Y, CSLKPCDY, CSSETATY, CSUTLDPY |
| **Data Owned** | `accounts` table (from ACCTFILE VSAM KSDS) |
| **Data Read** | customers, card_xref (via API calls to other services) |
| **Candidate Service** | **account-service** |
| **API Surface** | GET/PUT /accounts/{id}, GET /accounts/{id}/cards, GET /accounts/{id}/balance |
| **Notes** | Owns ACCTFILE writes; most complex program in estate; validation logic becomes shared library |

### Context 2: Card Management

| Attribute | Detail |
|-----------|--------|
| **Programs** | COCRDLIC (1,459), COCRDSLC (887), COCRDUPC (1,560) |
| **Copybooks** | CVACT02Y, CVCRD01Y, CVACT03Y, CVCUS01Y |
| **Data Owned** | `cards` table (from CARDFILE), `card_xref` table (from CARDXREF) |
| **Data Read** | accounts, customers (via API) |
| **Candidate Service** | **card-service** |
| **API Surface** | GET /cards, GET /cards/{num}, PUT /cards/{num}, GET /cards?account={id} |
| **Notes** | Paginated browse pattern becomes `AbstractListController`; xref is owned here but read by many |

### Context 3: Transaction Processing

| Attribute | Detail |
|-----------|--------|
| **Programs** | COTRN00C (699), COTRN01C (330), COTRN02C (783), CBTRN01C (494), CBTRN02C (731), COBIL00C (572) |
| **Copybooks** | CVTRA05Y, CVTRA06Y, CVTRA01Y, CVACT01Y, CVACT03Y |
| **Data Owned** | `transactions` table (from TRANSACT), `daily_transactions` (from DALYTRAN), `tran_category_balances` (from TCATBALF), `daily_rejects` (from DALYREJS) |
| **Data Read** | accounts, card_xref (via API) |
| **Candidate Service** | **transaction-service** |
| **API Surface** | GET/POST /transactions, GET /transactions/{id}, POST /transactions/post-daily, POST /bill-payments |
| **JCL Jobs** | POSTTRAN.jcl → Spring Batch `PostTransactionJob` |
| **Notes** | Includes bill payment (COBIL00C) as it writes to TRANSACT and updates ACCTFILE (via account-service API). Publishes transaction events consumed by interest-calc and reporting |

### Context 4: Interest Calculation

| Attribute | Detail |
|-----------|--------|
| **Programs** | CBACT04C (652) |
| **Copybooks** | CVTRA01Y, CVTRA02Y, CVACT01Y, CVACT03Y, CVTRA05Y |
| **Data Owned** | `disclosure_groups` table (from DISCGRP) |
| **Data Read** | accounts, tran_category_balances, card_xref (via API or shared DB) |
| **Candidate Service** | **interest-calculation-service** (or module within transaction-service) |
| **API Surface** | POST /interest/calculate-daily |
| **JCL Jobs** | INTCALC.jcl → Spring Batch `InterestCalculationJob` |
| **Notes** | Single program; could be a module inside transaction-service rather than a separate microservice. Consumes TCATBALF produced by POSTTRAN; writes back to accounts |

### Context 5: Statement & Reporting

| Attribute | Detail |
|-----------|--------|
| **Programs** | CBSTM03A (924), CBSTM03B (230), CBTRN03C (649), CORPT00C (649) |
| **Copybooks** | COSTM01, CUSTREC, CVACT01Y, CVACT03Y, CVTRA05Y, CVTRA03Y, CVTRA04Y, CVTRA07Y |
| **Data Owned** | Statement output files, report output files |
| **Data Read** | accounts, customers, transactions, card_xref, tran_types (all via API) |
| **Candidate Service** | **reporting-service** |
| **API Surface** | POST /reports/statements, POST /reports/daily-transactions, GET /reports/{id}/download |
| **JCL Jobs** | CREASTMT.JCL → `StatementGenerationJob`; TRANREPT.jcl → `TransactionReportJob` |
| **Notes** | Pure consumer of other contexts' data. No shared writes. Ideal for early migration (CBSTM03A as pilot) |

### Context 6: User & Authentication

| Attribute | Detail |
|-----------|--------|
| **Programs** | COSGN00C (260), COUSR00C (695), COUSR01C (299), COUSR02C (414), COUSR03C (359), COMEN01C (308), COADM01C (288) |
| **Copybooks** | CSUSR01Y, COADM02Y, COMEN02Y |
| **Data Owned** | `users` table (from USRSEC VSAM KSDS) |
| **Data Read** | None — fully independent |
| **Candidate Service** | **user-auth-service** |
| **API Surface** | POST /auth/login, POST /auth/logout, GET/POST/PUT/DELETE /users/{id} |
| **JCL Jobs** | DUSRSECJ.jcl → Flyway migration for user table |
| **Notes** | Completely isolated data. Includes menu programs (COMEN01C, COADM01C) which become frontend routing in React/Angular. Critical security fix: plain-text passwords → bcrypt |

### Context 7: Transaction Type Administration

| Attribute | Detail |
|-----------|--------|
| **Programs** | COTRTLIC (2,098), COTRTUPC (1,702), COBTUPDT (batch) |
| **Copybooks** | DCLTRTYP, DCLTRCAT, CSDB2RWY, CVCRD01Y |
| **Data Owned** | `transaction_types` table, `transaction_categories` table (from DB2) |
| **Data Read** | None — fully independent |
| **Candidate Service** | **transaction-type-service** (or admin module within transaction-service) |
| **API Surface** | GET/POST/PUT/DELETE /transaction-types/{id}, GET /transaction-categories |
| **Notes** | Already DB2 — easiest database migration. Could be a module within transaction-service. Isolated data, no VSAM dependency |

### Context 8: Authorization Processing

| Attribute | Detail |
|-----------|--------|
| **Programs** | COPAUA0C (1,026), COPAUS0C (1,032), COPAUS1C (604), COPAUS2C (244), CBPAUP0C (386), PAUDBLOD (369), PAUDBUNL (317), DBUNLDGS (366) |
| **Copybooks** | CIPAUSMY, CIPAUDTY, CCPAURQY, CCPAURLY, CCPAUERY, CMQODV, CMQMDV, CMQV, IMSFUNCS, PAUTBPCB, PADFLPCB, PASFLPCB |
| **Data Owned** | `authorization_summary` table, `authorization_detail` table (from IMS DB), `fraud_flags` table (from DB2) |
| **Data Read** | accounts, customers, cards (via API) |
| **Candidate Service** | **authorization-service** |
| **API Surface** | POST /authorizations/decide, GET /authorizations?account={id}, PUT /authorizations/{id}/fraud-flag, DELETE /authorizations/expired |
| **JCL Jobs** | PAUDBLOD → data migration; CBPAUP0C → scheduled cleanup |
| **Notes** | Most isolated subsystem — own data store (IMS), own messaging (MQ), own copybooks. All 12 auth-specific copybooks are used exclusively here. Must migrate as a unit |

### Context 9: Data Migration (Temporary)

| Attribute | Detail |
|-----------|--------|
| **Programs** | CBEXPORT (582), CBIMPORT (487) |
| **Copybooks** | CVEXPORT + all core entity copybooks |
| **Data Owned** | Export/import sequential files |
| **Data Read/Written** | All 5 core VSAM files |
| **Candidate Service** | **data-migration-tools** (temporary) |
| **Notes** | Cross-cutting — touches all data stores. Needed only during migration. Retire after cutover |

---

## 6. Context Map (Inter-Service Dependencies)

```
                        ┌─────────────────┐
                        │  user-auth-     │
                        │  service        │
                        │  (JWT/OAuth2)   │
                        └────────┬────────┘
                                 │ authenticates
                    ┌────────────┼────────────┐
                    ▼            ▼             ▼
          ┌─────────────┐ ┌──────────┐ ┌──────────────┐
          │ account-    │ │ card-    │ │ transaction- │
          │ service     │ │ service  │ │ type-service │
          │ (ACCTFILE)  │ │(CARDFILE)│ │ (DB2 tables) │
          └──────┬──────┘ └────┬─────┘ └──────┬───────┘
                 │             │              │
          reads ─┤─── reads ───┤              │ reads
                 │             │              │
          ┌──────▼─────────────▼──────────────▼───────┐
          │         transaction-service                │
          │   (TRANSACT, DALYTRAN, TCATBALF)           │
          │   + interest-calculation module             │
          │   + bill-payment module                     │
          └──────────────────┬────────────────────────┘
                             │ publishes events
                    ┌────────┼────────┐
                    ▼                 ▼
          ┌─────────────┐   ┌─────────────────┐
          │ reporting-  │   │ authorization-  │
          │ service     │   │ service         │
          │ (statements,│   │ (IMS→PostgreSQL,│
          │  reports)   │   │  MQ→SQS)        │
          └─────────────┘   └─────────────────┘
```

### Communication Patterns

| From → To | Current Mechanism | Target Mechanism |
|-----------|-------------------|------------------|
| Online screens → Programs | CICS XCTL/LINK | REST API calls |
| Programs → VSAM files | READ/WRITE/REWRITE | JPA Repository |
| Programs → DB2 | EXEC SQL | Spring Data JPA |
| Programs → IMS | CALL CBLTDLI | JPA (relational mapping) |
| Programs → MQ | CALL MQOPEN/GET/PUT | Spring JMS / Amazon SQS |
| Batch pipeline steps | JCL COND CODE | Spring Batch Step flow |
| Report request → Batch job | JCL Internal Reader | REST API + async job |
| COMMAREA data passing | Memory block | DTO/POJO + REST payload |

---

## 7. Shared Libraries (Cross-Cutting Concerns)

The following copybook-derived logic should become shared Java libraries rather than being duplicated across services:

| Library | Source Copybooks | Used By Services | Purpose |
|---------|-----------------|------------------|---------|
| `carddemo-validation` | CSLKPCDY, CSUTLDPY, CSUTLDWY | account, card, transaction | State/ZIP/phone/date validation |
| `carddemo-commons` | COCOM01Y, COTTL01Y, CSDAT01Y, CSMSG01Y/02Y | All online services | Common DTOs, message formatting |
| `carddemo-security` | CSUSR01Y | All services | JWT token parsing, user context |
| `carddemo-screen-attrs` | CSSETATY, DFHBMSCA | None (retired) | BMS attributes — replaced by CSS/UI framework |

---

## 8. Database Schema Mapping

| Bounded Context | VSAM/DB2 Source | Target Table(s) | Owner Service |
|----------------|-----------------|------------------|---------------|
| Account | ACCTFILE (KSDS, 300B) | `accounts` | account-service |
| Card | CARDFILE (KSDS, 150B) | `cards` | card-service |
| Card XREF | CARDXREF (KSDS, 50B) | `card_xref` | card-service |
| Customer | CUSTFILE (KSDS, 500B) | `customers` | account-service (or separate customer-service) |
| Transaction | TRANSACT (KSDS, 350B) | `transactions` | transaction-service |
| Daily Trans | DALYTRAN (sequential) | `daily_transactions` (staging) | transaction-service |
| Tran Cat Balance | TCATBALF (KSDS) | `tran_category_balances` | transaction-service |
| Daily Rejects | DALYREJS (GDG) | `rejected_transactions` | transaction-service |
| Disclosure Group | DISCGRP (KSDS) | `disclosure_groups` | transaction-service |
| Tran Type | DB2 TRANSACTION_TYPE | `transaction_types` | transaction-type-service |
| Tran Category | DB2 TRANSACTION_CATEGORY | `transaction_categories` | transaction-type-service |
| User Security | USRSEC (KSDS, 80B) | `users` | user-auth-service |
| Auth Summary | IMS AUTH_SUMMARY | `authorization_summary` | authorization-service |
| Auth Detail | IMS AUTH_DETAIL | `authorization_detail` | authorization-service |
| Fraud Flags | DB2 FRAUD_FLAGS | `fraud_flags` | authorization-service |
| Export Data | Sequential file | (temporary staging table) | data-migration-tools |
