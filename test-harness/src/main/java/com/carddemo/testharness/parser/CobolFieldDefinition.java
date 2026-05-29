package com.carddemo.testharness.parser;

public class CobolFieldDefinition {

    public enum PicType {
        ALPHANUMERIC,
        NUMERIC,
        SIGNED_DECIMAL
    }

    private final String name;
    private final PicType picType;
    private final int offset;
    private final int length;
    private final int decimals;
    private final boolean signed;
    private final boolean filler;

    public CobolFieldDefinition(String name, PicType picType, int offset, int length,
                                int decimals, boolean signed, boolean filler) {
        this.name = name;
        this.picType = picType;
        this.offset = offset;
        this.length = length;
        this.decimals = decimals;
        this.signed = signed;
        this.filler = filler;
    }

    public static CobolFieldDefinition alphanumeric(String name, int offset, int length) {
        return new CobolFieldDefinition(name, PicType.ALPHANUMERIC, offset, length, 0, false, false);
    }

    public static CobolFieldDefinition numeric(String name, int offset, int length) {
        return new CobolFieldDefinition(name, PicType.NUMERIC, offset, length, 0, false, false);
    }

    public static CobolFieldDefinition signedDecimal(String name, int offset, int length, int decimals) {
        return new CobolFieldDefinition(name, PicType.SIGNED_DECIMAL, offset, length, decimals, true, false);
    }

    public static CobolFieldDefinition filler(int offset, int length) {
        return new CobolFieldDefinition("FILLER", PicType.ALPHANUMERIC, offset, length, 0, false, true);
    }

    public String getName() { return name; }
    public String getJsonName() { return name.replace('-', '_'); }
    public PicType getPicType() { return picType; }
    public int getOffset() { return offset; }
    public int getLength() { return length; }
    public int getDecimals() { return decimals; }
    public boolean isSigned() { return signed; }
    public boolean isFiller() { return filler; }
}
