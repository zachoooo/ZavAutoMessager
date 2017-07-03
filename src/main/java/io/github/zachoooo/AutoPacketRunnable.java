package io.github.zachoooo;

import io.github.zachoooo.api.AutoPacketEvent;
import io.github.zachoooo.api.CommandPacketEvent;
import io.github.zachoooo.api.MessagePacketEvent;
import io.github.zachoooo.packets.AutoPacket;
import io.github.zachoooo.packets.CommandPacket;
import io.github.zachoooo.packets.MessagePacket;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Random;

/**
 * Created by Zach on 4/26/14.
 */
public class AutoPacketRunnable implements Runnable {

    private int messageIterator;
    private int previousMessageIndex;
    private ZavMessage zavMessage;

    public AutoPacketRunnable(ZavMessage zavMessage) {
        this.zavMessage = zavMessage;
    }

    @Override
    public void run() {
        if (ZavMessage.getMainConfig().getConfig().getBoolean("enabled", true)) {
            if (ZavMessage.getMainConfig().getConfig().getBoolean("requireplayersonline") && zavMessage.getServer().getOnlinePlayers().size() == 0) {
                return;
            }
            boolean randomMessaging = ZavMessage.getMainConfig().getConfig().getBoolean("messageinrandomorder", false);
            boolean permissions = ZavMessage.getMainConfig().getConfig().getBoolean("permissionsenabled", false);
            if (zavMessage.getAutoPacketList().size() == 1) {
                messageIterator = 0;
            } else if (randomMessaging) {
                messageIterator = getRandomMessage();
            }

            AutoPacket autoPacket = zavMessage.getAutoPacketList().get(messageIterator);
            AutoPacketEvent autoPacketEvent = null;

            if (autoPacket instanceof CommandPacket) {
                CommandPacket commandPacket = (CommandPacket) autoPacket;
                autoPacketEvent = new CommandPacketEvent(commandPacket);
            } else {
                MessagePacket messagePacket = (MessagePacket) autoPacket;
                autoPacketEvent = new MessagePacketEvent(messagePacket);
                MessagePacketEvent messagePacketEvent = (MessagePacketEvent) autoPacketEvent;
                if (permissions && messagePacket.getPermission() != null && !messagePacket.getPermission().equalsIgnoreCase("default")) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        if (player.hasPermission(messagePacket.getPermission()) && !ZavMessage.getIgnoreConfig().getConfig().getStringList("players").contains(player.getUniqueId().toString()) && !player.hasPermission("zavautomessager.ignoregroup")) {
                            messagePacket.getPlayers().add(player.getUniqueId());
                        }
                    }
                } else {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        if (!ZavMessage.getIgnoreConfig().getConfig().getStringList("players").contains(player.getUniqueId().toString())) {
                            messagePacket.getPlayers().add(player.getUniqueId());
                        }
                    }
                }
            }

            Bukkit.getPluginManager().callEvent(autoPacketEvent);

            if (autoPacketEvent.isCancelled()) {
                return;
            }

            autoPacket.processPacket();

            if (++messageIterator == zavMessage.getAutoPacketList().size()) {
                messageIterator = 0;
            }

        }
        return;
    }

    private int getRandomMessage() {
        Random random = new Random();
        if (ZavMessage.getMainConfig().getConfig().getBoolean("dontrepeatrandommessages", true)) {
            int i = random.nextInt(zavMessage.getAutoPacketList().size());
            if ((i != previousMessageIndex)) {
                previousMessageIndex = i;
                return i;
            }
            return getRandomMessage();
        }
        return random.nextInt(zavMessage.getAutoPacketList().size());
    }

}
