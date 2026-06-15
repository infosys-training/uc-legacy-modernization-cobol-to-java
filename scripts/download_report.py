"""
Portal Report Downloader - ABC Corporation
===========================================
Automates accessing the AlconNxt Reports portal, selecting Customer AAAA1111
from a dropdown, clicking 'View Report', downloading the XLSX report,
and saving it with a date-stamped filename.

Supports three automation backends (tries in order):
  1. Selenium (pip install selenium webdriver-manager)
  2. Playwright (pip install playwright && playwright install chromium)
  3. Requests + BeautifulSoup (pip install requests beautifulsoup4)

Usage:
    python download_report.py
    python download_report.py --customer AAAA1111
    python download_report.py --output-dir /path/to/save
    python download_report.py --headless
    python download_report.py --backend selenium
    python download_report.py --backend playwright
    python download_report.py --backend requests
"""

import argparse
import glob
import os
import shutil
import time
from datetime import datetime

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
        default=30,
        help="Timeout in seconds for page operations (default: 30)",
    )
    parser.add_argument(
        "--backend",
        choices=["selenium", "playwright", "requests", "auto"],
        default="auto",
        help="Automation backend to use (default: auto-detect)",
    )
    return parser.parse_args()


# ---------------------------------------------------------------------------
# Backend 1: Selenium
# ---------------------------------------------------------------------------
def download_with_selenium(customer, output_dir, output_path, headless, timeout):
    from selenium import webdriver
    from selenium.webdriver.chrome.options import Options
    from selenium.webdriver.chrome.service import Service
    from selenium.webdriver.common.by import By
    from selenium.webdriver.support.ui import WebDriverWait, Select
    from selenium.webdriver.support import expected_conditions as EC

    # Try to use webdriver-manager if available, otherwise use system chromedriver
    try:
        from webdriver_manager.chrome import ChromeDriverManager
        service = Service(ChromeDriverManager().install())
    except ImportError:
        service = Service()  # uses chromedriver from PATH

    chrome_options = Options()
    if headless:
        chrome_options.add_argument("--headless=new")
    chrome_options.add_argument("--no-sandbox")
    chrome_options.add_argument("--disable-dev-shm-usage")
    chrome_options.add_argument("--disable-gpu")

    # Configure download directory
    prefs = {
        "download.default_directory": os.path.abspath(output_dir),
        "download.prompt_for_download": False,
        "download.directory_upgrade": True,
        "safebrowsing.enabled": True,
    }
    chrome_options.add_experimental_option("prefs", prefs)

    driver = webdriver.Chrome(service=service, options=chrome_options)
    wait = WebDriverWait(driver, timeout)

    try:
        # Step 1: Navigate to the portal
        print("[1/4] Navigating to the portal...")
        driver.get(PORTAL_URL)
        wait.until(EC.presence_of_element_located((By.TAG_NAME, "body")))
        print("  Page loaded successfully.")

        # Step 2: Select customer from dropdown
        print(f"[2/4] Selecting customer '{customer}' from dropdown...")
        dropdown_selectors = [
            (By.CSS_SELECTOR, "select[name*='ustomer' i]"),
            (By.CSS_SELECTOR, "select[id*='ustomer' i]"),
            (By.CSS_SELECTOR, "select[name*='Customer']"),
            (By.CSS_SELECTOR, "select[id*='Customer']"),
            (By.ID, "CustomerDropdown"),
            (By.ID, "ddlCustomer"),
            (By.CSS_SELECTOR, "select"),
        ]

        dropdown_found = False
        for by, selector in dropdown_selectors:
            try:
                element = wait.until(EC.presence_of_element_located((by, selector)))
                select = Select(element)
                # Try selecting by value, then visible text, then partial match
                try:
                    select.select_by_value(customer)
                except Exception:
                    try:
                        select.select_by_visible_text(customer)
                    except Exception:
                        for option in select.options:
                            if customer in option.text:
                                select.select_by_visible_text(option.text.strip())
                                break
                dropdown_found = True
                print(f"  Customer '{customer}' selected (selector: {selector}).")
                break
            except Exception:
                continue

        if not dropdown_found:
            print("  ERROR: Could not find customer dropdown.")
            selects = driver.find_elements(By.TAG_NAME, "select")
            for i, s in enumerate(selects):
                name = s.get_attribute("name") or s.get_attribute("id") or "unknown"
                print(f"    [{i}] name/id={name}")
            return False

        time.sleep(2)

        # Step 3: Click 'View Report'
        print("[3/4] Clicking 'View Report'...")
        view_report_selectors = [
            (By.LINK_TEXT, "View Report"),
            (By.XPATH, "//button[contains(text(),'View Report')]"),
            (By.XPATH, "//input[@value='View Report']"),
            (By.XPATH, "//a[contains(text(),'View Report')]"),
            (By.ID, "btnViewReport"),
            (By.XPATH, "//button[contains(text(),'View')]"),
            (By.XPATH, "//input[contains(@value,'View')]"),
        ]

        button_found = False
        for by, selector in view_report_selectors:
            try:
                btn = driver.find_element(by, selector)
                if btn.is_displayed():
                    btn.click()
                    button_found = True
                    print(f"  'View Report' clicked.")
                    break
            except Exception:
                continue

        if not button_found:
            print("  ERROR: Could not find 'View Report' button.")
            return False

        # Wait for report to render
        print("  Waiting for report to render...")
        time.sleep(5)

        # Step 4: Download the report
        print("[4/4] Downloading report...")
        download_selectors = [
            (By.LINK_TEXT, "Download"),
            (By.LINK_TEXT, "Export"),
            (By.XPATH, "//a[contains(text(),'Download')]"),
            (By.XPATH, "//button[contains(text(),'Download')]"),
            (By.XPATH, "//a[contains(text(),'Export')]"),
            (By.XPATH, "//button[contains(text(),'Export')]"),
            (By.XPATH, "//a[contains(text(),'Excel')]"),
            (By.XPATH, "//button[contains(text(),'Excel')]"),
            (By.XPATH, "//a[contains(@href,'download')]"),
            (By.XPATH, "//a[contains(@href,'export')]"),
            (By.ID, "btnDownload"),
            (By.ID, "btnExport"),
        ]

        download_clicked = False
        for by, selector in download_selectors:
            try:
                dl_btn = driver.find_element(by, selector)
                if dl_btn.is_displayed():
                    dl_btn.click()
                    download_clicked = True
                    print("  Download triggered.")
                    break
            except Exception:
                continue

        if not download_clicked:
            # Fallback: View Report may have triggered the download directly
            print("  No separate download button found.")
            print("  Checking if View Report triggered a direct download...")

        # Wait for download to complete
        print("  Waiting for download to complete...")
        downloaded_file = _wait_for_download(output_dir, timeout, ".xlsx")

        if downloaded_file:
            # Rename to date-stamped filename
            if downloaded_file != output_path:
                shutil.move(downloaded_file, output_path)
            print(f"  Report downloaded successfully.")
            return True
        else:
            print("  ERROR: Download did not complete within timeout.")
            driver.save_screenshot(os.path.join(output_dir, "debug_screenshot.png"))
            print(f"  Debug screenshot saved.")
            return False

    except Exception as e:
        print(f"  ERROR: {e}")
        try:
            driver.save_screenshot(os.path.join(output_dir, "debug_screenshot.png"))
            print(f"  Debug screenshot saved.")
        except Exception:
            pass
        return False
    finally:
        driver.quit()


