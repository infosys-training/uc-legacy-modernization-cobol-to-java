# Logical Data Model

## Overview

This document presents the logical data model for the CardDemo credit card management application. It describes the entities, their attributes, primary/foreign keys, and relationships independent of the physical storage technology (VSAM, Db2, IMS).

---

## Entity-Relationship Diagram (Text Notation)

```
                         +-----------------------+
                         |      CUSTOMER         |
                         |-----------------------|
                         | PK: cust_id           |
                         +-----------+-----------+
                                     |
                                     | 1
                                     |
                                     | *
                    +----------------+----------------+
                    |        CARD_CROSS_REFERENCE      |
                    |----------------------------------|
                    | PK: xref_card_num                |
                    | FK: xref_cust_id -> CUSTOMER     |
                    | FK: xref_acct_id -> ACCOUNT      |
                    +--------+--------+----------------+
                             |        |
                           * |        | *
                             |        |
                           1 |        | 1
              +--------------+--+  +--+--------------+
              |   CREDIT_CARD   |  |     ACCOUNT     |
              |-----------------|  |-----------------|
              | PK: card_num    |  | PK: acct_id     |
              | FK: card_acct_id|  |                  |
              |    -> ACCOUNT   |  |                  |
              +--------+--------+  +---+------+------+
                       |               |      |
                       | 1             | 1    | 1
                       |               |      |
                       | *             | *    | *
              +--------+--------+  +--+------+-------+
              |   TRANSACTION   |  | TRAN_CAT_BALANCE |
              |-----------------|  |------------------|
              | PK: tran_card_  |  | PK: trancat_     |
              |     num +       |  |     acct_id +    |
              |     tran_id     |  |     type_cd +    |
              | FK: tran_type_cd|  |     cat_cd       |
              |  -> TRAN_TYPE   |  | FK: acct_id      |
              | FK: tran_cat_cd |  |    -> ACCOUNT     |
              |  -> TRAN_CATEG  |  +------------------+
              +-----------------+
                       |
                       | uses
                       v
      +----------------+--+     +---------------------+
      | TRANSACTION_TYPE  |<----| TRANSACTION_CATEGORY |
      |-------------------|  1  |----------------------|
      | PK: tran_type     |  *  | PK: tran_type_cd +  |
      |                   |---->|     tran_cat_cd      |
      +-------------------+     | FK: tran_type_cd     |
                                |    -> TRAN_TYPE       |
                                +----------------------+
                                         |
                                         | referenced by
                                         v
                                +--------------------+
                                | DISCLOSURE_GROUP    |
                                |--------------------|
                                | PK: acct_group_id +|
                                |     tran_type_cd + |
                                |     tran_cat_cd    |
                                +--------------------+


              +---------------------+
              |    USER_SECURITY    |
              |---------------------|
              | PK: usr_id          |
              +---------------------+
                (standalone entity)


  === Authorization Subsystem (IMS / Db2 / MQ) ===

              +----------------------------+
              |  PENDING_AUTH_SUMMARY (IMS) |
              |----------------------------|
              | PK: acct_id                |
              | FK: acct_id -> ACCOUNT     |
              | FK: cust_id -> CUSTOMER    |
              +-------------+--------------+
                            |
                            | 1
                            |
                            | *
              +-------------+--------------+
              |  PENDING_AUTH_DETAIL (IMS)  |
              |----------------------------|
              | PK: auth_date + auth_time  |
              | FK: card_num -> CREDIT_CARD|
              +----------------------------+


              +----------------------------+
              | AUTH_FRAUD_RECORD (Db2)     |
              |----------------------------|
              | PK: card_num + auth_ts     |
              | FK: card_num -> CREDIT_CARD|
              | FK: acct_id -> ACCOUNT     |
              | FK: cust_id -> CUSTOMER    |
              +----------------------------+
```

---

## Logical Entity Definitions

### 1. CUSTOMER

The root entity representing a cardholder or account owner.

