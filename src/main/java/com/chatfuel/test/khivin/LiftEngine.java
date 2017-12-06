package com.chatfuel.test.khivin;

import com.chatfuel.test.khivin.commands.UserCommand;
import com.chatfuel.test.khivin.events.LiftEngineEvent;

import java.util.concurrent.TimeUnit;

/**
 * Created by kh on 06.12.17.
 */
public interface LiftEngine extends  Runnable {
    EventSupplier<LiftEngineEvent> supplier(long timeout, TimeUnit unit);

    void command(UserCommand command) throws InterruptedException;
}
