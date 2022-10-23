package no.vestlandetmc.limbo.handler;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import no.vestlandetmc.limbo.config.Config;

public class DiscordManager {

	private static boolean discordEnabled = false;

	public static void sendLimbo(String message) {
		if(Config.DISCORDSRV_ENABLED && discordEnabled) {
			final TextChannel textChannel = DiscordSRV.getPlugin().getMainGuild().getTextChannelById(Config.DISCORD_CHANNEL);
			textChannel.sendMessage(message).queue();
		}
	}

	public static void discordEnabled() {
		discordEnabled = true;
	}

}