| Attribute | Logical Type | Nullable | Description |
|-----------|-------------|----------|-------------|
| `cust_id` (PK) | INTEGER(9) | No | Unique customer identifier |
| `cust_first_name` | VARCHAR(25) | No | Customer first name |
| `cust_middle_name` | VARCHAR(25) | Yes | Customer middle name |
| `cust_last_name` | VARCHAR(25) | No | Customer last name |
| `cust_addr_line_1` | VARCHAR(50) | No | Primary address line |
| `cust_addr_line_2` | VARCHAR(50) | Yes | Secondary address line |
| `cust_addr_line_3` | VARCHAR(50) | Yes | Tertiary address line |
| `cust_addr_state_cd` | CHAR(2) | No | U.S. state code |
| `cust_addr_country_cd` | CHAR(3) | No | Country code (e.g., USA) |
| `cust_addr_zip` | VARCHAR(10) | No | Postal/ZIP code |
| `cust_phone_num_1` | VARCHAR(15) | No | Primary phone number |
| `cust_phone_num_2` | VARCHAR(15) | Yes | Secondary phone number |
| `cust_ssn` | INTEGER(9) | No | Social Security Number |
| `cust_govt_issued_id` | VARCHAR(20) | Yes | Government-issued ID number |
| `cust_dob` | DATE | No | Date of birth (YYYY-MM-DD) |
| `cust_eft_account_id` | VARCHAR(10) | Yes | Linked EFT/bank account for payments |
| `cust_pri_card_holder_ind` | CHAR(1) | No | Primary cardholder indicator (Y/N) |
| `cust_fico_credit_score` | INTEGER(3) | No | FICO credit score |

---

### 2. ACCOUNT

Represents a credit card account with financial state.

| Attribute | Logical Type | Nullable | Description |
|-----------|-------------|----------|-------------|
| `acct_id` (PK) | INTEGER(11) | No | Unique account identifier |
| `acct_active_status` | CHAR(1) | No | Account status (Y=Active, N=Inactive) |
| `acct_curr_bal` | DECIMAL(12,2) | No | Current account balance |
| `acct_credit_limit` | DECIMAL(12,2) | No | Purchase credit limit |
| `acct_cash_credit_limit` | DECIMAL(12,2) | No | Cash advance credit limit |
| `acct_open_date` | DATE | No | Date account was opened |
| `acct_expiration_date` | DATE | No | Account expiration date |
| `acct_reissue_date` | DATE | Yes | Date of last card reissue |
| `acct_curr_cyc_credit` | DECIMAL(12,2) | No | Credits in current billing cycle |
| `acct_curr_cyc_debit` | DECIMAL(12,2) | No | Debits in current billing cycle |
| `acct_addr_zip` | VARCHAR(10) | No | Account billing ZIP code |
| `acct_group_id` | VARCHAR(10) | No | Account group for disclosure/interest rate assignment |

---

### 3. CREDIT_CARD

Represents a physical or virtual card issued on an account.

| Attribute | Logical Type | Nullable | Description |
|-----------|-------------|----------|-------------|
| `card_num` (PK) | CHAR(16) | No | Card number (PAN) |
| `card_acct_id` (FK) | INTEGER(11) | No | Parent account ID -> ACCOUNT |
| `card_cvv_cd` | INTEGER(3) | No | Card Verification Value code |
| `card_embossed_name` | VARCHAR(50) | No | Name embossed on the card |
| `card_expiration_date` | DATE | No | Card expiration date |
| `card_active_status` | CHAR(1) | No | Card status (Y=Active, N=Inactive) |

---

### 4. CARD_CROSS_REFERENCE

Associative entity linking cards to customers and accounts.

| Attribute | Logical Type | Nullable | Description |
|-----------|-------------|----------|-------------|
| `xref_card_num` (PK) | CHAR(16) | No | Card number -> CREDIT_CARD |
| `xref_cust_id` (FK) | INTEGER(9) | No | Customer ID -> CUSTOMER |
| `xref_acct_id` (FK) | INTEGER(11) | No | Account ID -> ACCOUNT |

---

### 5. TRANSACTION

Records a completed financial transaction.

| Attribute | Logical Type | Nullable | Description |
|-----------|-------------|----------|-------------|
| `tran_card_num` (PK) | CHAR(16) | No | Card number associated with transaction |
| `tran_id` (PK) | CHAR(16) | No | Unique transaction identifier |
| `tran_type_cd` (FK) | CHAR(2) | No | Transaction type code -> TRANSACTION_TYPE |
| `tran_cat_cd` (FK) | INTEGER(4) | No | Transaction category code -> TRANSACTION_CATEGORY |
| `tran_source` | VARCHAR(10) | No | Transaction source/channel (e.g., POS TERM, OPERATOR) |
| `tran_desc` | VARCHAR(100) | Yes | Transaction description |
| `tran_amt` | DECIMAL(11,2) | No | Transaction amount (signed) |
| `tran_merchant_id` | INTEGER(9) | No | Merchant identifier |
| `tran_merchant_name` | VARCHAR(50) | Yes | Merchant name |
| `tran_merchant_city` | VARCHAR(50) | Yes | Merchant city |
| `tran_merchant_zip` | VARCHAR(10) | Yes | Merchant ZIP code |
| `tran_orig_ts` | TIMESTAMP | No | Transaction origination timestamp |
| `tran_proc_ts` | TIMESTAMP | Yes | Transaction processing timestamp |

