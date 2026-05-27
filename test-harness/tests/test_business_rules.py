"""Tests for business-specific validation rules."""

import pytest
from decimal import Decimal
from pathlib import Path

from test_harness.business_rules import (
    CYCDEBIT_SUBSTITUTION_VALUE,
    ARRAY_FIXED_VALUES,
    check_cycdebit_substitution,
    check_date_reformatting,
    check_array_fixed_values,
    check_vbr_record_pairs,
    check_posttran_record_conservation,
    check_posttran_balance_integrity,
    check_no_duplicate_tran_ids,
    calculate_interest_truncated,
    check_interest_truncation,
    check_max_field_values,
    check_active_status_valid,
    check_date_validity,
    check_credit_limit_consistency,
    check_balance_within_limit,
)
from test_harness.copybook_layouts import ACCOUNT_RECORD
from test_harness.parser import parse_data_file

DATA_DIR = Path(__file__).parent.parent.parent / "app" / "data" / "ASCII"


class TestCycDebitSubstitution:
    """CBACT01C: when cycDebit=0, output must be 2525.00."""

    def test_zero_input_produces_2525(self):
        inp = [{"ACCT-CURR-CYC-DEBIT": Decimal("0.00")}]
        out = [{"OUT-ACCT-CURR-CYC-DEBIT": Decimal("2525.00")}]
        result = check_cycdebit_substitution(inp, out)
        assert result.passed

    def test_zero_input_wrong_output_fails(self):
        inp = [{"ACCT-CURR-CYC-DEBIT": Decimal("0.00")}]
        out = [{"OUT-ACCT-CURR-CYC-DEBIT": Decimal("0.00")}]
        result = check_cycdebit_substitution(inp, out)
        assert not result.passed

    def test_nonzero_input_passes_through(self):
        inp = [{"ACCT-CURR-CYC-DEBIT": Decimal("150.00")}]
        out = [{"OUT-ACCT-CURR-CYC-DEBIT": Decimal("150.00")}]
        result = check_cycdebit_substitution(inp, out)
        assert result.passed

    def test_nonzero_input_changed_fails(self):
        inp = [{"ACCT-CURR-CYC-DEBIT": Decimal("150.00")}]
        out = [{"OUT-ACCT-CURR-CYC-DEBIT": Decimal("2525.00")}]
        result = check_cycdebit_substitution(inp, out)
        assert not result.passed

    @pytest.mark.skipif(not DATA_DIR.exists(), reason="Data dir not available")
    def test_all_sample_accounts_have_zero_cycdebit(self):
        """All 50 sample accounts have cycDebit=0 → should all get 2525.00."""
        records = parse_data_file(DATA_DIR / "acctdata.txt")
        for r in records:
            assert r["ACCT-CURR-CYC-DEBIT"] == Decimal("0.00"), (
                f"Account {r['ACCT-ID']} has non-zero cycDebit"
            )

    def test_substitution_value_is_correct(self):
        assert CYCDEBIT_SUBSTITUTION_VALUE == Decimal("2525.00")


class TestDateReformatting:
    """CBACT01C: dates reformatted from YYYY-MM-DD to YYYYMMDD."""

    def test_standard_date_reformatting(self):
        inp = [{"ACCT-OPEN-DATE": "2014-11-20"}]
        out = [{"OUT-ACCT-OPEN-DATE": "20141120"}]
        result = check_date_reformatting(
            inp, out,
            date_fields=[("ACCT-OPEN-DATE", "OUT-ACCT-OPEN-DATE")]
        )
        assert result.passed

    def test_date_with_hyphens_not_removed_fails(self):
        inp = [{"ACCT-OPEN-DATE": "2014-11-20"}]
        out = [{"OUT-ACCT-OPEN-DATE": "2014-11-20"}]
        result = check_date_reformatting(
            inp, out,
            date_fields=[("ACCT-OPEN-DATE", "OUT-ACCT-OPEN-DATE")]
        )
        assert not result.passed


class TestArrayFixedValues:
    """CBACT01C: array output always contains 5 fixed values."""

    def test_correct_values(self):
        rec = {
            "ARRAY-SLOT-1": Decimal("1005.00"),
            "ARRAY-SLOT-2": Decimal("1525.00"),
            "ARRAY-SLOT-3": Decimal("-1025.00"),
            "ARRAY-SLOT-4": Decimal("-2500.00"),
            "ARRAY-SLOT-5": Decimal("0.00"),
        }
        result = check_array_fixed_values([rec])
        assert result.passed

    def test_wrong_value_fails(self):
        rec = {
            "ARRAY-SLOT-1": Decimal("999.00"),
            "ARRAY-SLOT-2": Decimal("1525.00"),
            "ARRAY-SLOT-3": Decimal("-1025.00"),
            "ARRAY-SLOT-4": Decimal("-2500.00"),
            "ARRAY-SLOT-5": Decimal("0.00"),
        }
        result = check_array_fixed_values([rec])
        assert not result.passed

    def test_expected_array_values(self):
        assert ARRAY_FIXED_VALUES == [
            Decimal("1005.00"),
            Decimal("1525.00"),
            Decimal("-1025.00"),
            Decimal("-2500.00"),
            Decimal("0.00"),
        ]


