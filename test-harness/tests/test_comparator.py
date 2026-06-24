"""Tests for the field-by-field comparison utility."""

from decimal import Decimal

import pytest

from carddemo_harness.comparator import compare_records, FieldMismatch, ComparisonResult
from carddemo_harness.copybook_layouts import ACCOUNT_LAYOUT, TRAN_TYPE_LAYOUT


class TestCompareRecords:
    """Tests for record comparison logic."""

    def test_identical_records_pass(self):
        records = [
            {"ACCT-ID": 1, "ACCT-ACTIVE-STATUS": "Y", "ACCT-CURR-BAL": Decimal("194.00"),
             "ACCT-CREDIT-LIMIT": Decimal("2020.00"), "ACCT-CASH-CREDIT-LIMIT": Decimal("1020.00"),
             "ACCT-OPEN-DATE": "2014-11-20", "ACCT-EXPIRAION-DATE": "2025-05-20",
             "ACCT-REISSUE-DATE": "2025-05-20", "ACCT-CURR-CYC-CREDIT": Decimal("0.00"),
             "ACCT-CURR-CYC-DEBIT": Decimal("0.00"), "ACCT-ADDR-ZIP": "A000000000",
             "ACCT-GROUP-ID": ""},
        ]
        result = compare_records(records, records.copy(), ACCOUNT_LAYOUT)
        assert result.passed
        assert result.matched == 1
        assert result.field_mismatches == []

    def test_decimal_mismatch_detected(self):
        expected = [
            {"ACCT-ID": 1, "ACCT-ACTIVE-STATUS": "Y", "ACCT-CURR-BAL": Decimal("194.00"),
             "ACCT-CREDIT-LIMIT": Decimal("2020.00"), "ACCT-CASH-CREDIT-LIMIT": Decimal("1020.00"),
             "ACCT-OPEN-DATE": "2014-11-20", "ACCT-EXPIRAION-DATE": "2025-05-20",
             "ACCT-REISSUE-DATE": "2025-05-20", "ACCT-CURR-CYC-CREDIT": Decimal("0.00"),
             "ACCT-CURR-CYC-DEBIT": Decimal("0.00"), "ACCT-ADDR-ZIP": "A000000000",
             "ACCT-GROUP-ID": ""},
        ]
        actual = [
            {"ACCT-ID": 1, "ACCT-ACTIVE-STATUS": "Y", "ACCT-CURR-BAL": Decimal("195.00"),
             "ACCT-CREDIT-LIMIT": Decimal("2020.00"), "ACCT-CASH-CREDIT-LIMIT": Decimal("1020.00"),
             "ACCT-OPEN-DATE": "2014-11-20", "ACCT-EXPIRAION-DATE": "2025-05-20",
             "ACCT-REISSUE-DATE": "2025-05-20", "ACCT-CURR-CYC-CREDIT": Decimal("0.00"),
             "ACCT-CURR-CYC-DEBIT": Decimal("0.00"), "ACCT-ADDR-ZIP": "A000000000",
             "ACCT-GROUP-ID": ""},
        ]
        result = compare_records(expected, actual, ACCOUNT_LAYOUT)
        assert not result.passed
        assert result.mismatched_records == 1
        assert len(result.field_mismatches) == 1
        assert result.field_mismatches[0].field_name == "ACCT-CURR-BAL"
        assert result.field_mismatches[0].expected == Decimal("194.00")
        assert result.field_mismatches[0].actual == Decimal("195.00")

    def test_missing_key_detected(self):
        expected = [
            {"ACCT-ID": 1, "ACCT-ACTIVE-STATUS": "Y", "ACCT-CURR-BAL": Decimal("0.00"),
             "ACCT-CREDIT-LIMIT": Decimal("0.00"), "ACCT-CASH-CREDIT-LIMIT": Decimal("0.00"),
             "ACCT-OPEN-DATE": "", "ACCT-EXPIRAION-DATE": "", "ACCT-REISSUE-DATE": "",
             "ACCT-CURR-CYC-CREDIT": Decimal("0.00"), "ACCT-CURR-CYC-DEBIT": Decimal("0.00"),
             "ACCT-ADDR-ZIP": "", "ACCT-GROUP-ID": ""},
            {"ACCT-ID": 2, "ACCT-ACTIVE-STATUS": "Y", "ACCT-CURR-BAL": Decimal("0.00"),
             "ACCT-CREDIT-LIMIT": Decimal("0.00"), "ACCT-CASH-CREDIT-LIMIT": Decimal("0.00"),
             "ACCT-OPEN-DATE": "", "ACCT-EXPIRAION-DATE": "", "ACCT-REISSUE-DATE": "",
             "ACCT-CURR-CYC-CREDIT": Decimal("0.00"), "ACCT-CURR-CYC-DEBIT": Decimal("0.00"),
             "ACCT-ADDR-ZIP": "", "ACCT-GROUP-ID": ""},
        ]
        actual = [expected[0]]  # only first record
        result = compare_records(expected, actual, ACCOUNT_LAYOUT)
        assert not result.passed
        assert len(result.missing_keys) == 1
        assert "2" in result.missing_keys

    def test_extra_key_detected(self):
        rec = {"ACCT-ID": 1, "ACCT-ACTIVE-STATUS": "Y", "ACCT-CURR-BAL": Decimal("0.00"),
               "ACCT-CREDIT-LIMIT": Decimal("0.00"), "ACCT-CASH-CREDIT-LIMIT": Decimal("0.00"),
               "ACCT-OPEN-DATE": "", "ACCT-EXPIRAION-DATE": "", "ACCT-REISSUE-DATE": "",
               "ACCT-CURR-CYC-CREDIT": Decimal("0.00"), "ACCT-CURR-CYC-DEBIT": Decimal("0.00"),
               "ACCT-ADDR-ZIP": "", "ACCT-GROUP-ID": ""}
        extra = {"ACCT-ID": 99, "ACCT-ACTIVE-STATUS": "N", "ACCT-CURR-BAL": Decimal("0.00"),
                 "ACCT-CREDIT-LIMIT": Decimal("0.00"), "ACCT-CASH-CREDIT-LIMIT": Decimal("0.00"),
                 "ACCT-OPEN-DATE": "", "ACCT-EXPIRAION-DATE": "", "ACCT-REISSUE-DATE": "",
                 "ACCT-CURR-CYC-CREDIT": Decimal("0.00"), "ACCT-CURR-CYC-DEBIT": Decimal("0.00"),
                 "ACCT-ADDR-ZIP": "", "ACCT-GROUP-ID": ""}
        result = compare_records([rec], [rec, extra], ACCOUNT_LAYOUT)
        assert not result.passed
        assert len(result.extra_keys) == 1

    def test_string_trim_comparison(self):
        expected = [{"TRAN-TYPE": "01", "TRAN-TYPE-DESC": "Purchase"}]
        actual = [{"TRAN-TYPE": "01", "TRAN-TYPE-DESC": "Purchase   "}]
        result = compare_records(expected, actual, TRAN_TYPE_LAYOUT)
        assert result.passed

    def test_summary_format(self):
        rec = {"ACCT-ID": 1, "ACCT-ACTIVE-STATUS": "Y", "ACCT-CURR-BAL": Decimal("0.00"),
               "ACCT-CREDIT-LIMIT": Decimal("0.00"), "ACCT-CASH-CREDIT-LIMIT": Decimal("0.00"),
               "ACCT-OPEN-DATE": "", "ACCT-EXPIRAION-DATE": "", "ACCT-REISSUE-DATE": "",
               "ACCT-CURR-CYC-CREDIT": Decimal("0.00"), "ACCT-CURR-CYC-DEBIT": Decimal("0.00"),
               "ACCT-ADDR-ZIP": "", "ACCT-GROUP-ID": ""}
        result = compare_records([rec], [rec], ACCOUNT_LAYOUT)
        summary = result.summary()
        assert "PASS" in summary
        assert "Expected records: 1" in summary
