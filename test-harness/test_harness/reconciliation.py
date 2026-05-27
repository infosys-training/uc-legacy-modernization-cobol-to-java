"""
Reconciliation check functions for CardDemo batch migration.

Validates record counts, numeric field sums, and cross-reference
integrity across the CardDemo data files.
"""

from dataclasses import dataclass, field
from decimal import Decimal
from pathlib import Path
from typing import Optional

from test_harness.copybook_layouts import (
    ACCOUNT_RECORD,
    CARD_RECORD,
    CARD_XREF_RECORD,
    CUSTOMER_RECORD,
    DAILY_TRANSACTION_RECORD,
    DISCLOSURE_GROUP_RECORD,
    TRAN_CAT_BAL_RECORD,
)
from test_harness.parser import parse_file


@dataclass
class CheckResult:
    """Result of a single reconciliation check."""
    check_name: str
    passed: bool
    message: str
    details: Optional[str] = None

    def __str__(self) -> str:
        status = "PASS" if self.passed else "FAIL"
        s = f"[{status}] {self.check_name}: {self.message}"
        if self.details:
            s += f"\n       {self.details}"
        return s


@dataclass
class ReconciliationReport:
    """Summary of all reconciliation checks."""
    checks: list[CheckResult] = field(default_factory=list)

    @property
    def all_passed(self) -> bool:
        return all(c.passed for c in self.checks)

    @property
    def pass_count(self) -> int:
        return sum(1 for c in self.checks if c.passed)

    @property
    def fail_count(self) -> int:
        return sum(1 for c in self.checks if not c.passed)

    def summary(self) -> str:
        lines = [f"Reconciliation: {self.pass_count} passed, {self.fail_count} failed"]
        for c in self.checks:
            lines.append(f"  {c}")
        return "\n".join(lines)


def check_record_count(
    filepath: Path, layout: list, expected_count: Optional[int] = None
) -> CheckResult:
    """Validate the number of records in a data file."""
    records = parse_file(filepath, layout)
    actual = len(records)
    if expected_count is not None:
        passed = actual == expected_count
        return CheckResult(
            check_name=f"Record count: {filepath.name}",
            passed=passed,
            message=f"{actual} records" + ("" if passed else f" (expected {expected_count})"),
        )
    return CheckResult(
        check_name=f"Record count: {filepath.name}",
        passed=actual > 0,
        message=f"{actual} records",
    )


def check_numeric_sum(
    filepath: Path, layout: list, field_name: str, expected_sum: Optional[Decimal] = None
) -> CheckResult:
    """Sum a numeric field across all records and optionally compare to expected."""
    records = parse_file(filepath, layout)
    total = Decimal("0")
    for rec in records:
        val = rec.get(field_name)
        if isinstance(val, Decimal):
            total += val
        elif val is not None:
            try:
                total += Decimal(str(val))
            except Exception:
                pass

    if expected_sum is not None:
        passed = total.compare(expected_sum) == 0
        return CheckResult(
            check_name=f"Numeric sum: {filepath.name}.{field_name}",
            passed=passed,
            message=f"sum={total}" + ("" if passed else f" (expected {expected_sum})"),
        )
    return CheckResult(
        check_name=f"Numeric sum: {filepath.name}.{field_name}",
        passed=True,
        message=f"sum={total}",
    )


