package io.github.zachoooo.commands;

import io.github.zachoooo.ZavMessage;
import io.github.zachoooo.packets.AutoPacket;
import io.github.zachoooo.packets.CommandPacket;
import io.github.zachoooo.packets.MessagePacket;
import io.github.zachoooo.utils.PluginPM;
import io.github.zachoooo.utils.PluginPM.MessageType;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import se.ranzdo.bukkit.methodcommand.Arg;
import se.ranzdo.bukkit.methodcommand.Command;
import se.ranzdo.bukkit.methodcommand.CommandHandler;
import se.ranzdo.bukkit.methodcommand.Wildcard;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zach on 5/5/14.
 */
public class CommandManager {

    private ZavMessage zavMessage;
    private CommandHandler handler;

    public CommandManager(ZavMessage zavMessage) {
        this.zavMessage = zavMessage;
    }

    public void enableCommands() {
        handler = new CommandHandler(zavMessage);
        handler.registerCommands(this);
    }

    @Command(
            identifier = "automessager",
            description = "ZavMessage Parent Command",
            onlyPlayers = false,
            permissions = {"zavmessage.view", "zavmessage.*"}
    )
    public void root(CommandSender sender) {
        zavMessage.getServer().dispatchCommand(sender, "am help 1");
    }

    @Command(
            identifier = "automessager reload",
            description = "Reload the plugins messages",
            onlyPlayers = false,
            permissions = {"zavmessage.reload", "zavmessage.*"}
    )
    public void reload(CommandSender sender) {
        zavMessage.reload();
        PluginPM.sendMessage(MessageType.INFO, sender, "The plugin has been reloaded.");
    }

    @Command(
            identifier = "automessager toggle",
            description = "Toggle the plugin on and off",
            onlyPlayers = false,
            permissions = {"zavmessage.toggle", "zavmessage.*"}
    )
    public void toggle(CommandSender sender) {
        zavMessage.getMainConfig().getConfig().set("enabled", !zavMessage.getMainConfig().getConfig().getBoolean("enabled"));
        zavMessage.getMainConfig().saveConfig();
        zavMessage.getMainConfig().reloadConfig();
        PluginPM.sendMessage(PluginPM.MessageType.INFO, sender, "Automatic messaging has been set to: " + (zavMessage.getMainConfig().getConfig().getBoolean("enabled") ? "enabled" : "disabled"));
    }

    @Command(
            identifier = "automessager broadcast",
            description = "Broadcast a message using tag formatting",
            onlyPlayers = false,
            permissions = {"zavmessage.broadcast", "zavmessage.*"}
    )
    public void broadcast(
            CommandSender sender,
            @Wildcard @Arg(name = "message") String message
    ) {
        PluginPM.sendMessage(PluginPM.MessageType.NO_TAG, zavMessage.getMainConfig().getConfig().getString("chatformat").replace("%msg", message));
    }

    @Command(
            identifier = "automessager list",
            description = "List the messages in the config",
            onlyPlayers = false,
            permissions = {"zavmessage.list", "zavmessage.*"}
    )
    public void list(
            CommandSender sender,
            @Arg(name = "page", verifiers = "min[1]", def = "1") int page
    ) {
        try {
            zavMessage.getAutoPacketList().get((5 * page) - 5);
        } catch (IndexOutOfBoundsException e) {
            sender.sendMessage(ChatColor.RED + "You do not have that any messages on that page");
            return;
        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + "You have to enter an invalid number to show help page.");
            return;
        }

        sender.sendMessage(ChatColor.GOLD + "ZavMessage Messages Page: " + page);
        int initialInt = (5 * page) - 5;
        int finalInt = initialInt + 5;
        for (int iterator = initialInt; iterator < finalInt; iterator++) {
            String message = ChatColor.GOLD + Integer.toString(iterator + 1) + ". ";
            try {
                AutoPacket am = zavMessage.getAutoPacketList().get(iterator);
                if (am instanceof MessagePacket) {
                    MessagePacket mp = (MessagePacket) am;
                    message = message + "Node: " + mp.getPermission() + " Message:&r " + mp.getMessages().get(0);
                    if (mp.getMessages().size() > 1) {
                        message = message + "...";
                    }
                    message = ChatColor.translateAlternateColorCodes('&', message);
                } else {
                    CommandPacket cp = (CommandPacket) am;
                    message = message + "Command: /" + cp.getCommand();
                }
            } catch (IndexOutOfBoundsException e) {
                message = message + "None";
            }
            sender.sendMessage(message);
        }

    }

    @Command(
            identifier = "automessager ignore",
            description = "Toggle ignoring messages on and off",
            onlyPlayers = true,
            permissions = {"zavmessage.ignore", "zavmessage.*"}
    )
    public void ignore(Player sender) {
        List<String> ignorePlayers = new ArrayList<String>();
        ignorePlayers = zavMessage.getIgnoreConfig().getConfig().getStringList("players");
        boolean added = true;
        if (ignorePlayers.contains(sender.getUniqueId().toString())) {
            added = false;
            ignorePlayers.remove(sender.getUniqueId().toString());
        } else {
            ignorePlayers.add(sender.getUniqueId().toString());
        }
        zavMessage.getIgnoreConfig().getConfig().set("players", ignorePlayers);
        zavMessage.getIgnoreConfig().saveConfig();
        zavMessage.getIgnoreConfig().reloadConfig();
        PluginPM.sendMessage(PluginPM.MessageType.INFO, sender, "Ignoring auto messages is: " + (added ? "enabled" : "disabled"));
    }


}
