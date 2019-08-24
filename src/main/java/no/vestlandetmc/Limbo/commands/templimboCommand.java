package no.vestlandetmc.Limbo.commands;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import no.vestlandetmc.Limbo.LimboPlugin;
import no.vestlandetmc.Limbo.config.Config;
import no.vestlandetmc.Limbo.config.Messages;
import no.vestlandetmc.Limbo.handler.Announce;
import no.vestlandetmc.Limbo.handler.DataHandler;

public class templimboCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			LimboPlugin.getInstance().getServer().getConsoleSender().sendMessage(ChatColor.RED + "This cannot be used from the console. You must be a player to use this command!");
			return true;
		}

		final Player cPlayer = (Player) sender;

		if(!sender.hasPermission("limbo.templimbo")) {
			DataHandler.sendMessage(cPlayer, Messages.placeholders(Messages.MISSING_PERMISSION, null, sender.getName(), null, null));
			return true;
		}

		if (args.length == 0) {
			helpCommand.onCommand(cPlayer);
			return true;
		}

		final Player player = Bukkit.getPlayer(args[0]);

		if (args[0].equals("help")) {
			helpCommand.onCommand(cPlayer);
			return true;
		} else if(player == null) {
			DataHandler.sendMessage(cPlayer, Messages.placeholders(Messages.PLAYER_NOT_ONLINE, args[0], sender.getName(), null, null));
			return true;
		} else if(player.hasPermission("limbo.bypass")) {
			DataHandler.sendMessage(cPlayer, Messages.placeholders(Messages.PLAYER_BYPASS, player.getName(), sender.getName(), null, null));
			return true;
		} else if(DataHandler.isLimbo(player)) {
			DataHandler.sendMessage(cPlayer, Messages.placeholders(Messages.PLAYER_EXIST_IN_LIMBO, player.getName(), sender.getName(), null, null));
			return true;
		}

		if(args.length < 2 || !DataHandler.isInt(args[1].replaceAll("\\D", ""))) {
			DataHandler.sendMessage(cPlayer, Messages.placeholders(Messages.TYPE_VALID_NUMBER, player.getName(), sender.getName(), null, null));
			DataHandler.sendMessage(cPlayer, Messages.placeholders(Messages.CORRECT_FORMAT, player.getName(), sender.getName(), null, null));
			return true;
		} else if(DataHandler.getTime(args[1]) == 0L) {
			DataHandler.sendMessage(cPlayer, Messages.placeholders(Messages.CORRECT_FORMAT, player.getName(), sender.getName(), null, null));
			return true;
		}

		if(Config.VISIBLE) {
			for (final Player p : Bukkit.getOnlinePlayers()) {
				player.hidePlayer(LimboPlugin.getInstance(), p);
			}
		}

		final Date now = new Date();
		final long unixTime = now.getTime();

		LimboPlugin.getInstance().getDataFile().set(player.getUniqueId().toString() + ".limbo", true);
		LimboPlugin.getInstance().getDataFile().set(player.getUniqueId().toString() + ".time", unixTime);
		LimboPlugin.getInstance().getDataFile().set(player.getUniqueId().toString() + ".limboBy", cPlayer.getUniqueId().toString());
		LimboPlugin.getInstance().getDataFile().set(player.getUniqueId().toString() + ".reason", DataHandler.reason(args));
		LimboPlugin.getInstance().getDataFile().set(player.getUniqueId().toString() + ".releasetime", DataHandler.getTime(args[1]) + unixTime);

		Announce.templimboAnnounce(player, cPlayer, DataHandler.reason(args), DataHandler.getTime(args[1]) + unixTime);

		try {
			final File file = new File(LimboPlugin.getInstance().getDataFolder(), "data.dat");
			LimboPlugin.getInstance().getDataFile().save(file);
		} catch (final IOException e) {
			e.printStackTrace();
		}

		return true;
	}
}