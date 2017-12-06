package com.chatfuel.test.khivin.commands;

import lombok.Getter;

public class WithinLiftUserCommand extends UserCommand {

    @Getter private final int floor;

    /**
     * @see UserCommand
     * @param floor
     */
    public WithinLiftUserCommand(int floor) {
        super(1);
        this.floor = floor;
    }
}



