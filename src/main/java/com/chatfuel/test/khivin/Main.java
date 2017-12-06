package com.chatfuel.test.khivin;

import com.chatfuel.test.khivin.impl.LiftControlConsoleAdapter;
import com.chatfuel.test.khivin.impl.LiftMonitorConsoleAdapter;
import com.chatfuel.test.khivin.impl.NaiveLiftEngine;

import java.util.concurrent.TimeUnit;

/**
 * Created by kh on 05.12.17.
 */
public class Main {
    private static final String USAGE_MESSAGE = "Usage: java -jar <floors> <floor height> <lift speed> <waiting period>";

    private static void printUsage() {
        System.out.println(USAGE_MESSAGE);
    }

    public static void main(String[] args) throws InterruptedException {
        if (args.length != 4) {
            printUsage();
            System.exit(0);
        }

        int floors;
        int floorHeight;
        int speed;
        int waitingPeriod;

        try {
            floors = Integer.parseUnsignedInt(args[0]);
            floorHeight = Integer.parseUnsignedInt(args[1]);
            speed = Integer.parseUnsignedInt(args[2]);
            waitingPeriod = Integer.parseUnsignedInt(args[3]);
        } catch (Exception e) {
            printUsage();
            return;
        }

        LiftControlAdapter controlAdapter = new LiftControlConsoleAdapter(
                floors,
                System.in,
                System.out);
        LiftEngine engine = new NaiveLiftEngine(floors, floorHeight, speed, waitingPeriod);
        LiftMonitorAdapter monitor = new LiftMonitorConsoleAdapter(
                engine.supplier(100, TimeUnit.MILLISECONDS),
                System.err);

        new LiftSimulator(
                floors,
                engine,
                controlAdapter,
                monitor).start();

    }
}
