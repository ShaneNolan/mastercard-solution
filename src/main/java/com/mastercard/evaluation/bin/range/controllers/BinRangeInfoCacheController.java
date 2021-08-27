package com.mastercard.evaluation.bin.range.controllers;

import com.google.common.base.Preconditions;
import com.mastercard.evaluation.bin.range.events.EventManager;
import com.mastercard.evaluation.bin.range.events.models.AuditEvent;
import com.mastercard.evaluation.bin.range.models.BinRangeInfo;
import com.mastercard.evaluation.bin.range.services.BinRangeService;
import com.mastercard.evaluation.bin.range.services.CachingBinRangeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.*;

@RestController
@RequestMapping(value = "/binRangeInfoCache")
public class BinRangeInfoCacheController {

    private final BinRangeService cachingBinRangeService;
    private final EventManager eventManager;

    @Autowired
    public BinRangeInfoCacheController(BinRangeService cachingBinRangeService, EventManager eventManager) {
        Preconditions.checkNotNull(cachingBinRangeService, "cachingBinRangeService cannot be null.");

        this.cachingBinRangeService = cachingBinRangeService;
        this.eventManager = eventManager;
    }

    @RequestMapping(method = GET, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<HashMap<UUID, BinRangeInfo>> getAllBinRangeInfo() {
        return new ResponseEntity<>(cachingBinRangeService.getBinRangeInfoCache(), HttpStatus.OK);
    }

    @RequestMapping(method = POST, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<BinRangeInfo> createBinRangeInfo(@RequestBody @Valid BinRangeInfo binRangeInfo) {
        Optional<UUID> binRangeRef = cachingBinRangeService.checkBinRangeExists(binRangeInfo);
        if(binRangeRef.isPresent()) {
            throw new BinRangeInfoAlreadyExistsException(binRangeRef.get());
        }

        binRangeInfo = cachingBinRangeService.addBinRangeInfo(binRangeInfo);

        eventManager.publishAsync(new AuditEvent(Optional.empty(), Optional.ofNullable(binRangeInfo)));

        return new ResponseEntity<>(binRangeInfo, HttpStatus.CREATED);
    }

    @RequestMapping(value="/{ref}", method = PUT, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<BinRangeInfo> updateBinRangeInfoByRef(@PathVariable("ref") UUID ref, @RequestBody @Valid BinRangeInfo binRangeInfo) {
        if(!ref.equals(binRangeInfo.getRef())){
            throw new BinRangeInfoInvalidException(String.format("Ref values %s : %s don't match.", ref, binRangeInfo.getRef()));
        }

        Optional<BinRangeInfo> foundBinRange = cachingBinRangeService.findBinRangeInfoByRef(ref);
        if(!foundBinRange.isPresent()){
            throw new BinRangeInfoNotFoundException(ref);
        }

        if(!binRangeInfo.getStart().equals(foundBinRange.get().getStart())
                || !binRangeInfo.getEnd().equals(foundBinRange.get().getEnd())){
            throw new BinRangeInfoInvalidException("Bin Ranges cannot be modified.");
        }

        cachingBinRangeService.updateBinRangeInfo(binRangeInfo);

        eventManager.publishAsync(new AuditEvent(Optional.ofNullable(foundBinRange), Optional.ofNullable(binRangeInfo)));

        return new ResponseEntity<>(binRangeInfo, HttpStatus.OK);
    }

    @RequestMapping(value = "/{ref}", method = DELETE)
    public ResponseEntity<BinRangeInfo> deleteBinRangeInfoByRef(@PathVariable("ref") UUID ref) {
        Optional<BinRangeInfo> foundBinRange = cachingBinRangeService.findBinRangeInfoByRef(ref);
        if(!foundBinRange.isPresent()){
            throw new BinRangeInfoNotFoundException(ref);
        }

        BinRangeInfo binRangeInfo = foundBinRange.get();

        cachingBinRangeService.deleteBinRangeInfoByRef(binRangeInfo);

        eventManager.publishAsync(new AuditEvent(Optional.ofNullable(binRangeInfo), Optional.empty()));

        return new ResponseEntity<>(binRangeInfo, HttpStatus.OK);
    }

}
