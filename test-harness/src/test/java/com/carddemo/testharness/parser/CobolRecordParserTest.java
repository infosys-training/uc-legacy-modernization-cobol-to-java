package com.carddemo.testharness.parser;

import com.carddemo.testharness.model.RecordLayout;
import com.carddemo.testharness.model.RecordLayouts;
import org.junit.jupiter.api.BeforeEach;
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

    private CobolRecordParser parser;

    @BeforeEach
    void setUp() {
        parser = new CobolRecordParser();
    }

    @Test
    void shouldParseAcctdataRecord() throws Exception {
        List<Map<String, Object>> records = parseFixture("sample-acctdata.txt", RecordLayouts.acctdata());
        assertThat(records).hasSize(3);

        Map<String, Object> first = records.get(0);
        assertThat(first.get("ACCT-ID")).isEqualTo("00000000001");
        assertThat(first.get("ACCT-ACTIVE-STATUS")).isEqualTo("Y");
        assertThat(first.get("ACCT-CURR-BAL")).isEqualTo(new BigDecimal("194.00"));
        assertThat(first.get("ACCT-CREDIT-LIMIT")).isEqualTo(new BigDecimal("2020.00"));
        assertThat(first.get("ACCT-CASH-CREDIT-LIMIT")).isEqualTo(new BigDecimal("1020.00"));
        assertThat(first.get("ACCT-OPEN-DATE")).isEqualTo("2014-11-20");
        assertThat(first.get("ACCT-EXPIRAION-DATE")).isEqualTo("2025-05-20");
        assertThat(first.get("ACCT-REISSUE-DATE")).isEqualTo("2025-05-20");
        assertThat(first.get("ACCT-CURR-CYC-CREDIT")).isEqualTo(new BigDecimal("0.00"));
        assertThat(first.get("ACCT-CURR-CYC-DEBIT")).isEqualTo(new BigDecimal("0.00"));
        assertThat(first).doesNotContainKey("FILLER");
    }

    @Test
    void shouldParseCarddataRecord() throws Exception {
        List<Map<String, Object>> records = parseFixture("sample-carddata.txt", RecordLayouts.carddata());
        assertThat(records).hasSize(3);

        Map<String, Object> first = records.get(0);
        assertThat(first.get("CARD-NUM")).isEqualTo("0500024453765740");
        assertThat(first.get("CARD-ACCT-ID")).isEqualTo("00000000050");
        assertThat(first.get("CARD-CVV-CD")).isEqualTo("747");
        assertThat(first.get("CARD-EMBOSSED-NAME")).isEqualTo("Aniya Von");
        assertThat(first.get("CARD-EXPIRAION-DATE")).isEqualTo("2023-03-09");
        assertThat(first.get("CARD-ACTIVE-STATUS")).isEqualTo("Y");
        assertThat(first).doesNotContainKey("FILLER");
    }

    @Test
    void shouldParseCardxrefRecord() throws Exception {
        List<Map<String, Object>> records = parseFixture("sample-cardxref.txt", RecordLayouts.cardxref());
        assertThat(records).hasSize(3);

        Map<String, Object> first = records.get(0);
        assertThat(first.get("XREF-CARD-NUM")).isEqualTo("0500024453765740");
        assertThat(first.get("XREF-CUST-ID")).isEqualTo("000000050");
        assertThat(first.get("XREF-ACCT-ID")).isEqualTo("00000000050");
    }

    @Test
    void shouldParseCustdataRecord() throws Exception {
        List<Map<String, Object>> records = parseFixture("sample-custdata.txt", RecordLayouts.custdata());
        assertThat(records).hasSize(3);

        Map<String, Object> first = records.get(0);
        assertThat(first.get("CUST-ID")).isEqualTo("000000001");
        assertThat(first.get("CUST-FIRST-NAME")).isEqualTo("Immanuel");
        assertThat(first.get("CUST-MIDDLE-NAME")).isEqualTo("Madeline");
        assertThat(first.get("CUST-LAST-NAME")).isEqualTo("Kessler");
        assertThat(first.get("CUST-ADDR-STATE-CD")).isEqualTo("NC");
        assertThat(first.get("CUST-ADDR-COUNTRY-CD")).isEqualTo("USA");
        assertThat(first.get("CUST-SSN")).isEqualTo("020973888");
        assertThat(first.get("CUST-PRI-CARD-HOLDER-IND")).isEqualTo("Y");
        assertThat(first.get("CUST-FICO-CREDIT-SCORE")).isEqualTo("274");
    }

    @Test
    void shouldParseDailytranRecord() throws Exception {
        List<Map<String, Object>> records = parseFixture("sample-dailytran.txt", RecordLayouts.dailytran());
        assertThat(records).hasSize(3);

        Map<String, Object> first = records.get(0);
        assertThat(first.get("DALYTRAN-ID")).isEqualTo("0000000000683580");
        assertThat(first.get("DALYTRAN-TYPE-CD")).isEqualTo("01");
        assertThat(first.get("DALYTRAN-CAT-CD")).isEqualTo("0001");
        assertThat(first.get("DALYTRAN-SOURCE")).isEqualTo("POS TERM");
        assertThat(first.get("DALYTRAN-AMT")).isEqualTo(new BigDecimal("504.77"));
        assertThat(first.get("DALYTRAN-MERCHANT-ID")).isEqualTo("800000000");

        Map<String, Object> second = records.get(1);
        assertThat(second.get("DALYTRAN-AMT")).isEqualTo(new BigDecimal("-919.00"));
    }

    @Test
    void shouldParseDiscgrpRecord() throws Exception {
        List<Map<String, Object>> records = parseFixture("sample-discgrp.txt", RecordLayouts.discgrp());
        assertThat(records).hasSize(3);

        Map<String, Object> first = records.get(0);
        assertThat(first.get("DIS-ACCT-GROUP-ID")).isEqualTo("A000000000");
        assertThat(first.get("DIS-TRAN-TYPE-CD")).isEqualTo("01");
        assertThat(first.get("DIS-TRAN-CAT-CD")).isEqualTo("0001");
        assertThat(first.get("DIS-INT-RATE")).isEqualTo(new BigDecimal("15.00"));
    }

    @Test
    void shouldParseTcatbalRecord() throws Exception {
        List<Map<String, Object>> records = parseFixture("sample-tcatbal.txt", RecordLayouts.tcatbal());
        assertThat(records).hasSize(3);

        Map<String, Object> first = records.get(0);
        assertThat(first.get("TRANCAT-ACCT-ID")).isEqualTo("00000000001");
        assertThat(first.get("TRANCAT-TYPE-CD")).isEqualTo("01");
        assertThat(first.get("TRANCAT-CD")).isEqualTo("0001");
        assertThat(first.get("TRAN-CAT-BAL")).isEqualTo(new BigDecimal("0.00"));
    }

    @Test
    void shouldParseTrancatgRecord() throws Exception {
        List<Map<String, Object>> records = parseFixture("sample-trancatg.txt", RecordLayouts.trancatg());
        assertThat(records).hasSize(3);

        Map<String, Object> first = records.get(0);
        assertThat(first.get("TRAN-TYPE-CD")).isEqualTo("01");
        assertThat(first.get("TRAN-CAT-CD")).isEqualTo("0001");
        assertThat(first.get("TRAN-CAT-TYPE-DESC")).isEqualTo("Regular Sales Draft");
    }

    @Test
    void shouldParseTrantypeRecord() throws Exception {
        List<Map<String, Object>> records = parseFixture("sample-trantype.txt", RecordLayouts.trantype());
        assertThat(records).hasSize(3);

        Map<String, Object> first = records.get(0);
        assertThat(first.get("TRAN-TYPE")).isEqualTo("01");
        assertThat(first.get("TRAN-TYPE-DESC")).isEqualTo("Purchase");
    }

    @Test
    void shouldHandleLineShorterThanRecordLength() {
        RecordLayout layout = RecordLayouts.cardxref();
        String shortLine = "0500024453765740000000050";
        Map<String, Object> record = parser.parseLine(shortLine, layout);

        assertThat(record.get("XREF-CARD-NUM")).isEqualTo("0500024453765740");
    }

    @Test
    void shouldSkipFillerFields() throws Exception {
        List<Map<String, Object>> records = parseFixture("sample-trantype.txt", RecordLayouts.trantype());
        Map<String, Object> first = records.get(0);
        assertThat(first).doesNotContainKey("FILLER");
    }

    private List<Map<String, Object>> parseFixture(String fileName, RecordLayout layout)
            throws IOException, URISyntaxException {
        Path path = Paths.get(getClass().getClassLoader()
            .getResource("fixtures/" + fileName).toURI());
        return parser.parseFile(path, layout);
    }
}
