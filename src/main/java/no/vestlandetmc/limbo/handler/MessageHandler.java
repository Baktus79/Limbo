package no.vestlandetmc.limbo.handler;

import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Content;
import no.vestlandetmc.limbo.LimboPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class MessageHandler {

	public static void sendMessage(Player player, String... messages) {
		for (final String message : messages) {
			player.sendMessage(colorize(message));
		}
	}

	public static void sendAnnounce(String... messages) {
		for (final Player player : Bukkit.getOnlinePlayers()) {
			for (final String message : messages) {
				player.sendMessage(colorize(message));
			}
		}
	}

	public static void sendConsole(String... messages) {
		for (final String message : messages) {
			LimboPlugin.getPlugin().getServer().getConsoleSender().sendMessage(colorize(message));
		}
	}

	public static String colorize(String message) {
		return ChatColor.translateAlternateColorCodes('&', message);
	}

	public static void hoverMessage(Player player, String message, Content... hover) {
		final TextComponent msg = new TextComponent(colorize(message));

		msg.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hover));
		player.spigot().sendMessage(msg);
	}

}
