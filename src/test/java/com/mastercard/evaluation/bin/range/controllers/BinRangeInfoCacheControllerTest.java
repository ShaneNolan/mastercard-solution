package com.mastercard.evaluation.bin.range.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mastercard.evaluation.bin.range.models.BinRangeInfo;
import com.mastercard.evaluation.bin.range.services.BinRangeService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class BinRangeInfoCacheControllerTest extends BaseControllerTest {

    private BinRangeService binRangeService = mock(BinRangeService.class);
    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private BinRangeInfoCacheController binRangeInfoCacheController;

    @Before
    public void setUp() {
        ReflectionTestUtils.setField(binRangeInfoCacheController, "cachingBinRangeService", binRangeService);
    }

    private String asJsonString(final Object obj) {
        try {
            final String jsonContent = objectMapper.writeValueAsString(obj);
            return jsonContent;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testCreateBinRangeInfo() throws Exception {
        BinRangeInfo fakeBinRangeInfo = new BinRangeInfo();
        fakeBinRangeInfo.generateFakeInfo();

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/binRangeInfoCache")
                .content(asJsonString(fakeBinRangeInfo))
                .contentType(MediaType.APPLICATION_JSON);

        MockHttpServletResponse response = getMockMvc().perform(builder).andReturn().getResponse();
        assertEquals(HttpStatus.CREATED.value(), response.getStatus());

        when(binRangeService.checkBinRangeExists(fakeBinRangeInfo)).thenReturn(Optional.ofNullable(fakeBinRangeInfo.getRef()));
        MockHttpServletResponse conflictResponse = getMockMvc().perform(builder).andReturn().getResponse();
        assertEquals(HttpStatus.CONFLICT.value(), conflictResponse.getStatus());
    }

    @Test
    public void testUpdateBinRangeInfoByRefInvalidRefValuesException() throws Exception {
        BinRangeInfo fakeBinRangeInfo = new BinRangeInfo();
        fakeBinRangeInfo.generateFakeInfo();

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .put("/binRangeInfoCache/{ref}", UUID.randomUUID())
                .content(asJsonString(fakeBinRangeInfo))
                .contentType(MediaType.APPLICATION_JSON);

        MockHttpServletResponse response = getMockMvc().perform(builder).andReturn().getResponse();
        assertEquals(HttpStatus.CONFLICT.value(), response.getStatus());
    }

    @Test
    public void testUpdateBinRangeInfoByRefNotFoundException() throws Exception {
        BinRangeInfo fakeBinRangeInfo = new BinRangeInfo();
        fakeBinRangeInfo.generateFakeInfo();

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .put("/binRangeInfoCache/{ref}", fakeBinRangeInfo.getRef())
                .content(asJsonString(fakeBinRangeInfo))
                .contentType(MediaType.APPLICATION_JSON);

        when(binRangeService.findBinRangeInfoByRef(fakeBinRangeInfo.getRef())).thenReturn(Optional.ofNullable(null));
        MockHttpServletResponse response = getMockMvc().perform(builder).andReturn().getResponse();
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }

    private void helperUpdateBinRangeByRef(BinRangeInfo fakeBinRangeInfo, MockHttpServletRequestBuilder builder) throws Exception {
        when(binRangeService.findBinRangeInfoByRef(fakeBinRangeInfo.getRef())).thenReturn(Optional.ofNullable(fakeBinRangeInfo));
        MockHttpServletResponse response = getMockMvc().perform(builder).andReturn().getResponse();
        assertEquals(HttpStatus.CONFLICT.value(), response.getStatus());
        assertEquals(response.getContentAsString(), "Bin Ranges cannot be modified.");
    }

    @Test
    public void testUpdateBinRangeInfoByRefModifiedBinRangeException() throws Exception {
        BinRangeInfo fakeBinRangeInfo = new BinRangeInfo();
        fakeBinRangeInfo.generateFakeInfo();

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .put("/binRangeInfoCache/{ref}", fakeBinRangeInfo.getRef())
                .content(asJsonString(fakeBinRangeInfo))
                .contentType(MediaType.APPLICATION_JSON);

        // Test start attribute modified.
        BigDecimal savedStart = fakeBinRangeInfo.getStart();
        fakeBinRangeInfo.setStart(new BigDecimal("0000000000000000"));
        helperUpdateBinRangeByRef(fakeBinRangeInfo, builder);

        // Test end attribute modified.
        fakeBinRangeInfo.setStart(savedStart);
        fakeBinRangeInfo.setEnd(new BigDecimal("0000000000000000"));
        helperUpdateBinRangeByRef(fakeBinRangeInfo, builder);
    }

    @Test
    public void testUpdateBinRangeInfoByRef() throws Exception {
        BinRangeInfo fakeBinRangeInfo = new BinRangeInfo();
        fakeBinRangeInfo.generateFakeInfo();

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .put("/binRangeInfoCache/{ref}", fakeBinRangeInfo.getRef())
                .content(asJsonString(fakeBinRangeInfo))
                .contentType(MediaType.APPLICATION_JSON);

        when(binRangeService.findBinRangeInfoByRef(fakeBinRangeInfo.getRef())).thenReturn(Optional.ofNullable(fakeBinRangeInfo));
        MockHttpServletResponse response = getMockMvc().perform(builder).andReturn().getResponse();
        assertEquals(HttpStatus.OK.value(), response.getStatus());
    }

    @Test
    public void testDeleteBinRangeInfoByRef() throws Exception {
        BinRangeInfo fakeBinRangeInfo = new BinRangeInfo();
        fakeBinRangeInfo.generateFakeInfo();

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .put("/binRangeInfoCache/{ref}", fakeBinRangeInfo.getRef())
                .content(asJsonString(fakeBinRangeInfo))
                .contentType(MediaType.APPLICATION_JSON);

        when(binRangeService.findBinRangeInfoByRef(fakeBinRangeInfo.getRef())).thenReturn(Optional.ofNullable(null));
        MockHttpServletResponse notFoundResponse = getMockMvc().perform(builder).andReturn().getResponse();
        assertEquals(HttpStatus.NOT_FOUND.value(), notFoundResponse.getStatus());

        when(binRangeService.findBinRangeInfoByRef(fakeBinRangeInfo.getRef())).thenReturn(Optional.ofNullable(fakeBinRangeInfo));
        MockHttpServletResponse response = getMockMvc().perform(builder).andReturn().getResponse();
        assertEquals(HttpStatus.OK.value(), response.getStatus());
    }

}