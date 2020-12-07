package no.vestlandetmc.Limbo.listener;

import java.util.Iterator;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

import no.vestlandetmc.Limbo.LimboPlugin;
import no.vestlandetmc.Limbo.config.Config;
import no.vestlandetmc.Limbo.config.Messages;
import no.vestlandetmc.Limbo.handler.DataHandler;
import no.vestlandetmc.Limbo.handler.UpdateNotification;

@SuppressWarnings("deprecation")
public class PlayerListener implements Listener {

	@EventHandler
	public void playerJoin(PlayerJoinEvent p) {
		final Player player = p.getPlayer();

		if(Config.VISIBLE) {

			try {
				for(final String limboPlayers : LimboPlugin.getInstance().getDataFile().getKeys(false)) {
					if(LimboPlugin.getInstance().getDataFile().getBoolean(limboPlayers.toString() + ".limbo")) {
						final Player playerInLimbo = LimboPlugin.getInstance().getServer().getPlayer(UUID.fromString(limboPlayers));
						playerInLimbo.getPlayer().hidePlayer(player);
					}
				}
			} catch (final NullPointerException e) {
			}

			if(DataHandler.isLimbo(player)) {
				for (final Player limboP : Bukkit.getOnlinePlayers()) {
					player.hidePlayer(limboP);
				}
			}
		}

		if(player.isOp()) {
			if(UpdateNotification.isUpdateAvailable()) {
				player.sendMessage(ChatColor.GREEN + "------------------------------------");
				player.sendMessage(ChatColor.GREEN + "Limbo is outdated. Update is available!");
				player.sendMessage(ChatColor.GREEN + "Your version is " + ChatColor.BOLD + UpdateNotification.getCurrentVersion() + ChatColor.GREEN + " and can be updated to version " + ChatColor.BOLD + UpdateNotification.getLatestVersion());
				player.sendMessage(ChatColor.GREEN + "Get the new update at https://www.spigotmc.org/resources/" + UpdateNotification.getProjectId());
				player.sendMessage(ChatColor.GREEN + "------------------------------------");
			}
		}

	}

	@EventHandler(priority = EventPriority.MONITOR)
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

	@EventHandler(priority = EventPriority.HIGH)
	public void onBlockBreak(BlockBreakEvent e) {
		final Player player = e.getPlayer();

		if(DataHandler.isLimbo(player)) {
			e.setCancelled(Config.BLOCK_BREAK);
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onBlockPlace(BlockPlaceEvent e) {
		final Player player = e.getPlayer();

		if(DataHandler.isLimbo(player)) {
			e.setCancelled(Config.BLOCK_PLACE);
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onCommand(PlayerCommandPreprocessEvent e) {
		final Player player = e.getPlayer();

		if(DataHandler.isLimbo(player)) {
			for(int i = 0; i < Config.BLACKLISTED_COMMANDS.size(); i++) {
				if(e.getMessage().startsWith("/" + Config.BLACKLISTED_COMMANDS.get(i))) {
					e.setCancelled(true);
					DataHandler.sendMessage(player, Messages.placeholders(Messages.BLACKLISTED_COMMANDS, player.getName(), null, null, null));
					return;
				}
			}
		}
	}

	@EventHandler
	public void onPickup(PlayerPickupItemEvent e) {
		if(e.getPlayer() instanceof Player) {
			final Player player = e.getPlayer();
			if(DataHandler.isLimbo(player)) {
				e.setCancelled(Config.ITEM_PICKUP);
			}
		}
	}
}
