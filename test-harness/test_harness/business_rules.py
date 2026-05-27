"""
Business-specific validation checks for CardDemo migration.

Validates program-specific business rules that the Java rewrites
must preserve exactly:
  - CBACT01C: cycDebit substitution, date reformatting, array values, VBR pairs
  - CBTRN02C: rejection criteria, balance updates, category balance accumulation
  - CBACT04C: interest truncation (not rounding), lookup chain
  - CBSTM03A: statement completeness, dual output format
"""

from dataclasses import dataclass
from decimal import Decimal, ROUND_DOWN
from pathlib import Path
from typing import Optional

from test_harness.copybook_layouts import (
    ACCOUNT_RECORD,
    CARD_XREF_RECORD,
    DAILY_TRANSACTION_RECORD,
    DISCLOSURE_GROUP_RECORD,
    TRAN_CAT_BAL_RECORD,
)
from test_harness.parser import parse_file
from test_harness.reconciliation import CheckResult


# --- CBACT01C Business Rules ---

CYCDEBIT_SUBSTITUTION_VALUE = Decimal("2525.00")
ARRAY_FIXED_VALUES = [
    Decimal("1005.00"),
    Decimal("1525.00"),
    Decimal("-1025.00"),
    Decimal("-2500.00"),
    Decimal("0.00"),
]


def check_cycdebit_substitution(
    input_records: list[dict],
    output_records: list[dict],
    debit_input_field: str = "ACCT-CURR-CYC-DEBIT",
    debit_output_field: str = "OUT-ACCT-CURR-CYC-DEBIT",
) -> CheckResult:
    """CBACT01C rule: if cycDebit=0 in input, output must be 2525.00."""
    violations = []
    for i, (inp, out) in enumerate(zip(input_records, output_records)):
        input_val = inp.get(debit_input_field, Decimal("0"))
        output_val = out.get(debit_output_field, Decimal("0"))

        if isinstance(input_val, str):
            input_val = Decimal(input_val) if input_val.strip() else Decimal("0")
        if isinstance(output_val, str):
            output_val = Decimal(output_val) if output_val.strip() else Decimal("0")

        if input_val == Decimal("0"):
            if output_val != CYCDEBIT_SUBSTITUTION_VALUE:
                violations.append(
                    f"Record {i}: cycDebit=0, expected output={CYCDEBIT_SUBSTITUTION_VALUE}, "
                    f"got {output_val}"
                )
        else:
            if output_val != input_val:
                violations.append(
                    f"Record {i}: cycDebit={input_val}, expected output={input_val}, "
                    f"got {output_val}"
                )

    return CheckResult(
        check_name="CBACT01C cycDebit substitution",
        passed=len(violations) == 0,
        message=f"{len(input_records)} records checked, {len(violations)} violations",
        details="; ".join(violations[:5]) if violations else None,
    )


def check_date_reformatting(
    input_records: list[dict],
    output_records: list[dict],
    date_fields: Optional[list[tuple[str, str]]] = None,
) -> CheckResult:
    """CBACT01C rule: dates reformatted from YYYY-MM-DD to YYYYMMDD via COBDATFT."""
    if date_fields is None:
        date_fields = [
            ("ACCT-OPEN-DATE", "OUT-ACCT-OPEN-DATE"),
            ("ACCT-EXPIRAION-DATE", "OUT-ACCT-EXPIRAION-DATE"),
            ("ACCT-REISSUE-DATE", "OUT-ACCT-REISSUE-DATE"),
        ]

    violations = []
    for i, (inp, out) in enumerate(zip(input_records, output_records)):
        for in_field, out_field in date_fields:
            in_date = str(inp.get(in_field, "")).strip()
            out_date = str(out.get(out_field, "")).strip()
            expected = in_date.replace("-", "")
            if out_date != expected:
                violations.append(
                    f"Record {i}: {in_field}='{in_date}', "
                    f"expected '{expected}', got '{out_date}'"
                )

    return CheckResult(
        check_name="CBACT01C date reformatting (COBDATFT)",
        passed=len(violations) == 0,
        message=f"{len(input_records)} records checked, {len(violations)} violations",
        details="; ".join(violations[:5]) if violations else None,
    )


