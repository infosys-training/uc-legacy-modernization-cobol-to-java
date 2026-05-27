package com.carddemo.batch.cbact01c;

import com.carddemo.batch.cbact01c.io.AccountFileReader;
import com.carddemo.batch.cbact01c.io.AccountFileWriter;
import com.carddemo.batch.cbact01c.model.AccountRecord;
import com.carddemo.batch.cbact01c.model.ArrayAccountRecord;
import com.carddemo.batch.cbact01c.model.OutputAccountRecord;
import com.carddemo.batch.cbact01c.model.VbrRecord;

import java.io.IOException;
import java.nio.file.Path;
import java.util.logging.Logger;

/**
 * Java 17+ rewrite of COBOL batch program CBACT01C.cbl.
 *
 * CBACT01C reads an indexed account file (VSAM KSDS) sequentially and
 * writes three output files:
 * <ol>
 *   <li>OUTFILE  — flat account extract with date reformatting</li>
 *   <li>ARRYFILE — array-format records with fixed balance/debit values</li>
 *   <li>VBRCFILE — variable-length records (two per account)</li>
 * </ol>
 *
 * Usage: java -jar cbact01c.jar &lt;acctfile&gt; &lt;outfile&gt; &lt;arryfile&gt; &lt;vbrcfile&gt;
 */
public class Cbact01cApplication {

    private static final Logger LOG = Logger.getLogger(Cbact01cApplication.class.getName());

    public static void main(String[] args) {
        if (args.length < 4) {
            System.err.println("Usage: Cbact01cApplication <acctfile> <outfile> <arryfile> <vbrcfile>");
            System.exit(1);
        }

        Path acctFile = Path.of(args[0]);
        Path outFile = Path.of(args[1]);
        Path arryFile = Path.of(args[2]);
        Path vbrcFile = Path.of(args[3]);

        try {
            int count = process(acctFile, outFile, arryFile, vbrcFile);
            LOG.info("Processed %d account records.".formatted(count));
        } catch (IOException e) {
            LOG.severe("ABENDING PROGRAM: " + e.getMessage());
            System.exit(999);
        }
    }

    /**
     * Processes the account file and writes all three output files.
     *
     * @return the number of records processed
     */
    public static int process(Path acctFile, Path outFile, Path arryFile, Path vbrcFile)
            throws IOException {
        LOG.info("START OF EXECUTION OF PROGRAM CBACT01C");

        AccountProcessor processor = new AccountProcessor();
        int count = 0;

        try (AccountFileReader reader = new AccountFileReader(acctFile);
             AccountFileWriter writer = new AccountFileWriter(outFile, arryFile, vbrcFile)) {

            AccountRecord record;
            while ((record = reader.readNext()) != null) {
                processor.displayRecord(record);

                OutputAccountRecord outRec = processor.toOutputRecord(record);
                writer.writeOutputRecord(outRec);

                ArrayAccountRecord arrRec = processor.toArrayRecord(record);
                writer.writeArrayRecord(arrRec);

                VbrRecord.Type1 vbr1 = processor.toVbrType1(record);
                writer.writeVbrRecord(vbr1);

                VbrRecord.Type2 vbr2 = processor.toVbrType2(record);
                writer.writeVbrRecord(vbr2);

                count++;
            }
        }

        LOG.info("END OF EXECUTION OF PROGRAM CBACT01C");
        return count;
    }
}
