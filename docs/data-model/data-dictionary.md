# Data Dictionary

## Overview

This data dictionary provides business definitions for all enterprise data entities and their attributes in the CardDemo credit card management system. Each attribute is documented with its business meaning, source copybook field name, data type, size, constraints, and sample values where applicable.

---

## 1. Customer Entity

**Business Definition:** A Customer represents an individual person who holds or is associated with one or more credit card accounts. The customer entity captures personal identification, contact information, address details, and creditworthiness indicators required for Know Your Customer (KYC) compliance and credit risk assessment.

**Source:** `CVCUS01Y.cpy`, `CUSTREC.cpy` | **Storage:** VSAM KSDS (`CUSTFILE`) | **Record Length:** 500 bytes

| # | Attribute | COBOL Field | Data Type | Size | Key | Business Definition | Sample Value |
|---|-----------|-------------|-----------|------|-----|---------------------|--------------|
| 1 | Customer ID | `CUST-ID` | Numeric | 9 digits | PK | Unique system-generated identifier assigned to each customer upon enrollment. Used as the primary reference across all customer-related operations. | `000000001` |
| 2 | First Name | `CUST-FIRST-NAME` | Alphanumeric | 25 chars | | Legal first name of the customer as it appears on government-issued identification. | `Immanuel` |
| 3 | Middle Name | `CUST-MIDDLE-NAME` | Alphanumeric | 25 chars | | Legal middle name of the customer. May be blank if the customer has no middle name. | `Madeline` |
| 4 | Last Name | `CUST-LAST-NAME` | Alphanumeric | 25 chars | | Legal last name (surname) of the customer as it appears on government-issued identification. | `Kessler` |
| 5 | Address Line 1 | `CUST-ADDR-LINE-1` | Alphanumeric | 50 chars | | Primary street address for the customer's mailing/billing address. | `618 Deshaun Route` |
| 6 | Address Line 2 | `CUST-ADDR-LINE-2` | Alphanumeric | 50 chars | | Secondary address information such as apartment, suite, or unit number. | `Apt. 802` |
| 7 | Address Line 3 | `CUST-ADDR-LINE-3` | Alphanumeric | 50 chars | | Additional address details such as city name, neighborhood, or community. | `Altenwerthshire` |
| 8 | State Code | `CUST-ADDR-STATE-CD` | Alphanumeric | 2 chars | | Two-letter U.S. state or territory abbreviation code (e.g., NC, CA, NY). Validated against a list of valid state codes. | `NC` |
| 9 | Country Code | `CUST-ADDR-COUNTRY-CD` | Alphanumeric | 3 chars | | Three-letter country code (ISO 3166 standard, e.g., USA). | `USA` |
| 10 | ZIP Code | `CUST-ADDR-ZIP` | Alphanumeric | 10 chars | | U.S. postal ZIP code, supports both 5-digit and ZIP+4 formats. | `12546` |
| 11 | Phone Number 1 | `CUST-PHONE-NUM-1` | Alphanumeric | 15 chars | | Primary contact phone number including area code. Validated against North American Numbering Plan (NANPA) area codes. | `(908)119-8310` |
| 12 | Phone Number 2 | `CUST-PHONE-NUM-2` | Alphanumeric | 15 chars | | Secondary/alternate contact phone number. | `(373)693-8684` |
| 13 | Social Security Number | `CUST-SSN` | Numeric | 9 digits | | U.S. Social Security Number used for identity verification and regulatory reporting. Sensitive PII field. | `020973888` |
| 14 | Government-Issued ID | `CUST-GOVT-ISSUED-ID` | Alphanumeric | 20 chars | | Government-issued identification number (e.g., driver's license, passport number). Used as a secondary identity verification mechanism. | `000000000004936843` |
| 15 | Date of Birth | `CUST-DOB-YYYY-MM-DD` | Date | 10 chars | | Customer's date of birth in YYYY-MM-DD format. Used for age verification, fraud detection, and regulatory compliance. | `1961-06-08` |
| 16 | EFT Account ID | `CUST-EFT-ACCOUNT-ID` | Alphanumeric | 10 chars | | Electronic Funds Transfer account identifier linking the customer to a bank account for automatic payment processing. | `0053581756` |
| 17 | Primary Cardholder Indicator | `CUST-PRI-CARD-HOLDER-IND` | Alphanumeric | 1 char | | Indicates whether this customer is the primary cardholder (Y) or an authorized user/secondary cardholder (N) on the account. | `Y` |
| 18 | FICO Credit Score | `CUST-FICO-CREDIT-SCORE` | Numeric | 3 digits | | Customer's FICO credit score at the time of last assessment. Used for credit limit decisions and risk management. Range: 300-850. | `274` |

---

## 2. Account Entity

**Business Definition:** An Account represents a credit card financial account that tracks the monetary relationship between the issuer and the customer. It records credit limits, current balances, billing cycle activity, and lifecycle dates. Each account belongs to an account group that determines applicable interest rates.

**Source:** `CVACT01Y.cpy` | **Storage:** VSAM KSDS (`ACCTFILE`) | **Record Length:** 300 bytes

| # | Attribute | COBOL Field | Data Type | Size | Key | Business Definition | Sample Value |
|---|-----------|-------------|-----------|------|-----|---------------------|--------------|
| 1 | Account ID | `ACCT-ID` | Numeric | 11 digits | PK | Unique system-generated account identifier. Serves as the primary reference for all financial operations on the account. | `00000000001` |
| 2 | Active Status | `ACCT-ACTIVE-STATUS` | Alphanumeric | 1 char | | Indicates the current operational status of the account. Y=Active (open and eligible for transactions), N=Inactive (closed or suspended). | `Y` |
| 3 | Current Balance | `ACCT-CURR-BAL` | Signed Decimal | 12,2 | | The current outstanding balance on the account. Positive values indicate money owed by the customer; negative values indicate a credit balance. | `00000001940.00` |
| 4 | Credit Limit | `ACCT-CREDIT-LIMIT` | Signed Decimal | 12,2 | | The maximum amount of purchase credit extended to this account. Authorizations that would exceed this limit are declined. | `00000020200.00` |
| 5 | Cash Credit Limit | `ACCT-CASH-CREDIT-LIMIT` | Signed Decimal | 12,2 | | The maximum amount of cash advances allowed on this account. Typically a subset of the overall credit limit. | `00000010200.00` |
| 6 | Open Date | `ACCT-OPEN-DATE` | Date | 10 chars | | The date the account was originally opened. Format: YYYY-MM-DD. | `2014-11-20` |
| 7 | Expiration Date | `ACCT-EXPIRAION-DATE` | Date | 10 chars | | The date the account is scheduled to expire. After this date, the account requires renewal or reissue. | `2025-05-20` |
| 8 | Reissue Date | `ACCT-REISSUE-DATE` | Date | 10 chars | | The date of the most recent card reissue on this account. Updated when replacement cards are issued. | `2025-05-20` |
| 9 | Current Cycle Credit | `ACCT-CURR-CYC-CREDIT` | Signed Decimal | 12,2 | | Total credit (payment and adjustment) amounts posted to the account during the current billing cycle. Reset at cycle close. | `00000000000.00` |
| 10 | Current Cycle Debit | `ACCT-CURR-CYC-DEBIT` | Signed Decimal | 12,2 | | Total debit (purchase and cash advance) amounts posted to the account during the current billing cycle. Reset at cycle close. | `00000000000.00` |
| 11 | Billing ZIP Code | `ACCT-ADDR-ZIP` | Alphanumeric | 10 chars | | The ZIP code associated with the account billing address. May differ from the customer's mailing address for fraud verification purposes (AVS). | `A000000000` |
| 12 | Account Group ID | `ACCT-GROUP-ID` | Alphanumeric | 10 chars | | Identifies the product/pricing group this account belongs to. Used to look up applicable interest rates and disclosure terms in the Disclosure Group entity. | `A000000000` |

---

## 3. Credit Card Entity

**Business Definition:** A Credit Card represents a payment instrument (physical plastic card or virtual card number) issued on a credit card account. Each card has a unique Primary Account Number (PAN), security codes, and lifecycle management attributes. Multiple cards can be issued on a single account (e.g., for authorized users).

**Source:** `CVACT02Y.cpy` | **Storage:** VSAM KSDS (`CARDFILE`) | **Record Length:** 150 bytes

| # | Attribute | COBOL Field | Data Type | Size | Key | Business Definition | Sample Value |
|---|-----------|-------------|-----------|------|-----|---------------------|--------------|
| 1 | Card Number | `CARD-NUM` | Alphanumeric | 16 chars | PK | The Primary Account Number (PAN) uniquely identifying this card. This is the number embossed on the physical card and used for transaction authorization. | `0500024453765740` |
| 2 | Account ID | `CARD-ACCT-ID` | Numeric | 11 digits | FK | The account this card is issued against. Links to the Account entity. All transactions on this card affect this account's balance. | `00000000050` |
| 3 | CVV Code | `CARD-CVV-CD` | Numeric | 3 digits | | Card Verification Value - a three-digit security code printed on the card. Used for card-not-present transaction verification. | `747` |
| 4 | Embossed Name | `CARD-EMBOSSED-NAME` | Alphanumeric | 50 chars | | The cardholder name as embossed on the physical card. May differ from the customer's legal name (e.g., preferred name or business name). | `Aniya Von` |
| 5 | Expiration Date | `CARD-EXPIRAION-DATE` | Date | 10 chars | | The expiration date of the card. Transactions after this date are declined. Format: YYYY-MM-DD. | `2023-03-09` |
| 6 | Active Status | `CARD-ACTIVE-STATUS` | Alphanumeric | 1 char | | Current operational status of the card. Y=Active (can be used for transactions), N=Inactive (blocked, lost, stolen, or cancelled). | `Y` |

---

## 4. Card Cross-Reference Entity

**Business Definition:** The Card Cross-Reference is an associative (junction) entity that establishes the many-to-many-to-many relationship between cards, customers, and accounts. It enables the system to quickly resolve which customer and account a given card number belongs to - a fundamental lookup used in nearly every online and batch operation. An alternate index on Account ID enables reverse lookups.

**Source:** `CVACT03Y.cpy` | **Storage:** VSAM KSDS (`CARDXREF`) with AIX | **Record Length:** 50 bytes

| # | Attribute | COBOL Field | Data Type | Size | Key | Business Definition | Sample Value |
|---|-----------|-------------|-----------|------|-----|---------------------|--------------|
| 1 | Card Number | `XREF-CARD-NUM` | Alphanumeric | 16 chars | PK | The card number serving as the primary lookup key. Links to the Credit Card entity. | `0500024453765740` |
| 2 | Customer ID | `XREF-CUST-ID` | Numeric | 9 digits | FK | The customer who owns this card. Links to the Customer entity. | `000000005` |
| 3 | Account ID | `XREF-ACCT-ID` | Numeric | 11 digits | FK | The account this card is associated with. Links to the Account entity. | `00000000050` |

---

## 5. Transaction Entity

**Business Definition:** A Transaction represents a completed financial event that has been posted to a credit card account. This includes purchases, payments, credits, refunds, reversals, and adjustments. Each transaction records the full context including the monetary amount, merchant information, origination channel, timestamps, and classification codes used for reporting and interest calculation.

**Source:** `CVTRA05Y.cpy`, `COSTM01.CPY` | **Storage:** VSAM KSDS (`TRANSACT`) | **Record Length:** 350 bytes

| # | Attribute | COBOL Field | Data Type | Size | Key | Business Definition | Sample Value |
|---|-----------|-------------|-----------|------|-----|---------------------|--------------|
| 1 | Transaction ID | `TRAN-ID` | Alphanumeric | 16 chars | PK | Unique system-generated identifier for each transaction. Used for transaction lookup, reporting, and audit trail. | `0000000000683580` |
| 2 | Transaction Type Code | `TRAN-TYPE-CD` | Alphanumeric | 2 chars | FK | Code classifying the nature of the transaction. References the Transaction Type entity (01=Purchase, 02=Payment, etc.). | `01` |
| 3 | Transaction Category Code | `TRAN-CAT-CD` | Numeric | 4 digits | FK | Sub-classification within the transaction type. References the Transaction Category entity (e.g., 0001=Regular Sales Draft). | `0001` |
| 4 | Transaction Source | `TRAN-SOURCE` | Alphanumeric | 10 chars | | Channel or system that originated the transaction. Identifies how the transaction entered the system. | `POS TERM` |
| 5 | Transaction Description | `TRAN-DESC` | Alphanumeric | 100 chars | | Free-text description of the transaction, typically including the merchant name and transaction details. | `Purchase at Abshire-Lowe` |
| 6 | Transaction Amount | `TRAN-AMT` | Signed Decimal | 11,2 | | The monetary amount of the transaction. Positive for debits (purchases, cash advances); negative for credits (payments, refunds). | `00000050.47` |
| 7 | Merchant ID | `TRAN-MERCHANT-ID` | Numeric | 9 digits | | Identifier of the merchant where the transaction occurred. Used for merchant-level reporting and dispute resolution. | `800000000` |
| 8 | Merchant Name | `TRAN-MERCHANT-NAME` | Alphanumeric | 50 chars | | Business name of the merchant. Appears on customer statements and reports. | `Abshire-Lowe` |
| 9 | Merchant City | `TRAN-MERCHANT-CITY` | Alphanumeric | 50 chars | | City where the merchant is located. Used for geographic transaction analysis. | `North Enoshaven` |
| 10 | Merchant ZIP | `TRAN-MERCHANT-ZIP` | Alphanumeric | 10 chars | | Postal code of the merchant location. | `72112` |
| 11 | Card Number | `TRAN-CARD-NUM` | Alphanumeric | 16 chars | FK | The card number used for this transaction. Links to the Credit Card entity via Card Cross-Reference. | `4859452612877065` |
| 12 | Origination Timestamp | `TRAN-ORIG-TS` | Timestamp | 26 chars | | Date and time the transaction was originated at the point of sale or source system. Format: YYYY-MM-DD HH:MM:SS.NNNNNN. | `2022-06-10 19:27:53.000000` |
| 13 | Processing Timestamp | `TRAN-PROC-TS` | Timestamp | 26 chars | | Date and time the transaction was processed and posted to the account by the batch system. | (set during batch processing) |

---

## 6. Daily Transaction Entity

**Business Definition:** A Daily Transaction is a staging record representing an incoming transaction that has been received but not yet posted to the main Transaction file. Daily transactions are loaded into this staging file during the day and processed during the nightly batch cycle. After successful validation and posting, records move to the main Transaction file and the staging file is cleared for the next business day.

**Source:** `CVTRA06Y.cpy` | **Storage:** VSAM KSDS (`DALYTRAN`) | **Record Length:** 350 bytes

*Attributes are structurally identical to the Transaction entity (see Section 5) with the prefix `DALYTRAN-` instead of `TRAN-`.*

---

## 7. Transaction Type Entity

**Business Definition:** Transaction Type is a reference data entity that defines the major classifications of financial activity supported by the credit card system. Each type represents a fundamentally different kind of monetary event. This entity serves as a controlled vocabulary ensuring consistent transaction classification across all subsystems.

**Source:** `CVTRA03Y.cpy` (VSAM), `DCLTRTYP.dcl` / `TRNTYPE.ddl` (Db2) | **Storage:** VSAM KSDS (`TRANTYPE`) or Db2 (`CARDDEMO.TRANSACTION_TYPE`)

| # | Attribute | COBOL Field (VSAM / Db2) | Data Type | Size | Key | Business Definition | Sample Value |
|---|-----------|--------------------------|-----------|------|-----|---------------------|--------------|
| 1 | Transaction Type Code | `TRAN-TYPE` / `TR_TYPE` | Alphanumeric | 2 chars | PK | A two-character code uniquely identifying the transaction type. | `01` |
| 2 | Type Description | `TRAN-TYPE-DESC` / `TR_DESCRIPTION` | Alphanumeric | 50 chars | | Human-readable description of the transaction type. | `Purchase` |

**Defined Values:**

| Code | Business Definition |
|------|---------------------|
| `01` | **Purchase** - A debit transaction where the cardholder acquires goods or services from a merchant. |
| `02` | **Payment** - A credit transaction where the cardholder remits funds to reduce the account balance. |
| `03` | **Credit** - A non-payment credit applied to the account (e.g., billing adjustment, promotional credit). |
| `04` | **Authorization** - A hold placed on available credit for a pending transaction, not yet settled. |
| `05` | **Refund** - A credit issued when a previously completed purchase is returned or disputed. |
| `06` | **Reversal** - Cancellation of a previously posted transaction, restoring the original account balance. |
| `07` | **Adjustment** - A correction to the account balance made by operations, typically for billing errors or disputes. |

---

## 8. Transaction Category Entity

**Business Definition:** Transaction Category provides a second level of classification within each Transaction Type. Categories distinguish between different methods, channels, or specific scenarios for a given type of transaction. For example, within the Purchase type, categories differentiate between in-store sales drafts, cash advances, ATM withdrawals, and interest accrual. Each category is tied to a specific interest rate through the Disclosure Group.

**Source:** `CVTRA04Y.cpy` (VSAM), `DCLTRCAT.dcl` / `TRNTYCAT.ddl` (Db2) | **Storage:** VSAM KSDS (`TRANCATG`) or Db2 (`CARDDEMO.TRANSACTION_TYPE_CATEGORY`)

| # | Attribute | COBOL Field (VSAM / Db2) | Data Type | Size | Key | Business Definition | Sample Value |
|---|-----------|--------------------------|-----------|------|-----|---------------------|--------------|
| 1 | Transaction Type Code | `TRAN-TYPE-CD` / `TRC_TYPE_CODE` | Alphanumeric | 2 chars | PK, FK | Parent transaction type code. References Transaction Type entity. | `01` |
| 2 | Category Code | `TRAN-CAT-CD` / `TRC_TYPE_CATEGORY` | Numeric/Char | 4 digits | PK | Numeric code identifying the specific sub-category within the type. | `0001` |
| 3 | Category Description | `TRAN-CAT-TYPE-DESC` / `TRC_CAT_DATA` | Alphanumeric | 50 chars | | Human-readable description of the transaction category. | `Regular Sales Draft` |

**Defined Values:**

| Type | Category | Business Definition |
|------|----------|---------------------|
| 01 | 0001 | **Regular Sales Draft** - Standard point-of-sale purchase transaction using the credit card. |
| 01 | 0002 | **Regular Cash Advance** - Cash withdrawal obtained using the credit card at a bank teller. |
| 01 | 0003 | **Convenience Check Debit** - Debit resulting from a convenience check issued against the credit line. |
| 01 | 0004 | **ATM Cash Advance** - Cash withdrawal obtained from an automated teller machine (ATM). |
| 01 | 0005 | **Interest Amount** - Interest charges accrued on outstanding purchase or cash advance balances. |
| 02 | 0001 | **Cash Payment** - Payment made by the cardholder using cash (in-branch). |
| 02 | 0002 | **Electronic Payment** - Payment made via electronic funds transfer (ACH/EFT). |
| 02 | 0003 | **Check Payment** - Payment made by personal check. |
| 03 | 0001 | **Credit to Account** - General credit applied to the overall account balance. |
| 03 | 0002 | **Credit to Purchase Balance** - Credit applied specifically to the purchase balance portion. |
| 03 | 0003 | **Credit to Cash Balance** - Credit applied specifically to the cash advance balance portion. |
| 04 | 0001 | **Zero Dollar Authorization** - Authorization for $0.00, typically used for card validation. |
| 04 | 0002 | **Online Purchase Authorization** - Authorization hold for an e-commerce/online transaction. |
| 04 | 0003 | **Travel Booking Authorization** - Authorization hold for a travel-related booking (hotel, airline). |
| 05 | 0001 | **Refund Credit** - Credit issued for a returned purchase or merchant-initiated refund. |
| 06 | 0001 | **Fraud Reversal** - Reversal of a transaction confirmed as fraudulent. |
| 06 | 0002 | **Non-Fraud Reversal** - Reversal of a transaction for reasons other than fraud (e.g., duplicate charge). |
| 07 | 0001 | **Sales Draft Credit Adjustment** - Manual adjustment to correct a sales draft amount. |

---

## 9. Transaction Category Balance Entity

**Business Definition:** Transaction Category Balance is a derived/aggregated entity that maintains running balance totals for each combination of account, transaction type, and transaction category. Updated during batch transaction processing, it enables the system to track separate balances (e.g., purchase balance vs. cash advance balance) for each account, which is essential for applying the correct interest rate from the Disclosure Group.

**Source:** `CVTRA01Y.cpy` | **Storage:** VSAM KSDS (`TCATBALF`) | **Record Length:** 50 bytes

| # | Attribute | COBOL Field | Data Type | Size | Key | Business Definition | Sample Value |
|---|-----------|-------------|-----------|------|-----|---------------------|--------------|
| 1 | Account ID | `TRANCAT-ACCT-ID` | Numeric | 11 digits | PK, FK | The account this balance belongs to. References the Account entity. | `00000000001` |
| 2 | Transaction Type Code | `TRANCAT-TYPE-CD` | Alphanumeric | 2 chars | PK | The transaction type this balance is for. | `01` |
| 3 | Category Code | `TRANCAT-CD` | Numeric | 4 digits | PK | The transaction category this balance is for. | `0001` |
| 4 | Category Balance | `TRAN-CAT-BAL` | Signed Decimal | 11,2 | | The accumulated monetary balance for this account/type/category combination. Updated with each batch posting cycle. | `000000000.00` |

---

## 10. Disclosure Group Entity

**Business Definition:** The Disclosure Group is a configuration entity that defines the interest rate applicable to specific combinations of account group, transaction type, and transaction category. It implements the credit card issuer's pricing strategy by allowing different interest rates for different kinds of activity (e.g., a higher rate for cash advances than for purchases). Account group assignment (from the Account entity) determines which set of rates applies.

**Source:** `CVTRA02Y.cpy` | **Storage:** VSAM KSDS (`DISCGRP`) | **Record Length:** 50 bytes

| # | Attribute | COBOL Field | Data Type | Size | Key | Business Definition | Sample Value |
|---|-----------|-------------|-----------|------|-----|---------------------|--------------|
| 1 | Account Group ID | `DIS-ACCT-GROUP-ID` | Alphanumeric | 10 chars | PK | Identifier for the product/pricing group. Accounts in the same group share the same interest rate schedule. | `A000000000` |
| 2 | Transaction Type Code | `DIS-TRAN-TYPE-CD` | Alphanumeric | 2 chars | PK | The transaction type this rate applies to. | `01` |
| 3 | Category Code | `DIS-TRAN-CAT-CD` | Numeric | 4 digits | PK | The transaction category this rate applies to. | `0001` |
| 4 | Interest Rate | `DIS-INT-RATE` | Signed Decimal | 6,2 | | The annual percentage rate (APR) applied to balances in this type/category for accounts in this group. | `0150.00` (representing 15.00%) |

---

## 11. User Security Entity

**Business Definition:** User Security stores authentication and authorization data for application operators. It controls access to the CardDemo CICS online system by maintaining login credentials and role assignments. Users with Admin type have access to administrative functions (user management, transaction type maintenance), while User type accounts are restricted to operational functions (account/card/transaction management).

**Source:** `CSUSR01Y.cpy` | **Storage:** VSAM KSDS (`USRSEC`) | **Record Length:** 80 bytes

| # | Attribute | COBOL Field | Data Type | Size | Key | Business Definition | Sample Value |
|---|-----------|-------------|-----------|------|-----|---------------------|--------------|
| 1 | User ID | `SEC-USR-ID` | Alphanumeric | 8 chars | PK | Unique login identifier for the application user. Used for authentication at sign-on. | `ADMIN001` |
| 2 | First Name | `SEC-USR-FNAME` | Alphanumeric | 20 chars | | First name of the user. Displayed on screen for user identification. | `John` |
| 3 | Last Name | `SEC-USR-LNAME` | Alphanumeric | 20 chars | | Last name of the user. | `Smith` |
| 4 | Password | `SEC-USR-PWD` | Alphanumeric | 8 chars | | User password for authentication. Stored in plaintext in the legacy system. | `PASS1234` |
| 5 | User Type | `SEC-USR-TYPE` | Alphanumeric | 1 char | | Role/access level of the user. Determines which menu options and functions are accessible. | `A` |

**User Type Values:**

| Code | Business Definition |
|------|---------------------|
| `A` | **Administrator** - Full access to all application functions including user management, transaction type maintenance, and all operational screens. |
| `U` | **User** - Standard operator access limited to operational functions: account view/update, card management, transaction processing, and reporting. |

---

## 12. Authorization Fraud Record Entity (Db2)

**Business Definition:** The Authorization Fraud Record captures the complete context of every credit card authorization event processed by the system. It serves dual purposes: (1) enabling real-time fraud detection by recording authorization patterns, merchant details, and geographic indicators, and (2) providing a compliance audit trail for regulatory requirements (PCI DSS, Regulation E). Each record represents a single authorization attempt with its outcome.

**Source:** `AUTHFRDS.dcl`, `AUTHFRDS.ddl` | **Storage:** Db2 (`CARDDEMO.AUTHFRDS`)

| # | Attribute | Db2 Column | Data Type | Key | Business Definition |
|---|-----------|------------|-----------|-----|---------------------|
| 1 | Card Number | `CARD_NUM` | CHAR(16) | PK | Card number for which authorization was requested. |
| 2 | Authorization Timestamp | `AUTH_TS` | TIMESTAMP | PK | Precise timestamp of the authorization event. Combined with Card Number forms a unique key. |
| 3 | Authorization Type | `AUTH_TYPE` | CHAR(4) | | Type of authorization request (e.g., initial, re-authorization, reversal). |
| 4 | Card Expiry Date | `CARD_EXPIRY_DATE` | CHAR(4) | | Expiry date as provided in the authorization request (MMYY format). |
| 5 | Message Type | `MESSAGE_TYPE` | CHAR(6) | | ISO 8583 message type identifier classifying the authorization message. |
| 6 | Message Source | `MESSAGE_SOURCE` | CHAR(6) | | Identifier of the system or network that originated the authorization. |
| 7 | Authorization ID Code | `AUTH_ID_CODE` | CHAR(6) | | Unique authorization approval code assigned to approved transactions. |
| 8 | Response Code | `AUTH_RESP_CODE` | CHAR(2) | | Two-character code indicating the authorization decision (00=Approved). |
| 9 | Response Reason | `AUTH_RESP_REASON` | CHAR(4) | | Code providing additional detail on why the authorization was approved or declined. |
| 10 | Processing Code | `PROCESSING_CODE` | CHAR(6) | | Code identifying the type of processing to be applied to the transaction. |
| 11 | Transaction Amount | `TRANSACTION_AMT` | DECIMAL(12,2) | | The amount requested for authorization. |
| 12 | Approved Amount | `APPROVED_AMT` | DECIMAL(12,2) | | The amount actually approved (may differ from requested for partial approvals). |
| 13 | Merchant Category Code | `MERCHANT_CATAGORY_CODE` | CHAR(4) | | MCC code classifying the merchant's business type (ISO 18245). |
| 14 | Acquirer Country Code | `ACQR_COUNTRY_CODE` | CHAR(3) | | Country code of the acquiring bank/terminal. Used for cross-border fraud detection. |
| 15 | POS Entry Mode | `POS_ENTRY_MODE` | SMALLINT | | Code indicating how the card data was captured (e.g., swiped, chip, contactless, manual entry). |
| 16 | Merchant ID | `MERCHANT_ID` | CHAR(15) | | Unique identifier assigned to the merchant by the acquirer. |
| 17 | Merchant Name | `MERCHANT_NAME` | VARCHAR(22) | | Business name of the merchant. |
| 18 | Merchant City | `MERCHANT_CITY` | CHAR(13) | | City where the merchant is located. |
| 19 | Merchant State | `MERCHANT_STATE` | CHAR(2) | | State code where the merchant is located. |
| 20 | Merchant ZIP | `MERCHANT_ZIP` | CHAR(9) | | Postal code of the merchant location. |
| 21 | Transaction ID | `TRANSACTION_ID` | CHAR(15) | | Transaction identifier for matching authorizations with settlements. |
| 22 | Match Status | `MATCH_STATUS` | CHAR(1) | | Status of matching this authorization to a settled transaction. |
| 23 | Authorization Fraud Flag | `AUTH_FRAUD` | CHAR(1) | | Fraud determination status for this authorization. |
| 24 | Fraud Report Date | `FRAUD_RPT_DATE` | DATE | | Date the fraud was reported, if applicable. |
| 25 | Account ID | `ACCT_ID` | DECIMAL(11) | FK | Account associated with this authorization. |
| 26 | Customer ID | `CUST_ID` | DECIMAL(9) | FK | Customer associated with this authorization. |

**Match Status Values:**

| Code | Business Definition |
|------|---------------------|
| `P` | **Pending** - Authorization is awaiting a matching settlement transaction. |
| `D` | **Declined** - Authorization was declined. |
| `E` | **Expired** - Authorization expired without a matching settlement. |
| `M` | **Matched** - Authorization was successfully matched with a settled transaction. |

**Fraud Flag Values:**

| Code | Business Definition |
|------|---------------------|
| `F` | **Fraud Confirmed** - The transaction has been confirmed as fraudulent. |
| `R` | **Fraud Removed** - A previously flagged fraud designation has been removed/cleared. |

---

## 13. Pending Authorization Summary Entity (IMS)

**Business Definition:** The Pending Authorization Summary is an IMS hierarchical database root segment that provides an account-level snapshot of authorization activity. It stores the account's current limits, balances, and aggregated authorization statistics (counts and amounts for both approvals and declines). This summary enables rapid authorization decisions without scanning individual authorization records.

**Source:** `CIPAUSMY.cpy` | **Storage:** IMS HIDAM (`DBPAUTP0`, segment `PAUTSUM0`)

| # | Attribute | COBOL Field | Data Type | Size | Key | Business Definition |
|---|-----------|-------------|-----------|------|-----|---------------------|
| 1 | Account ID | `PA-ACCT-ID` | Packed Decimal | 11 digits | PK, FK | Account identifier. Root key for the IMS hierarchy. |
| 2 | Customer ID | `PA-CUST-ID` | Numeric | 9 digits | FK | Customer associated with this account. |
| 3 | Authorization Status | `PA-AUTH-STATUS` | Alphanumeric | 1 char | | Overall authorization eligibility status for the account. |
| 4 | Account Status Array | `PA-ACCOUNT-STATUS` | Alphanumeric | 2 chars x 5 | | Array of account status codes covering different status dimensions. |
| 5 | Credit Limit | `PA-CREDIT-LIMIT` | Packed Decimal | 11,2 | | Current credit limit for authorization decisions. |
| 6 | Cash Limit | `PA-CASH-LIMIT` | Packed Decimal | 11,2 | | Current cash advance limit. |
| 7 | Credit Balance | `PA-CREDIT-BALANCE` | Packed Decimal | 11,2 | | Current outstanding credit balance including pending authorizations. |
| 8 | Cash Balance | `PA-CASH-BALANCE` | Packed Decimal | 11,2 | | Current outstanding cash advance balance including pending authorizations. |
| 9 | Approved Authorization Count | `PA-APPROVED-AUTH-CNT` | Binary Integer | 4 digits | | Running count of approved authorizations in the current period. |
| 10 | Declined Authorization Count | `PA-DECLINED-AUTH-CNT` | Binary Integer | 4 digits | | Running count of declined authorizations in the current period. |
| 11 | Approved Authorization Amount | `PA-APPROVED-AUTH-AMT` | Packed Decimal | 11,2 | | Cumulative dollar amount of approved authorizations. |
| 12 | Declined Authorization Amount | `PA-DECLINED-AUTH-AMT` | Packed Decimal | 11,2 | | Cumulative dollar amount of declined authorizations. |

---

## 14. Pending Authorization Detail Entity (IMS)

**Business Definition:** The Pending Authorization Detail is an IMS child segment containing the full context of an individual pending authorization transaction. Each detail record represents a single authorization request with its outcome, merchant information, and fraud tracking status. Detail segments are created during real-time authorization processing and are matched against settled transactions during batch processing.

**Source:** `CIPAUDTY.cpy` | **Storage:** IMS HIDAM (`DBPAUTP0`, segment `PAUTDTL1`, child of `PAUTSUM0`)

| # | Attribute | COBOL Field | Data Type | Size | Key | Business Definition |
|---|-----------|-------------|-----------|------|-----|---------------------|
| 1 | Authorization Date (packed) | `PA-AUTH-DATE-9C` | Packed Decimal | 5 bytes | PK | Authorization date in packed decimal format (sequence key). |
| 2 | Authorization Time (packed) | `PA-AUTH-TIME-9C` | Packed Decimal | 9 bytes | PK | Authorization time in packed decimal format (sequence key). |
| 3 | Original Date | `PA-AUTH-ORIG-DATE` | Alphanumeric | 6 chars | | Display-format date of the original authorization. |
| 4 | Original Time | `PA-AUTH-ORIG-TIME` | Alphanumeric | 6 chars | | Display-format time of the original authorization. |
| 5 | Card Number | `PA-CARD-NUM` | Alphanumeric | 16 chars | FK | Card number used in this authorization. |
| 6 | Authorization Type | `PA-AUTH-TYPE` | Alphanumeric | 4 chars | | Type of authorization. |
| 7 | Card Expiry Date | `PA-CARD-EXPIRY-DATE` | Alphanumeric | 4 chars | | Card expiry as submitted. |
| 8 | Message Type | `PA-MESSAGE-TYPE` | Alphanumeric | 6 chars | | Authorization message type. |
| 9 | Message Source | `PA-MESSAGE-SOURCE` | Alphanumeric | 6 chars | | Source system of the authorization. |
| 10 | Auth ID Code | `PA-AUTH-ID-CODE` | Alphanumeric | 6 chars | | Authorization approval code. |
| 11 | Response Code | `PA-AUTH-RESP-CODE` | Alphanumeric | 2 chars | | Authorization decision code (00=Approved). |
| 12 | Response Reason | `PA-AUTH-RESP-REASON` | Alphanumeric | 4 chars | | Detailed reason for the authorization decision. |
| 13 | Processing Code | `PA-PROCESSING-CODE` | Numeric | 6 digits | | Transaction processing classification code. |
| 14 | Transaction Amount | `PA-TRANSACTION-AMT` | Packed Decimal | 12,2 | | Requested transaction amount. |
| 15 | Approved Amount | `PA-APPROVED-AMT` | Packed Decimal | 12,2 | | Amount approved (may differ for partial approvals). |
| 16 | Merchant Category Code | `PA-MERCHANT-CATAGORY-CODE` | Alphanumeric | 4 chars | | MCC code for the merchant. |
| 17 | Acquirer Country Code | `PA-ACQR-COUNTRY-CODE` | Alphanumeric | 3 chars | | Country code of the acquiring institution. |
| 18 | POS Entry Mode | `PA-POS-ENTRY-MODE` | Numeric | 2 digits | | Method of card data capture at the point of sale. |
| 19 | Merchant ID | `PA-MERCHANT-ID` | Alphanumeric | 15 chars | | Merchant identifier. |
| 20 | Merchant Name | `PA-MERCHANT-NAME` | Alphanumeric | 22 chars | | Merchant business name. |
| 21 | Merchant City | `PA-MERCHANT-CITY` | Alphanumeric | 13 chars | | Merchant city. |
| 22 | Merchant State | `PA-MERCHANT-STATE` | Alphanumeric | 2 chars | | Merchant state code. |
| 23 | Merchant ZIP | `PA-MERCHANT-ZIP` | Alphanumeric | 9 chars | | Merchant postal code. |
| 24 | Transaction ID | `PA-TRANSACTION-ID` | Alphanumeric | 15 chars | | Transaction identifier for settlement matching. |
| 25 | Match Status | `PA-MATCH-STATUS` | Alphanumeric | 1 char | | Settlement match status (P=Pending, D=Declined, E=Expired, M=Matched). |
| 26 | Fraud Flag | `PA-AUTH-FRAUD` | Alphanumeric | 1 char | | Fraud indicator (F=Confirmed, R=Removed). |
| 27 | Fraud Report Date | `PA-FRAUD-RPT-DATE` | Alphanumeric | 8 chars | | Date the fraud was reported. |

---

## 15. Export Record Entity

**Business Definition:** The Export Record is a multi-format data structure used for branch migration and data portability. It supports extracting normalized enterprise data (customers, accounts, transactions, cards, cross-references) into a single sequential file format with a common header and polymorphic data payload. This enables data transfer between branches or environments.

**Source:** `CVEXPORT.cpy` | **Storage:** Sequential file (PS) | **Record Length:** 500 bytes

**Common Header Attributes:**

| # | Attribute | COBOL Field | Data Type | Size | Key | Business Definition |
|---|-----------|-------------|-----------|------|-----|---------------------|
| 1 | Record Type | `EXPORT-REC-TYPE` | Alphanumeric | 1 char | | Discriminator indicating the type of data in the record payload (C=Customer, A=Account, T=Transaction, X=Cross-Reference, R=Card). |
| 2 | Timestamp | `EXPORT-TIMESTAMP` | Alphanumeric | 26 chars | | Date and time of export in ISO timestamp format. |
| 3 | Sequence Number | `EXPORT-SEQUENCE-NUM` | Binary Integer | 9 digits | | Sequential record number within the export batch. |
| 4 | Branch ID | `EXPORT-BRANCH-ID` | Alphanumeric | 4 chars | | Identifier of the branch from which data is being exported. |
| 5 | Region Code | `EXPORT-REGION-CODE` | Alphanumeric | 5 chars | | Regional classification code for the exporting branch. |
| 6 | Record Data | `EXPORT-RECORD-DATA` | Alphanumeric | 460 chars | | Polymorphic data payload containing the entity-specific attributes. Structure depends on Record Type. |

---

## 16. COMMAREA (Communication Area)

**Business Definition:** The COMMAREA is a transient in-memory data structure used as the inter-program communication mechanism within the CICS online transaction processing environment. It carries session context, current entity selections, and navigation state between programs as users move through the application's menu system and functional screens. Not persisted to any storage medium.

**Source:** `COCOM01Y.cpy` | **Transport:** CICS COMMAREA

| # | Attribute | COBOL Field | Data Type | Size | Business Definition |
|---|-----------|-------------|-----------|------|---------------------|
| 1 | From Transaction ID | `CDEMO-FROM-TRANID` | Alphanumeric | 4 chars | CICS transaction ID of the calling program. |
| 2 | From Program | `CDEMO-FROM-PROGRAM` | Alphanumeric | 8 chars | Name of the calling COBOL program. |
| 3 | To Transaction ID | `CDEMO-TO-TRANID` | Alphanumeric | 4 chars | CICS transaction ID of the target program. |
| 4 | To Program | `CDEMO-TO-PROGRAM` | Alphanumeric | 8 chars | Name of the target COBOL program. |
| 5 | User ID | `CDEMO-USER-ID` | Alphanumeric | 8 chars | Currently logged-in user's ID. |
| 6 | User Type | `CDEMO-USER-TYPE` | Alphanumeric | 1 char | Role of the logged-in user (A=Admin, U=User). |
| 7 | Program Context | `CDEMO-PGM-CONTEXT` | Numeric | 1 digit | State flag: 0=Initial entry, 1=Re-entry (used for screen flow control). |
| 8 | Customer ID | `CDEMO-CUST-ID` | Numeric | 9 digits | Currently selected customer ID. |
| 9 | Customer First Name | `CDEMO-CUST-FNAME` | Alphanumeric | 25 chars | Currently selected customer's first name. |
| 10 | Customer Middle Name | `CDEMO-CUST-MNAME` | Alphanumeric | 25 chars | Currently selected customer's middle name. |
| 11 | Customer Last Name | `CDEMO-CUST-LNAME` | Alphanumeric | 25 chars | Currently selected customer's last name. |
| 12 | Account ID | `CDEMO-ACCT-ID` | Numeric | 11 digits | Currently selected account ID. |
| 13 | Account Status | `CDEMO-ACCT-STATUS` | Alphanumeric | 1 char | Status of the currently selected account. |
| 14 | Card Number | `CDEMO-CARD-NUM` | Numeric | 16 digits | Currently selected card number. |
| 15 | Last Map | `CDEMO-LAST-MAP` | Alphanumeric | 7 chars | Name of the last BMS map displayed. |
| 16 | Last Mapset | `CDEMO-LAST-MAPSET` | Alphanumeric | 7 chars | Name of the last BMS mapset used. |

---

## Glossary of Business Terms

| Term | Definition |
|------|------------|
| **PAN** | Primary Account Number - the card number embossed on a credit card, typically 16 digits. |
| **CVV** | Card Verification Value - a security code used to verify card-not-present transactions. |
| **AVS** | Address Verification Service - fraud prevention that compares billing address with issuer records. |
| **MCC** | Merchant Category Code - a four-digit code classifying a merchant's business type (ISO 18245). |
| **POS** | Point of Sale - the location/terminal where a card transaction is initiated. |
| **EFT** | Electronic Funds Transfer - electronic movement of money between bank accounts. |
| **ACH** | Automated Clearing House - a network for electronic interbank funds transfers. |
| **APR** | Annual Percentage Rate - the yearly interest rate charged on outstanding balances. |
| **FICO** | Fair Isaac Corporation score - a credit score used to assess creditworthiness (300-850). |
| **KSDS** | Key Sequenced Data Set - a VSAM file type with records organized by a primary key. |
| **AIX** | Alternate Index - a secondary key path for VSAM, enabling lookups by non-primary keys. |
| **HIDAM** | Hierarchical Indexed Direct Access Method - an IMS database access method using an index. |
| **COMMAREA** | Communication Area - a CICS memory buffer for passing data between programs. |
| **BMS** | Basic Mapping Support - CICS facility for defining 3270 terminal screen layouts. |
| **PCI DSS** | Payment Card Industry Data Security Standard - security standards for handling cardholder data. |
| **KYC** | Know Your Customer - regulatory process of verifying customer identity. |
| **GDG** | Generation Data Group - a mainframe mechanism for managing versions of sequential files. |
| **ISO 8583** | International standard for financial transaction message interchange. |
