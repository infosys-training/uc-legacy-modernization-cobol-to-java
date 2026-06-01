# COBOL to Java Migration Mapping

## Program Mapping

| COBOL Artifact | Type | Java Replacement | Location | Status |
|---|---|---|---|---|
| `app/cbl/CBACT01C.cbl` | Batch Program | `AccountFileProcessor` | `migrated-code/java/cbact01c-processor/` | Migrated |
| `app/cpy/CVACT01Y.cpy` | Copybook | `AccountRecord.java` | `.../model/AccountRecord.java` | Migrated |
| `app/cpy/CODATECN.cpy` | Copybook | `DateFormatter.java` | `.../util/DateFormatter.java` | Migrated |
| `app/asm/COBDATFT.asm` | Assembler | `DateFormatter.java` | `.../util/DateFormatter.java` | Migrated |
| `app/jcl/READACCT.jcl` | JCL | `Main.java` CLI | `.../Main.java` | Migrated |

## Paragraph-to-Method Mapping

| COBOL Paragraph | Java Method | Notes |
|---|---|---|
| `0000-ACCTFILE-OPEN` | Constructor / `process()` init | File open via BufferedReader |
| `1000-ACCTFILE-GET-NEXT` | `processNextRecord()` | readline + parse |
| `1100-DISPLAY-ACCT-RECORD` | `logAccountRecord()` | SLF4J logging |
| `1300-POPUL-ACCT-RECORD` | `buildOutRecord()` | Transform + date format + debit default. **Fidelity note:** COBOL has no ELSE/MOVE for non-zero debit (stale output field); Java intentionally uses actual value — see comment in source. |
| `1400-POPUL-ARRAY-RECORD` | `buildArrayRecord()` | Hardcoded array population |
| `1500-POPUL-VBRC-RECORD` | `buildVbRecords()` | VB1 + VB2 construction |
| `9910-DISPLAY-IO-STATUS` | Exception handling | IOException / FileNotFoundException |
| `9999-ABEND-PROGRAM` | `System.exit(16)` | Non-zero exit code |

## Complexity Metrics (from HOTSPOT_REPORT)

| Metric | Value |
|---|---|
| Lines of Code | 430 |
| Copybooks | 2 (CVACT01Y, CODATECN) |
| I/O Operations | 8 (1 input, 3 outputs x open/close/read/write) |
| External Calls | 1 (COBDATFT assembler) |
| Architecture Decision | Plain Java 17+ (no framework needed) |
