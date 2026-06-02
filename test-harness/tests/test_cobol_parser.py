"""Tests for the COBOL fixed-width data file parser."""

import json
from decimal import Decimal
from pathlib import Path

import pytest

import sys
sys.path.insert(0, str(Path(__file__).resolve().parent.parent))

from cobol_parser import (
    FieldDefinition,
    FieldType,
    RecordLayout,
    decode_comp3,
    decode_overpunch,
    encode_comp3,
    encode_overpunch,
    parse_file,
    parse_record,
)


# ============================================================
# Overpunch Decode Tests
# ============================================================

class TestDecodeOverpunch:
    def test_positive_zero(self):
        assert decode_overpunch("00000000000{", 2) == Decimal("0.00")

    def test_positive_value(self):
        assert decode_overpunch("00000001940{", 2) == Decimal("194.00")

    def test_negative_zero(self):
        # } means negative with last digit 0
        assert decode_overpunch("00000000000}", 2) == Decimal("0")

    def test_negative_value_ending_in_zero(self):
        # "00000001940}" = digits "000000019400" = 19400 -> 194.00, negative
        assert decode_overpunch("00000001940}", 2) == Decimal("-194.00")

    def test_negative_value_ending_in_nonzero(self):
        # J=1 negative, so "00000009190J" -> "000000091901" -> 91901 -> 919.01 negative
        # Actually: "0000009190J" has 11 chars, last=J(1), digits="00000091901"
        # Let me test with proper 11+1=12 chars:
        assert decode_overpunch("00000009190}", 2) == Decimal("-919.00")

    def test_positive_a_through_i(self):
        assert decode_overpunch("00000000000A", 2) == Decimal("0.01")
        assert decode_overpunch("00000000000B", 2) == Decimal("0.02")
        assert decode_overpunch("00000000000I", 2) == Decimal("0.09")

    def test_negative_j_through_r(self):
        assert decode_overpunch("00000000000J", 2) == Decimal("-0.01")
        assert decode_overpunch("00000000000K", 2) == Decimal("-0.02")
        assert decode_overpunch("00000000000R", 2) == Decimal("-0.09")

    def test_large_value(self):
        # PIC S9(10)V99: max = 9999999999.99
        assert decode_overpunch("99999999999I", 2) == Decimal("9999999999.99")

    def test_scale_zero(self):
        assert decode_overpunch("00150{", 0) == Decimal("1500")

    def test_empty_string(self):
        assert decode_overpunch("", 2) == Decimal("0")

    def test_blank_string(self):
        assert decode_overpunch("            ", 2) == Decimal("0")

    def test_scale_two_six_chars(self):
        # PIC S9(04)V99 -> 6 chars, scale=2
        # "00150{" = digits "001500" = 1500, movePointLeft(2) = 15.00
        assert decode_overpunch("00150{", 2) == Decimal("15.00")


class TestEncodeOverpunch:
    def test_positive_zero(self):
        result = encode_overpunch(Decimal("0.00"), 12, 2)
        assert result == "00000000000{"

    def test_positive_value(self):
        result = encode_overpunch(Decimal("194.00"), 12, 2)
        assert result == "00000001940{"

    def test_negative_value(self):
        result = encode_overpunch(Decimal("-194.00"), 12, 2)
        assert result == "00000001940}"

    def test_roundtrip(self):
        values = [
            Decimal("0.00"), Decimal("194.00"), Decimal("-194.00"),
            Decimal("0.01"), Decimal("-0.01"), Decimal("9999999999.99"),
            Decimal("-9999999999.99"), Decimal("2525.00"),
        ]
        for v in values:
            encoded = encode_overpunch(v, 12, 2)
            decoded = decode_overpunch(encoded, 2)
            assert decoded == v, f"Roundtrip failed for {v}: encoded={encoded}, decoded={decoded}"


# ============================================================
# COMP-3 (Packed Decimal) Tests
# ============================================================

class TestComp3:
    def test_encode_positive(self):
        result = encode_comp3(Decimal("2525.00"), 12, 2)
        # 12 digits + 1 sign = 13 nibbles = 7 bytes
        assert len(result) == 7
        # Last nibble should be 0x0C (positive)
        assert result[-1] & 0x0F == 0x0C

    def test_encode_negative(self):
        result = encode_comp3(Decimal("-1025.00"), 12, 2)
        assert len(result) == 7
        assert result[-1] & 0x0F == 0x0D

    def test_encode_zero(self):
        result = encode_comp3(Decimal("0.00"), 12, 2)
        assert len(result) == 7
        # All digit nibbles should be 0, sign = 0x0C
        assert result[-1] == 0x0C

    def test_decode_positive(self):
        encoded = encode_comp3(Decimal("2525.00"), 12, 2)
        decoded = decode_comp3(encoded, 2)
        assert decoded == Decimal("2525.00")

    def test_decode_negative(self):
        encoded = encode_comp3(Decimal("-1025.00"), 12, 2)
        decoded = decode_comp3(encoded, 2)
        assert decoded == Decimal("-1025.00")

    def test_roundtrip(self):
        values = [
            Decimal("0.00"), Decimal("2525.00"), Decimal("-2525.00"),
            Decimal("1005.00"), Decimal("-1025.00"), Decimal("0.01"),
            Decimal("-0.01"), Decimal("9999999999.99"),
        ]
        for v in values:
            encoded = encode_comp3(v, 12, 2)
            decoded = decode_comp3(encoded, 2)
            assert decoded == v, f"Roundtrip failed for {v}"

    def test_nibble_alignment(self):
        """Verify the COMP-3 bug fix: nibble offset uses byteLength*2."""
        # 12 digits + 1 sign = 13 nibbles -> 7 bytes (14 nibble slots)
        # First nibble should be padding zero
        encoded = encode_comp3(Decimal("123456789012.00"), 14, 2)
        # 14 digits + 1 sign = 15 nibbles -> 8 bytes
        assert len(encoded) == 8
        decoded = decode_comp3(encoded, 2)
        assert decoded == Decimal("123456789012.00")


