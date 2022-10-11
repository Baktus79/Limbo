package no.vestlandetmc.limbo.listener;

import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import no.vestlandetmc.limbo.LimboPlugin;
import no.vestlandetmc.limbo.config.Config;
import no.vestlandetmc.limbo.config.Messages;
import no.vestlandetmc.limbo.handler.Callback;
import no.vestlandetmc.limbo.handler.DataHandler;
import no.vestlandetmc.limbo.handler.MessageHandler;
import no.vestlandetmc.limbo.handler.UpdateNotification;
import no.vestlandetmc.limbo.obj.CachePlayer;

public class PlayerListener implements Listener {

	private final DataHandler data = new DataHandler();

	@EventHandler
	public void playerJoin(PlayerJoinEvent p) {
		final Player player = p.getPlayer();

		final Callback<Boolean> callback = b -> {
			final Runnable task = () -> {
				if(b) {
					if(Config.VISIBLE) {
						for (final Player limboP : Bukkit.getOnlinePlayers()) {
							player.hidePlayer(LimboPlugin.getInstance() ,limboP);
						}
					}
				}

				for(final Entry<UUID, CachePlayer> lp : this.data.getAllPlayers().entrySet()) {
					final Player playerInLimbo = Bukkit.getPlayer(lp.getValue().getUniqueId());
					playerInLimbo.getPlayer().hidePlayer(LimboPlugin.getInstance() ,player);
				}
			};

			this.data.runSync(task);
		};

		this.data.getDBPlayer(player.getUniqueId(), callback);

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

	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent e) {
		this.data.removePlayer(e.getPlayer().getUniqueId());
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onBlockBreak(BlockBreakEvent e) {
		final Player player = e.getPlayer();
		if(player.hasPermission("limbo.bypass")) { return; }

		if(this.data.isLimbo(player.getUniqueId())) {
			e.setCancelled(Config.BLOCK_BREAK);
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onBlockPlace(BlockPlaceEvent e) {
		final Player player = e.getPlayer();
		if(player.hasPermission("limbo.bypass")) { return; }

		if(this.data.isLimbo(player.getUniqueId())) {
			e.setCancelled(Config.BLOCK_PLACE);
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onCommand(PlayerCommandPreprocessEvent e) {
		final Player player = e.getPlayer();
		if(player.hasPermission("limbo.bypass")) { return; }

		if(this.data.isLimbo(player.getUniqueId())) {
			for(int i = 0; i < Config.BLACKLISTED_COMMANDS.size(); i++) {
				if(e.getMessage().startsWith("/" + Config.BLACKLISTED_COMMANDS.get(i))) {
					e.setCancelled(true);
					MessageHandler.sendMessage(player, Messages.placeholders(Messages.BLACKLISTED_COMMANDS, player.getName(), null, null, null));
					return;
				}
			}
		}
	}

	@EventHandler
	public void onPickup(EntityPickupItemEvent e) {
		if(e.getEntity() instanceof Player) {
			final Player player = (Player) e.getEntity();
			if(player.hasPermission("limbo.bypass")) { return; }
			if(this.data.isLimbo(player.getUniqueId())) {
				e.setCancelled(Config.ITEM_PICKUP);
			}
		}
	}
}
