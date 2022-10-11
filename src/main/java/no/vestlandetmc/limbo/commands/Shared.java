package no.vestlandetmc.limbo.commands;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import no.vestlandetmc.limbo.config.Messages;
import no.vestlandetmc.limbo.handler.DataHandler;
import no.vestlandetmc.limbo.handler.MessageHandler;

public class Shared {

	private final Player cplayer;
	private final DataHandler data = new DataHandler();

	public Shared(Player player) {
		this.cplayer = player;
	}

	public boolean playerCheck(OfflinePlayer player, String name) {
		if(player.isOnline() && player.getPlayer().hasPermission("limbo.bypass")) {
			MessageHandler.sendMessage(this.cplayer, Messages.placeholders(Messages.PLAYER_BYPASS, player.getName(), this.cplayer.getName(), null, null));
			return false;
		}

		if(this.data.isLimbo(player.getUniqueId())) {
			MessageHandler.sendMessage(this.cplayer, Messages.placeholders(Messages.PLAYER_EXIST_IN_LIMBO, player.getName(), this.cplayer.getName(), null, null));
			return false;
		}

		return true;
	}
}
