"""Tests for the field-by-field output comparator."""

import tempfile
from decimal import Decimal
from pathlib import Path

import pytest

import sys
sys.path.insert(0, str(Path(__file__).resolve().parent.parent))

from cobol_parser import FieldDefinition, FieldType, RecordLayout
from comparator import (
    ComparisonResult,
    FieldMismatch,
    MismatchSeverity,
    compare_files,
    compare_records,
)


@pytest.fixture
def simple_layout():
    return RecordLayout(
        name="TEST-RECORD",
        record_length=30,
        copybook="TEST.cpy",
        fields=[
            FieldDefinition("ID", 0, 5, "9(05)", FieldType.NUMERIC_DISPLAY),
            FieldDefinition("NAME", 5, 10, "X(10)", FieldType.ALPHANUMERIC),
            FieldDefinition("AMOUNT", 15, 12, "S9(10)V99",
                            FieldType.SIGNED_DECIMAL, scale=2),
            FieldDefinition("FILLER", 27, 3, "X(03)", FieldType.FILLER),
        ],
    )


class TestCompareRecords:
    def test_identical_records(self, simple_layout):
        line = "00001TestName  00000001000{   "
        mismatches = compare_records(line, line, simple_layout, 1)
        assert len(mismatches) == 0

    def test_field_mismatch(self, simple_layout):
        expected = "00001TestName  00000001000{   "
        actual   = "00001TestName  00000002000{   "
        mismatches = compare_records(expected, actual, simple_layout, 1)
        assert len(mismatches) == 1
        assert mismatches[0].field_name == "AMOUNT"
        assert mismatches[0].expected_decoded == "100.00"
        assert mismatches[0].actual_decoded == "200.00"

    def test_multiple_mismatches(self, simple_layout):
        expected = "00001TestName  00000001000{   "
        actual   = "00002OtherName 00000002000}   "
        mismatches = compare_records(expected, actual, simple_layout, 1)
        assert len(mismatches) == 3  # ID, NAME, AMOUNT

    def test_filler_ignored(self, simple_layout):
        expected = "00001TestName  00000001000{abc"
        actual   = "00001TestName  00000001000{xyz"
        mismatches = compare_records(expected, actual, simple_layout, 1)
        assert len(mismatches) == 0  # Filler differences are ignored


class TestCompareFiles:
    def test_matching_files(self, simple_layout):
        with tempfile.NamedTemporaryFile(mode='w', suffix='.txt', delete=False) as f1, \
             tempfile.NamedTemporaryFile(mode='w', suffix='.txt', delete=False) as f2:
            content = "00001TestName  00000001000{   \n00002OtherName 00000002000{   \n"
            f1.write(content)
            f2.write(content)
            f1.flush()
            f2.flush()

            result = compare_files(f1.name, f2.name, simple_layout)
            assert result.status == "MATCH"
            assert result.matching_records == 2
            assert result.mismatched_records == 0

    def test_mismatched_files(self, simple_layout):
        with tempfile.NamedTemporaryFile(mode='w', suffix='.txt', delete=False) as f1, \
             tempfile.NamedTemporaryFile(mode='w', suffix='.txt', delete=False) as f2:
            f1.write("00001TestName  00000001000{   \n")
            f2.write("00001TestName  00000002000{   \n")
            f1.flush()
            f2.flush()

            result = compare_files(f1.name, f2.name, simple_layout)
            assert result.status == "FIELD_MISMATCH"
            assert result.mismatched_records == 1
            assert len(result.mismatches) == 1
            assert result.mismatches[0].field_name == "AMOUNT"

    def test_record_count_mismatch(self, simple_layout):
        with tempfile.NamedTemporaryFile(mode='w', suffix='.txt', delete=False) as f1, \
             tempfile.NamedTemporaryFile(mode='w', suffix='.txt', delete=False) as f2:
            f1.write("00001TestName  00000001000{   \n00002Name2     00000002000{   \n")
            f2.write("00001TestName  00000001000{   \n")
            f1.flush()
            f2.flush()

            result = compare_files(f1.name, f2.name, simple_layout)
            assert result.status == "RECORD_COUNT_MISMATCH"
            assert result.total_records_expected == 2
            assert result.total_records_actual == 1


class TestComparisonResult:
    def test_json_output(self):
        result = ComparisonResult(
            expected_file="expected.txt",
            actual_file="actual.txt",
            layout_name="TEST",
            total_records_expected=1,
            total_records_actual=1,
            matching_records=0,
            mismatched_records=1,
            mismatches=[
                FieldMismatch(
                    record_number=1,
                    field_name="AMOUNT",
                    byte_offset=15,
                    byte_length=12,
                    pic_clause="S9(10)V99",
                    expected_raw="00000001000{",
                    actual_raw="00000002000{",
                    expected_decoded="100.00",
                    actual_decoded="200.00",
                ),
            ],
        )
        output = result.to_json()
        assert '"status": "FIELD_MISMATCH"' in output
        assert '"field": "AMOUNT"' in output

    def test_summary(self):
        result = ComparisonResult(
            expected_file="expected.txt",
            actual_file="actual.txt",
            layout_name="TEST",
            total_records_expected=10,
            total_records_actual=10,
            matching_records=10,
        )
        assert "MATCH" in result.summary()
