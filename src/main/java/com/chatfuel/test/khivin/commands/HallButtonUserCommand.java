package com.chatfuel.test.khivin.commands;

import lombok.Getter;

public class HallButtonUserCommand extends UserCommand {

    @Getter private final int floor;

    /**
     * @see UserCommand
     *
     * @param floor
     */
    public HallButtonUserCommand(int floor) {
        super(0);
        this.floor = floor;
    }
}



