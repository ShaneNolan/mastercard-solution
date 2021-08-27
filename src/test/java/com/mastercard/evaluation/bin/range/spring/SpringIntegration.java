package com.mastercard.evaluation.bin.range.spring;

import com.mastercard.evaluation.bin.range.BinRangeWebApplication;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.transaction.AfterTransaction;

@RunWith(SpringRunner.class)
@Rollback
@SpringBootTest(classes = BinRangeWebApplication.class)
@ActiveProfiles("test")
public abstract class SpringIntegration {

    @AfterTransaction
    public void clean() {
        // = Clean dirty stuffs after transaction
    }
}