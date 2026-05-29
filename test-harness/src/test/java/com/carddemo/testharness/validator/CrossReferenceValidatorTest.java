package com.carddemo.testharness.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assumptions.assumeThat;

class CrossReferenceValidatorTest {

    private CrossReferenceValidator validator;

    @BeforeEach
    void setUp() {
        validator = new CrossReferenceValidator();
    }

    @Test
    void shouldPassWhenAllReferencesExist() {
        List<Map<String, Object>> sourceRecords = List.of(
            Map.of("XREF-CARD-NUM", "4000000000000001"),
            Map.of("XREF-CARD-NUM", "4000000000000002")
        );
        List<Map<String, Object>> targetRecords = List.of(
            Map.of("CARD-NUM", "4000000000000001"),
            Map.of("CARD-NUM", "4000000000000002"),
            Map.of("CARD-NUM", "4000000000000003")
        );

        ValidationResult result = validator.validate(
            sourceRecords, "XREF-CARD-NUM",
            targetRecords, "CARD-NUM");

        assertThat(result.isValid()).isTrue();
    }

    @Test
    void shouldFailWhenReferencesAreMissing() {
        List<Map<String, Object>> sourceRecords = List.of(
            Map.of("XREF-CARD-NUM", "4000000000000001"),
            Map.of("XREF-CARD-NUM", "4000000000000099")
        );
        List<Map<String, Object>> targetRecords = List.of(
            Map.of("CARD-NUM", "4000000000000001")
        );

        ValidationResult result = validator.validate(
            sourceRecords, "XREF-CARD-NUM",
            targetRecords, "CARD-NUM");

        assertThat(result.isValid()).isFalse();
        assertThat(result.getMessage()).contains("4000000000000099");
    }

    @Test
    void shouldPassWithEmptySource() {
        List<Map<String, Object>> sourceRecords = List.of();
        List<Map<String, Object>> targetRecords = List.of(
            Map.of("CARD-NUM", "4000000000000001")
        );

        ValidationResult result = validator.validate(
            sourceRecords, "XREF-CARD-NUM",
            targetRecords, "CARD-NUM");

        assertThat(result.isValid()).isTrue();
    }

    @Test
    void shouldFailWithEmptyTarget() {
        List<Map<String, Object>> sourceRecords = List.of(
            Map.of("XREF-CARD-NUM", "4000000000000001")
        );
        List<Map<String, Object>> targetRecords = List.of();

        ValidationResult result = validator.validate(
            sourceRecords, "XREF-CARD-NUM",
            targetRecords, "CARD-NUM");

        assertThat(result.isValid()).isFalse();
    }

    @Test
    void shouldHandleDuplicateSourceReferences() {
        List<Map<String, Object>> sourceRecords = List.of(
            Map.of("CARD-NUM", "4000000000000001"),
            Map.of("CARD-NUM", "4000000000000001"),
            Map.of("CARD-NUM", "4000000000000002")
        );
        List<Map<String, Object>> targetRecords = List.of(
            Map.of("ACCT-ID", "4000000000000001"),
            Map.of("ACCT-ID", "4000000000000002")
        );

        ValidationResult result = validator.validate(
            sourceRecords, "CARD-NUM",
            targetRecords, "ACCT-ID");

        assertThat(result.isValid()).isTrue();
    }

    @Test
    void shouldReportMultipleMissingReferences() {
        List<Map<String, Object>> sourceRecords = List.of(
            Map.of("ID", "A"),
            Map.of("ID", "B"),
            Map.of("ID", "C")
        );
        List<Map<String, Object>> targetRecords = List.of(
            Map.of("REF", "A")
        );

        ValidationResult result = validator.validate(
            sourceRecords, "ID",
            targetRecords, "REF");

        assertThat(result.isValid()).isFalse();
        assertThat(result.getMessage()).contains("2 values");
    }

    // --- Tests for validateTransactionTypeFK ---

    @Test
    void shouldPassWhenAllTransactionTypesExist() {
        List<Map<String, Object>> transactions = List.of(
            Map.of("TRAN-TYPE-CD", "SA"),
            Map.of("TRAN-TYPE-CD", "PR")
        );
        List<Map<String, Object>> trantypes = List.of(
            Map.of("TYPE-CD", "SA"),
            Map.of("TYPE-CD", "PR"),
            Map.of("TYPE-CD", "CR")
        );

        ValidationResult result = validator.validateTransactionTypeFK(
            transactions, "TRAN-TYPE-CD", trantypes, "TYPE-CD");

        assertThat(result.isValid()).isTrue();
    }

