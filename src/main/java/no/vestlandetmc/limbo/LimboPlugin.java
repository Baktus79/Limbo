package no.vestlandetmc.limbo;

import lombok.Getter;
import no.vestlandetmc.limbo.commands.limboCommand;
import no.vestlandetmc.limbo.commands.limbolistCommand;
import no.vestlandetmc.limbo.commands.templimboCommand;
import no.vestlandetmc.limbo.commands.unlimboCommand;
import no.vestlandetmc.limbo.config.Config;
import no.vestlandetmc.limbo.config.Messages;
import no.vestlandetmc.limbo.database.SqlPool;
import no.vestlandetmc.limbo.handler.*;
import no.vestlandetmc.limbo.listener.ChatListener;
import no.vestlandetmc.limbo.listener.PlayerListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LimboPlugin extends JavaPlugin {

	@Getter
	private static LimboPlugin plugin;
	private final boolean libraryExist = true;
	private final List<Library> libraries = new ArrayList<>();

	@Override
	public void onLoad() {
		libraries.add(new Library("https://repo1.maven.org/maven2/org/mariadb/jdbc/mariadb-java-client/3.5.1/mariadb-java-client-3.5.1.jar",
				"mariadb-java-client-3.5.1.jar", "org.mariadb.jdbc", this));

		libraries.add(new Library("https://repo1.maven.org/maven2/mysql/mysql-connector-java/8.0.33/mysql-connector-java-8.0.33.jar",
				"mysql-connector-java-8.0.33.jar", "com.mysql.cj.jdbc.Driver", this));

		libraries.add(new Library("https://repo1.maven.org/maven2/com/zaxxer/HikariCP/6.2.1/HikariCP-6.2.1.jar",
				"HikariCP-6.2.1.jar", "com.zaxxer.hikari.HikariConfig", this));

		libraries.add(new Library("https://repo1.maven.org/maven2/org/xerial/sqlite-jdbc/3.47.1.0/sqlite-jdbc-3.47.1.0.jar",
				"sqlite-jdbc-3.47.1.0.jar", "org.sqlite.JDBC", this));
	}

	@Override
	public void onEnable() {
		plugin = this;
		libraries.forEach(Library::load);

		Messages.initialize();
		Config.initialize();

		if (!this.libraryExist) {
			getLogger().warning("Please restart the server for the libraries to take effect...");
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}

		if (getServer().getPluginManager().getPlugin("DiscordSRV") != null) {
			DiscordManager.discordEnabled();
			MessageHandler.sendConsole("[" + getDescription().getPrefix() + "] Successfully hooked up to DiscordSRV v"
					+ getServer().getPluginManager().getPlugin("DiscordSRV").getDescription().getVersion());

			if (!Config.DISCORDSRV_ENABLED)
				MessageHandler.sendConsole("[" + getDescription().getPrefix() + "] DiscordSRV is currently disabled in the config file.");
		}

		try {
			new SqlPool().initialize();
		} catch (final SQLException e) {
			getLogger().severe(e.getMessage());
		}

		Objects.requireNonNull(this.getCommand("limbo")).setExecutor(new limboCommand());
		Objects.requireNonNull(this.getCommand("unlimbo")).setExecutor(new unlimboCommand());
		Objects.requireNonNull(this.getCommand("limbolist")).setExecutor(new limbolistCommand());
		Objects.requireNonNull(this.getCommand("templimbo")).setExecutor(new templimboCommand());
		this.getServer().getPluginManager().registerEvents(new PlayerListener(), this);
		this.getServer().getPluginManager().registerEvents(new ChatListener(), this);
		this.getServer().getScheduler().scheduleSyncRepeatingTask(this, () -> {
			new DataHandler().isExpired();
		}, 0L, 60 * 20L);

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

		if (!this.libraryExist) {
			return;
		}

		try {
			if (!SqlPool.getDataSource().isClosed()) {
				SqlPool.getDataSource().close();
			}
		} catch (final SQLException e) {
			getLogger().severe(e.getMessage());
		}
	}

	public void reload() {
		Messages.initialize();
		Config.initialize();

	}
}
