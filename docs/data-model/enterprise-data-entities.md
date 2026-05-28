# Enterprise Data Entities

## Overview

The CardDemo application is a credit card management system that manages the full lifecycle of credit card operations including customer onboarding, account management, card issuance, transaction processing, authorization, billing, and reporting. This document identifies all enterprise data entities consumed and created by the system.

---

## Entity Inventory

### 1. Customer

| Property | Value |
|----------|-------|
| **Source Copybook** | `CVCUS01Y.cpy`, `CUSTREC.cpy` |
| **VSAM File** | `CUSTFILE` (KSDS) |
| **Record Length** | 500 bytes |
| **Primary Key** | `CUST-ID` (9-digit numeric) |
| **Data File** | `AWS.M2.CARDDEMO.CUSTDATA.PS` |
| **Role** | Consumed and Created |

**Description:** Represents an individual or entity that holds one or more credit card accounts. Stores personal identification, contact, address, and creditworthiness information. This is a core master entity consumed by online CICS programs (account view, card view, transaction processing) and created/updated through customer management operations.

---

### 2. Account

| Property | Value |
|----------|-------|
| **Source Copybook** | `CVACT01Y.cpy` |
| **VSAM File** | `ACCTFILE` (KSDS) |
| **Record Length** | 300 bytes |
| **Primary Key** | `ACCT-ID` (11-digit numeric) |
| **Data File** | `AWS.M2.CARDDEMO.ACCTDATA.PS` |
| **Role** | Consumed and Created |

**Description:** Represents a credit card account linked to a customer. Holds financial state including current balance, credit limits (purchase and cash), billing cycle credits/debits, account dates (open, expiration, reissue), and grouping information used for disclosure/interest rate assignment. Accounts are viewed and updated through CICS online programs (`COACTVWC`, `COACTUPC`) and processed by batch programs (`CBACT01C`–`CBACT04C`).

---

### 3. Credit Card

| Property | Value |
|----------|-------|
| **Source Copybook** | `CVACT02Y.cpy` |
| **VSAM File** | `CARDFILE` (KSDS) |
| **Record Length** | 150 bytes |
| **Primary Key** | `CARD-NUM` (16-character alphanumeric) |
| **Data File** | `AWS.M2.CARDDEMO.CARDDATA.PS` |
| **Role** | Consumed and Created |

**Description:** Represents a physical or virtual credit card issued against an account. Contains the card number, linked account ID, CVV code, embossed cardholder name, expiration date, and active/inactive status. Cards are listed, viewed, and updated via CICS programs (`COCRDLIC`, `COCRDSLC`, `COCRDUPC`).

---

### 4. Card Cross-Reference (XREF)

| Property | Value |
|----------|-------|
| **Source Copybook** | `CVACT03Y.cpy` |
| **VSAM File** | `CARDXREF` (KSDS), with Alternate Index (AIX) |
| **Record Length** | 50 bytes |
| **Primary Key** | `XREF-CARD-NUM` (16-character) |
| **Data File** | `AWS.M2.CARDDEMO.CARDXREF.PS` |
| **Role** | Consumed and Created |

**Description:** A cross-reference entity that links a credit card number to its owning customer and account. This is a critical navigation entity used throughout the application to resolve the card-to-customer-to-account relationship. The alternate index enables lookups by account ID. Used by virtually all online and batch programs when a card number is the entry point.

---

### 5. Transaction

| Property | Value |
|----------|-------|
| **Source Copybook** | `CVTRA05Y.cpy`, `COSTM01.CPY` |
| **VSAM File** | `TRANSACT` (KSDS) |
| **Record Length** | 350 bytes |
| **Primary Key** | `TRAN-CARD-NUM` + `TRAN-ID` (composite, 32 bytes) |
| **Role** | Consumed and Created |

