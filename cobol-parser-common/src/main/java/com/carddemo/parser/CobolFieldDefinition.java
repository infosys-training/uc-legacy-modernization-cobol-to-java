package com.carddemo.parser;

public class CobolFieldDefinition {

    public enum FieldType {
        ALPHANUMERIC,
        NUMERIC,
        SIGNED_NUMERIC
    }

    private final String name;
    private final FieldType type;
    private final int length;
    private final int decimals;
    private final boolean filler;

    private CobolFieldDefinition(String name, FieldType type, int length, int decimals, boolean filler) {
        this.name = name;
        this.type = type;
        this.length = length;
        this.decimals = decimals;
        this.filler = filler;
    }

    public static CobolFieldDefinition alphanumeric(String name, int length) {
        return new CobolFieldDefinition(name, FieldType.ALPHANUMERIC, length, 0, false);
    }

    public static CobolFieldDefinition numeric(String name, int length) {
        return new CobolFieldDefinition(name, FieldType.NUMERIC, length, 0, false);
    }

    public static CobolFieldDefinition signedNumeric(String name, int integerDigits, int decimalDigits) {
        return new CobolFieldDefinition(name, FieldType.SIGNED_NUMERIC,
                integerDigits + decimalDigits, decimalDigits, false);
    }

    public static CobolFieldDefinition filler(int length) {
        return new CobolFieldDefinition("FILLER", FieldType.ALPHANUMERIC, length, 0, true);
    }

    public String getName() {
        return name;
    }

    public FieldType getType() {
        return type;
    }

    public int getLength() {
        return length;
    }

    public int getDecimals() {
        return decimals;
    }

    public boolean isFiller() {
        return filler;
    }

    public String getJsonName() {
        return name.replace('-', '_');
    }
}
