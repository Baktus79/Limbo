package no.vestlandetmc.limbo.handler;

import java.text.SimpleDateFormat;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import no.vestlandetmc.limbo.config.Messages;

public class Announce {

	public static void limboAnnounce(OfflinePlayer player, OfflinePlayer punisher, String reason, boolean silence) {
		for (final Player p : Bukkit.getOnlinePlayers()) {
			if(p.hasPermission("limbo.notify") && !silence) {
				MessageHandler.sendMessage(p, Messages.placeholders(Messages.PLACED_IN_LIMBO_ANNOUNCE, player.getName(), punisher.getName(), null, reason));
				DiscordManager.sendLimbo(Messages.placeholders(Messages.PLACED_IN_LIMBO_ANNOUNCE, player.getName(), punisher.getName(), null, reason, true));
			}

			if(p.hasPermission("limbo.notify.silence") && silence) {
				MessageHandler.sendMessage(p, Messages.placeholders(Messages.PLACED_IN_LIMBO_ANNOUNCE_SILENCE, player.getName(), punisher.getName(), null, reason));
			}
		}
	}

	public static void templimboAnnounce(OfflinePlayer player, OfflinePlayer punisher, String reason, long time, boolean silence) {
		final SimpleDateFormat timeFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
		for (final Player p : Bukkit.getOnlinePlayers()) {
			if(p.hasPermission("limbo.notify") && !silence) {
				MessageHandler.sendMessage(p, Messages.placeholders(Messages.TEMPORARY_LIMBO, player.getName(), punisher.getName(), timeFormat.format(time), reason));
				DiscordManager.sendLimbo(Messages.placeholders(Messages.TEMPORARY_LIMBO, player.getName(), punisher.getName(), timeFormat.format(time), reason));
			}

			if(p.hasPermission("limbo.notify.silence") && silence) {
				MessageHandler.sendMessage(p, Messages.placeholders(Messages.TEMPORARY_LIMBO_SILENCE, player.getName(), punisher.getName(), timeFormat.format(time), reason));
			}
		}
	}

	public static void unlimboAnnounce(OfflinePlayer player, OfflinePlayer punisher, boolean silence) {
		for (final Player p : Bukkit.getOnlinePlayers()) {
			if(p.hasPermission("limbo.notify") && !silence) {
				MessageHandler.sendMessage(p, Messages.placeholders(Messages.RELEASED_LIMBO, player.getName(), punisher.getName(), null, null));
			}

			if(p.hasPermission("limbo.notify.silence") && silence) {
				MessageHandler.sendMessage(p, Messages.placeholders(Messages.RELEASED_LIMBO_SILENCE, player.getName(), punisher.getName(), null, null));
			}
		}
	}
}