def check_array_fixed_values(output_records: list[dict]) -> CheckResult:
    """CBACT01C rule: array output always contains 5 fixed values."""
    violations = []
    for i, rec in enumerate(output_records):
        for slot_idx, expected_val in enumerate(ARRAY_FIXED_VALUES):
            field_name = f"ARRAY-SLOT-{slot_idx + 1}"
            actual = rec.get(field_name)
            if actual is not None:
                if isinstance(actual, str):
                    actual = Decimal(actual)
                if actual != expected_val:
                    violations.append(
                        f"Record {i}: {field_name} expected {expected_val}, got {actual}"
                    )

    if not output_records:
        return CheckResult(
            check_name="CBACT01C array fixed values",
            passed=True,
            message="No output records to check",
        )

    return CheckResult(
        check_name="CBACT01C array fixed values",
        passed=len(violations) == 0,
        message=f"{len(output_records)} records checked, {len(violations)} violations",
        details="; ".join(violations[:5]) if violations else None,
    )


def check_vbr_record_pairs(vbr_records: list[dict]) -> CheckResult:
    """CBACT01C rule: VBR records come in pairs (Type1 then Type2) for each account."""
    if len(vbr_records) % 2 != 0:
        return CheckResult(
            check_name="CBACT01C VBR record pairing",
            passed=False,
            message=f"Odd number of VBR records: {len(vbr_records)} (expected even)",
        )

    violations = []
    for i in range(0, len(vbr_records), 2):
        type1 = vbr_records[i]
        type2 = vbr_records[i + 1]
        t1_acct = str(type1.get("VBR-ACCT-ID", "")).strip()
        t2_acct = str(type2.get("VBR-ACCT-ID", "")).strip()
        if t1_acct != t2_acct:
            violations.append(
                f"Pair {i // 2}: Type1 acct={t1_acct}, Type2 acct={t2_acct}"
            )

    return CheckResult(
        check_name="CBACT01C VBR record pairing",
        passed=len(violations) == 0,
        message=f"{len(vbr_records)} VBR records ({len(vbr_records) // 2} pairs), "
                f"{len(violations)} mismatched",
        details="; ".join(violations[:5]) if violations else None,
    )


# --- CBTRN02C Business Rules ---

def check_posttran_record_conservation(
    dalytran_count: int,
    posted_count: int,
    rejected_count: int,
) -> CheckResult:
    """POSTTRAN rule: every input transaction must be posted or rejected."""
    expected = dalytran_count
    actual = posted_count + rejected_count
    return CheckResult(
        check_name="POSTTRAN record conservation",
        passed=expected == actual,
        message=f"DALYTRAN={dalytran_count}, posted={posted_count}, rejected={rejected_count}, "
                f"sum={actual}",
        details=f"Missing: {expected - actual}" if expected != actual else None,
    )


def check_posttran_balance_integrity(
    acct_bal_before: dict[str, Decimal],
    acct_bal_after: dict[str, Decimal],
    posted_amounts: dict[str, Decimal],
) -> CheckResult:
    """POSTTRAN rule: per-account balance delta must equal sum of posted amounts."""
    violations = []
    for acct_id, amount_sum in posted_amounts.items():
        before = acct_bal_before.get(acct_id, Decimal("0"))
        after = acct_bal_after.get(acct_id, Decimal("0"))
        delta = after - before
        if delta != amount_sum:
            violations.append(
                f"Account {acct_id}: delta={delta}, posted_sum={amount_sum}, "
                f"diff={delta - amount_sum}"
            )

    return CheckResult(
        check_name="POSTTRAN balance integrity",
        passed=len(violations) == 0,
        message=f"{len(posted_amounts)} accounts with transactions, "
                f"{len(violations)} balance mismatches",
        details="; ".join(violations[:5]) if violations else None,
    )


