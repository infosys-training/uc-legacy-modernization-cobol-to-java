#!/usr/bin/env python3
"""Generate golden-file JSONs from all ASCII data files in app/data/ASCII/.

Usage:
    python test-harness/generate_golden_files.py [data_dir] [output_dir]

Defaults:
    data_dir   = app/data/ASCII/
    output_dir = golden-files/
"""

import json
import sys
from pathlib import Path

# Add the parser module to path
sys.path.insert(0, str(Path(__file__).parent / "src" / "main" / "python"))
from cobol_parser import LAYOUT_REGISTRY, parse_file


def main() -> None:
    repo_root = Path(__file__).resolve().parent.parent
    data_dir = Path(sys.argv[1]) if len(sys.argv) > 1 else repo_root / "app" / "data" / "ASCII"
    output_dir = Path(sys.argv[2]) if len(sys.argv) > 2 else repo_root / "golden-files"

    output_dir.mkdir(parents=True, exist_ok=True)

    total_records = 0
    for filename in sorted(LAYOUT_REGISTRY.keys()):
        data_file = data_dir / filename
        if not data_file.exists():
            print(f"SKIP  {filename} — file not found at {data_file}")
            continue

        result = parse_file(data_file)
        count = result["metadata"]["record_count"]
        total_records += count

        stem = data_file.stem
        out_path = output_dir / f"{stem}.json"
        out_path.write_text(
            json.dumps(result, indent=2, default=str),
            encoding="utf-8",
        )
        print(f"OK    {filename:20s} → {out_path.name:20s}  ({count} records)")

    print(f"\nTotal: {total_records} records across {len(LAYOUT_REGISTRY)} files")


if __name__ == "__main__":
    main()
