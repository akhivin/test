package com.chatfuel.test.khivin;

import com.chatfuel.test.khivin.commands.UserCommand;

import java.util.concurrent.TimeUnit;

/**
 * Created by kh on 05.12.17.
 */
public interface LiftControlAdapter extends Runnable {

    EventSupplier<UserCommand> supplier(long timeout, TimeUnit unit);

}
