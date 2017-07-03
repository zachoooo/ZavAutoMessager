package io.github.zachoooo.utils;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;

public class CustomConfig {

	private final String fileName;
    private final JavaPlugin plugin;
	private File configFile;
	private FileConfiguration fileConfiguration;

	public CustomConfig(JavaPlugin plugin, String fileName) {
		if (plugin == null)
			throw new IllegalArgumentException("Plugin cannot be null");
		if (!plugin.isEnabled())
			throw new IllegalArgumentException("Plugin must be initialized");
		this.plugin = plugin;
		this.fileName = fileName;
		File dataFolder = plugin.getDataFolder();
		if (dataFolder == null)
			throw new IllegalStateException();
		this.configFile = new File(plugin.getDataFolder(), fileName);
	}

	public void reloadConfig() {
		if (!configFile.exists()) {
			plugin.saveResource(fileName, true);
		}
		fileConfiguration = YamlConfiguration.loadConfiguration(configFile);
	}

	public FileConfiguration getConfig() {
		if (fileConfiguration == null) {
			this.reloadConfig();
		}
		return fileConfiguration;
	}

	public void saveConfig() {
		if (fileConfiguration == null || configFile == null) {
			return;
		} else {
			try {
				getConfig().save(configFile);
			} catch (IOException ex) {
				plugin.getLogger().log(Level.SEVERE, "Could not save config to " + configFile, ex);
			}
		}
	}
	public void saveDefaultConfig() {
		if (!configFile.exists()) {
			this.plugin.saveResource(fileName, false);
		}
	}
}