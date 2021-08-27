package com.mastercard.evaluation.bin.range.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class BinRangeInfoInvalidAdvice {
    @ResponseBody
    @ExceptionHandler(BinRangeInfoInvalidException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    String binRangeInfoInvalidHandler(BinRangeInfoInvalidException ex) {
        return ex.getMessage();
    }
}
