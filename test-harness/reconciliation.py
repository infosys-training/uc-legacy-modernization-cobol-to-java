"""
Reconciliation Check Functions

Validates batch output integrity through:
  - Record count validation
  - Numeric field sum validation
  - Cross-reference integrity checks
  - File-level checksum verification
"""

import hashlib
import json
from dataclasses import dataclass, field
from decimal import Decimal
from pathlib import Path
from typing import Optional

from cobol_parser import (
    FieldDefinition,
    FieldType,
    RecordLayout,
    parse_file,
)


@dataclass
class ReconciliationCheck:
    """A single reconciliation check result."""
    name: str
    status: str           # PASS, FAIL, WARN, SKIP
    expected: str
    actual: str
    message: str = ""

    def to_dict(self) -> dict:
        return {
            "name": self.name,
            "status": self.status,
            "expected": self.expected,
            "actual": self.actual,
            "message": self.message,
        }


@dataclass
class ReconciliationReport:
    """Complete reconciliation report for a batch run."""
    job_name: str
    checks: list[ReconciliationCheck] = field(default_factory=list)

    @property
    def passed(self) -> int:
        return sum(1 for c in self.checks if c.status == "PASS")

    @property
    def failed(self) -> int:
        return sum(1 for c in self.checks if c.status == "FAIL")

    @property
    def status(self) -> str:
        if self.failed > 0:
            return "FAIL"
        return "PASS"

    def add(self, check: ReconciliationCheck):
        self.checks.append(check)

    def to_dict(self) -> dict:
        return {
            "job": self.job_name,
            "status": self.status,
            "total_checks": len(self.checks),
            "passed": self.passed,
            "failed": self.failed,
            "checks": [c.to_dict() for c in self.checks],
        }

    def to_json(self, indent: int = 2) -> str:
        return json.dumps(self.to_dict(), indent=indent)

    def summary(self) -> str:
        lines = [
            f"Reconciliation: {self.job_name}",
            f"  Status: {self.status}",
            f"  Checks: {len(self.checks)} total, {self.passed} passed, {self.failed} failed",
        ]
        for c in self.checks:
            icon = "OK" if c.status == "PASS" else "FAIL"
            lines.append(f"  [{icon}] {c.name}: expected={c.expected}, actual={c.actual}")
            if c.message:
                lines.append(f"        {c.message}")
        return "\n".join(lines)


# ============================================================
# Record Count Validation
# ============================================================

def check_record_count(filepath: str | Path, expected_count: int,
                       label: str = "Record count") -> ReconciliationCheck:
    """Verify that a file contains the expected number of lines/records."""
    filepath = Path(filepath)
    if not filepath.exists():
        return ReconciliationCheck(
            name=label, status="FAIL",
            expected=str(expected_count), actual="FILE_NOT_FOUND",
            message=f"File not found: {filepath}")

    with open(filepath, 'r', errors='replace') as f:
        actual_count = sum(1 for _ in f)

    status = "PASS" if actual_count == expected_count else "FAIL"
    return ReconciliationCheck(
        name=label, status=status,
        expected=str(expected_count), actual=str(actual_count))


def check_file_size(filepath: str | Path, record_length: int,
                    expected_records: int,
                    label: str = "File size") -> ReconciliationCheck:
    """Verify that file size = record_count * record_length (for FB files)."""
    filepath = Path(filepath)
    if not filepath.exists():
        return ReconciliationCheck(
            name=label, status="FAIL",
            expected="", actual="FILE_NOT_FOUND")

    actual_size = filepath.stat().st_size
    # Account for newlines in text files (each record + newline)
    expected_with_newlines = expected_records * (record_length + 1)
    expected_without = expected_records * record_length

    if actual_size == expected_without:
        return ReconciliationCheck(
            name=label, status="PASS",
            expected=str(expected_without), actual=str(actual_size),
            message="Binary file (no newlines)")
    elif actual_size == expected_with_newlines:
        return ReconciliationCheck(
            name=label, status="PASS",
            expected=str(expected_with_newlines), actual=str(actual_size),
            message="Text file (with newlines)")
    else:
        return ReconciliationCheck(
            name=label, status="FAIL",
            expected=f"{expected_without} or {expected_with_newlines}",
            actual=str(actual_size),
            message=f"Expected {expected_records} records * {record_length} bytes")


# ============================================================
# Numeric Field Sum Validation
# ============================================================

def check_field_sum(records: list[dict], field_name: str,
                    expected_sum: Decimal,
                    tolerance: Decimal = Decimal('0'),
                    label: Optional[str] = None) -> ReconciliationCheck:
    """Verify that the sum of a numeric field matches the expected total."""
    label = label or f"Sum({field_name})"
    actual_sum = Decimal('0')
    for rec in records:
        try:
            actual_sum += Decimal(str(rec.get(field_name, '0')))
        except Exception:
            pass

    diff = abs(actual_sum - expected_sum)
    status = "PASS" if diff <= tolerance else "FAIL"
    return ReconciliationCheck(
        name=label, status=status,
        expected=str(expected_sum), actual=str(actual_sum),
        message=f"Difference: {diff}" if diff > 0 else "")


