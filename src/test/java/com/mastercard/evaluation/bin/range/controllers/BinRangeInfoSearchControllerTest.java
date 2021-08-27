package com.mastercard.evaluation.bin.range.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mastercard.evaluation.bin.range.models.BinRangeInfo;
import com.mastercard.evaluation.bin.range.services.BinRangeService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BinRangeInfoSearchControllerTest extends BaseControllerTest {

    private static final String TEST_PAN = "4263123412341234";

    private BinRangeService binRangeService = mock(BinRangeService.class);
    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private BinRangeInfoSearchController binRangeInfoSearchController;

    @Before
    public void setUp() {
        when(binRangeService.findBinRangeInfoByPan(TEST_PAN)).thenReturn(mockBinRangeDetails());

        ReflectionTestUtils.setField(binRangeInfoSearchController, "binRangeService", binRangeService);
    }

    @Test
    public void test() throws Exception {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/binRangeInfoSearch/{pan}", TEST_PAN);

        MockHttpServletResponse response = getMockMvc().perform(builder).andReturn().getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());

        BinRangeInfo binRangeInfo = objectMapper.readValue(response.getContentAsString(), BinRangeInfo.class);

        assertNotNull(binRangeInfo);
        assertEquals(new BigDecimal("4263000000000000"), binRangeInfo.getStart());
        assertEquals(new BigDecimal("4263999999999999"), binRangeInfo.getEnd());
        assertEquals("SOME_TEST_BANK", binRangeInfo.getBankName());
        assertEquals("GBP", binRangeInfo.getCurrencyCode());
    }

    private Optional<BinRangeInfo> mockBinRangeDetails() {
        BinRangeInfo binRangeInfo = new BinRangeInfo();

        binRangeInfo.setStart(new BigDecimal("4263000000000000"));
        binRangeInfo.setEnd(new BigDecimal("4263999999999999"));
        binRangeInfo.setBankName("SOME_TEST_BANK");
        binRangeInfo.setCurrencyCode("GBP");

        return Optional.of(binRangeInfo);
    }
}