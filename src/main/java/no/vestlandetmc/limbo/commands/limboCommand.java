package no.vestlandetmc.limbo.commands;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import no.vestlandetmc.limbo.LimboPlugin;
import no.vestlandetmc.limbo.config.Config;
import no.vestlandetmc.limbo.config.Messages;
import no.vestlandetmc.limbo.handler.*;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("UnstableApiUsage")
public class limboCommand implements BasicCommand {

	private final DataHandler data;

	public limboCommand(DataHandler data) {
		this.data = data;
	}

	@Override
	public void execute(CommandSourceStack commandSourceStack, String @NotNull [] args) {
		if (!(commandSourceStack.getSender() instanceof Player cPlayer)) {
			MessageHandler.sendConsole("&4This cannot be used from the console. You must be a player to use this command!");
			return;
		}

		final Shared share = new Shared(cPlayer);

		if (args.length == 0) {
			helpCommand.onCommand(cPlayer);
			return;
		}

		boolean s = false;
		final List<String> argsArray = new ArrayList<>(Arrays.asList(args));

		for (final String str : args) {
			if (str.equals("-s")) {
				s = true;
				argsArray.remove("-s");
			}
		}

		final boolean silence = s;

		if (args[0].equals("help")) {
			helpCommand.onCommand(cPlayer);
			return;
		} else if (args[0].equals("reload")) {
			if (cPlayer.hasPermission("limbo.admin")) {
				LimboPlugin.getPlugin().reload();
				MessageHandler.sendMessage(cPlayer, Messages.RELOAD);
			} else {
				MessageHandler.sendMessage(cPlayer, Messages.placeholders(Messages.MISSING_PERMISSION, null, cPlayer.getName(), null, null));
			}
			return;
		}

		final Callback<OfflinePlayer> callback = player -> {
			if (!share.playerCheck(player)) {
				return;
			}

			if (player.isOnline()) {
				if (Config.VISIBLE) {
					for (final Player p : Bukkit.getOnlinePlayers()) {
						Objects.requireNonNull(player.getPlayer()).hidePlayer(LimboPlugin.getPlugin(), p);
					}
				}
			}

			final long timestamp = System.currentTimeMillis();
			this.data.setPlayer(player.getUniqueId(), cPlayer.getUniqueId(), timestamp, -1L, DataHandler.reason(argsArray, false));
			Announce.limboAnnounce(player, cPlayer, DataHandler.reason(argsArray, false), silence);

		};

		final Runnable task = () -> {
			final OfflinePlayer player = Bukkit.getOfflinePlayer(argsArray.getFirst());
			if (player.getName() == null) {
				MessageHandler.sendMessage(cPlayer, Messages.placeholders(Messages.PLAYER_NONEXIST, argsArray.getFirst(), null, null, null));
				return;
			}

			Bukkit.getScheduler().runTask(LimboPlugin.getPlugin(), () -> callback.execute(player));
		};

		this.data.runAsync(task);
	}

	@Override
	public @NotNull Collection<String> suggest(@NotNull CommandSourceStack commandSourceStack, String @NotNull [] args) {
		String partial = args.length > 0 ? args[args.length - 1].toLowerCase() : "";
		return Bukkit.getOnlinePlayers().stream()
				.map(Player::getName)
				.filter(name -> name.toLowerCase().startsWith(partial))
				.collect(Collectors.toList());
	}

	@Override
	public boolean canUse(@NotNull CommandSender sender) {
		return BasicCommand.super.canUse(sender);
	}

	@Override
	public @Nullable String permission() {
		return Permissions.LIMBO.getName();
	}
}
