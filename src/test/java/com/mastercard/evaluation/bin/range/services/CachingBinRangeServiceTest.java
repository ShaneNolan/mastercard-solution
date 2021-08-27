package com.mastercard.evaluation.bin.range.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import com.mastercard.evaluation.bin.range.events.EventManager;
import com.mastercard.evaluation.bin.range.models.BinRangeInfo;
import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

public class CachingBinRangeServiceTest {

    private final ObjectMapper objectMapper = spy(new ObjectMapper());
    private final EventManager eventManager = mock(EventManager.class);

    private CachingBinRangeService cachingBinRangeService;


    @Before
    public void setup() {
        cachingBinRangeService = new CachingBinRangeService(objectMapper, eventManager);

        cachingBinRangeService.refreshCache();
    }

    @Test
    public void shouldFindTheCorrectRangeForAGivenPan() {
        String panWithinSomeTestBankRange = "4263123412341234";

        Optional<BinRangeInfo> binRangeInfoOptional = cachingBinRangeService.findBinRangeInfoByPan(panWithinSomeTestBankRange);

        assertTrue(binRangeInfoOptional.isPresent());
        assertNotNull(binRangeInfoOptional.get());
        assertThat(binRangeInfoOptional.get().getRef(), is(UUID.fromString("2A480C8A-83CA-4BB7-95B7-F19CEC97B3FD")));
        assertThat(binRangeInfoOptional.get().getStart(), is(new BigDecimal("4263000000000000")));
        assertThat(binRangeInfoOptional.get().getEnd(), is(new BigDecimal("4263999999999999")));
        assertThat(binRangeInfoOptional.get().getBankName(), is("AIB"));
        assertThat(binRangeInfoOptional.get().getCurrencyCode(), is("EUR"));
    }

    @Test
    public void shouldFailToFindTheCorrectRangeForANonExistentPan() {
        String panWithinSomeTestBankRange = "6263123412341234";

        Optional<BinRangeInfo> binRangeInfoOptional = cachingBinRangeService.findBinRangeInfoByPan(panWithinSomeTestBankRange);

        assertFalse(binRangeInfoOptional.isPresent());
    }

    @Test
    public void shouldMaintainAValidCache() {
        String testBankOnePan = "4263000000000001";
        String testBankTwoPan = "4319000000000001";
        String testBankThreePan = "5432000000000001";
        String testBankFourPan = "5263000000000001";
        String testBankFivePan = "0000000000000001";

        assertTrue(cachingBinRangeService.findBinRangeInfoByPan(testBankOnePan).isPresent());
        assertTrue(cachingBinRangeService.findBinRangeInfoByPan(testBankTwoPan).isPresent());
        assertTrue(cachingBinRangeService.findBinRangeInfoByPan(testBankThreePan).isPresent());

        cachingBinRangeService.populateCache(getLatestBinRangeInfo());

        assertTrue(cachingBinRangeService.findBinRangeInfoByPan(testBankOnePan).isPresent());
        assertTrue(cachingBinRangeService.findBinRangeInfoByPan(testBankFourPan).isPresent());
        assertTrue(cachingBinRangeService.findBinRangeInfoByPan(testBankTwoPan).isPresent());
        assertTrue(cachingBinRangeService.findBinRangeInfoByPan(testBankThreePan).isPresent());

        assertFalse(cachingBinRangeService.findBinRangeInfoByPan(testBankFivePan).isPresent());
    }

    private List<BinRangeInfo> getLatestBinRangeInfo(){
        Faker faker = new Faker();

        BinRangeInfo updatedBinRangeInfo = new BinRangeInfo();
        updatedBinRangeInfo.setRef(UUID.fromString("2A480C8A-83CA-4BB7-95B7-F19CEC97B3FD"));
        updatedBinRangeInfo.setStart(new BigDecimal("4263000000000000"));
        updatedBinRangeInfo.setEnd(new BigDecimal("4263999999999999"));
        updatedBinRangeInfo.setBankName(faker.company().name());
        updatedBinRangeInfo.setCurrencyCode(faker.currency().code());

        BinRangeInfo newBinRangeInfo = new BinRangeInfo();
        newBinRangeInfo.setRef(UUID.fromString("9651794E-8166-423F-8B8D-EE235A04DDB7"));
        newBinRangeInfo.setStart(new BigDecimal("5263000000000000"));
            newBinRangeInfo.setEnd(new BigDecimal("5263999999999999"));
        newBinRangeInfo.setBankName(faker.company().name());
        newBinRangeInfo.setCurrencyCode(faker.currency().code());

        return Lists.newArrayList(updatedBinRangeInfo, newBinRangeInfo);
    }

