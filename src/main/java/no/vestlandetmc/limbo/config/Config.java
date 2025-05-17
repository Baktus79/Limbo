package no.vestlandetmc.limbo.config;

import java.util.List;

public class Config extends ConfigHandler {

	private Config(String fileName) {
		super(fileName);
	}

	public static List<String>
			BLACKLISTED_COMMANDS;

	public static String
			HOST,
			USER,
			PASSWORD,
			DATABASE,
			SQLTYPE,
			DISCORD_WEBHOOK_URL;

	public static int
			PORT,
			MAX_POOL,
			CON_TIMEOUT,
			CON_LIFETIME;

	public static boolean
			BLOCK_BREAK,
			BLOCK_PLACE,
			ITEM_PICKUP,
			CHAT,
			VISIBLE,
			ENABLE_SSL,
			DISCORD_ENABLED;

	private void onLoad() {

		BLACKLISTED_COMMANDS = getStringList("Chat.BlackListedCommands");
		BLOCK_BREAK = getBoolean("WorldSettings.DisableBlockBreak");
		BLOCK_PLACE = getBoolean("WorldSettings.DisableBlockPlace");
		ITEM_PICKUP = getBoolean("WorldSettings.DisableItemPickup");
		CHAT = getBoolean("WorldSettings.DisableChat");
		VISIBLE = getBoolean("WorldSettings.DisableVisible");

		DISCORD_ENABLED = getBoolean("discord.enabled");
		DISCORD_WEBHOOK_URL = getString("discord.webhookURL");

		SQLTYPE = getString("database.engine");
		HOST = getString("database.host");
		USER = getString("database.user");
		PASSWORD = getString("database.password");
		DATABASE = getString("database.database");
		PORT = getInt("database.port");
		ENABLE_SSL = getBoolean("database.enable-ssl");
		MAX_POOL = getInt("database.pool.max-pool-size");
		CON_TIMEOUT = getInt("database.pool.connection-timeout") * 1000;
		CON_LIFETIME = getInt("database.pool.max-lifetime") * 1000;
	}

	public static void initialize() {
		new Config("config.yml").onLoad();
	}

}