# ---------------------------------------------------------------------------
# Backend 2: Playwright
# ---------------------------------------------------------------------------
def download_with_playwright(customer, output_dir, output_path, headless, timeout):
    from playwright.sync_api import sync_playwright, TimeoutError as PlaywrightTimeout

    timeout_ms = timeout * 1000

    with sync_playwright() as p:
        browser = p.chromium.launch(headless=headless)
        context = browser.new_context(accept_downloads=True)
        page = context.new_page()

        # Step 1: Navigate
        print("[1/4] Navigating to the portal...")
        try:
            page.goto(PORTAL_URL, wait_until="networkidle", timeout=timeout_ms)
        except PlaywrightTimeout:
            print("  Warning: Page load timed out, proceeding anyway...")
        except Exception as e:
            error_msg = str(e)
            if "ERR_NAME_NOT_RESOLVED" in error_msg:
                print("  ERROR: Cannot resolve hostname. Ensure you are on the Infosys network.")
            else:
                print(f"  ERROR: Failed to navigate to portal: {error_msg}")
            browser.close()
            return False
        print("  Page loaded successfully.")

        # Step 2: Select customer
        print(f"[2/4] Selecting customer '{customer}' from dropdown...")
        customer_selectors = [
            "select[name*='ustomer' i]", "select[id*='ustomer' i]",
            "select[name*='Customer']", "select[id*='Customer']",
            "#CustomerDropdown", "#ddlCustomer", "select",
        ]
        dropdown_found = False
        for selector in customer_selectors:
            try:
                dropdown = page.locator(selector).first
                if dropdown.is_visible(timeout=3000):
                    try:
                        dropdown.select_option(value=customer, timeout=5000)
                    except Exception:
                        try:
                            dropdown.select_option(label=customer, timeout=5000)
                        except Exception:
                            for option in dropdown.locator("option").all():
                                if customer in (option.text_content() or ""):
                                    dropdown.select_option(label=option.text_content().strip(), timeout=5000)
                                    break
                    dropdown_found = True
                    print(f"  Customer '{customer}' selected.")
                    break
            except Exception:
                continue

        if not dropdown_found:
            print("  ERROR: Could not find customer dropdown.")
            browser.close()
            return False

        page.wait_for_timeout(2000)

        # Step 3: Click View Report
        print("[3/4] Clicking 'View Report'...")
        vr_selectors = [
            "text=View Report", "button:has-text('View Report')",
            "input[value='View Report']", "a:has-text('View Report')",
            "#btnViewReport",
        ]
        button_found = False
        for selector in vr_selectors:
            try:
                btn = page.locator(selector).first
                if btn.is_visible(timeout=3000):
                    btn.click()
                    button_found = True
                    print("  'View Report' clicked.")
                    break
            except Exception:
                continue

        if not button_found:
            print("  ERROR: Could not find 'View Report' button.")
            browser.close()
            return False

        page.wait_for_timeout(5000)

        # Step 4: Download
        print("[4/4] Downloading report...")
        dl_selectors = [
            "text=Download", "text=Export",
            "a:has-text('Download')", "button:has-text('Download')",
            "a:has-text('Export')", "button:has-text('Export')",
            "a:has-text('Excel')", "button:has-text('Excel')",
            "#btnDownload", "#btnExport",
        ]
        for selector in dl_selectors:
            try:
                dl_btn = page.locator(selector).first
                if dl_btn.is_visible(timeout=3000):
                    with page.expect_download(timeout=timeout_ms) as download_info:
                        dl_btn.click()
                    download_info.value.save_as(output_path)
                    print("  Report downloaded successfully.")
                    browser.close()
                    return True
            except Exception:
                continue

        print("  ERROR: Could not trigger report download.")
        page.screenshot(path=os.path.join(output_dir, "debug_screenshot.png"), full_page=True)
        browser.close()
        return False


