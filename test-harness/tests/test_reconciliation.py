"""Tests for the reconciliation check functions."""

import tempfile
from decimal import Decimal
from pathlib import Path

import pytest

import sys
sys.path.insert(0, str(Path(__file__).resolve().parent.parent))

from reconciliation import (
    ReconciliationReport,
    check_card_xref_coverage,
    check_field_sum,
    check_file_size,
    check_record_count,
    check_transaction_card_refs,
    check_xref_accounts,
    check_xref_customers,
    compute_field_sums,
    reconcile_sample_data,
)


class TestRecordCount:
    def test_correct_count(self, tmp_path):
        f = tmp_path / "test.txt"
        f.write_text("line1\nline2\nline3\n")
        result = check_record_count(f, 3)
        assert result.status == "PASS"

    def test_incorrect_count(self, tmp_path):
        f = tmp_path / "test.txt"
        f.write_text("line1\nline2\n")
        result = check_record_count(f, 3)
        assert result.status == "FAIL"

    def test_file_not_found(self):
        result = check_record_count("/nonexistent/file.txt", 10)
        assert result.status == "FAIL"
        assert "FILE_NOT_FOUND" in result.actual


class TestFileSize:
    def test_binary_size(self, tmp_path):
        f = tmp_path / "test.bin"
        f.write_bytes(b'\x00' * 300)  # 1 record * 300 bytes
        result = check_file_size(f, 300, 1)
        assert result.status == "PASS"

    def test_text_size(self, tmp_path):
        f = tmp_path / "test.txt"
        f.write_text("x" * 300 + "\n")
        result = check_file_size(f, 300, 1)
        assert result.status == "PASS"

    def test_wrong_size(self, tmp_path):
        f = tmp_path / "test.bin"
        f.write_bytes(b'\x00' * 250)
        result = check_file_size(f, 300, 1)
        assert result.status == "FAIL"


class TestFieldSum:
    def test_correct_sum(self):
        records = [
            {"AMT": "100.00"},
            {"AMT": "200.00"},
            {"AMT": "-50.00"},
        ]
        result = check_field_sum(records, "AMT", Decimal("250.00"))
        assert result.status == "PASS"

    def test_incorrect_sum(self):
        records = [{"AMT": "100.00"}, {"AMT": "200.00"}]
        result = check_field_sum(records, "AMT", Decimal("999.00"))
        assert result.status == "FAIL"

    def test_with_tolerance(self):
        records = [{"AMT": "100.01"}]
        result = check_field_sum(records, "AMT", Decimal("100.00"),
                                 tolerance=Decimal("0.01"))
        assert result.status == "PASS"


class TestComputeFieldSums:
    def test_basic_sums(self):
        records = [
            {"BAL": "100.00", "CREDIT": "50.00"},
            {"BAL": "200.00", "CREDIT": "75.00"},
        ]
        sums = compute_field_sums(records, ["BAL", "CREDIT"])
        assert sums["BAL"] == Decimal("300.00")
        assert sums["CREDIT"] == Decimal("125.00")

    def test_missing_field(self):
        records = [{"BAL": "100.00"}, {"BAL": "200.00"}]
        sums = compute_field_sums(records, ["BAL", "MISSING"])
        assert sums["BAL"] == Decimal("300.00")
        assert sums["MISSING"] == Decimal("0")


class TestXrefIntegrity:
    def test_all_accounts_exist(self):
        xref = [{"XREF-CARD-NUM": "CARD1", "XREF-ACCT-ID": "00000000001"}]
        accts = [{"ACCT-ID": "00000000001"}]
        result = check_xref_accounts(xref, accts)
        assert result.status == "PASS"

    def test_orphaned_account(self):
        xref = [{"XREF-CARD-NUM": "CARD1", "XREF-ACCT-ID": "00000000999"}]
        accts = [{"ACCT-ID": "00000000001"}]
        result = check_xref_accounts(xref, accts)
        assert result.status == "FAIL"

    def test_all_customers_exist(self):
        xref = [{"XREF-CARD-NUM": "CARD1", "XREF-CUST-ID": "000000001"}]
        custs = [{"CUST-ID": "000000001"}]
        result = check_xref_customers(xref, custs)
        assert result.status == "PASS"

    def test_orphaned_customer(self):
        xref = [{"XREF-CARD-NUM": "CARD1", "XREF-CUST-ID": "000000999"}]
        custs = [{"CUST-ID": "000000001"}]
        result = check_xref_customers(xref, custs)
        assert result.status == "FAIL"


class TestCardXrefCoverage:
    def test_all_cards_covered(self):
        cards = [{"CARD-NUM": "CARD1"}, {"CARD-NUM": "CARD2"}]
        xref = [{"XREF-CARD-NUM": "CARD1"}, {"XREF-CARD-NUM": "CARD2"}]
        result = check_card_xref_coverage(cards, xref)
        assert result.status == "PASS"

    def test_uncovered_card(self):
        cards = [{"CARD-NUM": "CARD1"}, {"CARD-NUM": "CARD3"}]
        xref = [{"XREF-CARD-NUM": "CARD1"}]
        result = check_card_xref_coverage(cards, xref)
        assert result.status == "FAIL"


class TestTransactionCardRefs:
    def test_all_refs_valid(self):
        trans = [
            {"TRAN-CARD-NUM": "CARD1"},
            {"TRAN-CARD-NUM": "CARD2"},
        ]
        xref = [
            {"XREF-CARD-NUM": "CARD1"},
            {"XREF-CARD-NUM": "CARD2"},
        ]
        result = check_transaction_card_refs(trans, xref)
        assert result.status == "PASS"

    def test_unknown_card(self):
        trans = [{"TRAN-CARD-NUM": "CARD999"}]
        xref = [{"XREF-CARD-NUM": "CARD1"}]
        result = check_transaction_card_refs(trans, xref)
        assert result.status == "FAIL"


class TestReconcileSampleData:
    def test_full_reconciliation(self):
        """Run reconciliation against the actual sample data."""
        repo_root = Path(__file__).resolve().parent.parent.parent
        data_dir = repo_root / "app" / "data" / "ASCII"
        golden_dir = repo_root / "golden-files"

        if not data_dir.exists() or not golden_dir.exists():
            pytest.skip("Sample data or golden files not available")

        report = reconcile_sample_data(data_dir)
        print(report.summary())

        # Record counts
        assert report.status == "PASS", f"Reconciliation failed:\n{report.summary()}"

        # Verify specific checks passed
        check_names = [c.name for c in report.checks]
        assert "XREF->Account integrity" in check_names
        assert "XREF->Customer integrity" in check_names
        assert "Card->XREF coverage" in check_names
        assert "Transaction->XREF card integrity" in check_names
        assert "Account balance sum" in check_names
        assert "Transaction amount sum" in check_names


class TestReconciliationReport:
    def test_report_structure(self):
        report = ReconciliationReport(job_name="TEST")
        from reconciliation import ReconciliationCheck
        report.add(ReconciliationCheck("Check1", "PASS", "10", "10"))
        report.add(ReconciliationCheck("Check2", "FAIL", "20", "15"))

        assert report.passed == 1
        assert report.failed == 1
        assert report.status == "FAIL"

        d = report.to_dict()
        assert d["job"] == "TEST"
        assert len(d["checks"]) == 2
