"""Tests for the reconciliation check functions."""

from decimal import Decimal
from pathlib import Path

import pytest

from carddemo_harness.reconciliation import (
    check_record_count,
    check_numeric_sum,
    check_key_uniqueness,
    check_referential_integrity,
    compute_file_checksum,
    run_full_reconciliation,
)


class TestRecordCount:
    """Tests for record count validation."""

    def test_acctdata_count(self, data_dir):
        result = check_record_count(data_dir / "acctdata.txt", 50)
        assert result.passed
        assert result.details["actual"] == 50

    def test_dailytran_count(self, data_dir):
        result = check_record_count(data_dir / "dailytran.txt", 300)
        assert result.passed

    def test_wrong_count_fails(self, data_dir):
        result = check_record_count(data_dir / "acctdata.txt", 99)
        assert not result.passed


class TestNumericSum:
    """Tests for numeric field sum validation."""

    def test_acct_balance_sum(self, data_dir):
        result = check_numeric_sum(data_dir / "acctdata.txt", "ACCT-CURR-BAL")
        assert result.passed
        assert "Sum(ACCT-CURR-BAL)" in result.message

    def test_acct_balance_sum_wrong_expected(self, data_dir):
        result = check_numeric_sum(
            data_dir / "acctdata.txt", "ACCT-CURR-BAL", Decimal("999999.99")
        )
        assert not result.passed

    def test_dailytran_amount_sum(self, data_dir):
        result = check_numeric_sum(data_dir / "dailytran.txt", "DALYTRAN-AMT")
        assert result.passed
        assert Decimal(result.details["sum"]) != Decimal("0")

    def test_tcatbal_sum(self, data_dir):
        result = check_numeric_sum(
            data_dir / "tcatbal.txt", "TRAN-CAT-BAL", Decimal("0")
        )
        assert result.passed


class TestKeyUniqueness:
    """Tests for primary key uniqueness validation."""

    def test_acctdata_unique(self, data_dir):
        result = check_key_uniqueness(data_dir / "acctdata.txt")
        assert result.passed

    def test_carddata_unique(self, data_dir):
        result = check_key_uniqueness(data_dir / "carddata.txt")
        assert result.passed

    def test_cardxref_unique(self, data_dir):
        result = check_key_uniqueness(data_dir / "cardxref.txt")
        assert result.passed

    def test_discgrp_composite_key_unique(self, data_dir):
        result = check_key_uniqueness(data_dir / "discgrp.txt")
        assert result.passed

    def test_trancatg_composite_key_unique(self, data_dir):
        result = check_key_uniqueness(data_dir / "trancatg.txt")
        assert result.passed

    def test_trantype_unique(self, data_dir):
        result = check_key_uniqueness(data_dir / "trantype.txt")
        assert result.passed


class TestReferentialIntegrity:
    """Tests for cross-file referential integrity."""

    def test_xref_to_account(self, data_dir):
        result = check_referential_integrity(
            data_dir / "cardxref.txt", "XREF-ACCT-ID",
            data_dir / "acctdata.txt", "ACCT-ID",
        )
        assert result.passed

    def test_xref_to_customer(self, data_dir):
        result = check_referential_integrity(
            data_dir / "cardxref.txt", "XREF-CUST-ID",
            data_dir / "custdata.txt", "CUST-ID",
        )
        assert result.passed

    def test_card_to_xref(self, data_dir):
        result = check_referential_integrity(
            data_dir / "carddata.txt", "CARD-NUM",
            data_dir / "cardxref.txt", "XREF-CARD-NUM",
        )
        assert result.passed

    def test_dailytran_to_xref(self, data_dir):
        result = check_referential_integrity(
            data_dir / "dailytran.txt", "DALYTRAN-CARD-NUM",
            data_dir / "cardxref.txt", "XREF-CARD-NUM",
        )
        assert result.passed

    def test_tcatbal_to_account(self, data_dir):
        result = check_referential_integrity(
            data_dir / "tcatbal.txt", "TRANCAT-ACCT-ID",
            data_dir / "acctdata.txt", "ACCT-ID",
        )
        assert result.passed


class TestChecksum:
    """Tests for file checksum computation."""

    def test_checksum_deterministic(self, data_dir):
        c1 = compute_file_checksum(data_dir / "acctdata.txt")
        c2 = compute_file_checksum(data_dir / "acctdata.txt")
        assert c1 == c2
        assert len(c1) == 32  # MD5 hex digest

    def test_different_files_different_checksums(self, data_dir):
        c1 = compute_file_checksum(data_dir / "acctdata.txt")
        c2 = compute_file_checksum(data_dir / "carddata.txt")
        assert c1 != c2


class TestFullReconciliation:
    """Tests for the complete reconciliation suite."""

    def test_all_checks_pass(self, data_dir):
        report = run_full_reconciliation(data_dir)
        assert report.passed, f"Reconciliation failed:\n{report.summary()}"
        assert report.pass_count == 27
        assert report.fail_count == 0

    def test_summary_format(self, data_dir):
        report = run_full_reconciliation(data_dir)
        summary = report.summary()
        assert "ALL PASS" in summary
        assert "Passed:" in summary
