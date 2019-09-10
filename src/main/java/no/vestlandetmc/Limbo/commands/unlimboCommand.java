package no.vestlandetmc.Limbo.commands;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import no.vestlandetmc.Limbo.LimboPlugin;
import no.vestlandetmc.Limbo.config.Messages;
import no.vestlandetmc.Limbo.handler.Announce;
import no.vestlandetmc.Limbo.handler.DataHandler;

public class unlimboCommand implements CommandExecutor {

	File file;

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			LimboPlugin.getInstance().getServer().getConsoleSender().sendMessage(ChatColor.RED + "This cannot be used from the console. You must be a player to use this command!");
			return true;
		}

		if(!sender.hasPermission("limbo.unlimbo")) {
			DataHandler.sendMessage((Player) sender, Messages.placeholders(Messages.MISSING_PERMISSION, null, sender.getName(), null, null));
			return true;
		}

		if (args.length == 0) {
			helpCommand.onCommand((Player) sender);
			return true;
		}

		if(!(LimboPlugin.getInstance().getDataFile().getKeys(false).toArray().length == 0)) {
			for(final String p : LimboPlugin.getInstance().getDataFile().getKeys(false)) {
				final OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(p));
				if(player.getName().equalsIgnoreCase(args[0])) {
					if(player.isOnline()) {
						final Player oPlayer = Bukkit.getPlayer(args[0]);
						for (final Player onlinePlayers : Bukkit.getOnlinePlayers()) {
							oPlayer.getPlayer().showPlayer(onlinePlayers);
						}
					}
					LimboPlugin.getInstance().getDataFile().set(player.getUniqueId().toString(), null);
					Announce.unlimboAnnounce(player, (Player) sender);
					try {
						file = new File(LimboPlugin.getInstance().getDataFolder(), "data.dat");
						LimboPlugin.getInstance().getDataFile().save(file);
					} catch (final IOException e) {
						e.printStackTrace();
					}
					return true;
				}
			}
		}

		DataHandler.sendMessage((Player) sender, Messages.placeholders(Messages.PLAYER_NONEXIST_IN_LIMBO, args[0], sender.getName(), null, null));
		return true;
	}

}
