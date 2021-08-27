package com.mastercard.evaluation.bin.range.models;

import org.junit.Test;

import java.math.BigDecimal;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThat;

public class BinRangeTest {

    @Test
    public void shouldTestForEquality() {
        BinRange binRange = new BinRange("4263000000000000");
        BinRange secondBinRange = new BinRange("4263000000000000");

        assertEquals(binRange, secondBinRange);
    }

    @Test
    public void shouldTestForInEquality() {
        BinRange binRange = new BinRange("4263000000000000");
        BinRange secondBinRange = new BinRange("4263000000000001");

        assertNotEquals(binRange, secondBinRange);
    }

    @Test
    public void shouldImplementHashCode() {
        BinRange binRange = new BinRange("4263000000000000");

        assertThat(binRange.hashCode(), is(-1151090335));
    }

    @Test
    public void shouldCompare() {
        BigDecimal startRange = new BigDecimal("4263000000000000");
        BigDecimal endRange = new BigDecimal("4263999999999999");

        BinRange binRange = new BinRange(startRange, endRange);
        BinRange panWithinRange = new BinRange("4263000000000001");

        assertThat(binRange.compareTo(panWithinRange), is(0));
    }

    @Test
    public void shouldCompareLessThan() {
        BigDecimal startRange = new BigDecimal("4263000000000000");
        BigDecimal endRange = new BigDecimal("4263999999999999");

        BinRange binRange = new BinRange(startRange, endRange);
        BinRange panBelowRange = new BinRange("4262999999999999");

        assertThat(binRange.compareTo(panBelowRange), is(-1));
    }

    @Test
    public void shouldCompareGreaterThan() {
        BigDecimal startRange = new BigDecimal("4263000000000000");
        BigDecimal endRange = new BigDecimal("4263999999999999");

        BinRange binRange = new BinRange(startRange, endRange);
        BinRange panBelowRange = new BinRange("4264000000000000");

        assertThat(binRange.compareTo(panBelowRange), is(1));
    }
}