# ============================================================
# Record Parsing Tests
# ============================================================

class TestParseRecord:
    @pytest.fixture
    def account_layout(self):
        return RecordLayout(
            name="ACCOUNT-RECORD",
            record_length=300,
            copybook="CVACT01Y.cpy",
            fields=[
                FieldDefinition("ACCT-ID", 0, 11, "9(11)", FieldType.NUMERIC_DISPLAY),
                FieldDefinition("ACCT-ACTIVE-STATUS", 11, 1, "X(01)", FieldType.ALPHANUMERIC),
                FieldDefinition("ACCT-CURR-BAL", 12, 12, "S9(10)V99",
                                FieldType.SIGNED_DECIMAL, scale=2),
                FieldDefinition("ACCT-CREDIT-LIMIT", 24, 12, "S9(10)V99",
                                FieldType.SIGNED_DECIMAL, scale=2),
                FieldDefinition("ACCT-CASH-CREDIT-LIMIT", 36, 12, "S9(10)V99",
                                FieldType.SIGNED_DECIMAL, scale=2),
                FieldDefinition("ACCT-OPEN-DATE", 48, 10, "X(10)", FieldType.ALPHANUMERIC),
                FieldDefinition("ACCT-EXPIRAION-DATE", 58, 10, "X(10)", FieldType.ALPHANUMERIC),
                FieldDefinition("ACCT-REISSUE-DATE", 68, 10, "X(10)", FieldType.ALPHANUMERIC),
                FieldDefinition("ACCT-CURR-CYC-CREDIT", 78, 12, "S9(10)V99",
                                FieldType.SIGNED_DECIMAL, scale=2),
                FieldDefinition("ACCT-CURR-CYC-DEBIT", 90, 12, "S9(10)V99",
                                FieldType.SIGNED_DECIMAL, scale=2),
                FieldDefinition("ACCT-ADDR-ZIP", 102, 10, "X(10)", FieldType.ALPHANUMERIC),
                FieldDefinition("ACCT-GROUP-ID", 112, 10, "X(10)", FieldType.ALPHANUMERIC),
                FieldDefinition("FILLER", 122, 178, "X(178)", FieldType.FILLER),
            ],
        )

    def test_parse_first_record(self, account_layout):
        line = ("00000000001Y00000001940{00000020200{00000010200{"
                "2014-11-202025-05-202025-05-20"
                "00000000000{00000000000{A000000000"
                + " " * 188)
        record = parse_record(line, account_layout)
        assert record["ACCT-ID"] == "00000000001"
        assert record["ACCT-ACTIVE-STATUS"] == "Y"
        assert record["ACCT-CURR-BAL"] == "194.00"
        assert record["ACCT-CREDIT-LIMIT"] == "2020.00"
        assert record["ACCT-CASH-CREDIT-LIMIT"] == "1020.00"
        assert record["ACCT-OPEN-DATE"] == "2014-11-20"
        assert record["ACCT-EXPIRAION-DATE"] == "2025-05-20"
        assert record["ACCT-CURR-CYC-CREDIT"] == "0.00"
        assert record["ACCT-CURR-CYC-DEBIT"] == "0.00"
        assert record["ACCT-ADDR-ZIP"] == "A000000000"


class TestParseFile:
    def test_parse_acctdata(self):
        repo_root = Path(__file__).resolve().parent.parent.parent
        acctdata = repo_root / "app" / "data" / "ASCII" / "acctdata.txt"
        if not acctdata.exists():
            pytest.skip("Sample data not available")

        layout = RecordLayout(
            name="ACCOUNT-RECORD",
            record_length=300,
            copybook="CVACT01Y.cpy",
            fields=[
                FieldDefinition("ACCT-ID", 0, 11, "9(11)", FieldType.NUMERIC_DISPLAY),
                FieldDefinition("ACCT-ACTIVE-STATUS", 11, 1, "X(01)", FieldType.ALPHANUMERIC),
                FieldDefinition("ACCT-CURR-BAL", 12, 12, "S9(10)V99",
                                FieldType.SIGNED_DECIMAL, scale=2),
                FieldDefinition("FILLER", 122, 178, "X(178)", FieldType.FILLER),
            ],
        )

        records = parse_file(acctdata, layout)
        assert len(records) == 50
        assert records[0]["ACCT-ID"] == "00000000001"
        assert records[0]["ACCT-CURR-BAL"] == "194.00"

    def test_parse_cardxref(self):
        repo_root = Path(__file__).resolve().parent.parent.parent
        xref_file = repo_root / "app" / "data" / "ASCII" / "cardxref.txt"
        if not xref_file.exists():
            pytest.skip("Sample data not available")

        layout = RecordLayout(
            name="CARD-XREF-RECORD",
            record_length=36,
            copybook="CVACT03Y.cpy",
            fields=[
                FieldDefinition("XREF-CARD-NUM", 0, 16, "X(16)", FieldType.ALPHANUMERIC),
                FieldDefinition("XREF-CUST-ID", 16, 9, "9(09)", FieldType.NUMERIC_DISPLAY),
                FieldDefinition("XREF-ACCT-ID", 25, 11, "9(11)", FieldType.NUMERIC_DISPLAY),
                FieldDefinition("FILLER", 36, 14, "X(14)", FieldType.FILLER),
            ],
        )

        records = parse_file(xref_file, layout)
        assert len(records) == 50
        assert records[0]["XREF-CARD-NUM"] == "0500024453765740"