def compute_field_sums(records: list[dict],
                       numeric_fields: list[str]) -> dict[str, Decimal]:
    """Compute sums of specified numeric fields across all records."""
    sums = {f: Decimal('0') for f in numeric_fields}
    for rec in records:
        for f in numeric_fields:
            try:
                sums[f] += Decimal(str(rec.get(f, '0')))
            except Exception:
                pass
    return sums


# ============================================================
# Cross-Reference Integrity Checks
# ============================================================

def check_xref_accounts(xref_records: list[dict],
                        account_records: list[dict]) -> ReconciliationCheck:
    """
    Verify every card in cardxref has a matching account in acctdata.

    xref_records: parsed cardxref.txt records
    account_records: parsed acctdata.txt records
    """
    acct_ids = {rec.get("ACCT-ID", "").lstrip("0") or "0"
                for rec in account_records}

    missing = []
    for xref in xref_records:
        xref_acct = xref.get("XREF-ACCT-ID", "").lstrip("0") or "0"
        if xref_acct not in acct_ids:
            missing.append(
                f"Card {xref.get('XREF-CARD-NUM', '?')} -> Account {xref.get('XREF-ACCT-ID', '?')}")

    if missing:
        return ReconciliationCheck(
            name="XREF->Account integrity",
            status="FAIL",
            expected="All XREF accounts exist in ACCTFILE",
            actual=f"{len(missing)} orphaned cross-references",
            message="; ".join(missing[:5]) + ("..." if len(missing) > 5 else ""))
    else:
        return ReconciliationCheck(
            name="XREF->Account integrity",
            status="PASS",
            expected="All XREF accounts exist in ACCTFILE",
            actual=f"{len(xref_records)} cross-refs validated")


def check_xref_customers(xref_records: list[dict],
                         customer_records: list[dict]) -> ReconciliationCheck:
    """Verify every card xref has a matching customer in custdata."""
    cust_ids = {rec.get("CUST-ID", "").lstrip("0") or "0"
                for rec in customer_records}

    missing = []
    for xref in xref_records:
        xref_cust = xref.get("XREF-CUST-ID", "").lstrip("0") or "0"
        if xref_cust not in cust_ids:
            missing.append(
                f"Card {xref.get('XREF-CARD-NUM', '?')} -> Customer {xref.get('XREF-CUST-ID', '?')}")

    if missing:
        return ReconciliationCheck(
            name="XREF->Customer integrity",
            status="FAIL",
            expected="All XREF customers exist in CUSTFILE",
            actual=f"{len(missing)} orphaned cross-references",
            message="; ".join(missing[:5]) + ("..." if len(missing) > 5 else ""))
    else:
        return ReconciliationCheck(
            name="XREF->Customer integrity",
            status="PASS",
            expected="All XREF customers exist in CUSTFILE",
            actual=f"{len(xref_records)} cross-refs validated")


def check_card_xref_coverage(card_records: list[dict],
                             xref_records: list[dict]) -> ReconciliationCheck:
    """Verify every card in carddata has an entry in cardxref."""
    xref_cards = {rec.get("XREF-CARD-NUM", "") for rec in xref_records}

    missing = []
    for card in card_records:
        card_num = card.get("CARD-NUM", "")
        if card_num and card_num not in xref_cards:
            missing.append(card_num)

    if missing:
        return ReconciliationCheck(
            name="Card->XREF coverage",
            status="FAIL",
            expected="All cards have XREF entries",
            actual=f"{len(missing)} cards without XREF entries",
            message="; ".join(missing[:5]) + ("..." if len(missing) > 5 else ""))
    else:
        return ReconciliationCheck(
            name="Card->XREF coverage",
            status="PASS",
            expected="All cards have XREF entries",
            actual=f"{len(card_records)} cards validated")


def check_transaction_card_refs(transaction_records: list[dict],
                                xref_records: list[dict]) -> ReconciliationCheck:
    """Verify every transaction card number exists in the cross-reference."""
    xref_cards = {rec.get("XREF-CARD-NUM", "") for rec in xref_records}

    missing = set()
    for tran in transaction_records:
        card_num = tran.get("TRAN-CARD-NUM", "")
        if card_num and card_num not in xref_cards:
            missing.add(card_num)

    if missing:
        return ReconciliationCheck(
            name="Transaction->XREF card integrity",
            status="FAIL",
            expected="All transaction card numbers exist in XREF",
            actual=f"{len(missing)} unknown card numbers in transactions",
            message="; ".join(list(missing)[:5]) + ("..." if len(missing) > 5 else ""))
    else:
        unique_cards = {t.get("TRAN-CARD-NUM", "") for t in transaction_records}
        return ReconciliationCheck(
            name="Transaction->XREF card integrity",
            status="PASS",
            expected="All transaction card numbers exist in XREF",
            actual=f"{len(unique_cards)} unique cards validated across {len(transaction_records)} transactions")


