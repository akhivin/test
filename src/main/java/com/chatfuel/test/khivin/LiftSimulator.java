package com.chatfuel.test.khivin;

import com.chatfuel.test.khivin.commands.StopSimulationUserCommand;
import com.chatfuel.test.khivin.commands.UserCommand;
import lombok.NonNull;
import lombok.extern.java.Log;

import java.util.concurrent.*;

/**
 * Created by kh on 05.12.17.
 */

@Log
public class LiftSimulator {

    private final LiftEngine liftEngine;
    private final LiftControlAdapter controlAdapter;
    private final LiftMonitorAdapter liftMonitor;

    private final ThreadPoolExecutor threadPool;

    public LiftSimulator(
            int floorsNumber,
            @NonNull LiftEngine liftEngine,
            @NonNull LiftControlAdapter controlAdapter,
            @NonNull LiftMonitorAdapter monitorAdapter) {

        this.liftEngine = liftEngine;

        this.controlAdapter = controlAdapter;
        this.liftMonitor = monitorAdapter;
        this.threadPool = new ThreadPoolExecutor(
                4,
                4,
                10,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(),
                Executors.defaultThreadFactory());


    }

    public void start() {
        this.threadPool.execute(this.controlAdapter);
        this.threadPool.execute(() -> {

            try {
                while (!Thread.currentThread().isInterrupted()) {
                    EventSupplier<UserCommand> userCommandSupplier =
                            controlAdapter.supplier(1000L, TimeUnit.MILLISECONDS);
                    UserCommand event = userCommandSupplier.get();
                    if (event == null) {
                        continue;
                    }
                    if (event instanceof StopSimulationUserCommand) {
                        stop();
                        continue;
                    }
                    this.liftEngine.command(event);
                }
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }
        });

        this.threadPool.execute(this.liftEngine);
        this.threadPool.execute(this.liftMonitor);
    }

    public void stop() {
        this.threadPool.shutdownNow();
        log.info("Simulation being terminated");
    }
}