    private BinRangeInfo validBinRangeInfo(){
        // This BinRangeInfo exists in the cache thats loaded the bin-range-info.json file.
        // It could be reimplemented to create a random BinRangeInfo from the JSON file.

        return new BinRangeInfo(
                UUID.fromString("2A480C8A-83CA-4BB7-95B7-F19CEC97B3FD"),
                new BigDecimal("4263000000000000"),
                new BigDecimal("4263999999999999"),
                "AIB",
                "EUR"
        );
    }

    @Test
    public void testCheckBinRangeExists(){
        BinRangeInfo fakeBinRangeInfo = new BinRangeInfo();
        fakeBinRangeInfo.generateFakeInfo();

        BinRangeInfo validBinRangeInfo = validBinRangeInfo();

        assertFalse(cachingBinRangeService.checkBinRangeExists(fakeBinRangeInfo).isPresent());
        assertTrue(cachingBinRangeService.checkBinRangeExists(validBinRangeInfo).isPresent());
    }

    @Test
    public void testAddBinRangeInfo(){
        BinRangeInfo fakeBinRangeInfo = new BinRangeInfo();
        fakeBinRangeInfo.generateFakeInfo();

        assertFalse(cachingBinRangeService.findBinRangeInfoByRef(fakeBinRangeInfo.getRef()).isPresent());

        cachingBinRangeService.addBinRangeInfo(fakeBinRangeInfo);

        assertTrue(cachingBinRangeService.findBinRangeInfoByRef(fakeBinRangeInfo.getRef()).isPresent());

    }

    @Test
    public void testAddSubBinRangeInfo(){
        BinRangeInfo fakeSubBinRangeInfo = new BinRangeInfo();
        fakeSubBinRangeInfo.generateFakeInfo();
        fakeSubBinRangeInfo.setStart(new BigDecimal("4263500000000000"));
        fakeSubBinRangeInfo.setEnd(new BigDecimal("4263600000000000"));

        BinRangeInfo savedSubBinRangeInfo = cachingBinRangeService.addBinRangeInfo(fakeSubBinRangeInfo);
        assertEquals(cachingBinRangeService.checkBinRangeExists(fakeSubBinRangeInfo).get(), savedSubBinRangeInfo.getRef());
    }

    @Test
    public void testFindBinRangeInfoByRef(){
        BinRangeInfo fakeBinRangeInfo = new BinRangeInfo();
        fakeBinRangeInfo.generateFakeInfo();
        BinRangeInfo validBinRangeInfo = validBinRangeInfo();

        assertFalse(cachingBinRangeService.findBinRangeInfoByRef(fakeBinRangeInfo.getRef()).isPresent());
        assertTrue(cachingBinRangeService.findBinRangeInfoByRef(validBinRangeInfo.getRef()).isPresent());
    }

    @Test
    public void testUpdateBinRangeInfo(){
        String tempBankName = "test";
        BinRangeInfo validBinRangeInfo = validBinRangeInfo();

        assertNotEquals(cachingBinRangeService.findBinRangeInfoByRef(validBinRangeInfo.getRef()).get().getBankName(), tempBankName);

        validBinRangeInfo.setBankName(tempBankName);
        cachingBinRangeService.updateBinRangeInfo(validBinRangeInfo);

        assertEquals(cachingBinRangeService.findBinRangeInfoByRef(validBinRangeInfo.getRef()).get().getBankName(), tempBankName);
    }

    @Test
    public void testDeleteBinRangeInfoByRef(){
        BinRangeInfo fakeBinRangeInfo = new BinRangeInfo();
        fakeBinRangeInfo.generateFakeInfo();

        cachingBinRangeService.addBinRangeInfo(fakeBinRangeInfo);

        assertTrue(cachingBinRangeService.findBinRangeInfoByRef(fakeBinRangeInfo.getRef()).isPresent());

        cachingBinRangeService.deleteBinRangeInfoByRef(fakeBinRangeInfo);

        assertFalse(cachingBinRangeService.findBinRangeInfoByRef(fakeBinRangeInfo.getRef()).isPresent());
    }
}