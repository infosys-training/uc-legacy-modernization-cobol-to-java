#!/usr/bin/env python3
"""CardDemo Migration Test Harness — CLI entry point.

Usage:
    python run_tests.py --suite golden       Generate golden-file JSON from ASCII data
    python run_tests.py --suite reconciliation   Run all reconciliation checks
    python run_tests.py --suite all          Run everything
"""

import argparse
import sys
from pathlib import Path

# Add parent dir to allow running from test-harness/
sys.path.insert(0, str(Path(__file__).parent))

from carddemo_harness.golden_generator import generate_all_golden_files
from carddemo_harness.reconciliation import run_full_reconciliation


def find_data_dir() -> Path:
    """Locate the app/data/ASCII/ directory relative to this script."""
    candidates = [
        Path(__file__).parent.parent / "app" / "data" / "ASCII",
        Path("app/data/ASCII"),
        Path("../app/data/ASCII"),
    ]
    for p in candidates:
        if p.exists():
            return p
    raise FileNotFoundError(
        "Cannot find app/data/ASCII/. Run from the repo root or test-harness/ directory."
    )


def find_golden_dir() -> Path:
    """Locate or create the golden-files/ directory."""
    candidates = [
        Path(__file__).parent.parent / "golden-files",
        Path("golden-files"),
        Path("../golden-files"),
    ]
    for p in candidates:
        if p.exists() or p.parent.exists():
            p.mkdir(exist_ok=True)
            return p
    raise FileNotFoundError("Cannot find golden-files/ directory.")


def main() -> None:
    parser = argparse.ArgumentParser(description="CardDemo Migration Test Harness")
    parser.add_argument(
        "--suite",
        choices=["golden", "reconciliation", "all"],
        default="all",
        help="Test suite to run",
    )
    args = parser.parse_args()

    data_dir = find_data_dir()
    print(f"Data directory: {data_dir}")

    exit_code = 0

    if args.suite in ("golden", "all"):
        print("\n" + "=" * 60)
        print("GENERATING GOLDEN FILES")
        print("=" * 60)
        golden_dir = find_golden_dir()
        generated = generate_all_golden_files(data_dir, golden_dir)
        print(f"\nGenerated {len(generated)} golden files in {golden_dir}/")

    if args.suite in ("reconciliation", "all"):
        print("\n" + "=" * 60)
        print("RUNNING RECONCILIATION CHECKS")
        print("=" * 60)
        report = run_full_reconciliation(data_dir)
        print(report.summary())
        if not report.passed:
            exit_code = 1

    sys.exit(exit_code)


if __name__ == "__main__":
    main()
