package io.github.zachoooo.api;

import io.github.zachoooo.packets.MessagePacket;

/**
 * Created by Zach on 4/26/14.
 */
public class MessagePacketEvent extends AutoPacketEvent {

    public MessagePacketEvent(MessagePacket messagePacket) {
        super(messagePacket);
    }

    public MessagePacket getAutoPacket() {
        return (MessagePacket) super.getAutoPacket();
    }


}