class TestVBRRecordPairs:
    """CBACT01C: VBR records come in Type1/Type2 pairs."""

    def test_valid_pairs(self):
        vbr = [
            {"VBR-ACCT-ID": "001"},
            {"VBR-ACCT-ID": "001"},
            {"VBR-ACCT-ID": "002"},
            {"VBR-ACCT-ID": "002"},
        ]
        result = check_vbr_record_pairs(vbr)
        assert result.passed

    def test_odd_count_fails(self):
        vbr = [{"VBR-ACCT-ID": "001"}]
        result = check_vbr_record_pairs(vbr)
        assert not result.passed

    def test_mismatched_pair_fails(self):
        vbr = [
            {"VBR-ACCT-ID": "001"},
            {"VBR-ACCT-ID": "002"},
        ]
        result = check_vbr_record_pairs(vbr)
        assert not result.passed


class TestPosttranRecordConservation:
    """POSTTRAN: every input must be posted or rejected."""

    def test_conservation_passes(self):
        result = check_posttran_record_conservation(300, 280, 20)
        assert result.passed

    def test_conservation_fails_missing(self):
        result = check_posttran_record_conservation(300, 280, 10)
        assert not result.passed
        assert "Missing: 10" in result.details

    def test_conservation_fails_extra(self):
        result = check_posttran_record_conservation(300, 290, 20)
        assert not result.passed


class TestPosttranBalanceIntegrity:
    """POSTTRAN: per-account balance delta = sum of posted amounts."""

    def test_balance_matches(self):
        before = {"001": Decimal("100.00"), "002": Decimal("200.00")}
        after = {"001": Decimal("150.00"), "002": Decimal("175.00")}
        posted = {"001": Decimal("50.00"), "002": Decimal("-25.00")}
        result = check_posttran_balance_integrity(before, after, posted)
        assert result.passed

    def test_balance_mismatch(self):
        before = {"001": Decimal("100.00")}
        after = {"001": Decimal("160.00")}
        posted = {"001": Decimal("50.00")}
        result = check_posttran_balance_integrity(before, after, posted)
        assert not result.passed


class TestNoDuplicateTranIds:
    """Verify all transaction IDs are unique."""

    def test_unique_ids_pass(self):
        result = check_no_duplicate_tran_ids(["T001", "T002", "T003"])
        assert result.passed

    def test_duplicate_ids_fail(self):
        result = check_no_duplicate_tran_ids(["T001", "T002", "T001"])
        assert not result.passed
        assert "1 duplicates" in result.message


class TestInterestTruncation:
    """CBACT04C: interest must use TRUNCATE (RoundingMode.DOWN), not ROUND."""

    def test_truncation_not_rounding(self):
        # balance=1000.00, rate=15.00 → monthly = 1000 * 15 / 1200 = 12.50
        result = calculate_interest_truncated(Decimal("1000.00"), Decimal("15.00"))
        assert result == Decimal("12.50")

    def test_truncation_drops_fractional_cents(self):
        # balance=333.33, rate=7.50 → monthly = 333.33 * 7.50 / 1200 = 2.0833125
        # TRUNCATE → 2.08 (not 2.09 which would be rounded)
        result = calculate_interest_truncated(Decimal("333.33"), Decimal("7.50"))
        assert result == Decimal("2.08")

    def test_truncation_exact_penny(self):
        # balance=1200.00, rate=12.00 → monthly = 1200 * 12 / 1200 = 12.00
        result = calculate_interest_truncated(Decimal("1200.00"), Decimal("12.00"))
        assert result == Decimal("12.00")

    def test_zero_balance_no_interest(self):
        result = calculate_interest_truncated(Decimal("0.00"), Decimal("15.00"))
        assert result == Decimal("0.00")

    def test_zero_rate_no_interest(self):
        result = calculate_interest_truncated(Decimal("1000.00"), Decimal("0.00"))
        assert result == Decimal("0.00")

    def test_large_balance_truncation(self):
        # balance=99999.99, rate=24.99 → monthly = 99999.99 * 24.99 / 1200 = 2083.324..
        result = calculate_interest_truncated(Decimal("99999.99"), Decimal("24.99"))
        expected = (Decimal("99999.99") * Decimal("24.99") / Decimal("1200")).quantize(
            Decimal("0.01"), rounding="ROUND_DOWN"
        )
        assert result == expected

    def test_rounding_would_give_different_result(self):
        # Specifically choose values where truncation ≠ rounding
        # balance=100.00, rate=7.00 → monthly = 100*7/1200 = 0.58333...
        # TRUNCATE → 0.58, ROUND_HALF_UP → 0.58 (same here, try another)
        # balance=100.00, rate=8.00 → monthly = 100*8/1200 = 0.66666...
        # TRUNCATE → 0.66, ROUND_HALF_UP → 0.67
        result = calculate_interest_truncated(Decimal("100.00"), Decimal("8.00"))
        assert result == Decimal("0.66")
        # Verify rounding would give a different answer
        rounded = (Decimal("100.00") * Decimal("8.00") / Decimal("1200")).quantize(
            Decimal("0.01"), rounding="ROUND_HALF_UP"
        )
        assert rounded == Decimal("0.67")
        assert result != rounded


