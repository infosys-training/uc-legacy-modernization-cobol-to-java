"""
Reconciliation check functions for COBOL-to-Java migration.

Provides:
- Record count validation
- Numeric field sum validation
- Cross-reference integrity checks
- Primary key uniqueness checks
- Range validation for numeric fields
"""

from __future__ import annotations

import json
import sys
from dataclasses import dataclass
from decimal import Decimal
from pathlib import Path
from typing import Any


@dataclass
class CheckResult:
    check_name: str
    passed: bool
    details: str

    def __str__(self) -> str:
        status = "PASS" if self.passed else "FAIL"
        return f"[{status}] {self.check_name}: {self.details}"


def load_records(json_path: Path) -> list[dict]:
    """Load records from a golden-file JSON."""
    with open(json_path, encoding="utf-8") as f:
        data = json.load(f)
    return data.get("records", [])


def check_record_count(records: list[dict], expected: int, label: str = "") -> CheckResult:
    """Verify record count matches expected."""
    actual = len(records)
    return CheckResult(
        check_name=f"Record count{' (' + label + ')' if label else ''}",
        passed=actual == expected,
        details=f"expected={expected}, actual={actual}"
    )


def check_numeric_sum(
    records: list[dict], field: str, expected_sum: str | None = None
) -> CheckResult:
    """Sum a numeric/decimal field across all records.

    If expected_sum is provided, verify it matches. Otherwise just report.
    """
    total = Decimal("0")
    for rec in records:
        val = rec.get(field, "0")
        try:
            total += Decimal(str(val))
        except Exception:
            pass

    if expected_sum is not None:
        expected = Decimal(expected_sum)
        passed = total == expected
        return CheckResult(
            check_name=f"Sum({field})",
            passed=passed,
            details=f"expected={expected}, actual={total}"
        )
    return CheckResult(
        check_name=f"Sum({field})",
        passed=True,
        details=f"total={total} (no expected value provided — informational)"
    )


def check_cross_reference(
    source_records: list[dict],
    source_fk_field: str,
    target_records: list[dict],
    target_pk_field: str,
    label: str = "",
) -> CheckResult:
    """Verify every foreign key in source exists as a primary key in target."""
    target_keys = set()
    for rec in target_records:
        pk = rec.get(target_pk_field)
        if pk is not None:
            target_keys.add(str(pk))

    missing: list[str] = []
    for rec in source_records:
        fk = rec.get(source_fk_field)
        if fk is not None and str(fk) not in target_keys:
            missing.append(str(fk))

    passed = len(missing) == 0
    desc = label or f"{source_fk_field} → {target_pk_field}"
    if passed:
        details = f"All {len(source_records)} references resolved"
    else:
        details = f"{len(missing)} missing: {missing[:10]}"
        if len(missing) > 10:
            details += f" ... +{len(missing) - 10} more"

    return CheckResult(check_name=f"Cross-ref({desc})", passed=passed, details=details)


def check_uniqueness(records: list[dict], key_field: str) -> CheckResult:
    """Verify primary key field is unique across all records."""
    seen: dict[str, int] = {}
    duplicates: list[str] = []

    for rec in records:
        key = str(rec.get(key_field, ""))
        if key in seen:
            duplicates.append(key)
        seen[key] = seen.get(key, 0) + 1

    passed = len(duplicates) == 0
    if passed:
        details = f"All {len(records)} keys are unique"
    else:
        details = f"{len(duplicates)} duplicate(s): {duplicates[:10]}"

    return CheckResult(
        check_name=f"Uniqueness({key_field})",
        passed=passed,
        details=details,
    )


def check_range(
    records: list[dict],
    field: str,
    min_val: Decimal | None = None,
    max_val: Decimal | None = None,
) -> CheckResult:
    """Verify numeric field values are within expected bounds."""
    violations: list[tuple[int, Decimal]] = []

    for rec in records:
        val_str = rec.get(field, "0")
        try:
            val = Decimal(str(val_str))
        except Exception:
            continue

        rec_num = rec.get("_record_number", 0)
        if min_val is not None and val < min_val:
            violations.append((rec_num, val))
        if max_val is not None and val > max_val:
            violations.append((rec_num, val))

    passed = len(violations) == 0
    bounds = []
    if min_val is not None:
        bounds.append(f"min={min_val}")
    if max_val is not None:
        bounds.append(f"max={max_val}")

    if passed:
        details = f"All {len(records)} values within {', '.join(bounds)}"
    else:
        details = f"{len(violations)} violation(s): {violations[:5]}"

    return CheckResult(
        check_name=f"Range({field})",
        passed=passed,
        details=details,
    )


