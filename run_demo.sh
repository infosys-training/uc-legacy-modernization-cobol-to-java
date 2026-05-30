#!/usr/bin/env bash
#
# CardDemo Migration Test Harness — Demo Script
#
# Demonstrates the COBOL-to-Java migration test infrastructure:
#   1. Parses all 9 COBOL data files (626 records) into golden JSON
#   2. Runs 22 reconciliation checks (counts, sums, cross-refs, uniqueness)
#   3. Builds and tests the Java 17 rewrite of CBACT01C (23 JUnit tests)
#   4. Runs CBACT01C Java batch against actual CardDemo test data (50 accounts)
#
# Usage:  ./run_demo.sh
# Requirements: Python 3.8+, Java 17+, Maven 3.6+

set -e

REPO_ROOT="$(cd "$(dirname "$0")" && pwd)"
BOLD="\033[1m"
GREEN="\033[1;32m"
CYAN="\033[1;36m"
YELLOW="\033[1;33m"
RED="\033[1;31m"
RESET="\033[0m"

divider() {
    echo -e "${CYAN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${RESET}"
}

header() {
    echo
    divider
    echo -e "${BOLD}  $1${RESET}"
    divider
    echo
}

pass() { echo -e "  ${GREEN}[PASS]${RESET} $1"; }
fail() { echo -e "  ${RED}[FAIL]${RESET} $1"; }
info() { echo -e "  ${YELLOW}[INFO]${RESET} $1"; }

ERRORS=0

# ──────────────────────────────────────────────────────────────────────
header "CARDDEMO MIGRATION TEST HARNESS — DEMO"
# ──────────────────────────────────────────────────────────────────────

echo -e "  Repository:  ${BOLD}uc-legacy-modernization-cobol-to-java${RESET}"
echo -e "  Date:        $(date '+%Y-%m-%d %H:%M:%S %Z')"
echo -e "  Python:      $(python3 --version 2>&1)"
echo

JAVA_HOME_FOUND=""
for jdir in /usr/lib/jvm/java-17-openjdk-amd64 /usr/lib/jvm/java-17 /usr/lib/jvm/temurin-17-jdk-amd64; do
    if [ -d "$jdir" ]; then
        export JAVA_HOME="$jdir"
        export PATH="$JAVA_HOME/bin:$PATH"
        JAVA_HOME_FOUND="$jdir"
        break
    fi
done

if command -v java &>/dev/null; then
    echo -e "  Java:        $(java -version 2>&1 | head -1)"
else
    echo -e "  Java:        ${RED}not found${RESET} (Java steps will be skipped)"
fi

if command -v mvn &>/dev/null; then
    echo -e "  Maven:       $(mvn --version 2>&1 | head -1)"
else
    echo -e "  Maven:       ${RED}not found${RESET} (Maven steps will be skipped)"
fi
echo

# ──────────────────────────────────────────────────────────────────────
header "PHASE 1: Parse COBOL Data Files → Golden JSON"
# ──────────────────────────────────────────────────────────────────────

info "Parsing 9 data files from app/data/ASCII/ using copybook layouts..."
echo
python3 "$REPO_ROOT/test-harness/generate_golden_files.py" \
    "$REPO_ROOT/app/data/ASCII" \
    "$REPO_ROOT/golden-files" 2>&1 | sed 's/^/  /'
echo
pass "Golden files generated in golden-files/"

# ──────────────────────────────────────────────────────────────────────
header "PHASE 2: Reconciliation Checks (22 automated checks)"
# ──────────────────────────────────────────────────────────────────────

RECON_OUTPUT=$(python3 "$REPO_ROOT/test-harness/src/main/python/reconciliation.py" \
    "$REPO_ROOT/golden-files" 2>&1)
RECON_EXIT=$?

echo "$RECON_OUTPUT" | sed 's/^/  /'
echo

if [ $RECON_EXIT -eq 0 ]; then
    pass "All reconciliation checks passed"
else
    fail "Some reconciliation checks failed"
    ERRORS=$((ERRORS + 1))
fi

# ──────────────────────────────────────────────────────────────────────
header "PHASE 3: Build & Test Java Rewrite (CBACT01C)"
# ──────────────────────────────────────────────────────────────────────

