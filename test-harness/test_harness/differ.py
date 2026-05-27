"""
Field-by-field comparison utility for COBOL migration testing.

Compares two parsed record sets (lists of dicts) and reports mismatches
with field names, positions, expected values, and actual values.

Supports:
  - Field-level normalizers (date formats, trailing spaces)
  - Per-field tolerance overrides
  - Comparison mode profiles (strict, tolerant, financial)
  - Key-based record sorting before comparison
"""

import json
import re
from dataclasses import dataclass, field
from decimal import Decimal
from enum import Enum
from pathlib import Path
from typing import Callable, Optional


class ComparisonMode(Enum):
    """Pre-defined comparison profiles."""
    STRICT = "strict"
    TOLERANT = "tolerant"
    FINANCIAL = "financial"


# Default tolerance presets per mode
_MODE_DEFAULTS: dict[ComparisonMode, Optional[Decimal]] = {
    ComparisonMode.STRICT: None,
    ComparisonMode.TOLERANT: Decimal("0.01"),
    ComparisonMode.FINANCIAL: None,
}

# Fields that must always match exactly, regardless of mode
EXACT_MATCH_FIELDS = frozenset({
    "ACCT-ID", "CARD-NUM", "XREF-CARD-NUM", "XREF-CUST-ID", "XREF-ACCT-ID",
    "CUST-ID", "TRAN-ID", "DALYTRAN-ID", "CARD-ACCT-ID", "TRAN-CARD-NUM",
    "DALYTRAN-CARD-NUM", "ACCT-ACTIVE-STATUS", "CARD-ACTIVE-STATUS",
    "CUST-PRI-CARD-HOLDER-IND", "TRANCAT-ACCT-ID",
})

# Date fields that should be normalized before comparison
DATE_FIELDS = frozenset({
    "ACCT-OPEN-DATE", "ACCT-EXPIRAION-DATE", "ACCT-REISSUE-DATE",
    "CARD-EXPIRAION-DATE", "CUST-DOB-YYYY-MM-DD",
})

# Timestamp fields (variable format)
TIMESTAMP_FIELDS = frozenset({
    "TRAN-ORIG-TS", "TRAN-PROC-TS", "DALYTRAN-ORIG-TS", "DALYTRAN-PROC-TS",
})


def normalize_date(value: str) -> str:
    """Normalize date to YYYYMMDD, stripping hyphens and padding."""
    s = str(value).strip().replace("-", "").replace("/", "")
    return s[:8] if len(s) >= 8 else s


def normalize_timestamp(value: str) -> str:
    """Normalize timestamp by stripping trailing spaces and microsecond padding."""
    return str(value).strip()


def normalize_strip(value: str) -> str:
    """Strip leading and trailing whitespace."""
    return str(value).strip()


# Built-in normalizer registry
_BUILTIN_NORMALIZERS: dict[str, Callable] = {}
for _f in DATE_FIELDS:
    _BUILTIN_NORMALIZERS[_f] = normalize_date
for _f in TIMESTAMP_FIELDS:
    _BUILTIN_NORMALIZERS[_f] = normalize_timestamp


@dataclass
class FieldMismatch:
    """A single field-level mismatch between expected and actual."""
    record_index: int
    field_name: str
    expected: str
    actual: str
    key_value: str = ""

    def __str__(self) -> str:
        key_info = f" (key={self.key_value})" if self.key_value else ""
        return (
            f"Record {self.record_index}{key_info}: "
            f"field '{self.field_name}' — "
            f"expected '{self.expected}', got '{self.actual}'"
        )


