# AlconNxt Portal Report Downloader

Python script to automate downloading XLSX reports from the AlconNxt Reports portal.

## What it does

1. Opens the portal at `https://iscls1apps.ad.infosys.com/AlconNxt/Reports/Index`
2. Selects customer **AAAA1111** from the dropdown
3. Clicks **View Report**
4. Downloads the report and saves it as `Report_AAAA1111_YYYY-MM-DD.xlsx`

## Prerequisites

```bash
pip install playwright
playwright install chromium
```

## Usage

```bash
# Basic usage (opens browser, selects AAAA1111, downloads report)
python scripts/download_report.py

# Run headless (no browser window)
python scripts/download_report.py --headless

# Different customer
python scripts/download_report.py --customer BBBB2222

# Custom output directory
python scripts/download_report.py --output-dir /path/to/reports

# All options
python scripts/download_report.py --customer AAAA1111 --output-dir ./reports --headless --timeout 60000
```

## Output

Reports are saved to the `scripts/reports/` directory by default:
```
scripts/reports/Report_AAAA1111_2024-03-15.xlsx
```

## Troubleshooting

- **Portal not reachable**: Ensure you are on the Infosys network or connected via VPN
- **Dropdown not found**: The script tries multiple CSS selector patterns. If it fails, check the debug screenshot and update the selectors in the script
- **Download not triggered**: Some portals use different download mechanisms. Check if the report opens in a new tab or an iframe
