package com.chatfuel.test.khivin.impl;

import com.chatfuel.test.khivin.LiftControlAdapter;
import com.chatfuel.test.khivin.commands.HallButtonUserCommand;
import com.chatfuel.test.khivin.commands.WithinLiftUserCommand;
import com.chatfuel.test.khivin.commands.StopSimulationUserCommand;
import com.chatfuel.test.khivin.commands.UserCommand;
import com.chatfuel.test.khivin.EventSupplier;
import lombok.NonNull;
import lombok.extern.java.Log;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

/**
 * Created by kh on 05.12.17.
 */
@Log
public class LiftControlConsoleAdapter implements LiftControlAdapter {

    private static final String GO_COMMAND = "goto";
    private static final String FLOOR_COMMAND = "floor";
    private static final String EXIT_COMMAND = "exit";

    private final int floors;
    private final Scanner scanner;
    private final PrintStream out;

    private final BlockingQueue<UserCommand> events = new LinkedBlockingDeque<>();


    public LiftControlConsoleAdapter(
            int floors,
            @NonNull InputStream in,
            @NonNull PrintStream out) {
        this.floors = floors;
        scanner = new Scanner(in);
        this.out = out;
    }

    @Override
    public void run() {
        out.println("Welcome to Lift Simulator!");
        out.println("Commands: ");
        out.println("  exit - stop execution ");
        out.println(String.format("  %s <n> - user pressed button inside elevator. n - target floor number", GO_COMMAND));
        out.println(String.format("  floor <n> - user pressed hall button on a floor n", FLOOR_COMMAND));

        try {
            while (!Thread.currentThread().isInterrupted()) {
                out.print(": ");
                UserCommand command;
                try {
                    String textCommand = this.scanner.nextLine();
                    command = parse(textCommand);
                } catch (CommandParseException | NoSuchElementException e) {
                    out.println("Invalid command: " + e.getMessage());
                    continue;
                }
                events.put(command);
            }
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
    }

    private UserCommand parse(String textCommand) throws CommandParseException {
        if (textCommand == null) {
            throw new CommandParseException("Unknown command");
        }
        int floor;
        try {
            String[] parts = textCommand.split(" ");
            switch (parts[0]) {
                case GO_COMMAND:
                    floor = Integer.parseInt(parts[1]);
                    checkFloor(floor);

                    return new WithinLiftUserCommand(floor);

                case FLOOR_COMMAND:
                    floor = Integer.parseInt(parts[1]);
                    checkFloor(floor);

                    return new HallButtonUserCommand(floor);

                case EXIT_COMMAND:
                    return new StopSimulationUserCommand();

                default:
                    throw new CommandParseException("Unknown command: " + parts[0]);
            }
        } catch (IllegalArgumentException iae) {
            throw new CommandParseException(iae.getMessage(), iae);

        } catch (Exception e) {
            throw new CommandParseException("Cannot parse command: [" + textCommand + "]", e);
        }
    }

    private void checkFloor(int floor) {
        if (floor < 1 || floor > this.floors) {
            throw new IllegalArgumentException(String.format("Floor should more %d and less or equal to %d", 0, this.floors));
        }
    }

    public EventSupplier<UserCommand> supplier(long timeout, TimeUnit unit) {
        return () -> events.poll(timeout, unit);
    }

    private static class CommandParseException extends Exception {
        public CommandParseException(String s) {
            super(s);
        }

        public CommandParseException(String s, Throwable throwable) {
            super(s, throwable);
        }
    }
}
