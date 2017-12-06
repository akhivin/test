package com.chatfuel.test.khivin;

@FunctionalInterface
public interface EventSupplier<T> {
    T get() throws InterruptedException;
}
