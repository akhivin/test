package com.chatfuel.test.khivin.events;

import java.time.Instant;

/**
 * Created by kh on 05.12.17.
 */
public class DoorsClosedEvent implements LiftEngineEvent {
    private final Instant moment;

    public DoorsClosedEvent(Instant moment) {
        this.moment = moment;
    }

    @Override
    public String getDescription() {
        return "Doors are closed";
    }

    @Override
    public Instant getTime() {
        return this.moment;
    }
}
