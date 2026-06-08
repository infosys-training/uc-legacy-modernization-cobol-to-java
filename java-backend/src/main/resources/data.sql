-- ============================================================================
-- CardDemo Seed Data — migrated from EBCDIC datasets in app/data/
-- Uses MERGE INTO for idempotent re-execution (H2 upsert).
-- Passwords are BCrypt hashes (original COBOL stored plain text PIC X(08))
-- ============================================================================

-- Users (from USRSEC VSAM file)
MERGE INTO users (user_id, first_name, last_name, password, user_type) KEY (user_id) VALUES
('admin01', 'System', 'Admin', '$2a$10$7w5hoF6MC/4rckCDjb941Ov4XQ.djd7aqqsth.HFDmoeipy.Vt6Ua', 'A'),
('user0001', 'First', 'User', '$2a$10$7w5hoF6MC/4rckCDjb941Ov4XQ.djd7aqqsth.HFDmoeipy.Vt6Ua', 'U'),
('user0002', 'Second', 'User', '$2a$10$7w5hoF6MC/4rckCDjb941Ov4XQ.djd7aqqsth.HFDmoeipy.Vt6Ua', 'U');

-- Accounts (from ACCTFILE VSAM — CVACT01Y layout)
MERGE INTO accounts (acct_id, active_status, curr_bal, credit_limit, cash_credit_limit, open_date, expiration_date, reissue_date, curr_cyc_credit, curr_cyc_debit, addr_zip, group_id) KEY (acct_id) VALUES
(80001000001, 'Y', 1500.00, 5000.00, 1500.00, '2019-03-15', '2026-03-15', '2024-03-15', 200.00, 350.00, '60601', 'GRPACCT01'),
(80002000002, 'Y', 3200.50, 10000.00, 3000.00, '2018-07-20', '2025-07-20', '2023-07-20', 500.00, 750.00, '10001', 'GRPACCT02'),
(80003000003, 'Y', 750.25, 3000.00, 1000.00, '2020-01-10', '2027-01-10', '2025-01-10', 100.00, 200.00, '90210', 'GRPACCT01'),
(80004000004, 'N', 0.00, 2000.00, 500.00, '2017-05-01', '2023-05-01', '2022-05-01', 0.00, 0.00, '30301', 'GRPACCT03'),
(80005000005, 'Y', 8750.00, 15000.00, 5000.00, '2021-11-30', '2028-11-30', '2026-11-30', 1200.00, 1800.00, '94102', 'GRPACCT02');

-- Customers (from CUSTFILE VSAM — CVCUS01Y layout)
MERGE INTO customers (cust_id, first_name, middle_name, last_name, addr_line_1, addr_line_2, addr_line_3, addr_state_cd, addr_country_cd, addr_zip, phone_num_1, phone_num_2, ssn, govt_issued_id, date_of_birth, eft_account_id, pri_card_holder_ind, fico_credit_score) KEY (cust_id) VALUES
(100000001, 'John', 'M', 'Smith', '123 Main St', 'Apt 4B', '', 'IL', 'US', '60601', '(312)555-0101', '', '123456789', 'DL-IL-12345', '1985-06-15', 'EFT000001', 'Y', 750),
(100000002, 'Jane', 'A', 'Doe', '456 Oak Ave', '', '', 'NY', 'US', '10001', '(212)555-0202', '(212)555-0203', '987654321', 'DL-NY-67890', '1990-03-22', 'EFT000002', 'Y', 680),
(100000003, 'Robert', '', 'Williams', '789 Pine Rd', 'Suite 100', '', 'CA', 'US', '90210', '(310)555-0303', '', '456789012', 'PP-US-11111', '1978-12-01', 'EFT000003', 'Y', 720),
(100000004, 'Emily', 'R', 'Johnson', '321 Elm St', '', '', 'GA', 'US', '30301', '(404)555-0404', '', '654321098', 'DL-GA-22222', '1995-09-10', 'EFT000004', 'N', 590),
(100000005, 'Michael', 'J', 'Brown', '555 Market St', 'Floor 3', '', 'CA', 'US', '94102', '(415)555-0505', '(415)555-0506', '789012345', 'DL-CA-33333', '1982-01-28', 'EFT000005', 'Y', 800);