def check_xref_integrity(data_dir: Path) -> list[CheckResult]:
    """Verify cross-reference integrity across CardDemo data files.

    Checks:
    1. Every card in cardxref has a matching account in acctdata
    2. Every card in cardxref has a matching customer in custdata
    3. Every card in carddata has a matching entry in cardxref
    4. Every daily transaction card number has a matching entry in cardxref
    """
    results: list[CheckResult] = []

    xref_path = data_dir / "cardxref.txt"
    acct_path = data_dir / "acctdata.txt"
    cust_path = data_dir / "custdata.txt"
    card_path = data_dir / "carddata.txt"
    dtran_path = data_dir / "dailytran.txt"

    xref_records = parse_file(xref_path, CARD_XREF_RECORD) if xref_path.exists() else []
    acct_records = parse_file(acct_path, ACCOUNT_RECORD) if acct_path.exists() else []
    cust_records = parse_file(cust_path, CUSTOMER_RECORD) if cust_path.exists() else []
    card_records = parse_file(card_path, CARD_RECORD) if card_path.exists() else []

    acct_ids = {str(r["ACCT-ID"]).strip() for r in acct_records}
    cust_ids = {str(r["CUST-ID"]).strip() for r in cust_records}
    xref_card_nums = {str(r["XREF-CARD-NUM"]).strip() for r in xref_records}

    # Check 1: XREF → ACCT
    orphan_accts = []
    for xr in xref_records:
        acct_id = str(xr["XREF-ACCT-ID"]).strip()
        if acct_id not in acct_ids:
            orphan_accts.append(
                f"card={xr['XREF-CARD-NUM'].strip()}, acct={acct_id}"
            )
    results.append(CheckResult(
        check_name="XREF→ACCT integrity",
        passed=len(orphan_accts) == 0,
        message=f"{len(xref_records)} xref records, {len(orphan_accts)} orphaned",
        details="; ".join(orphan_accts[:5]) if orphan_accts else None,
    ))

    # Check 2: XREF → CUST
    orphan_custs = []
    for xr in xref_records:
        cust_id = str(xr["XREF-CUST-ID"]).strip()
        if cust_id not in cust_ids:
            orphan_custs.append(
                f"card={xr['XREF-CARD-NUM'].strip()}, cust={cust_id}"
            )
    results.append(CheckResult(
        check_name="XREF→CUST integrity",
        passed=len(orphan_custs) == 0,
        message=f"{len(xref_records)} xref records, {len(orphan_custs)} orphaned",
        details="; ".join(orphan_custs[:5]) if orphan_custs else None,
    ))

    # Check 3: CARD → XREF
    orphan_cards = []
    for cr in card_records:
        card_num = str(cr["CARD-NUM"]).strip()
        if card_num not in xref_card_nums:
            orphan_cards.append(card_num)
    results.append(CheckResult(
        check_name="CARD→XREF integrity",
        passed=len(orphan_cards) == 0,
        message=f"{len(card_records)} cards, {len(orphan_cards)} not in xref",
        details="; ".join(orphan_cards[:5]) if orphan_cards else None,
    ))

    # Check 4: DAILYTRAN → XREF
    if dtran_path.exists():
        dtran_records = parse_file(dtran_path, DAILY_TRANSACTION_RECORD)
        orphan_dtrans = []
        for dt in dtran_records:
            card_num = str(dt["DALYTRAN-CARD-NUM"]).strip()
            if card_num not in xref_card_nums:
                orphan_dtrans.append(card_num)
        unique_orphans = set(orphan_dtrans)
        results.append(CheckResult(
            check_name="DAILYTRAN→XREF integrity",
            passed=len(orphan_dtrans) == 0,
            message=(
                f"{len(dtran_records)} daily transactions, "
                f"{len(orphan_dtrans)} with unknown cards "
                f"({len(unique_orphans)} unique)"
            ),
            details="; ".join(list(unique_orphans)[:5]) if unique_orphans else None,
        ))

    return results


def check_tcatbal_acct_integrity(data_dir: Path) -> CheckResult:
    """Every account in TCATBALF exists in ACCTDAT."""
    tcatbal_path = data_dir / "tcatbal.txt"
    acct_path = data_dir / "acctdata.txt"

    if not tcatbal_path.exists() or not acct_path.exists():
        return CheckResult(
            check_name="TCATBAL→ACCT integrity",
            passed=True,
            message="Skipped — file not found",
        )

    tcatbal_records = parse_file(tcatbal_path, TRAN_CAT_BAL_RECORD)
    acct_records = parse_file(acct_path, ACCOUNT_RECORD)
    acct_ids = {str(r["ACCT-ID"]).strip() for r in acct_records}

    orphans = []
    for tc in tcatbal_records:
        acct_id = str(tc["TRANCAT-ACCT-ID"]).strip()
        if acct_id not in acct_ids:
            orphans.append(acct_id)

    return CheckResult(
        check_name="TCATBAL→ACCT integrity",
        passed=len(orphans) == 0,
        message=f"{len(tcatbal_records)} category balances, {len(orphans)} orphaned",
        details="; ".join(set(orphans[:5])) if orphans else None,
    )


