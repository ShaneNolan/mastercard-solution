package com.mastercard.evaluation.bin.range.controllers;

import com.google.common.base.Preconditions;
import com.mastercard.evaluation.bin.range.models.BinRangeInfo;
import com.mastercard.evaluation.bin.range.services.BinRangeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping(value = "/binRangeInfoSearch")
public class BinRangeInfoSearchController {

    private final BinRangeService binRangeService;

    @Autowired
    public BinRangeInfoSearchController(BinRangeService binRangeService) {
        Preconditions.checkNotNull(binRangeService, "binRangeService cannot be null");

        this.binRangeService = binRangeService;
    }

    @RequestMapping(value = "/{pan}", method = GET, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<BinRangeInfo> getBinRangeInfoByPan(@PathVariable("pan") String pan) {
        return binRangeService.findBinRangeInfoByPan(pan)
                .map(binRangeInfo -> new ResponseEntity<>(binRangeInfo, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
