package no.vestlandetmc.Limbo.listener;

import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import no.vestlandetmc.Limbo.config.Config;
import no.vestlandetmc.Limbo.handler.DataHandler;

public class ChatListener implements Listener {

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerChat(AsyncPlayerChatEvent e) {
		if(Config.CHAT) {
			final Player player = e.getPlayer();

			if(DataHandler.isLimbo(player)) {
				e.setCancelled(true);
				e.getRecipients().remove(player);
				player.sendMessage(ChatColor.RED + "[Limbo] " + ChatColor.WHITE +  "<" + player.getDisplayName() + "> " + e.getMessage());
				DataHandler.sendConsole("&c[Limbo] &f<" + player.getName() + "> " + e.getMessage());
				for (final Player perm : Bukkit.getOnlinePlayers()) {
					if(perm.hasPermission("limbo.chatvisible")) {
						perm.sendMessage(ChatColor.RED + "[Limbo] " + ChatColor.WHITE +  "<" + player.getDisplayName() + "> " + e.getMessage());
					}
				}
			}

			for(final Iterator<Player> it = e.getRecipients().iterator(); it.hasNext();) {
				final Player p = it.next();
				if(DataHandler.isLimbo(p)) {
					it.remove();
				}
			}
		}
	}

}
