package no.vestlandetmc.limbo.commands;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import no.vestlandetmc.limbo.config.Messages;
import no.vestlandetmc.limbo.database.SQLHandler;
import no.vestlandetmc.limbo.handler.DataHandler;
import no.vestlandetmc.limbo.handler.MessageHandler;
import no.vestlandetmc.limbo.obj.CachePlayer;

public class limbolistCommand implements CommandExecutor {

	private final DataHandler data = new DataHandler();
	private final SQLHandler sql = new SQLHandler();

	int countTo = 5;
	int countFrom = 0;
	int number = 1;

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			MessageHandler.sendConsole("&4This cannot be used from the console. You must be a player to use this command!");
			return true;
		}

		final Player player = (Player) sender;

		if(!sender.hasPermission("limbo.limbolist")) {
			MessageHandler.sendMessage(player, Messages.placeholders(Messages.MISSING_PERMISSION, null, sender.getName(), null, null));
			return true;
		}

		MessageHandler.sendMessage(player, Messages.placeholders(Messages.LIST_HEADER, null, sender.getName(), null, null));

		if(args.length != 0) {
			if(DataHandler.isInt(args[0])) {
				this.number = Integer.parseInt(args[0]);
				this.countTo = 5 * number;
				this.countFrom = 5 * number - 5;
			}
			else {
				MessageHandler.sendMessage(player, Messages.placeholders(Messages.TYPE_VALID_NUMBER, null, sender.getName(), null, null));
				return true;
			}
		}

		final Runnable task = () -> {

			LinkedHashMap<UUID, CachePlayer> list = null;
			try { list = sql.getAll(); } catch (final SQLException e) { e.printStackTrace(); }
			final LinkedList<UUID> indexList = new LinkedList<>(list.keySet());

			if(list.isEmpty()) {
				MessageHandler.sendMessage(player, Messages.placeholders(Messages.NO_PLAYERS_LIMBO, null, sender.getName(), null, null));
				return;
			}

			final int totalPage = list.size() / 5 + 1;

			for(int i = 0; i < list.size(); i++) {
				if(this.number > totalPage || this.number == 0) {
					this.countTo = 5 * totalPage;
					this.countFrom = 5 * totalPage - 5;
					this.number = totalPage;
				}
				if(i >= this.countFrom) {
					final CachePlayer cache = list.get(indexList.get(i));

					long release = 0;
					String releaseMessage = Messages.PERMANENT;
					final String staff = Bukkit.getOfflinePlayer(cache.getStaffUUID()).getName();
					final String playerName = cache.getName();
					final String reason = cache.getReason();
					final SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm");

					if(cache.getExpire() != -1) {
						release = cache.getExpire();
						releaseMessage = Messages.TEMPORARY;
					}

					final String reasonHover = Messages.placeholders(Messages.REASON_HOVER, playerName, staff, format.format(release), reason);
					final String expireHover = Messages.placeholders(Messages.PLACED_BY, playerName, staff, format.format(release), reason);
					final String textHover = Messages.placeholders(releaseMessage, playerName, staff, format.format(release), reason);
					final String timeHover = Messages.placeholders(Messages.TIME_PLACED, playerName, staff, format.format(cache.getTimestamp()), reason);

					MessageHandler.hoverMessage(player,
							textHover,
							new Text(MessageHandler.colorize(timeHover) + "\n"),
							new Text(MessageHandler.colorize(expireHover) + "\n"),
							new Text(MessageHandler.colorize(reasonHover)));

					if(i == this.countTo) {
						sender.sendMessage(ChatColor.GRAY + "<--- [" + ChatColor.GREEN + this.number + "\\" + totalPage + ChatColor.GRAY +  "] --->");
						break;
					}
				}
				continue;
			}
			if(this.number == totalPage) {
				sender.sendMessage(ChatColor.GRAY + "<--- [" + ChatColor.GREEN + totalPage + "\\" + totalPage + ChatColor.GRAY +  "] --->");
			}

		};

		this.data.runAsync(task);

		return true;
	}

	public static void pagination(Player player, String command, int page, int totalPage) {
		final TextComponent back = new TextComponent(MessageHandler.colorize("&8<--- "));
		final TextComponent forward = new TextComponent(MessageHandler.colorize(" &8--->"));
		final TextComponent middle;

		if(page == totalPage) {
			middle = new TextComponent(MessageHandler.colorize("&8[&2" + totalPage + "\\" + totalPage + "&8]"));
		} else { middle = new TextComponent(MessageHandler.colorize("&8[&2" + page + "\\" + totalPage + "&8]")); }

		back.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command + " " + (page - 1)));
		forward.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command + " " + (page + 1)));

		final BaseComponent[] msg = new ComponentBuilder(back)
				.append(middle)
				.append(forward)
				.create();

		player.spigot().sendMessage(msg);

	}
}
