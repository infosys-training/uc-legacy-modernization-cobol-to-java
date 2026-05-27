"""Tests for the COBOL fixed-width file parser."""

import pytest
from decimal import Decimal
from pathlib import Path

from test_harness.parser import (
    decode_zoned_decimal,
    parse_field,
    parse_record,
    parse_data_file,
)
from test_harness.copybook_layouts import (
    ACCOUNT_RECORD,
    CARD_RECORD,
    CARD_XREF_RECORD,
    CUSTOMER_RECORD,
    DAILY_TRANSACTION_RECORD,
    DISCLOSURE_GROUP_RECORD,
    TRAN_CAT_BAL_RECORD,
    TRAN_TYPE_RECORD,
    TRAN_CATEGORY_RECORD,
)

DATA_DIR = Path(__file__).parent.parent.parent / "app" / "data" / "ASCII"


class TestZonedDecimal:
    """Tests for COBOL zoned decimal parsing with sign overpunch."""

    def test_positive_zero(self):
        assert decode_zoned_decimal("00000000000{", "S9(10)V99") == Decimal("0.00")

    def test_positive_value(self):
        # 00000001940{ -> 0000000194.00
        assert decode_zoned_decimal("00000001940{", "S9(10)V99") == Decimal("194.00")

    def test_positive_A_through_I(self):
        assert decode_zoned_decimal("00000001580A", "S9(10)V99") == Decimal("158.01")
        assert decode_zoned_decimal("00000001580B", "S9(10)V99") == Decimal("158.02")
        assert decode_zoned_decimal("00000001580I", "S9(10)V99") == Decimal("158.09")

    def test_negative_zero(self):
        assert decode_zoned_decimal("00000000000}", "S9(10)V99") == Decimal("0.00")

    def test_negative_value(self):
        assert decode_zoned_decimal("00000001000J", "S9(10)V99") == Decimal("-100.01")

    def test_negative_K_through_R(self):
        assert decode_zoned_decimal("00000001000K", "S9(10)V99") == Decimal("-100.02")
        assert decode_zoned_decimal("00000001000R", "S9(10)V99") == Decimal("-100.09")

    def test_plain_digit_trailing(self):
        assert decode_zoned_decimal("000000010000", "S9(10)V99") == Decimal("100.00")

    def test_s9_4_v99(self):
        # discgrp rate: 00150{ -> 15.00 (S9(4)V99 = 4 integer + 2 decimal digits)
        assert decode_zoned_decimal("00150{", "S9(4)V99") == Decimal("15.00")

    def test_s9_9_v99(self):
        # 11-char field: 0000005047G -> 504.77 (G=+7)
        assert decode_zoned_decimal("0000005047G", "S9(9)V99") == Decimal("504.77")

    def test_negative_amount(self):
        # 0000009190} -> -919.00 (}=-0)
        assert decode_zoned_decimal("0000009190}", "S9(9)V99") == Decimal("-919.00")

    def test_empty_string(self):
        assert decode_zoned_decimal("", "S9(10)V99") == Decimal("0")

    def test_whitespace_string(self):
        assert decode_zoned_decimal("            ", "S9(10)V99") == Decimal("0")

    def test_invalid_overpunch_raises(self):
        with pytest.raises(ValueError, match="Invalid overpunch"):
            decode_zoned_decimal("00000000000Z", "S9(10)V99")


class TestParseField:
    """Tests for individual field parsing."""

    def test_alphanumeric_strips_trailing_spaces(self):
        result = parse_field("Hello     ", "X(10)")
        assert result == "Hello"

    def test_unsigned_numeric(self):
        result = parse_field("00000000001", "9(11)")
        assert result == "00000000001"

    def test_signed_decimal(self):
        result = parse_field("00000001940{", "S9(10)V99")
        assert result == Decimal("194.00")


class TestParseRecord:
    """Tests for full record parsing."""

    def test_account_record_line1(self):
        line = (
            "00000000001Y00000001940{00000020200{00000010200{"
            "2014-11-202025-05-202025-05-2000000000000{00000000000{"
            "A000000000"
        )
        line = line.ljust(300)
        record = parse_record(line, ACCOUNT_RECORD)
        assert record["ACCT-ID"] == "00000000001"
        assert record["ACCT-ACTIVE-STATUS"] == "Y"
        assert record["ACCT-CURR-BAL"] == Decimal("194.00")
        assert record["ACCT-CREDIT-LIMIT"] == Decimal("2020.00")
        assert record["ACCT-CASH-CREDIT-LIMIT"] == Decimal("1020.00")
        assert record["ACCT-OPEN-DATE"] == "2014-11-20"
        assert record["ACCT-EXPIRAION-DATE"] == "2025-05-20"
        assert record["ACCT-REISSUE-DATE"] == "2025-05-20"
        assert record["ACCT-CURR-CYC-CREDIT"] == Decimal("0.00")
        assert record["ACCT-CURR-CYC-DEBIT"] == Decimal("0.00")
        assert record["ACCT-ADDR-ZIP"].startswith("A000000000")
        assert record["ACCT-GROUP-ID"] == ""

    def test_card_xref_record(self):
        line = "050002445376574000000005000000000050"
        line = line.ljust(50)
        record = parse_record(line, CARD_XREF_RECORD)
        assert record["XREF-CARD-NUM"] == "0500024453765740"
        assert record["XREF-CUST-ID"] == "000000050"
        assert record["XREF-ACCT-ID"] == "00000000050"


