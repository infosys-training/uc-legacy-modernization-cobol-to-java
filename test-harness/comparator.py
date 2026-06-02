"""
Field-by-Field Output Comparator

Diffs two COBOL/Java output files field-by-field and reports mismatches
with field names, byte positions, expected values, and actual values.
Supports both text (overpunch) and binary (COMP-3, COMP) field types.
"""

import hashlib
import json
from dataclasses import dataclass, field
from decimal import Decimal
from enum import Enum
from pathlib import Path
from typing import Optional

from cobol_parser import (
    FieldDefinition,
    FieldType,
    RecordLayout,
    parse_field,
    parse_record,
)


class MismatchSeverity(Enum):
    """Severity level for field mismatches."""
    CRITICAL = "critical"    # Key field or monetary value mismatch
    WARNING = "warning"      # Non-key field mismatch (e.g., filler, padding)
    INFO = "info"            # Cosmetic difference (trailing spaces, etc.)


@dataclass
class FieldMismatch:
    """A single field-level mismatch between expected and actual output."""
    record_number: int
    field_name: str
    byte_offset: int
    byte_length: int
    pic_clause: str
    expected_raw: str
    actual_raw: str
    expected_decoded: str
    actual_decoded: str
    severity: MismatchSeverity = MismatchSeverity.CRITICAL

    def to_dict(self) -> dict:
        return {
            "record_number": self.record_number,
            "field": self.field_name,
            "byte_position": f"{self.byte_offset}-{self.byte_offset + self.byte_length - 1}",
            "pic_clause": self.pic_clause,
            "expected": self.expected_raw,
            "actual": self.actual_raw,
            "expected_decoded": self.expected_decoded,
            "actual_decoded": self.actual_decoded,
            "severity": self.severity.value,
        }


@dataclass
class ComparisonResult:
    """Result of comparing two output files."""
    expected_file: str
    actual_file: str
    layout_name: str
    total_records_expected: int
    total_records_actual: int
    matching_records: int = 0
    mismatched_records: int = 0
    mismatches: list[FieldMismatch] = field(default_factory=list)
    expected_hash: str = ""
    actual_hash: str = ""

    @property
    def status(self) -> str:
        if self.total_records_expected != self.total_records_actual:
            return "RECORD_COUNT_MISMATCH"
        if self.mismatched_records > 0:
            return "FIELD_MISMATCH"
        return "MATCH"

    def to_dict(self) -> dict:
        return {
            "status": self.status,
            "expected_file": self.expected_file,
            "actual_file": self.actual_file,
            "layout": self.layout_name,
            "total_records_expected": self.total_records_expected,
            "total_records_actual": self.total_records_actual,
            "matching_records": self.matching_records,
            "mismatched_records": self.mismatched_records,
            "expected_sha256": self.expected_hash,
            "actual_sha256": self.actual_hash,
            "mismatches": [m.to_dict() for m in self.mismatches],
        }

    def to_json(self, indent: int = 2) -> str:
        return json.dumps(self.to_dict(), indent=indent)

    def summary(self) -> str:
        lines = [
            f"Comparison: {self.expected_file} vs {self.actual_file}",
            f"  Status: {self.status}",
            f"  Records: {self.total_records_expected} expected, {self.total_records_actual} actual",
            f"  Match: {self.matching_records}, Mismatch: {self.mismatched_records}",
        ]
        if self.mismatches:
            lines.append(f"  First mismatch: record {self.mismatches[0].record_number}, "
                         f"field {self.mismatches[0].field_name}")
        return "\n".join(lines)


def _file_sha256(filepath: str | Path) -> str:
    """Compute SHA-256 hash of a file."""
    h = hashlib.sha256()
    with open(filepath, 'rb') as f:
        for chunk in iter(lambda: f.read(8192), b''):
            h.update(chunk)
    return h.hexdigest()


def _classify_severity(field_def: FieldDefinition) -> MismatchSeverity:
    """Classify the severity of a mismatch based on field type."""
    name_lower = field_def.name.lower()
    if any(k in name_lower for k in ['id', 'key', 'num', 'bal', 'amt',
                                      'limit', 'credit', 'debit', 'rate']):
        return MismatchSeverity.CRITICAL
    if field_def.field_type in (FieldType.SIGNED_DECIMAL,
                                FieldType.COMP3_PACKED,
                                FieldType.COMP_BINARY):
        return MismatchSeverity.CRITICAL
    return MismatchSeverity.WARNING


def compare_records(expected_line: str, actual_line: str,
                    layout: RecordLayout, record_number: int,
                    expected_bytes: Optional[bytes] = None,
                    actual_bytes: Optional[bytes] = None) -> list[FieldMismatch]:
    """Compare two records field-by-field and return a list of mismatches."""
    mismatches = []

    for f in layout.fields:
        if f.field_type == FieldType.FILLER:
            continue

        exp_val = parse_field(expected_line, expected_bytes, f)
        act_val = parse_field(actual_line, actual_bytes, f)

        exp_raw = expected_line[f.offset:f.offset + f.length]
        act_raw = actual_line[f.offset:f.offset + f.length]

        # Normalize for comparison
        exp_str = str(exp_val) if exp_val is not None else ""
        act_str = str(act_val) if act_val is not None else ""

        if exp_str != act_str:
            severity = _classify_severity(f)
            mismatches.append(FieldMismatch(
                record_number=record_number,
                field_name=f.name,
                byte_offset=f.offset,
                byte_length=f.length,
                pic_clause=f.pic,
                expected_raw=exp_raw,
                actual_raw=act_raw,
                expected_decoded=exp_str,
                actual_decoded=act_str,
                severity=severity,
            ))

    return mismatches


