package com.mastercard.evaluation.bin.range.events;

import com.google.common.eventbus.AsyncEventBus;
import com.mastercard.evaluation.bin.range.events.subscribers.AuditSubscriber;
import com.mastercard.evaluation.bin.range.events.subscribers.Subscriber;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

@Configuration
public class EventBusConfiguration {

    private static final int THREAD_POOL_SIZE = 100;

    @Bean
    public AsyncEventBus getAsyncEventBus() {
        return new AsyncEventBus(Executors.newFixedThreadPool(THREAD_POOL_SIZE));
    }

    @Bean
    public List<Subscriber> getSubscribers() {
        List<Subscriber> subscribers  = new ArrayList<Subscriber>();
        subscribers.add(new AuditSubscriber());

        return subscribers;
    }
}
