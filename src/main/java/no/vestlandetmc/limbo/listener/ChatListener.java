package no.vestlandetmc.limbo.listener;

import no.vestlandetmc.limbo.config.Config;
import no.vestlandetmc.limbo.handler.DataHandler;
import no.vestlandetmc.limbo.handler.MessageHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

@SuppressWarnings("deprecation")
public class ChatListener implements Listener {

	private final DataHandler data;

	public ChatListener(DataHandler data) {
		this.data = data;
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPlayerChat(AsyncPlayerChatEvent e) {
		if (Config.CHAT) {
			final Player player = e.getPlayer();

			if (player.hasPermission("limbo.bypass")) {
				return;
			}

			if (this.data.isLimbo(player.getUniqueId())) {
				e.setCancelled(true);

				player.sendMessage(MessageHandler.colorize("&c[Limbo] &f[" + player.getDisplayName() + "] " + e.getMessage()));
				MessageHandler.sendConsole(MessageHandler.colorize("&c[Limbo] &f[" + player.getDisplayName() + "] " + e.getMessage()));

				for (final Player perm : Bukkit.getOnlinePlayers()) {
					if (perm.hasPermission("limbo.chatvisible")) {
						perm.sendMessage(MessageHandler.colorize("&c[Limbo] &f[" + player.getDisplayName() + "] " + e.getMessage()));
					}
				}
			} else {
				e.getRecipients().removeIf(p -> this.data.isLimbo(p.getUniqueId()));
			}
		}
	}
}
