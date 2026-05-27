"""Tests for the field-level comparison utility."""

import json
import pytest
from decimal import Decimal
from pathlib import Path

from test_harness.differ import diff_records, diff_json_files, DiffReport


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
