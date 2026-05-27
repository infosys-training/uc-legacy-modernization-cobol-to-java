"""
Field-by-field comparison utility for COBOL migration testing.

Compares two parsed record sets (lists of dicts) and reports mismatches
with field names, positions, expected values, and actual values.
"""

import json
from dataclasses import dataclass
from decimal import Decimal
from pathlib import Path
from typing import Optional


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

    def summary(self) -> str:
        if self.is_match:
            return f"✓ {self.file_name}: {self.expected_count} records — MATCH"
        lines = [
            f"✗ {self.file_name}: {len(self.mismatches)} mismatches"
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


def _values_equal(expected, actual, numeric_tolerance: Optional[Decimal] = None) -> bool:
    """Compare two values, handling Decimal and string types."""
    if isinstance(expected, Decimal) and isinstance(actual, Decimal):
        if numeric_tolerance is not None:
            return abs(expected - actual) <= numeric_tolerance
        return expected.compare(actual) == 0

    if isinstance(expected, Decimal):
        try:
            return expected.compare(Decimal(str(actual))) == 0
        except Exception:
            return False

    if isinstance(actual, Decimal):
        try:
            return Decimal(str(expected)).compare(actual) == 0
        except Exception:
            return False

    return str(expected).strip() == str(actual).strip()


def diff_records(
    expected: list[dict],
    actual: list[dict],
    file_name: str = "unknown",
    key_field: Optional[str] = None,
    numeric_tolerance: Optional[Decimal] = None,
) -> DiffReport:
    """Compare two lists of record dicts field-by-field.

    Args:
        expected: The golden/reference records.
        actual: The records from the Java implementation.
        file_name: Label for the comparison report.
        key_field: Optional field name to use as record identifier in reports.
        numeric_tolerance: Optional tolerance for numeric comparisons.

    Returns:
        DiffReport with all mismatches.
    """
    mismatches: list[FieldMismatch] = []

    compare_count = min(len(expected), len(actual))
    for i in range(compare_count):
        exp_rec = expected[i]
        act_rec = actual[i]
        key_val = str(exp_rec.get(key_field, "")) if key_field else ""

        all_fields = set(exp_rec.keys()) | set(act_rec.keys())
        for field in sorted(all_fields):
            if field.startswith("_"):
                continue

            exp_val = exp_rec.get(field)
            act_val = act_rec.get(field)

            if exp_val is None and act_val is not None:
                mismatches.append(FieldMismatch(
                    record_index=i,
                    field_name=field,
                    expected="<missing>",
                    actual=str(act_val),
                    key_value=key_val,
                ))
            elif exp_val is not None and act_val is None:
                mismatches.append(FieldMismatch(
                    record_index=i,
                    field_name=field,
                    expected=str(exp_val),
                    actual="<missing>",
                    key_value=key_val,
                ))
            elif not _values_equal(exp_val, act_val, numeric_tolerance):
                mismatches.append(FieldMismatch(
                    record_index=i,
                    field_name=field,
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
) -> DiffReport:
    """Compare two JSON golden files field-by-field."""

    def _load(path: Path) -> list[dict]:
        with open(path) as f:
            data = json.load(f)
        records = data.get("records", data) if isinstance(data, dict) else data
        # Convert string decimals back to Decimal
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
    )
