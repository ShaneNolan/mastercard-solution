package com.mastercard.evaluation.bin.range.events.subscribers;

import com.mastercard.evaluation.bin.range.events.models.AuditEvent;
import com.mastercard.evaluation.bin.range.models.BinRangeInfo;
import org.junit.Test;

import java.util.Optional;

import static org.mockito.Mockito.mock;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class AuditSubscriberTest {

    private AuditSubscriber auditSubscriber = mock(AuditSubscriber.class);

    @Test
    public void shouldHandleAuditEventCorrectly() {
        BinRangeInfo fakeBinRangeInfo = new BinRangeInfo();
        fakeBinRangeInfo.generateFakeInfo();

        BinRangeInfo fakeBinRangeInfoModified = new BinRangeInfo();
        fakeBinRangeInfoModified.generateFakeInfo();
        fakeBinRangeInfoModified.setRef(fakeBinRangeInfo.getRef());

        AuditEvent auditEvent = new AuditEvent(
                Optional.ofNullable(fakeBinRangeInfo),
                Optional.ofNullable(fakeBinRangeInfoModified)
        );

        auditSubscriber.handleEvent(auditEvent);
        verify(auditSubscriber, times(1)).handleEvent((auditEvent));
    }
}