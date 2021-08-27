package com.mastercard.evaluation.bin.range.events.models;

import com.mastercard.evaluation.bin.range.models.BinRangeInfo;
import org.junit.Test;

import java.util.Date;
import java.util.Optional;

import static org.junit.Assert.assertEquals;

public class AuditEventTest {

    private String helperToString(String date, String before, String after){
        return "createdAt={" + date + "}, before={" + before + "}, after={" + after + "}";
    }

    @Test
    public void testToString() {
        BinRangeInfo fakeBinRangeInfo = new BinRangeInfo();
        fakeBinRangeInfo.generateFakeInfo();

        Date date = new Date();
        AuditEvent auditEventDelete = new AuditEvent(Optional.ofNullable(fakeBinRangeInfo), Optional.empty());
        auditEventDelete.setCreatedAt(date);

        assertEquals(
                helperToString(date.toString(), fakeBinRangeInfo.toString(), auditEventDelete.getEMPTY_OBJECT_PLACEHOLDER()),
                auditEventDelete.toString()
        );

        AuditEvent auditEventCreate = new AuditEvent(Optional.empty(), Optional.ofNullable(fakeBinRangeInfo));
        auditEventCreate.setCreatedAt(date);

        assertEquals(
                helperToString(date.toString(), auditEventDelete.getEMPTY_OBJECT_PLACEHOLDER(), fakeBinRangeInfo.toString()),
                auditEventCreate.toString()
        );
    }
}