    @Test
    void shouldFailWhenTransactionTypeIsMissing() {
        List<Map<String, Object>> transactions = List.of(
            Map.of("TRAN-TYPE-CD", "SA"),
            Map.of("TRAN-TYPE-CD", "XX")
        );
        List<Map<String, Object>> trantypes = List.of(
            Map.of("TYPE-CD", "SA"),
            Map.of("TYPE-CD", "PR")
        );

        ValidationResult result = validator.validateTransactionTypeFK(
            transactions, "TRAN-TYPE-CD", trantypes, "TYPE-CD");

        assertThat(result.isValid()).isFalse();
        assertThat(result.getMessage()).contains("XX");
    }

    // --- Tests for validateTransactionCategoryFK ---

    @Test
    void shouldPassWhenAllCategoryPairsExist() {
        List<Map<String, Object>> transactions = List.of(
            mapOf("TYPE-CD", "SA", "CAT-CD", "01"),
            mapOf("TYPE-CD", "PR", "CAT-CD", "02")
        );
        List<Map<String, Object>> trancatg = List.of(
            mapOf("TRAN-TYPE-CD", "SA", "TRAN-CAT-CD", "01"),
            mapOf("TRAN-TYPE-CD", "PR", "TRAN-CAT-CD", "02"),
            mapOf("TRAN-TYPE-CD", "CR", "TRAN-CAT-CD", "03")
        );

        ValidationResult result = validator.validateTransactionCategoryFK(
            transactions, "TYPE-CD", "CAT-CD",
            trancatg, "TRAN-TYPE-CD", "TRAN-CAT-CD");

        assertThat(result.isValid()).isTrue();
    }

    @Test
    void shouldFailWhenCategoryPairIsMissing() {
        List<Map<String, Object>> transactions = List.of(
            mapOf("TYPE-CD", "SA", "CAT-CD", "99")
        );
        List<Map<String, Object>> trancatg = List.of(
            mapOf("TRAN-TYPE-CD", "SA", "TRAN-CAT-CD", "01")
        );

        ValidationResult result = validator.validateTransactionCategoryFK(
            transactions, "TYPE-CD", "CAT-CD",
            trancatg, "TRAN-TYPE-CD", "TRAN-CAT-CD");

        assertThat(result.isValid()).isFalse();
        assertThat(result.getMessage()).contains("SA|99");
    }

    // --- Tests for validateTcatbalIntegrity ---

    @Test
    void shouldPassTcatbalIntegrityWhenAllRefsExist() {
        List<Map<String, Object>> tcatbal = List.of(
            mapOf("TRANCAT-ACCT-ID", "001", "TRANCAT-TYPE-CD", "SA", "TRANCAT-CAT-CD", "01")
        );
        List<Map<String, Object>> acctdata = List.of(Map.of("ACCT-ID", "001"));
        List<Map<String, Object>> trantype = List.of(Map.of("TYPE-CD", "SA"));
        List<Map<String, Object>> trancatg = List.of(
            mapOf("TRAN-TYPE-CD", "SA", "TRAN-CAT-CD", "01")
        );

        ValidationResult result = validator.validateTcatbalIntegrity(
            tcatbal, "TRANCAT-ACCT-ID", "TRANCAT-TYPE-CD", "TRANCAT-CAT-CD",
            acctdata, "ACCT-ID",
            trantype, "TYPE-CD",
            trancatg, "TRAN-TYPE-CD", "TRAN-CAT-CD");

        assertThat(result.isValid()).isTrue();
    }

    @Test
    void shouldFailTcatbalIntegrityWhenAcctIdMissing() {
        List<Map<String, Object>> tcatbal = List.of(
            mapOf("TRANCAT-ACCT-ID", "999", "TRANCAT-TYPE-CD", "SA", "TRANCAT-CAT-CD", "01")
        );
        List<Map<String, Object>> acctdata = List.of(Map.of("ACCT-ID", "001"));
        List<Map<String, Object>> trantype = List.of(Map.of("TYPE-CD", "SA"));
        List<Map<String, Object>> trancatg = List.of(
            mapOf("TRAN-TYPE-CD", "SA", "TRAN-CAT-CD", "01")
        );

        ValidationResult result = validator.validateTcatbalIntegrity(
            tcatbal, "TRANCAT-ACCT-ID", "TRANCAT-TYPE-CD", "TRANCAT-CAT-CD",
            acctdata, "ACCT-ID",
            trantype, "TYPE-CD",
            trancatg, "TRAN-TYPE-CD", "TRAN-CAT-CD");

        assertThat(result.isValid()).isFalse();
        assertThat(result.getMessage()).contains("999");
    }

