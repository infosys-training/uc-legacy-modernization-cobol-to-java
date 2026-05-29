package com.carddemo.testharness.integration;

import com.carddemo.testharness.comparator.ComparisonResult;
import com.carddemo.testharness.comparator.FieldByFieldComparator;
import com.carddemo.testharness.comparator.ToleranceRule;
import com.carddemo.testharness.model.RecordLayout;
import com.carddemo.testharness.model.RecordLayouts;
import com.carddemo.testharness.parser.CobolRecordParser;
import com.carddemo.testharness.validator.CrossReferenceValidator;
import com.carddemo.testharness.validator.NumericSumValidator;
import com.carddemo.testharness.validator.RecordCountValidator;
import com.carddemo.testharness.validator.ValidationResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assumptions.assumeThat;

/**
 * Integration tests that validate Java-migrated output against COBOL golden files.
 *
 * These tests expect golden-files/output/ to exist with migrated output data.
 * They are skipped by default (tagged as "integration") and skipped at runtime
 * if the golden-files directory is not present.
 *
 * Usage pattern:
 *   1. Run the COBOL-to-Java migrated application to produce output in golden-files/output/
 *   2. Run these tests with: mvn test -Dgroups=integration
 */
@Tag("integration")
class GoldenFileValidationTest {

    private static final Path GOLDEN_OUTPUT_DIR = Paths.get("golden-files", "output");
    private static final Path COBOL_INPUT_DIR = Paths.get("..", "app", "data", "ASCII");

    private CobolRecordParser parser;
    private FieldByFieldComparator comparator;
    private RecordCountValidator recordCountValidator;
    private NumericSumValidator numericSumValidator;
    private CrossReferenceValidator crossReferenceValidator;

    @BeforeEach
    void setUp() {
        parser = new CobolRecordParser();
        comparator = new FieldByFieldComparator();
        recordCountValidator = new RecordCountValidator();
        numericSumValidator = new NumericSumValidator();
        crossReferenceValidator = new CrossReferenceValidator();
    }

    @Test
    void shouldValidateAcctdataRecordCounts() throws IOException {
        assumeThat(Files.exists(GOLDEN_OUTPUT_DIR)).isTrue();
        Path goldenFile = GOLDEN_OUTPUT_DIR.resolve("acctdata.txt");
        assumeThat(Files.exists(goldenFile)).isTrue();

        Path cobolFile = COBOL_INPUT_DIR.resolve("acctdata.txt");
        RecordLayout layout = RecordLayouts.acctdata();

        List<Map<String, Object>> expected = parser.parseFile(cobolFile, layout);
        List<Map<String, Object>> actual = parser.parseFile(goldenFile, layout);

        ValidationResult countResult = recordCountValidator.validate(expected, actual);
        assertThat(countResult.isValid())
            .as("Record count mismatch: %s", countResult.getMessage())
            .isTrue();
    }

    @Test
    void shouldValidateAcctdataFieldByField() throws IOException {
        assumeThat(Files.exists(GOLDEN_OUTPUT_DIR)).isTrue();
        Path goldenFile = GOLDEN_OUTPUT_DIR.resolve("acctdata.txt");
        assumeThat(Files.exists(goldenFile)).isTrue();

        Path cobolFile = COBOL_INPUT_DIR.resolve("acctdata.txt");
        RecordLayout layout = RecordLayouts.acctdata();

        List<Map<String, Object>> expected = parser.parseFile(cobolFile, layout);
        List<Map<String, Object>> actual = parser.parseFile(goldenFile, layout);

        Set<ToleranceRule> tolerances = EnumSet.of(
            ToleranceRule.TRAILING_SPACES,
            ToleranceRule.DECIMAL_PRECISION,
            ToleranceRule.FILLER_SKIP
        );

        for (int i = 0; i < Math.min(expected.size(), actual.size()); i++) {
            List<ComparisonResult> results = comparator.compare(
                expected.get(i), actual.get(i), tolerances);
            for (ComparisonResult cr : results) {
                assertThat(cr.isMatch())
                    .as("Record %d, field '%s': expected=%s, actual=%s",
                        i, cr.getFieldName(), cr.getExpected(), cr.getActual())
                    .isTrue();
            }
        }
    }

    @Test
    void shouldValidateCardXrefIntegrity() throws IOException {
        assumeThat(Files.exists(GOLDEN_OUTPUT_DIR)).isTrue();
        Path xrefFile = GOLDEN_OUTPUT_DIR.resolve("cardxref.txt");
        Path cardFile = GOLDEN_OUTPUT_DIR.resolve("carddata.txt");
        assumeThat(Files.exists(xrefFile)).isTrue();
        assumeThat(Files.exists(cardFile)).isTrue();

        List<Map<String, Object>> xrefRecords = parser.parseFile(xrefFile, RecordLayouts.cardxref());
        List<Map<String, Object>> cardRecords = parser.parseFile(cardFile, RecordLayouts.carddata());

        ValidationResult result = crossReferenceValidator.validate(
            xrefRecords, "XREF-CARD-NUM",
            cardRecords, "CARD-NUM");

        assertThat(result.isValid())
            .as("Cross-reference integrity: %s", result.getMessage())
            .isTrue();
    }

    @Test
    void shouldValidateAccountBalanceSums() throws IOException {
        assumeThat(Files.exists(GOLDEN_OUTPUT_DIR)).isTrue();
        Path goldenFile = GOLDEN_OUTPUT_DIR.resolve("acctdata.txt");
        Path cobolFile = COBOL_INPUT_DIR.resolve("acctdata.txt");
        assumeThat(Files.exists(goldenFile)).isTrue();

        RecordLayout layout = RecordLayouts.acctdata();
        List<Map<String, Object>> expected = parser.parseFile(cobolFile, layout);
        List<Map<String, Object>> actual = parser.parseFile(goldenFile, layout);

        BigDecimal expectedSum = expected.stream()
            .map(r -> (BigDecimal) r.get("ACCT-CURR-BAL"))
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        ValidationResult result = numericSumValidator.validate(actual, "ACCT-CURR-BAL", expectedSum);
        assertThat(result.isValid())
            .as("Balance sum: %s", result.getMessage())
            .isTrue();
    }
}
