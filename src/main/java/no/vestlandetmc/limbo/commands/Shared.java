package no.vestlandetmc.limbo.commands;

import no.vestlandetmc.limbo.LimboPlugin;
import no.vestlandetmc.limbo.config.Messages;
import no.vestlandetmc.limbo.handler.MessageHandler;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Objects;

public class Shared {

	private final Player cplayer;

	public Shared(Player player) {
		this.cplayer = player;
	}

	public boolean playerCheck(OfflinePlayer player) {
		if (player.isOnline() && Objects.requireNonNull(player.getPlayer()).hasPermission("limbo.bypass")) {
			MessageHandler.sendMessage(this.cplayer, Messages.placeholders(Messages.PLAYER_BYPASS, player.getName(), this.cplayer.getName(), null, null));
			return false;
		}

		if (LimboPlugin.getDataManager().isLimbo(player.getUniqueId())) {
			MessageHandler.sendMessage(this.cplayer, Messages.placeholders(Messages.PLAYER_EXIST_IN_LIMBO, player.getName(), this.cplayer.getName(), null, null));
			return false;
		}

		return true;
	}
}
