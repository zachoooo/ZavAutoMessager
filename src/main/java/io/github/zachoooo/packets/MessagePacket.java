package io.github.zachoooo.packets;

import io.github.zachoooo.ZavMessage;
import io.github.zachoooo.utils.PluginPM;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.util.ChatPaginator;

import java.util.*;
import java.util.logging.Level;

/**
 * Created by Zach on 4/25/14.
 */
public class MessagePacket extends AutoPacket {

    private String permission; // The permission node associated with this message

    private List<String> messages = new ArrayList<String>();

    private List<UUID> players = new ArrayList<UUID>();

    public MessagePacket(String message) {
        this(message, null);
    }

    public MessagePacket(String message, String permission) {
        messages.add(message);
        this.permission = permission;
    }

    public MessagePacket(Collection<String> collection) {
        this(collection, null);
    }

    public MessagePacket(Collection<String> collection, String permission) {
        messages.addAll(collection);
        this.permission = permission;
    }

    public MessagePacket(String[] messages) {
        this(messages, null);
    }

    public MessagePacket(String[] messages, String permission) {
        for (String message : messages) {
            this.messages.add(message);
        }
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public List<String> getMessages() {
        return messages;
    }

    public List<UUID> getPlayers() {
        return players;
    }

    public void setMessages(List<String> messages) {
        this.messages = messages;
    }

    /**
     * This method applies all color affects. If the messages variable is only one line in length, it splits the lines up.
     */
    public void processMessages(boolean chatPaginating) {

        /**
         * This if section checks if their is only one message in the list.
         * If so it splits the message into the appropriate lines.
         * Their should not be a case where multiple lines are added that still need to be split
         */
        if (messages.size() == 1) {
            List<String> newMessages = new ArrayList<String>();
            newMessages.addAll(Arrays.asList(messages.get(0).split("%n")));
            messages = newMessages;
        }

        /**
         *
         */
        if (chatPaginating) {
            List<String> newMessages = new ArrayList<String>();
            for (String message : messages) {
                newMessages.addAll(Arrays.asList(ChatPaginator.wordWrap(message, ChatPaginator.AVERAGE_CHAT_PAGE_WIDTH)));
            }
            messages = newMessages;
        }

        /**
         * Color messages
         */
        List<String> newMessages = new ArrayList<String>();
        for (String message : messages) {
            message = ChatColor.translateAlternateColorCodes('&', message);
            newMessages.add(message);
        }
        messages = newMessages;



    }

    @Override
    public void processPacket() {
        ChatColor[] COLOR_LIST = {ChatColor.AQUA, ChatColor.BLACK, ChatColor.BLUE, ChatColor.DARK_AQUA, ChatColor.DARK_BLUE, ChatColor.DARK_GRAY,
                ChatColor.DARK_GREEN, ChatColor.DARK_PURPLE, ChatColor.DARK_RED, ChatColor.GOLD, ChatColor.GRAY, ChatColor.GREEN, ChatColor.LIGHT_PURPLE,
                ChatColor.RED, ChatColor.YELLOW};
        boolean firstLine = true;
        for (String message : messages) {
            for (UUID uuid : players) {
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
                if (offlinePlayer.isOnline()) {
                    Player player = (Player) offlinePlayer;
                    String tag = "";
                    if (firstLine) {
                        tag = ZavMessage.getMainConfig().getConfig().getString("chatformat");
                        tag = ChatColor.translateAlternateColorCodes('&', tag);
                        tag = tag.replace("%msg", message);
                        firstLine = false;
                    } else {
                        tag = message;
                    }
                    Random random = new Random();
                    tag = tag.replace("%r", COLOR_LIST[random.nextInt(COLOR_LIST.length)].toString());
                    PluginPM.sendMessage(PluginPM.MessageType.NO_TAG, player, tag);
                }
            }
            if (ZavMessage.getMainConfig().getConfig().getBoolean("messagesinconsole")) {
                PluginPM.sendMessage(Level.INFO, message);
            }
        }

        players.clear();

    }
}