# ---------------------------------------------------------------------------
# Backend 3: Requests + BeautifulSoup (no browser needed)
# ---------------------------------------------------------------------------
def download_with_requests(customer, output_dir, output_path, timeout):
    import requests
    from bs4 import BeautifulSoup

    session = requests.Session()

    # Step 1: Load the portal page
    print("[1/4] Loading portal page...")
    try:
        resp = session.get(PORTAL_URL, timeout=timeout, verify=False)
        resp.raise_for_status()
    except requests.exceptions.ConnectionError as e:
        print(f"  ERROR: Cannot connect to portal. Ensure you are on the Infosys network.")
        print(f"  Details: {e}")
        return False
    except Exception as e:
        print(f"  ERROR: {e}")
        return False
    print("  Page loaded successfully.")

    soup = BeautifulSoup(resp.text, "html.parser")

    # Step 2: Find the form and customer dropdown
    print(f"[2/4] Looking for customer dropdown with '{customer}'...")
    form = soup.find("form")
    if not form:
        print("  Warning: No form found, using page body.")
        form = soup

    # Find select element for customer
    select_el = None
    for sel in form.find_all("select"):
        name = (sel.get("name") or sel.get("id") or "").lower()
        if "customer" in name or "cust" in name:
            select_el = sel
            break
    if not select_el:
        selects = form.find_all("select")
        if selects:
            select_el = selects[0]

    if not select_el:
        print("  ERROR: No dropdown found on the page.")
        return False

    select_name = select_el.get("name") or select_el.get("id")
    print(f"  Found dropdown: {select_name}")

    # Step 3: Build form data and submit
    print("[3/4] Submitting form with customer selection...")
    form_data = {}

    # Collect all hidden inputs and default values
    for inp in form.find_all("input"):
        name = inp.get("name")
        if name:
            form_data[name] = inp.get("value", "")

    # Set the customer selection
    form_data[select_name] = customer

    # Find submit button / form action
    form_action = form.get("action", "")
    if not form_action.startswith("http"):
        from urllib.parse import urljoin
        form_action = urljoin(PORTAL_URL, form_action) if form_action else PORTAL_URL

    # Look for View Report button name/value
    for btn in form.find_all(["button", "input"]):
        btn_text = btn.get_text("").strip().lower() if btn.name == "button" else ""
        btn_value = (btn.get("value") or "").lower()
        if "view" in btn_text or "view" in btn_value or "report" in btn_text:
            btn_name = btn.get("name")
            if btn_name:
                form_data[btn_name] = btn.get("value", "")
            break

    try:
        resp = session.post(form_action, data=form_data, timeout=timeout, verify=False)
        resp.raise_for_status()
    except Exception as e:
        print(f"  ERROR: Form submission failed: {e}")
        return False
    print("  Form submitted.")

    # Step 4: Download the report
    print("[4/4] Downloading report...")

    # Check if the response itself is the XLSX file
    content_type = resp.headers.get("Content-Type", "")
    if "spreadsheet" in content_type or "excel" in content_type or "octet-stream" in content_type:
        with open(output_path, "wb") as f:
            f.write(resp.content)
        print("  Report downloaded directly from form response.")
        return True

    # Otherwise, parse the response page for a download link
    soup2 = BeautifulSoup(resp.text, "html.parser")
    download_link = None
    for a_tag in soup2.find_all("a", href=True):
        text = a_tag.get_text("").strip().lower()
        href = a_tag["href"].lower()
        if any(kw in text for kw in ["download", "export", "excel"]) or \
           any(kw in href for kw in ["download", "export", ".xlsx"]):
            download_link = a_tag["href"]
            break

    if download_link:
        if not download_link.startswith("http"):
            from urllib.parse import urljoin
            download_link = urljoin(PORTAL_URL, download_link)

        dl_resp = session.get(download_link, timeout=timeout, verify=False)
        dl_resp.raise_for_status()
        with open(output_path, "wb") as f:
            f.write(dl_resp.content)
        print("  Report downloaded via download link.")
        return True

    print("  ERROR: Could not find download link in response.")
    # Save the HTML for debugging
    debug_path = os.path.join(output_dir, "debug_response.html")
    with open(debug_path, "w", encoding="utf-8") as f:
        f.write(resp.text)
    print(f"  Response HTML saved to: {debug_path}")
    return False


