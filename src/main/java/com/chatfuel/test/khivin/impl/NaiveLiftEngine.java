package com.chatfuel.test.khivin.impl;

import com.chatfuel.test.khivin.EventSupplier;
import com.chatfuel.test.khivin.LiftEngine;
import com.chatfuel.test.khivin.events.LiftEngineEvent;
import com.chatfuel.test.khivin.commands.HallButtonUserCommand;
import com.chatfuel.test.khivin.commands.UserCommand;
import com.chatfuel.test.khivin.commands.WithinLiftUserCommand;
import com.chatfuel.test.khivin.events.*;
import lombok.extern.java.Log;

import java.time.Instant;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by kh on 05.12.17.
 */
@Log
public class NaiveLiftEngine implements LiftEngine {
    private static final int IMPOSSIBLE_FLOOR = -1;


    private final BlockingQueue<LiftEngineEvent> events = new LinkedBlockingQueue<LiftEngineEvent>();

    private final BlockingQueue<UserCommand> targets = new PriorityBlockingQueue<>();

    private final int floors;
    private final int floorHeight;
    private final double speed;
    private final int awaitingPeriod;

    private volatile int targetFloor;

    /**
     * height from the ground
     * first floor - the lowest one
     */
    private double currentPosition;

    private long currentMoment;

    public NaiveLiftEngine(int floors, int floorHeight, double speed, int awaitingPeriod) {
        this.floors = floors;
        this.floorHeight = floorHeight;
        this.speed = speed;
        this.awaitingPeriod = awaitingPeriod;

        this.currentMoment = System.currentTimeMillis();
    }

    public void command(UserCommand floor) throws InterruptedException {
        this.targets.put(floor);
    }


    @Override
    public EventSupplier<LiftEngineEvent> supplier(long timeout, TimeUnit unit) {
        return () -> events.poll(timeout, unit);
    }

    @Override
    public void run() {
        this.currentMoment = System.currentTimeMillis();
        this.currentPosition = 0;
        this.targetFloor = IMPOSSIBLE_FLOOR;

        try {
            while (!Thread.currentThread().isInterrupted()) {
                if (this.targetFloor == IMPOSSIBLE_FLOOR) {
                    int target = chooseTargetFloorOrWaitSignal();
                    if (target == IMPOSSIBLE_FLOOR) {
                        continue;
                    }
                    newTarget(target);
                }
                Thread.currentThread().sleep(1000);

                boolean goalReached = false;
                long timeAdjustment = 0;
                long now = System.currentTimeMillis();
                double distanceToTarget = distanceToTarget();
                int direction = (distanceToTarget <= 0.) ? -1 : 1;
                double distancePassed = ((double) (now - this.currentMoment)) * this.speed / 1000;

                // if lift have reached target before this moment
                //
                if ((Math.abs(distancePassed) >= Math.abs(distanceToTarget))) {
                    timeAdjustment = 0;//(long) ((distancePassed - distanceToTarget) / speed);
                    distancePassed = distanceToTarget;
                    goalReached = true;
                }

                int currentFloor = floor(this.currentPosition);
                int floorsPassed = Math.abs(
                        currentFloor - floor(this.currentPosition + distancePassed * direction));

                for (int i = 1; i <= floorsPassed; i++) {
                    fireEvent(new LiftOnFloorEvent(currentFloor + direction * i, Instant.ofEpochMilli(now)));
                }

                this.currentPosition += direction * distancePassed;
                this.currentMoment = now;

                if (goalReached) {
                    fireEvent(new DoorsOpenedEvent(Instant.ofEpochMilli(now - timeAdjustment)));

                    long pause = this.awaitingPeriod * 1000 - timeAdjustment;
                    // if simulator lost the actual moment when Doors should were opened
                    // then just go run without pause
                    if (pause > 0) {
                        Thread.currentThread().sleep(this.awaitingPeriod * 1000 - timeAdjustment);
                    }
                    fireEvent(new DoorsClosedEvent(
                            Instant.ofEpochMilli(now - timeAdjustment + this.awaitingPeriod * 1000)));
                    this.targetFloor = IMPOSSIBLE_FLOOR;
                }
            }

        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
        log.info("Lift Engine terminated.");
    }

    private void newTarget(int targetFloor) {
        this.targetFloor = targetFloor;
        this.currentMoment = System.currentTimeMillis();
    }

    private double distanceToTarget() {
        return (this.targetFloor - 1) * this.floorHeight - this.currentPosition;
    }

    private void fireEvent(LiftEngineEvent event) throws InterruptedException {
        this.events.put(event);
    }

    private int floor(double height) {
        int i = 0;
        while (height >= 0) {
            i++;
            height -= this.floorHeight;
        }
        return i;
    }

    private int chooseTargetFloorOrWaitSignal() throws InterruptedException {
        UserCommand command = this.targets.poll(10, TimeUnit.SECONDS);
        if (command instanceof WithinLiftUserCommand) {
            return ((WithinLiftUserCommand) command).getFloor();
        }
        if (command instanceof HallButtonUserCommand) {
            return ((HallButtonUserCommand) command).getFloor();
        }
        return IMPOSSIBLE_FLOOR;
    }
}