class TestParseFile:
    """Integration tests against actual data files."""

    @pytest.mark.skipif(not DATA_DIR.exists(), reason="Data dir not available")
    def test_parse_acctdata(self):
        records = parse_data_file(DATA_DIR / "acctdata.txt")
        assert len(records) == 50
        assert records[0]["ACCT-ID"] == "00000000001"
        assert records[0]["ACCT-ACTIVE-STATUS"] == "Y"
        assert isinstance(records[0]["ACCT-CURR-BAL"], Decimal)

    @pytest.mark.skipif(not DATA_DIR.exists(), reason="Data dir not available")
    def test_parse_carddata(self):
        records = parse_data_file(DATA_DIR / "carddata.txt")
        assert len(records) == 50
        assert len(records[0]["CARD-NUM"]) == 16

    @pytest.mark.skipif(not DATA_DIR.exists(), reason="Data dir not available")
    def test_parse_cardxref(self):
        records = parse_data_file(DATA_DIR / "cardxref.txt")
        assert len(records) == 50

    @pytest.mark.skipif(not DATA_DIR.exists(), reason="Data dir not available")
    def test_parse_custdata(self):
        records = parse_data_file(DATA_DIR / "custdata.txt")
        assert len(records) == 50
        assert len(records[0]["CUST-FIRST-NAME"]) > 0

    @pytest.mark.skipif(not DATA_DIR.exists(), reason="Data dir not available")
    def test_parse_dailytran(self):
        records = parse_data_file(DATA_DIR / "dailytran.txt")
        assert len(records) == 300
        assert isinstance(records[0]["DALYTRAN-AMT"], Decimal)

    @pytest.mark.skipif(not DATA_DIR.exists(), reason="Data dir not available")
    def test_parse_discgrp(self):
        records = parse_data_file(DATA_DIR / "discgrp.txt")
        assert len(records) == 51
        assert isinstance(records[0]["DIS-INT-RATE"], Decimal)

    @pytest.mark.skipif(not DATA_DIR.exists(), reason="Data dir not available")
    def test_parse_tcatbal(self):
        records = parse_data_file(DATA_DIR / "tcatbal.txt")
        assert len(records) == 50

    @pytest.mark.skipif(not DATA_DIR.exists(), reason="Data dir not available")
    def test_parse_trancatg(self):
        records = parse_data_file(DATA_DIR / "trancatg.txt")
        assert len(records) == 18

    @pytest.mark.skipif(not DATA_DIR.exists(), reason="Data dir not available")
    def test_parse_trantype(self):
        records = parse_data_file(DATA_DIR / "trantype.txt")
        assert len(records) == 7

    @pytest.mark.skipif(not DATA_DIR.exists(), reason="Data dir not available")
    def test_all_acct_balances_are_positive(self):
        records = parse_data_file(DATA_DIR / "acctdata.txt")
        for r in records:
            assert r["ACCT-CURR-BAL"] >= 0, f"Account {r['ACCT-ID']} has negative balance"

    @pytest.mark.skipif(not DATA_DIR.exists(), reason="Data dir not available")
    def test_all_accounts_active(self):
        records = parse_data_file(DATA_DIR / "acctdata.txt")
        for r in records:
            assert r["ACCT-ACTIVE-STATUS"] == "Y", f"Account {r['ACCT-ID']} is not active"

    @pytest.mark.skipif(not DATA_DIR.exists(), reason="Data dir not available")
    def test_all_cycdebit_zero(self):
        """Validates that all 50 sample records have cycDebit=0 (known property of test data)."""
        records = parse_data_file(DATA_DIR / "acctdata.txt")
        for r in records:
            assert r["ACCT-CURR-CYC-DEBIT"] == Decimal("0.00"), (
                f"Account {r['ACCT-ID']} has non-zero cycDebit: {r['ACCT-CURR-CYC-DEBIT']}"
            )

    def test_unknown_layout_raises(self):
        with pytest.raises(ValueError, match="Unknown layout"):
            parse_data_file(Path("/tmp/fake.txt"), "nonexistent")


