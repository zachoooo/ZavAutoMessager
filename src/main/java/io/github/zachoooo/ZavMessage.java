package io.github.zachoooo;

import io.github.zachoooo.commands.CommandManager;
import io.github.zachoooo.packets.AutoPacket;
import io.github.zachoooo.packets.CommandPacket;
import io.github.zachoooo.packets.MessagePacket;
import io.github.zachoooo.utils.CustomConfig;
import io.github.zachoooo.utils.PluginPM;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * Main running class. This class should be responsible only for running the plugin.
 * Individual responsibilities should fall on separate classes.
 * Not doing the aforementioned is what made this re-write required.
 * Keep code as modular as possible.
 */
public class ZavMessage extends JavaPlugin {

    List<AutoPacket> autoPacketList = new ArrayList<AutoPacket>();

    public static CustomConfig getMainConfig() {
        return mainConfig;
    }

    public static CustomConfig getIgnoreConfig() {
        return ignoreConfig;
    }

    private CommandManager commandManager = new CommandManager(this);

    private static CustomConfig mainConfig;
    private static CustomConfig ignoreConfig;

    public void onEnable() {
        PluginPM.sendMessage(Level.INFO, "================== ZavMessage ==================");
        PluginPM.sendMessage(Level.INFO, "Loading configs...");
        mainConfig = new CustomConfig(this, "config.yml");
        ignoreConfig = new CustomConfig(this, "ignore.yml");
        mainConfig.saveDefaultConfig();
        ignoreConfig.saveDefaultConfig();
        mainConfig.reloadConfig();
        ignoreConfig.reloadConfig();
        loadMessages();
        PluginPM.sendMessage(Level.INFO, "Reading messages from config...");
        if (autoPacketList.size() < 1) {
            PluginPM.sendMessage(Level.SEVERE, "No messages could be loaded. Disabling plugin.");
            setEnabled(false);
            return;
        }
        PluginPM.sendMessage(Level.INFO, "Message list read!");
        commandManager.enableCommands();
        getServer().getScheduler().cancelTasks(this);
        getServer().getScheduler().scheduleSyncRepeatingTask(this, new AutoPacketRunnable(this), 0L, ((long) mainConfig.getConfig().getInt("delay") * 20));
        PluginPM.sendMessage(Level.INFO, "ZavMessage has loaded!");
        if (!mainConfig.getConfig().getBoolean("suppressconsoleads")) {
            PluginPM.sendMessage(Level.INFO, "Like this plugin? Consider donating towards development!");
            PluginPM.sendMessage(Level.INFO, "PayPal: zachoooo@gmail.com");
            PluginPM.sendMessage(Level.INFO, "Bitcoin: 16L1XuGXNbbNwfS9J3g49QnC22VhgYTn2G");
            PluginPM.sendMessage(Level.INFO, "Ethereum: 0xCd57A4abf36f20a3A68C4624Fefeea8FABa91812");
            PluginPM.sendMessage(Level.INFO, "Litecoin: MDBjepGGBLYhMep76bHaSLsnM6xgBW8erm");
            PluginPM.sendMessage(Level.INFO, "Dash: XmoKNLiL7kUZJ4ShLkELY7guaPGxFHqG75");
            PluginPM.sendMessage(Level.INFO, "ZCash: t1K2YitrCTWE3L1Vq5ByW1vtYAB333AxkQG");
        }
        PluginPM.sendMessage(Level.INFO, "================================================");
    }

    public void onDisable() {

    }

    public void loadMessages() {
        getAutoPacketList().clear();
        for (String permission : mainConfig.getConfig().getConfigurationSection("messages").getKeys(false)) {
            for (String message : mainConfig.getConfig().getStringList("messages." + permission)) {
                AutoPacket autoPacket = null;
                if (message.startsWith("/")) {
                    autoPacket = new CommandPacket(message.substring(1));
                } else {
                    autoPacket = new MessagePacket(message, permission);
                    ((MessagePacket) autoPacket).processMessages(mainConfig.getConfig().getBoolean("wordwrap"));
                }
                autoPacketList.add(autoPacket);
            }
        }
    }

    public List<AutoPacket> getAutoPacketList() {
        return autoPacketList;
    }

    public void reload() {
        getMainConfig().reloadConfig();
        loadMessages();
        getServer().getScheduler().cancelTasks(this);
        getServer().getScheduler().scheduleSyncRepeatingTask(this, new AutoPacketRunnable(this), 0L, ((long) mainConfig.getConfig().getInt("delay") * 20));
    }

}
