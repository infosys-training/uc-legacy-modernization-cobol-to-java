package com.cardemo.batch.cbact02c.model;

/**
 * Java record mapping the CVACT02Y copybook CARD-RECORD layout (150 bytes).
 *
 * <pre>
 *   Offset  Length  Field
 *   0       16      CARD-NUM             PIC X(16)
 *   16      11      CARD-ACCT-ID         PIC 9(11)
 *   27       3      CARD-CVV-CD          PIC 9(03)
 *   30      50      CARD-EMBOSSED-NAME   PIC X(50)
 *   80      10      CARD-EXPIRAION-DATE  PIC X(10)
 *   90       1      CARD-ACTIVE-STATUS   PIC X(01)
 *   91      59      FILLER               PIC X(59)
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

    @Override
    public String toString() {
        return String.format(
                "CardRecord[cardNum=%s, acctId=%011d, cvv=%03d, name=%-50s, expires=%s, active=%s]",
                cardNum, acctId, cvvCode, embossedName, expirationDate, activeStatus);
    }
}