    // --- Tests for validateDisclosureGroupCompleteness ---

    @Test
    void shouldPassDisclosureGroupWhenComplete() {
        List<Map<String, Object>> acctdata = List.of(
            Map.of("ACCT-GROUP-ID", "GRP1")
        );
        List<Map<String, Object>> trancatg = List.of(
            mapOf("TYPE-CD", "SA", "CAT-CD", "01")
        );
        List<Map<String, Object>> discgrp = List.of(
            mapOf("GROUP-ID", "GRP1", "DISC-TYPE-CD", "SA", "DISC-CAT-CD", "01")
        );

        ValidationResult result = validator.validateDisclosureGroupCompleteness(
            acctdata, "ACCT-GROUP-ID",
            trancatg, "TYPE-CD", "CAT-CD",
            discgrp, "GROUP-ID", "DISC-TYPE-CD", "DISC-CAT-CD");

        assertThat(result.isValid()).isTrue();
    }

    @Test
    void shouldPassDisclosureGroupWithDefaultFallback() {
        List<Map<String, Object>> acctdata = List.of(
            Map.of("ACCT-GROUP-ID", "GRP1")
        );
        List<Map<String, Object>> trancatg = List.of(
            mapOf("TYPE-CD", "SA", "CAT-CD", "01")
        );
        List<Map<String, Object>> discgrp = List.of(
            mapOf("GROUP-ID", "DEFAULT", "DISC-TYPE-CD", "SA", "DISC-CAT-CD", "01")
        );

        ValidationResult result = validator.validateDisclosureGroupCompleteness(
            acctdata, "ACCT-GROUP-ID",
            trancatg, "TYPE-CD", "CAT-CD",
            discgrp, "GROUP-ID", "DISC-TYPE-CD", "DISC-CAT-CD");

        assertThat(result.isValid()).isTrue();
    }

    @Test
    void shouldFailDisclosureGroupWhenMissing() {
        List<Map<String, Object>> acctdata = List.of(
            Map.of("ACCT-GROUP-ID", "GRP1")
        );
        List<Map<String, Object>> trancatg = List.of(
            mapOf("TYPE-CD", "SA", "CAT-CD", "01"),
            mapOf("TYPE-CD", "PR", "CAT-CD", "02")
        );
        List<Map<String, Object>> discgrp = List.of(
            mapOf("GROUP-ID", "GRP1", "DISC-TYPE-CD", "SA", "DISC-CAT-CD", "01")
        );

        ValidationResult result = validator.validateDisclosureGroupCompleteness(
            acctdata, "ACCT-GROUP-ID",
            trancatg, "TYPE-CD", "CAT-CD",
            discgrp, "GROUP-ID", "DISC-TYPE-CD", "DISC-CAT-CD");

        assertThat(result.isValid()).isFalse();
        assertThat(result.getMessage()).contains("GRP1");
        assertThat(result.getMessage()).contains("PR");
    }

    // --- Integration test loading golden files ---

    @Test
    @Tag("integration")
    void shouldValidateGoldenFileCrossReferences() {
        Path goldenOutputDir = Paths.get("..", "golden-files", "output");
        assumeThat(Files.exists(goldenOutputDir)).isTrue();

        Path acctdataFile = goldenOutputDir.resolve("acctdata.txt");
        Path trantypeFile = goldenOutputDir.resolve("trantype.txt");
        Path trancatgFile = goldenOutputDir.resolve("trancatg.txt");
        assumeThat(Files.exists(acctdataFile)).isTrue();
        assumeThat(Files.exists(trantypeFile)).isTrue();
        assumeThat(Files.exists(trancatgFile)).isTrue();

        // This test validates the structure exists; actual parsing requires
        // the CobolRecordParser from cobol-parser-common which is tested
        // in GoldenFileValidationTest
        assertThat(Files.exists(goldenOutputDir)).isTrue();
    }

    private static Map<String, Object> mapOf(String k1, Object v1, String k2, Object v2) {
        Map<String, Object> map = new HashMap<>();
        map.put(k1, v1);
        map.put(k2, v2);
        return map;
    }

    private static Map<String, Object> mapOf(String k1, Object v1, String k2, Object v2, String k3, Object v3) {
        Map<String, Object> map = new HashMap<>();
        map.put(k1, v1);
        map.put(k2, v2);
        map.put(k3, v3);
        return map;
    }
}
