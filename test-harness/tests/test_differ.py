"""Tests for the field-level comparison utility."""

import json
import pytest
from decimal import Decimal
from pathlib import Path

from test_harness.differ import (
    diff_records,
    diff_json_files,
    DiffReport,
    ComparisonMode,
    normalize_date,
    normalize_timestamp,
    EXACT_MATCH_FIELDS,
)


class TestDiffRecords:
    """Tests for record-level diffing."""

    def test_identical_records_match(self):
        records = [{"A": "1", "B": Decimal("10.00")}]
        report = diff_records(records, records, file_name="test")
        assert report.is_match
        assert report.expected_count == 1
        assert report.actual_count == 1

    def test_field_mismatch_detected(self):
        expected = [{"A": "hello", "B": Decimal("10.00")}]
        actual = [{"A": "hello", "B": Decimal("20.00")}]
        report = diff_records(expected, actual, file_name="test")
        assert not report.is_match
        assert len(report.mismatches) == 1
        assert report.mismatches[0].field_name == "B"
        assert report.mismatches[0].expected == "10.00"
        assert report.mismatches[0].actual == "20.00"

    def test_missing_field_detected(self):
        expected = [{"A": "1", "B": "2"}]
        actual = [{"A": "1"}]
        report = diff_records(expected, actual, file_name="test")
        assert not report.is_match
        assert report.mismatches[0].field_name == "B"
        assert report.mismatches[0].actual == "<missing>"

    def test_extra_field_detected(self):
        expected = [{"A": "1"}]
        actual = [{"A": "1", "B": "2"}]
        report = diff_records(expected, actual, file_name="test")
        assert not report.is_match
        assert report.mismatches[0].field_name == "B"
        assert report.mismatches[0].expected == "<missing>"

    def test_missing_records_detected(self):
        expected = [{"A": "1"}, {"A": "2"}]
        actual = [{"A": "1"}]
        report = diff_records(expected, actual, file_name="test")
        assert not report.is_match
        assert report.missing_records == 1

    def test_extra_records_detected(self):
        expected = [{"A": "1"}]
        actual = [{"A": "1"}, {"A": "2"}]
        report = diff_records(expected, actual, file_name="test")
        assert not report.is_match
        assert report.extra_records == 1

    def test_decimal_comparison_ignores_trailing_zeros(self):
        expected = [{"amt": Decimal("10")}]
        actual = [{"amt": Decimal("10.00")}]
        report = diff_records(expected, actual, file_name="test")
        assert report.is_match

    def test_numeric_tolerance(self):
        expected = [{"amt": Decimal("10.00")}]
        actual = [{"amt": Decimal("10.01")}]
        report = diff_records(
            expected, actual, file_name="test",
            numeric_tolerance=Decimal("0.02")
        )
        assert report.is_match

    def test_key_field_in_report(self):
        expected = [{"ID": "ACC001", "BAL": Decimal("100")}]
        actual = [{"ID": "ACC001", "BAL": Decimal("200")}]
        report = diff_records(expected, actual, file_name="test", key_field="ID")
        assert report.mismatches[0].key_value == "ACC001"

    def test_summary_match(self):
        records = [{"A": "1"}]
        report = diff_records(records, records, file_name="test.json")
        assert "MATCH" in report.summary()

    def test_summary_mismatch(self):
        expected = [{"A": "1"}]
        actual = [{"A": "2"}]
        report = diff_records(expected, actual, file_name="test.json")
        summary = report.summary()
        assert "mismatch" in summary.lower()

    def test_internal_fields_skipped(self):
        expected = [{"A": "1", "_line_number": 1}]
        actual = [{"A": "1", "_line_number": 5}]
        report = diff_records(expected, actual, file_name="test")
        assert report.is_match


class TestNormalizers:
    """Tests for date and timestamp normalization."""

    def test_date_normalizer_strips_hyphens(self):
        assert normalize_date("2014-11-20") == "20141120"

    def test_date_normalizer_strips_slashes(self):
        assert normalize_date("2014/11/20") == "20141120"

    def test_date_normalizer_handles_no_delimiter(self):
        assert normalize_date("20141120") == "20141120"

    def test_date_normalizer_strips_padding(self):
        assert normalize_date("20141120  ") == "20141120"

    def test_timestamp_normalizer_strips_spaces(self):
        assert normalize_timestamp("2022-01-15 10:30:00     ") == "2022-01-15 10:30:00"

    def test_date_field_normalization_in_diff(self):
        expected = [{"ACCT-OPEN-DATE": "2014-11-20"}]
        actual = [{"ACCT-OPEN-DATE": "20141120  "}]
        report = diff_records(expected, actual, file_name="test")
        assert report.is_match

    def test_custom_normalizer(self):
        expected = [{"NAME": "JOHN DOE    "}]
        actual = [{"NAME": "john doe"}]
        report = diff_records(
            expected, actual, file_name="test",
            normalizers={"NAME": lambda v: str(v).strip().upper()}
        )
        assert report.is_match


