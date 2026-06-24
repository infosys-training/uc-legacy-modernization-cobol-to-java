"""Shared fixtures for CardDemo test harness tests."""

from pathlib import Path

import pytest


@pytest.fixture
def data_dir() -> Path:
    """Path to app/data/ASCII/ directory."""
    p = Path(__file__).parent.parent.parent / "app" / "data" / "ASCII"
    assert p.exists(), f"Data directory not found: {p}"
    return p


@pytest.fixture
def golden_dir() -> Path:
    """Path to golden-files/ directory."""
    p = Path(__file__).parent.parent.parent / "golden-files"
    assert p.exists(), f"Golden-files directory not found: {p}"
    return p
