package io.github.zachoooo.api;

import io.github.zachoooo.packets.CommandPacket;

/**
 * Created by Zach on 4/26/14.
 */
public class CommandPacketEvent extends AutoPacketEvent {

    public CommandPacketEvent(CommandPacket commandPacket) {
        super(commandPacket);
    }

    public CommandPacket getAutoPacket() {
        return (CommandPacket) super.getAutoPacket();
    }

}
