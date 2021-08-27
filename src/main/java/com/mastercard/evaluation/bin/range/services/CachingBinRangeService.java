package com.mastercard.evaluation.bin.range.services;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.io.Resources;
import com.mastercard.evaluation.bin.range.events.EventManager;
import com.mastercard.evaluation.bin.range.models.BinRange;
import com.mastercard.evaluation.bin.range.models.BinRangeInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.NavigableMap;
import java.util.Optional;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class CachingBinRangeService implements BinRangeService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CachingBinRangeService.class);

    private static final int ONE_HOUR_IN_MILLIS = 3600000;
    private static final String BIN_TABLE_RESOURCE_FILE_NAME = "bin-range-info-data.json";

    private final Lock lock = new ReentrantLock();
    private final ObjectMapper objectMapper;
    private final EventManager eventManager;

    private HashMap<UUID, BinRangeInfo> binRangeInfoCache = new HashMap<>();
    private NavigableMap<BinRange, UUID> binRangeInfoByBinRangeIndex = new TreeMap<>();

    @Autowired
    public CachingBinRangeService(ObjectMapper objectMapper, EventManager eventManager) {
        Preconditions.checkNotNull(objectMapper, "ObjectMapper cannot be null");
        Preconditions.checkNotNull(eventManager, "EventManager cannot be null");

        this.objectMapper = objectMapper;
        this.eventManager = eventManager;
    }

    @PostConstruct
    @Scheduled(fixedRate = ONE_HOUR_IN_MILLIS)
    @SuppressWarnings("unchecked")
    public void refreshCache() {
        LOGGER.info("Refreshing cache");

        try {
            URL url = Resources.getResource(BIN_TABLE_RESOURCE_FILE_NAME);
            JavaType type = objectMapper.getTypeFactory().constructCollectionType(List.class, BinRangeInfo.class);
            List<BinRangeInfo> binTableEntries = objectMapper.readValue(Resources.toString(url, Charsets.UTF_8), type);

            populateCache(binTableEntries);
        } catch (IOException e) {
            LOGGER.error("Failed to read bin range entries from file={}", BIN_TABLE_RESOURCE_FILE_NAME, e);
        }
    }

    @Override
    public Optional<BinRangeInfo> findBinRangeInfoByPan(String pan) {
        lock.lock();

        try {
            Optional<UUID> binRangeInfo = Optional.ofNullable(binRangeInfoByBinRangeIndex.get(new BinRange(pan)));

            return binRangeInfo.isPresent() ?
                    Optional.ofNullable(binRangeInfoCache.get(binRangeInfo.get())) :
                    Optional.empty();
        } finally {
            lock.unlock();
        }
    }

    @VisibleForTesting
    void populateCache(List<BinRangeInfo> binTableEntries) {
        lock.lock();

        try {
            binTableEntries.forEach(this::populateCacheAndIndices);
        } finally {
            lock.unlock();
        }
    }

    public HashMap<UUID, BinRangeInfo> getBinRangeInfoCache(){
        return binRangeInfoCache;
    }

    public Optional<UUID> checkBinRangeExists(BinRangeInfo binRangeInfo){
        BinRange binRange = getBinRangeFromEntry(binRangeInfo);

        lock.lock();
        try{
            Optional<UUID> binRangeRef = Optional.ofNullable(binRangeInfoByBinRangeIndex.get(binRange));

            return binRangeRef.isPresent() ? binRangeRef : Optional.empty();
        }finally {
            lock.unlock();
        }
    }

    public BinRangeInfo addBinRangeInfo(BinRangeInfo binRangeInfo) {
        lock.lock();

        binRangeInfo.setRef(UUID.randomUUID());

        try{
            populateCacheAndIndices(binRangeInfo);

            return binRangeInfo;
        }finally{
            lock.unlock();
        }
    }

    public Optional<BinRangeInfo> findBinRangeInfoByRef(UUID ref){
        lock.lock();

        try {
            return Optional.ofNullable(binRangeInfoCache.get(ref));
        } finally {
            lock.unlock();
        }
    }

    public void updateBinRangeInfo(BinRangeInfo binRangeInfo) {
        /*
        I wasnt sure what the best practice for Spring boot was regarding updates to 'models'.
        I decided not to implement validation logic here since a function should do only one thing.

        A possible solution is to create another function which searches and the updates the model.

        */
        lock.lock();

        try {
            populateCacheAndIndices(binRangeInfo);
        }finally {
            lock.unlock();
        }
    }

    public void deleteBinRangeInfoByRef(BinRangeInfo binRangeInfo){
        /*
        Discussed above regarding search.
         */
        lock.lock();

        try {
            binRangeInfoCache.remove(binRangeInfo.getRef());
            binRangeInfoByBinRangeIndex.remove(getBinRangeFromEntry(binRangeInfo));
        }
        finally {
            lock.unlock();
        }
    }

    private void populateCacheAndIndices(BinRangeInfo entry) {
        binRangeInfoCache.put(entry.getRef(), entry);
        binRangeInfoByBinRangeIndex.put(getBinRangeFromEntry(entry), entry.getRef());
    }

    private BinRange getBinRangeFromEntry(BinRangeInfo binRangeInfo) {
        return new BinRange(binRangeInfo.getStart(), binRangeInfo.getEnd());
    }
}
