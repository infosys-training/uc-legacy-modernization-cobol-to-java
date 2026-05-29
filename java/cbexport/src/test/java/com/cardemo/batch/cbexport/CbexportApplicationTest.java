package com.cardemo.batch.cbexport;

import com.cardemo.batch.cbexport.io.*;
import com.cardemo.batch.cbexport.model.*;
import com.cardemo.batch.cbexport.model.ExportRecord.*;
import com.cardemo.batch.cbexport.service.ExportService;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CbexportApplicationTest {

    // ---------------------------------------------------------------
    // CobolFieldParser — zoned decimal / overpunch
    // ---------------------------------------------------------------

    @Nested
    class CobolFieldParserTest {

        @Test
        void parsePositiveZero() {
            // '{' = +0, all zeros → 0.00
            assertEquals(0,
                    BigDecimal.ZERO.compareTo(
                            CobolFieldParser.parseZonedDecimal("00000000000{", 2)));
            // '{' = +0, "00000001940{" → 000000019400 → V99 → 194.00
            assertEquals(0,
                    new BigDecimal("194.00").compareTo(
                            CobolFieldParser.parseZonedDecimal("00000001940{", 2)));
        }

        @Test
        void parsePositiveDigits() {
            // 'A'=+1, 'I'=+9
            assertEquals(0,
                    new BigDecimal("194.01").compareTo(
                            CobolFieldParser.parseZonedDecimal("00000001940A", 2)));
            assertEquals(0,
                    new BigDecimal("194.09").compareTo(
                            CobolFieldParser.parseZonedDecimal("00000001940I", 2)));
            // 'E' = +5
            assertEquals(0,
                    new BigDecimal("194.05").compareTo(
                            CobolFieldParser.parseZonedDecimal("00000001940E", 2)));
        }

        @Test
        void parseNegativeValues() {
            // '}' = -0
            assertEquals(0,
                    new BigDecimal("0.00").compareTo(
                            CobolFieldParser.parseZonedDecimal("00000000000}", 2)));
            // 'J' = -1
            assertEquals(0,
                    new BigDecimal("-194.01").compareTo(
                            CobolFieldParser.parseZonedDecimal("00000001940J", 2)));
            // 'R' = -9
            assertEquals(0,
                    new BigDecimal("-194.09").compareTo(
                            CobolFieldParser.parseZonedDecimal("00000001940R", 2)));
        }

        @Test
        void parseNegativeZero() {
            BigDecimal result = CobolFieldParser.parseZonedDecimal("00000000000}", 2);
            assertEquals(0, BigDecimal.ZERO.compareTo(result));
        }

        @Test
        void parseBlankReturnsZero() {
            assertEquals(BigDecimal.ZERO, CobolFieldParser.parseZonedDecimal("", 2));
            assertEquals(BigDecimal.ZERO, CobolFieldParser.parseZonedDecimal("   ", 2));
            assertEquals(BigDecimal.ZERO, CobolFieldParser.parseZonedDecimal(null, 2));
        }

        @Test
        void parseUnsignedDigit() {
            // plain digit as last char
            assertEquals(0,
                    new BigDecimal("194.05").compareTo(
                            CobolFieldParser.parseZonedDecimal("000000019405", 2)));
        }

        @Test
        void extractFieldPadsShortLine() {
            assertEquals("ab   ", CobolFieldParser.extractField("ab", 0, 5));
            assertEquals("     ", CobolFieldParser.extractField("ab", 5, 5));
        }

        @Test
        void extractFieldNormalCase() {
            assertEquals("ello", CobolFieldParser.extractField("Hello, World!", 1, 4));
        }

        @Test
        void extractStringTrimsSpaces() {
            assertEquals("Test", CobolFieldParser.extractString("  Test          rest", 2, 10));
        }

        @Test
        void extractIntFromField() {
            assertEquals(12345, CobolFieldParser.extractInt("00012345rest", 0, 8));
        }

        @Test
        void extractIntBlankReturnsZero() {
            assertEquals(0, CobolFieldParser.extractInt("          ", 0, 10));
        }

        @Test
        void signedDecimalFromLine() {
            // "00000020200{" at offset 24, len 12 → 000000202000 → V99 → 2020.00
            String line = "00000000001Y00000001940{00000020200{00000010200{";
            assertEquals(0,
                    new BigDecimal("2020.00").compareTo(
                            CobolFieldParser.extractSignedDecimal(line, 24, 12, 2)));
        }

        @Test
        void parseAllPositiveOverpunchChars() {
            char[] chars = {'{', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I'};
            for (int i = 0; i < chars.length; i++) {
                BigDecimal expected = new BigDecimal(i).movePointLeft(2);
                BigDecimal actual = CobolFieldParser.parseZonedDecimal("0000000000" + chars[i], 2);
                assertEquals(0, expected.compareTo(actual),
                        "Failed for positive overpunch char '" + chars[i] + "'");
            }
        }

        @Test
        void parseAllNegativeOverpunchChars() {
            char[] chars = {'}', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R'};
            for (int i = 0; i < chars.length; i++) {
                BigDecimal expected = new BigDecimal(-i).movePointLeft(2);
                BigDecimal actual = CobolFieldParser.parseZonedDecimal("0000000000" + chars[i], 2);
                assertEquals(0, expected.compareTo(actual),
                        "Failed for negative overpunch char '" + chars[i] + "'");
            }
        }

        @Test
        void parseOverpunchGFromTransactionData() {
            // From dailytran.txt: "0000005047G" → G=+7 → 00000050477 / 100 = 504.77
            assertEquals(0,
                    new BigDecimal("504.77").compareTo(
                            CobolFieldParser.parseZonedDecimal("0000005047G", 2)));
        }
    }

    // ---------------------------------------------------------------
    // File Readers — individual record parsing
    // ---------------------------------------------------------------

    @Nested
    class CustomerFileReaderTest {

        @Test
        void parseFirstCustomerLine() {
            String line = "000000001Immanuel                 Madeline"
                    + "                 Kessler                  "
                    + "618 Deshaun Route"
                    + "                                 "
                    + "Apt. 802"
                    + "                                          "
                    + "Altenwerthshire"
                    + "                                   "
                    + "NC" + "USA"
                    + "12546     "
                    + "(908)119-8310  "
                    + "(373)693-8684  "
                    + "020973888"
                    + "00000000000049368437"
                    + "1961-06-08"
                    + "0053581756"
                    + "Y"
                    + "274";

            CustomerRecord rec = CustomerFileReader.parseLine(line);
            assertEquals(1, rec.custId());
            assertEquals("Immanuel", rec.firstName());
            assertEquals("Madeline", rec.middleName());
            assertEquals("Kessler", rec.lastName());
            assertEquals("618 Deshaun Route", rec.addressLines().get(0));
            assertEquals("Apt. 802", rec.addressLines().get(1));
            assertEquals("Altenwerthshire", rec.addressLines().get(2));
            assertEquals("NC", rec.stateCode());
            assertEquals("USA", rec.countryCode());
            assertEquals("12546", rec.zipCode());
            assertEquals("(908)119-8310", rec.phoneNumbers().get(0));
            assertEquals("(373)693-8684", rec.phoneNumbers().get(1));
            assertEquals(20973888, rec.ssn());
            assertEquals("00000000000049368437", rec.govtIssuedId());
            assertEquals("1961-06-08", rec.dateOfBirth());
            assertEquals("0053581756", rec.eftAccountId());
            assertEquals("Y", rec.primaryCardHolderInd());
            assertEquals(274, rec.ficoCreditScore());
        }

        @Test
        void readAllFromFile(@TempDir Path tmpDir) throws IOException {
            String line1 = padRight("000000001TestFirst                TestMiddle               TestLast                 "
                    + "123 Main St                                       "
                    + "                                                  "
                    + "                                                  "
                    + "NY" + "USA" + "10001     "
                    + "(212)555-1234  " + "(212)555-5678  "
                    + "123456789" + "GOVTID12345678901234" + "1990-01-15"
                    + "0012345678" + "Y" + "750", 500);

            Path file = tmpDir.resolve("custdata.txt");
            Files.writeString(file, line1 + "\n");

            List<CustomerRecord> records = CustomerFileReader.readAll(file);
            assertEquals(1, records.size());
            assertEquals(1, records.get(0).custId());
            assertEquals("TestFirst", records.get(0).firstName());
        }
    }

    @Nested
    class AccountFileReaderTest {

        @Test
        void parseFirstAccountLine() {
            String line = "00000000001Y00000001940{00000020200{00000010200{"
                    + "2014-11-20" + "2025-05-20" + "2025-05-20"
                    + "00000000000{" + "00000000000{"
                    + "A000000000";

            AccountRecord rec = AccountFileReader.parseLine(line);
            assertEquals("00000000001", rec.acctId());
            assertEquals("Y", rec.activeStatus());
            assertEquals(0, new BigDecimal("194.00").compareTo(rec.currentBalance()));
            assertEquals(0, new BigDecimal("2020.00").compareTo(rec.creditLimit()));
            assertEquals(0, new BigDecimal("1020.00").compareTo(rec.cashCreditLimit()));
            assertEquals("2014-11-20", rec.openDate());
            assertEquals("2025-05-20", rec.expirationDate());
            assertEquals("2025-05-20", rec.reissueDate());
            assertEquals(0, BigDecimal.ZERO.compareTo(rec.currentCycleCredit()));
            assertEquals(0, BigDecimal.ZERO.compareTo(rec.currentCycleDebit()));
        }

        @Test
        void readAllFromFile(@TempDir Path tmpDir) throws IOException {
            String line = padRight("00000000001Y00000001940{00000020200{00000010200{"
                    + "2014-11-20" + "2025-05-20" + "2025-05-20"
                    + "00000000000{" + "00000000000{"
                    + "10001     " + "GRP001    ", 300);

            Path file = tmpDir.resolve("acctdata.txt");
            Files.writeString(file, line + "\n");

            List<AccountRecord> records = AccountFileReader.readAll(file);
            assertEquals(1, records.size());
            assertEquals("00000000001", records.get(0).acctId());
        }
    }

    @Nested
    class CardXrefFileReaderTest {

        @Test
        void parseXrefLine() {
            String line = "0500024453765740000000050" + "00000000050";
            CardXrefRecord rec = CardXrefFileReader.parseLine(line);
            assertEquals("0500024453765740", rec.cardNum());
            assertEquals(50, rec.custId());
            assertEquals("00000000050", rec.acctId());
        }

        @Test
        void parseShortLineWithPadding() {
            // Trailing spaces may be trimmed in data files
            String line = "050002445376574000000005000000000050";
            CardXrefRecord rec = CardXrefFileReader.parseLine(line);
            assertEquals("0500024453765740", rec.cardNum());
            assertEquals(50, rec.custId());
            assertEquals("00000000050", rec.acctId());
        }

        @Test
        void readAllFromFile(@TempDir Path tmpDir) throws IOException {
            Path file = tmpDir.resolve("cardxref.txt");
            Files.writeString(file,
                    "0500024453765740000000050" + "00000000050" + " ".repeat(14) + "\n"
                            + "1234567890123456000000001" + "00000000001" + " ".repeat(14) + "\n");

            List<CardXrefRecord> records = CardXrefFileReader.readAll(file);
            assertEquals(2, records.size());
            assertEquals("0500024453765740", records.get(0).cardNum());
            assertEquals("1234567890123456", records.get(1).cardNum());
        }
    }

    @Nested
    class TransactionFileReaderTest {

        @Test
        void parseTransactionLine() {
            // Build a transaction line matching CVTRA05Y layout (350 chars)
            StringBuilder sb = new StringBuilder();
            sb.append("0000000000683580"); // TRAN-ID X(16)
            sb.append("01");               // TRAN-TYPE-CD X(02)
            sb.append("0001");             // TRAN-CAT-CD 9(04)
            sb.append(padRight("POS TERM", 10));   // TRAN-SOURCE X(10)
            sb.append(padRight("Purchase at Abshire-Lowe", 100)); // TRAN-DESC X(100)
            sb.append("0000005047G");      // TRAN-AMT S9(09)V99 = +504.77
            sb.append("800000000");        // TRAN-MERCHANT-ID 9(09)
            sb.append(padRight("Abshire-Lowe", 50));  // TRAN-MERCHANT-NAME X(50)
            sb.append(padRight("North Enoshaven", 50)); // TRAN-MERCHANT-CITY X(50)
            sb.append(padRight("72112", 10));     // TRAN-MERCHANT-ZIP X(10)
            sb.append("4859452612877065"); // TRAN-CARD-NUM X(16)
            sb.append(padRight("2022-06-10 19:27:53.000000", 26)); // TRAN-ORIG-TS X(26)
            sb.append(padRight("", 26));   // TRAN-PROC-TS X(26)
            sb.append(padRight("", 20));   // FILLER X(20)

            TransactionRecord rec = TransactionFileReader.parseLine(sb.toString());
            assertEquals("0000000000683580", rec.tranId());
            assertEquals("01", rec.typeCode());
            assertEquals(1, rec.categoryCode());
            assertEquals("POS TERM", rec.source());
            assertEquals("Purchase at Abshire-Lowe", rec.description());
            assertEquals(0, new BigDecimal("504.77").compareTo(rec.amount()));
            assertEquals(800000000, rec.merchantId());
            assertEquals("Abshire-Lowe", rec.merchantName());
            assertEquals("North Enoshaven", rec.merchantCity());
            assertEquals("72112", rec.merchantZip());
            assertEquals("4859452612877065", rec.cardNum());
            assertEquals("2022-06-10 19:27:53.000000", rec.origTimestamp());
        }
    }

    @Nested
    class CardFileReaderTest {

        @Test
        void parseCardLine() {
            StringBuilder sb = new StringBuilder();
            sb.append("0500024453765740"); // CARD-NUM X(16)
            sb.append("00000000050");      // CARD-ACCT-ID 9(11)
            sb.append("747");              // CARD-CVV-CD 9(03)
            sb.append(padRight("Aniya Von", 50)); // CARD-EMBOSSED-NAME X(50)
            sb.append("2023-03-09");       // CARD-EXPIRAION-DATE X(10)
            sb.append("Y");                // CARD-ACTIVE-STATUS X(01)
            sb.append(" ".repeat(59));     // FILLER X(59)

            CardRecord rec = CardFileReader.parseLine(sb.toString());
            assertEquals("0500024453765740", rec.cardNum());
            assertEquals("00000000050", rec.acctId());
            assertEquals(747, rec.cvvCode());
            assertEquals("Aniya Von", rec.embossedName());
            assertEquals("2023-03-09", rec.expirationDate());
            assertEquals("Y", rec.activeStatus());
        }

        @Test
        void readAllFromFile(@TempDir Path tmpDir) throws IOException {
            String line = padRight("0500024453765740" + "00000000050" + "747"
                    + padRight("Aniya Von", 50)
                    + "2023-03-09" + "Y", 150);
            Path file = tmpDir.resolve("carddata.txt");
            Files.writeString(file, line + "\n");

            List<CardRecord> records = CardFileReader.readAll(file);
            assertEquals(1, records.size());
            assertEquals("0500024453765740", records.get(0).cardNum());
        }
    }

    // ---------------------------------------------------------------
    // Export Record creation
    // ---------------------------------------------------------------

    @Nested
    class ExportRecordTest {

        @Test
        void customerExportRecordType() {
            CustomerExport exp = new CustomerExport(
                    "2026-05-29 07:00:00.00", 1, "0001", "NORTH",
                    1, "John", "M", "Doe",
                    List.of("123 St", "Apt 1", ""),
                    "NY", "USA", "10001",
                    List.of("555-1234", "555-5678"),
                    123456789, "GOVT123", "1990-01-01",
                    "0012345678", "Y", 750
            );
            assertEquals('C', exp.recordType());
            assertEquals("0001", exp.branchId());
            assertEquals("NORTH", exp.regionCode());
        }

        @Test
        void accountExportRecordType() {
            AccountExport exp = new AccountExport(
                    "2026-05-29 07:00:00.00", 2, "0001", "NORTH",
                    "00000000001", "Y",
                    new BigDecimal("194.00"), new BigDecimal("202.00"),
                    new BigDecimal("102.00"),
                    "2014-11-20", "2025-05-20", "2025-05-20",
                    BigDecimal.ZERO, BigDecimal.ZERO,
                    "10001", "GRP001"
            );
            assertEquals('A', exp.recordType());
        }

        @Test
        void xrefExportRecordType() {
            XrefExport exp = new XrefExport(
                    "2026-05-29 07:00:00.00", 3, "0001", "NORTH",
                    "0500024453765740", 50, "00000000050"
            );
            assertEquals('X', exp.recordType());
        }

        @Test
        void transactionExportRecordType() {
            TransactionExport exp = new TransactionExport(
                    "2026-05-29 07:00:00.00", 4, "0001", "NORTH",
                    "0000000000683580", "01", 1, "POS TERM",
                    "Purchase", new BigDecimal("504.77"),
                    800000000, "Abshire-Lowe", "North Enoshaven",
                    "72112", "4859452612877065",
                    "2022-06-10 19:27:53.000000", ""
            );
            assertEquals('T', exp.recordType());
        }

        @Test
        void cardExportRecordType() {
            CardExport exp = new CardExport(
                    "2026-05-29 07:00:00.00", 5, "0001", "NORTH",
                    "0500024453765740", "00000000050",
                    747, "Aniya Von", "2023-03-09", "Y"
            );
            assertEquals('D', exp.recordType());
        }
    }

    // ---------------------------------------------------------------
    // ExportFileWriter — pipe-delimited output
    // ---------------------------------------------------------------

    @Nested
    class ExportFileWriterTest {

        @Test
        void headerLine() {
            assertEquals("RecordType|Timestamp|SequenceNum|BranchID|RegionCode|Data",
                    ExportFileWriter.headerLine());
        }

        @Test
        void formatCustomerRecord() {
            CustomerExport exp = new CustomerExport(
                    "2026-05-29 07:00:00.00", 1, "0001", "NORTH",
                    42, "Jane", "A", "Smith",
                    List.of("100 Oak Ave", "Suite 5", ""),
                    "CA", "USA", "90210",
                    List.of("555-0001", "555-0002"),
                    999888777, "GOVT999", "1985-12-25",
                    "9876543210", "N", 680
            );
            String line = ExportFileWriter.formatRecord(exp);
            assertTrue(line.startsWith("C|2026-05-29 07:00:00.00|1|0001|NORTH|"));
            assertTrue(line.contains("Jane"));
            assertTrue(line.contains("42"));
        }

        @Test
        void formatAccountRecord() {
            AccountExport exp = new AccountExport(
                    "2026-05-29 07:00:00.00", 2, "0001", "NORTH",
                    "00000000001", "Y",
                    new BigDecimal("194.00"), new BigDecimal("5000.00"),
                    new BigDecimal("1000.00"),
                    "2020-01-01", "2025-12-31", "2024-06-01",
                    new BigDecimal("100.50"), new BigDecimal("50.25"),
                    "10001", "GRP001"
            );
            String line = ExportFileWriter.formatRecord(exp);
            assertTrue(line.startsWith("A|"));
            assertTrue(line.contains("194.00"));
            assertTrue(line.contains("5000.00"));
        }

        @Test
        void writeAllProducesCorrectOutput(@TempDir Path tmpDir) throws IOException {
            List<ExportRecord> records = List.of(
                    new XrefExport("2026-05-29 07:00:00.00", 1, "0001", "NORTH",
                            "1234567890123456", 1, "00000000001"),
                    new CardExport("2026-05-29 07:00:00.00", 2, "0001", "NORTH",
                            "1234567890123456", "00000000001", 123,
                            "John Doe", "2025-12-31", "Y")
            );
            Path out = tmpDir.resolve("export.csv");
            ExportFileWriter.writeAll(out, records);

            List<String> lines = Files.readAllLines(out);
            assertEquals(3, lines.size()); // header + 2 records
            assertTrue(lines.get(0).startsWith("RecordType|"));
            assertTrue(lines.get(1).startsWith("X|"));
            assertTrue(lines.get(2).startsWith("D|"));
        }
    }

    // ---------------------------------------------------------------
    // ExportService — statistics and orchestration
    // ---------------------------------------------------------------

    @Nested
    class ExportServiceTest {

        @Test
        void statisticsCounting(@TempDir Path tmpDir) throws IOException {
            // Create minimal data files
            writeMinimalCustomerFile(tmpDir.resolve("custdata.txt"), 3);
            writeMinimalAccountFile(tmpDir.resolve("acctdata.txt"), 2);
            writeMinimalXrefFile(tmpDir.resolve("cardxref.txt"), 4);
            writeMinimalTransactionFile(tmpDir.resolve("dailytran.txt"), 5);
            writeMinimalCardFile(tmpDir.resolve("carddata.txt"), 1);

            Path output = tmpDir.resolve("export.csv");
            ExportService service = new ExportService();
            ExportService.ExportStatistics stats = service.runExport(
                    tmpDir.resolve("custdata.txt"),
                    tmpDir.resolve("acctdata.txt"),
                    tmpDir.resolve("cardxref.txt"),
                    tmpDir.resolve("dailytran.txt"),
                    tmpDir.resolve("carddata.txt"),
                    output
            );

            assertEquals(3, stats.customers());
            assertEquals(2, stats.accounts());
            assertEquals(4, stats.xrefs());
            assertEquals(5, stats.transactions());
            assertEquals(1, stats.cards());
            assertEquals(15, stats.total());

            // Verify output file has header + 15 data records
            List<String> lines = Files.readAllLines(output);
            assertEquals(16, lines.size()); // 1 header + 15 records
        }

        @Test
        void sequenceNumbersAreIncremental(@TempDir Path tmpDir) throws IOException {
            writeMinimalCustomerFile(tmpDir.resolve("custdata.txt"), 2);
            writeMinimalAccountFile(tmpDir.resolve("acctdata.txt"), 1);
            writeMinimalXrefFile(tmpDir.resolve("cardxref.txt"), 0);
            writeMinimalTransactionFile(tmpDir.resolve("dailytran.txt"), 0);
            writeMinimalCardFile(tmpDir.resolve("carddata.txt"), 0);

            Path output = tmpDir.resolve("export.csv");
            ExportService service = new ExportService();
            service.runExport(
                    tmpDir.resolve("custdata.txt"),
                    tmpDir.resolve("acctdata.txt"),
                    tmpDir.resolve("cardxref.txt"),
                    tmpDir.resolve("dailytran.txt"),
                    tmpDir.resolve("carddata.txt"),
                    output
            );

            List<String> lines = Files.readAllLines(output);
            // Skip header, check sequence numbers
            assertEquals("1", lines.get(1).split("\\|")[2]); // first record seq=1
            assertEquals("2", lines.get(2).split("\\|")[2]); // second record seq=2
            assertEquals("3", lines.get(3).split("\\|")[2]); // third record seq=3
        }

        @Test
        void emptyFilesProduceZeroStatistics(@TempDir Path tmpDir) throws IOException {
            // Create empty files
            Files.writeString(tmpDir.resolve("custdata.txt"), "");
            Files.writeString(tmpDir.resolve("acctdata.txt"), "");
            Files.writeString(tmpDir.resolve("cardxref.txt"), "");
            Files.writeString(tmpDir.resolve("dailytran.txt"), "");
            Files.writeString(tmpDir.resolve("carddata.txt"), "");

            Path output = tmpDir.resolve("export.csv");
            ExportService service = new ExportService();
            ExportService.ExportStatistics stats = service.runExport(
                    tmpDir.resolve("custdata.txt"),
                    tmpDir.resolve("acctdata.txt"),
                    tmpDir.resolve("cardxref.txt"),
                    tmpDir.resolve("dailytran.txt"),
                    tmpDir.resolve("carddata.txt"),
                    output
            );

            assertEquals(0, stats.total());
            assertEquals(0, stats.customers());

            // Output should only have the header
            List<String> lines = Files.readAllLines(output);
            assertEquals(1, lines.size());
        }
    }

    // ---------------------------------------------------------------
    // Integration test with real sample data
    // ---------------------------------------------------------------

    @Nested
    class IntegrationTest {

        private static final Path DATA_DIR =
                Path.of("../../app/data/ASCII");

        @Test
        void integrationWithRealData(@TempDir Path tmpDir) throws IOException {
            Path custFile = DATA_DIR.resolve("custdata.txt");
            Path acctFile = DATA_DIR.resolve("acctdata.txt");
            Path xrefFile = DATA_DIR.resolve("cardxref.txt");
            Path tranFile = DATA_DIR.resolve("dailytran.txt");
            Path cardFile = DATA_DIR.resolve("carddata.txt");

            // Skip if data files are not available
            if (!Files.exists(custFile)) {
                System.out.println("Skipping integration test — data files not found at "
                        + DATA_DIR.toAbsolutePath());
                return;
            }

            Path output = tmpDir.resolve("export.csv");
            ExportService service = new ExportService();
            ExportService.ExportStatistics stats = service.runExport(
                    custFile, acctFile, xrefFile, tranFile, cardFile, output
            );

            // Known data counts: 50 cust, 50 acct, 50 xref (based on wc -l)
            assertEquals(50, stats.customers());
            assertEquals(50, stats.accounts());
            assertEquals(50, stats.xrefs());
            assertEquals(300, stats.transactions());
            assertEquals(50, stats.cards());
            assertEquals(500, stats.total());

            // Verify output file
            List<String> lines = Files.readAllLines(output);
            assertEquals(501, lines.size()); // header + 500 data records

            // Verify first data line is customer record
            String firstData = lines.get(1);
            assertTrue(firstData.startsWith("C|"));

            // Verify record type ordering: C, A, X, T, D
            assertTrue(lines.get(1).startsWith("C|"));
            assertTrue(lines.get(51).startsWith("A|"));
            assertTrue(lines.get(101).startsWith("X|"));
            assertTrue(lines.get(151).startsWith("T|"));
            assertTrue(lines.get(451).startsWith("D|"));

            // Verify sequence numbers are correct
            assertEquals("1", lines.get(1).split("\\|")[2]);
            assertEquals("500", lines.get(500).split("\\|")[2]);

            // All records should have branch=0001 and region=NORTH
            for (int i = 1; i <= 500; i++) {
                String[] parts = lines.get(i).split("\\|");
                assertEquals("0001", parts[3], "Record " + i + " should have branchId=0001");
                assertEquals("NORTH", parts[4], "Record " + i + " should have regionCode=NORTH");
            }
        }

        @Test
        void customerRecordFieldsFromRealData() throws IOException {
            Path custFile = DATA_DIR.resolve("custdata.txt");
            if (!Files.exists(custFile)) return;

            List<CustomerRecord> records = CustomerFileReader.readAll(custFile);
            assertFalse(records.isEmpty());

            CustomerRecord first = records.get(0);
            assertEquals(1, first.custId());
            assertEquals("Immanuel", first.firstName());
            assertEquals("Kessler", first.lastName());
            assertEquals("NC", first.stateCode());
            assertEquals("USA", first.countryCode());
            assertEquals(3, first.addressLines().size());
            assertEquals(2, first.phoneNumbers().size());
        }

        @Test
        void accountRecordFieldsFromRealData() throws IOException {
            Path acctFile = DATA_DIR.resolve("acctdata.txt");
            if (!Files.exists(acctFile)) return;

            List<AccountRecord> records = AccountFileReader.readAll(acctFile);
            assertFalse(records.isEmpty());

            AccountRecord first = records.get(0);
            assertEquals("00000000001", first.acctId());
            assertEquals("Y", first.activeStatus());
            assertEquals(0, new BigDecimal("194.00").compareTo(first.currentBalance()));
            assertEquals(0, new BigDecimal("2020.00").compareTo(first.creditLimit()));
            assertEquals(0, new BigDecimal("1020.00").compareTo(first.cashCreditLimit()));
            assertEquals("2014-11-20", first.openDate());
        }

        @Test
        void cardRecordFieldsFromRealData() throws IOException {
            Path cardFile = DATA_DIR.resolve("carddata.txt");
            if (!Files.exists(cardFile)) return;

            List<CardRecord> records = CardFileReader.readAll(cardFile);
            assertFalse(records.isEmpty());

            CardRecord first = records.get(0);
            assertEquals("0500024453765740", first.cardNum());
            assertEquals("00000000050", first.acctId());
            assertEquals(747, first.cvvCode());
            assertEquals("Aniya Von", first.embossedName());
            assertEquals("2023-03-09", first.expirationDate());
            assertEquals("Y", first.activeStatus());
        }

        @Test
        void xrefRecordFieldsFromRealData() throws IOException {
            Path xrefFile = DATA_DIR.resolve("cardxref.txt");
            if (!Files.exists(xrefFile)) return;

            List<CardXrefRecord> records = CardXrefFileReader.readAll(xrefFile);
            assertFalse(records.isEmpty());

            CardXrefRecord first = records.get(0);
            assertEquals("0500024453765740", first.cardNum());
            assertEquals(50, first.custId());
        }

        @Test
        void transactionRecordFieldsFromRealData() throws IOException {
            Path tranFile = DATA_DIR.resolve("dailytran.txt");
            if (!Files.exists(tranFile)) return;

            List<TransactionRecord> records = TransactionFileReader.readAll(tranFile);
            assertFalse(records.isEmpty());
            assertEquals(300, records.size());

            TransactionRecord first = records.get(0);
            assertFalse(first.tranId().isBlank());
            assertFalse(first.typeCode().isBlank());
        }
    }

    // ---------------------------------------------------------------
    // Edge Cases
    // ---------------------------------------------------------------

    @Nested
    class EdgeCaseTest {

        @Test
        void shortLinePaddedCorrectly() {
            // A customer line shorter than 500 chars
            String shortLine = "000000099ShortName";
            CustomerRecord rec = CustomerFileReader.parseLine(shortLine);
            assertEquals(99, rec.custId());
            assertEquals("ShortName", rec.firstName());
            assertEquals("", rec.middleName()); // padded spaces → trimmed to empty
        }

        @Test
        void blankLinesSkipped(@TempDir Path tmpDir) throws IOException {
            Path file = tmpDir.resolve("test.txt");
            String validLine = padRight("0500024453765740" + "00000000050" + "747"
                    + padRight("Test Name", 50) + "2023-03-09" + "Y", 150);
            Files.writeString(file, "\n\n" + validLine + "\n\n");

            List<CardRecord> records = CardFileReader.readAll(file);
            assertEquals(1, records.size());
        }

        @Test
        void largeMonetaryValues() {
            // S9(10)V99: max = 9999999999.99
            BigDecimal result = CobolFieldParser.parseZonedDecimal("999999999I", 2);
            assertEquals(0, new BigDecimal("99999999.99").compareTo(result));
        }

        @Test
        void negativeMonetaryValues() {
            // Negative balance
            BigDecimal result = CobolFieldParser.parseZonedDecimal("00000001940J", 2);
            assertEquals(0, new BigDecimal("-194.01").compareTo(result));
            assertTrue(result.signum() < 0);
        }

        @Test
        void exportRecordSealedInterface() {
            // Verify all record types implement the sealed interface
            ExportRecord rec = new ExportRecord.CustomerExport(
                    "ts", 1, "0001", "NORTH",
                    1, "F", "M", "L",
                    List.of("A1", "A2", "A3"), "ST", "CO", "ZIP",
                    List.of("P1", "P2"), 0, "GID", "DOB",
                    "EFT", "Y", 700
            );
            assertInstanceOf(ExportRecord.class, rec);
            assertInstanceOf(ExportRecord.CustomerExport.class, rec);
        }
    }

    // ---------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------

    private static String padRight(String s, int width) {
        if (s.length() >= width) return s.substring(0, width);
        return s + " ".repeat(width - s.length());
    }

    private static void writeMinimalCustomerFile(Path path, int count) throws IOException {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= count; i++) {
            sb.append(padRight(String.format("%09d", i) + padRight("First" + i, 25)
                    + padRight("Mid" + i, 25) + padRight("Last" + i, 25)
                    + " ".repeat(150) + "NY" + "USA" + " ".repeat(10)
                    + " ".repeat(30) + String.format("%09d", i)
                    + " ".repeat(20) + "2000-01-01" + " ".repeat(10)
                    + "Y" + "700", 500));
            sb.append('\n');
        }
        Files.writeString(path, sb.toString());
    }

    private static void writeMinimalAccountFile(Path path, int count) throws IOException {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= count; i++) {
            sb.append(padRight(String.format("%011d", i) + "Y"
                    + "00000001000{" + "00000050000{" + "00000025000{"
                    + "2020-01-01" + "2025-12-31" + "2024-06-01"
                    + "00000000000{" + "00000000000{"
                    + "10001     " + "GRP001    ", 300));
            sb.append('\n');
        }
        Files.writeString(path, sb.toString());
    }

    private static void writeMinimalXrefFile(Path path, int count) throws IOException {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= count; i++) {
            sb.append(padRight(String.format("%-16s", "CARD" + i)
                    + String.format("%09d", i)
                    + String.format("%011d", i), 50));
            sb.append('\n');
        }
        Files.writeString(path, sb.toString());
    }

    private static void writeMinimalTransactionFile(Path path, int count) throws IOException {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= count; i++) {
            sb.append(padRight(String.format("%-16s", "TRAN" + i) + "01" + "0001"
                    + padRight("POS", 10) + padRight("Purchase " + i, 100)
                    + "0000000100{" + String.format("%09d", i)
                    + padRight("Merchant" + i, 50) + padRight("City" + i, 50)
                    + padRight("12345", 10) + String.format("%-16s", "CARD" + i)
                    + padRight("2022-01-01 00:00:00.000000", 26)
                    + padRight("", 26), 350));
            sb.append('\n');
        }
        Files.writeString(path, sb.toString());
    }

    private static void writeMinimalCardFile(Path path, int count) throws IOException {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= count; i++) {
            sb.append(padRight(String.format("%-16s", "CARD" + i)
                    + String.format("%011d", i)
                    + String.format("%03d", i)
                    + padRight("Name " + i, 50)
                    + "2025-12-31" + "Y", 150));
            sb.append('\n');
        }
        Files.writeString(path, sb.toString());
    }
}
