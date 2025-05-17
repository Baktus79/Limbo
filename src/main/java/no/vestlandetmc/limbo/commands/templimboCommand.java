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
public class templimboCommand implements BasicCommand {

	private final DataHandler data;

	public templimboCommand(DataHandler data) {
		this.data = data;
	}

	@Override
	public void execute(@NotNull CommandSourceStack commandSourceStack, String @NotNull [] args) {
		if (!(commandSourceStack.getSender() instanceof Player cPlayer)) {
			MessageHandler.sendConsole("&4This cannot be used from the console. You must be a player to use this command!");
			return;
		}

		final Shared share = new Shared(cPlayer);

		if (!cPlayer.hasPermission("limbo.templimbo")) {
			MessageHandler.sendMessage(cPlayer, Messages.placeholders(Messages.MISSING_PERMISSION, null, cPlayer.getName(), null, null));
			return;
		}

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

		final Callback<OfflinePlayer> callback = player -> {
			if (!share.playerCheck(player)) {
				return;
			}

			if (args.length < 2 || !DataHandler.isInt(argsArray.get(1).replaceAll("\\D", ""))) {
				MessageHandler.sendMessage(cPlayer, Messages.placeholders(Messages.TYPE_VALID_NUMBER, player.getName(), cPlayer.getName(), null, null));
				MessageHandler.sendMessage(cPlayer, Messages.placeholders(Messages.CORRECT_FORMAT, player.getName(), cPlayer.getName(), null, null));
				return;
			} else if (DataHandler.getTime(argsArray.get(1)) == 0L) {
				MessageHandler.sendMessage(cPlayer, Messages.placeholders(Messages.CORRECT_FORMAT, player.getName(), cPlayer.getName(), null, null));
				return;
			}

			if (player.isOnline()) {
				if (Config.VISIBLE) {
					for (final Player p : Bukkit.getOnlinePlayers()) {
						Objects.requireNonNull(player.getPlayer()).hidePlayer(LimboPlugin.getPlugin(), p);
					}
				}
			}

			final long current = System.currentTimeMillis();
			final long expire = DataHandler.getTime(argsArray.get(1)) + current;

			this.data.setPlayer(player.getUniqueId(), cPlayer.getUniqueId(), current, expire, DataHandler.reason(argsArray, true));
			Announce.templimboAnnounce(player, cPlayer, DataHandler.reason(argsArray, true), expire, silence);
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
		return Permissions.TEMPLIMBO.getName();
	}
}