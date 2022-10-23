package no.vestlandetmc.limbo;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import no.vestlandetmc.limbo.commands.limboCommand;
import no.vestlandetmc.limbo.commands.limbolistCommand;
import no.vestlandetmc.limbo.commands.templimboCommand;
import no.vestlandetmc.limbo.commands.unlimboCommand;
import no.vestlandetmc.limbo.config.Config;
import no.vestlandetmc.limbo.config.Messages;
import no.vestlandetmc.limbo.database.SqlPool;
import no.vestlandetmc.limbo.handler.DataHandler;
import no.vestlandetmc.limbo.handler.DiscordManager;
import no.vestlandetmc.limbo.handler.MessageHandler;
import no.vestlandetmc.limbo.handler.UpdateNotification;
import no.vestlandetmc.limbo.listener.ChatListener;
import no.vestlandetmc.limbo.listener.PlayerListener;
import no.vestlandetmc.limbo.utils.DownloadLibs;

public class LimboPlugin extends JavaPlugin {

	private static LimboPlugin instance;
	private boolean libraryExist = true;

	public static LimboPlugin getInstance() {
		return instance;
	}

	@Override
	public void onEnable() {
		instance = this;

		try { downloadLibs(); }
		catch (final IOException e) { e.printStackTrace(); }

		Messages.initialize();
		Config.initialize();

		if(!this.libraryExist) {
			getLogger().warning("Please restart the server for the libraries to take effect...");
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}

		if(getServer().getPluginManager().getPlugin("DiscordSRV") != null) {
			DiscordManager.discordEnabled();
			MessageHandler.sendConsole("[" + getDescription().getPrefix() + "] Successfully hooked up to DiscordSRV v"
					+ getServer().getPluginManager().getPlugin("DiscordSRV").getDescription().getVersion());

			if(!Config.DISCORDSRV_ENABLED)
				MessageHandler.sendConsole("[" + getDescription().getPrefix() + "] DiscordSRV is currently disabled in the config file.");
		}

		try { new SqlPool().initialize(); }
		catch (final SQLException e) { e.printStackTrace(); }

		this.getCommand("limbo").setExecutor(new limboCommand());
		this.getCommand("unlimbo").setExecutor(new unlimboCommand());
		this.getCommand("limbolist").setExecutor(new limbolistCommand());
		this.getCommand("templimbo").setExecutor(new templimboCommand());
		this.getServer().getPluginManager().registerEvents(new PlayerListener(), this);
		this.getServer().getScheduler().scheduleSyncRepeatingTask(this, () -> { new DataHandler().isExpired(); }, 0L, 60 * 20L);
		this.getServer().getPluginManager().registerEvents(new ChatListener(), this);

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
		getServer().getScheduler().cancelTasks(this);

		if(!this.libraryExist) { return; }

		try {
			if(!SqlPool.getDataSource().isClosed()) {
				SqlPool.getDataSource().close();
			}
		} catch (final SQLException e) {
			e.printStackTrace();
		}
	}

	public void reload() {
		Messages.initialize();
		Config.initialize();

	}

	private void downloadLibs() throws IOException {
		final HashMap<String, String> libs = new HashMap<>();

		libs.put("mariadb-java-client-3.0.4.jar", "https://repo1.maven.org/maven2/org/mariadb/jdbc/mariadb-java-client/3.0.4/mariadb-java-client-3.0.4.jar");
		libs.put("mysql-connector-java-8.0.29.jar", "https://repo1.maven.org/maven2/mysql/mysql-connector-java/8.0.29/mysql-connector-java-8.0.29.jar");
		libs.put("HikariCP-5.0.1.jar", "https://repo1.maven.org/maven2/com/zaxxer/HikariCP/5.0.1/HikariCP-5.0.1.jar");
		libs.put("sqlite-jdbc-3.36.0.3.jar", "https://repo1.maven.org/maven2/org/xerial/sqlite-jdbc/3.36.0.3/sqlite-jdbc-3.36.0.3.jar");

		for(final String filename : libs.keySet()) {
			final DownloadLibs dl = new DownloadLibs(filename);
			final String url = libs.get(filename);

			if(!dl.exist()) {
				dl.url(url);
				this.libraryExist = false;

				MessageHandler.sendConsole("[" + getDescription().getPrefix() + "] Library " + filename + " was downloaded...");
			}
		}
	}
}
