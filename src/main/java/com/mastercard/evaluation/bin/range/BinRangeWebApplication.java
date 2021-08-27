package com.mastercard.evaluation.bin.range;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class BinRangeWebApplication {

    public static void main(String... args) {
        SpringApplication.run(BinRangeWebApplication.class, args);
    }
}