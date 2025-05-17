package no.vestlandetmc.limbo;

import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import lombok.Getter;
import no.vestlandetmc.limbo.commands.limboCommand;
import no.vestlandetmc.limbo.commands.limbolistCommand;
import no.vestlandetmc.limbo.commands.templimboCommand;
import no.vestlandetmc.limbo.commands.unlimboCommand;
import no.vestlandetmc.limbo.config.Config;
import no.vestlandetmc.limbo.config.Messages;
import no.vestlandetmc.limbo.database.SQLHandler;
import no.vestlandetmc.limbo.database.SqlPool;
import no.vestlandetmc.limbo.handler.DataHandler;
import no.vestlandetmc.limbo.handler.MessageHandler;
import no.vestlandetmc.limbo.handler.Permissions;
import no.vestlandetmc.limbo.handler.UpdateNotification;
import no.vestlandetmc.limbo.listener.ChatListener;
import no.vestlandetmc.limbo.listener.PlayerListener;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;

@SuppressWarnings({"deprecation", "UnstableApiUsage"})
public class LimboPlugin extends JavaPlugin {

	@Getter
	private static LimboPlugin plugin;

	@Getter
	private static DataHandler dataManager;

	@Getter
	private static SQLHandler sqlManager;

	@Override
	public void onEnable() {
		plugin = this;

		Messages.initialize();
		Config.initialize();
		Permissions.register();

		try {
			new SqlPool().initialize();
		} catch (final SQLException e) {
			getLogger().severe(e.getMessage());
		}

		sqlManager = new SQLHandler();
		dataManager = new DataHandler(sqlManager);

		getServer().getScheduler().runTaskAsynchronously(this, () -> dataManager.cachePlayerNames());

		if (Config.DISCORD_ENABLED)
			MessageHandler.sendConsole("[" + getDescription().getPrefix() + "] Discord feature is enable.");

		this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, cmd -> {
			cmd.registrar().register(
					"limbo",
					"Place a player in limbo.",
					new limboCommand(dataManager));

			cmd.registrar().register(
					"templimbo",
					"Place a player temporary in limbo.",
					new templimboCommand(dataManager));

			cmd.registrar().register(
					"unlimbo",
					"Remove a player from limbo.",
					new unlimboCommand(dataManager, sqlManager));

			cmd.registrar().register(
					"limbolist",
					"List players that are placed in limbo.",
					new limbolistCommand(dataManager, sqlManager));
		});

		this.getServer().getPluginManager().registerEvents(new PlayerListener(dataManager), this);
		this.getServer().getPluginManager().registerEvents(new ChatListener(dataManager), this);
		this.getServer().getScheduler().scheduleSyncRepeatingTask(this, () -> dataManager.isExpired(), 0L, 60 * 20L);

		new UpdateNotification("limbo-plugin") {

			@Override
			public void onUpdateAvailable() {
				MessageHandler.sendConsole(
						"&4-----------------------",
						"&4[Limbo] Version " + getLatestVersion() + " is now available!",
						"&4[Limbo] Download the update at https://modrinth.com/plugin/" + getProjectSlug(),
						"&4-----------------------"
				);
			}
		}.runTaskAsynchronously(this);

		final int pluginId = 25816;
		final Metrics metrics = new Metrics(this, pluginId);
	}

	@Override
	public void onDisable() {
		getServer().getScheduler().cancelTasks(this);

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
