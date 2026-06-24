"""Generate golden-file JSON representations from CardDemo ASCII data files.

Parses each fixed-width data file using copybook field definitions and produces
structured JSON with metadata, field definitions, and all parsed records.
"""

import json
from decimal import Decimal
from pathlib import Path
from typing import Any

from .cobol_parser import parse_data_file
from .copybook_layouts import FILE_LAYOUTS, RecordLayout


class _DecimalEncoder(json.JSONEncoder):
    """JSON encoder that converts Decimal to string to preserve precision."""
    def default(self, obj: Any) -> Any:
        if isinstance(obj, Decimal):
            return str(obj)
        return super().default(obj)


def _record_to_json_safe(record: dict[str, Any]) -> dict[str, Any]:
    """Convert a parsed record to JSON-safe types (Decimal → str)."""
    result = {}
    for key, value in record.items():
        if isinstance(value, Decimal):
            result[key] = str(value)
        else:
            result[key] = value
    return result


def generate_golden_file(
    data_path: Path,
    layout: RecordLayout,
    output_path: Path,
) -> dict[str, Any]:
    """Generate a golden-file JSON from a COBOL data file.

    Args:
        data_path: Path to the ASCII data file
        layout: RecordLayout from copybook definitions
        output_path: Path to write the JSON output

    Returns:
        The JSON structure as a dictionary
    """
    records = parse_data_file(data_path)

    field_definitions = []
    for f in layout.fields:
        if f.name == "FILLER":
            continue
        field_definitions.append({
            "field_name": f.name,
            "pic_clause": f.pic_clause,
            "byte_offset": f.offset,
            "byte_length": f.length,
            "data_type": f.data_type,
            "decimal_scale": f.scale,
            "business_meaning": f.business_meaning,
        })

    golden = {
        "metadata": {
            "source_file": data_path.name,
            "copybook": layout.copybook,
            "entity": layout.entity,
            "record_length": layout.record_length,
            "key_field": layout.key_field,
            "record_count": len(records),
        },
        "field_definitions": field_definitions,
        "records": [_record_to_json_safe(r) for r in records],
    }

    output_path.parent.mkdir(parents=True, exist_ok=True)
    with open(output_path, "w", encoding="utf-8") as f:
        json.dump(golden, f, indent=2, cls=_DecimalEncoder)

    return golden


def generate_all_golden_files(
    data_dir: Path,
    output_dir: Path,
) -> list[Path]:
    """Generate golden-file JSON for all known CardDemo data files.

    Args:
        data_dir: Path to app/data/ASCII/
        output_dir: Path to golden-files/

    Returns:
        List of generated JSON file paths
    """
    generated: list[Path] = []

    for filename, layout in FILE_LAYOUTS.items():
        data_path = data_dir / filename
        if not data_path.exists():
            print(f"SKIP: {filename} not found at {data_path}")
            continue

        json_name = filename.replace(".txt", ".json")
        output_path = output_dir / json_name

        print(f"Generating {json_name} from {filename} "
              f"(copybook={layout.copybook}, entity={layout.entity})...")

        generate_golden_file(data_path, layout, output_path)
        generated.append(output_path)

        print(f"  → {output_path} ({layout.record_length}-byte records)")

    return generated
