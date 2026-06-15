# AlconNxt Portal Report Downloader

Python script to automate downloading XLSX reports from the AlconNxt Reports portal.

## What it does

1. Opens the portal at `https://iscls1apps.ad.infosys.com/AlconNxt/Reports/Index`
2. Selects customer **AAAA1111** from the dropdown
3. Clicks **View Report**
4. Downloads the report and saves it as `Report_AAAA1111_YYYY-MM-DD.xlsx`

## Prerequisites (pick one)

**Option 1 — Selenium (recommended):**
```bash
pip install selenium webdriver-manager
```

**Option 2 — Playwright:**
```bash
pip install playwright
playwright install chromium
```

**Option 3 — Requests (no browser needed, limited):**
```bash
pip install requests beautifulsoup4
```

## Usage

```bash
# Auto-detect best available backend
python scripts/download_report.py

# Run headless (no browser window)
python scripts/download_report.py --headless

# Force a specific backend
python scripts/download_report.py --backend selenium
python scripts/download_report.py --backend playwright
python scripts/download_report.py --backend requests

# Different customer
python scripts/download_report.py --customer BBBB2222

# Custom output directory
python scripts/download_report.py --output-dir /path/to/reports

# All options
python scripts/download_report.py --customer AAAA1111 --output-dir ./reports --headless --backend selenium --timeout 60
```

## Output

Reports are saved to the `scripts/reports/` directory by default:
```
scripts/reports/Report_AAAA1111_2024-03-15.xlsx
```

## Troubleshooting

- **ModuleNotFoundError**: Install one of the three backend options listed above
- **Portal not reachable**: Ensure you are on the Infosys network or connected via VPN
- **Dropdown not found**: The script tries multiple CSS selector patterns; check the debug screenshot and update selectors if needed
- **Download not triggered**: Some portals use different download mechanisms; check if the report opens in a new tab or iframe