@dataclass
class DiffReport:
    """Summary of differences between two record sets."""
    file_name: str
    expected_count: int
    actual_count: int
    mismatches: list[FieldMismatch]
    missing_records: int = 0
    extra_records: int = 0

    @property
    def is_match(self) -> bool:
        return (
            self.expected_count == self.actual_count
            and len(self.mismatches) == 0
        )

    @property
    def mismatch_fields(self) -> set[str]:
        """Set of field names that have mismatches."""
        return {m.field_name for m in self.mismatches}

    def mismatches_for_field(self, field_name: str) -> list[FieldMismatch]:
        """Get all mismatches for a specific field."""
        return [m for m in self.mismatches if m.field_name == field_name]

    def summary(self) -> str:
        if self.is_match:
            return f"MATCH {self.file_name}: {self.expected_count} records"
        lines = [
            f"MISMATCH {self.file_name}: {len(self.mismatches)} mismatches"
        ]
        if self.expected_count != self.actual_count:
            lines.append(
                f"  Record count: expected {self.expected_count}, "
                f"got {self.actual_count}"
            )
        for m in self.mismatches[:20]:
            lines.append(f"  {m}")
        if len(self.mismatches) > 20:
            lines.append(f"  ... and {len(self.mismatches) - 20} more")
        return "\n".join(lines)


def _values_equal(
    expected,
    actual,
    field_name: str = "",
    numeric_tolerance: Optional[Decimal] = None,
    field_tolerances: Optional[dict[str, Decimal]] = None,
    normalizers: Optional[dict[str, Callable]] = None,
) -> bool:
    """Compare two values, handling Decimal, string types, normalization, and tolerances."""
    # Apply field-specific normalizer
    normalizer = None
    if normalizers and field_name in normalizers:
        normalizer = normalizers[field_name]
    elif field_name in _BUILTIN_NORMALIZERS:
        normalizer = _BUILTIN_NORMALIZERS[field_name]

    if normalizer and not isinstance(expected, Decimal) and not isinstance(actual, Decimal):
        expected = normalizer(expected) if expected is not None else expected
        actual = normalizer(actual) if actual is not None else actual

    # Determine effective tolerance for this field
    effective_tolerance = numeric_tolerance
    if field_tolerances and field_name in field_tolerances:
        effective_tolerance = field_tolerances[field_name]
    if field_name in EXACT_MATCH_FIELDS:
        effective_tolerance = None

    # Decimal comparison
    if isinstance(expected, Decimal) and isinstance(actual, Decimal):
        if effective_tolerance is not None:
            return abs(expected - actual) <= effective_tolerance
        return expected.compare(actual) == 0

    if isinstance(expected, Decimal):
        try:
            actual_dec = Decimal(str(actual).strip())
            if effective_tolerance is not None:
                return abs(expected - actual_dec) <= effective_tolerance
            return expected.compare(actual_dec) == 0
        except Exception:
            return False

    if isinstance(actual, Decimal):
        try:
            expected_dec = Decimal(str(expected).strip())
            if effective_tolerance is not None:
                return abs(expected_dec - actual) <= effective_tolerance
            return expected_dec.compare(actual) == 0
        except Exception:
            return False

    # String comparison: strip both sides
    return str(expected).strip() == str(actual).strip()