def check_rejection_criteria(
    rejected_records: list[dict],
    xref_card_nums: set[str],
    acct_ids: set[str],
    active_acct_ids: set[str],
    xref_card_to_acct: Optional[dict[str, str]] = None,
    card_field: str = "DALYTRAN-CARD-NUM",
) -> CheckResult:
    """POSTTRAN rule: every rejected transaction must have a valid rejection reason.

    A rejection is justified if at least one of the following holds:
      (a) DALYTRAN-CARD-NUM not found in CARDXREF
      (b) Resolved XREF-ACCT-ID not found in ACCTDATA
      (c) Resolved account's ACCT-ACTIVE-STATUS ≠ 'Y'

    Args:
        rejected_records: Records written to DALYREJS.
        xref_card_nums: Set of all card numbers present in CARDXREF.
        acct_ids: Set of all account IDs present in ACCTDATA.
        active_acct_ids: Subset of acct_ids whose ACCT-ACTIVE-STATUS = 'Y'.
        xref_card_to_acct: Mapping from XREF-CARD-NUM → XREF-ACCT-ID. Required
            to validate criteria (b) and (c). If omitted, only criterion (a)
            is checked and any rejection of a card that exists in CARDXREF
            is reported as unjustified.
        card_field: Field name in the rejected record that holds the card num.
    """
    unjustified = []
    for i, rec in enumerate(rejected_records):
        card_num = str(rec.get(card_field, "")).strip()

        # Criterion (a): card not in xref → justified
        if card_num not in xref_card_nums:
            continue

        # Need card→acct mapping to check (b) and (c)
        if xref_card_to_acct is None:
            unjustified.append(
                f"Record {i}: card={card_num} exists in xref "
                f"(cannot verify acct/status without xref_card_to_acct)"
            )
            continue

        resolved_acct = str(xref_card_to_acct.get(card_num, "")).strip()

        # Criterion (b): resolved acct not in acctdata → justified
        if resolved_acct not in acct_ids:
            continue

        # Criterion (c): account not active → justified
        if resolved_acct not in active_acct_ids:
            continue

        # All three criteria failed: card exists, acct exists, acct is active
        unjustified.append(
            f"Record {i}: card={card_num}, acct={resolved_acct} "
            f"(card in xref, acct exists and is active)"
        )

    return CheckResult(
        check_name="POSTTRAN rejection criteria",
        passed=len(unjustified) == 0,
        message=f"{len(rejected_records)} rejected transactions, "
                f"{len(unjustified)} unjustified",
        details="; ".join(unjustified[:5]) if unjustified else None,
    )


def check_no_duplicate_tran_ids(tran_ids: list[str]) -> CheckResult:
    """Verify all transaction IDs are unique."""
    seen = set()
    duplicates = []
    for tid in tran_ids:
        t = tid.strip()
        if t in seen:
            duplicates.append(t)
        seen.add(t)

    return CheckResult(
        check_name="No duplicate TRAN-IDs",
        passed=len(duplicates) == 0,
        message=f"{len(tran_ids)} transactions, {len(duplicates)} duplicates",
        details="; ".join(duplicates[:10]) if duplicates else None,
    )


# --- CBACT04C Business Rules ---

def calculate_interest_truncated(
    balance: Decimal,
    annual_rate: Decimal,
) -> Decimal:
    """Calculate monthly interest using COBOL truncation semantics.

    COBOL PIC S9(09)V99 truncates — it does NOT round.
    interest = TRUNCATE(balance * rate / 1200, 2 decimal places)
    """
    if annual_rate == 0 or balance == 0:
        return Decimal("0.00")
    monthly = balance * annual_rate / Decimal("1200")
    return monthly.quantize(Decimal("0.01"), rounding=ROUND_DOWN)


