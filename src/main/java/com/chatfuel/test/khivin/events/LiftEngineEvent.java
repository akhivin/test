package com.chatfuel.test.khivin.events;

import com.chatfuel.test.khivin.events.EventDateFormatter;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

/**
 * Created by kh on 05.12.17.
 */
public interface LiftEngineEvent {
    String getDescription();

    Instant getTime();

    default String format() {
        return String.format("Event at %s : %s",
                EventDateFormatter.format(getTime()),
                getDescription());
    }
}
