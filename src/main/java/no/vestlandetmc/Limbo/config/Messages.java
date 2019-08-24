package no.vestlandetmc.Limbo.config;

public class Messages extends ConfigHandler {

	private Messages(String fileName) {
		super(fileName);
	}

	public static String
	LIMBO_HELP,
	LIMBO_COMMAND,
	TEMPLIMBO,
	UNLIMBO,
	LIMBOLIST,
	LIMBO_RELOAD,
	LIST_HEADER,
	NO_PLAYERS_LIMBO,
	PERMANENT,
	TEMPORARY,
	PLACED_IN_LIMBO,
	PLACED_IN_LIMBO_BY,
	MISSING_PERMISSION,
	PLAYER_NOT_ONLINE,
	PLAYER_BYPASS,
	PLAYER_EXIST_IN_LIMBO,
	PLAYER_NONEXIST_IN_LIMBO,
	TYPE_VALID_NUMBER,
	CORRECT_FORMAT,
	BLACKLISTED_COMMANDS,
	PLACED_IN_LIMBO_ANNOUNCE,
	TEMPORARY_LIMBO,
	RELEASED_LIMBO,
	NO_REASON,
	RELOAD;

	private void onLoad() {

		LIMBO_HELP = getString("helpCommand.limboHelp");
		LIMBO_COMMAND = getString("helpCommand.limbo");
		TEMPLIMBO = getString("helpCommand.templimbo");
		UNLIMBO = getString("helpCommand.unlimbo");
		LIMBOLIST = getString("helpCommand.limbolist");
		LIMBO_RELOAD = getString("helpCommand.limboReload");
		LIST_HEADER = getString("limboList.listHeader");
		NO_PLAYERS_LIMBO = getString("limboList.noPlayersInLimbo");
		PERMANENT = getString("limboList.permanent");
		TEMPORARY = getString("limboList.temporary");
		PLACED_IN_LIMBO = getString("limboList.placedInLimbo");
		PLACED_IN_LIMBO_BY = getString("limboList.placedInLimboBy");
		MISSING_PERMISSION = getString("warningMessages.missingPermission");
		PLAYER_NOT_ONLINE = getString("warningMessages.playerIsNotOnline");
		PLAYER_BYPASS = getString("warningMessages.playerBypass");
		PLAYER_EXIST_IN_LIMBO = getString("warningMessages.playerExistInLimbo");
		PLAYER_NONEXIST_IN_LIMBO = getString("warningMessages.playerIsNotInLimbo");
		TYPE_VALID_NUMBER = getString("warningMessages.typeValidNumber");
		CORRECT_FORMAT = getString("warningMessages.correctFormat");
		BLACKLISTED_COMMANDS = getString("warningMessages.blacklistedCommand");
		PLACED_IN_LIMBO_ANNOUNCE = getString("announce.placedInLimbo");
		TEMPORARY_LIMBO = getString("announce.temporaryLimbo");
		RELEASED_LIMBO = getString("announce.releasedLimbo");
		NO_REASON = getString("announce.noReason");
		RELOAD = getString("notifier.reload");
	}

	public static void initialize() {
		new Messages("messages.yml").onLoad();
	}

	public static String placeholders(String message, String player, String bywhom, String time, String reason) {
		final String converted = message.replaceAll("%player%", player).replaceAll("%bywhom%", bywhom).replaceAll("%reason%", reason).replaceAll("%time%", time);

		return converted;

	}

}
