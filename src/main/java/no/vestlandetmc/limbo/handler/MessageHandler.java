package no.vestlandetmc.limbo.handler;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import no.vestlandetmc.limbo.LimboPlugin;
import org.bukkit.entity.Player;

public class MessageHandler {

	public static void sendMessage(Player player, String... message) {
		for (String s : message) {
			sendMessage(player, s);
		}
	}

	public static void sendMessage(Player player, String message) {
		final Component text = colorize(message);
		player.sendMessage(text);
	}

	public static void sendConsole(String... message) {
		for (String m : message) {
			sendConsole(m);
		}
	}

	public static void sendConsole(String message) {
		final Component text = colorize(message);
		LimboPlugin.getPlugin().getServer().getConsoleSender().sendMessage(text);
	}

	public static void sendConsole(Component message) {
		LimboPlugin.getPlugin().getServer().getConsoleSender().sendMessage(message);
	}

	public static Component colorize(String message) {
		return LegacyComponentSerializer.legacy('&').deserialize(message);
	}

	public static void hoverMessage(Player player, String message, String... hoverLines) {
		Component base = colorize(message);
		StringBuilder hoverText = new StringBuilder();

		for (String line : hoverLines) {
			if (line != null) {
				hoverText.append(line);
			}
		}

		Component hover = colorize(hoverText.toString().trim());
		base = base.hoverEvent(HoverEvent.showText(hover));

		player.sendMessage(base);
	}

}
