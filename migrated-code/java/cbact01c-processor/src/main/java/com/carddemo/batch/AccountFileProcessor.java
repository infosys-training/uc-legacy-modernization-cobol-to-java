package com.carddemo.batch;

import com.carddemo.batch.model.*;
import com.carddemo.batch.parser.AccountRecordParser;
import com.carddemo.batch.util.DateFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.*;
import java.math.BigDecimal;
import java.nio.file.*;
import java.util.*;

/**
 * Core pipeline mirroring the CBACT01C PROCEDURE DIVISION.
 * Reads account records, transforms them, and writes to three output files.
 */
public class AccountFileProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(AccountFileProcessor.class);
    private final AccountRecordParser parser = new AccountRecordParser();
    private final DateFormatter dateFormatter = new DateFormatter();

    public void process(Path inputFile, Path outputDir, OutputFormat format) throws IOException {
        LOG.info("START OF EXECUTION OF PROGRAM CBACT01C");
        Files.createDirectories(outputDir);

        try (OutputWriter writer = createWriter(format, outputDir);
             BufferedReader reader = Files.newBufferedReader(inputFile)) {

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                AccountRecord account = parser.parse(line);

                // 1100-DISPLAY-ACCT-RECORD
                logAccountRecord(account);

                // 1300-POPUL-ACCT-RECORD
                OutAccountRecord outRecord = buildOutRecord(account);

                // 1350-WRITE-ACCT-RECORD
                writer.writeOutRecord(outRecord);

                // 1400-POPUL-ARRAY-RECORD
                ArrayRecord arrayRecord = buildArrayRecord(account);

                // 1450-WRITE-ARRY-RECORD
                writer.writeArrayRecord(arrayRecord);

                // 1500-POPUL-VBRC-RECORD
                VbRecord1 vb1 = buildVbRecord1(account);
                VbRecord2 vb2 = buildVbRecord2(account);

                // 1550/1575-WRITE-VB-RECORD
                writer.writeVbRecord1(vb1);
                writer.writeVbRecord2(vb2);
            }
        }

        LOG.info("END OF EXECUTION OF PROGRAM CBACT01C");
    }

    // 1100-DISPLAY-ACCT-RECORD
    private void logAccountRecord(AccountRecord acct) {
        LOG.info("ACCT-ID                 :{}", acct.acctId());
        LOG.info("ACCT-ACTIVE-STATUS      :{}", acct.activeStatus());
        LOG.info("ACCT-CURR-BAL           :{}", acct.currentBalance());
        LOG.info("ACCT-CREDIT-LIMIT       :{}", acct.creditLimit());
        LOG.info("ACCT-CASH-CREDIT-LIMIT  :{}", acct.cashCreditLimit());
        LOG.info("ACCT-OPEN-DATE          :{}", acct.openDate());
        LOG.info("ACCT-EXPIRAION-DATE     :{}", acct.expirationDate());
        LOG.info("ACCT-REISSUE-DATE       :{}", acct.reissueDate());
        LOG.info("ACCT-CURR-CYC-CREDIT    :{}", acct.currentCycleCredit());
        LOG.info("ACCT-CURR-CYC-DEBIT     :{}", acct.currentCycleDebit());
        LOG.info("ACCT-GROUP-ID           :{}", acct.groupId());
        LOG.info("-------------------------------------------------");
    }

    // 1300-POPUL-ACCT-RECORD
    private OutAccountRecord buildOutRecord(AccountRecord acct) {
        String formattedReissueDate = dateFormatter.convertDate(acct.reissueDate(), '2', '2');

        BigDecimal debit = acct.currentCycleDebit();
        if (debit.compareTo(BigDecimal.ZERO) == 0) {
            debit = new BigDecimal("2525.00");
        }

        return new OutAccountRecord(
                acct.acctId(),
                acct.activeStatus(),
                acct.currentBalance(),
                acct.creditLimit(),
                acct.cashCreditLimit(),
                acct.openDate(),
                acct.expirationDate(),
                formattedReissueDate,
                acct.currentCycleCredit(),
                debit,
                acct.groupId()
        );
    }

    // 1400-POPUL-ARRAY-RECORD
    private ArrayRecord buildArrayRecord(AccountRecord acct) {
        List<ArrayElement> elements = new ArrayList<>(5);

        // Element 0: balance = input balance, debit = 1005.00
        elements.add(new ArrayElement(acct.currentBalance(), new BigDecimal("1005.00")));
        // Element 1: balance = input balance, debit = 1525.00
        elements.add(new ArrayElement(acct.currentBalance(), new BigDecimal("1525.00")));
        // Element 2: balance = -1025.00, debit = -2500.00
        elements.add(new ArrayElement(new BigDecimal("-1025.00"), new BigDecimal("-2500.00")));
        // Elements 3-4: zeros (from INITIALIZE)
        elements.add(new ArrayElement(BigDecimal.ZERO.setScale(2), BigDecimal.ZERO.setScale(2)));
        elements.add(new ArrayElement(BigDecimal.ZERO.setScale(2), BigDecimal.ZERO.setScale(2)));

        return new ArrayRecord(acct.acctId(), elements, "    ");
    }

    // 1500-POPUL-VBRC-RECORD
    private VbRecord1 buildVbRecord1(AccountRecord acct) {
        return new VbRecord1(acct.acctId(), acct.activeStatus());
    }

    private VbRecord2 buildVbRecord2(AccountRecord acct) {
        String reissueYyyy = acct.reissueDate().length() >= 4
                ? acct.reissueDate().substring(0, 4)
                : acct.reissueDate();
        return new VbRecord2(acct.acctId(), acct.currentBalance(), acct.creditLimit(), reissueYyyy);
    }

    private OutputWriter createWriter(OutputFormat format, Path outputDir) throws IOException {
        return switch (format) {
            case JSON -> new JsonOutputWriter(outputDir);
            case COBOL_BINARY -> throw new UnsupportedOperationException("COBOL_BINARY output not yet implemented");
        };
    }
}
