---
name: cobol-to-java-migration
description: Migrate a COBOL batch program from the CardDemo estate to Java 17+ with full test coverage and golden-file validation
model_invocable: true
---

# COBOL-to-Java Batch Migration

Migrate a single COBOL batch program from the CardDemo mainframe application to a Java 17+ Maven module. Produces production-quality code with JUnit 5 tests validated against golden reference files.

## Inputs

- `program_name`: The COBOL program to migrate (e.g., `CBACT01C`, `CBACT02C`). Source is at `app/cbl/<program_name>.cbl`.

## Steps

### Step 1: Analyze the COBOL Program

1. Read the COBOL source at `app/cbl/<program_name>.cbl`
2. Identify all `COPY` statements and read each copybook from `app/cpy/`
3. For each copybook, extract:
   - Field names and PIC clauses
   - Byte positions (calculate from PIC widths)
   - Signed decimal fields: `PIC S9(n)V9(m)` → overpunch-encoded, implied decimal
   - COMP-3 packed decimal fields
   - FILLER bytes and REDEFINES clauses
4. Map all I/O operations: OPEN, READ, WRITE, REWRITE, DELETE, CLOSE — note the DD names / file references
5. Document business rules from EVALUATE/IF logic, COMPUTE statements, and PERFORM paragraphs
6. Check batch pipeline position — does this program depend on output from another? Does another program consume its output?

### Step 2: Create Maven Module

Create the project structure under `java-src/<program-name>/`:

```
java-src/<program-name>/
├── pom.xml            (Java 17, JUnit 5, maven-surefire-plugin)
└── src/
    ├── main/java/com/carddemo/
    │   ├── batch/     (main program class)
    │   ├── model/     (Java record classes for COBOL records)
    │   └── util/      (parsing utilities, date helpers)
    └── test/java/com/carddemo/
        └── batch/     (JUnit 5 tests)
```

The `pom.xml` must target Java 17+ and include JUnit 5 dependencies.

### Step 3: Map COBOL Data Types to Java

Follow these mappings exactly:

| COBOL PIC Clause | Java Type | Notes |
|------------------|-----------|-------|
| `PIC X(n)` | `String` | Right-trim spaces |
| `PIC 9(n)` | `int` or `long` | Use `long` if n > 9 |
| `PIC S9(n)V9(m)` | `BigDecimal` | **MANDATORY** — never use float/double for money |
| `COMP-3` | `BigDecimal` | Packed decimal: 2 digits/byte, last nibble = sign |
| Group items | Java `record` | One record class per copybook |

**Overpunch sign decoding** (trailing character on signed DISPLAY fields):
- Positive: `{`=0, `A`=1, `B`=2, `C`=3, `D`=4, `E`=5, `F`=6, `G`=7, `H`=8, `I`=9
- Negative: `}`=0, `J`=1, `K`=2, `L`=3, `M`=4, `N`=5, `O`=6, `P`=7, `Q`=8, `R`=9
- The sign applies to the **full number**, not just the last digit
- Example: `00000001940}` → the last char `}` means digit=0, sign=negative → raw digits = `000000019400` → with V99 scale → **-194.00**

**Implied decimal (V):**
- `PIC S9(10)V99` = 12 display bytes total (10 integer + 2 decimal)
- After assembling all digits, call `movePointLeft(scale)` where scale = digits after V

### Step 4: Implement Record Parsing

Create a `parse(String line)` static method on each record class that reads fixed-width fields by byte position:

```java
public record AccountRecord(String acctId, String status, BigDecimal currentBalance, ...) {
    public static AccountRecord parse(String line) {
        String acctId = line.substring(0, 11).trim();
        String status = line.substring(11, 12);
        BigDecimal balance = OverpunchDecoder.decode(line.substring(12, 24), 2);
        // ... continue for all fields per copybook layout
    }
}
```

**Verify byte positions**: Always run `wc -c` on sample data and `cut -c{start}-{end}` to confirm field positions match the copybook before coding.