**Description:** Represents a completed financial transaction against a credit card. Includes transaction ID, type code, category code, source channel, description, monetary amount, merchant details (ID, name, city, ZIP), the associated card number, and origination/processing timestamps. Transactions are listed, viewed, and added through CICS programs (`COTRN00C`, `COTRN01C`, `COTRN02C`) and processed in batch by `CBTRN01C`–`CBTRN03C`.

---

### 6. Daily Transaction

| Property | Value |
|----------|-------|
| **Source Copybook** | `CVTRA06Y.cpy` |
| **VSAM File** | `DALYTRAN` (KSDS) |
| **Record Length** | 350 bytes |
| **Primary Key** | `DALYTRAN-ID` |
| **Data File** | `AWS.M2.CARDDEMO.DALYTRAN.PS` |
| **Role** | Consumed (input) and Created (staging) |

**Description:** Represents an incoming daily transaction to be processed in batch. Structurally identical to the Transaction entity but serves as a staging/input file. Daily transactions are consumed by the batch transaction posting process (`CBTRN01C`), validated, and then posted to the main Transaction file. After processing, records are cleared during the batch close/open cycle.

---

### 7. Transaction Type

| Property | Value |
|----------|-------|
| **Source Copybook** | `CVTRA03Y.cpy` |
| **VSAM File** | `TRANTYPE` (KSDS) |
| **Db2 Table** | `CARDDEMO.TRANSACTION_TYPE` |
| **Record Length** | 60 bytes (VSAM) |
| **Primary Key** | `TRAN-TYPE` / `TR_TYPE` (2-character code) |
| **Data File** | `AWS.M2.CARDDEMO.TRANTYPE.PS` |
| **Role** | Consumed (reference data) |

**Description:** A reference/lookup entity defining the types of transactions supported by the system (e.g., Purchase, Payment, Credit, Authorization, Refund, Reversal, Adjustment). Used to classify and validate transactions during processing. Maintained through admin programs (`COTRTLIC`, `COTRTUPC`) in the Db2-based variant.

---

### 8. Transaction Category

| Property | Value |
|----------|-------|
| **Source Copybook** | `CVTRA04Y.cpy` |
| **VSAM File** | `TRANCATG` (KSDS) |
| **Db2 Table** | `CARDDEMO.TRANSACTION_TYPE_CATEGORY` |
| **Record Length** | 60 bytes (VSAM) |
| **Primary Key** | `TRAN-TYPE-CD` + `TRAN-CAT-CD` (composite) |
| **Data File** | `AWS.M2.CARDDEMO.TRANCATG.PS` |
| **Role** | Consumed (reference data) |

**Description:** A subordinate reference entity that further categorizes transactions within each type. For example, under Transaction Type "01" (Purchase), categories include "Regular Sales Draft", "Regular Cash Advance", "ATM Cash Advance", etc. Referenced during transaction processing and reporting to provide granular classification. Has a foreign key relationship to Transaction Type.

---

### 9. Transaction Category Balance

| Property | Value |
|----------|-------|
| **Source Copybook** | `CVTRA01Y.cpy` |
| **VSAM File** | `TCATBALF` (KSDS) |
| **Record Length** | 50 bytes |
| **Primary Key** | `TRANCAT-ACCT-ID` + `TRANCAT-TYPE-CD` + `TRANCAT-CD` (composite) |
| **Data File** | `AWS.M2.CARDDEMO.TCATBALF.PS` |
| **Role** | Created (derived/aggregated) |

**Description:** An aggregated entity that tracks the running balance for each transaction type and category combination per account. Updated during batch transaction processing to maintain category-level financial summaries. Used for billing calculations and reporting.

---

### 10. Disclosure Group

| Property | Value |
|----------|-------|
| **Source Copybook** | `CVTRA02Y.cpy` |
| **VSAM File** | `DISCGRP` (KSDS) |
| **Record Length** | 50 bytes |
| **Primary Key** | `DIS-ACCT-GROUP-ID` + `DIS-TRAN-TYPE-CD` + `DIS-TRAN-CAT-CD` (composite) |
| **Data File** | `AWS.M2.CARDDEMO.DISCGRP.PS` |
| **Role** | Consumed (reference/configuration data) |

