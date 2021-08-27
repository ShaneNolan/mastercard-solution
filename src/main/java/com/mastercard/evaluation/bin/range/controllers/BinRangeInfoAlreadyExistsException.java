package com.mastercard.evaluation.bin.range.controllers;

import java.util.UUID;

public class BinRangeInfoAlreadyExistsException extends RuntimeException {
    public BinRangeInfoAlreadyExistsException(UUID ref){
        super("BinRangeInfo: " + ref.toString() + " already exists.");
    }
}