def diff_records(
    expected: list[dict],
    actual: list[dict],
    file_name: str = "unknown",
    key_field: Optional[str] = None,
    numeric_tolerance: Optional[Decimal] = None,
    mode: ComparisonMode = ComparisonMode.STRICT,
    field_tolerances: Optional[dict[str, Decimal]] = None,
    normalizers: Optional[dict[str, Callable]] = None,
    sort_by: Optional[str] = None,
    skip_fields: Optional[set[str]] = None,
) -> DiffReport:
    """Compare two lists of record dicts field-by-field.

    Args:
        expected: The golden/reference records.
        actual: The records from the Java implementation.
        file_name: Label for the comparison report.
        key_field: Optional field name to use as record identifier in reports.
        numeric_tolerance: Global tolerance for numeric comparisons.
        mode: Comparison mode preset (STRICT, TOLERANT, FINANCIAL).
        field_tolerances: Per-field tolerance overrides (e.g., {"ACCT-CURR-BAL": Decimal("0")}).
        normalizers: Per-field normalizer functions (e.g., {"ACCT-OPEN-DATE": normalize_date}).
        sort_by: Sort both record sets by this field before comparison.
        skip_fields: Fields to exclude from comparison entirely.

    Returns:
        DiffReport with all mismatches.
    """
    # Apply mode defaults if no explicit tolerance given
    if numeric_tolerance is None and mode != ComparisonMode.STRICT:
        numeric_tolerance = _MODE_DEFAULTS.get(mode)

    # Sort records if requested
    if sort_by:
        expected = sorted(expected, key=lambda r: str(r.get(sort_by, "")))
        actual = sorted(actual, key=lambda r: str(r.get(sort_by, "")))

    skip = skip_fields or set()

    mismatches: list[FieldMismatch] = []

    compare_count = min(len(expected), len(actual))
    for i in range(compare_count):
        exp_rec = expected[i]
        act_rec = actual[i]
        key_val = str(exp_rec.get(key_field, "")) if key_field else ""

        all_fields = set(exp_rec.keys()) | set(act_rec.keys())
        for field_name in sorted(all_fields):
            if field_name.startswith("_"):
                continue
            if field_name in skip:
                continue

            exp_val = exp_rec.get(field_name)
            act_val = act_rec.get(field_name)

            if exp_val is None and act_val is not None:
                mismatches.append(FieldMismatch(
                    record_index=i,
                    field_name=field_name,
                    expected="<missing>",
                    actual=str(act_val),
                    key_value=key_val,
                ))
            elif exp_val is not None and act_val is None:
                mismatches.append(FieldMismatch(
                    record_index=i,
                    field_name=field_name,
                    expected=str(exp_val),
                    actual="<missing>",
                    key_value=key_val,
                ))
            elif not _values_equal(
                exp_val, act_val,
                field_name=field_name,
                numeric_tolerance=numeric_tolerance,
                field_tolerances=field_tolerances,
                normalizers=normalizers,
            ):
                mismatches.append(FieldMismatch(
                    record_index=i,
                    field_name=field_name,
                    expected=str(exp_val),
                    actual=str(act_val),
                    key_value=key_val,
                ))

    missing = max(0, len(expected) - len(actual))
    extra = max(0, len(actual) - len(expected))
    if missing > 0:
        for i in range(len(actual), len(expected)):
            key_val = str(expected[i].get(key_field, "")) if key_field else ""
            mismatches.append(FieldMismatch(
                record_index=i,
                field_name="<entire record>",
                expected="present",
                actual="<missing>",
                key_value=key_val,
            ))
    if extra > 0:
        for i in range(len(expected), len(actual)):
            key_val = str(actual[i].get(key_field, "")) if key_field else ""
            mismatches.append(FieldMismatch(
                record_index=i,
                field_name="<entire record>",
                expected="<missing>",
                actual="present",
                key_value=key_val,
            ))

    return DiffReport(
        file_name=file_name,
        expected_count=len(expected),
        actual_count=len(actual),
        mismatches=mismatches,
        missing_records=missing,
        extra_records=extra,
    )


def diff_json_files(
    expected_path: Path,
    actual_path: Path,
    key_field: Optional[str] = None,
    numeric_tolerance: Optional[Decimal] = None,
    mode: ComparisonMode = ComparisonMode.STRICT,
    sort_by: Optional[str] = None,
) -> DiffReport:
    """Compare two JSON golden files field-by-field."""

    def _load(path: Path) -> list[dict]:
        with open(path) as f:
            data = json.load(f)
        records = data.get("records", data) if isinstance(data, dict) else data
        result = []
        for rec in records:
            converted = {}
            for k, v in rec.items():
                if isinstance(v, str):
                    try:
                        converted[k] = Decimal(v)
                    except Exception:
                        converted[k] = v
                else:
                    converted[k] = v
            result.append(converted)
        return result

    expected = _load(expected_path)
    actual = _load(actual_path)
    return diff_records(
        expected, actual,
        file_name=expected_path.name,
        key_field=key_field,
        numeric_tolerance=numeric_tolerance,
        mode=mode,
        sort_by=sort_by,
    )
