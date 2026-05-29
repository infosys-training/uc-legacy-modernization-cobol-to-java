package com.carddemo.golden.parser;

import java.util.Collections;
import java.util.List;

public class CobolRecordLayout {

    private final String recordName;
    private final List<CobolFieldDefinition> fields;

    public CobolRecordLayout(String recordName, List<CobolFieldDefinition> fields) {
        this.recordName = recordName;
        this.fields = Collections.unmodifiableList(fields);
    }

    public String getRecordName() {
        return recordName;
    }

    public List<CobolFieldDefinition> getFields() {
        return fields;
    }

    public int getTotalLength() {
        return fields.stream().mapToInt(CobolFieldDefinition::getLength).sum();
    }
}
