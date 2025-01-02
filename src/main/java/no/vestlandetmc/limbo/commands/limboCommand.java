package no.vestlandetmc.limbo.commands;

import no.vestlandetmc.limbo.LimboPlugin;
import no.vestlandetmc.limbo.config.Config;
import no.vestlandetmc.limbo.config.Messages;
import no.vestlandetmc.limbo.handler.Announce;
import no.vestlandetmc.limbo.handler.Callback;
import no.vestlandetmc.limbo.handler.DataHandler;
import no.vestlandetmc.limbo.handler.MessageHandler;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class limboCommand implements CommandExecutor {

	private final DataHandler data = new DataHandler();

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player cPlayer)) {
			MessageHandler.sendConsole("&4This cannot be used from the console. You must be a player to use this command!");
			return true;
		}

		final Shared share = new Shared(cPlayer);

		if (args.length == 0) {
			helpCommand.onCommand(cPlayer);
			return true;
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
			return true;
		} else if (args[0].equals("reload")) {
			if (sender.hasPermission("limbo.admin")) {
				LimboPlugin.getPlugin().reload();
				MessageHandler.sendMessage(cPlayer, Messages.RELOAD);
				return true;
			} else {
				MessageHandler.sendMessage(cPlayer, Messages.placeholders(Messages.MISSING_PERMISSION, null, sender.getName(), null, null));
				return true;
			}
		}

		final Callback<OfflinePlayer> callback = player -> {
			if (!share.playerCheck(player, argsArray.get(0))) {
				return;
			}

			if (player.isOnline()) {
				if (Config.VISIBLE) {
					for (final Player p : Bukkit.getOnlinePlayers()) {
						player.getPlayer().hidePlayer(LimboPlugin.getPlugin(), p);
					}
				}
			}

			final long timestamp = System.currentTimeMillis();
			this.data.setPlayer(player.getUniqueId(), cPlayer.getUniqueId(), timestamp, -1L, DataHandler.reason(argsArray, false));
			Announce.limboAnnounce(player, cPlayer, DataHandler.reason(argsArray, false), silence);

		};

		final Runnable task = () -> {
			final OfflinePlayer player = Bukkit.getOfflinePlayer(argsArray.get(0));
			Bukkit.getScheduler().runTask(LimboPlugin.getPlugin(), () -> callback.execute(player));
		};

		this.data.runAsync(task);

		return true;
	}
}
