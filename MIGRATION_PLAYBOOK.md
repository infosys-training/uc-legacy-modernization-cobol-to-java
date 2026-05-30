# COBOL-to-Java Migration — Full Estate Playbook

Step-by-step guide for migrating COBOL programs from the CardDemo estate to Java 17+. Covers analysis, implementation, testing with the test harness, and PR creation.

---

## Pre-Requisites

- Repository: `uc-legacy-modernization-cobol-to-java`
- Java 17+, Maven 3.6+, Python 3.8+
- Existing artifacts: TEST_STRATEGY.md, RECONCILIATION_CHECKS.md, golden-files/, test-harness/
- Knowledge notes approved: COBOL Modernization Notes (7 notes)

---

## Phase 1: Analyze the COBOL Program

1. **Read the COBOL source** in `app/cbl/` — understand PROCEDURE DIVISION logic, WORKING-STORAGE variables, and PERFORM/CALL flow
2. **Identify all copybooks** referenced via COPY statements in `app/cpy/` — these define the data structures
3. **Map data structures** — For each copybook:
   - Extract field names, PIC clauses, and byte positions
   - Identify signed decimal fields (PIC S9(n)V99) — these use overpunch encoding
   - Identify COMP-3 packed decimal fields
   - Note FILLER bytes and REDEFINES clauses
4. **Identify I/O operations** — OPEN, READ, WRITE, REWRITE, DELETE, CLOSE on VSAM files
5. **Document business rules** — EVALUATE/IF logic, COMPUTE statements, date conversions
6. **Check the batch pipeline position** — Refer to CUTOVER_PLAN.md for migration phase and dependencies

## Phase 2: Design the Java Structure

1. **Create Maven module** under `java-src/<program-name>/`
   ```
   java-src/<program-name>/
   ├── pom.xml
   └── src/
       ├── main/java/com/carddemo/
       │   ├── batch/    (main program logic)
       │   ├── model/    (record classes — Java records preferred)
       │   └── util/     (utilities — date, parsing, etc.)
       └── test/java/com/carddemo/
           └── batch/    (JUnit 5 tests)
   ```
2. **Map COBOL data structures to Java**:
   - `PIC X(n)` → `String` (trimmed)
   - `PIC 9(n)` → `int` or `long`
   - `PIC S9(n)V99` → `BigDecimal(precision=n+2, scale=2)` — **MANDATORY: never use float/double for monetary fields**
   - COBOL record → Java `record` class with `parse(String line)` static method
   - Group items → nested records or flat fields
3. **Handle COBOL-specific encodings**:
   - **Overpunch sign decoding**: trailing char encodes sign — `{`/`A-I` = positive, `}`/`J-R` = negative
   - **Implied decimal (V)**: PIC S9(10)V99 = 12 display bytes, use `movePointLeft(2)` for scale
   - **COMP-3 packed decimal**: 2 digits per byte, last nibble is sign

## Phase 3: Implement the Java Program

1. **Record parsing** — Create a `parse(String line)` method that reads fixed-width fields by byte position:
   ```java
   public static AccountRecord parse(String line) {
       String acctId = line.substring(0, 11).trim();
       String status = line.substring(11, 12);
       BigDecimal balance = parseSignedDecimal(line.substring(12, 24));
       // ... continue for all fields
   }
   ```
2. **Main batch logic** — Mirror the COBOL PROCEDURE DIVISION:
   - Open → Read loop → Process → Write → Close
   - Use `try-with-resources` for file I/O
   - Preserve exact COBOL DISPLAY output format for verification
3. **Date handling** — Use `java.time` (LocalDate, DateTimeFormatter) not legacy Date/Calendar
4. **Error handling** — Map COBOL FILE STATUS codes to Java exceptions
5. **COBOL rounding semantics** — Use `RoundingMode.DOWN` unless COBOL source has `ROUNDED` keyword

## Phase 4: Write JUnit Tests

1. **Golden-file comparison tests** — Parse the golden JSON from `golden-files/` and compare against Java output:
   ```java
   @Test
   void testParseAllRecords() {
       List<AccountRecord> records = parseFile("app/data/ASCII/acctdata.txt");
       assertEquals(50, records.size());
       assertEquals(new BigDecimal("194.00"), records.get(0).currentBalance());
   }
   ```
