package no.vestlandetmc.limbo.handler;

import no.vestlandetmc.limbo.LimboPlugin;
import no.vestlandetmc.limbo.config.Config;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class DiscordManager {

	public static void sendLimbo(String message) {
		if (Config.DISCORD_ENABLED) {
			sendWebhook(Config.DISCORD_WEBHOOK_URL, message);
		}
	}

	private static void sendWebhook(String webhookUrl, String content) {
		try {
			URL url = URI.create(webhookUrl).toURL();
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setDoOutput(true);

			String payload = "{\"content\":\"" + escapeJson(content) + "\"}";

			try (OutputStream os = connection.getOutputStream()) {
				byte[] input = payload.getBytes(StandardCharsets.UTF_8);
				os.write(input, 0, input.length);
			}

			int responseCode = connection.getResponseCode();
			if (responseCode != 204) {
				LimboPlugin.getPlugin().getLogger().warning("Failed to send webhook: HTTP " + responseCode);
			}
		} catch (Exception e) {
			LimboPlugin.getPlugin().getLogger().severe(e.getMessage());
		}
	}

	private static String escapeJson(String text) {
		return text
				.replace("\\", "\\\\")
				.replace("\"", "\\\"")
				.replace("\n", "\\n")
				.replace("\r", "");
	}

}
