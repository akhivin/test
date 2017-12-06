package com.chatfuel.test.khivin.impl;

import com.chatfuel.test.khivin.LiftMonitorAdapter;
import com.chatfuel.test.khivin.EventSupplier;
import com.chatfuel.test.khivin.events.LiftEngineEvent;

import java.io.PrintStream;

public class LiftMonitorConsoleAdapter implements LiftMonitorAdapter, Runnable {

    private EventSupplier<LiftEngineEvent> supplier;
    private PrintStream out;

    public LiftMonitorConsoleAdapter(EventSupplier<LiftEngineEvent> supplier, PrintStream out) {
        this.supplier = supplier;
        this.out = out;
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                LiftEngineEvent event = this.supplier.get();
                if (event != null) {
                    out.println(event.format());
                }
            }
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
    }
}
