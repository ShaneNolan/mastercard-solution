package com.mastercard.evaluation.bin.range.events.models;

import com.google.common.annotations.VisibleForTesting;

import java.util.Date;
import java.util.Optional;

public class AuditEvent<T> implements Event {

    private Date createdAt;
    private Optional<T> before;
    private Optional<T> after;

    private final String EMPTY_OBJECT_PLACEHOLDER = "null";

    public AuditEvent(Optional<T> before, Optional<T> after){
        this.createdAt = new Date();
        this.before = before;
        this.after = after;
    }

    @Override
    public Date getCreatedAt() {
        return createdAt;
    }

    @VisibleForTesting
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    @VisibleForTesting
    public String getEMPTY_OBJECT_PLACEHOLDER() { return EMPTY_OBJECT_PLACEHOLDER; }

    @Override
    public Optional<T> getBeforeObject() { return before; }

    @Override
    public Optional<T> getCurrentObject() { return after; }

    @Override
    public String toString() {
        String beforeObject = before.isPresent() ? before.get().toString() : EMPTY_OBJECT_PLACEHOLDER;
        String afterObject = after.isPresent() ? after.get().toString() : EMPTY_OBJECT_PLACEHOLDER;

        return "createdAt={" + createdAt.toString() + "}, before={" + beforeObject + "}, after={" + afterObject + "}";
    }

}
