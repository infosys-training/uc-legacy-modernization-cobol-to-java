"""Tests that validate golden-file JSON against source data files."""

import json
from decimal import Decimal
from pathlib import Path

import pytest

from carddemo_harness.cobol_parser import parse_data_file
from carddemo_harness.copybook_layouts import FILE_LAYOUTS


GOLDEN_FILES = [
    ("acctdata.json", "acctdata.txt", 50),
    ("carddata.json", "carddata.txt", 50),
    ("cardxref.json", "cardxref.txt", 50),
    ("custdata.json", "custdata.txt", 50),
    ("dailytran.json", "dailytran.txt", 300),
    ("discgrp.json", "discgrp.txt", 51),
    ("tcatbal.json", "tcatbal.txt", 50),
    ("trancatg.json", "trancatg.txt", 18),
    ("trantype.json", "trantype.txt", 7),
]


class TestGoldenFileMetadata:
    """Tests for golden file metadata accuracy."""

    @pytest.mark.parametrize("json_name,txt_name,expected_count", GOLDEN_FILES)
    def test_record_count_matches(self, golden_dir, json_name, txt_name, expected_count):
        with open(golden_dir / json_name) as f:
            data = json.load(f)
        assert data["metadata"]["record_count"] == expected_count
        assert len(data["records"]) == expected_count

    @pytest.mark.parametrize("json_name,txt_name,expected_count", GOLDEN_FILES)
    def test_source_file_matches(self, golden_dir, json_name, txt_name, expected_count):
        with open(golden_dir / json_name) as f:
            data = json.load(f)
        assert data["metadata"]["source_file"] == txt_name

    @pytest.mark.parametrize("json_name,txt_name,expected_count", GOLDEN_FILES)
    def test_has_field_definitions(self, golden_dir, json_name, txt_name, expected_count):
        with open(golden_dir / json_name) as f:
            data = json.load(f)
        assert len(data["field_definitions"]) > 0
        for fdef in data["field_definitions"]:
            assert "field_name" in fdef
            assert "pic_clause" in fdef
            assert "byte_offset" in fdef
            assert "business_meaning" in fdef


class TestGoldenFileConsistency:
    """Tests that golden files match live parser output."""

    @pytest.mark.parametrize("json_name,txt_name,expected_count", GOLDEN_FILES)
    def test_golden_matches_parser(self, golden_dir, data_dir, json_name, txt_name, expected_count):
        """Golden file records must match fresh parser output exactly."""
        with open(golden_dir / json_name) as f:
            golden = json.load(f)

        parsed = parse_data_file(data_dir / txt_name)
        assert len(golden["records"]) == len(parsed)

        for i, (g_rec, p_rec) in enumerate(zip(golden["records"], parsed)):
            for field_name, p_val in p_rec.items():
                g_val = g_rec[field_name]
                if isinstance(p_val, Decimal):
                    assert Decimal(g_val) == p_val, (
                        f"Record {i}, field {field_name}: "
                        f"golden={g_val}, parsed={p_val}"
                    )
                elif isinstance(p_val, int):
                    assert int(g_val) == p_val, (
                        f"Record {i}, field {field_name}: "
                        f"golden={g_val}, parsed={p_val}"
                    )
                else:
                    assert g_val == p_val, (
                        f"Record {i}, field {field_name}: "
                        f"golden={g_val!r}, parsed={p_val!r}"
                    )


class TestGoldenFileSpecificValues:
    """Spot-check specific values from known records."""

    def test_first_account_balance(self, golden_dir):
        with open(golden_dir / "acctdata.json") as f:
            data = json.load(f)
        rec = data["records"][0]
        assert Decimal(rec["ACCT-CURR-BAL"]) == Decimal("194.00")
        assert rec["ACCT-ACTIVE-STATUS"] == "Y"
        assert rec["ACCT-OPEN-DATE"] == "2014-11-20"

    def test_first_transaction_amount(self, golden_dir):
        with open(golden_dir / "dailytran.json") as f:
            data = json.load(f)
        rec = data["records"][0]
        assert Decimal(rec["DALYTRAN-AMT"]) == Decimal("504.77")
        assert rec["DALYTRAN-TYPE-CD"] == "01"

    def test_negative_transaction(self, golden_dir):
        with open(golden_dir / "dailytran.json") as f:
            data = json.load(f)
        rec = data["records"][1]
        assert Decimal(rec["DALYTRAN-AMT"]) == Decimal("-919.00")

    def test_disclosure_group_rate(self, golden_dir):
        with open(golden_dir / "discgrp.json") as f:
            data = json.load(f)
        rec = data["records"][0]
        assert Decimal(rec["DIS-INT-RATE"]) == Decimal("15.00")

    def test_transaction_type(self, golden_dir):
        with open(golden_dir / "trantype.json") as f:
            data = json.load(f)
        rec = data["records"][0]
        assert rec["TRAN-TYPE"] == "01"
        assert "Purchase" in rec["TRAN-TYPE-DESC"]