**Description:** Defines the interest rate applicable to a specific account group, transaction type, and transaction category combination. Used during interest calculation in batch processing to determine the appropriate rate for billing. Links account groups (from the Account entity's `ACCT-GROUP-ID`) to financial terms.

---

### 11. User Security

| Property | Value |
|----------|-------|
| **Source Copybook** | `CSUSR01Y.cpy` |
| **VSAM File** | `USRSEC` (KSDS) |
| **Record Length** | 80 bytes |
| **Primary Key** | `SEC-USR-ID` (8-character) |
| **Data File** | `AWS.M2.CARDDEMO.USRSEC.PS` |
| **Role** | Consumed and Created |

**Description:** Stores application-level user credentials and access control information. Contains user ID, first/last name, password, and user type (Admin or User). Managed through admin CICS programs (`COUSR00C`–`COUSR03C`) for listing, adding, updating, and deleting users. Consumed during the sign-on process (`COSGN00C`) to authenticate and authorize users.

---

### 12. Authorization Fraud Record (Db2)

| Property | Value |
|----------|-------|
| **Source** | `AUTHFRDS.dcl`, `AUTHFRDS.ddl` |
| **Db2 Table** | `CARDDEMO.AUTHFRDS` |
| **Primary Key** | `CARD_NUM` + `AUTH_TS` (composite) |
| **Role** | Created |

**Description:** A Db2-persisted entity that records authorization events for fraud detection and compliance purposes. Captures the full context of an authorization including card number, timestamp, authorization type, card expiry, message type/source, response codes, transaction and approved amounts, merchant details (category code, ID, name, city, state, ZIP), acquirer country, POS entry mode, match status, fraud flag, and linked account/customer IDs. Created by the authorization processing subsystem.

---

### 13. Pending Authorization Summary (IMS)

| Property | Value |
|----------|-------|
| **Source Copybook** | `CIPAUSMY.cpy` |
| **IMS Database** | `DBPAUTP0` (HIDAM/VSAM) |
| **IMS Segment** | `PAUTSUM0` (root segment) |
| **Segment Size** | 100 bytes |
| **Key Field** | `PA-ACCT-ID` (Account ID, packed decimal) |
| **Role** | Created |

**Description:** An IMS hierarchical root segment that holds summary-level authorization status for an account. Contains account and customer IDs, authorization status, account status array, credit/cash limits, credit/cash balances, and aggregated counts and amounts for approved and declined authorizations. Serves as the parent segment for individual pending authorization detail records.

---

### 14. Pending Authorization Detail (IMS)

| Property | Value |
|----------|-------|
| **Source Copybook** | `CIPAUDTY.cpy` |
| **IMS Database** | `DBPAUTP0` (HIDAM/VSAM) |
| **IMS Segment** | `PAUTDTL1` (child of `PAUTSUM0`) |
| **Segment Size** | 200 bytes |
| **Key Field** | `PA-AUTHORIZATION-KEY` (date + time, packed decimal) |
| **Role** | Created |

**Description:** An IMS hierarchical child segment containing the full detail of an individual pending authorization. Includes authorization date/time, card number, auth type, card expiry, message type/source, auth ID code, response code/reason, processing code, transaction and approved amounts, merchant details, POS entry mode, transaction ID, match status, and fraud indicators. Created during real-time authorization processing and matched against settled transactions.

---

### 15. Export Record (Branch Migration)

| Property | Value |
|----------|-------|
| **Source Copybook** | `CVEXPORT.cpy` |
| **File** | Sequential (PS) |
| **Record Length** | 500 bytes |
| **Data File** | `AWS.M2.CARDDEMO.EXPORT.DATA.PS` |
| **Role** | Created |

**Description:** A multi-format export record used for branch migration data transfers. Contains a common header (record type, timestamp, sequence number, branch ID, region code) followed by a polymorphic data area that holds one of five record types via COBOL REDEFINES: Customer, Account, Transaction, Card Cross-Reference, or Card data. Produced by the batch export program (`CBEXPORT`) and consumed by the import program (`CBIMPORT`).

---

### 16. Pending Authorization Request (MQ Message)

| Property | Value |
|----------|-------|
| **Source Copybook** | `CCPAURQY.cpy` |
| **Transport** | IBM MQ (message queue) |
| **Role** | Consumed (inbound message) |

**Description:** An inbound message structure representing a request to authorize a credit card transaction. Contains authorization date/time, card number, auth type, card expiry, message type/source, processing code, transaction amount, merchant details, acquirer country, POS entry mode, and transaction ID. Consumed from MQ by the authorization processing subsystem.

---

### 17. Pending Authorization Response (MQ Message)

| Property | Value |
|----------|-------|
| **Source Copybook** | `CCPAURLY.cpy` |
| **Transport** | IBM MQ (message queue) |
| **Role** | Created (outbound message) |

**Description:** An outbound message structure representing the result of an authorization decision. Contains the card number, transaction ID, authorization ID code, response code, response reason, and approved amount. Produced by the authorization processing subsystem and sent via MQ back to the requestor.

---

### 18. Authorization Error Log

| Property | Value |
|----------|-------|
| **Source Copybook** | `CCPAUERY.cpy` |
| **File** | Sequential log file |
| **Role** | Created |

**Description:** A structured error logging record for the authorization subsystem. Captures error date/time, application and program names, location code, severity level (Log/Info/Warning/Critical), subsystem identifier (App/CICS/IMS/DB2/MQ/File), two error codes, a descriptive message, and an event key for correlation. Created when errors occur during authorization processing.

---

### 19. COMMAREA (Communication Area)

| Property | Value |
|----------|-------|
| **Source Copybook** | `COCOM01Y.cpy` |
| **Transport** | CICS COMMAREA (in-memory) |
| **Role** | Consumed and Created (transient) |

**Description:** A transient in-memory data structure used for inter-program communication within the CICS online subsystem. Contains general navigation info (from/to transaction and program IDs, user ID, user type, program context), current customer info (ID, first/middle/last name), account info (ID, status), card info (card number), and screen state (last map/mapset). Not persisted but is a critical integration entity passed between all online CICS programs.

---

## Summary: Entity Classification

| # | Entity | Storage | Type | Role |
|---|--------|---------|------|------|
| 1 | Customer | VSAM KSDS | Master | Consumed & Created |
| 2 | Account | VSAM KSDS | Master | Consumed & Created |
| 3 | Credit Card | VSAM KSDS | Master | Consumed & Created |
| 4 | Card Cross-Reference | VSAM KSDS + AIX | Associative | Consumed & Created |
| 5 | Transaction | VSAM KSDS | Transactional | Consumed & Created |
| 6 | Daily Transaction | VSAM KSDS | Staging | Consumed & Created |
| 7 | Transaction Type | VSAM KSDS / Db2 | Reference | Consumed |
| 8 | Transaction Category | VSAM KSDS / Db2 | Reference | Consumed |
| 9 | Transaction Category Balance | VSAM KSDS | Derived | Created |
| 10 | Disclosure Group | VSAM KSDS | Configuration | Consumed |
| 11 | User Security | VSAM KSDS | Master | Consumed & Created |
| 12 | Authorization Fraud Record | Db2 | Audit/Compliance | Created |
| 13 | Pending Auth Summary | IMS HIDAM | Aggregated | Created |
| 14 | Pending Auth Detail | IMS HIDAM | Transactional | Created |
| 15 | Export Record | Sequential PS | Integration | Created |
| 16 | Auth Request | MQ Message | Message | Consumed |
| 17 | Auth Response | MQ Message | Message | Created |
| 18 | Auth Error Log | Sequential | Operational | Created |
| 19 | COMMAREA | CICS Memory | Transient | Consumed & Created |
