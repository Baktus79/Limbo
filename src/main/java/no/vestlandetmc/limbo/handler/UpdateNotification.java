package no.vestlandetmc.limbo.handler;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Getter;
import no.vestlandetmc.limbo.LimboPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URLConnection;

@SuppressWarnings("UnstableApiUsage")
public abstract class UpdateNotification extends BukkitRunnable {

	@Getter
	private static String projectSlug;
	@Getter
	private static String latestVersion = "";

	public UpdateNotification(String slug) {
		UpdateNotification.projectSlug = slug;
	}

	@Override
	public void run() {
		try {
			URI uri = new URI("https://api.modrinth.com/v2/project/" + projectSlug + "/version");
			URLConnection con = uri.toURL().openConnection();
			con.setRequestProperty("User-Agent", "YourPluginName/1.0");

			try (BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
				JsonElement element = JsonParser.parseReader(reader);

				if (!element.isJsonArray()) return;

				JsonArray array = element.getAsJsonArray();
				for (JsonElement e : array) {
					JsonObject version = e.getAsJsonObject();

					if (!version.get("version_type").getAsString().equalsIgnoreCase("release")) {
						continue;
					}

					latestVersion = version.get("version_number").getAsString();
					break;
				}
			}

			if (isUpdateAvailable()) {
				onUpdateAvailable();
			}

		} catch (Exception e) {
			LimboPlugin.getPlugin().getLogger().severe(e.getMessage());
		}
	}

	public abstract void onUpdateAvailable();

	public static boolean isUpdateAvailable() {
		return !latestVersion.equals(LimboPlugin.getPlugin().getPluginMeta().getVersion());
	}

	public static String getCurrentVersion() {
		return LimboPlugin.getPlugin().getPluginMeta().getVersion();
	}
}
