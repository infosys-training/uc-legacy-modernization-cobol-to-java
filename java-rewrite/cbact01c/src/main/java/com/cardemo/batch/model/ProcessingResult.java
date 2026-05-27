package com.cardemo.batch.model;

/**
 * Aggregates all output records produced from processing a single account.
 */
public record ProcessingResult(
    OutAccountRecord outRecord,
    ArrayAccountRecord arrayRecord,
    VbrcRecord1 vbrcRecord1,
    VbrcRecord2 vbrcRecord2
) {}
