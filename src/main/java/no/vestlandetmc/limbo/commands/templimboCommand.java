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

public class templimboCommand implements CommandExecutor {

	private final DataHandler data = new DataHandler();

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player cPlayer)) {
			MessageHandler.sendConsole("&4This cannot be used from the console. You must be a player to use this command!");
			return true;
		}

		final Shared share = new Shared(cPlayer);

		if (!sender.hasPermission("limbo.templimbo")) {
			MessageHandler.sendMessage(cPlayer, Messages.placeholders(Messages.MISSING_PERMISSION, null, sender.getName(), null, null));
			return true;
		}

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

		final Callback<OfflinePlayer> callback = player -> {
			if (!share.playerCheck(player, argsArray.get(0))) {
				return;
			}

			if (args.length < 2 || !DataHandler.isInt(argsArray.get(1).replaceAll("\\D", ""))) {
				MessageHandler.sendMessage(cPlayer, Messages.placeholders(Messages.TYPE_VALID_NUMBER, player.getName(), sender.getName(), null, null));
				MessageHandler.sendMessage(cPlayer, Messages.placeholders(Messages.CORRECT_FORMAT, player.getName(), sender.getName(), null, null));
				return;
			} else if (DataHandler.getTime(argsArray.get(1)) == 0L) {
				MessageHandler.sendMessage(cPlayer, Messages.placeholders(Messages.CORRECT_FORMAT, player.getName(), sender.getName(), null, null));
				return;
			}

			if (player.isOnline()) {
				if (Config.VISIBLE) {
					for (final Player p : Bukkit.getOnlinePlayers()) {
						player.getPlayer().hidePlayer(LimboPlugin.getPlugin(), p);
					}
				}
			}

			final long current = System.currentTimeMillis();
			final long expire = DataHandler.getTime(argsArray.get(1)) + current;

			this.data.setPlayer(player.getUniqueId(), cPlayer.getUniqueId(), current, expire, DataHandler.reason(argsArray, true));
			Announce.templimboAnnounce(player, cPlayer, DataHandler.reason(argsArray, true), expire, silence);
		};

		final Runnable task = () -> {
			final OfflinePlayer player = Bukkit.getOfflinePlayer(argsArray.get(0));
			Bukkit.getScheduler().runTask(LimboPlugin.getPlugin(), () -> callback.execute(player));
		};

		this.data.runAsync(task);

		return true;
	}
}