"""
Field-by-field comparison of two JSON outputs (golden vs actual).

Reports mismatches with: record number, field name, expected value, actual value.
Returns exit code 0 if identical, 1 if differences found.
"""

from __future__ import annotations

import json
import sys
from dataclasses import dataclass
from pathlib import Path


@dataclass
class Mismatch:
    record_number: int
    field_name: str
    expected: str
    actual: str

    def __str__(self) -> str:
        return (f"Record {self.record_number}, field '{self.field_name}': "
                f"expected={self.expected!r}, actual={self.actual!r}")


def compare_records(
    expected: list[dict], actual: list[dict]
) -> tuple[list[Mismatch], dict]:
    """Compare two lists of parsed records field-by-field.

    Returns (list of mismatches, summary dict).
    """
    mismatches: list[Mismatch] = []

    count_expected = len(expected)
    count_actual = len(actual)

    summary = {
        "expected_count": count_expected,
        "actual_count": count_actual,
        "count_match": count_expected == count_actual,
        "fields_compared": 0,
        "fields_matched": 0,
        "fields_mismatched": 0,
    }

    min_count = min(count_expected, count_actual)
    for i in range(min_count):
        rec_e = expected[i]
        rec_a = actual[i]
        rec_num = rec_e.get("_record_number", i + 1)

        all_keys = set(rec_e.keys()) | set(rec_a.keys())
        all_keys.discard("_record_number")

        for key in sorted(all_keys):
            summary["fields_compared"] += 1
            val_e = rec_e.get(key)
            val_a = rec_a.get(key)

            if str(val_e) == str(val_a):
                summary["fields_matched"] += 1
            else:
                summary["fields_mismatched"] += 1
                mismatches.append(Mismatch(
                    record_number=rec_num,
                    field_name=key,
                    expected=str(val_e),
                    actual=str(val_a),
                ))

    if count_expected > count_actual:
        for i in range(count_actual, count_expected):
            rec_num = expected[i].get("_record_number", i + 1)
            mismatches.append(Mismatch(
                record_number=rec_num,
                field_name="<entire record>",
                expected="<present>",
                actual="<missing>",
            ))
    elif count_actual > count_expected:
        for i in range(count_expected, count_actual):
            rec_num = actual[i].get("_record_number", i + 1)
            mismatches.append(Mismatch(
                record_number=rec_num,
                field_name="<entire record>",
                expected="<missing>",
                actual="<present>",
            ))

    return mismatches, summary


def compare_files(expected_path: Path, actual_path: Path) -> tuple[list[Mismatch], dict]:
    """Compare two golden-file JSONs."""
    with open(expected_path, encoding="utf-8") as f:
        expected_data = json.load(f)
    with open(actual_path, encoding="utf-8") as f:
        actual_data = json.load(f)

    return compare_records(expected_data["records"], actual_data["records"])


def print_report(mismatches: list[Mismatch], summary: dict) -> None:
    """Print a human-readable comparison report."""
    print("=" * 70)
    print("MIGRATION COMPARISON REPORT")
    print("=" * 70)
    print(f"Expected records: {summary['expected_count']}")
    print(f"Actual records:   {summary['actual_count']}")
    print(f"Record count match: {'YES' if summary['count_match'] else 'NO'}")
    print(f"Fields compared:  {summary['fields_compared']}")
    print(f"Fields matched:   {summary['fields_matched']}")
    print(f"Fields mismatched: {summary['fields_mismatched']}")
    print()

    if not mismatches:
        print("RESULT: PASS — All fields match.")
    else:
        print(f"RESULT: FAIL — {len(mismatches)} mismatch(es) found:")
        print("-" * 70)
        for m in mismatches[:50]:
            print(f"  {m}")
        if len(mismatches) > 50:
            print(f"  ... and {len(mismatches) - 50} more")

    print("=" * 70)


def main() -> None:
    if len(sys.argv) != 3:
        print("Usage: python comparator.py <expected.json> <actual.json>")
        sys.exit(1)

    expected_path = Path(sys.argv[1])
    actual_path = Path(sys.argv[2])

    mismatches, summary = compare_files(expected_path, actual_path)
    print_report(mismatches, summary)

    sys.exit(0 if not mismatches else 1)


if __name__ == "__main__":
    main()
