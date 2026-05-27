"""Entry point for running test harness reconciliation checks."""

import sys
from pathlib import Path

from test_harness.reconciliation import run_all_checks


def main() -> None:
    if len(sys.argv) < 2:
        print("Usage: python -m test_harness <data_dir>")
        print("  Runs all reconciliation checks against the data directory.")
        sys.exit(1)

    data_dir = Path(sys.argv[1])
    if not data_dir.is_dir():
        print(f"Error: {data_dir} is not a directory")
        sys.exit(1)

    report = run_all_checks(data_dir)
    print(report.summary())
    sys.exit(0 if report.all_passed else 1)


if __name__ == "__main__":
    main()