### Step 5: Implement Batch Logic

Mirror the COBOL PROCEDURE DIVISION structure:

1. Open files → Read loop → Process each record → Write output → Close files
2. Use `try-with-resources` for all file I/O
3. Use `RoundingMode.DOWN` for arithmetic unless COBOL source has the `ROUNDED` keyword
4. Preserve exact COBOL DISPLAY output format for verification against golden files
5. Replace assembler subroutine calls (e.g., COBDATFT) with Java utility classes
6. Use `java.time` (LocalDate, DateTimeFormatter) for all date handling

### Step 6: Write JUnit 5 Tests

1. **Golden-file comparison tests** — parse records from `app/data/ASCII/` and verify field values:
   ```java
   @Test
   void testParseAllRecords() {
       List<AccountRecord> records = parseFile("app/data/ASCII/acctdata.txt");
       assertEquals(50, records.size());
       assertEquals(new BigDecimal("194.00"), records.get(0).currentBalance());
   }
   ```
2. **Overpunch sign tests** — test ALL sign characters, especially:
   - `00000001940{` → +194.00
   - `00000001940}` → -194.00 (NOT -0.00)
   - `0000000194I` → +194.09
3. **Edge cases** — empty fields, max values, zero-fill, FILLER bytes
4. **Business rule tests** — verify COMPUTE results, EVALUATE branches
5. **End-to-end batch test** — run full batch, verify output record counts and sums

### Step 7: Build and Verify

```bash
cd java-src/<program-name>
mvn clean test
```

All tests must pass. Fix any failures before proceeding.

### Step 8: Run Reconciliation (if test harness exists)

If the `test-harness/` and `golden-files/` directories exist:

```bash
python3 test-harness/src/main/python/reconciliation.py golden-files/
python3 test-harness/src/main/python/comparator.py golden-files/<file>.json actual/<file>.json
```

Verify all 22 reconciliation checks pass (counts, sums, cross-references, uniqueness).

### Step 9: Create PR

1. Create a feature branch: `devin/<timestamp>-<program-name>-java-rewrite`
2. Commit all files under `java-src/<program-name>/`
3. Create PR with title: `feat: modernize <program_name>.cbl to Java 17+ batch application`
4. Wait for CI checks to pass

## Common Bugs to Watch For

| Bug | Symptom | Fix |
|-----|---------|-----|
| Overpunch `}` treated as -0.00 | All negative amounts show as 0 | Sign applies to FULL number, not just last digit |
| PIC S9(10)V99 treated as 12 integers | Amounts 100× too large | V is implied decimal — use `movePointLeft(2)` |
| `float`/`double` for money | Penny rounding errors | Always use `BigDecimal` |
| Wrong RoundingMode | Off-by-one-cent errors | Use `RoundingMode.DOWN` (COBOL truncation semantics) |
| Test in wrong package | Compile errors accessing methods | Keep tests in same package or make parsing methods `public` |
| Field position mismatch | Wrong values parsed | Verify with `cut -c{start}-{end}` on sample data |

## Migration Priority Order

Batch programs (simplest → hardest):
1. CBACT01C — Account file reader (baseline pilot)
2. CBACT02C — Card data reader
3. CBCUS01C — Customer file reader
4. CBACT03C — Account cross-reference processor
5. CBEXPORT — Data export utility
6. CBACT04C — Interest calculator (business logic heavy)
7. CBTRN01C — Transaction file reader
8. CBTRN02C — Transaction posting (writes to master)
9. CBTRN03C — Transaction report generator
10. CBSTM03A/B — Statement generation (CALL chain)
11. CBIMPORT — Data import with validation (most complex batch)

## References

- `MIGRATION_PLAYBOOK.md` — Detailed playbook with phase descriptions
- `app/cpy/` — All copybook layouts (field definitions)
- `app/data/ASCII/` — Test data files converted from EBCDIC
- `golden-files/` — Golden reference JSON for comparison
- `test-harness/` — Automated reconciliation check framework
