package com.mastercard.evaluation.bin.range.services;

import com.mastercard.evaluation.bin.range.models.BinRangeInfo;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

public interface BinRangeService {

    Optional<BinRangeInfo> findBinRangeInfoByPan(String pan);
    Optional<UUID> checkBinRangeExists(BinRangeInfo binRangeInfo);
    BinRangeInfo addBinRangeInfo(BinRangeInfo binRangeInfo);
    Optional<BinRangeInfo> findBinRangeInfoByRef(UUID ref);
    void updateBinRangeInfo(BinRangeInfo binRangeInfo);
    void deleteBinRangeInfoByRef(BinRangeInfo binRangeInfo);
    HashMap<UUID, BinRangeInfo> getBinRangeInfoCache();
}
