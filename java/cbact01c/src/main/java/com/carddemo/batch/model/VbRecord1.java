package com.carddemo.batch.model;

/**
 * Short variable-length record (12 bytes) written to the VBR file.
 * Contains only the account ID and active status.
 */
public record VbRecord1(long acctId, char activeStatus) {}
