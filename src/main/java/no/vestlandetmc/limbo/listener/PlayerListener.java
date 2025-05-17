package no.vestlandetmc.limbo.listener;

import no.vestlandetmc.limbo.LimboPlugin;
import no.vestlandetmc.limbo.config.Config;
import no.vestlandetmc.limbo.config.Messages;
import no.vestlandetmc.limbo.handler.Callback;
import no.vestlandetmc.limbo.handler.DataHandler;
import no.vestlandetmc.limbo.handler.MessageHandler;
import no.vestlandetmc.limbo.handler.UpdateNotification;
import no.vestlandetmc.limbo.obj.CachePlayer;
import org.bukkit.Bukkit;
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

import java.util.Map.Entry;
import java.util.Objects;
import java.util.UUID;

public class PlayerListener implements Listener {

	private final DataHandler data;

	public PlayerListener(DataHandler data) {
		this.data = data;
	}

	@EventHandler
	public void playerJoin(PlayerJoinEvent p) {
		final Player player = p.getPlayer();

		final Callback<Boolean> callback = b -> {
			final Runnable task = () -> {
				if (b) {
					if (Config.VISIBLE) {
						for (final Player limboP : Bukkit.getOnlinePlayers()) {
							player.hidePlayer(LimboPlugin.getPlugin(), limboP);
						}
					}
				}

				for (final Entry<UUID, CachePlayer> lp : this.data.getAllPlayers().entrySet()) {
					final Player playerInLimbo = Bukkit.getPlayer(lp.getValue().getUniqueId());
					Objects.requireNonNull(playerInLimbo.getPlayer()).hidePlayer(LimboPlugin.getPlugin(), player);
				}
			};

			this.data.runSync(task);
		};

		this.data.getDBPlayer(player.getUniqueId(), callback);

		if (player.isOp()) {
			if (UpdateNotification.isUpdateAvailable()) {
				MessageHandler.sendMessage(player,
						"&a------------------------------------",
						"&aLimbo is outdated. Update is available!",
						"&aYour version is &l" + UpdateNotification.getCurrentVersion() + " &aand can be updated to version &l" + UpdateNotification.getLatestVersion(),
						"&aDownload the update at https://modrinth.com/plugin/" + UpdateNotification.getProjectSlug(),
						"&a------------------------------------"
				);
			}
		}

	}

	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent e) {
		this.data.removePlayerCache(e.getPlayer().getUniqueId());
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockBreak(BlockBreakEvent e) {
		final Player player = e.getPlayer();
		if (player.hasPermission("limbo.bypass")) {
			return;
		}

		if (this.data.isLimbo(player.getUniqueId())) {
			e.setCancelled(Config.BLOCK_BREAK);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockPlace(BlockPlaceEvent e) {
		final Player player = e.getPlayer();
		if (player.hasPermission("limbo.bypass")) {
			return;
		}

		if (this.data.isLimbo(player.getUniqueId())) {
			e.setCancelled(Config.BLOCK_PLACE);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onCommand(PlayerCommandPreprocessEvent e) {
		final Player player = e.getPlayer();
		if (player.hasPermission("limbo.bypass")) {
			return;
		}

		if (this.data.isLimbo(player.getUniqueId())) {
			for (int i = 0; i < Config.BLACKLISTED_COMMANDS.size(); i++) {
				if (e.getMessage().startsWith("/" + Config.BLACKLISTED_COMMANDS.get(i))) {
					e.setCancelled(true);
					MessageHandler.sendMessage(player, Messages.placeholders(Messages.BLACKLISTED_COMMANDS, player.getName(), null, null, null));
					return;
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPickup(EntityPickupItemEvent e) {
		if (e.getEntity() instanceof Player player) {
			if (player.hasPermission("limbo.bypass")) {
				return;
			}
			if (this.data.isLimbo(player.getUniqueId())) {
				e.setCancelled(Config.ITEM_PICKUP);
			}
		}
	}
}
