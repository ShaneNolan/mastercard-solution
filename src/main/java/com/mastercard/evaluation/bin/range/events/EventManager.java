package com.mastercard.evaluation.bin.range.events;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import com.mastercard.evaluation.bin.range.events.models.Event;
import com.mastercard.evaluation.bin.range.events.subscribers.Subscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EventManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventManager.class);

    private final EventBus asyncEventBus;

    @Autowired
    public EventManager(AsyncEventBus asyncEventBus, List<Subscriber> subscribers) {
        this.asyncEventBus = asyncEventBus;

        subscribers.forEach(subscriber -> {
            LOGGER.info("Registering subscriber with name={}", subscriber.getClass().getSimpleName());

            asyncEventBus.register(subscriber);
        });
    }

    public <T extends Event> void publishAsync(T event) {
        asyncEventBus.post(event);
    }
}
