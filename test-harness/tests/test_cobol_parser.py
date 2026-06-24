"""Tests for the COBOL fixed-width file parser."""

from decimal import Decimal
from pathlib import Path

import pytest

from carddemo_harness.cobol_parser import decode_overpunch, parse_field, parse_record, parse_data_file
from carddemo_harness.copybook_layouts import (
    FieldDef, ACCOUNT_LAYOUT, CARD_LAYOUT, CARD_XREF_LAYOUT,
    CUSTOMER_LAYOUT, DAILY_TRAN_LAYOUT, TRAN_CAT_BAL_LAYOUT,
    DISC_GROUP_LAYOUT, TRAN_TYPE_LAYOUT, TRAN_CATEGORY_LAYOUT,
)


class TestDecodeOverpunch:
    """Tests for overpunch character decoding."""

    @pytest.mark.parametrize("raw,scale,expected", [
        ("00000001940{", 2, Decimal("194.00")),
        ("00000020200{", 2, Decimal("2020.00")),
        ("00000010200{", 2, Decimal("1020.00")),
        ("00000000000{", 2, Decimal("0.00")),
    ])
    def test_positive_zero_overpunch(self, raw, scale, expected):
        assert decode_overpunch(raw, scale) == expected

    @pytest.mark.parametrize("raw,scale,expected", [
        ("00000001940}", 2, Decimal("-194.00")),
        ("00000020200}", 2, Decimal("-2020.00")),
    ])
    def test_negative_zero_overpunch(self, raw, scale, expected):
        assert decode_overpunch(raw, scale) == expected

    @pytest.mark.parametrize("char,digit", [
        ("A", 1), ("B", 2), ("C", 3), ("D", 4), ("E", 5),
        ("F", 6), ("G", 7), ("H", 8), ("I", 9),
    ])
    def test_positive_overpunch_digits(self, char, digit):
        raw = f"0000000000{char}"
        result = decode_overpunch(raw, 2)
        expected = Decimal(digit) / 100
        assert result == expected.quantize(Decimal("0.01"))

    @pytest.mark.parametrize("char,digit", [
        ("J", 1), ("K", 2), ("L", 3), ("M", 4), ("N", 5),
        ("O", 6), ("P", 7), ("Q", 8), ("R", 9),
    ])
    def test_negative_overpunch_digits(self, char, digit):
        raw = f"0000000000{char}"
        result = decode_overpunch(raw, 2)
        expected = -(Decimal(digit) / 100)
        assert result == expected.quantize(Decimal("0.01"))

    def test_empty_string(self):
        assert decode_overpunch("", 2) == Decimal("0")

    def test_whitespace(self):
        assert decode_overpunch("           ", 2) == Decimal("0")

    def test_amount_with_nonzero_ending_in_zero(self):
        """Regression: -2500.00 should decode correctly (last digit 0, negative).
        PIC S9(10)V99 = 12 display bytes. -2500.00 → 250000 → '000000250000'
        → overpunch last 0 as negative → '00000025000}'
        """
        raw = "00000025000}"
        result = decode_overpunch(raw, 2)
        assert result == Decimal("-2500.00")

    def test_scale_zero(self):
        raw = "00000001940{"
        result = decode_overpunch(raw, 0)
        assert result == Decimal("19400")


class TestParseField:
    """Tests for individual field parsing."""

    def test_signed_decimal(self):
        field = FieldDef("BAL", "PIC S9(10)V99", 0, 12, "signed_decimal", 2, "")
        assert parse_field("00000001940{", field) == Decimal("194.00")

    def test_numeric(self):
        field = FieldDef("ID", "PIC 9(11)", 0, 11, "numeric", 0, "")
        assert parse_field("00000000001", field) == 1

    def test_alphanumeric(self):
        field = FieldDef("NAME", "PIC X(25)", 0, 25, "alphanumeric", 0, "")
        assert parse_field("Immanuel                 ", field) == "Immanuel"

    def test_alphanumeric_preserves_leading_spaces(self):
        field = FieldDef("NAME", "PIC X(10)", 0, 10, "alphanumeric", 0, "")
        assert parse_field("  Hello   ", field) == "  Hello"

    def test_numeric_with_spaces(self):
        field = FieldDef("ID", "PIC 9(09)", 0, 9, "numeric", 0, "")
        assert parse_field("         ", field) == 0


class TestParseRecord:
    """Tests for full record parsing."""

    def test_account_record_first(self, data_dir):
        with open(data_dir / "acctdata.txt") as f:
            line = f.readline().rstrip("\n").rstrip("\r")
        rec = parse_record(line, ACCOUNT_LAYOUT)
        assert rec["ACCT-ID"] == 1
        assert rec["ACCT-ACTIVE-STATUS"] == "Y"
        assert rec["ACCT-CURR-BAL"] == Decimal("194.00")
        assert rec["ACCT-CREDIT-LIMIT"] == Decimal("2020.00")
        assert rec["ACCT-OPEN-DATE"] == "2014-11-20"

    def test_card_record_first(self, data_dir):
        with open(data_dir / "carddata.txt") as f:
            line = f.readline().rstrip("\n").rstrip("\r")
        rec = parse_record(line, CARD_LAYOUT)
        assert len(rec["CARD-NUM"]) == 16
        assert rec["CARD-CVV-CD"] > 0
        assert rec["CARD-ACTIVE-STATUS"] in ("Y", "N")

    def test_xref_record_first(self, data_dir):
        with open(data_dir / "cardxref.txt") as f:
            line = f.readline().rstrip("\n").rstrip("\r")
        rec = parse_record(line, CARD_XREF_LAYOUT)
        assert len(str(rec["XREF-CARD-NUM"])) == 16
        assert rec["XREF-CUST-ID"] > 0
        assert rec["XREF-ACCT-ID"] > 0


class TestParseDataFile:
    """Tests for full file parsing."""

    @pytest.mark.parametrize("filename,expected_count", [
        ("acctdata.txt", 50),
        ("carddata.txt", 50),
        ("cardxref.txt", 50),
        ("custdata.txt", 50),
        ("dailytran.txt", 300),
        ("discgrp.txt", 51),
        ("tcatbal.txt", 50),
        ("trancatg.txt", 18),
        ("trantype.txt", 7),
    ])
    def test_record_counts(self, data_dir, filename, expected_count):
        records = parse_data_file(data_dir / filename)
        assert len(records) == expected_count

    def test_unknown_file_raises(self, tmp_path):
        f = tmp_path / "unknown.txt"
        f.write_text("test")
        with pytest.raises(ValueError, match="Unknown data file"):
            parse_data_file(f)

    def test_account_balances_are_decimal(self, data_dir):
        records = parse_data_file(data_dir / "acctdata.txt")
        for rec in records:
            assert isinstance(rec["ACCT-CURR-BAL"], Decimal)
            assert isinstance(rec["ACCT-CREDIT-LIMIT"], Decimal)

    def test_transaction_amounts_signed(self, data_dir):
        records = parse_data_file(data_dir / "dailytran.txt")
        has_positive = any(r["DALYTRAN-AMT"] > 0 for r in records)
        has_negative = any(r["DALYTRAN-AMT"] < 0 for r in records)
        assert has_positive, "Expected some positive transaction amounts"
        assert has_negative, "Expected some negative transaction amounts"
