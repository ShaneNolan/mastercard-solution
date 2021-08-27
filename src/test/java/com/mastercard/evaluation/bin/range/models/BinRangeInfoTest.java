package com.mastercard.evaluation.bin.range.models;

import com.github.javafaker.Faker;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThat;

public class BinRangeInfoTest {

    private final Faker faker = new Faker();

    @Test
    public void shouldTestForEquality() {
        BinRangeInfo binRangeInfo = new BinRangeInfo();
        binRangeInfo.setRef(UUID.fromString("2A480C8A-83CA-4BB7-95B7-F19CEC97B3FD"));
        binRangeInfo.setStart(new BigDecimal("4263000000000000"));
        binRangeInfo.setEnd(new BigDecimal("4263999999999999"));
        binRangeInfo.setBankName("SOME_BANK");
        binRangeInfo.setCurrencyCode("EUR");

        BinRangeInfo secondBinRangeInfo = new BinRangeInfo();
        secondBinRangeInfo.setRef(UUID.fromString("2A480C8A-83CA-4BB7-95B7-F19CEC97B3FD"));
        secondBinRangeInfo.setStart(new BigDecimal("4263000000000000"));
        secondBinRangeInfo.setEnd(new BigDecimal("4263999999999999"));
        secondBinRangeInfo.setBankName("SOME_BANK");
        secondBinRangeInfo.setCurrencyCode("EUR");

        assertEquals(binRangeInfo, secondBinRangeInfo);
    }

    @Test
    public void shouldTestForInEquality() {
        BinRangeInfo binRangeInfo = new BinRangeInfo();
        binRangeInfo.setRef(UUID.fromString("2A480C8A-83CA-4BB7-95B7-F19CEC97B3FD"));
        binRangeInfo.setStart(new BigDecimal("4263000000000000"));
        binRangeInfo.setEnd(new BigDecimal("4263999999999999"));
        binRangeInfo.setBankName(faker.company().name());
        binRangeInfo.setCurrencyCode(faker.currency().code());

        BinRangeInfo secondBinRangeInfo = new BinRangeInfo();
        secondBinRangeInfo.setRef(UUID.fromString("2A480C8A-83CA-4BB7-95B7-F19CEC97B3FD"));
        secondBinRangeInfo.setStart(new BigDecimal("4263000000000000"));
        secondBinRangeInfo.setEnd(new BigDecimal("4263999999999999"));
        secondBinRangeInfo.setBankName(faker.company().name());
        secondBinRangeInfo.setCurrencyCode(faker.currency().code());

        assertNotEquals(binRangeInfo, secondBinRangeInfo);
    }

    @Test
    public void shouldImplementHashCode() {
        BinRangeInfo binRangeInfo = new BinRangeInfo();
        binRangeInfo.setRef(UUID.fromString("2A480C8A-83CA-4BB7-95B7-F19CEC97B3FD"));
        binRangeInfo.setStart(new BigDecimal("4263000000000000"));
        binRangeInfo.setEnd(new BigDecimal("4263999999999999"));
        binRangeInfo.setBankName("SOME_BANK");
        binRangeInfo.setCurrencyCode("EUR");

        assertThat(binRangeInfo.hashCode(), is(1260523007));
    }
}