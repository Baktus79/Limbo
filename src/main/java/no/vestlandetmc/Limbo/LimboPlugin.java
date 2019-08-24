package no.vestlandetmc.Limbo;

import java.io.File;
import java.io.IOException;

import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import no.vestlandetmc.Limbo.commands.limboCommand;
import no.vestlandetmc.Limbo.commands.limbolistCommand;
import no.vestlandetmc.Limbo.commands.templimboCommand;
import no.vestlandetmc.Limbo.commands.unlimboCommand;
import no.vestlandetmc.Limbo.config.Config;
import no.vestlandetmc.Limbo.config.Messages;
import no.vestlandetmc.Limbo.handler.DataHandler;
import no.vestlandetmc.Limbo.handler.UpdateNotification;
import no.vestlandetmc.Limbo.listener.PlayerListener;

public class LimboPlugin extends JavaPlugin {
	private static LimboPlugin instance;
	private File dataFile;
	private FileConfiguration data;

	public static LimboPlugin getInstance() {
		return instance;
	}

	@Override
	public void onEnable() {
		instance = this;
		this.getCommand("limbo").setExecutor(new limboCommand());
		this.getCommand("unlimbo").setExecutor(new unlimboCommand());
		this.getCommand("limbolist").setExecutor(new limbolistCommand());
		this.getCommand("templimbo").setExecutor(new templimboCommand());
		this.getServer().getPluginManager().registerEvents(new PlayerListener(), this);
		this.getServer().getScheduler().scheduleSyncRepeatingTask(this, () -> { DataHandler.checkTime(); }, 0L, 1200L);

		Messages.initialize();
		Config.initialize();
		createDatafile();

		new UpdateNotification(68055) {

			@Override
			public void onUpdateAvailable() {
				getServer().getConsoleSender().sendMessage(ChatColor.GRAY + "-----------------------");
				getServer().getConsoleSender().sendMessage(ChatColor.GRAY + "[Limbo] Version " + getLatestVersion() + " is now available!");
				getServer().getConsoleSender().sendMessage(ChatColor.GRAY + "[Limbo] Download the update at https://www.spigotmc.org/resources/" + getProjectId());
				getServer().getConsoleSender().sendMessage(ChatColor.GRAY + "-----------------------");
			}
		}.runTaskAsynchronously(this);
	}

	@Override
	public void onDisable() {
		dataFile = new File(this.getDataFolder(), "data.dat");
		try {
			this.getDataFile().save(dataFile);
		} catch (final IOException e) {
			e.printStackTrace();
		}

		getServer().getScheduler().cancelTasks(this);
	}

	public FileConfiguration getDataFile() {
		return this.data;
	}

	public void createDatafile() {
		dataFile = new File(this.getDataFolder(), "data.dat");
		if (!dataFile.exists()) {
			dataFile.getParentFile().mkdirs();
			try {
				dataFile.createNewFile();
			} catch (final IOException e) {
				e.printStackTrace();
			}
		}

		data = new YamlConfiguration();
		try {
			data.load(dataFile);
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
	}

	public void reload() {
		Messages.initialize();
		Config.initialize();

	}
}
