# CardDemo Application - Domain-Driven Design Analysis

This document captures the domain-driven design (DDD) analysis of the AWS CardDemo mainframe credit card management application. It identifies bounded contexts, maps inter-domain dependencies, analyses batch processing patterns, and evaluates VSAM-to-DB2 simplification opportunities. It is intended as a persistent reference for modernization efforts.

---

## Table of Contents

1. [Business Domains (Bounded Contexts)](#1-business-domains-bounded-contexts)
   - [Security / Identity Management](#11-security--identity-management)
   - [Customer Management](#12-customer-management)
   - [Account Management](#13-account-management)
   - [Credit Card Management](#14-credit-card-management)
   - [Transaction Management](#15-transaction-management)
   - [Authorization / Fraud Management](#16-authorization--fraud-management)
2. [Inter-Domain Dependencies](#2-inter-domain-dependencies)
3. [Batch Processing Analysis](#3-batch-processing-analysis)
4. [VSAM-to-DB2 Simplification Analysis](#4-vsam-to-db2-simplification-analysis)

---

## 1. Business Domains (Bounded Contexts)

### 1.1 Security / Identity Management

| Attribute | Value |
|---|---|
| **Base Entity** | User |
| **Key Copybook** | `app/cpy/CSUSR01Y.cpy` (record layout: `SEC-USER-DATA`) |
| **VSAM Dataset** | USRSEC (KSDS, keyed by `SEC-USR-ID`) |
| **Record Fields** | `SEC-USR-ID` (8), `SEC-USR-FNAME` (20), `SEC-USR-LNAME` (20), `SEC-USR-PWD` (8), `SEC-USR-TYPE` (1) |

**Technologies:** COBOL, CICS, VSAM KSDS, BMS maps.

**User Interaction (CICS Transactions / BMS Maps):**

| Transaction | BMS Map | Program | Function |
|---|---|---|---|
| COSG | COSGN00 | COSGN00C | Sign-on / authentication |
| CU00 | COUSR00 | COUSR00C | User management menu |
| CU01 | COUSR01 | COUSR01C | User add |
| CU02 | COUSR02 | COUSR02C | User update |
| CU03 | COUSR03 | COUSR03C | User delete |

**REST Resource Mapping (Target State):**

| Verb | Resource | Action |
|---|---|---|
| POST | `/api/auth/login` | Authenticate user |
| GET | `/api/users` | List users |
| POST | `/api/users` | Create user |
| PUT | `/api/users/{id}` | Update user |
| DELETE | `/api/users/{id}` | Delete user |

**CRUD Actions:** Create user, Read/List users, Update user profile & password, Delete user, Authenticate (sign-on / sign-off).

---

### 1.2 Customer Management

| Attribute | Value |
|---|---|
| **Base Entity** | Customer |
| **Key Copybook** | `app/cpy/CVCUS01Y.cpy` (record layout: `CUSTOMER-RECORD`) |
| **VSAM Dataset** | CUSTDATA (KSDS, keyed by `CUST-ID`) |
| **Record Fields** | `CUST-ID` (9), `CUST-FIRST-NAME` (25), `CUST-MIDDLE-NAME` (25), `CUST-LAST-NAME` (25), address fields, `CUST-SSN` (9), `CUST-GOVT-ISSUED-ID` (20), `CUST-DOB-YYYY-MM-DD` (10), `CUST-EFT-ACCOUNT-ID` (10), `CUST-PRI-CARD-HOLDER-IND` (1), `CUST-FICO-CREDIT-SCORE` (3) |

**Technologies:** COBOL, CICS, VSAM KSDS, BMS maps.

**User Interaction (CICS Transactions / BMS Maps):**

The Customer Management domain does not have dedicated CICS transactions for direct customer CRUD in the base application. Customer data is accessed indirectly through Account and Card management screens. Batch jobs (CUSTFILE, READCUST, DEFCUST) handle customer data loading and extraction.

| Job | Function |
|---|---|
| CUSTFILE | Define VSAM CUSTDATA cluster |
| READCUST | Read/extract customer records |
| DEFCUST | Define customer data structures |

**REST Resource Mapping (Target State):**

| Verb | Resource | Action |
|---|---|---|
| GET | `/api/customers` | List customers |
| GET | `/api/customers/{id}` | Get customer detail |
| POST | `/api/customers` | Create customer |
| PUT | `/api/customers/{id}` | Update customer |
| DELETE | `/api/customers/{id}` | Delete customer |

**CRUD Actions:** Create customer (batch import), Read customer details, Update customer profile, Delete customer (batch).

---

### 1.3 Account Management

| Attribute | Value |
|---|---|
| **Base Entity** | Account |
| **Key Copybook** | `app/cpy/CVACT01Y.cpy` (record layout: `ACCOUNT-RECORD`) |
| **VSAM Dataset** | ACCTDATA (KSDS, keyed by `ACCT-ID`) |
| **Record Fields** | `ACCT-ID` (11), `ACCT-ACTIVE-STATUS` (1), `ACCT-CURR-BAL` S9(10)V99, `ACCT-CREDIT-LIMIT` S9(10)V99, `ACCT-CASH-CREDIT-LIMIT` S9(10)V99, `ACCT-OPEN-DATE` (10), `ACCT-EXPIRAION-DATE` (10), `ACCT-REISSUE-DATE` (10), `ACCT-CURR-CYC-CREDIT` S9(10)V99, `ACCT-CURR-CYC-DEBIT` S9(10)V99, `ACCT-ADDR-ZIP` (10), `ACCT-GROUP-ID` (10) |

**Technologies:** COBOL, CICS, VSAM KSDS, BMS maps.

**User Interaction (CICS Transactions / BMS Maps):**

| Transaction | BMS Map | Program | Function |
|---|---|---|---|
| CA00 | COADM01 | COADM01C | Admin menu (account operations gateway) |
| CACV | COACTVW | COACTVWC | Account view |
| CACU | COACTUP | COACTUPC | Account update |

**REST Resource Mapping (Target State):**

| Verb | Resource | Action |
|---|---|---|
| GET | `/api/accounts` | List accounts |
| GET | `/api/accounts/{id}` | Get account detail |
| POST | `/api/accounts` | Create account |
| PUT | `/api/accounts/{id}` | Update account |
| DELETE | `/api/accounts/{id}` | Close/deactivate account |

**CRUD Actions:** Create account (via batch/import), Read/View account details, Update account fields, Close/deactivate account.

---

### 1.4 Credit Card Management

| Attribute | Value |
|---|---|
| **Base Entity** | CreditCard |
| **Key Copybooks** | `app/cpy/CVACT02Y.cpy` (record layout: `CARD-RECORD`), `app/cpy/CVACT03Y.cpy` (record layout: `CARD-XREF-RECORD`) |
| **VSAM Datasets** | CARDDATA (KSDS, keyed by `CARD-NUM`), CARDXREF (KSDS, keyed by `XREF-CARD-NUM`) |
| **Card Record Fields** | `CARD-NUM` (16), `CARD-ACCT-ID` (11), `CARD-CVV-CD` (3), `CARD-EMBOSSED-NAME` (50), `CARD-EXPIRAION-DATE` (10), `CARD-ACTIVE-STATUS` (1) |
| **XREF Record Fields** | `XREF-CARD-NUM` (16), `XREF-CUST-ID` (9), `XREF-ACCT-ID` (11) |

**Technologies:** COBOL, CICS, VSAM KSDS, BMS maps.

**User Interaction (CICS Transactions / BMS Maps):**

| Transaction | BMS Map | Program | Function |
|---|---|---|---|
| CCLI | COCRDLI | COCRDLIC | Credit card list |
| CCSL | COCRDSL | COCRDSLC | Credit card search/select |
| CCUP | COCRDUP | COCRDUPC | Credit card update |

**REST Resource Mapping (Target State):**

| Verb | Resource | Action |
|---|---|---|
| GET | `/api/cards` | List credit cards |
| GET | `/api/cards/{num}` | Get card detail |
| POST | `/api/cards` | Issue new card |
| PUT | `/api/cards/{num}` | Update card |
| DELETE | `/api/cards/{num}` | Deactivate card |
| GET | `/api/cards/{num}/xref` | Get card cross-reference (customer + account) |

**CRUD Actions:** List cards (by account or customer), View card details, Update card fields, Issue/reissue cards, Deactivate cards.

---

### 1.5 Transaction Management

| Attribute | Value |
|---|---|
| **Base Entity** | Transaction |
| **Key Copybooks** | `app/cpy/CVTRA05Y.cpy` (record layout: `TRAN-RECORD`), `app/cpy/CVTRA06Y.cpy` (record layout: `DALYTRAN-RECORD`) |
| **VSAM Datasets** | TRANSACT (KSDS, keyed by `TRAN-ID`), DALYTRAN (KSDS, keyed by `DALYTRAN-ID`) |
| **Transaction Record Fields** | `TRAN-ID` (16), `TRAN-TYPE-CD` (2), `TRAN-CAT-CD` (4), `TRAN-SOURCE` (10), `TRAN-DESC` (100), `TRAN-AMT` S9(9)V99, `TRAN-MERCHANT-ID` (9), `TRAN-MERCHANT-NAME` (50), `TRAN-MERCHANT-CITY` (50), `TRAN-MERCHANT-ZIP` (10), `TRAN-CARD-NUM` (16), `TRAN-ORIG-TS` (26), `TRAN-PROC-TS` (26) |

**Technologies:** COBOL, CICS, VSAM KSDS, BMS maps, DB2 (for transaction type reference data via `app-transaction-type-db2` extension).

**User Interaction (CICS Transactions / BMS Maps):**

| Transaction | BMS Map | Program | Function |
|---|---|---|---|
| CT00 | COTRN00 | COTRN00C | Transaction list/browse |
| CT01 | COTRN01 | COTRN01C | Transaction detail view |
| CT02 | COTRN02 | COTRN02C | Transaction add |
| CTLI | COTRTLI | COTRTLIC | Transaction type list (DB2 extension) |
| CTTU | COTRTUP | COTRTUPC | Transaction type add/edit (DB2 extension) |

**REST Resource Mapping (Target State):**

| Verb | Resource | Action |
|---|---|---|
| GET | `/api/transactions` | List transactions |
| GET | `/api/transactions/{id}` | Get transaction detail |
| POST | `/api/transactions` | Create transaction |
| GET | `/api/transaction-types` | List transaction types |
| PUT | `/api/transaction-types/{code}` | Update transaction type |

**CRUD Actions:** Browse/list transactions, View transaction detail, Add new transactions, Manage transaction types and categories (via DB2 extension).

---

### 1.6 Authorization / Fraud Management

| Attribute | Value |
|---|---|
| **Base Entity** | Authorization |
| **Key Stores** | IMS HIDAM database (`DBPAUTP0` primary + `DBPAUTX0` index), DB2 table `CARDDEMO.AUTHFRDS` |
| **DB2 Schema** | `CARD_NUM` CHAR(16), `AUTH_TS` TIMESTAMP, `AUTH_TYPE` CHAR(4), `TRANSACTION_AMT` DECIMAL(12,2), `APPROVED_AMT` DECIMAL(12,2), `AUTH_FRAUD` CHAR(1), `FRAUD_RPT_DATE` DATE, `ACCT_ID` DECIMAL(11), `CUST_ID` DECIMAL(9), plus merchant and processing fields |
| **IMS DBDs** | `DBPAUTP0.dbd` (HIDAM primary), `DBPAUTX0.dbd` (HIDAM index) |
| **IMS PSBs** | `PSBPAUTB.psb` (BMP), `PSBPAUTL.psb` (Load) |

**Technologies:** COBOL, CICS, IMS DB (HIDAM), DB2, MQ (message queuing for authorization requests/responses), VSAM (cross-reference lookups).

**User Interaction (CICS Transactions / BMS Maps):**

| Transaction | BMS Map | Program | Function |
|---|---|---|---|
| CPAU | COPAUS0 | COPAUS0C | Authorization summary view |
| - | COPAUS1 | COPAUS1C | Authorization detail view |
| - | COPAUS2 | COPAUS2C | Authorization update / fraud marking |
| - | COPAUA0 | COPAUA0C | Authorization request processing (MQ-triggered) |

**Batch Components:**

| Program | Function |
|---|---|
| CBPAUP0C | Batch authorization purge (expired authorizations) |
| PAUDBLOD | IMS database load utility |
| PAUDBUNL | IMS database unload utility |
| DBUNLDGS | DB2 unload for fraud data |

**REST Resource Mapping (Target State):**

| Verb | Resource | Action |
|---|---|---|
| POST | `/api/authorizations` | Submit authorization request |
| GET | `/api/authorizations` | List authorizations |
| GET | `/api/authorizations/{id}` | Get authorization detail |
| PUT | `/api/authorizations/{id}/fraud` | Mark as fraudulent |
| DELETE | `/api/authorizations/{id}` | Purge expired authorization |
| GET | `/api/fraud-reports` | List fraud cases |

**CRUD Actions:** Process authorization requests (via MQ), View authorization summaries and details, Mark transactions as fraudulent (writes to DB2 `AUTHFRDS`), Purge expired authorizations (batch), Load/unload IMS authorization database.

---

## 2. Inter-Domain Dependencies

This section documents the tight couplings identified between bounded contexts. These represent the highest-risk areas during modernization, as changes to one domain may cascade into others.

### 2.1 Dependency Matrix

| Dependency | Direction | Coupling Mechanism | Migration Risk | Notes |
|---|---|---|---|---|
| **Card <-> Account <-> Customer** | Bidirectional | CARDXREF VSAM file (`CVACT03Y.cpy`) | **HIGH** | The XREF file (`XREF-CARD-NUM`, `XREF-CUST-ID`, `XREF-ACCT-ID`) is the central join table linking all three domains. Every card lookup resolves the owning customer and account through this file. Splitting these into independent services requires replacing VSAM XREF with a shared lookup service or API gateway composition. |
| **Transaction -> Account** | Unidirectional | POSTTRAN / INTCALC batch jobs | **HIGH** | The POSTTRAN batch job posts daily transactions to account balances (`ACCT-CURR-BAL`, `ACCT-CURR-CYC-DEBIT`, `ACCT-CURR-CYC-CREDIT`). The INTCALC batch job sweeps all accounts for monthly interest calculation. Both mutate the Account domain from the Transaction domain. Decoupling requires event-driven or saga-based balance updates. |
| **Security -> All Domains** | Cross-cutting | CICS sign-on gate, `SEC-USR-TYPE` flag | **LOW** | Security is a cross-cutting concern. Every CICS transaction checks sign-on status via `COSGN00C`. The `SEC-USR-TYPE` field (Admin vs Regular) gates access to admin-only screens (CA00). This maps cleanly to an API gateway authentication/authorization filter. |
| **Authorization -> Customer + Account + Card** | Unidirectional | IMS/VSAM/DB2 cross-lookup | **MEDIUM** | Authorization processing (`COPAUA0C`) receives MQ messages, then looks up the card via CARDXREF to resolve `ACCT-ID` and `CUST-ID`, reads account data from ACCTDATA VSAM, and stores authorization details in IMS HIDAM. Fraud records are written to DB2 `AUTHFRDS` with `ACCT_ID` and `CUST_ID` foreign keys. Three storage technologies (IMS + VSAM + DB2) must be coordinated. |
| **Transaction Type DB2 <-> Transaction VSAM** | Bidirectional (sync) | TRANEXTR / TRANCATG / TRANTYPE batch jobs | **MEDIUM** | Transaction type reference data lives in DB2 (`TRANSACTION_TYPE`, `TRANSACTION_TYPE_CATEGORY` tables) but must be extracted and synced to VSAM for the online CICS programs to use. The weekly `TRANEXTR` job extracts from DB2, and `TRANCATG`/`TRANTYPE` jobs rebuild VSAM files. This dual-store pattern is a modernization target. |

### 2.2 Dependency Diagram (ASCII)

```
                    ┌──────────────┐
                    │   Security   │
                    │  (Cross-cut) │
                    └──────┬───────┘
                           │ LOW risk
                           ▼
           ┌───────────────────────────────┐
           │        All Domains            │
           └───────────────────────────────┘

  ┌──────────┐    CARDXREF     ┌──────────┐    CARDXREF     ┌──────────┐
  │ Customer │◄───(HIGH)──────►│   Card   │◄───(HIGH)──────►│ Account  │
  └──────────┘                 └──────────┘                 └────┬─────┘
                                    │                            │
                                    │ MQ + VSAM + IMS            │ POSTTRAN
                                    │ (MEDIUM)                   │ INTCALC
                                    ▼                            │ (HIGH)
                            ┌──────────────┐                    │
                            │Authorization │                    │
                            │   / Fraud    │                    │
                            └──────────────┘                    │
                                                                ▼
                            ┌──────────────┐    TRANEXTR   ┌──────────┐
                            │ Tran Type    │◄──(MEDIUM)───►│Transaction│
                            │   (DB2)      │               │  (VSAM)  │
                            └──────────────┘               └──────────┘
```

---

## 3. Batch Processing Analysis

### 3.1 Overview

CardDemo uses two enterprise job schedulers:
- **Control-M** (`app/scheduler/CardDemo.controlm`) - XML-based folder/job definitions
- **CA7** (`app/scheduler/CardDemo.ca7`) - Legacy CA7 job definitions with trigger chains

Batch jobs follow the **Close-Process-Open** pattern required by VSAM's exclusive access model: CICS must release file control before batch jobs can write to VSAM datasets, and files must be re-opened after batch completes.

### 3.2 Job Inventory by Schedule Cycle

#### Daily Jobs (DAILY-TransactionBackup)

| Job | JCL | Program | Purpose | Why Batch? |
|---|---|---|---|---|
| **CLOSEFIL** | `CLOSEFIL.jcl` | (CICS utility) | Close VSAM files for batch access | **VSAM exclusive access** - CICS holds share-mode locks on VSAM files; batch programs need exclusive access for sequential I/O. |
| **TRANBKP** | `TRANBKP.jcl` | CBTRN02C | Backup daily transaction file (DALYTRAN) to GDG | **Volume** - Daily transaction volume requires sequential backup to GDG (Generation Data Group) for recovery and audit. |
| **CBPAUP0J** | (triggered) | CBPAUP0C | Purge expired authorizations from IMS DB | **IMS maintenance** - Expired authorization records must be removed to prevent unbounded growth of IMS HIDAM segments. |
| **POSTTRAN** | `POSTTRAN.jcl` | CBTRN03C | Post daily transactions to account balances | **Volume + Consistency** - High-volume sequential processing of all daily transactions; updates `ACCT-CURR-BAL` across potentially thousands of accounts. Online posting would cause contention. |
| **WAITSTEP** | `WAITSTEP.jcl` | (utility) | Wait/synchronization step between jobs | **Coordination** - Ensures previous job has fully completed and resources are released before next job begins. |
| **OPENFIL** | `OPENFIL.jcl` | (CICS utility) | Re-open VSAM files for online access | **VSAM exclusive access** - Returns file control to CICS for online transaction processing. |

**Control-M Chain:** `CLOSEFIL` → `TRANBKP` → `WAITSTEP` → `OPENFIL`

**CA7 Chain:** `CLOSEFIL` → `CBPAUP0J` → `POSTTRAN` → `WAITSTEP` → `OPENFIL`

#### Weekly Jobs (WEEKLY-TransactionTypesDBRefresh)

| Job | JCL | Program | Purpose | Why Batch? |
|---|---|---|---|---|
| **MNTTRDB2** | (DB2 utility) | COBTUPDT | Batch maintenance of transaction types in DB2 | **Reference data sync** - Bulk updates to transaction type reference data that must be consistent before extraction. |
| **TRANEXTR** | (DSNTIAUL) | (DB2 extract) | Extract DB2 transaction type data to sequential file | **Dual-store sync** - Transaction types live in DB2 for relational integrity but must be extracted for VSAM-based online programs. |
| **TRANCATG** | `TRANCATG.jcl` | (IDCAMS) | Rebuild VSAM transaction category file from extract | **VSAM rebuild** - IDCAMS REPRO loads extracted data into VSAM KSDS for online access. |
| **TRANTYPE** | `TRANTYPE.jcl` | (IDCAMS) | Rebuild VSAM transaction type file from extract | **VSAM rebuild** - Same as TRANCATG but for the type lookup file. |

**Control-M Chains:**
- `MNTTRDB2` → (triggers two parallel SMART_FOLDERs):
  - DisclosureGroupsRefresh: `CLOSEFIL` → `DISCGRP` → `WAITSTEP` → `OPENFIL`
  - TransactionTypesDBRefresh: `TRANEXTR` (extraction from DB2)

**CA7 Chain:** `CLOSEFIL` → `TRANTYPE` → `WAITSTEP` → (`CLOSEFIL1` + `CLOSEFIL2` in parallel) → `TRANCATG` / `TCATBALF` → `WAITSTEP` → `OPENFIL`

#### Monthly Jobs (MONTHLY-InterestCalculation)

| Job | JCL | Program | Purpose | Why Batch? |
|---|---|---|---|---|
| **CLOSEFIL** | `CLOSEFIL.jcl` | (CICS utility) | Close VSAM files for batch access | **VSAM exclusive access** |
| **INTCALC** | `INTCALC.jcl` | CBACT04C | Calculate and post monthly interest to all accounts | **Compute-intensive sweep** - Iterates every active account, calculates interest based on balance and rate tiers, and updates `ACCT-CURR-BAL`. Too expensive and contentious for online. |
| **COMBTRAN** | `COMBTRAN.jcl` | CBTRN01C | Combine/consolidate daily transactions into monthly summary | **Consolidation** - Merges DALYTRAN records into TRANSACT master file, resets the daily transaction file. Requires exclusive access to both files. |
| **WAITSTEP** | `WAITSTEP.jcl` | (utility) | Wait/synchronization step | **Coordination** |
| **OPENFIL** | `OPENFIL.jcl` | (CICS utility) | Re-open VSAM files for online access | **VSAM exclusive access** |

**Control-M Chain:** `CLOSEFIL` → `INTCALC` → `COMBTRAN` → `WAITSTEP` → `OPENFIL`

**CA7 Chain (Statement Cycle):**
- `CLOSEFIL` → `READACCT` → `READCARD` → `READCUST` → `READXREF` → `WAITSTEP` → `OPENFIL` (data extraction chain)
- `CLOSEFIL` → `CREASTMT` → `TXT2PDF1` → `WAITSTEP` → `OPENFIL` (statement generation)
- `CLOSEFIL` → `PRTCATBL` → `WAITSTEP` → `OPENFIL` (catalog balance report)

#### Statement Generation Jobs (Monthly - CA7 only)

| Job | JCL | Program | Purpose | Why Batch? |
|---|---|---|---|---|
| **CREASTMT** | `CREASTMT.JCL` | CBSTM03A/B | Generate monthly account statements | **Monthly aggregation** - Reads all transactions for a billing cycle, formats statement output. Requires consistent point-in-time snapshot of account and transaction data. |
| **TXT2PDF1** | `TXT2PDF1.JCL` | (utility) | Convert text statements to PDF | **Format conversion** - Bulk conversion of all generated statements to PDF for distribution. |
| **PRTCATBL** | `PRTCATBL.jcl` | (report utility) | Print catalog balance report | **Reporting** - Generates a balance summary across all account categories. |

#### Data Export/Import Jobs (On-demand)

| Job | JCL | Program | Purpose | Why Batch? |
|---|---|---|---|---|
| **CBEXPORT** | `CBEXPORT.jcl` | CBEXPORT | Export VSAM data to sequential files | **Branch migration** - Exports normalized data for cross-system data movement. |
| **CBIMPORT** | `CBIMPORT.jcl` | CBIMPORT | Import sequential data into VSAM | **Branch migration** - Imports data from external systems. |
| **READACCT** | `READACCT.jcl` | (utility) | Read/extract account records | **Data extraction** |
| **READCARD** | `READCARD.jcl` | (utility) | Read/extract card records | **Data extraction** |
| **READCUST** | `READCUST.jcl` | (utility) | Read/extract customer records | **Data extraction** |
| **READXREF** | `READXREF.jcl` | (utility) | Read/extract cross-reference records | **Data extraction** |
| **DISCGRP** | `DISCGRP.jcl` | (utility) | Refresh disclosure group data | **Reference data** |
| **TCATBALF** | `TCATBALF.jcl` | (utility) | Transaction category balance file maintenance | **Data maintenance** |

### 3.3 VSAM Dataset Definition Jobs (Infrastructure)

These IDCAMS-based jobs define VSAM clusters and are run during initial setup or recovery:

| Job | Purpose |
|---|---|
| `ACCTFILE.jcl` | Define ACCTDATA VSAM cluster |
| `CARDFILE.jcl` | Define CARDDATA VSAM cluster |
| `CUSTFILE.jcl` | Define CUSTDATA VSAM cluster |
| `TRANFILE.jcl` | Define TRANSACT VSAM cluster |
| `XREFFILE.jcl` | Define CARDXREF VSAM cluster |
| `TRANIDX.jcl` | Build alternate index (AIX) for transaction lookups |
| `DEFGDGB.jcl` | Define GDG base for backup generations |
| `DEFGDGD.jcl` | Define GDG base for daily data |
| `ESDSRRDS.jcl` | Define ESDS/RRDS support datasets |

### 3.4 Batch Pattern Summary

| Pattern | Jobs | Rationale |
|---|---|---|
| **VSAM Exclusive Access** | CLOSEFIL / OPENFIL (every chain) | VSAM does not support concurrent read-write from CICS and batch. Files must be closed to CICS before batch can write. |
| **Volume Processing** | POSTTRAN, TRANBKP | High-volume sequential I/O is far more efficient in batch than random CICS updates. |
| **Compute-Intensive Sweep** | INTCALC | Interest calculation touches every account; online execution would monopolize CICS resources. |
| **Consolidation** | COMBTRAN | Merging daily transactions into the master file requires exclusive access and sequential processing of both datasets. |
| **Monthly Aggregation** | CREASTMT, TXT2PDF1, PRTCATBL | Statement generation requires a consistent point-in-time view and produces bulk output. |
| **Dual-Store Sync** | TRANEXTR, TRANCATG, TRANTYPE, MNTTRDB2 | DB2 reference data must be extracted and rebuilt as VSAM files for online programs. |
| **Data Migration** | CBEXPORT, CBIMPORT, READxxxx | Bulk data movement for branch migration and system integration. |

---

## 4. VSAM-to-DB2 Simplification Analysis

This section analyzes what would change if VSAM files were replaced with DB2 tables as a modernization step, independent of any CICS-to-Java migration.

### 4.1 What Gets Simpler

#### 4.1.1 Elimination of the Batch Window (CLOSEFIL / OPENFIL)

**Current State:** Every batch chain begins with CLOSEFIL and ends with OPENFIL because VSAM requires exclusive access for batch writes. During this window, CICS online users cannot access the affected files.

**With DB2:** DB2 supports concurrent read-write access with row-level locking. Batch programs can run SQL against the same tables that CICS programs are querying. The CLOSEFIL/OPENFIL pattern becomes unnecessary.

**Impact:** Eliminates the daily maintenance window entirely. Online availability increases from ~23 hours to 24 hours per day.

#### 4.1.2 Elimination of Dual-Store Sync (TRANEXTR / TRANCATG / TRANTYPE)

**Current State:** Transaction type reference data is mastered in DB2 but must be weekly-extracted and rebuilt as VSAM files for online CICS programs. Three batch jobs (TRANEXTR, TRANCATG, TRANTYPE) and the MNTTRDB2 orchestrator handle this sync.

**With DB2:** Online CICS programs would read transaction types directly from DB2 via embedded SQL, the same way the `app-transaction-type-db2` extension programs (COTRTLIC, COTRTUPC) already do. The extraction pipeline becomes unnecessary.

**Impact:** Eliminates 4 weekly batch jobs and the risk of stale reference data between sync cycles.

#### 4.1.3 Elimination of IDCAMS Boilerplate (9 Jobs)

**Current State:** VSAM cluster definitions, alternate index builds, and GDG management require IDCAMS utility jobs: ACCTFILE, CARDFILE, CUSTFILE, TRANFILE, XREFFILE, TRANIDX, DEFGDGB, DEFGDGD, ESDSRRDS.

**With DB2:** Tables are defined via DDL (`CREATE TABLE`), indexes via `CREATE INDEX`. No IDCAMS, no cluster definitions, no GDG management. Schema changes use `ALTER TABLE`.

**Impact:** Eliminates 9 infrastructure JCL jobs. Schema management becomes SQL-based and version-controllable (e.g., Flyway, Liquibase).

#### 4.1.4 Elimination of AIX Management (TRANIDX, BLDINDEX Steps)

**Current State:** VSAM alternate indexes (AIX) provide secondary key access (e.g., looking up transactions by card number rather than transaction ID). AIX must be explicitly defined, built, and maintained.

**With DB2:** Secondary access paths are standard SQL indexes (`CREATE INDEX`). No separate build step, no explicit AIX management. The optimizer automatically uses indexes.

**Impact:** Eliminates AIX-related JCL and the risk of stale alternate indexes after batch runs.

#### 4.1.5 Simplified COBOL Data Access (SQL vs EXEC CICS READ)

**Current State:** Online programs use `EXEC CICS READ FILE(...)` with explicit key management, record-level I/O, and manual cursor positioning. Batch programs use sequential file I/O with OPEN/READ/CLOSE verbs.

**With DB2:** Both online and batch programs use embedded SQL (`EXEC SQL SELECT ... END-EXEC`). Joins replace multi-file lookups. For example, the CARDXREF lookup pattern:

```
Current (3 VSAM reads):
  EXEC CICS READ FILE('CARDXREF') INTO(XREF-REC) RIDFLD(CARD-NUM)
  EXEC CICS READ FILE('ACCTDATA') INTO(ACCT-REC) RIDFLD(XREF-ACCT-ID)
  EXEC CICS READ FILE('CUSTDATA') INTO(CUST-REC) RIDFLD(XREF-CUST-ID)

With DB2 (1 SQL join):
  EXEC SQL
    SELECT c.*, a.*, cu.*
    FROM CARDXREF x
    JOIN CARDDATA c ON c.CARD_NUM = x.XREF_CARD_NUM
    JOIN ACCTDATA a ON a.ACCT_ID = x.XREF_ACCT_ID
    JOIN CUSTDATA cu ON cu.CUST_ID = x.XREF_CUST_ID
    WHERE x.XREF_CARD_NUM = :WS-CARD-NUM
  END-EXEC
```

**Impact:** Reduces code complexity, eliminates manual file I/O error handling, enables relational joins.

### 4.2 What Does NOT Get Simpler

#### 4.2.1 Business Logic

The core business rules in COBOL programs (interest calculation in CBACT04C, transaction posting in CBTRN03C, statement generation in CBSTM03A/B) remain unchanged regardless of storage technology. The algorithms, validation rules, and computation logic are independent of whether data comes from VSAM or DB2.

#### 4.2.2 CICS Transaction Structure

The CICS program structure (BMS maps, pseudo-conversational patterns, COMMAREA passing, program-to-program XCTL/LINK) is unaffected by the storage change. Programs still use `EXEC CICS SEND MAP` and `EXEC CICS RECEIVE MAP`. Only the data access layer changes.

#### 4.2.3 IMS DB + MQ Integration (Authorization Domain)

The Authorization/Fraud domain already uses IMS HIDAM and DB2. Replacing VSAM with DB2 for the core domains does not simplify the IMS hierarchical database or MQ messaging patterns. The authorization subsystem would still need IMS for its hierarchical storage model, or a separate migration effort to move IMS data to DB2.

#### 4.2.4 Batch Jobs with Business Logic

Batch jobs that perform business logic (POSTTRAN, INTCALC, COMBTRAN, CREASTMT) still need to run as batch processes even with DB2. The reasons for batching shift from "VSAM exclusive access" to "volume processing efficiency" and "point-in-time consistency." However, the elimination of CLOSEFIL/OPENFIL means these jobs can run concurrently with online access, potentially as scheduled DB2 stored procedures or as CICS-triggered batch initiators.

### 4.3 Migration Impact Summary

| Category | VSAM Jobs/Patterns Eliminated | Estimated Complexity Reduction |
|---|---|---|
| Batch window (CLOSEFIL/OPENFIL) | 2 jobs per chain × ~5 chains = ~10 job instances | High - enables 24/7 online availability |
| Dual-store sync | 4 weekly jobs (MNTTRDB2, TRANEXTR, TRANCATG, TRANTYPE) | Medium - eliminates stale-data risk |
| IDCAMS infrastructure | 9 setup/recovery jobs | Low (one-time, not operational) |
| AIX management | TRANIDX + BLDINDEX steps | Low |
| Data access code | ~30+ EXEC CICS READ/WRITE/REWRITE calls across 16 online programs | High - replaced with embedded SQL |
| **Unchanged** | Business logic, CICS structure, IMS+MQ, batch scheduling logic | N/A |

### 4.4 Recommended Migration Sequence

1. **Phase 1 - Reference Data:** Migrate transaction types (already partially in DB2) to DB2-only access. Eliminate TRANEXTR/TRANCATG/TRANTYPE sync jobs.
2. **Phase 2 - Core Entities:** Migrate CUSTDATA, ACCTDATA, CARDDATA, CARDXREF to DB2 tables. Convert EXEC CICS READ/WRITE to embedded SQL. Eliminate CLOSEFIL/OPENFIL for daily batch.
3. **Phase 3 - Transactions:** Migrate TRANSACT and DALYTRAN to DB2. Refactor POSTTRAN, INTCALC, COMBTRAN to use SQL. Eliminate GDG-based backup in favor of DB2 backup/recovery.
4. **Phase 4 - Authorization:** Evaluate consolidating IMS HIDAM authorization data into DB2, unifying all data access under a single RDBMS.

---

*This analysis is based on the repository structure of `infosys-training/uc-legacy-modernization-cobol-to-java` and serves as a foundation for modernization planning. It should be updated as the migration progresses.*
