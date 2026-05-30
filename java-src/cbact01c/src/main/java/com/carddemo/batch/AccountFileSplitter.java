package com.carddemo.batch;

import com.carddemo.model.AccountRecord;
import com.carddemo.util.DateFormatter;

import java.io.*;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Java 17 rewrite of COBOL batch program CBACT01C.cbl.
 *
 * <p><b>Business Logic:</b> Reads the account master file (VSAM KSDS, 300-byte
 * records) sequentially and produces three output files:</p>
 * <ol>
 *   <li><b>Fixed-format file (OUTFILE)</b> — selected account fields with date
 *       reformatting (YYYY-MM-DD → YYYYMMDD via COBDATFT replacement) and a
 *       special rule: if ACCT-CURR-CYC-DEBIT is zero, substitute 2525.00.</li>
 *   <li><b>Array file (ARRYFILE)</b> — account ID followed by a 5-element array
 *       (slots 1–2 populated with actual balance and hardcoded debits; slot 3
 *       with negative constants; slots 4–5 zeroed) plus 4 bytes filler.</li>
 *   <li><b>Variable-length file (VBRCFILE)</b> — two records per account:
 *       a short record (ID + active status) and a longer record
 *       (ID + balance + credit limit + reissue year).</li>
 * </ol>
 *
 * <p><b>COBOL Artifacts Replaced:</b></p>
 * <ul>
 *   <li>CBACT01C.cbl — this program</li>
 *   <li>CVACT01Y.cpy — {@link AccountRecord}</li>
 *   <li>CODATECN.cpy — {@link DateFormatter}</li>
 *   <li>COBDATFT (assembler) — {@link DateFormatter#format}</li>
 * </ul>
 */
public class AccountFileSplitter {

    private static final BigDecimal DEFAULT_CYC_DEBIT = new BigDecimal("2525.00");
    private static final BigDecimal ARR_DEBIT_1 = new BigDecimal("1005.00");
    private static final BigDecimal ARR_DEBIT_2 = new BigDecimal("1525.00");
    private static final BigDecimal ARR_BAL_3 = new BigDecimal("-1025.00");
    private static final BigDecimal ARR_DEBIT_3 = new BigDecimal("-2500.00");

    private final Path inputFile;
    private final Path outFile;
    private final Path arryFile;
    private final Path vbrcFile;
    private final PrintStream log;

    public AccountFileSplitter(Path inputFile, Path outFile, Path arryFile, Path vbrcFile) {
        this(inputFile, outFile, arryFile, vbrcFile, System.out);
    }

    public AccountFileSplitter(Path inputFile, Path outFile, Path arryFile, Path vbrcFile,
                                PrintStream log) {
        this.inputFile = inputFile;
        this.outFile = outFile;
        this.arryFile = arryFile;
        this.vbrcFile = vbrcFile;
        this.log = log;
    }

    /**
     * Execute the batch job. Returns the number of records processed.
     */
    public int execute() throws IOException {
        log.println("START OF EXECUTION OF PROGRAM CBACT01C");

        List<String> lines = Files.readAllLines(inputFile);

        int count = 0;
        try (BufferedWriter outWriter = Files.newBufferedWriter(outFile);
             BufferedWriter arryWriter = Files.newBufferedWriter(arryFile);
             BufferedWriter vbrcWriter = Files.newBufferedWriter(vbrcFile)) {

            for (String line : lines) {
                if (line.isBlank()) continue;

                AccountRecord acct = AccountRecord.parse(line);
                displayAccountRecord(acct);

                writeOutRecord(outWriter, acct);
                writeArryRecord(arryWriter, acct);
                writeVbrcRecords(vbrcWriter, acct);

                count++;
            }
        }

        log.println("END OF EXECUTION OF PROGRAM CBACT01C");
        return count;
    }

    /**
     * COBOL paragraph 1100-DISPLAY-ACCT-RECORD — display each field.
     */
    private void displayAccountRecord(AccountRecord acct) {
        log.printf("ACCT-ID                 :%011d%n", acct.acctId());
        log.printf("ACCT-ACTIVE-STATUS      :%s%n", acct.activeStatus());
        log.printf("ACCT-CURR-BAL           :%s%n", formatSignedDecimal(acct.currBal()));
        log.printf("ACCT-CREDIT-LIMIT       :%s%n", formatSignedDecimal(acct.creditLimit()));
        log.printf("ACCT-CASH-CREDIT-LIMIT  :%s%n", formatSignedDecimal(acct.cashCreditLimit()));
        log.printf("ACCT-OPEN-DATE          :%s%n", acct.openDate());
        log.printf("ACCT-EXPIRAION-DATE     :%s%n", acct.expirationDate());
        log.printf("ACCT-REISSUE-DATE       :%s%n", acct.reissueDate());
        log.printf("ACCT-CURR-CYC-CREDIT    :%s%n", formatSignedDecimal(acct.currCycCredit()));
        log.printf("ACCT-CURR-CYC-DEBIT     :%s%n", formatSignedDecimal(acct.currCycDebit()));
        log.printf("ACCT-GROUP-ID           :%s%n", acct.groupId());
        log.println("-------------------------------------------------");
    }

    /**
     * Format a BigDecimal as COBOL would display PIC S9(10)V99.
     */
    static String formatSignedDecimal(BigDecimal value) {
        boolean negative = value.signum() < 0;
        BigDecimal abs = value.abs();
        String formatted = String.format("%013.2f", abs).replace('.', ' ')
                .replace(" ", "");
        // Pad to 12 chars total (10 integer + 2 decimal)
        while (formatted.length() < 12) {
            formatted = "0" + formatted;
        }
        return (negative ? "-" : "+") + formatted;
    }

    /**
     * COBOL paragraphs 1300-POPUL-ACCT-RECORD + 1350-WRITE-ACCT-RECORD.
     *
     * Writes fixed-format output record with date formatting and
     * the zero-debit substitution rule.
     */
    private void writeOutRecord(BufferedWriter writer, AccountRecord acct) throws IOException {
        // Date formatting: YYYY-MM-DD → YYYYMMDD (type 2 in, outtype 2)
        String formattedReissueDate = DateFormatter.format(acct.reissueDate(), "2", "2");

        // Special rule: if debit is zero, substitute 2525.00
        BigDecimal cycDebit = acct.currCycDebit().signum() == 0
                ? DEFAULT_CYC_DEBIT
                : acct.currCycDebit();

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%011d", acct.acctId()));
        sb.append(acct.activeStatus());
        sb.append(formatNumericField(acct.currBal(), 12));
        sb.append(formatNumericField(acct.creditLimit(), 12));
        sb.append(formatNumericField(acct.cashCreditLimit(), 12));
        sb.append(String.format("%-10s", acct.openDate()));
        sb.append(String.format("%-10s", acct.expirationDate()));
        sb.append(String.format("%-10s", formattedReissueDate));
        sb.append(formatNumericField(acct.currCycCredit(), 12));
        sb.append(formatNumericField(cycDebit, 12));
        sb.append(String.format("%-10s", acct.groupId()));

        writer.write(sb.toString());
        writer.newLine();
    }

    /**
     * COBOL paragraphs 1400-POPUL-ARRAY-RECORD + 1450-WRITE-ARRY-RECORD.
     *
     * Array structure: ACCT-ID + 5 × (BAL + DEBIT) + 4-byte filler.
     * Only slots 1–3 are populated; 4–5 remain zero.
     */
    private void writeArryRecord(BufferedWriter writer, AccountRecord acct) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%011d", acct.acctId()));

        // Slot 1: actual balance, hardcoded debit 1005.00
        sb.append(formatNumericField(acct.currBal(), 12));
        sb.append(formatNumericField(ARR_DEBIT_1, 12));

        // Slot 2: actual balance, hardcoded debit 1525.00
        sb.append(formatNumericField(acct.currBal(), 12));
        sb.append(formatNumericField(ARR_DEBIT_2, 12));

        // Slot 3: hardcoded -1025.00, hardcoded -2500.00
        sb.append(formatNumericField(ARR_BAL_3, 12));
        sb.append(formatNumericField(ARR_DEBIT_3, 12));

        // Slots 4–5: zeroes
        sb.append(formatNumericField(BigDecimal.ZERO, 12));
        sb.append(formatNumericField(BigDecimal.ZERO, 12));
        sb.append(formatNumericField(BigDecimal.ZERO, 12));
        sb.append(formatNumericField(BigDecimal.ZERO, 12));

        // 4-byte filler
        sb.append("    ");

        writer.write(sb.toString());
        writer.newLine();
    }

    /**
     * COBOL paragraphs 1500/1550/1575 — two variable-length records per account.
     *
     * Record 1 (short, 12 bytes): ACCT-ID(11) + ACTIVE-STATUS(1)
     * Record 2 (long, 39 bytes):  ACCT-ID(11) + CURR-BAL(12) + CREDIT-LIMIT(12) + REISSUE-YYYY(4)
     */
    private void writeVbrcRecords(BufferedWriter writer, AccountRecord acct) throws IOException {
        // Short record (VB1)
        String vb1 = String.format("%011d", acct.acctId()) + acct.activeStatus();
        writer.write(vb1);
        writer.newLine();

        // Long record (VB2) — extract year from reissue date
        String reissueYear = acct.reissueDate().length() >= 4
                ? acct.reissueDate().substring(0, 4)
                : "    ";

        String vb2 = String.format("%011d", acct.acctId())
                + formatNumericField(acct.currBal(), 12)
                + formatNumericField(acct.creditLimit(), 12)
                + reissueYear;
        writer.write(vb2);
        writer.newLine();
    }

    /**
     * Format a BigDecimal into a fixed-width signed numeric string
     * matching COBOL PIC S9(n)V99 display format with trailing overpunch sign.
     */
    static String formatNumericField(BigDecimal value, int totalLength) {
        boolean negative = value.signum() < 0;
        BigDecimal abs = value.abs().setScale(2, java.math.RoundingMode.HALF_UP);

        // Convert to unscaled integer string (cents)
        String digits = abs.movePointRight(2).toBigInteger().toString();

        // Pad to required length
        while (digits.length() < totalLength) {
            digits = "0" + digits;
        }
        if (digits.length() > totalLength) {
            digits = digits.substring(digits.length() - totalLength);
        }

        // Apply trailing overpunch sign to last digit
        char lastDigit = digits.charAt(digits.length() - 1);
        int d = lastDigit - '0';
        char signedChar;
        if (negative) {
            signedChar = d == 0 ? '}' : (char) ('J' + d - 1);
        } else {
            signedChar = d == 0 ? '{' : (char) ('A' + d - 1);
        }

        return digits.substring(0, digits.length() - 1) + signedChar;
    }

    public static void main(String[] args) throws IOException {
        if (args.length < 4) {
            System.err.println("Usage: AccountFileSplitter <acctfile> <outfile> <arryfile> <vbrcfile>");
            System.exit(1);
        }

        AccountFileSplitter splitter = new AccountFileSplitter(
                Path.of(args[0]), Path.of(args[1]), Path.of(args[2]), Path.of(args[3])
        );

        int count = splitter.execute();
        System.out.printf("Processed %d account records.%n", count);
    }
}
