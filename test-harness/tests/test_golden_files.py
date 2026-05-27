"""Tests for golden file generation and consistency."""

import json
import pytest
from decimal import Decimal
from pathlib import Path

from test_harness.golden_file_generator import generate_golden_file, generate_all
from test_harness.copybook_layouts import FILE_LAYOUTS

DATA_DIR = Path(__file__).parent.parent.parent / "app" / "data" / "ASCII"


@pytest.mark.skipif(not DATA_DIR.exists(), reason="Data dir not available")
class TestGoldenFileGeneration:
    """Test that golden files are generated correctly."""

    def test_generate_acctdata(self, tmp_path):
        output = generate_golden_file(DATA_DIR / "acctdata.txt", tmp_path, "acctdata")
        assert output.exists()
        data = json.loads(output.read_text())
        assert data["record_count"] == 50
        assert len(data["records"]) == 50
        assert data["copybook_layout"] == "acctdata"
        assert len(data["field_definitions"]) > 0

    def test_generate_carddata(self, tmp_path):
        output = generate_golden_file(DATA_DIR / "carddata.txt", tmp_path, "carddata")
        data = json.loads(output.read_text())
        assert data["record_count"] == 50
        assert data["records"][0]["CARD-NUM"]

    def test_generate_all_files(self, tmp_path):
        paths = generate_all(DATA_DIR, tmp_path)
        assert len(paths) == len(FILE_LAYOUTS)
        for p in paths:
            assert p.exists()
            data = json.loads(p.read_text())
            assert data["record_count"] > 0

    def test_golden_file_has_field_definitions(self, tmp_path):
        output = generate_golden_file(DATA_DIR / "acctdata.txt", tmp_path, "acctdata")
        data = json.loads(output.read_text())
        field_defs = data["field_definitions"]
        field_names = [f["field"] for f in field_defs]
        assert "ACCT-ID" in field_names
        assert "ACCT-CURR-BAL" in field_names
        # Each definition should have description
        for fd in field_defs:
            assert "description" in fd
            assert "pic" in fd
            assert "start_byte" in fd
            assert "length" in fd

    def test_golden_file_decimal_serialization(self, tmp_path):
        output = generate_golden_file(DATA_DIR / "acctdata.txt", tmp_path, "acctdata")
        data = json.loads(output.read_text())
        bal = data["records"][0]["ACCT-CURR-BAL"]
        # Should be a string representation of the decimal
        assert isinstance(bal, str)
        assert Decimal(bal) == Decimal("194.00")

    def test_golden_file_roundtrip(self, tmp_path):
        """Generate a golden file and verify it can be reloaded for comparison."""
        output = generate_golden_file(DATA_DIR / "cardxref.txt", tmp_path, "cardxref")
        data = json.loads(output.read_text())
        assert data["records"][0]["XREF-CARD-NUM"]
        assert data["records"][0]["XREF-CUST-ID"]
        assert data["records"][0]["XREF-ACCT-ID"]
