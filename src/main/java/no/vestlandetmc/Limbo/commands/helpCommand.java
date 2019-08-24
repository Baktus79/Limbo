package no.vestlandetmc.Limbo.commands;

import org.bukkit.entity.Player;

import no.vestlandetmc.Limbo.LimboPlugin;
import no.vestlandetmc.Limbo.config.Messages;
import no.vestlandetmc.Limbo.handler.DataHandler;

public class helpCommand {

	public static void onCommand(Player player) {

		DataHandler.sendMessage(player, "&7--------------------- [ &aLimbo v" + LimboPlugin.getInstance().getDescription().getVersion() + " &7] ---------------------");
		DataHandler.sendMessage(player, Messages.placeholders(Messages.LIMBO_HELP, player.getName(), null, null, null));
		DataHandler.sendMessage(player, Messages.placeholders(Messages.LIMBO_COMMAND, player.getName(), null, null, null));
		DataHandler.sendMessage(player, Messages.placeholders(Messages.TEMPLIMBO, player.getName(), null, null, null));
		DataHandler.sendMessage(player, Messages.placeholders(Messages.UNLIMBO, player.getName(), null, null, null));
		DataHandler.sendMessage(player, Messages.placeholders(Messages.LIMBOLIST, player.getName(), null, null, null));
		DataHandler.sendMessage(player, Messages.placeholders(Messages.LIMBO_RELOAD, player.getName(), null, null, null));
	}
}
