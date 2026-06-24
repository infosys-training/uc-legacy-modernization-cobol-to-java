"""Reconciliation check functions for CardDemo migration verification.

Provides:
- Record count validation
- Numeric field sum validation
- Cross-reference integrity checks
- Key uniqueness checks
- Checksum computation
"""

import hashlib
from collections import Counter
from dataclasses import dataclass, field
from decimal import Decimal
from pathlib import Path
from typing import Any

from .cobol_parser import parse_data_file
from .copybook_layouts import FILE_LAYOUTS


@dataclass
class CheckResult:
    """Result of a single reconciliation check."""
    name: str
    passed: bool
    message: str
    details: dict[str, Any] = field(default_factory=dict)

    def __str__(self) -> str:
        status = "PASS" if self.passed else "FAIL"
        return f"[{status}] {self.name}: {self.message}"


@dataclass
class ReconciliationReport:
    """Aggregate result of all reconciliation checks."""
    checks: list[CheckResult] = field(default_factory=list)

    @property
    def passed(self) -> bool:
        return all(c.passed for c in self.checks)

    @property
    def pass_count(self) -> int:
        return sum(1 for c in self.checks if c.passed)

    @property
    def fail_count(self) -> int:
        return sum(1 for c in self.checks if not c.passed)

    def summary(self) -> str:
        lines = [
            f"Reconciliation: {'ALL PASS' if self.passed else 'FAILURES DETECTED'}",
            f"  Passed: {self.pass_count}/{len(self.checks)}",
        ]
        for c in self.checks:
            lines.append(f"  {c}")
        return "\n".join(lines)


def check_record_count(file_path: Path, expected_count: int) -> CheckResult:
    """Validate that a data file has the expected number of records."""
    records = parse_data_file(file_path)
    actual = len(records)
    return CheckResult(
        name=f"RecordCount({file_path.name})",
        passed=actual == expected_count,
        message=f"Expected {expected_count}, got {actual}",
        details={"expected": expected_count, "actual": actual},
    )


def check_numeric_sum(
    file_path: Path,
    field_name: str,
    expected_sum: Decimal | None = None,
) -> CheckResult:
    """Validate the sum of a numeric field across all records.

    If expected_sum is None, just computes and reports the sum.
    """
    records = parse_data_file(file_path)
    total = Decimal("0")
    for rec in records:
        val = rec.get(field_name, 0)
        if isinstance(val, Decimal):
            total += val
        elif isinstance(val, (int, float)):
            total += Decimal(str(val))

    if expected_sum is not None:
        passed = total.compare(expected_sum) == 0
        msg = f"Sum({field_name}) = {total}, expected {expected_sum}"
    else:
        passed = True
        msg = f"Sum({field_name}) = {total}"

    return CheckResult(
        name=f"NumericSum({file_path.name}.{field_name})",
        passed=passed,
        message=msg,
        details={"field": field_name, "sum": str(total)},
    )


def check_key_uniqueness(file_path: Path) -> CheckResult:
    """Validate that primary key values are unique across all records."""
    layout = FILE_LAYOUTS.get(file_path.name)
    if layout is None:
        return CheckResult(
            name=f"KeyUnique({file_path.name})",
            passed=False,
            message=f"Unknown file layout: {file_path.name}",
        )

    records = parse_data_file(file_path)
    key_fields = layout.key_fields
    keys = [
        "|".join(str(rec.get(kf, "")) for kf in key_fields)
        for rec in records
    ]
    key_label = "+".join(key_fields)
    counter = Counter(keys)
    duplicates = {k: v for k, v in counter.items() if v > 1}

    return CheckResult(
        name=f"KeyUnique({file_path.name}.{key_label})",
        passed=len(duplicates) == 0,
        message=f"{'No duplicates' if not duplicates else f'{len(duplicates)} duplicate keys: {list(duplicates.keys())[:5]}'}",
        details={"duplicates": duplicates},
    )


