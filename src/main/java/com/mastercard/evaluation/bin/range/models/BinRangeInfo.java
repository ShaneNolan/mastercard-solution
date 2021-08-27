package com.mastercard.evaluation.bin.range.models;

import com.github.javafaker.Faker;
import com.google.common.annotations.VisibleForTesting;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

public class BinRangeInfo {
    private UUID ref;
    @NotNull
    private BigDecimal start;
    @NotNull
    private BigDecimal end;
    @NotNull
    private String bankName;
    @NotNull
    private String currencyCode;

    public BinRangeInfo() {
        // Will throw a null pointer exception if values arent provided.
        // Could call generateFakeInfo here but that would reduce readability.
    }

    public BinRangeInfo(UUID ref, BigDecimal start, BigDecimal end, String bankName, String currencyCode) {
        this.ref = ref;
        this.start = start;
        this.end = end;
        this.bankName = bankName;
        this.currencyCode = currencyCode;
    }

    public UUID getRef() {
        return ref;
    }

    public void setRef(UUID ref) {
        this.ref = ref;
    }

    public BigDecimal getStart() {
        return start;
    }

    public void setStart(BigDecimal start) {
        this.start = start;
    }

    public BigDecimal getEnd() {
        return end;
    }

    public void setEnd(BigDecimal end) {
        this.end = end;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    @VisibleForTesting
    public void generateFakeInfo(){
        Faker faker = new Faker();
        String randomBin = Integer.toString(1000 + new Random().nextInt(9999));

        ref = UUID.randomUUID();
        start= new BigDecimal(randomBin + "000000000000");
        end = new BigDecimal(randomBin + "999999999999");
        bankName = faker.company().name();
        currencyCode = faker.currency().code();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BinRangeInfo that = (BinRangeInfo) o;
        return Objects.equals(ref, that.ref) &&
                Objects.equals(start, that.start) &&
                Objects.equals(end, that.end) &&
                Objects.equals(bankName, that.bankName) &&
                Objects.equals(currencyCode, that.currencyCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ref, start, end, bankName, currencyCode);
    }

    @Override
    public String toString() {
        return "BinRangeInfo: ref=" + ref +
                ", start=" + start.toString() +
                ", end=" + end.toString() +
                ", bankName=" + bankName +
                ", currencyCode=" + currencyCode;
    }
}