# ---------------------------------------------------------------------------
# Helpers
# ---------------------------------------------------------------------------
def _wait_for_download(download_dir, timeout_sec, extension=".xlsx"):
    """Wait for a new file with the given extension to appear in download_dir."""
    existing = set(glob.glob(os.path.join(download_dir, f"*{extension}")))
    deadline = time.time() + timeout_sec
    while time.time() < deadline:
        current = set(glob.glob(os.path.join(download_dir, f"*{extension}")))
        new_files = current - existing
        if new_files:
            # Wait a bit for download to finish (no .crdownload temp files)
            time.sleep(2)
            temp_files = glob.glob(os.path.join(download_dir, "*.crdownload"))
            if not temp_files:
                return list(new_files)[0]
        time.sleep(1)
    return None


def _detect_backend():
    """Auto-detect the best available backend."""
    try:
        import selenium  # noqa: F401
        print("Auto-detected backend: Selenium")
        return "selenium"
    except ImportError:
        pass
    try:
        import playwright  # noqa: F401
        print("Auto-detected backend: Playwright")
        return "playwright"
    except ImportError:
        pass
    try:
        import requests  # noqa: F401
        import bs4  # noqa: F401
        print("Auto-detected backend: Requests + BeautifulSoup")
        return "requests"
    except ImportError:
        pass
    return None


def main():
    args = parse_args()

    print("=" * 55)
    print("  AlconNxt Portal Report Downloader")
    print("=" * 55)
    print()

    os.makedirs(args.output_dir, exist_ok=True)
    today = datetime.now().strftime("%Y-%m-%d")
    output_filename = f"Report_{args.customer}_{today}.xlsx"
    output_path = os.path.join(args.output_dir, output_filename)

    print(f"Portal URL  : {PORTAL_URL}")
    print(f"Customer    : {args.customer}")
    print(f"Output file : {output_path}")
    print(f"Headless    : {args.headless}")
    print()

    # Determine backend
    backend = args.backend
    if backend == "auto":
        backend = _detect_backend()
        if not backend:
            print("ERROR: No supported automation library found.")
            print("Install one of the following:")
            print("  Option 1 (recommended): pip install selenium webdriver-manager")
            print("  Option 2: pip install playwright && playwright install chromium")
            print("  Option 3: pip install requests beautifulsoup4")
            exit(1)
    print()

    # Run the selected backend
    success = False
    if backend == "selenium":
        success = download_with_selenium(
            args.customer, args.output_dir, output_path, args.headless, args.timeout
        )
    elif backend == "playwright":
        success = download_with_playwright(
            args.customer, args.output_dir, output_path, args.headless, args.timeout
        )
    elif backend == "requests":
        success = download_with_requests(
            args.customer, args.output_dir, output_path, args.timeout
        )

    if success:
        print()
        print(f"Report saved to: {output_path}")
        print(f"File size: {os.path.getsize(output_path):,} bytes")
        print("\nDone!")
    else:
        print("\nFailed. Check the error messages above.")
        print("You may need to adjust CSS selectors to match the portal's HTML.")
        exit(1)


if __name__ == "__main__":
    main()