---

### 6. DAILY_TRANSACTION

Staging entity for inbound daily transactions (same structure as TRANSACTION).

| Attribute | Logical Type | Nullable | Description |
|-----------|-------------|----------|-------------|
| `dalytran_id` (PK) | CHAR(16) | No | Daily transaction identifier |
| `dalytran_type_cd` | CHAR(2) | No | Transaction type code |
| `dalytran_cat_cd` | INTEGER(4) | No | Transaction category code |
| `dalytran_source` | VARCHAR(10) | No | Transaction source/channel |
| `dalytran_desc` | VARCHAR(100) | Yes | Transaction description |
| `dalytran_amt` | DECIMAL(11,2) | No | Transaction amount (signed) |
| `dalytran_merchant_id` | INTEGER(9) | No | Merchant identifier |
| `dalytran_merchant_name` | VARCHAR(50) | Yes | Merchant name |
| `dalytran_merchant_city` | VARCHAR(50) | Yes | Merchant city |
| `dalytran_merchant_zip` | VARCHAR(10) | Yes | Merchant ZIP code |
| `dalytran_card_num` | CHAR(16) | No | Associated card number |
| `dalytran_orig_ts` | TIMESTAMP | No | Transaction origination timestamp |
| `dalytran_proc_ts` | TIMESTAMP | Yes | Processing timestamp |

---

### 7. TRANSACTION_TYPE

Reference entity defining transaction type codes.

| Attribute | Logical Type | Nullable | Description |
|-----------|-------------|----------|-------------|
| `tran_type` (PK) | CHAR(2) | No | Transaction type code |
| `tran_type_desc` | VARCHAR(50) | No | Transaction type description |

**Reference Values:**

| Code | Description |
|------|-------------|
| 01 | Purchase |
| 02 | Payment |
| 03 | Credit |
| 04 | Authorization |
| 05 | Refund |
| 06 | Reversal |
| 07 | Adjustment |

---

### 8. TRANSACTION_CATEGORY

Reference entity defining sub-categories within each transaction type.

| Attribute | Logical Type | Nullable | Description |
|-----------|-------------|----------|-------------|
| `tran_type_cd` (PK, FK) | CHAR(2) | No | Parent transaction type -> TRANSACTION_TYPE |
| `tran_cat_cd` (PK) | INTEGER(4) | No | Category code within the type |
| `tran_cat_type_desc` | VARCHAR(50) | No | Category description |

**Reference Values (sample):**

| Type | Category | Description |
|------|----------|-------------|
| 01 | 0001 | Regular Sales Draft |
| 01 | 0002 | Regular Cash Advance |
| 01 | 0003 | Convenience Check Debit |
| 01 | 0004 | ATM Cash Advance |
| 01 | 0005 | Interest Amount |
| 02 | 0001 | Cash payment |
| 02 | 0002 | Electronic payment |
| 02 | 0003 | Check payment |
| 03 | 0001 | Credit to Account |
| 03 | 0002 | Credit to Purchase balance |
| 03 | 0003 | Credit to Cash balance |
| 04 | 0001 | Zero dollar authorization |
| 04 | 0002 | Online purchase authorization |
| 04 | 0003 | Travel booking authorization |
| 05 | 0001 | Refund credit |
| 06 | 0001 | Fraud reversal |
| 06 | 0002 | Non-fraud reversal |
| 07 | 0001 | Sales draft credit adjustment |

---

### 9. TRANSACTION_CATEGORY_BALANCE

Derived entity tracking running balances per account/type/category.

| Attribute | Logical Type | Nullable | Description |
|-----------|-------------|----------|-------------|
| `trancat_acct_id` (PK, FK) | INTEGER(11) | No | Account ID -> ACCOUNT |
| `trancat_type_cd` (PK) | CHAR(2) | No | Transaction type code |
| `trancat_cd` (PK) | INTEGER(4) | No | Transaction category code |
| `tran_cat_bal` | DECIMAL(11,2) | No | Running balance for this type/category |

---

### 10. DISCLOSURE_GROUP

Configuration entity for interest rate assignment by account group and transaction classification.