def check_referential_integrity(
    child_path: Path,
    child_field: str,
    parent_path: Path,
    parent_field: str,
) -> CheckResult:
    """Validate that every value in child_field exists in parent_field.

    E.g., every XREF-ACCT-ID in cardxref.txt has a matching ACCT-ID in acctdata.txt.
    """
    child_records = parse_data_file(child_path)
    parent_records = parse_data_file(parent_path)

    parent_values = {str(rec.get(parent_field, "")) for rec in parent_records}
    child_values = [str(rec.get(child_field, "")) for rec in child_records]

    orphans = [v for v in child_values if v not in parent_values]

    return CheckResult(
        name=f"RefIntegrity({child_path.name}.{child_field} → {parent_path.name}.{parent_field})",
        passed=len(orphans) == 0,
        message=(
            f"All {len(child_values)} references valid"
            if not orphans
            else f"{len(orphans)} orphan(s): {orphans[:5]}{'...' if len(orphans) > 5 else ''}"
        ),
        details={"orphan_count": len(orphans), "orphans": orphans[:20]},
    )


def compute_file_checksum(file_path: Path) -> str:
    """Compute MD5 checksum of a data file for change detection."""
    md5 = hashlib.md5()
    with open(file_path, "rb") as f:
        for chunk in iter(lambda: f.read(8192), b""):
            md5.update(chunk)
    return md5.hexdigest()


def run_full_reconciliation(data_dir: Path) -> ReconciliationReport:
    """Run all reconciliation checks against the CardDemo ASCII data files.

    Args:
        data_dir: Path to app/data/ASCII/ directory

    Returns:
        ReconciliationReport with all check results
    """
    report = ReconciliationReport()

    # --- Record counts ---
    expected_counts = {
        "acctdata.txt": 50,
        "carddata.txt": 50,
        "cardxref.txt": 50,
        "custdata.txt": 50,
        "dailytran.txt": 300,
        "discgrp.txt": 51,
        "tcatbal.txt": 50,
        "trancatg.txt": 18,
        "trantype.txt": 7,
    }
    for filename, count in expected_counts.items():
        fp = data_dir / filename
        if fp.exists():
            report.checks.append(check_record_count(fp, count))

    # --- Key uniqueness ---
    for filename in FILE_LAYOUTS:
        fp = data_dir / filename
        if fp.exists():
            report.checks.append(check_key_uniqueness(fp))

    # --- Numeric sums (monetary fields) ---
    if (data_dir / "acctdata.txt").exists():
        report.checks.append(
            check_numeric_sum(data_dir / "acctdata.txt", "ACCT-CURR-BAL")
        )
        report.checks.append(
            check_numeric_sum(data_dir / "acctdata.txt", "ACCT-CREDIT-LIMIT")
        )

    if (data_dir / "dailytran.txt").exists():
        report.checks.append(
            check_numeric_sum(data_dir / "dailytran.txt", "DALYTRAN-AMT")
        )

    if (data_dir / "tcatbal.txt").exists():
        report.checks.append(
            check_numeric_sum(data_dir / "tcatbal.txt", "TRAN-CAT-BAL")
        )

    # --- Cross-reference integrity ---
    acct = data_dir / "acctdata.txt"
    cust = data_dir / "custdata.txt"
    xref = data_dir / "cardxref.txt"
    card = data_dir / "carddata.txt"
    tran = data_dir / "dailytran.txt"
    tcatbal = data_dir / "tcatbal.txt"

    if xref.exists() and acct.exists():
        report.checks.append(check_referential_integrity(
            xref, "XREF-ACCT-ID", acct, "ACCT-ID",
        ))

    if xref.exists() and cust.exists():
        report.checks.append(check_referential_integrity(
            xref, "XREF-CUST-ID", cust, "CUST-ID",
        ))

    if card.exists() and xref.exists():
        report.checks.append(check_referential_integrity(
            card, "CARD-NUM", xref, "XREF-CARD-NUM",
        ))

    if tran.exists() and xref.exists():
        report.checks.append(check_referential_integrity(
            tran, "DALYTRAN-CARD-NUM", xref, "XREF-CARD-NUM",
        ))

    if tcatbal.exists() and acct.exists():
        report.checks.append(check_referential_integrity(
            tcatbal, "TRANCAT-ACCT-ID", acct, "ACCT-ID",
        ))

    return report