-- Cards (from CARDFILE VSAM — CVACT02Y layout)
MERGE INTO cards (card_num, acct_id, cvv_code, embossed_name, expiration_date, active_status) KEY (card_num) VALUES
('4111111111111111', 80001000001, '123', 'JOHN M SMITH', '2026-03-15', 'Y'),
('4222222222222222', 80002000002, '456', 'JANE A DOE', '2025-07-20', 'Y'),
('4333333333333333', 80003000003, '789', 'ROBERT WILLIAMS', '2027-01-10', 'Y'),
('4444444444444444', 80004000004, '012', 'EMILY R JOHNSON', '2023-05-01', 'N'),
('4555555555555555', 80005000005, '345', 'MICHAEL J BROWN', '2028-11-30', 'Y'),
('4111111111112222', 80001000001, '678', 'JOHN M SMITH', '2026-03-15', 'Y');

-- Card Cross-References (from CARDXREF VSAM — CVACT03Y layout)
MERGE INTO card_xref (card_num, cust_id, acct_id) KEY (card_num) VALUES
('4111111111111111', 100000001, 80001000001),
('4222222222222222', 100000002, 80002000002),
('4333333333333333', 100000003, 80003000003),
('4444444444444444', 100000004, 80004000004),
('4555555555555555', 100000005, 80005000005),
('4111111111112222', 100000001, 80001000001);

-- Transaction Types (from TRANTYPE VSAM — CVTRA03Y layout)
MERGE INTO transaction_types (type_cd, type_desc) KEY (type_cd) VALUES
('SA', 'Sale'),
('RT', 'Return'),
('CA', 'Cash Advance'),
('PM', 'Payment'),
('FE', 'Fee'),
('IN', 'Interest');

-- Transaction Categories (from TRANCATG VSAM — CVTRA04Y layout)
MERGE INTO transaction_categories (type_cd, cat_cd, cat_desc) KEY (type_cd, cat_cd) VALUES
('SA', 5001, 'Retail Purchase'),
('SA', 5002, 'Online Purchase'),
('SA', 5003, 'Recurring Payment'),
('RT', 6001, 'Merchandise Return'),
('CA', 7001, 'ATM Cash Advance'),
('PM', 8001, 'Monthly Payment'),
('PM', 8002, 'Additional Payment'),
('FE', 9001, 'Annual Fee'),
('FE', 9002, 'Late Payment Fee'),
('IN', 9501, 'Monthly Interest');

-- Disclosure Groups (from DISCGRP VSAM — CVTRA02Y layout)
MERGE INTO disclosure_groups (acct_group_id, tran_type_cd, tran_cat_cd, interest_rate) KEY (acct_group_id, tran_type_cd, tran_cat_cd) VALUES
('GRPACCT01', 'SA', 5001, 19.99),
('GRPACCT01', 'CA', 7001, 24.99),
('GRPACCT01', 'SA', 5002, 19.99),
('GRPACCT02', 'SA', 5001, 17.49),
('GRPACCT02', 'CA', 7001, 22.99),
('GRPACCT03', 'SA', 5001, 21.99),
('GRPACCT03', 'CA', 7001, 26.99);

-- Transaction Category Balances (from TCATBAL VSAM — CVTRA01Y layout)
MERGE INTO tran_cat_balances (acct_id, type_cd, cat_cd, balance) KEY (acct_id, type_cd, cat_cd) VALUES
(80001000001, 'SA', 5001, 950.00),
(80001000001, 'SA', 5002, 350.00),
(80001000001, 'PM', 8001, -200.00),
(80002000002, 'SA', 5001, 2100.50),
(80002000002, 'CA', 7001, 600.00),
(80002000002, 'PM', 8001, -500.00),
(80003000003, 'SA', 5001, 550.25),
(80003000003, 'PM', 8001, -100.00);

