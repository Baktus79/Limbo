package no.vestlandetmc.limbo.commands;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import no.vestlandetmc.limbo.LimboPlugin;
import no.vestlandetmc.limbo.config.Messages;
import no.vestlandetmc.limbo.database.SQLHandler;
import no.vestlandetmc.limbo.handler.DataHandler;
import no.vestlandetmc.limbo.handler.MessageHandler;
import no.vestlandetmc.limbo.handler.Permissions;
import no.vestlandetmc.limbo.obj.CachePlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

@SuppressWarnings("UnstableApiUsage")
public class limbolistCommand implements BasicCommand {

	private final DataHandler data;
	private final SQLHandler sql;

	public limbolistCommand(DataHandler data, SQLHandler sql) {
		this.data = data;
		this.sql = sql;
	}

	@Override
	public void execute(@NotNull CommandSourceStack commandSourceStack, String @NotNull [] args) {
		if (!(commandSourceStack.getSender() instanceof Player player)) {
			MessageHandler.sendConsole("&4This cannot be used from the console. You must be a player to use this command!");
			return;
		}

		if (!player.hasPermission("limbo.limbolist")) {
			MessageHandler.sendMessage(player, Messages.placeholders(Messages.MISSING_PERMISSION, null, player.getName(), null, null));
			return;
		}

		MessageHandler.sendMessage(player, Messages.placeholders(Messages.LIST_HEADER, null, player.getName(), null, null));

		int pageNumber = 1;
		if (args.length != 0 && DataHandler.isInt(args[0])) {
			pageNumber = Integer.parseInt(args[0]);
		}

		final int finalPageNumber = pageNumber;

		final Runnable task = () -> {
			LinkedHashMap<UUID, CachePlayer> list;
			try {
				list = sql.getAll();
			} catch (final SQLException e) {
				LimboPlugin.getPlugin().getLogger().severe(e.getMessage());
				return;
			}

			if (list == null || list.isEmpty()) {
				MessageHandler.sendMessage(player, Messages.placeholders(Messages.NO_PLAYERS_LIMBO, null, player.getName(), null, null));
				return;
			}

			final List<UUID> indexList = new ArrayList<>(list.keySet());
			final int totalPage = (int) Math.ceil((double) list.size() / 5);

			int page = finalPageNumber;
			if (page <= 0) page = 1;
			if (page > totalPage) page = totalPage;

			int countTo = 5 * page;
			int countFrom = countTo - 5;

			final SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm");

			for (int i = 0; i < list.size(); i++) {
				if (i >= countTo) break;
				if (i >= countFrom) {
					final CachePlayer cache = list.get(indexList.get(i));

					long release = 0;
					String releaseMessage = Messages.PERMANENT;
					final String staff = Bukkit.getOfflinePlayer(cache.getStaffUUID()).getName();
					final String playerName = cache.getName();
					final String reason = cache.getReason();

					if (cache.getExpire() != -1) {
						release = cache.getExpire();
						releaseMessage = Messages.TEMPORARY;
					}

					final String reasonHover = Messages.placeholders(Messages.REASON_HOVER, playerName, staff, format.format(release), reason);
					final String expireHover = Messages.placeholders(Messages.PLACED_BY, playerName, staff, format.format(release), reason);
					final String textHover = Messages.placeholders(releaseMessage, playerName, staff, format.format(release), reason);
					final String timeHover = Messages.placeholders(Messages.TIME_PLACED, playerName, staff, format.format(cache.getTimestamp()), reason);

					MessageHandler.hoverMessage(player,
							textHover,
							timeHover + "\n",
							expireHover + "\n",
							reasonHover);
				}
			}

			Component left = MessageHandler.colorize("&7<--- ");
			Component center = MessageHandler.colorize("&7[&a" + page + "/" + totalPage + "&7]");
			Component right = MessageHandler.colorize(" &7--->");

			if (page > 1) {
				left = left
						.hoverEvent(HoverEvent.showText(Component.text("Previous page")))
						.clickEvent(ClickEvent.runCommand("/limbolist " + (page - 1)));
			}

			if (page < totalPage) {
				right = right
						.hoverEvent(HoverEvent.showText(Component.text("Next page")))
						.clickEvent(ClickEvent.runCommand("/limbolist " + (page + 1)));
			}

			final Component nav = Component.empty().append(left).append(center).append(right);
			player.sendMessage(nav);
		};

		this.data.runAsync(task);
	}

	@Override
	public @NotNull Collection<String> suggest(@NotNull CommandSourceStack commandSourceStack, String @NotNull [] args) {
		return BasicCommand.super.suggest(commandSourceStack, args);
	}

	@Override
	public boolean canUse(@NotNull CommandSender sender) {
		return BasicCommand.super.canUse(sender);
	}

	@Override
	public @Nullable String permission() {
		return Permissions.LIMBOLIST.getName();
	}
}