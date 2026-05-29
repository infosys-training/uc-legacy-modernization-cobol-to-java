package com.carddemo.golden;

import com.carddemo.parser.CobolRecordLayout;
import com.carddemo.parser.CobolRecordParser;
import com.carddemo.parser.RecordLayouts;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class GoldenFileGenerator {

    private static final Path DATA_DIR = Path.of("../app/data/ASCII");
    private static final Path OUTPUT_DIR = Path.of("output");

    private final ObjectMapper mapper;

    public GoldenFileGenerator() {
        this.mapper = new ObjectMapper();
        this.mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public static void main(String[] args) throws IOException {
        GoldenFileGenerator generator = new GoldenFileGenerator();
        Files.createDirectories(OUTPUT_DIR);

        generator.generate("acctdata.txt", "CVACT01Y.cpy", RecordLayouts.accountRecord());
        generator.generate("carddata.txt", "CVACT02Y.cpy", RecordLayouts.cardRecord());
        generator.generate("cardxref.txt", "CVACT03Y.cpy", RecordLayouts.cardXrefRecord());
        generator.generate("custdata.txt", "CVCUS01Y.cpy", RecordLayouts.customerRecord());
        generator.generate("dailytran.txt", "CVTRA06Y.cpy", RecordLayouts.dailyTransactionRecord());
        generator.generate("discgrp.txt", "CVTRA02Y.cpy", RecordLayouts.disclosureGroupRecord());
        generator.generate("tcatbal.txt", "CVTRA01Y.cpy", RecordLayouts.tranCatBalRecord());
        generator.generate("trancatg.txt", "CVTRA04Y.cpy", RecordLayouts.tranCatRecord());
        generator.generate("trantype.txt", "CVTRA03Y.cpy", RecordLayouts.tranTypeRecord());

        System.out.println("Golden file generation complete.");
    }

    private void generate(String dataFileName, String copybookName, CobolRecordLayout layout)
            throws IOException {
        Path dataFile = DATA_DIR.resolve(dataFileName);
        List<String> lines = Files.readAllLines(dataFile);

        CobolRecordParser parser = new CobolRecordParser(layout);
        List<Map<String, Object>> records = new ArrayList<>();

        for (String line : lines) {
            String cleaned = line.replaceAll("\\r", "");
            if (cleaned.isEmpty()) {
                continue;
            }
            records.add(parser.parse(cleaned));
        }

        Map<String, Object> output = new LinkedHashMap<>();
        output.put("sourceFile", dataFileName);
        output.put("copybook", copybookName);
        output.put("recordCount", records.size());
        output.put("records", records);

        String jsonFileName = dataFileName.replace(".txt", ".json");
        Path outputFile = OUTPUT_DIR.resolve(jsonFileName);
        mapper.writeValue(outputFile.toFile(), output);

        System.out.printf("Generated %s: %d records%n", jsonFileName, records.size());
    }
}