2. **Overpunch sign tests** — Test all sign characters:
   - `00000001940{` → +194.00
   - `00000001940}` → -194.00 (NOT -0.00 — the sign applies to the FULL number)
   - `0000000194I` → +194.09
3. **Edge case tests** — Empty fields, max values, zero-fill, FILLER bytes
4. **Business rule tests** — Verify COMPUTE results, EVALUATE branches, validation logic
5. **End-to-end batch test** — Run full batch against `app/data/ASCII/` input, verify output record counts and sums

## Phase 5: Run the Test Harness

1. **Generate golden files** (if not already done):
   ```bash
   python3 test-harness/generate_golden_files.py
   ```
2. **Run reconciliation checks** (22 automated checks — counts, sums, cross-refs, uniqueness):
   ```bash
   python3 test-harness/src/main/python/reconciliation.py golden-files/
   ```
3. **Compare Java output against golden reference**:
   ```bash
   python3 test-harness/src/main/python/comparator.py golden-files/<file>.json actual/<file>.json
   ```
4. **Run full demo** to verify everything end-to-end:
   ```bash
   ./run_demo.sh
   ```

## Phase 6: Create PR and Verify

1. **Branch naming**: `devin/<timestamp>-<program-name>-java-rewrite`
2. **Commit message**: Include program name, record counts, test counts
3. **PR description**: Use repo template — include field mapping table, test results, any bugs found
4. **CI verification**: All JUnit tests must pass
5. **Update RECONCILIATION_CHECKS.md** if the program has new reconciliation rules

---

## Common Bugs & Prevention

| Bug | Root Cause | Prevention |
|-----|-----------|------------|
| Overpunch `}` decoded as -0 | Sign applied only to last digit | Apply sign to FULL number value |
| PIC S9(10)V99 read as 19400 | Forgot implied V decimal | Always `movePointLeft(scale)` |
| Field position off-by-one | Wrong byte offset | Verify with `cut -c{start}-{end}` against actual data |
| Float rounding errors | Used double for money | MANDATORY: `BigDecimal` for ALL monetary fields |
| Package-private methods | Cross-package test access | Make parser methods `public static` |

---

## Migration Priority Order

Refer to CUTOVER_PLAN.md for full phasing. Summary:

| Phase | Programs | Priority |
|-------|----------|----------|
| P0 Foundation | CBACT01C (done), CBACT02C, CBACT03C, CBCUS01C | Batch readers — low risk |
| P1 Core Batch | CBTRN02C (POSTTRAN), CBACT04C (INTCALC) | Transaction posting — high risk |
| P2 Reports | CBSTM03A/B, CBTRN03C | Statement generation |
| P3 Data Migration | CBEXPORT, CBIMPORT | Export/import |
| P4 Online CICS | COMEN01C, COSGN00C, COACTUPC (largest) | Screen programs — highest complexity |

---

## Test Harness Tools Reference

| Tool | Command | Purpose |
|------|---------|---------|
| Golden file generator | `python3 test-harness/generate_golden_files.py` | Parse 9 COBOL data files → JSON |
| Reconciliation runner | `python3 test-harness/src/main/python/reconciliation.py golden-files/` | 22 automated checks |
| Output comparator | `python3 test-harness/src/main/python/comparator.py expected.json actual.json` | Field-by-field diff |
| COBOL parser | `python3 test-harness/src/main/python/cobol_parser.py <datafile> [output.json]` | Parse single data file |
| Full demo | `./run_demo.sh` | Run all 4 phases end-to-end |

---

## Key Reference Files

- `TEST_STRATEGY.md` — 4-layer testing approach
- `RECONCILIATION_CHECKS.md` — Per-JCL-job reconciliation rules
- `golden-files/` — 9 JSON reference files (626 records)
- `test-harness/` — Python parser, comparator, reconciliation runner
- `run_demo.sh` — Executable demo (4 phases)
- `MODERNIZATION_BLUEPRINT.md` — Strategy per functional area
- `DOMAIN_DECOMPOSITION.md` — Bounded contexts and microservice mapping
- `CUTOVER_PLAN.md` — 7-phase migration sequence
- `RISK_REGISTER.md` — Top 10 migration risks

---

## Completed Migrations

| Program | Java Class | Tests | PR | Status |
|---------|-----------|-------|----|--------|
| CBACT01C.cbl | AccountFileSplitter | 23 JUnit | [PR #50](https://github.com/infosys-training/uc-legacy-modernization-cobol-to-java/pull/50) | Done |
