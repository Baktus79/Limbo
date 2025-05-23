package no.vestlandetmc.limbo.commands;

import no.vestlandetmc.limbo.LimboPlugin;
import no.vestlandetmc.limbo.config.Messages;
import no.vestlandetmc.limbo.handler.MessageHandler;
import org.bukkit.entity.Player;

@SuppressWarnings("deprecation")
public class helpCommand {

	public static void onCommand(Player player) {

		MessageHandler.sendMessage(player,
				"&7------------- [ &aLimbo v" + LimboPlugin.getPlugin().getDescription().getVersion() + " &7] -------------",
				Messages.placeholders(Messages.LIMBO_HELP, player.getName(), null, null, null),
				Messages.placeholders(Messages.LIMBO_COMMAND, player.getName(), null, null, null),
				Messages.placeholders(Messages.TEMPLIMBO, player.getName(), null, null, null),
				Messages.placeholders(Messages.UNLIMBO, player.getName(), null, null, null),
				Messages.placeholders(Messages.LIMBOLIST, player.getName(), null, null, null),
				Messages.placeholders(Messages.LIMBO_RELOAD, player.getName(), null, null, null));
	}
}
