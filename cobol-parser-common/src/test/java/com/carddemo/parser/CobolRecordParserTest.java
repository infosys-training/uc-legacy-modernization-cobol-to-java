package com.carddemo.parser;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class CobolRecordParserTest {

    @Test
    void parseAccountRecord() {
        CobolRecordLayout layout = RecordLayouts.accountRecord();
        CobolRecordParser parser = new CobolRecordParser(layout);

        String line = "00000000001Y00000001940{00000020200{00000010200{2014-11-202025-05-202025-05-2000000000000{00000000000{A000000000"
                + "          "
                + " ".repeat(178);
        Map<String, Object> record = parser.parse(line);

        assertThat(record.get("ACCT_ID")).isEqualTo("00000000001");
        assertThat(record.get("ACCT_ACTIVE_STATUS")).isEqualTo("Y");
        assertThat((BigDecimal) record.get("ACCT_CURR_BAL")).isEqualByComparingTo("194.00");
        assertThat((BigDecimal) record.get("ACCT_CREDIT_LIMIT")).isEqualByComparingTo("2020.00");
        assertThat((BigDecimal) record.get("ACCT_CASH_CREDIT_LIMIT")).isEqualByComparingTo("1020.00");
        assertThat(record.get("ACCT_OPEN_DATE")).isEqualTo("2014-11-20");
        assertThat(record.get("ACCT_EXPIRAION_DATE")).isEqualTo("2025-05-20");
        assertThat(record.get("ACCT_REISSUE_DATE")).isEqualTo("2025-05-20");
        assertThat((BigDecimal) record.get("ACCT_CURR_CYC_CREDIT")).isEqualByComparingTo("0.00");
        assertThat((BigDecimal) record.get("ACCT_CURR_CYC_DEBIT")).isEqualByComparingTo("0.00");
        assertThat(record.get("ACCT_ADDR_ZIP")).isEqualTo("A000000000");
        assertThat(record).doesNotContainKey("FILLER");
    }

    @Test
    void parseCardRecord() {
        CobolRecordLayout layout = RecordLayouts.cardRecord();
        CobolRecordParser parser = new CobolRecordParser(layout);

        String line = "050002445376574000000000050747Aniya Von"
                + " ".repeat(41)
                + "2023-03-09Y" + " ".repeat(59);
        Map<String, Object> record = parser.parse(line);

        assertThat(record.get("CARD_NUM")).isEqualTo("0500024453765740");
        assertThat(record.get("CARD_ACCT_ID")).isEqualTo("00000000050");
        assertThat(record.get("CARD_CVV_CD")).isEqualTo("747");
        assertThat(record.get("CARD_EMBOSSED_NAME")).isEqualTo("Aniya Von");
        assertThat(record.get("CARD_EXPIRAION_DATE")).isEqualTo("2023-03-09");
        assertThat(record.get("CARD_ACTIVE_STATUS")).isEqualTo("Y");
        assertThat(record).doesNotContainKey("FILLER");
    }

    @Test
    void parseCardXrefRecordShortLine() {
        CobolRecordLayout layout = RecordLayouts.cardXrefRecord();
        CobolRecordParser parser = new CobolRecordParser(layout);

        String line = "050002445376574000000005000000000050";
        Map<String, Object> record = parser.parse(line);

        assertThat(record.get("XREF_CARD_NUM")).isEqualTo("0500024453765740");
        assertThat(record.get("XREF_CUST_ID")).isEqualTo("000000050");
        assertThat(record.get("XREF_ACCT_ID")).isEqualTo("00000000050");
        assertThat(record).doesNotContainKey("FILLER");
    }

    @Test
    void parseDailyTransactionWithSignedAmount() {
        CobolRecordLayout layout = RecordLayouts.dailyTransactionRecord();
        CobolRecordParser parser = new CobolRecordParser(layout);

        StringBuilder sb = new StringBuilder();
        sb.append("0000000000683580"); // DALYTRAN-ID (16)
        sb.append("01");               // DALYTRAN-TYPE-CD (2)
        sb.append("0001");             // DALYTRAN-CAT-CD (4)
        sb.append("POS TERM  ");       // DALYTRAN-SOURCE (10)
        String desc = "Purchase at Abshire-Lowe";
        sb.append(desc);
        sb.append(" ".repeat(100 - desc.length())); // DALYTRAN-DESC (100)
        sb.append("0000005047G");       // DALYTRAN-AMT S9(9)V99 (11)
        sb.append("800000000");         // DALYTRAN-MERCHANT-ID (9)
        String mname = "Abshire-Lowe";
        sb.append(mname);
        sb.append(" ".repeat(50 - mname.length())); // DALYTRAN-MERCHANT-NAME (50)
        String mcity = "North Enoshaven";
        sb.append(mcity);
        sb.append(" ".repeat(50 - mcity.length())); // DALYTRAN-MERCHANT-CITY (50)
        sb.append("72112     ");        // DALYTRAN-MERCHANT-ZIP (10)
        sb.append("4859452612877065");  // DALYTRAN-CARD-NUM (16)
        sb.append("2022-06-10 19:27:53.000000"); // DALYTRAN-ORIG-TS (26)
        sb.append(" ".repeat(26));      // DALYTRAN-PROC-TS (26)
        sb.append(" ".repeat(20));      // FILLER (20)

        Map<String, Object> record = parser.parse(sb.toString());

        assertThat(record.get("DALYTRAN_ID")).isEqualTo("0000000000683580");
        assertThat((BigDecimal) record.get("DALYTRAN_AMT")).isEqualByComparingTo("504.77");
        assertThat(record.get("DALYTRAN_MERCHANT_NAME")).isEqualTo("Abshire-Lowe");
    }

    @Test
    void parseDisclosureGroupWithSignedRate() {
        CobolRecordLayout layout = RecordLayouts.disclosureGroupRecord();
        CobolRecordParser parser = new CobolRecordParser(layout);

        String line = "A00000000001000100150{" + " ".repeat(28);
        Map<String, Object> record = parser.parse(line);

        assertThat(record.get("DIS_ACCT_GROUP_ID")).isEqualTo("A000000000");
        assertThat(record.get("DIS_TRAN_TYPE_CD")).isEqualTo("01");
        assertThat(record.get("DIS_TRAN_CAT_CD")).isEqualTo("0001");
        assertThat((BigDecimal) record.get("DIS_INT_RATE")).isEqualByComparingTo("15.00");
    }

    @Test
    void parseTranCatBalRecord() {
        CobolRecordLayout layout = RecordLayouts.tranCatBalRecord();
        CobolRecordParser parser = new CobolRecordParser(layout);

        String line = "00000000001010001" + "0000000000{" + " ".repeat(22);
        Map<String, Object> record = parser.parse(line);

        assertThat(record.get("TRANCAT_ACCT_ID")).isEqualTo("00000000001");
        assertThat(record.get("TRANCAT_TYPE_CD")).isEqualTo("01");
        assertThat(record.get("TRANCAT_CD")).isEqualTo("0001");
        assertThat((BigDecimal) record.get("TRAN_CAT_BAL")).isEqualByComparingTo("0.00");
    }

    @Test
    void parseTranCatRecord() {
        CobolRecordLayout layout = RecordLayouts.tranCatRecord();
        CobolRecordParser parser = new CobolRecordParser(layout);

        String desc = "Regular Sales Draft";
        String line = "01" + "0001" + desc + " ".repeat(50 - desc.length()) + " ".repeat(4);
        Map<String, Object> record = parser.parse(line);

        assertThat(record.get("TRAN_TYPE_CD")).isEqualTo("01");
        assertThat(record.get("TRAN_CAT_CD")).isEqualTo("0001");
        assertThat(record.get("TRAN_CAT_TYPE_DESC")).isEqualTo("Regular Sales Draft");
    }

    @Test
    void parseTranTypeRecord() {
        CobolRecordLayout layout = RecordLayouts.tranTypeRecord();
        CobolRecordParser parser = new CobolRecordParser(layout);

        String desc = "Purchase";
        String line = "01" + desc + " ".repeat(50 - desc.length()) + " ".repeat(8);
        Map<String, Object> record = parser.parse(line);

        assertThat(record.get("TRAN_TYPE")).isEqualTo("01");
        assertThat(record.get("TRAN_TYPE_DESC")).isEqualTo("Purchase");
    }

    @Test
    void fillerFieldsExcludedFromOutput() {
        CobolRecordLayout layout = RecordLayouts.tranTypeRecord();
        CobolRecordParser parser = new CobolRecordParser(layout);

        String line = "01" + "Purchase" + " ".repeat(42) + " ".repeat(8);
        Map<String, Object> record = parser.parse(line);

        assertThat(record).doesNotContainKey("FILLER");
        assertThat(record).hasSize(2);
    }

    @Test
    void shouldParseAcctdataFixture() throws Exception {
        List<Map<String, Object>> records = parseFixture("sample-acctdata.txt", RecordLayouts.accountRecord());
        assertThat(records).hasSize(3);

        Map<String, Object> first = records.get(0);
        assertThat(first.get("ACCT_ID")).isEqualTo("00000000001");
        assertThat(first.get("ACCT_ACTIVE_STATUS")).isEqualTo("Y");
        assertThat(first.get("ACCT_CURR_BAL")).isEqualTo(new BigDecimal("194.00"));
        assertThat(first.get("ACCT_CREDIT_LIMIT")).isEqualTo(new BigDecimal("2020.00"));
        assertThat(first.get("ACCT_CASH_CREDIT_LIMIT")).isEqualTo(new BigDecimal("1020.00"));
        assertThat(first.get("ACCT_OPEN_DATE")).isEqualTo("2014-11-20");
        assertThat(first.get("ACCT_EXPIRAION_DATE")).isEqualTo("2025-05-20");
        assertThat(first.get("ACCT_REISSUE_DATE")).isEqualTo("2025-05-20");
        assertThat(first.get("ACCT_CURR_CYC_CREDIT")).isEqualTo(new BigDecimal("0.00"));
        assertThat(first.get("ACCT_CURR_CYC_DEBIT")).isEqualTo(new BigDecimal("0.00"));
        assertThat(first).doesNotContainKey("FILLER");
    }

    @Test
    void shouldParseCarddataFixture() throws Exception {
        List<Map<String, Object>> records = parseFixture("sample-carddata.txt", RecordLayouts.cardRecord());
        assertThat(records).hasSize(3);

        Map<String, Object> first = records.get(0);
        assertThat(first.get("CARD_NUM")).isEqualTo("0500024453765740");
        assertThat(first.get("CARD_ACCT_ID")).isEqualTo("00000000050");
        assertThat(first.get("CARD_CVV_CD")).isEqualTo("747");
        assertThat(first.get("CARD_EMBOSSED_NAME")).isEqualTo("Aniya Von");
        assertThat(first.get("CARD_EXPIRAION_DATE")).isEqualTo("2023-03-09");
        assertThat(first.get("CARD_ACTIVE_STATUS")).isEqualTo("Y");
        assertThat(first).doesNotContainKey("FILLER");
    }

    @Test
    void shouldParseCardxrefFixture() throws Exception {
        List<Map<String, Object>> records = parseFixture("sample-cardxref.txt", RecordLayouts.cardXrefRecord());
        assertThat(records).hasSize(3);

        Map<String, Object> first = records.get(0);
        assertThat(first.get("XREF_CARD_NUM")).isEqualTo("0500024453765740");
        assertThat(first.get("XREF_CUST_ID")).isEqualTo("000000050");
        assertThat(first.get("XREF_ACCT_ID")).isEqualTo("00000000050");
    }

    @Test
    void shouldParseCustdataFixture() throws Exception {
        List<Map<String, Object>> records = parseFixture("sample-custdata.txt", RecordLayouts.customerRecord());
        assertThat(records).hasSize(3);

        Map<String, Object> first = records.get(0);
        assertThat(first.get("CUST_ID")).isEqualTo("000000001");
        assertThat(first.get("CUST_FIRST_NAME")).isEqualTo("Immanuel");
        assertThat(first.get("CUST_MIDDLE_NAME")).isEqualTo("Madeline");
        assertThat(first.get("CUST_LAST_NAME")).isEqualTo("Kessler");
        assertThat(first.get("CUST_ADDR_STATE_CD")).isEqualTo("NC");
        assertThat(first.get("CUST_ADDR_COUNTRY_CD")).isEqualTo("USA");
        assertThat(first.get("CUST_SSN")).isEqualTo("020973888");
        assertThat(first.get("CUST_PRI_CARD_HOLDER_IND")).isEqualTo("Y");
        assertThat(first.get("CUST_FICO_CREDIT_SCORE")).isEqualTo("274");
    }

    @Test
    void shouldParseDailytranFixture() throws Exception {
        List<Map<String, Object>> records = parseFixture("sample-dailytran.txt", RecordLayouts.dailyTransactionRecord());
        assertThat(records).hasSize(3);

        Map<String, Object> first = records.get(0);
        assertThat(first.get("DALYTRAN_ID")).isEqualTo("0000000000683580");
        assertThat(first.get("DALYTRAN_TYPE_CD")).isEqualTo("01");
        assertThat(first.get("DALYTRAN_CAT_CD")).isEqualTo("0001");
        assertThat(first.get("DALYTRAN_SOURCE")).isEqualTo("POS TERM");
        assertThat(first.get("DALYTRAN_AMT")).isEqualTo(new BigDecimal("504.77"));
        assertThat(first.get("DALYTRAN_MERCHANT_ID")).isEqualTo("800000000");

        Map<String, Object> second = records.get(1);
        assertThat(second.get("DALYTRAN_AMT")).isEqualTo(new BigDecimal("-919.00"));
    }

    @Test
    void shouldParseDiscgrpFixture() throws Exception {
        List<Map<String, Object>> records = parseFixture("sample-discgrp.txt", RecordLayouts.disclosureGroupRecord());
        assertThat(records).hasSize(3);

        Map<String, Object> first = records.get(0);
        assertThat(first.get("DIS_ACCT_GROUP_ID")).isEqualTo("A000000000");
        assertThat(first.get("DIS_TRAN_TYPE_CD")).isEqualTo("01");
        assertThat(first.get("DIS_TRAN_CAT_CD")).isEqualTo("0001");
        assertThat(first.get("DIS_INT_RATE")).isEqualTo(new BigDecimal("15.00"));
    }

    @Test
    void shouldParseTcatbalFixture() throws Exception {
        List<Map<String, Object>> records = parseFixture("sample-tcatbal.txt", RecordLayouts.tranCatBalRecord());
        assertThat(records).hasSize(3);

        Map<String, Object> first = records.get(0);
        assertThat(first.get("TRANCAT_ACCT_ID")).isEqualTo("00000000001");
        assertThat(first.get("TRANCAT_TYPE_CD")).isEqualTo("01");
        assertThat(first.get("TRANCAT_CD")).isEqualTo("0001");
        assertThat(first.get("TRAN_CAT_BAL")).isEqualTo(new BigDecimal("0.00"));
    }

    @Test
    void shouldParseTrancatgFixture() throws Exception {
        List<Map<String, Object>> records = parseFixture("sample-trancatg.txt", RecordLayouts.tranCatRecord());
        assertThat(records).hasSize(3);

        Map<String, Object> first = records.get(0);
        assertThat(first.get("TRAN_TYPE_CD")).isEqualTo("01");
        assertThat(first.get("TRAN_CAT_CD")).isEqualTo("0001");
        assertThat(first.get("TRAN_CAT_TYPE_DESC")).isEqualTo("Regular Sales Draft");
    }

    @Test
    void shouldParseTrantypeFixture() throws Exception {
        List<Map<String, Object>> records = parseFixture("sample-trantype.txt", RecordLayouts.tranTypeRecord());
        assertThat(records).hasSize(3);

        Map<String, Object> first = records.get(0);
        assertThat(first.get("TRAN_TYPE")).isEqualTo("01");
        assertThat(first.get("TRAN_TYPE_DESC")).isEqualTo("Purchase");
    }

    @Test
    void shouldHandleLineShorterThanRecordLength() {
        CobolRecordLayout layout = RecordLayouts.cardXrefRecord();
        CobolRecordParser parser = new CobolRecordParser(layout);
        String shortLine = "0500024453765740000000050";
        Map<String, Object> record = parser.parse(shortLine);

        assertThat(record.get("XREF_CARD_NUM")).isEqualTo("0500024453765740");
    }

    @Test
    void shouldSkipFillerFields() throws Exception {
        List<Map<String, Object>> records = parseFixture("sample-trantype.txt", RecordLayouts.tranTypeRecord());
        Map<String, Object> first = records.get(0);
        assertThat(first).doesNotContainKey("FILLER");
    }

    private List<Map<String, Object>> parseFixture(String fileName, CobolRecordLayout layout)
            throws IOException, URISyntaxException {
        Path path = Paths.get(getClass().getClassLoader()
            .getResource("fixtures/" + fileName).toURI());
        CobolRecordParser parser = new CobolRecordParser(layout);
        return parser.parseFile(path);
    }
}