| Attribute | Logical Type | Nullable | Description |
|-----------|-------------|----------|-------------|
| `dis_acct_group_id` (PK) | VARCHAR(10) | No | Account group identifier |
| `dis_tran_type_cd` (PK) | CHAR(2) | No | Transaction type code |
| `dis_tran_cat_cd` (PK) | INTEGER(4) | No | Transaction category code |
| `dis_int_rate` | DECIMAL(6,2) | No | Interest rate percentage |

---

### 11. USER_SECURITY

Application user authentication and authorization entity.

| Attribute | Logical Type | Nullable | Description |
|-----------|-------------|----------|-------------|
| `usr_id` (PK) | CHAR(8) | No | User login ID |
| `usr_fname` | VARCHAR(20) | No | User first name |
| `usr_lname` | VARCHAR(20) | No | User last name |
| `usr_pwd` | CHAR(8) | No | User password |
| `usr_type` | CHAR(1) | No | User type (A=Admin, U=User) |

---

### 12. AUTHORIZATION_FRAUD_RECORD (Db2)

Persisted authorization event record for fraud detection.

| Attribute | Logical Type | Nullable | Description |
|-----------|-------------|----------|-------------|
| `card_num` (PK, FK) | CHAR(16) | No | Card number -> CREDIT_CARD |
| `auth_ts` (PK) | TIMESTAMP | No | Authorization timestamp |
| `auth_type` | CHAR(4) | Yes | Authorization type code |
| `card_expiry_date` | CHAR(4) | Yes | Card expiry (MMYY) |
| `message_type` | CHAR(6) | Yes | Message type identifier |
| `message_source` | CHAR(6) | Yes | Message source system |
| `auth_id_code` | CHAR(6) | Yes | Authorization identification code |
| `auth_resp_code` | CHAR(2) | Yes | Authorization response code |
| `auth_resp_reason` | CHAR(4) | Yes | Response reason code |
| `processing_code` | CHAR(6) | Yes | Processing code |
| `transaction_amt` | DECIMAL(12,2) | Yes | Requested transaction amount |
| `approved_amt` | DECIMAL(12,2) | Yes | Approved amount |
| `merchant_category_code` | CHAR(4) | Yes | Merchant Category Code (MCC) |
| `acqr_country_code` | CHAR(3) | Yes | Acquirer country code |
| `pos_entry_mode` | SMALLINT | Yes | Point-of-sale entry mode |
| `merchant_id` | CHAR(15) | Yes | Merchant identifier |
| `merchant_name` | VARCHAR(22) | Yes | Merchant name |
| `merchant_city` | CHAR(13) | Yes | Merchant city |
| `merchant_state` | CHAR(2) | Yes | Merchant state |
| `merchant_zip` | CHAR(9) | Yes | Merchant ZIP code |
| `transaction_id` | CHAR(15) | Yes | Transaction identifier |
| `match_status` | CHAR(1) | Yes | Match status (P=Pending, D=Declined, E=Expired, M=Matched) |
| `auth_fraud` | CHAR(1) | Yes | Fraud flag (F=Confirmed, R=Removed) |
| `fraud_rpt_date` | DATE | Yes | Fraud report date |
| `acct_id` (FK) | DECIMAL(11) | Yes | Account ID -> ACCOUNT |
| `cust_id` (FK) | DECIMAL(9) | Yes | Customer ID -> CUSTOMER |

---

### 13. PENDING_AUTH_SUMMARY (IMS)

IMS root segment for account-level authorization summaries.

| Attribute | Logical Type | Nullable | Description |
|-----------|-------------|----------|-------------|
| `acct_id` (PK, FK) | DECIMAL(11) | No | Account ID -> ACCOUNT |
| `cust_id` (FK) | INTEGER(9) | No | Customer ID -> CUSTOMER |
| `auth_status` | CHAR(1) | No | Current authorization status |
| `account_status` | CHAR(2) ARRAY[5] | No | Account status array |
| `credit_limit` | DECIMAL(11,2) | No | Credit limit |
| `cash_limit` | DECIMAL(11,2) | No | Cash advance limit |
| `credit_balance` | DECIMAL(11,2) | No | Current credit balance |
| `cash_balance` | DECIMAL(11,2) | No | Current cash balance |
| `approved_auth_cnt` | INTEGER(4) | No | Count of approved authorizations |
| `declined_auth_cnt` | INTEGER(4) | No | Count of declined authorizations |
| `approved_auth_amt` | DECIMAL(11,2) | No | Total approved amount |
| `declined_auth_amt` | DECIMAL(11,2) | No | Total declined amount |

