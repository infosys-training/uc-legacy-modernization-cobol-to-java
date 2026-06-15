"""
Portal Report Downloader - ABC Corporation
===========================================
Automates accessing the AlconNxt Reports portal, selecting Customer AAAA1111
from a dropdown, clicking 'View Report', downloading the XLSX report,
and saving it with a date-stamped filename.

Prerequisites:
    pip install playwright
    playwright install chromium

Usage:
    python download_report.py
    python download_report.py --customer AAAA1111
    python download_report.py --output-dir /path/to/save
    python download_report.py --headless
"""

import argparse
import os
import shutil
from datetime import datetime
from playwright.sync_api import sync_playwright, TimeoutError as PlaywrightTimeout

PORTAL_URL = "https://iscls1apps.ad.infosys.com/AlconNxt/Reports/Index"
DEFAULT_CUSTOMER = "AAAA1111"
DEFAULT_OUTPUT_DIR = os.path.join(os.path.dirname(os.path.abspath(__file__)), "reports")


def parse_args():
    parser = argparse.ArgumentParser(description="Download report from AlconNxt portal")
    parser.add_argument(
        "--customer",
        default=DEFAULT_CUSTOMER,
        help=f"Customer ID to select (default: {DEFAULT_CUSTOMER})",
    )
    parser.add_argument(
        "--output-dir",
        default=DEFAULT_OUTPUT_DIR,
        help=f"Directory to save the report (default: {DEFAULT_OUTPUT_DIR})",
    )
    parser.add_argument(
        "--headless",
        action="store_true",
        help="Run browser in headless mode (no GUI)",
    )
    parser.add_argument(
        "--timeout",
        type=int,
        default=30000,
        help="Timeout in milliseconds for page operations (default: 30000)",
    )
    return parser.parse_args()


