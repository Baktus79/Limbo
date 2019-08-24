package no.vestlandetmc.Limbo.config;

import java.util.List;

public class Config extends ConfigHandler {

	private Config(String fileName) {
		super(fileName);
	}

	public static List<String>
	BLACKLISTED_COMMANDS;

	public static boolean
	BLOCK_BREAK,
	BLOCK_PLACE,
	ITEM_PICKUP,
	CHAT,
	VISIBLE;

	private void onLoad() {

		BLACKLISTED_COMMANDS = getStringList("Chat.BlackListedCommands");
		BLOCK_BREAK = getBoolean("WorldSettings.DisableBlockBreak");
		BLOCK_PLACE = getBoolean("WorldSettings.DisableBlockPlace");
		ITEM_PICKUP = getBoolean("WorldSettings.DisableItemPickup");
		CHAT = getBoolean("WorldSettings.DisableChat");
		VISIBLE = getBoolean("WorldSettings.DisableVisible");
	}

	public static void initialize() {
		new Config("config.yml").onLoad();
	}

}