# ============================================================
# File Checksum Verification
# ============================================================

def check_file_checksum(filepath: str | Path,
                        expected_sha256: str,
                        label: str = "File checksum") -> ReconciliationCheck:
    """Verify file SHA-256 checksum matches expected value."""
    filepath = Path(filepath)
    if not filepath.exists():
        return ReconciliationCheck(
            name=label, status="FAIL",
            expected=expected_sha256, actual="FILE_NOT_FOUND")

    h = hashlib.sha256()
    with open(filepath, 'rb') as f:
        for chunk in iter(lambda: f.read(8192), b''):
            h.update(chunk)
    actual_hash = h.hexdigest()

    status = "PASS" if actual_hash == expected_sha256 else "FAIL"
    return ReconciliationCheck(
        name=label, status=status,
        expected=expected_sha256[:16] + "...",
        actual=actual_hash[:16] + "...")


# ============================================================
# Full Data Set Reconciliation
# ============================================================

def reconcile_sample_data(data_dir: str | Path) -> ReconciliationReport:
    """
    Run all cross-reference integrity checks on the sample ASCII data files.

    This is the primary entry point for validating data consistency.
    """
    data_dir = Path(data_dir)
    report = ReconciliationReport(job_name="Sample Data Integrity")

    # Load golden JSON files or parse raw files
    files_to_load = {
        "acctdata": ("acctdata.json", "ACCT-ID"),
        "carddata": ("carddata.json", "CARD-NUM"),
        "cardxref": ("cardxref.json", "XREF-CARD-NUM"),
        "custdata": ("custdata.json", "CUST-ID"),
        "dailytran": ("dailytran.json", "TRAN-ID"),
    }

    loaded = {}
    # golden-files is at the repo root, sibling to app/
    golden_dir = data_dir.parent.parent.parent / "golden-files"

    for key, (json_file, _) in files_to_load.items():
        json_path = golden_dir / json_file
        if json_path.exists():
            with open(json_path) as f:
                data = json.load(f)
            loaded[key] = data["records"]
        else:
            loaded[key] = []

    # Record count checks
    report.add(ReconciliationCheck(
        name="Account record count", status="PASS" if len(loaded["acctdata"]) == 50 else "FAIL",
        expected="50", actual=str(len(loaded["acctdata"]))))
    report.add(ReconciliationCheck(
        name="Card record count", status="PASS" if len(loaded["carddata"]) == 50 else "FAIL",
        expected="50", actual=str(len(loaded["carddata"]))))
    report.add(ReconciliationCheck(
        name="XREF record count", status="PASS" if len(loaded["cardxref"]) == 50 else "FAIL",
        expected="50", actual=str(len(loaded["cardxref"]))))
    report.add(ReconciliationCheck(
        name="Customer record count", status="PASS" if len(loaded["custdata"]) == 50 else "FAIL",
        expected="50", actual=str(len(loaded["custdata"]))))
    report.add(ReconciliationCheck(
        name="Transaction record count", status="PASS" if len(loaded["dailytran"]) == 300 else "FAIL",
        expected="300", actual=str(len(loaded["dailytran"]))))

    # Cross-reference integrity
    report.add(check_xref_accounts(loaded["cardxref"], loaded["acctdata"]))
    report.add(check_xref_customers(loaded["cardxref"], loaded["custdata"]))
    report.add(check_card_xref_coverage(loaded["carddata"], loaded["cardxref"]))
    report.add(check_transaction_card_refs(loaded["dailytran"], loaded["cardxref"]))

    # Numeric field sums for accounts
    account_sums = compute_field_sums(loaded["acctdata"],
                                      ["ACCT-CURR-BAL", "ACCT-CREDIT-LIMIT"])
    report.add(ReconciliationCheck(
        name="Account balance sum",
        status="PASS" if account_sums["ACCT-CURR-BAL"] == Decimal("12269.00") else "FAIL",
        expected="12269.00",
        actual=str(account_sums["ACCT-CURR-BAL"])))

    # Transaction amount sum
    tran_sums = compute_field_sums(loaded["dailytran"], ["TRAN-AMT"])
    report.add(ReconciliationCheck(
        name="Transaction amount sum",
        status="PASS" if tran_sums["TRAN-AMT"] == Decimal("104801.54") else "FAIL",
        expected="104801.54",
        actual=str(tran_sums["TRAN-AMT"])))

    return report
