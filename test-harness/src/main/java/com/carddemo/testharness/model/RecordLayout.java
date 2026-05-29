package com.carddemo.testharness.model;

import com.carddemo.testharness.parser.CobolFieldDefinition;

import java.util.Collections;
import java.util.List;

public class RecordLayout {

    private final String name;
    private final List<CobolFieldDefinition> fields;
    private final int recordLength;

    public RecordLayout(String name, List<CobolFieldDefinition> fields, int recordLength) {
        this.name = name;
        this.fields = Collections.unmodifiableList(fields);
        this.recordLength = recordLength;
    }

    public String getName() { return name; }
    public List<CobolFieldDefinition> getFields() { return fields; }
    public int getRecordLength() { return recordLength; }
}
