"""Comparison utility that diffs two outputs field-by-field and reports mismatches.

Compares records from a COBOL (expected) output against a Java (actual) output,
matching by primary key and reporting every field-level mismatch with field name,
byte position, expected value, and actual value.
"""

from dataclasses import dataclass
from decimal import Decimal
from typing import Any

from .copybook_layouts import RecordLayout, FieldDef


@dataclass
class FieldMismatch:
    """A single field-level mismatch between expected and actual records."""
    record_key: str
    field_name: str
    byte_offset: int
    byte_length: int
    expected: Any
    actual: Any
    data_type: str

    def __str__(self) -> str:
        return (
            f"  Key={self.record_key} Field={self.field_name} "
            f"[offset={self.byte_offset}, len={self.byte_length}] "
            f"Expected={self.expected!r} Actual={self.actual!r}"
        )


@dataclass
class ComparisonResult:
    """Result of comparing two record sets."""
    total_expected: int
    total_actual: int
    matched: int
    mismatched_records: int
    field_mismatches: list[FieldMismatch]
    missing_keys: list[str]   # keys in expected but not actual
    extra_keys: list[str]     # keys in actual but not expected

    @property
    def passed(self) -> bool:
        return (
            not self.field_mismatches
            and not self.missing_keys
            and not self.extra_keys
            and self.total_expected == self.total_actual
        )

    def summary(self) -> str:
        lines = [
            f"Comparison: {'PASS' if self.passed else 'FAIL'}",
            f"  Expected records: {self.total_expected}",
            f"  Actual records:   {self.total_actual}",
            f"  Matched:          {self.matched}",
            f"  Mismatched:       {self.mismatched_records}",
            f"  Missing keys:     {len(self.missing_keys)}",
            f"  Extra keys:       {len(self.extra_keys)}",
            f"  Field mismatches: {len(self.field_mismatches)}",
        ]
        if self.missing_keys:
            lines.append(f"  Missing: {self.missing_keys[:10]}{'...' if len(self.missing_keys) > 10 else ''}")
        if self.extra_keys:
            lines.append(f"  Extra:   {self.extra_keys[:10]}{'...' if len(self.extra_keys) > 10 else ''}")
        for m in self.field_mismatches[:20]:
            lines.append(str(m))
        if len(self.field_mismatches) > 20:
            lines.append(f"  ... and {len(self.field_mismatches) - 20} more mismatches")
        return "\n".join(lines)


def _values_equal(expected: Any, actual: Any, data_type: str) -> bool:
    """Compare two field values with type-aware logic."""
    if data_type == "signed_decimal":
        if isinstance(expected, Decimal) and isinstance(actual, Decimal):
            return expected.compare(actual) == 0
        try:
            return Decimal(str(expected)).compare(Decimal(str(actual))) == 0
        except Exception:
            return str(expected) == str(actual)

    if data_type == "numeric":
        try:
            return int(expected) == int(actual)
        except (ValueError, TypeError):
            return str(expected) == str(actual)

    # alphanumeric: trim and compare
    return str(expected).strip() == str(actual).strip()


def compare_records(
    expected: list[dict[str, Any]],
    actual: list[dict[str, Any]],
    layout: RecordLayout,
) -> ComparisonResult:
    """Compare two sets of parsed records field-by-field.

    Args:
        expected: List of records from COBOL output (or golden file)
        actual: List of records from Java output
        layout: RecordLayout defining field positions for mismatch reporting

    Returns:
        ComparisonResult with all mismatches detailed
    """
    key_fields = layout.key_fields

    def _make_key(rec: dict[str, Any]) -> str:
        return "|".join(str(rec.get(kf, "")) for kf in key_fields)

    # Build lookup by key
    expected_by_key: dict[str, dict[str, Any]] = {}
    for rec in expected:
        expected_by_key[_make_key(rec)] = rec

    actual_by_key: dict[str, dict[str, Any]] = {}
    for rec in actual:
        actual_by_key[_make_key(rec)] = rec

    missing = [k for k in expected_by_key if k not in actual_by_key]
    extra = [k for k in actual_by_key if k not in expected_by_key]

    # Field definitions lookup (excluding FILLER)
    field_lookup: dict[str, FieldDef] = {
        f.name: f for f in layout.fields if f.name != "FILLER"
    }

    mismatches: list[FieldMismatch] = []
    mismatched_count = 0
    matched_count = 0

    for key, exp_rec in expected_by_key.items():
        act_rec = actual_by_key.get(key)
        if act_rec is None:
            continue

        record_has_mismatch = False
        for field_name, exp_val in exp_rec.items():
            act_val = act_rec.get(field_name)
            fdef = field_lookup.get(field_name)
            data_type = fdef.data_type if fdef else "alphanumeric"

            if not _values_equal(exp_val, act_val, data_type):
                record_has_mismatch = True
                mismatches.append(FieldMismatch(
                    record_key=key,
                    field_name=field_name,
                    byte_offset=fdef.offset if fdef else -1,
                    byte_length=fdef.length if fdef else -1,
                    expected=exp_val,
                    actual=act_val,
                    data_type=data_type,
                ))

        if record_has_mismatch:
            mismatched_count += 1
        else:
            matched_count += 1

    return ComparisonResult(
        total_expected=len(expected),
        total_actual=len(actual),
        matched=matched_count,
        mismatched_records=mismatched_count,
        field_mismatches=mismatches,
        missing_keys=missing,
        extra_keys=extra,
    )
