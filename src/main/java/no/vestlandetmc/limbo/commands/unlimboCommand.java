package no.vestlandetmc.limbo.commands;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import no.vestlandetmc.limbo.LimboPlugin;
import no.vestlandetmc.limbo.config.Messages;
import no.vestlandetmc.limbo.database.SQLHandler;
import no.vestlandetmc.limbo.handler.Announce;
import no.vestlandetmc.limbo.handler.DataHandler;
import no.vestlandetmc.limbo.handler.MessageHandler;
import no.vestlandetmc.limbo.handler.Permissions;
import no.vestlandetmc.limbo.obj.CachePlayer;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@SuppressWarnings("UnstableApiUsage")
public class unlimboCommand implements BasicCommand {

	private final DataHandler data;
	private final SQLHandler sql;

	public unlimboCommand(DataHandler data, SQLHandler sql) {
		this.data = data;
		this.sql = sql;
	}

	@Override
	public void execute(@NotNull CommandSourceStack commandSourceStack, String @NotNull [] args) {
		if (!(commandSourceStack.getSender() instanceof Player player)) {
			MessageHandler.sendConsole("&4This cannot be used from the console. You must be a player to use this command!");
			return;
		}

		if (!player.hasPermission("limbo.unlimbo")) {
			MessageHandler.sendMessage(player, Messages.placeholders(Messages.MISSING_PERMISSION, null, player.getName(), null, null));
			return;
		}

		if (args.length == 0) {
			helpCommand.onCommand(player);
			return;
		}

		final Runnable task = () -> {
			boolean silence = Arrays.asList(args).contains("-s");
			final List<String> argsArray = Arrays.stream(args).filter(arg -> !arg.equals("-s")).toList();
			final OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayerIfCached(argsArray.getFirst());

			if (offlinePlayer != null) {
				CachePlayer cache = null;

				try {
					cache = sql.getPlayer(offlinePlayer.getUniqueId());
				} catch (final SQLException e) {
					LimboPlugin.getPlugin().getLogger().severe(e.getMessage());
				}

				if (cache != null) {
					if (offlinePlayer.isOnline()) {
						final Player oPlayer = offlinePlayer.getPlayer();

						final Runnable syncTask = () -> {
							for (final Player onlinePlayers : Bukkit.getOnlinePlayers()) {
								Objects.requireNonNull(oPlayer).showPlayer(LimboPlugin.getPlugin(), onlinePlayers);
							}
						};

						this.data.runSync(syncTask);
					}

					this.data.removePlayer(cache.getUniqueId());
					Announce.unlimboAnnounce(offlinePlayer, player, silence);

					return;
				}
			}

			MessageHandler.sendMessage(player, Messages.placeholders(Messages.PLAYER_NONEXIST_IN_LIMBO, argsArray.getFirst(), player.getName(), null, null));

		};

		this.data.runAsync(task);
	}

	@Override
	public @NotNull Collection<String> suggest(@NotNull CommandSourceStack commandSourceStack, String @NotNull [] args) {
		String partial = args.length > 0 ? args[args.length - 1].toLowerCase() : "";
		return data.getCachePlayerNames().stream()
				.filter(name -> name.toLowerCase().startsWith(partial))
				.collect(Collectors.toList());
	}

	@Override
	public boolean canUse(@NotNull CommandSender sender) {
		return BasicCommand.super.canUse(sender);
	}

	@Override
	public @Nullable String permission() {
		return Permissions.UNLIMBO.getName();
	}
}
