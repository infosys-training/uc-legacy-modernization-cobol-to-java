# CardDemo Migration Test Harness

A Python testing framework for validating COBOL-to-Java migration of the CardDemo application. Provides fixed-width record parsing, field-by-field output comparison, and batch reconciliation checks.

## Components

### cobol_parser.py — Fixed-Width Record Parser
Reads COBOL data files based on copybook PIC clause definitions.

**Supported field types:**
- `PIC X(n)` — Alphanumeric (space-padded)
- `PIC 9(n)` — Unsigned numeric display (zero-padded)
- `PIC S9(n)V99` — Signed numeric with trailing overpunch encoding
- `PIC S9(n)V99 COMP-3` — Packed decimal (2 digits per byte, sign nibble)
- `PIC 9(n) COMP` — Binary integer (big-endian)

**Usage:**
```python
from cobol_parser import FieldDefinition, FieldType, RecordLayout, parse_file

layout = RecordLayout(
    name="ACCOUNT-RECORD",
    record_length=300,
    copybook="CVACT01Y.cpy",
    fields=[
        FieldDefinition("ACCT-ID", 0, 11, "9(11)", FieldType.NUMERIC_DISPLAY),
        FieldDefinition("ACCT-ACTIVE-STATUS", 11, 1, "X(01)", FieldType.ALPHANUMERIC),
        FieldDefinition("ACCT-CURR-BAL", 12, 12, "S9(10)V99", FieldType.SIGNED_DECIMAL, scale=2),
        # ... more fields
    ],
)

records = parse_file("app/data/ASCII/acctdata.txt", layout)
print(records[0])
# {'ACCT-ID': '00000000001', 'ACCT-ACTIVE-STATUS': 'Y', 'ACCT-CURR-BAL': '194.00', ...}
```

### comparator.py — Field-by-Field Output Comparator
Compares expected (COBOL/golden) and actual (Java) output files, reporting mismatches at the field level.

**Usage:**
```python
from comparator import compare_files
from cobol_parser import RecordLayout

result = compare_files("expected_output.dat", "java_output.dat", layout)
print(result.summary())
# Comparison: expected_output.dat vs java_output.dat
#   Status: MATCH
#   Records: 50 expected, 50 actual
#   Match: 50, Mismatch: 0

# For mismatches:
for m in result.mismatches:
    print(f"Record {m.record_number}: {m.field_name} expected={m.expected_decoded} actual={m.actual_decoded}")
```

### reconciliation.py — Batch Reconciliation Checks
Validates batch output integrity: record counts, numeric sums, cross-reference integrity.

**Usage:**
```python
from reconciliation import (
    check_record_count,
    check_field_sum,
    check_xref_accounts,
    reconcile_sample_data,
)
from decimal import Decimal

# Individual checks
result = check_record_count("output.dat", 50)
print(result.status)  # "PASS" or "FAIL"

# Full sample data reconciliation
report = reconcile_sample_data("app/data/ASCII")
print(report.summary())
```

## Running Tests

```bash
cd test-harness
pip install -r requirements.txt
pytest tests/ -v
```

**56 tests covering:**
- Overpunch decode/encode with edge cases (zero, negative, max values)
- COMP-3 packed decimal encode/decode round-trips
- Record parsing against actual sample data (50 account records, 50 xref records)
- File comparison with match/mismatch/count-mismatch scenarios
- All reconciliation functions with synthetic and real data
- Full cross-reference integrity against the sample dataset

## Directory Structure

```
test-harness/
├── README.md
├── pyproject.toml
├── requirements.txt
├── cobol_parser.py        # Fixed-width record parser
├── comparator.py          # Field-by-field diff utility
├── reconciliation.py      # Batch reconciliation checks
└── tests/
    ├── __init__.py
    ├── test_cobol_parser.py
    ├── test_comparator.py
    └── test_reconciliation.py
```