def compare_files(expected_path: str | Path, actual_path: str | Path,
                  layout: RecordLayout, binary: bool = False,
                  max_mismatches: int = 100) -> ComparisonResult:
    """
    Compare two COBOL data files field-by-field.

    Args:
        expected_path: Path to the expected (golden/COBOL) output file.
        actual_path: Path to the actual (Java) output file.
        layout: Record layout defining field positions.
        binary: Whether to read files in binary mode (for COMP-3/COMP).
        max_mismatches: Stop collecting after this many total mismatches.

    Returns:
        ComparisonResult with detailed mismatch information.
    """
    expected_path = Path(expected_path)
    actual_path = Path(actual_path)

    result = ComparisonResult(
        expected_file=str(expected_path.name),
        actual_file=str(actual_path.name),
        layout_name=layout.name,
        total_records_expected=0,
        total_records_actual=0,
        expected_hash=_file_sha256(expected_path),
        actual_hash=_file_sha256(actual_path),
    )

    if binary:
        expected_lines, actual_lines = _read_binary_records(
            expected_path, actual_path, layout.record_length)
    else:
        expected_lines = _read_text_records(expected_path, layout.record_length)
        actual_lines = _read_text_records(actual_path, layout.record_length)

    result.total_records_expected = len(expected_lines)
    result.total_records_actual = len(actual_lines)

    compare_count = min(len(expected_lines), len(actual_lines))

    for i in range(compare_count):
        exp_line, exp_bytes = expected_lines[i]
        act_line, act_bytes = actual_lines[i]

        mismatches = compare_records(
            exp_line, act_line, layout, i + 1,
            exp_bytes, act_bytes)

        if mismatches:
            result.mismatched_records += 1
            if len(result.mismatches) < max_mismatches:
                result.mismatches.extend(mismatches)
        else:
            result.matching_records += 1

    return result


def _read_text_records(filepath: Path,
                       record_length: int) -> list[tuple[str, Optional[bytes]]]:
    """Read text records, padding to record_length."""
    records = []
    with open(filepath, 'r', encoding='ascii', errors='replace') as f:
        for line in f:
            line = line.rstrip('\n').rstrip('\r')
            if len(line) < record_length:
                line = line.ljust(record_length)
            records.append((line, None))
    return records


def _read_binary_records(expected_path: Path, actual_path: Path,
                         record_length: int) -> tuple[
                             list[tuple[str, bytes]],
                             list[tuple[str, bytes]]]:
    """Read binary records from two files."""
    def read_bin(path):
        records = []
        with open(path, 'rb') as f:
            while True:
                raw = f.read(record_length)
                if not raw or len(raw) < record_length:
                    break
                text = raw.decode('ascii', errors='replace')
                records.append((text, raw))
        return records

    return read_bin(expected_path), read_bin(actual_path)


def compare_json_golden(golden_json_path: str | Path,
                        actual_records: list[dict],
                        key_fields: Optional[list[str]] = None) -> ComparisonResult:
    """
    Compare parsed records against a golden JSON reference file.

    Args:
        golden_json_path: Path to golden JSON file (from generate_golden_files.py).
        actual_records: List of parsed record dicts from Java output.
        key_fields: Optional fields to use for matching records (if order differs).

    Returns:
        ComparisonResult with field-level mismatches.
    """
    golden_json_path = Path(golden_json_path)
    with open(golden_json_path) as f:
        golden = json.load(f)

    golden_records = golden["records"]
    layout_name = golden["metadata"].get("copybook", "unknown")

    result = ComparisonResult(
        expected_file=str(golden_json_path.name),
        actual_file="<parsed records>",
        layout_name=layout_name,
        total_records_expected=len(golden_records),
        total_records_actual=len(actual_records),
    )

    compare_count = min(len(golden_records), len(actual_records))

    for i in range(compare_count):
        exp = golden_records[i]
        act = actual_records[i]
        record_mismatches = []

        for field_name in exp:
            if field_name.startswith('_'):
                continue
            exp_val = str(exp.get(field_name, ""))
            act_val = str(act.get(field_name, ""))

            if exp_val != act_val:
                record_mismatches.append(FieldMismatch(
                    record_number=i + 1,
                    field_name=field_name,
                    byte_offset=0,
                    byte_length=0,
                    pic_clause="",
                    expected_raw=exp_val,
                    actual_raw=act_val,
                    expected_decoded=exp_val,
                    actual_decoded=act_val,
                ))

        if record_mismatches:
            result.mismatched_records += 1
            result.mismatches.extend(record_mismatches)
        else:
            result.matching_records += 1

    return result