class TestEdgeCaseParsing:
    """Edge case tests for parser robustness."""

    def test_max_positive_s9_10_v99(self):
        # Maximum positive value: 9999999999.99 → "99999999999" + overpunch I(=9)
        assert decode_zoned_decimal("99999999999I", "S9(10)V99") == Decimal("9999999999.99")

    def test_max_negative_s9_10_v99(self):
        assert decode_zoned_decimal("99999999999R", "S9(10)V99") == Decimal("-9999999999.99")

    def test_single_cent_positive(self):
        assert decode_zoned_decimal("00000000000A", "S9(10)V99") == Decimal("0.01")

    def test_single_cent_negative(self):
        assert decode_zoned_decimal("00000000000J", "S9(10)V99") == Decimal("-0.01")

    def test_all_positive_overpunch_chars(self):
        overpunch_map = {
            "{": "0", "A": "1", "B": "2", "C": "3", "D": "4",
            "E": "5", "F": "6", "G": "7", "H": "8", "I": "9",
        }
        for char, digit in overpunch_map.items():
            raw = "0000000000" + "0" + char
            expected = Decimal(f"0.0{digit}")
            assert decode_zoned_decimal(raw, "S9(10)V99") == expected, (
                f"Overpunch '{char}' should decode to digit {digit}"
            )

    def test_all_negative_overpunch_chars(self):
        overpunch_map = {
            "}": "0", "J": "1", "K": "2", "L": "3", "M": "4",
            "N": "5", "O": "6", "P": "7", "Q": "8", "R": "9",
        }
        for char, digit in overpunch_map.items():
            raw = "0000000000" + "0" + char
            if digit == "0":
                expected = Decimal("0.00")
            else:
                expected = Decimal(f"-0.0{digit}")
            assert decode_zoned_decimal(raw, "S9(10)V99") == expected, (
                f"Overpunch '{char}' should decode to negative digit {digit}"
            )

    def test_parse_record_short_line_padded(self):
        """Short lines should be padded without errors."""
        line = "00000000001Y"  # Only 12 chars, layout expects 300
        record = parse_record(line, ACCOUNT_RECORD)
        assert record["ACCT-ID"] == "00000000001"
        assert record["ACCT-ACTIVE-STATUS"] == "Y"

    def test_parse_field_empty_alphanumeric(self):
        assert parse_field("          ", "X(10)") == ""

    def test_parse_field_empty_numeric(self):
        # Empty numeric fields return "0" (blank → stripped → empty → fallback "0")
        assert parse_field("     ", "9(5)") == "0"

    @pytest.mark.skipif(not DATA_DIR.exists(), reason="Data dir not available")
    def test_card_numbers_are_16_chars(self):
        """All card numbers should be exactly 16 characters."""
        records = parse_data_file(DATA_DIR / "carddata.txt")
        for r in records:
            assert len(r["CARD-NUM"]) == 16, (
                f"Card number '{r['CARD-NUM']}' is {len(r['CARD-NUM'])} chars"
            )

    @pytest.mark.skipif(not DATA_DIR.exists(), reason="Data dir not available")
    def test_xref_card_nums_match_carddata(self):
        """Cross-reference card numbers should appear in card data."""
        xref = parse_data_file(DATA_DIR / "cardxref.txt")
        cards = parse_data_file(DATA_DIR / "carddata.txt")
        card_nums = {r["CARD-NUM"].strip() for r in cards}
        for xr in xref:
            assert xr["XREF-CARD-NUM"].strip() in card_nums, (
                f"XREF card {xr['XREF-CARD-NUM']} not found in card data"
            )

    @pytest.mark.skipif(not DATA_DIR.exists(), reason="Data dir not available")
    def test_customer_ssn_is_9_digits(self):
        """All SSNs should be 9-digit numeric strings."""
        records = parse_data_file(DATA_DIR / "custdata.txt", "custdata")
        for r in records:
            ssn = r["CUST-SSN"]
            assert len(ssn) == 9, f"SSN '{ssn}' is not 9 digits"
            assert ssn.isdigit(), f"SSN '{ssn}' contains non-digits"

    @pytest.mark.skipif(not DATA_DIR.exists(), reason="Data dir not available")
    def test_daily_tran_amounts_reasonable(self):
        """Transaction amounts should be within reasonable range."""
        records = parse_data_file(DATA_DIR / "dailytran.txt")
        for r in records:
            amt = r["DALYTRAN-AMT"]
            assert abs(amt) < Decimal("100000"), (
                f"Transaction {r['DALYTRAN-ID']} has unreasonable amount: {amt}"
            )

    @pytest.mark.skipif(not DATA_DIR.exists(), reason="Data dir not available")
    def test_interest_rates_non_negative(self):
        """All interest rates in disclosure groups should be non-negative."""
        records = parse_data_file(DATA_DIR / "discgrp.txt")
        for r in records:
            rate = r["DIS-INT-RATE"]
            assert rate >= 0, f"Group {r['DIS-ACCT-GROUP-ID']} has negative rate: {rate}"

    @pytest.mark.skipif(not DATA_DIR.exists(), reason="Data dir not available")
    def test_tran_type_codes_2_chars(self):
        """Transaction type codes should be exactly 2 characters."""
        records = parse_data_file(DATA_DIR / "trantype.txt")
        for r in records:
            assert len(r["TRAN-TYPE"].strip()) == 2, (
                f"Type code '{r['TRAN-TYPE']}' is not 2 chars"
            )
