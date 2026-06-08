-- ============================================================================
-- CardDemo Schema — Java/Spring Boot migration from COBOL/VSAM
-- Maps VSAM KSDS files and DB2 tables to relational DDL.
-- ============================================================================

-- ACCTFILE (CVACT01Y.cpy) — 300-byte VSAM KSDS record
CREATE TABLE IF NOT EXISTS accounts (
    acct_id           BIGINT PRIMARY KEY,
    active_status     VARCHAR(1) NOT NULL DEFAULT 'Y',
    curr_bal          DECIMAL(12,2) NOT NULL DEFAULT 0,
    credit_limit      DECIMAL(12,2) NOT NULL DEFAULT 0,
    cash_credit_limit DECIMAL(12,2) NOT NULL DEFAULT 0,
    open_date         DATE,
    expiration_date   DATE,
    reissue_date      DATE,
    curr_cyc_credit   DECIMAL(12,2) NOT NULL DEFAULT 0,
    curr_cyc_debit    DECIMAL(12,2) NOT NULL DEFAULT 0,
    addr_zip          VARCHAR(10),
    group_id          VARCHAR(10)
);

-- CUSTFILE (CVCUS01Y.cpy) — 500-byte VSAM KSDS record
CREATE TABLE IF NOT EXISTS customers (
    cust_id               BIGINT PRIMARY KEY,
    first_name            VARCHAR(25) NOT NULL,
    middle_name           VARCHAR(25),
    last_name             VARCHAR(25) NOT NULL,
    addr_line_1           VARCHAR(50),
    addr_line_2           VARCHAR(50),
    addr_line_3           VARCHAR(50),
    addr_state_cd         VARCHAR(2),
    addr_country_cd       VARCHAR(3),
    addr_zip              VARCHAR(10),
    phone_num_1           VARCHAR(15),
    phone_num_2           VARCHAR(15),
    ssn                   VARCHAR(9),
    govt_issued_id        VARCHAR(20),
    date_of_birth         DATE,
    eft_account_id        VARCHAR(10),
    pri_card_holder_ind   VARCHAR(1),
    fico_credit_score     INT
);

-- CARDFILE (CVACT02Y.cpy) — 150-byte VSAM KSDS record
CREATE TABLE IF NOT EXISTS cards (
    card_num        VARCHAR(16) PRIMARY KEY,
    acct_id         BIGINT NOT NULL,
    cvv_code        VARCHAR(3),
    embossed_name   VARCHAR(50),
    expiration_date DATE,
    active_status   VARCHAR(1) NOT NULL DEFAULT 'Y'
);

-- CARDXREF (CVACT03Y.cpy) — 50-byte VSAM KSDS record
CREATE TABLE IF NOT EXISTS card_xref (
    card_num  VARCHAR(16) PRIMARY KEY,
    cust_id   BIGINT NOT NULL,
    acct_id   BIGINT NOT NULL
);

-- TRANSACT (CVTRA05Y.cpy) — 350-byte VSAM KSDS record
CREATE TABLE IF NOT EXISTS transactions (
    tran_id        VARCHAR(16) PRIMARY KEY,
    type_cd        VARCHAR(2) NOT NULL,
    cat_cd         INT,
    source         VARCHAR(10),
    description    VARCHAR(100),
    amount         DECIMAL(11,2) NOT NULL,
    merchant_id    BIGINT,
    merchant_name  VARCHAR(50),
    merchant_city  VARCHAR(50),
    merchant_zip   VARCHAR(10),
    card_num       VARCHAR(16) NOT NULL,
    orig_timestamp TIMESTAMP,
    proc_timestamp TIMESTAMP
);

-- TRANTYPE (CVTRA03Y.cpy) — 60-byte record
CREATE TABLE IF NOT EXISTS transaction_types (
    type_cd   VARCHAR(2) PRIMARY KEY,
    type_desc VARCHAR(50)
);

-- TRANCATG (CVTRA04Y.cpy) — 60-byte record
CREATE TABLE IF NOT EXISTS transaction_categories (
    type_cd  VARCHAR(2) NOT NULL,
    cat_cd   INT NOT NULL,
    cat_desc VARCHAR(50),
    PRIMARY KEY (type_cd, cat_cd)
);

-- DISCGRP (CVTRA02Y.cpy) — 50-byte record
CREATE TABLE IF NOT EXISTS disclosure_groups (
    acct_group_id  VARCHAR(10) NOT NULL,
    tran_type_cd   VARCHAR(2) NOT NULL,
    tran_cat_cd    INT NOT NULL,
    interest_rate  DECIMAL(6,2) NOT NULL,
    PRIMARY KEY (acct_group_id, tran_type_cd, tran_cat_cd)
);

-- TCATBAL (CVTRA01Y.cpy) — 50-byte record
CREATE TABLE IF NOT EXISTS tran_cat_balances (
    acct_id  BIGINT NOT NULL,
    type_cd  VARCHAR(2) NOT NULL,
    cat_cd   INT NOT NULL,
    balance  DECIMAL(11,2) NOT NULL DEFAULT 0,
    PRIMARY KEY (acct_id, type_cd, cat_cd)
);

-- USRSEC (CSUSR01Y.cpy) — 80-byte VSAM KSDS record
CREATE TABLE IF NOT EXISTS users (
    user_id    VARCHAR(8) PRIMARY KEY,
    first_name VARCHAR(20),
    last_name  VARCHAR(20),
    password   VARCHAR(72) NOT NULL,
    user_type  VARCHAR(1) NOT NULL
);

-- DALYTRAN (CVTRA06Y.cpy) — Daily transaction input for batch posting (CBTRN02C)
CREATE TABLE IF NOT EXISTS daily_transactions (
    tran_id        VARCHAR(16) PRIMARY KEY,
    card_num       VARCHAR(16) NOT NULL,
    type_cd        VARCHAR(2) NOT NULL,
    cat_cd         INT,
    source         VARCHAR(10),
    description    VARCHAR(100),
    amount         DECIMAL(11,2) NOT NULL,
    merchant_id    VARCHAR(9),
    merchant_name  VARCHAR(50),
    merchant_city  VARCHAR(30),
    merchant_zip   VARCHAR(10),
    orig_ts        TIMESTAMP,
    processed      BOOLEAN NOT NULL DEFAULT FALSE
);

-- DALYREJS — Rejected transactions log (CBTRN02C validation failures)
CREATE TABLE IF NOT EXISTS transaction_rejects (
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    tran_id           VARCHAR(16) NOT NULL,
    fail_reason_code  INT,
    fail_reason_desc  VARCHAR(200),
    rejected_at       TIMESTAMP NOT NULL
);