class TestComparisonModes:
    """Tests for comparison mode presets."""

    def test_strict_mode_no_tolerance(self):
        expected = [{"amt": Decimal("10.00")}]
        actual = [{"amt": Decimal("10.01")}]
        report = diff_records(
            expected, actual, file_name="test",
            mode=ComparisonMode.STRICT
        )
        assert not report.is_match

    def test_tolerant_mode_allows_penny_difference(self):
        expected = [{"amt": Decimal("10.00")}]
        actual = [{"amt": Decimal("10.01")}]
        report = diff_records(
            expected, actual, file_name="test",
            mode=ComparisonMode.TOLERANT
        )
        assert report.is_match

    def test_tolerant_mode_rejects_large_difference(self):
        expected = [{"amt": Decimal("10.00")}]
        actual = [{"amt": Decimal("10.05")}]
        report = diff_records(
            expected, actual, file_name="test",
            mode=ComparisonMode.TOLERANT
        )
        assert not report.is_match

    def test_financial_mode_exact_match(self):
        expected = [{"amt": Decimal("10.00")}]
        actual = [{"amt": Decimal("10.01")}]
        report = diff_records(
            expected, actual, file_name="test",
            mode=ComparisonMode.FINANCIAL
        )
        assert not report.is_match

    def test_exact_match_fields_override_tolerance(self):
        expected = [{"ACCT-ID": "00000000001", "amt": Decimal("10.00")}]
        actual = [{"ACCT-ID": "00000000002", "amt": Decimal("10.00")}]
        report = diff_records(
            expected, actual, file_name="test",
            mode=ComparisonMode.TOLERANT
        )
        assert not report.is_match
        assert report.mismatches[0].field_name == "ACCT-ID"

    def test_per_field_tolerance(self):
        expected = [{"bal": Decimal("100.00"), "interest": Decimal("1.50")}]
        actual = [{"bal": Decimal("100.05"), "interest": Decimal("1.51")}]
        report = diff_records(
            expected, actual, file_name="test",
            field_tolerances={
                "bal": Decimal("0.10"),
                "interest": Decimal("0"),
            }
        )
        # bal: 0.05 <= 0.10 tolerance → pass
        # interest: 0.01 > 0 tolerance → fail
        assert not report.is_match
        assert len(report.mismatches) == 1
        assert report.mismatches[0].field_name == "interest"


class TestSortAndSkip:
    """Tests for record sorting and field skipping."""

    def test_sort_by_key(self):
        expected = [{"ID": "B", "V": "1"}, {"ID": "A", "V": "2"}]
        actual = [{"ID": "A", "V": "2"}, {"ID": "B", "V": "1"}]
        report = diff_records(expected, actual, file_name="test", sort_by="ID")
        assert report.is_match

    def test_sort_by_key_detects_mismatch(self):
        expected = [{"ID": "A", "V": "1"}, {"ID": "B", "V": "2"}]
        actual = [{"ID": "B", "V": "2"}, {"ID": "A", "V": "WRONG"}]
        report = diff_records(expected, actual, file_name="test", sort_by="ID")
        assert not report.is_match
        assert report.mismatches[0].field_name == "V"

    def test_skip_fields(self):
        expected = [{"A": "1", "B": "2", "C": "3"}]
        actual = [{"A": "1", "B": "DIFFERENT", "C": "3"}]
        report = diff_records(
            expected, actual, file_name="test",
            skip_fields={"B"}
        )
        assert report.is_match

    def test_mismatch_fields_property(self):
        expected = [{"A": "1", "B": "2", "C": "3"}]
        actual = [{"A": "X", "B": "2", "C": "Y"}]
        report = diff_records(expected, actual, file_name="test")
        assert report.mismatch_fields == {"A", "C"}

    def test_mismatches_for_field(self):
        expected = [{"A": "1"}, {"A": "2"}]
        actual = [{"A": "X"}, {"A": "Y"}]
        report = diff_records(expected, actual, file_name="test")
        a_mismatches = report.mismatches_for_field("A")
        assert len(a_mismatches) == 2


class TestDiffJsonFiles:
    """Tests for JSON file comparison."""

    def test_diff_json_files(self, tmp_path):
        data = {
            "records": [
                {"ID": "1", "AMT": "100.00"},
                {"ID": "2", "AMT": "200.50"},
            ]
        }
        expected_path = tmp_path / "expected.json"
        actual_path = tmp_path / "actual.json"
        expected_path.write_text(json.dumps(data))
        actual_path.write_text(json.dumps(data))
        report = diff_json_files(expected_path, actual_path)
        assert report.is_match

    def test_diff_json_files_mismatch(self, tmp_path):
        expected_data = {"records": [{"ID": "1", "AMT": "100.00"}]}
        actual_data = {"records": [{"ID": "1", "AMT": "200.00"}]}
        expected_path = tmp_path / "expected.json"
        actual_path = tmp_path / "actual.json"
        expected_path.write_text(json.dumps(expected_data))
        actual_path.write_text(json.dumps(actual_data))
        report = diff_json_files(expected_path, actual_path)
        assert not report.is_match

    def test_diff_json_files_with_sort(self, tmp_path):
        expected_data = {"records": [{"ID": "2", "V": "b"}, {"ID": "1", "V": "a"}]}
        actual_data = {"records": [{"ID": "1", "V": "a"}, {"ID": "2", "V": "b"}]}
        expected_path = tmp_path / "expected.json"
        actual_path = tmp_path / "actual.json"
        expected_path.write_text(json.dumps(expected_data))
        actual_path.write_text(json.dumps(actual_data))
        report = diff_json_files(expected_path, actual_path, sort_by="ID")
        assert report.is_match
