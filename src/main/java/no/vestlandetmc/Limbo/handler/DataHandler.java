package no.vestlandetmc.Limbo.handler;

import java.util.Date;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import no.vestlandetmc.Limbo.LimboPlugin;
import no.vestlandetmc.Limbo.config.Messages;

public class DataHandler {

	public static boolean isLimbo(Player player) {
		try {
			if(LimboPlugin.getInstance().getDataFile().contains(player.getUniqueId().toString())) {
				return LimboPlugin.getInstance().getDataFile().getBoolean(player.getUniqueId().toString() + ".limbo");
			}
		} catch (final NullPointerException e) {
			return false;
		}
		return false;
	}

	public static void checkTime() {
		final Date now = new Date();
		final long unixTime = now.getTime();

		if(!(LimboPlugin.getInstance().getDataFile().getKeys(false).toArray().length == 0)) {
			for(final String p : LimboPlugin.getInstance().getDataFile().getKeys(false)) {
				if(LimboPlugin.getInstance().getDataFile().contains(p + ".releasetime")) {
					final OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(p));
					if(unixTime > LimboPlugin.getInstance().getDataFile().getLong(p + ".releasetime")) {
						if(player.isOnline()) {
							final Player oPlayer = Bukkit.getPlayer(player.getName());
							for (final Player onlinePlayers : Bukkit.getOnlinePlayers()) {
								oPlayer.getPlayer().showPlayer(LimboPlugin.getInstance(), onlinePlayers);
							}
						}
						LimboPlugin.getInstance().getDataFile().set(player.getUniqueId().toString(), null);
					}
				}
			}
		}
	}

	public static String reason(String[] args) {
		int n;
		if(args.length >= 3) {
			if(isInt(args[1].replaceAll("\\D", ""))) {
				n = 2;
			} else { n = 1; }
			final StringBuilder message = new StringBuilder();
			for(int i = n; i < args.length; i++){
				message.append(args[i] + " ");
			}
			return message.toString();
		}
		return Messages.NO_REASON;
	}

	public static boolean isInt(String str) {
		try {
			Integer.parseInt(str);
			return true;
		} catch (final NumberFormatException e) {
			return false;
		}
	}

	public static long getTime(String time) {
		final String name = time.replaceAll("\\d", "");
		final int nr = Integer.parseInt(time.replaceAll("\\D", ""));

		long releaseTime = 0L;

		if (name.equalsIgnoreCase("months") || name.equalsIgnoreCase("month") || name.equalsIgnoreCase("mon")) {
			releaseTime = (nr * 60 * 60 * 24 * 7 * 4) * 1000;
		} else if (name.equalsIgnoreCase("weeks") || name.equalsIgnoreCase("week") || name.equalsIgnoreCase("w")) {
			releaseTime = (nr * 60 * 60 * 24 * 7) * 1000;
		} else if (name.equalsIgnoreCase("days") || name.equalsIgnoreCase("day") || name.equalsIgnoreCase("d")) {
			releaseTime = (nr * 60 * 60 * 24) * 1000;
		} else if (name.equalsIgnoreCase("hours") || name.equalsIgnoreCase("hour") || name.equalsIgnoreCase("h")) {
			releaseTime = (nr * 60 * 60) * 1000;
		} else if (name.equalsIgnoreCase("minutes") || name.equalsIgnoreCase("minute") || name.equalsIgnoreCase("min") || name.equalsIgnoreCase("m")) {
			releaseTime = (nr * 60) * 1000;
		}

		return releaseTime;
	}

	public static void sendMessage(Player player, String message) {
		player.sendMessage(colorize(message));
	}

	public static String colorize(String message) {
		return ChatColor.translateAlternateColorCodes('&', message);
	}
}
