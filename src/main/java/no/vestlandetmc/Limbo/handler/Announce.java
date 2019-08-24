package no.vestlandetmc.Limbo.handler;

import java.text.SimpleDateFormat;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import no.vestlandetmc.Limbo.config.Messages;

public class Announce {

	public static void limboAnnounce(Player player, Player punisher, String reason) {
		for (final Player p : Bukkit.getOnlinePlayers()) {
			if(p.hasPermission("limbo.notify")) {
				DataHandler.sendMessage(p, Messages.placeholders(Messages.PLACED_IN_LIMBO_ANNOUNCE, player.getName(), punisher.getName(), null, reason));
			}
		}
	}

	public static void templimboAnnounce(Player player, Player punisher, String reason, long time) {
		final SimpleDateFormat timeFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
		for (final Player p : Bukkit.getOnlinePlayers()) {
			if(p.hasPermission("limbo.notify")) {
				DataHandler.sendMessage(p, Messages.placeholders(Messages.TEMPORARY_LIMBO, player.getName(), punisher.getName(), timeFormat.format(time), reason));
			}
		}
	}

	public static void unlimboAnnounce(OfflinePlayer player, Player punisher) {
		for (final Player p : Bukkit.getOnlinePlayers()) {
			if(p.hasPermission("limbo.notify")) {
				DataHandler.sendMessage(p, Messages.placeholders(Messages.RELEASED_LIMBO, player.getName(), punisher.getName(), null, null));
			}
		}
	}
}