---

### 14. PENDING_AUTH_DETAIL (IMS)

IMS child segment for individual pending authorization records.

| Attribute | Logical Type | Nullable | Description |
|-----------|-------------|----------|-------------|
| `auth_date` (PK) | PACKED_DECIMAL(5) | No | Authorization date (packed) |
| `auth_time` (PK) | PACKED_DECIMAL(9) | No | Authorization time (packed) |
| `auth_orig_date` | CHAR(6) | No | Original date (display format) |
| `auth_orig_time` | CHAR(6) | No | Original time (display format) |
| `card_num` (FK) | CHAR(16) | No | Card number -> CREDIT_CARD |
| `auth_type` | CHAR(4) | No | Authorization type |
| `card_expiry_date` | CHAR(4) | No | Card expiry (MMYY) |
| `message_type` | CHAR(6) | No | Message type |
| `message_source` | CHAR(6) | No | Message source |
| `auth_id_code` | CHAR(6) | No | Authorization ID code |
| `auth_resp_code` | CHAR(2) | No | Response code (00=Approved) |
| `auth_resp_reason` | CHAR(4) | No | Response reason |
| `processing_code` | INTEGER(6) | No | Processing code |
| `transaction_amt` | DECIMAL(12,2) | No | Requested amount |
| `approved_amt` | DECIMAL(12,2) | No | Approved amount |
| `merchant_category_code` | CHAR(4) | No | Merchant Category Code |
| `acqr_country_code` | CHAR(3) | No | Acquirer country code |
| `pos_entry_mode` | INTEGER(2) | No | POS entry mode |
| `merchant_id` | CHAR(15) | No | Merchant ID |
| `merchant_name` | CHAR(22) | No | Merchant name |
| `merchant_city` | CHAR(13) | No | Merchant city |
| `merchant_state` | CHAR(2) | No | Merchant state |
| `merchant_zip` | CHAR(9) | No | Merchant ZIP |
| `transaction_id` | CHAR(15) | No | Transaction ID |
| `match_status` | CHAR(1) | No | Match status |
| `auth_fraud` | CHAR(1) | No | Fraud indicator |
| `fraud_rpt_date` | CHAR(8) | Yes | Fraud report date |

---

## Relationship Summary

| Relationship | Cardinality | Description |
|-------------|-------------|-------------|
| CUSTOMER -> CARD_CROSS_REFERENCE | 1:N | A customer can have multiple cards |
| ACCOUNT -> CARD_CROSS_REFERENCE | 1:N | An account can have multiple cards |
| ACCOUNT -> CREDIT_CARD | 1:N | An account may have multiple cards issued |
| CREDIT_CARD -> TRANSACTION | 1:N | A card can have many transactions |
| TRANSACTION_TYPE -> TRANSACTION_CATEGORY | 1:N | Each type has multiple sub-categories |
| TRANSACTION_TYPE -> TRANSACTION | 1:N | Transactions are classified by type |
| ACCOUNT -> TRAN_CAT_BALANCE | 1:N | Each account tracks balances by type/category |
| DISCLOSURE_GROUP -> ACCOUNT (via group_id) | N:1 | Interest rates are assigned by account group |
| ACCOUNT -> PENDING_AUTH_SUMMARY | 1:1 | One summary record per account |
| PENDING_AUTH_SUMMARY -> PENDING_AUTH_DETAIL | 1:N | Each summary has multiple authorization details |
| CREDIT_CARD -> AUTH_FRAUD_RECORD | 1:N | A card can have multiple auth/fraud records |

---

## Data Flow Context

```
  [Daily Transaction File]
         |
         v (Batch: CBTRN01C-03C)
  [Transaction File] <-------> [Transaction Type]
         |                     [Transaction Category]
         v
  [Tran Category Balance] <--- [Disclosure Group] (interest rates)
         |
         v
  [Account] <---> [Card XREF] <---> [Customer]
         |              |
         v              v
  [Credit Card]   [Pending Auth Summary] (IMS)
         |              |
         v              v
  [Auth Fraud Record]  [Pending Auth Detail] (IMS)
  (Db2)
         ^              ^
         |              |
  [Auth Request] ---> [Auth Processing] ---> [Auth Response]
  (MQ inbound)                              (MQ outbound)

  [Export Record] <--- Batch Export (CBEXPORT) --- [All Master Entities]
```
