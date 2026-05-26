# CBACT01C.cbl → Java Migration Notes

## Source Program

| Attribute | Value |
|-----------|-------|
| Program   | `CBACT01C.cbl` |
| Application | CardDemo (AWS Mainframe Modernization) |
| Type      | Batch COBOL |
| Function  | Read account VSAM file, transform, write to 3 output files |

## COBOL → Java Mapping

### Data Structures

| COBOL (Copybook) | Java Class | Notes |
|-------------------|-----------|-------|
| `CVACT01Y.cpy` → `ACCOUNT-RECORD` (300 bytes) | `AccountRecord.java` | 12 fields + 178-byte filler |
| `FD OUT-FILE` → `OUT-ACCT-REC` | `OutputAccountRecord.java` | Includes COMP-3 field for cycle debit |
| `FD ARRY-FILE` → `ARR-ARRAY-REC` | `ArrayRecord.java` | `OCCURS 5 TIMES` → `BigDecimal[5]` |
| `VBRC-REC1` (12 bytes) | `VariableRecord1.java` (record) | Account ID + status |
| `VBRC-REC2` (39 bytes) | `VariableRecord2.java` (record) | Account ID + balance + limit + year |
| `CODATECN.cpy` → `CODATECN-REC` | `CobolDateFormatter.java` | Date format conversion utility |

### COBOL Numeric Types

| COBOL PIC | Java Type | Parsing |
|-----------|-----------|---------|
| `PIC 9(11)` | `long` | Direct numeric parse |
| `PIC S9(10)V99` | `BigDecimal` | Zoned decimal with trailing overpunch sign |
| `PIC S9(10)V99 COMP-3` | `BigDecimal` | Packed decimal (same Java type, different COBOL storage) |
| `PIC X(n)` | `String` / `char` | Direct character extraction |

### Trailing Overpunch (Zoned Decimal Sign Convention)

The ASCII data files use trailing overpunch to encode the sign of numeric fields:

| Last Character | Digit | Sign |
|----------------|-------|------|
| `{` | 0 | + |
| `A`–`I` | 1–9 | + |
| `}` | 0 | − |
| `J`–`R` | 1–9 | − |

Example: `00000001940{` → digits `000000019400`, sign `+` → `19400.00`

### Business Logic

| COBOL Paragraph | Java Method | Logic |
|-----------------|-------------|-------|
| `1100-DISPLAY-ACCT-RECORD` | `AccountProcessor.displayAccountRecord()` | Print all fields |
| `1300-POPUL-ACCT-RECORD` | `AccountProcessor.populateOutputRecord()` | Copy fields, reformat date, override zero debit |
| `1400-POPUL-ARRAY-RECORD` | `AccountProcessor.populateArrayRecord()` | Hardcoded test values at indices 1-3 |
| `1500-POPUL-VBRC-RECORD` | `AccountProcessor.populateVbRecord1/2()` | Two variable-length output records |
| `CALL 'COBDATFT'` | `CobolDateFormatter.formatDate()` | Assembler date format conversion |

### Key Business Rules Preserved

1. **Zero debit override**: When `ACCT-CURR-CYC-DEBIT` is zero, the output record gets `2525.00` instead (COBOL line ~236-238)
2. **Date reformatting**: Reissue date is converted from `YYYY-MM-DD` to `YYYYMMDD` via the date formatter
3. **Array hardcoding**: The array record always gets `1005.00`, `1525.00`, `-1025.00`, `-2500.00` at fixed indices
4. **Reissue year extraction**: Variable record 2 takes only the first 4 characters (year) of the reissue date

### File I/O Mapping

| COBOL | Java | Organization |
|-------|------|-------------|
| `ACCTFILE` (VSAM KSDS, input) | `AccountFileReader.readAll()` | Fixed-width 300-byte records → line-by-line |
| `OUTFILE` (sequential, output) | `OutputFileWriter.writeOutputRecords()` | Pipe-delimited text |
| `ARRYFILE` (sequential, output) | `OutputFileWriter.writeArrayRecords()` | Pipe-delimited text |
| `VBRCFILE` (variable-length, output) | `OutputFileWriter.writeVariableRecords()` | Text, VB1 + VB2 alternating |

### Error Handling

| COBOL | Java |
|-------|------|
| `ACCTFILE-STATUS = '10'` (EOF) | End of `BufferedReader` stream (null line) |
| `9910-DISPLAY-IO-STATUS` | `IOException` with message |
| `9999-ABEND-PROGRAM` → `CEE3ABD` | `RuntimeException` / propagated `IOException` |

## How to Build and Run

```bash
cd java-migration
mvn clean test        # Run parity tests
mvn clean package     # Build JAR
java -jar target/cbact01c-java-migration-1.0.0-SNAPSHOT.jar \
    ../app/data/ASCII/acctdata.txt ./output
```

## Test Coverage

| Test Class | What It Verifies |
|-----------|-----------------|
| `AccountFileReaderTest` | Zoned decimal parsing, overpunch signs, field extraction from fixed-width records |
| `AccountProcessorTest` | Business rule parity: zero-debit override, date reformatting, array values, VB records |
| `CobolDateFormatterTest` | All 4 date format combinations matching COBDATFT behavior |