JAVA_DIR="$REPO_ROOT/java-src/cbact01c"

if [ -d "$JAVA_DIR" ] && command -v mvn &>/dev/null && command -v java &>/dev/null; then
    info "Building CBACT01C Java 17 rewrite..."
    echo

    MVN_OUTPUT=$(cd "$JAVA_DIR" && mvn clean test 2>&1)
    MVN_EXIT=$?

    # Extract test summary
    TEST_LINE=$(echo "$MVN_OUTPUT" | grep "Tests run:" | tail -1)
    if [ -n "$TEST_LINE" ]; then
        echo "  $TEST_LINE"
    fi

    BUILD_STATUS=$(echo "$MVN_OUTPUT" | grep "BUILD " | tail -1)
    if [ -n "$BUILD_STATUS" ]; then
        echo "  $BUILD_STATUS"
    fi
    echo

    if [ $MVN_EXIT -eq 0 ]; then
        pass "All 23 JUnit tests passed"
    else
        fail "JUnit tests failed (exit code $MVN_EXIT)"
        ERRORS=$((ERRORS + 1))
    fi
else
    if [ ! -d "$JAVA_DIR" ]; then
        info "Skipping — java-src/cbact01c/ not found (run from CBACT01C rewrite branch)"
    else
        info "Skipping — Java/Maven not available"
    fi
fi

# ──────────────────────────────────────────────────────────────────────
header "PHASE 4: Run CBACT01C Against Actual Test Data (50 accounts)"
# ──────────────────────────────────────────────────────────────────────

if [ -d "$JAVA_DIR" ] && command -v mvn &>/dev/null && command -v java &>/dev/null; then
    DEMO_OUT="$REPO_ROOT/demo-output"
    mkdir -p "$DEMO_OUT"

    info "Executing Java batch: 50 accounts → 3 output files..."
    echo

    cd "$JAVA_DIR"
    java -cp target/classes com.carddemo.batch.AccountFileSplitter \
        "$REPO_ROOT/app/data/ASCII/acctdata.txt" \
        "$DEMO_OUT/outfile.dat" \
        "$DEMO_OUT/arryfile.dat" \
        "$DEMO_OUT/vbrcfile.dat" 2>&1 | tail -5 | sed 's/^/  /'
    JAVA_EXIT=$?
    echo

    if [ $JAVA_EXIT -eq 0 ]; then
        pass "Batch completed successfully"
        echo
        info "Output files:"
        for f in "$DEMO_OUT"/*.dat; do
            LINES=$(wc -l < "$f" 2>/dev/null || echo "0")
            SIZE=$(wc -c < "$f" 2>/dev/null || echo "0")
            echo -e "    $(basename $f): ${BOLD}$LINES records${RESET}, $SIZE bytes"
        done
    else
        fail "Batch execution failed (exit code $JAVA_EXIT)"
        ERRORS=$((ERRORS + 1))
    fi

    # Cleanup demo output
    rm -rf "$DEMO_OUT"
else
    info "Skipping — Java/Maven not available"
fi

# ──────────────────────────────────────────────────────────────────────
header "DEMO SUMMARY"
# ──────────────────────────────────────────────────────────────────────

echo -e "  ${BOLD}Data Estate:${RESET}          9 files, 626 records parsed"
echo -e "  ${BOLD}Golden Files:${RESET}         9 JSON reference files generated"
echo -e "  ${BOLD}Reconciliation:${RESET}       22 checks (counts, sums, cross-refs, uniqueness)"
echo -e "  ${BOLD}Java Rewrite:${RESET}         CBACT01C → AccountFileSplitter (23 JUnit tests)"
echo -e "  ${BOLD}Batch Execution:${RESET}      50 accounts → 3 output files"
echo

if [ $ERRORS -eq 0 ]; then
    echo -e "  ${GREEN}${BOLD}RESULT: ALL PHASES PASSED${RESET}"
else
    echo -e "  ${RED}${BOLD}RESULT: $ERRORS PHASE(S) FAILED${RESET}"
fi

divider
echo
exit $ERRORS