def check_discgrp_coverage(data_dir: Path) -> CheckResult:
    """Every account group in ACCTDAT has disclosure group entries in DISCGRP."""
    acct_path = data_dir / "acctdata.txt"
    discgrp_path = data_dir / "discgrp.txt"

    if not acct_path.exists() or not discgrp_path.exists():
        return CheckResult(
            check_name="ACCT→DISCGRP coverage",
            passed=True,
            message="Skipped — file not found",
        )

    acct_records = parse_file(acct_path, ACCOUNT_RECORD)
    discgrp_records = parse_file(discgrp_path, DISCLOSURE_GROUP_RECORD)

    discgrp_groups = {str(r["DIS-ACCT-GROUP-ID"]).strip() for r in discgrp_records}
    acct_groups = {str(r["ACCT-GROUP-ID"]).strip() for r in acct_records}

    missing = acct_groups - discgrp_groups
    return CheckResult(
        check_name="ACCT→DISCGRP coverage",
        passed=len(missing) == 0,
        message=(
            f"{len(acct_groups)} account groups, "
            f"{len(discgrp_groups)} disclosure groups, "
            f"{len(missing)} groups missing from DISCGRP"
        ),
        details="; ".join(sorted(missing)[:5]) if missing else None,
    )


def run_all_checks(data_dir: Path) -> ReconciliationReport:
    """Run all reconciliation checks against the data directory."""
    report = ReconciliationReport()
    data = Path(data_dir)

    # Record counts
    file_layout_pairs = [
        ("acctdata.txt",  ACCOUNT_RECORD,               50),
        ("carddata.txt",  CARD_RECORD,                   50),
        ("cardxref.txt",  CARD_XREF_RECORD,              50),
        ("custdata.txt",  CUSTOMER_RECORD,               50),
        ("dailytran.txt", DAILY_TRANSACTION_RECORD,     300),
        ("discgrp.txt",   DISCLOSURE_GROUP_RECORD,       51),
        ("tcatbal.txt",   TRAN_CAT_BAL_RECORD,           50),
        ("trancatg.txt",  None,                          18),
        ("trantype.txt",  None,                           7),
    ]
    for fname, layout, expected in file_layout_pairs:
        fpath = data / fname
        if fpath.exists() and layout is not None:
            report.checks.append(check_record_count(fpath, layout, expected))

    # Numeric sums
    if (data / "acctdata.txt").exists():
        report.checks.append(
            check_numeric_sum(data / "acctdata.txt", ACCOUNT_RECORD, "ACCT-CURR-BAL")
        )
        report.checks.append(
            check_numeric_sum(data / "acctdata.txt", ACCOUNT_RECORD, "ACCT-CREDIT-LIMIT")
        )
    if (data / "tcatbal.txt").exists():
        report.checks.append(
            check_numeric_sum(data / "tcatbal.txt", TRAN_CAT_BAL_RECORD, "TRAN-CAT-BAL")
        )
    if (data / "dailytran.txt").exists():
        report.checks.append(
            check_numeric_sum(
                data / "dailytran.txt", DAILY_TRANSACTION_RECORD, "DALYTRAN-AMT"
            )
        )

    # Cross-reference integrity
    report.checks.extend(check_xref_integrity(data))

    # TCATBAL → ACCT
    report.checks.append(check_tcatbal_acct_integrity(data))

    # DISCGRP coverage
    report.checks.append(check_discgrp_coverage(data))

    return report