def download_report(customer, output_dir, headless, timeout):
    os.makedirs(output_dir, exist_ok=True)

    today = datetime.now().strftime("%Y-%m-%d")
    output_filename = f"Report_{customer}_{today}.xlsx"
    output_path = os.path.join(output_dir, output_filename)

    print(f"Portal URL  : {PORTAL_URL}")
    print(f"Customer    : {customer}")
    print(f"Output file : {output_path}")
    print(f"Headless    : {headless}")
    print()

    with sync_playwright() as p:
        browser = p.chromium.launch(headless=headless)
        context = browser.new_context(accept_downloads=True)
        page = context.new_page()

        # Step 1: Navigate to the portal
        print("[1/4] Navigating to the portal...")
        try:
            page.goto(PORTAL_URL, wait_until="networkidle", timeout=timeout)
        except PlaywrightTimeout:
            print("  Warning: Page load timed out, proceeding anyway...")
        print("  Page loaded successfully.")

        # Step 2: Select customer from dropdown
        print(f"[2/4] Selecting customer '{customer}' from dropdown...")

        # Try multiple common selector patterns for the customer dropdown
        customer_selectors = [
            "select[name*='ustomer' i]",
            "select[id*='ustomer' i]",
            "select[name*='Customer']",
            "select[id*='Customer']",
            "select[name*='customer']",
            "select[id*='customer']",
            "#CustomerDropdown",
            "#ddlCustomer",
            "select.customer-dropdown",
            "select",
        ]

        dropdown_found = False
        for selector in customer_selectors:
            try:
                dropdown = page.locator(selector).first
                if dropdown.is_visible(timeout=3000):
                    # Try selecting by value first, then by label
                    try:
                        dropdown.select_option(value=customer, timeout=5000)
                    except Exception:
                        try:
                            dropdown.select_option(label=customer, timeout=5000)
                        except Exception:
                            # Try partial text match
                            options = dropdown.locator("option").all()
                            for option in options:
                                text = option.text_content()
                                if customer in text:
                                    dropdown.select_option(
                                        label=text.strip(), timeout=5000
                                    )
                                    break

                    dropdown_found = True
                    print(f"  Customer '{customer}' selected (selector: {selector}).")
                    break
            except Exception:
                continue

        if not dropdown_found:
            print(f"  ERROR: Could not find customer dropdown. Available selects:")
            selects = page.locator("select").all()
            for i, s in enumerate(selects):
                name = s.get_attribute("name") or s.get_attribute("id") or "unknown"
                print(f"    [{i}] name/id={name}")
            browser.close()
            return False

        # Small wait for any dynamic content to load after selection
        page.wait_for_timeout(2000)

        # Step 3: Click 'View Report'
        print("[3/4] Clicking 'View Report'...")

        view_report_selectors = [
            "text=View Report",
            "button:has-text('View Report')",
            "input[value='View Report']",
            "a:has-text('View Report')",
            "#btnViewReport",
            "button:has-text('View')",
            "input[type='submit'][value*='View']",
        ]

        button_found = False
        for selector in view_report_selectors:
            try:
                btn = page.locator(selector).first
                if btn.is_visible(timeout=3000):
                    btn.click()
                    button_found = True
                    print(f"  'View Report' clicked (selector: {selector}).")
                    break
            except Exception:
                continue

        if not button_found:
            print("  ERROR: Could not find 'View Report' button.")
            browser.close()
            return False

        # Wait for report to render
        print("  Waiting for report to render...")
        page.wait_for_timeout(5000)

        # Step 4: Download the report
        print("[4/4] Downloading report...")

        download_selectors = [
            "text=Download",
            "text=Export",
            "a:has-text('Download')",
            "button:has-text('Download')",
            "a:has-text('Export')",
            "button:has-text('Export')",
            "a:has-text('Excel')",
            "button:has-text('Excel')",
            "a[href*='download' i]",
            "a[href*='export' i]",
            "#btnDownload",
            "#btnExport",
            "input[value*='Download']",
            "input[value*='Export']",
        ]

        download_triggered = False
        for selector in download_selectors:
            try:
                dl_btn = page.locator(selector).first
                if dl_btn.is_visible(timeout=3000):
                    with page.expect_download(timeout=timeout) as download_info:
                        dl_btn.click()
                    download = download_info.value
                    download.save_as(output_path)
                    download_triggered = True
                    print(f"  Report downloaded successfully.")
                    break
            except Exception:
                continue

        if not download_triggered:
            # Fallback: try triggering download via the View Report button itself
            # Some portals directly download on View Report click
            print("  No separate download button found.")
            print("  Retrying: triggering download via 'View Report'...")
            try:
                for selector in view_report_selectors:
                    try:
                        btn = page.locator(selector).first
                        if btn.is_visible(timeout=2000):
                            with page.expect_download(timeout=timeout) as download_info:
                                btn.click()
                            download = download_info.value
                            download.save_as(output_path)
                            download_triggered = True
                            print(f"  Report downloaded via 'View Report'.")
                            break
                    except Exception:
                        continue
            except Exception:
                pass

        if not download_triggered:
            print("  ERROR: Could not trigger report download.")
            print("  Taking a screenshot for debugging...")
            screenshot_path = os.path.join(
                output_dir, f"debug_screenshot_{today}.png"
            )
            page.screenshot(path=screenshot_path, full_page=True)
            print(f"  Screenshot saved to: {screenshot_path}")
            browser.close()
            return False

        browser.close()

    print()
    print(f"Report saved to: {output_path}")
    print(f"File size: {os.path.getsize(output_path):,} bytes")
    return True


def main():
    args = parse_args()
    print("=" * 55)
    print("  AlconNxt Portal Report Downloader")
    print("=" * 55)
    print()

    success = download_report(
        customer=args.customer,
        output_dir=args.output_dir,
        headless=args.headless,
        timeout=args.timeout,
    )

    if success:
        print("\nDone!")
    else:
        print("\nFailed. Check the error messages above.")
        print("You may need to adjust the CSS selectors in the script")
        print("to match your portal's actual HTML structure.")
        exit(1)


if __name__ == "__main__":
    main()