class TestEdgeCaseValidations:
    """Edge case validators for data quality."""

    @pytest.mark.skipif(not DATA_DIR.exists(), reason="Data dir not available")
    def test_account_status_values_valid(self):
        records = parse_data_file(DATA_DIR / "acctdata.txt")
        result = check_active_status_valid(records, "ACCT-ACTIVE-STATUS")
        assert result.passed

    @pytest.mark.skipif(not DATA_DIR.exists(), reason="Data dir not available")
    def test_card_status_values_valid(self):
        records = parse_data_file(DATA_DIR / "carddata.txt", "carddata")
        result = check_active_status_valid(records, "CARD-ACTIVE-STATUS")
        assert result.passed

    @pytest.mark.skipif(not DATA_DIR.exists(), reason="Data dir not available")
    def test_account_dates_valid(self):
        records = parse_data_file(DATA_DIR / "acctdata.txt")
        result = check_date_validity(
            records,
            ["ACCT-OPEN-DATE", "ACCT-EXPIRAION-DATE", "ACCT-REISSUE-DATE"]
        )
        assert result.passed, result.details

    @pytest.mark.skipif(not DATA_DIR.exists(), reason="Data dir not available")
    def test_customer_dob_valid(self):
        records = parse_data_file(DATA_DIR / "custdata.txt", "custdata")
        result = check_date_validity(records, ["CUST-DOB-YYYY-MM-DD"])
        assert result.passed, result.details

    @pytest.mark.skipif(not DATA_DIR.exists(), reason="Data dir not available")
    def test_credit_limit_consistency(self):
        records = parse_data_file(DATA_DIR / "acctdata.txt")
        result = check_credit_limit_consistency(records)
        assert result.passed, result.details

    @pytest.mark.skipif(not DATA_DIR.exists(), reason="Data dir not available")
    def test_balance_within_limit(self):
        records = parse_data_file(DATA_DIR / "acctdata.txt")
        result = check_balance_within_limit(records)
        # This is a warning check, always passes
        assert result.passed

    @pytest.mark.skipif(not DATA_DIR.exists(), reason="Data dir not available")
    def test_max_field_values(self):
        records = parse_data_file(DATA_DIR / "acctdata.txt")
        result = check_max_field_values(records, ACCOUNT_RECORD)
        assert result.passed, result.details

    def test_date_validity_catches_invalid_month(self):
        records = [{"BAD-DATE": "2025-13-01"}]
        result = check_date_validity(records, ["BAD-DATE"])
        assert not result.passed

    def test_date_validity_catches_feb_30(self):
        records = [{"BAD-DATE": "2025-02-30"}]
        result = check_date_validity(records, ["BAD-DATE"])
        assert not result.passed

    def test_date_validity_catches_feb_29_non_leap(self):
        records = [{"BAD-DATE": "2025-02-29"}]
        result = check_date_validity(records, ["BAD-DATE"])
        assert not result.passed

    def test_date_validity_allows_feb_29_leap_year(self):
        records = [{"GOOD-DATE": "2024-02-29"}]
        result = check_date_validity(records, ["GOOD-DATE"])
        assert result.passed

    def test_date_validity_catches_day_31_in_april(self):
        records = [{"BAD-DATE": "2025-04-31"}]
        result = check_date_validity(records, ["BAD-DATE"])
        assert not result.passed

    def test_status_validator_catches_invalid(self):
        records = [{"STATUS": "X"}]
        result = check_active_status_valid(records, "STATUS")
        assert not result.passed

    def test_status_validator_accepts_Y_and_N(self):
        records = [{"STATUS": "Y"}, {"STATUS": "N"}]
        result = check_active_status_valid(records, "STATUS")
        assert result.passed