def check_interest_truncation(
    tcatbal_records: list[dict],
    discgrp_records: list[dict],
    acct_records: list[dict],
    interest_transactions: list[dict],
) -> CheckResult:
    """INTCALC rule: interest must use TRUNCATE, not ROUND.

    For each TCATBALF record with non-zero balance:
      1. Look up ACCTDATA[acct-id].ACCT-GROUP-ID → group
      2. Look up DISCGRP[group, type-cd, cat-cd].DIS-INT-RATE → rate
      3. expected = TRUNCATE(balance × rate / 1200, 2 decimals)
      4. Verify actual interest transaction matches exactly
    """
    # Build lookup maps
    acct_group = {}
    for r in acct_records:
        acct_id = str(r["ACCT-ID"]).strip()
        acct_group[acct_id] = str(r.get("ACCT-GROUP-ID", "")).strip()

    discgrp_rates = {}
    for r in discgrp_records:
        key = (
            str(r["DIS-ACCT-GROUP-ID"]).strip(),
            str(r["DIS-TRAN-TYPE-CD"]).strip(),
            str(r["DIS-TRAN-CAT-CD"]).strip(),
        )
        discgrp_rates[key] = r["DIS-INT-RATE"]

    # Index interest transactions by (acct-id, type-cd, cat-cd)
    interest_by_key = {}
    for txn in interest_transactions:
        # Interest transactions reference the account via the xref
        key = (
            str(txn.get("TRAN-ACCT-ID", "")).strip(),
            str(txn.get("TRAN-TYPE-CD", "")).strip(),
            str(txn.get("TRAN-CAT-CD", "")).strip(),
        )
        interest_by_key[key] = txn.get("TRAN-AMT", Decimal("0"))

    violations = []
    checked = 0

    for tc in tcatbal_records:
        balance = tc.get("TRAN-CAT-BAL", Decimal("0"))
        if isinstance(balance, str):
            balance = Decimal(balance)
        if balance == 0:
            continue

        acct_id = str(tc["TRANCAT-ACCT-ID"]).strip()
        type_cd = str(tc["TRANCAT-TYPE-CD"]).strip()
        cat_cd = str(tc["TRANCAT-CD"]).strip()

        group = acct_group.get(acct_id, "")
        rate_key = (group, type_cd, cat_cd)
        rate = discgrp_rates.get(rate_key)

        if rate is None:
            continue  # No rate found — record should be skipped per COBOL logic

        if isinstance(rate, str):
            rate = Decimal(rate)

        expected_interest = calculate_interest_truncated(balance, rate)
        actual_key = (acct_id, type_cd, cat_cd)
        actual_interest = interest_by_key.get(actual_key, Decimal("0"))

        if isinstance(actual_interest, str):
            actual_interest = Decimal(actual_interest)

        checked += 1
        if expected_interest != actual_interest:
            violations.append(
                f"acct={acct_id} type={type_cd} cat={cat_cd}: "
                f"bal={balance} rate={rate} "
                f"expected={expected_interest} actual={actual_interest}"
            )

    return CheckResult(
        check_name="INTCALC interest truncation (RoundingMode.DOWN)",
        passed=len(violations) == 0,
        message=f"{checked} interest calculations checked, {len(violations)} mismatches",
        details="; ".join(violations[:5]) if violations else None,
    )


# --- Edge Case Validators ---

def check_max_field_values(records: list[dict], layout: list) -> CheckResult:
    """Verify no field exceeds its maximum PIC-defined capacity."""
    violations = []
    for i, rec in enumerate(records):
        for field_name, pic, length in layout:
            if pic == "FILLER":
                continue
            val = rec.get(field_name)
            if val is None:
                continue

            if pic.startswith("S9") and "V" in pic:
                if isinstance(val, Decimal):
                    # Extract max from PIC: S9(10)V99 → max 9999999999.99
                    import re
                    m = re.match(r"S9\((\d+)\)V(\d+)", pic)
                    if m:
                        int_digits = int(m.group(1))
                        dec_digits = len(m.group(2)) if m.group(2).isdigit() else int(m.group(2))
                        max_val = Decimal("9" * int_digits + "." + "9" * dec_digits)
                        if abs(val) > max_val:
                            violations.append(
                                f"Record {i}: {field_name}={val} exceeds PIC {pic} max {max_val}"
                            )

    return CheckResult(
        check_name="Field value range check",
        passed=len(violations) == 0,
        message=f"{len(records)} records checked, {len(violations)} overflow violations",
        details="; ".join(violations[:5]) if violations else None,
    )


