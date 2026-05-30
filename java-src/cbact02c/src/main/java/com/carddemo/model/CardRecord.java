package com.carddemo.model;

/**
 * Java record mapping of COBOL copybook CVACT02Y.cpy (CARD-RECORD, 150 bytes).
 *
 * <pre>
 * Field               PIC         Bytes  Position
 * CARD-NUM            X(16)       16     0-15
 * CARD-ACCT-ID        9(11)       11     16-26
 * CARD-CVV-CD         9(03)        3     27-29
 * CARD-EMBOSSED-NAME  X(50)       50     30-79
 * CARD-EXPIRAION-DATE X(10)       10     80-89
 * CARD-ACTIVE-STATUS  X(01)        1     90
 * FILLER              X(59)       59     91-149
 * </pre>
 */
public record CardRecord(
        String cardNum,
        long acctId,
        int cvvCode,
        String embossedName,
        String expirationDate,
        String activeStatus
) {

    public static final int RECORD_LENGTH = 150;

    public static CardRecord parse(String line) {
        if (line.length() < RECORD_LENGTH) {
            throw new IllegalArgumentException(
                    "Card record too short: expected " + RECORD_LENGTH
                            + " bytes, got " + line.length());
        }

        String cardNum = line.substring(0, 16).trim();
        long acctId = Long.parseLong(line.substring(16, 27).trim());
        int cvvCode = Integer.parseInt(line.substring(27, 30).trim());
        String embossedName = line.substring(30, 80).trim();
        String expirationDate = line.substring(80, 90).trim();
        String activeStatus = line.substring(90, 91);

        return new CardRecord(cardNum, acctId, cvvCode, embossedName,
                expirationDate, activeStatus);
    }

    /**
     * Formats the record as a 150-byte fixed-width string matching the COBOL
     * DISPLAY output (entire CARD-RECORD is DISPLAYed).
     */
    public String toDisplayString() {
        return String.format("%-16s%011d%03d%-50s%-10s%-1s%-59s",
                cardNum, acctId, cvvCode, embossedName,
                expirationDate, activeStatus, "");
    }
}
