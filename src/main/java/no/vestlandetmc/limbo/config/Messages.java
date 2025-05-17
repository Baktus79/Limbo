package no.vestlandetmc.limbo.config;

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
			TIME_PLACED,
			PLACED_BY,
			REASON_HOVER,
			MISSING_PERMISSION,
			PLAYER_NOT_ONLINE,
			PLAYER_BYPASS,
			PLAYER_EXIST_IN_LIMBO,
			PLAYER_NONEXIST_IN_LIMBO,
			PLAYER_NONEXIST,
			TYPE_VALID_NUMBER,
			CORRECT_FORMAT,
			BLACKLISTED_COMMANDS,
			PLACED_IN_LIMBO_ANNOUNCE,
			PLACED_IN_LIMBO_ANNOUNCE_SILENCE,
			TEMPORARY_LIMBO,
			TEMPORARY_LIMBO_SILENCE,
			RELEASED_LIMBO,
			RELEASED_LIMBO_SILENCE,
			PERMANENT_LIMBO_DISCORD,
			TEMPORARY_LIMBO_DISCORD,
			RELEASED_LIMBO_DISCORD,
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
		TIME_PLACED = getString("limboList.timePlaced");
		PLACED_BY = getString("limboList.placedBy");
		REASON_HOVER = getString("limboList.reasonHover");
		MISSING_PERMISSION = getString("warningMessages.missingPermission");
		PLAYER_NOT_ONLINE = getString("warningMessages.playerIsNotOnline");
		PLAYER_BYPASS = getString("warningMessages.playerBypass");
		PLAYER_EXIST_IN_LIMBO = getString("warningMessages.playerExistInLimbo");
		PLAYER_NONEXIST_IN_LIMBO = getString("warningMessages.playerIsNotInLimbo");
		PLAYER_NONEXIST = getString("warningMessages.playerNotExist");
		TYPE_VALID_NUMBER = getString("warningMessages.typeValidNumber");
		CORRECT_FORMAT = getString("warningMessages.correctFormat");
		BLACKLISTED_COMMANDS = getString("warningMessages.blacklistedCommand");
		PLACED_IN_LIMBO_ANNOUNCE = getString("announce.placedInLimbo");
		PLACED_IN_LIMBO_ANNOUNCE_SILENCE = getString("announce.placedInLimboSilence");
		TEMPORARY_LIMBO = getString("announce.temporaryLimbo");
		TEMPORARY_LIMBO_SILENCE = getString("announce.temporaryLimboSilence");
		RELEASED_LIMBO = getString("announce.releasedLimbo");
		RELEASED_LIMBO_SILENCE = getString("announce.releasedLimboSilence");
		PERMANENT_LIMBO_DISCORD = getString("discord.permanent-limbo");
		TEMPORARY_LIMBO_DISCORD = getString("discord.temporary-limbo");
		RELEASED_LIMBO_DISCORD = getString("discord.released-limbo");
		NO_REASON = getString("announce.noReason");
		RELOAD = getString("notifier.reload");
	}

	public static void initialize() {
		new Messages("messages.yml").onLoad();
	}

	public static String placeholders(String message, String player, String bywhom, String time, String reason) {
		return placeholders(message, player, bywhom, time, reason, false);
	}

	public static String placeholders(String message, String player, String bywhom, String time, String reason, boolean discord) {
		String converted = message;

		if (player != null) {
			String safePlayer = discord ? player.replace("_", "\\_") : player;
			converted = converted.replace("%player%", safePlayer);
		}
		if (bywhom != null) {
			String safeBywhom = discord ? bywhom.replace("_", "\\_") : bywhom;
			converted = converted.replace("%bywhom%", safeBywhom);
		}
		if (reason != null) {
			converted = converted.replace("%reason%", reason);
		}
		if (time != null) {
			converted = converted.replace("%time%", time);
		}

		return converted;
	}

}