def check_active_status_valid(
    records: list[dict],
    status_field: str,
) -> CheckResult:
    """Verify status fields contain only valid values (Y/N)."""
    invalid = []
    for i, rec in enumerate(records):
        val = str(rec.get(status_field, "")).strip()
        if val and val not in ("Y", "N"):
            invalid.append(f"Record {i}: {status_field}='{val}'")

    return CheckResult(
        check_name=f"Valid status values: {status_field}",
        passed=len(invalid) == 0,
        message=f"{len(records)} records, {len(invalid)} invalid status values",
        details="; ".join(invalid[:5]) if invalid else None,
    )


def check_date_validity(
    records: list[dict],
    date_fields: list[str],
) -> CheckResult:
    """Verify date fields contain valid dates (YYYY-MM-DD or YYYYMMDD format)."""
    import re
    date_pattern = re.compile(
        r"^(\d{4})-?(\d{2})-?(\d{2})"
    )
    invalid = []

    for i, rec in enumerate(records):
        for field_name in date_fields:
            val = str(rec.get(field_name, "")).strip()
            if not val:
                continue
            m = date_pattern.match(val)
            if not m:
                invalid.append(f"Record {i}: {field_name}='{val}' — not a valid date format")
                continue
            year, month, day = int(m.group(1)), int(m.group(2)), int(m.group(3))
            if month < 1 or month > 12:
                invalid.append(f"Record {i}: {field_name}='{val}' — invalid month {month}")
            elif day < 1 or day > 31:
                invalid.append(f"Record {i}: {field_name}='{val}' — invalid day {day}")
            elif month in (4, 6, 9, 11) and day > 30:
                invalid.append(f"Record {i}: {field_name}='{val}' — {month} has max 30 days")
            elif month == 2:
                is_leap = (year % 4 == 0 and year % 100 != 0) or (year % 400 == 0)
                max_day = 29 if is_leap else 28
                if day > max_day:
                    invalid.append(
                        f"Record {i}: {field_name}='{val}' — Feb {year} max {max_day} days"
                    )

    return CheckResult(
        check_name="Date field validity",
        passed=len(invalid) == 0,
        message=f"{len(records)} records, {len(date_fields)} date fields, "
                f"{len(invalid)} invalid dates",
        details="; ".join(invalid[:5]) if invalid else None,
    )


def check_credit_limit_consistency(records: list[dict]) -> CheckResult:
    """Verify ACCT-CASH-CREDIT-LIMIT <= ACCT-CREDIT-LIMIT for all accounts."""
    violations = []
    for i, rec in enumerate(records):
        credit = rec.get("ACCT-CREDIT-LIMIT", Decimal("0"))
        cash = rec.get("ACCT-CASH-CREDIT-LIMIT", Decimal("0"))
        if isinstance(credit, str):
            credit = Decimal(credit)
        if isinstance(cash, str):
            cash = Decimal(cash)
        if cash > credit:
            violations.append(
                f"Account {rec.get('ACCT-ID', '?')}: "
                f"cash limit {cash} > credit limit {credit}"
            )

    return CheckResult(
        check_name="Credit limit consistency",
        passed=len(violations) == 0,
        message=f"{len(records)} accounts, {len(violations)} with cash > credit limit",
        details="; ".join(violations[:5]) if violations else None,
    )


def check_balance_within_limit(records: list[dict]) -> CheckResult:
    """Check whether any account balance exceeds its credit limit (warning, not error)."""
    over_limit = []
    for i, rec in enumerate(records):
        bal = rec.get("ACCT-CURR-BAL", Decimal("0"))
        limit = rec.get("ACCT-CREDIT-LIMIT", Decimal("0"))
        if isinstance(bal, str):
            bal = Decimal(bal)
        if isinstance(limit, str):
            limit = Decimal(limit)
        if bal > limit:
            over_limit.append(
                f"Account {rec.get('ACCT-ID', '?')}: "
                f"balance {bal} > limit {limit}"
            )

    return CheckResult(
        check_name="Balance within credit limit",
        passed=True,  # This is a warning check, not a failure
        message=f"{len(records)} accounts, {len(over_limit)} over credit limit",
        details="; ".join(over_limit[:5]) if over_limit else None,
    )
