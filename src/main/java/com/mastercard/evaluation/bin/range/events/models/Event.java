package com.mastercard.evaluation.bin.range.events.models;

import java.util.Date;

public interface Event<T> {
    Date getCreatedAt();
    T getBeforeObject();
    T getCurrentObject();
}