def run_all_checks(golden_dir: Path) -> list[CheckResult]:
    """Run all reconciliation checks against golden files."""
    results: list[CheckResult] = []

    files_to_load: dict[str, list[dict]] = {}
    for json_file in sorted(golden_dir.glob("*.json")):
        records = load_records(json_file)
        files_to_load[json_file.stem] = records

    # ── Account checks ──
    if "acctdata" in files_to_load:
        accts = files_to_load["acctdata"]
        results.append(check_record_count(accts, 50, "acctdata"))
        results.append(check_uniqueness(accts, "ACCT-ID"))
        results.append(check_numeric_sum(accts, "ACCT-CURR-BAL"))
        results.append(check_numeric_sum(accts, "ACCT-CREDIT-LIMIT"))

    # ── Card checks ──
    if "carddata" in files_to_load:
        cards = files_to_load["carddata"]
        results.append(check_record_count(cards, 50, "carddata"))
        results.append(check_uniqueness(cards, "CARD-NUM"))
        if "acctdata" in files_to_load:
            results.append(check_cross_reference(
                cards, "CARD-ACCT-ID",
                files_to_load["acctdata"], "ACCT-ID",
                "CARD → ACCOUNT"
            ))

    # ── Card XREF checks ──
    if "cardxref" in files_to_load:
        xrefs = files_to_load["cardxref"]
        results.append(check_record_count(xrefs, 50, "cardxref"))
        results.append(check_uniqueness(xrefs, "XREF-CARD-NUM"))
        if "acctdata" in files_to_load:
            results.append(check_cross_reference(
                xrefs, "XREF-ACCT-ID",
                files_to_load["acctdata"], "ACCT-ID",
                "XREF → ACCOUNT"
            ))
        if "custdata" in files_to_load:
            results.append(check_cross_reference(
                xrefs, "XREF-CUST-ID",
                files_to_load["custdata"], "CUST-ID",
                "XREF → CUSTOMER"
            ))

    # ── Customer checks ──
    if "custdata" in files_to_load:
        custs = files_to_load["custdata"]
        results.append(check_record_count(custs, 50, "custdata"))
        results.append(check_uniqueness(custs, "CUST-ID"))
        results.append(check_range(
            custs, "CUST-FICO-CREDIT-SCORE",
            min_val=Decimal("0"), max_val=Decimal("850")
        ))

    # ── Daily transaction checks ──
    if "dailytran" in files_to_load:
        txns = files_to_load["dailytran"]
        results.append(check_record_count(txns, 300, "dailytran"))
        results.append(check_numeric_sum(txns, "TRAN-AMT"))

    # ── Disclosure group checks ──
    if "discgrp" in files_to_load:
        dgs = files_to_load["discgrp"]
        results.append(check_record_count(dgs, 51, "discgrp"))

    # ── Tran category balance checks ──
    if "tcatbal" in files_to_load:
        tcbs = files_to_load["tcatbal"]
        results.append(check_record_count(tcbs, 50, "tcatbal"))
        results.append(check_numeric_sum(tcbs, "TRAN-CAT-BAL"))

    # ── Transaction category checks ──
    if "trancatg" in files_to_load:
        cats = files_to_load["trancatg"]
        results.append(check_record_count(cats, 18, "trancatg"))

    # ── Transaction type checks ──
    if "trantype" in files_to_load:
        types = files_to_load["trantype"]
        results.append(check_record_count(types, 7, "trantype"))
        results.append(check_uniqueness(types, "TRAN-TYPE"))

    return results


def print_results(results: list[CheckResult]) -> int:
    """Print results and return exit code (0=all pass, 1=failures)."""
    print("=" * 70)
    print("RECONCILIATION CHECK REPORT")
    print("=" * 70)

    passed = sum(1 for r in results if r.passed)
    failed = sum(1 for r in results if not r.passed)

    for r in results:
        print(f"  {r}")
    print()
    print(f"Total: {len(results)} checks, {passed} passed, {failed} failed")
    print("=" * 70)

    return 0 if failed == 0 else 1


def main() -> None:
    if len(sys.argv) < 2:
        print("Usage: python reconciliation.py <golden-files-dir>")
        sys.exit(1)

    golden_dir = Path(sys.argv[1])
    if not golden_dir.is_dir():
        print(f"Error: {golden_dir} is not a directory")
        sys.exit(1)

    results = run_all_checks(golden_dir)
    exit_code = print_results(results)
    sys.exit(exit_code)


if __name__ == "__main__":
    main()
