package io.github.zachoooo.packets;

/**
 * AutoPacket is an abstract class meant to provide guidelines for command packets, message packets,
 * and whatever other packets may eventually be created.
 */
public abstract class AutoPacket {

    /**
     * This method is used to actually process the packet and do what it needs to do.
     * That could be either sending a message, executing a command, or whatever it may be that you want to do.
     * This method should not be used for anything other than processing the packet as the plugin intended.
     */
    public abstract void processPacket();

}
