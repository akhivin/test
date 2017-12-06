package com.chatfuel.test.khivin.events;

import java.time.Instant;

/**
 * Created by kh on 05.12.17.
 */
public class LiftOnFloorEvent implements LiftEngineEvent {
    private final int floor;
    private final Instant moment;

    public LiftOnFloorEvent(int floor, Instant moment) {
        this.floor = floor;
        this.moment = moment;
    }

    @Override
    public String getDescription() {
        return String.format("Lift on the %d's floor", this.floor);
    }

    @Override
    public Instant getTime() {
        return this.moment;
    }
}
