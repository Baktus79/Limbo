package no.vestlandetmc.Limbo.commands;

import java.text.SimpleDateFormat;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import no.vestlandetmc.Limbo.LimboPlugin;
import no.vestlandetmc.Limbo.config.Messages;
import no.vestlandetmc.Limbo.handler.DataHandler;

public class limbolistCommand implements CommandExecutor {

	int countTo = 5;
	int countFrom = 0;
	int number = 1;

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			LimboPlugin.getInstance().getServer().getConsoleSender().sendMessage(ChatColor.RED + "This cannot be used from the console. You must be a player to use this command!");
			return true;
		}

		if(!sender.hasPermission("limbo.limbolist")) {
			DataHandler.sendMessage((Player) sender, Messages.placeholders(Messages.MISSING_PERMISSION, null, sender.getName(), null, null));
			return true;
		}

		DataHandler.sendMessage((Player) sender, Messages.placeholders(Messages.LIST_HEADER, null, sender.getName(), null, null));

		if(LimboPlugin.getInstance().getDataFile().getKeys(false).toArray().length == 0) {
			DataHandler.sendMessage((Player) sender, Messages.placeholders(Messages.NO_PLAYERS_LIMBO, null, sender.getName(), null, null));
			return true;
		}

		if(args.length != 0) {
			if(DataHandler.isInt(args[0])) {
				this.number = Integer.parseInt(args[0]);
				this.countTo = 5 * number;
				this.countFrom = (5 * number) - 5;
			}
			else {
				DataHandler.sendMessage((Player) sender, Messages.placeholders(Messages.TYPE_VALID_NUMBER, null, sender.getName(), null, null));
				return true;
			}
		}
		final int totalPage = (LimboPlugin.getInstance().getDataFile().getKeys(false).size() / 5) + 1;

		for(int i = 0; i < LimboPlugin.getInstance().getDataFile().getKeys(false).toArray().length; i++) {
			if(this.number > totalPage || this.number == 0) {
				this.countTo = 5 * totalPage;
				this.countFrom = (5 * totalPage) - 5;
				this.number = totalPage;
			}
			if(i >= this.countFrom) {
				long release = 0L;
				String releaseMessage = Messages.PERMANENT;
				final long time = LimboPlugin.getInstance().getDataFile().getLong(LimboPlugin.getInstance().getDataFile().getKeys(false).toArray()[i] + ".time");
				final String limboBy = LimboPlugin.getInstance().getDataFile().getString(LimboPlugin.getInstance().getDataFile().getKeys(false).toArray()[i] + ".limboBy");
				final String reason = LimboPlugin.getInstance().getDataFile().getString(LimboPlugin.getInstance().getDataFile().getKeys(false).toArray()[i] + ".reason");
				final SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm");

				if(LimboPlugin.getInstance().getDataFile().contains(LimboPlugin.getInstance().getDataFile().getKeys(false).toArray()[i] + ".releasetime")) {
					release = LimboPlugin.getInstance().getDataFile().getLong(LimboPlugin.getInstance().getDataFile().getKeys(false).toArray()[i] + ".releasetime");
					releaseMessage = Messages.TEMPORARY;
				}

				final OfflinePlayer player = LimboPlugin.getInstance().getServer().getOfflinePlayer(UUID.fromString(LimboPlugin.getInstance().getDataFile().getKeys(false).toArray()[i].toString()));
				final OfflinePlayer staff = LimboPlugin.getInstance().getServer().getOfflinePlayer(UUID.fromString(limboBy));

				DataHandler.sendMessage((Player) sender, Messages.placeholders(Messages.PLACED_IN_LIMBO + " " + releaseMessage, player.getName(), staff.getName(), format.format(release), reason));
				DataHandler.sendMessage((Player) sender, Messages.placeholders(Messages.PLACED_IN_LIMBO_BY, player.getName(), staff.getName(), format.format(time), reason));

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
		return true;
	}
}
