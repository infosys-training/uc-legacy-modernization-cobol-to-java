package com.cardemo.batch.cbexport.model;

import java.util.List;

/**
 * Maps to CVCUS01Y.cpy — Customer record (RECLN 500).
 */
public record CustomerRecord(
        int custId,
        String firstName,
        String middleName,
        String lastName,
        List<String> addressLines,
        String stateCode,
        String countryCode,
        String zipCode,
        List<String> phoneNumbers,
        int ssn,
        String govtIssuedId,
        String dateOfBirth,
        String eftAccountId,
        String primaryCardHolderInd,
        int ficoCreditScore
) {}