-- Transactions (from TRANSACT VSAM — CVTRA05Y layout)
MERGE INTO transactions (tran_id, type_cd, cat_cd, source, description, amount, merchant_id, merchant_name, merchant_city, merchant_zip, card_num, orig_timestamp, proc_timestamp) KEY (tran_id) VALUES
('0000000000000001', 'SA', 5001, 'POS', 'Grocery purchase at MegaMart', 125.50, 100000001, 'MegaMart', 'Chicago', '60601', '4111111111111111', '2024-01-15 10:30:00', '2024-01-15 10:30:05'),
('0000000000000002', 'SA', 5002, 'ONLINE', 'Electronics at TechStore.com', 499.99, 100000002, 'TechStore', 'New York', '10001', '4222222222222222', '2024-01-16 14:22:00', '2024-01-16 14:22:03'),
('0000000000000003', 'PM', 8001, 'ACH', 'Monthly payment', -200.00, NULL, NULL, NULL, NULL, '4111111111111111', '2024-01-20 08:00:00', '2024-01-20 08:00:01'),
('0000000000000004', 'CA', 7001, 'ATM', 'ATM Cash advance', 300.00, 100000003, 'First National ATM', 'Los Angeles', '90210', '4333333333333333', '2024-01-18 16:45:00', '2024-01-18 16:45:02'),
('0000000000000005', 'SA', 5001, 'POS', 'Restaurant dinner', 85.75, 100000004, 'Fine Dining LLC', 'San Francisco', '94102', '4555555555555555', '2024-01-22 19:30:00', '2024-01-22 19:30:04'),
('0000000000000006', 'RT', 6001, 'POS', 'Return - damaged item', -45.00, 100000002, 'TechStore', 'New York', '10001', '4222222222222222', '2024-01-25 11:15:00', '2024-01-25 11:15:02'),
('0000000000000007', 'FE', 9001, 'SYSTEM', 'Annual membership fee', 95.00, NULL, NULL, NULL, NULL, '4555555555555555', '2024-02-01 00:00:00', '2024-02-01 00:00:01'),
('0000000000000008', 'SA', 5003, 'ONLINE', 'Monthly subscription - StreamIt', 14.99, 100000005, 'StreamIt Inc', 'Austin', '73301', '4111111111112222', '2024-02-01 06:00:00', '2024-02-01 06:00:01');

-- Daily Transactions (pending batch processing — DALYTRAN-FILE equivalent)
MERGE INTO daily_transactions (tran_id, card_num, type_cd, cat_cd, source, description, amount, merchant_id, merchant_name, merchant_city, merchant_zip, orig_ts, processed) KEY (tran_id) VALUES
('DLY0000000000001', '4111111111111111', 'SA', 5001, 'POS', 'Hardware Store Purchase', 89.50, '100000010', 'HardwareHub', 'Chicago', '60601', '2024-03-01 14:30:00', FALSE),
('DLY0000000000002', '4222222222222222', 'SA', 5002, 'ONLINE', 'Book purchase online', 32.99, '100000011', 'BookWorld', 'New York', '10001', '2024-03-01 16:45:00', FALSE),
('DLY0000000000003', '4333333333333333', 'CA', 7001, 'ATM', 'ATM Cash Withdrawal', 200.00, '100000012', 'CityBank ATM', 'Los Angeles', '90210', '2024-03-02 09:15:00', FALSE),
('DLY0000000000004', '9999999999999999', 'SA', 5001, 'POS', 'Invalid card test', 50.00, '100000013', 'TestMerchant', 'Nowhere', '00000', '2024-03-02 10:00:00', FALSE),
('DLY0000000000005', '4555555555555555', 'SA', 5001, 'POS', 'Coffee shop', 6.50, '100000014', 'JavaBeans', 'San Francisco', '94102', '2024-03-02 07:30:00', FALSE);
