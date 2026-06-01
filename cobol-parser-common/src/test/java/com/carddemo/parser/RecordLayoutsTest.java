package com.carddemo.parser;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RecordLayoutsTest {

    @Test
    void accountRecordLength() {
        assertThat(RecordLayouts.accountRecord().getTotalLength()).isEqualTo(300);
    }

    @Test
    void cardRecordLength() {
        assertThat(RecordLayouts.cardRecord().getTotalLength()).isEqualTo(150);
    }

    @Test
    void cardXrefRecordLength() {
        assertThat(RecordLayouts.cardXrefRecord().getTotalLength()).isEqualTo(50);
    }

    @Test
    void customerRecordLength() {
        assertThat(RecordLayouts.customerRecord().getTotalLength()).isEqualTo(500);
    }

    @Test
    void dailyTransactionRecordLength() {
        assertThat(RecordLayouts.dailyTransactionRecord().getTotalLength()).isEqualTo(350);
    }

    @Test
    void disclosureGroupRecordLength() {
        assertThat(RecordLayouts.disclosureGroupRecord().getTotalLength()).isEqualTo(50);
    }

    @Test
    void tranCatBalRecordLength() {
        assertThat(RecordLayouts.tranCatBalRecord().getTotalLength()).isEqualTo(50);
    }

    @Test
    void tranCatRecordLength() {
        assertThat(RecordLayouts.tranCatRecord().getTotalLength()).isEqualTo(60);
    }

    @Test
    void tranTypeRecordLength() {
        assertThat(RecordLayouts.tranTypeRecord().getTotalLength()).isEqualTo(60);
    }
}
