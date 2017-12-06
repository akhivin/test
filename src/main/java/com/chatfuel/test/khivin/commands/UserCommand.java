package com.chatfuel.test.khivin.commands;

/**
 * Root class for all of user actions
 */
public abstract class UserCommand implements Comparable<UserCommand>{
    private int priority;

    /**
     * At first, we want to proceed with the command from within the lift and
     * after that, commands from a halls should be executed
     *
     * Halt command should be executed immediately.
     *
     * @see HallButtonUserCommand
     * @see WithinLiftUserCommand
     * @see StopSimulationUserCommand
     *
     * @param priority
     */
    public UserCommand(int priority) {
        this.priority = priority;
    }

    @Override
    public int compareTo(UserCommand o) {
        return this.priority - o.priority;
    }
}



