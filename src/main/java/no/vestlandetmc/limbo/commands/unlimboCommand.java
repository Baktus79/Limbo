package no.vestlandetmc.limbo.commands;

import no.vestlandetmc.limbo.LimboPlugin;
import no.vestlandetmc.limbo.config.Messages;
import no.vestlandetmc.limbo.database.SQLHandler;
import no.vestlandetmc.limbo.handler.Announce;
import no.vestlandetmc.limbo.handler.DataHandler;
import no.vestlandetmc.limbo.handler.MessageHandler;
import no.vestlandetmc.limbo.obj.CachePlayer;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class unlimboCommand implements CommandExecutor {

	private final DataHandler data = new DataHandler();
	private final SQLHandler sql = new SQLHandler();

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			MessageHandler.sendConsole("&4This cannot be used from the console. You must be a player to use this command!");
			return true;
		}

		if (!sender.hasPermission("limbo.unlimbo")) {
			MessageHandler.sendMessage((Player) sender, Messages.placeholders(Messages.MISSING_PERMISSION, null, sender.getName(), null, null));
			return true;
		}

		if (args.length == 0) {
			helpCommand.onCommand((Player) sender);
			return true;
		}

		@SuppressWarnings("deprecation") final Runnable task = () -> {
			boolean silence = false;
			final List<String> argsArray = new ArrayList<>(Arrays.asList(args));

			for (final String s : args) {
				if (s.equals("-s")) {
					silence = true;
					argsArray.remove("-s");
				}
			}

			final OfflinePlayer player = Bukkit.getOfflinePlayer(argsArray.get(0));

			if (player != null && player.hasPlayedBefore()) {
				CachePlayer cache = null;

				try {
					cache = sql.getPlayer(player.getUniqueId());
				} catch (final SQLException e) {
					e.printStackTrace();
				}

				if (cache != null) {
					if (player.isOnline()) {
						final Player oPlayer = Bukkit.getPlayer(argsArray.get(0));

						final Runnable syncTask = () -> {
							for (final Player onlinePlayers : Bukkit.getOnlinePlayers()) {
								oPlayer.getPlayer().showPlayer(LimboPlugin.getPlugin(), onlinePlayers);
							}
						};

						this.data.runSync(syncTask);
					}

					this.data.removePlayer(cache.getUniqueId());

					try {
						this.sql.deleteUser(cache.getUniqueId());
					} catch (final SQLException e) {
						e.printStackTrace();
					}

					Announce.unlimboAnnounce(player, (Player) sender, silence);

					return;
				}
			}

			MessageHandler.sendMessage((Player) sender, Messages.placeholders(Messages.PLAYER_NONEXIST_IN_LIMBO, argsArray.get(0), sender.getName(), null, null));

		};

		this.data.runAsync(task);

		return true;
	}

}
