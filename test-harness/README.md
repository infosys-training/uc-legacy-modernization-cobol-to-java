# Migration Test Harness

Tools for verifying COBOL-to-Java migration correctness in the CardDemo application.

## Components

| Tool | Purpose |
|------|---------|
| `src/main/python/cobol_parser.py` | Parse fixed-width COBOL data files using copybook PIC clause definitions |
| `src/main/python/comparator.py` | Field-by-field diff of two JSON outputs (golden vs actual) |
| `src/main/python/reconciliation.py` | Record count, sum, cross-ref, and uniqueness checks |
| `generate_golden_files.py` | Generate golden-file JSONs from `app/data/ASCII/` |

## Quick Start

```bash
# Generate golden files from COBOL data
python3 test-harness/generate_golden_files.py

# Run reconciliation checks
python3 test-harness/src/main/python/reconciliation.py golden-files/

# Compare two outputs
python3 test-harness/src/main/python/comparator.py golden-files/acctdata.json actual/acctdata.json

# Parse a single file
python3 test-harness/src/main/python/cobol_parser.py app/data/ASCII/acctdata.txt output.json
```

## Supported Data Files

| File | Copybook | Record Length | Records |
|------|----------|--------------|---------|
| acctdata.txt | CVACT01Y | 300 | 50 |
| carddata.txt | CVACT02Y | 150 | 50 |
| cardxref.txt | CVACT03Y | 50 | 50 |
| custdata.txt | CVCUS01Y | 500 | 50 |
| dailytran.txt | CVTRA06Y | 350 | 300 |
| discgrp.txt | CVTRA02Y | 50 | 51 |
| tcatbal.txt | CVTRA01Y | 50 | 50 |
| trancatg.txt | CVTRA04Y | 60 | 18 |
| trantype.txt | CVTRA03Y | 60 | 7 |

## Overpunch Sign Encoding

COBOL DISPLAY-format signed decimals use trailing overpunch characters:

| Char | Digit | Sign | | Char | Digit | Sign |
|------|-------|------|-|------|-------|------|
| `{` | 0 | + | | `}` | 0 | - |
| `A` | 1 | + | | `J` | 1 | - |
| `B` | 2 | + | | `K` | 2 | - |
| `C` | 3 | + | | `L` | 3 | - |
| `D` | 4 | + | | `M` | 4 | - |
| `E` | 5 | + | | `N` | 5 | - |
| `F` | 6 | + | | `O` | 6 | - |
| `G` | 7 | + | | `P` | 7 | - |
| `H` | 8 | + | | `Q` | 8 | - |
| `I` | 9 | + | | `R` | 9 | - |

Example: `00000001940{` with PIC S9(10)V99 → digits=000000019400, scale=2 → **194.00**

## Requirements

- Python 3.8+
- No external dependencies (stdlib only)
