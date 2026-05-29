package com.carddemo.testharness.edgecase;

import com.carddemo.parser.CobolRecordLayout;
import com.carddemo.parser.CobolRecordParser;
import com.carddemo.parser.RecordLayouts;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class EdgeCaseGoldenFileTest {

    // ---- helpers ----

    private Path resourcePath(String relativePath) {
        URL url = getClass().getClassLoader().getResource(relativePath);
        assertThat(url).as("Resource not found: %s", relativePath).isNotNull();
        return Paths.get(url.getPath());
    }

    private List<Map<String, Object>> parse(CobolRecordLayout layout, String resourcePath) throws Exception {
        return new CobolRecordParser(layout).parseFile(resourcePath(resourcePath));
    }

    // =====================================================================
    //  EMPTY INPUT FILES — expect 0 records
    // =====================================================================

    @Test
    void emptyAcctdata_shouldReturnZeroRecords() throws Exception {
        assertThat(parse(RecordLayouts.accountRecord(), "edge-cases/empty/acctdata.txt")).isEmpty();
    }

    @Test
    void emptyCarddata_shouldReturnZeroRecords() throws Exception {
        assertThat(parse(RecordLayouts.cardRecord(), "edge-cases/empty/carddata.txt")).isEmpty();
    }

    @Test
    void emptyCardxref_shouldReturnZeroRecords() throws Exception {
        assertThat(parse(RecordLayouts.cardXrefRecord(), "edge-cases/empty/cardxref.txt")).isEmpty();
    }

    @Test
    void emptyCustdata_shouldReturnZeroRecords() throws Exception {
        assertThat(parse(RecordLayouts.customerRecord(), "edge-cases/empty/custdata.txt")).isEmpty();
    }

    @Test
    void emptyDailytran_shouldReturnZeroRecords() throws Exception {
        assertThat(parse(RecordLayouts.dailyTransactionRecord(), "edge-cases/empty/dailytran.txt")).isEmpty();
    }

    @Test
    void emptyDiscgrp_shouldReturnZeroRecords() throws Exception {
        assertThat(parse(RecordLayouts.disclosureGroupRecord(), "edge-cases/empty/discgrp.txt")).isEmpty();
    }

    @Test
    void emptyTcatbal_shouldReturnZeroRecords() throws Exception {
        assertThat(parse(RecordLayouts.tranCatBalRecord(), "edge-cases/empty/tcatbal.txt")).isEmpty();
    }

    @Test
    void emptyTrancatg_shouldReturnZeroRecords() throws Exception {
        assertThat(parse(RecordLayouts.tranCatRecord(), "edge-cases/empty/trancatg.txt")).isEmpty();
    }

    @Test
    void emptyTrantype_shouldReturnZeroRecords() throws Exception {
        assertThat(parse(RecordLayouts.tranTypeRecord(), "edge-cases/empty/trantype.txt")).isEmpty();
    }

    // =====================================================================
    //  SINGLE RECORD FILES — expect exactly 1 record with known values
    // =====================================================================

    @Test
    void singleAcctdata_shouldReturnOneRecord() throws Exception {
        List<Map<String, Object>> records =
                parse(RecordLayouts.accountRecord(), "edge-cases/single-record/acctdata.txt");
        assertThat(records).hasSize(1);

        Map<String, Object> r = records.get(0);
        assertThat(r.get("ACCT_ID")).isEqualTo("00000000001");
        assertThat(r.get("ACCT_ACTIVE_STATUS")).isEqualTo("Y");
        assertThat(r.get("ACCT_CURR_BAL")).isEqualTo(new BigDecimal("0.00"));
        assertThat(r.get("ACCT_CREDIT_LIMIT")).isEqualTo(new BigDecimal("0.00"));
        assertThat(r.get("ACCT_CASH_CREDIT_LIMIT")).isEqualTo(new BigDecimal("0.00"));
        assertThat(r.get("ACCT_OPEN_DATE")).isEqualTo("2023-01-01");
        assertThat(r.get("ACCT_EXPIRAION_DATE")).isEqualTo("2025-12-31");
        assertThat(r.get("ACCT_REISSUE_DATE")).isEqualTo("2024-06-15");
        assertThat(r.get("ACCT_CURR_CYC_CREDIT")).isEqualTo(new BigDecimal("0.00"));
        assertThat(r.get("ACCT_CURR_CYC_DEBIT")).isEqualTo(new BigDecimal("0.00"));
        assertThat(r.get("ACCT_ADDR_ZIP")).isEqualTo("10001");
        assertThat(r.get("ACCT_GROUP_ID")).isEqualTo("GRP001");
    }

    @Test
    void singleCarddata_shouldReturnOneRecord() throws Exception {
        List<Map<String, Object>> records =
                parse(RecordLayouts.cardRecord(), "edge-cases/single-record/carddata.txt");
        assertThat(records).hasSize(1);

        Map<String, Object> r = records.get(0);
        assertThat(r.get("CARD_NUM")).isEqualTo("4111111111111111");
        assertThat(r.get("CARD_ACCT_ID")).isEqualTo("00000000001");
        assertThat(r.get("CARD_CVV_CD")).isEqualTo("123");
        assertThat(r.get("CARD_EMBOSSED_NAME")).isEqualTo("John Doe");
        assertThat(r.get("CARD_EXPIRAION_DATE")).isEqualTo("2025-12-31");
        assertThat(r.get("CARD_ACTIVE_STATUS")).isEqualTo("Y");
    }

    @Test
    void singleCardxref_shouldReturnOneRecord() throws Exception {
        List<Map<String, Object>> records =
                parse(RecordLayouts.cardXrefRecord(), "edge-cases/single-record/cardxref.txt");
        assertThat(records).hasSize(1);

        Map<String, Object> r = records.get(0);
        assertThat(r.get("XREF_CARD_NUM")).isEqualTo("4111111111111111");
        assertThat(r.get("XREF_CUST_ID")).isEqualTo("000000001");
        assertThat(r.get("XREF_ACCT_ID")).isEqualTo("00000000001");
    }

    @Test
    void singleCustdata_shouldReturnOneRecord() throws Exception {
        List<Map<String, Object>> records =
                parse(RecordLayouts.customerRecord(), "edge-cases/single-record/custdata.txt");
        assertThat(records).hasSize(1);

        Map<String, Object> r = records.get(0);
        assertThat(r.get("CUST_ID")).isEqualTo("000000001");
        assertThat(r.get("CUST_FIRST_NAME")).isEqualTo("John");
        assertThat(r.get("CUST_MIDDLE_NAME")).isEqualTo("Q");
        assertThat(r.get("CUST_LAST_NAME")).isEqualTo("Doe");
        assertThat(r.get("CUST_ADDR_LINE_1")).isEqualTo("123 Main St");
        assertThat(r.get("CUST_ADDR_STATE_CD")).isEqualTo("NY");
        assertThat(r.get("CUST_ADDR_COUNTRY_CD")).isEqualTo("USA");
        assertThat(r.get("CUST_ADDR_ZIP")).isEqualTo("10001");
        assertThat(r.get("CUST_PHONE_NUM_1")).isEqualTo("555-123-4567");
        assertThat(r.get("CUST_SSN")).isEqualTo("123456789");
        assertThat(r.get("CUST_GOVT_ISSUED_ID")).isEqualTo("DL12345678");
        assertThat(r.get("CUST_DOB_YYYY_MM_DD")).isEqualTo("1990-01-15");
        assertThat(r.get("CUST_EFT_ACCOUNT_ID")).isEqualTo("EFT0000001");
        assertThat(r.get("CUST_PRI_CARD_HOLDER_IND")).isEqualTo("Y");
        assertThat(r.get("CUST_FICO_CREDIT_SCORE")).isEqualTo("750");
    }

    @Test
    void singleDailytran_shouldReturnOneRecord() throws Exception {
        List<Map<String, Object>> records =
                parse(RecordLayouts.dailyTransactionRecord(), "edge-cases/single-record/dailytran.txt");
        assertThat(records).hasSize(1);

        Map<String, Object> r = records.get(0);
        assertThat(r.get("DALYTRAN_ID")).isEqualTo("TXN0000000000001");
        assertThat(r.get("DALYTRAN_TYPE_CD")).isEqualTo("SA");
        assertThat(r.get("DALYTRAN_CAT_CD")).isEqualTo("5001");
        assertThat(r.get("DALYTRAN_SOURCE")).isEqualTo("ONLINE");
        assertThat(r.get("DALYTRAN_DESC")).isEqualTo("Test transaction");
        assertThat(r.get("DALYTRAN_AMT")).isEqualTo(new BigDecimal("0.00"));
        assertThat(r.get("DALYTRAN_MERCHANT_ID")).isEqualTo("000000001");
        assertThat(r.get("DALYTRAN_MERCHANT_NAME")).isEqualTo("Test Merchant");
        assertThat(r.get("DALYTRAN_MERCHANT_CITY")).isEqualTo("New York");
        assertThat(r.get("DALYTRAN_MERCHANT_ZIP")).isEqualTo("10001");
        assertThat(r.get("DALYTRAN_CARD_NUM")).isEqualTo("4111111111111111");
        assertThat(r.get("DALYTRAN_ORIG_TS")).isEqualTo("2023-06-15-10.30.00.000000");
        assertThat(r.get("DALYTRAN_PROC_TS")).isEqualTo("2023-06-15-10.30.01.000000");
    }

    @Test
    void singleDiscgrp_shouldReturnOneRecord() throws Exception {
        List<Map<String, Object>> records =
                parse(RecordLayouts.disclosureGroupRecord(), "edge-cases/single-record/discgrp.txt");
        assertThat(records).hasSize(1);

        Map<String, Object> r = records.get(0);
        assertThat(r.get("DIS_ACCT_GROUP_ID")).isEqualTo("GRP001");
        assertThat(r.get("DIS_TRAN_TYPE_CD")).isEqualTo("SA");
        assertThat(r.get("DIS_TRAN_CAT_CD")).isEqualTo("5001");
        assertThat(r.get("DIS_INT_RATE")).isEqualTo(new BigDecimal("0.00"));
    }

    @Test
    void singleTcatbal_shouldReturnOneRecord() throws Exception {
        List<Map<String, Object>> records =
                parse(RecordLayouts.tranCatBalRecord(), "edge-cases/single-record/tcatbal.txt");
        assertThat(records).hasSize(1);

        Map<String, Object> r = records.get(0);
        assertThat(r.get("TRANCAT_ACCT_ID")).isEqualTo("00000000001");
        assertThat(r.get("TRANCAT_TYPE_CD")).isEqualTo("SA");
        assertThat(r.get("TRANCAT_CD")).isEqualTo("5001");
        assertThat(r.get("TRAN_CAT_BAL")).isEqualTo(new BigDecimal("0.00"));
    }

    @Test
    void singleTrancatg_shouldReturnOneRecord() throws Exception {
        List<Map<String, Object>> records =
                parse(RecordLayouts.tranCatRecord(), "edge-cases/single-record/trancatg.txt");
        assertThat(records).hasSize(1);

        Map<String, Object> r = records.get(0);
        assertThat(r.get("TRAN_TYPE_CD")).isEqualTo("SA");
        assertThat(r.get("TRAN_CAT_CD")).isEqualTo("5001");
        assertThat(r.get("TRAN_CAT_TYPE_DESC")).isEqualTo("Sales");
    }

    @Test
    void singleTrantype_shouldReturnOneRecord() throws Exception {
        List<Map<String, Object>> records =
                parse(RecordLayouts.tranTypeRecord(), "edge-cases/single-record/trantype.txt");
        assertThat(records).hasSize(1);

        Map<String, Object> r = records.get(0);
        assertThat(r.get("TRAN_TYPE")).isEqualTo("SA");
        assertThat(r.get("TRAN_TYPE_DESC")).isEqualTo("Sales");
    }

    // =====================================================================
    //  MAX VALUE FILES — expect records with maximum BigDecimal values
    // =====================================================================

    @Test
    void maxAcctdata_shouldReturnMaxValues() throws Exception {
        List<Map<String, Object>> records =
                parse(RecordLayouts.accountRecord(), "edge-cases/max-values/acctdata.txt");
        assertThat(records).hasSize(1);

        Map<String, Object> r = records.get(0);
        assertThat(r.get("ACCT_ID")).isEqualTo("99999999999");
        assertThat(r.get("ACCT_ACTIVE_STATUS")).isEqualTo("Z");
        assertThat(r.get("ACCT_CURR_BAL")).isEqualTo(new BigDecimal("9999999999.99"));
        assertThat(r.get("ACCT_CREDIT_LIMIT")).isEqualTo(new BigDecimal("9999999999.99"));
        assertThat(r.get("ACCT_CASH_CREDIT_LIMIT")).isEqualTo(new BigDecimal("9999999999.99"));
        assertThat(r.get("ACCT_OPEN_DATE")).isEqualTo("ZZZZZZZZZZ");
        assertThat(r.get("ACCT_EXPIRAION_DATE")).isEqualTo("ZZZZZZZZZZ");
        assertThat(r.get("ACCT_REISSUE_DATE")).isEqualTo("ZZZZZZZZZZ");
        assertThat(r.get("ACCT_CURR_CYC_CREDIT")).isEqualTo(new BigDecimal("9999999999.99"));
        assertThat(r.get("ACCT_CURR_CYC_DEBIT")).isEqualTo(new BigDecimal("9999999999.99"));
        assertThat(r.get("ACCT_ADDR_ZIP")).isEqualTo("ZZZZZZZZZZ");
        assertThat(r.get("ACCT_GROUP_ID")).isEqualTo("ZZZZZZZZZZ");
    }

    @Test
    void maxCarddata_shouldReturnMaxValues() throws Exception {
        List<Map<String, Object>> records =
                parse(RecordLayouts.cardRecord(), "edge-cases/max-values/carddata.txt");
        assertThat(records).hasSize(1);

        Map<String, Object> r = records.get(0);
        assertThat(r.get("CARD_NUM")).isEqualTo("ZZZZZZZZZZZZZZZZ");
        assertThat(r.get("CARD_ACCT_ID")).isEqualTo("99999999999");
        assertThat(r.get("CARD_CVV_CD")).isEqualTo("999");
        assertThat(r.get("CARD_EMBOSSED_NAME")).isEqualTo("ZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZ");
        assertThat(r.get("CARD_EXPIRAION_DATE")).isEqualTo("ZZZZZZZZZZ");
        assertThat(r.get("CARD_ACTIVE_STATUS")).isEqualTo("Z");
    }

    @Test
    void maxCardxref_shouldReturnMaxValues() throws Exception {
        List<Map<String, Object>> records =
                parse(RecordLayouts.cardXrefRecord(), "edge-cases/max-values/cardxref.txt");
        assertThat(records).hasSize(1);

        Map<String, Object> r = records.get(0);
        assertThat(r.get("XREF_CARD_NUM")).isEqualTo("ZZZZZZZZZZZZZZZZ");
        assertThat(r.get("XREF_CUST_ID")).isEqualTo("999999999");
        assertThat(r.get("XREF_ACCT_ID")).isEqualTo("99999999999");
    }

    @Test
    void maxCustdata_shouldReturnMaxValues() throws Exception {
        List<Map<String, Object>> records =
                parse(RecordLayouts.customerRecord(), "edge-cases/max-values/custdata.txt");
        assertThat(records).hasSize(1);

        Map<String, Object> r = records.get(0);
        assertThat(r.get("CUST_ID")).isEqualTo("999999999");
        assertThat(r.get("CUST_FIRST_NAME")).isEqualTo("ZZZZZZZZZZZZZZZZZZZZZZZZZ");
        assertThat(r.get("CUST_LAST_NAME")).isEqualTo("ZZZZZZZZZZZZZZZZZZZZZZZZZ");
        assertThat(r.get("CUST_SSN")).isEqualTo("999999999");
        assertThat(r.get("CUST_FICO_CREDIT_SCORE")).isEqualTo("999");
    }

    @Test
    void maxDailytran_shouldReturnMaxValues() throws Exception {
        List<Map<String, Object>> records =
                parse(RecordLayouts.dailyTransactionRecord(), "edge-cases/max-values/dailytran.txt");
        assertThat(records).hasSize(1);

        Map<String, Object> r = records.get(0);
        assertThat(r.get("DALYTRAN_ID")).isEqualTo("ZZZZZZZZZZZZZZZZ");
        assertThat(r.get("DALYTRAN_CAT_CD")).isEqualTo("9999");
        assertThat(r.get("DALYTRAN_AMT")).isEqualTo(new BigDecimal("999999999.99"));
        assertThat(r.get("DALYTRAN_MERCHANT_ID")).isEqualTo("999999999");
    }

    @Test
    void maxDiscgrp_shouldReturnMaxValues() throws Exception {
        List<Map<String, Object>> records =
                parse(RecordLayouts.disclosureGroupRecord(), "edge-cases/max-values/discgrp.txt");
        assertThat(records).hasSize(1);

        Map<String, Object> r = records.get(0);
        assertThat(r.get("DIS_ACCT_GROUP_ID")).isEqualTo("ZZZZZZZZZZ");
        assertThat(r.get("DIS_TRAN_CAT_CD")).isEqualTo("9999");
        assertThat(r.get("DIS_INT_RATE")).isEqualTo(new BigDecimal("9999.99"));
    }

    @Test
    void maxTcatbal_shouldReturnMaxValues() throws Exception {
        List<Map<String, Object>> records =
                parse(RecordLayouts.tranCatBalRecord(), "edge-cases/max-values/tcatbal.txt");
        assertThat(records).hasSize(1);

        Map<String, Object> r = records.get(0);
        assertThat(r.get("TRANCAT_ACCT_ID")).isEqualTo("99999999999");
        assertThat(r.get("TRANCAT_CD")).isEqualTo("9999");
        assertThat(r.get("TRAN_CAT_BAL")).isEqualTo(new BigDecimal("999999999.99"));
    }

    @Test
    void maxTrancatg_shouldReturnMaxValues() throws Exception {
        List<Map<String, Object>> records =
                parse(RecordLayouts.tranCatRecord(), "edge-cases/max-values/trancatg.txt");
        assertThat(records).hasSize(1);

        Map<String, Object> r = records.get(0);
        assertThat(r.get("TRAN_CAT_CD")).isEqualTo("9999");
        assertThat(r.get("TRAN_CAT_TYPE_DESC")).isEqualTo("ZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZ");
    }

    @Test
    void maxTrantype_shouldReturnMaxValues() throws Exception {
        List<Map<String, Object>> records =
                parse(RecordLayouts.tranTypeRecord(), "edge-cases/max-values/trantype.txt");
        assertThat(records).hasSize(1);

        Map<String, Object> r = records.get(0);
        assertThat(r.get("TRAN_TYPE")).isEqualTo("ZZ");
        assertThat(r.get("TRAN_TYPE_DESC")).isEqualTo("ZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZ");
    }
}
