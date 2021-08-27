package com.mastercard.evaluation.bin.range.controllers;

import java.util.UUID;

public class BinRangeInfoNotFoundException extends RuntimeException {
    public BinRangeInfoNotFoundException(UUID ref){
        super("BinRangeInfo: " + ref.toString() + " was not found.");
    }
}
