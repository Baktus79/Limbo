package no.vestlandetmc.limbo.handler;

import no.vestlandetmc.limbo.LimboPlugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Driver;
import java.sql.DriverManager;

public class Library {

	private final String url;
	private final String file;
	private final String classPath;
	private final LimboPlugin plugin;

	public Library(String url, String file, String classPath, LimboPlugin plugin) {
		this.url = url;
		this.file = file;
		this.classPath = classPath;
		this.plugin = plugin;

		try {
			downloadLibIfNeeded();
			plugin.getLogger().info(this.file + " is downloaded and ready.");
		} catch (IOException e) {
			plugin.getLogger().severe("Could not download " + this.file + ": " + e.getMessage());
		}
	}

	private void downloadLibIfNeeded() throws IOException {
		final File libsDir = new File(this.plugin.getDataFolder(), "libs");
		final File file = new File(libsDir, this.file);

		if (!libsDir.exists()) {
			libsDir.mkdirs();
		}

		if (!file.exists()) {
			this.plugin.getLogger().info(this.file + " was not found. Downloading...");
			downloadFile(URI.create(this.url), file);
		}
	}

	private void downloadFile(URI uri, File destination) throws IOException {
		final URL url = uri.toURL();
		final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("GET");
		connection.setDoOutput(true);
		connection.connect();

		try (InputStream in = connection.getInputStream();
			 FileOutputStream out = new FileOutputStream(destination)) {

			byte[] buffer = new byte[1024];
			int bytesRead;
			while ((bytesRead = in.read(buffer)) != -1) {
				out.write(buffer, 0, bytesRead);
			}
		}
		this.plugin.getLogger().info("Successfully downloaded: " + this.file);
	}

	public void load() {
		try {
			final File libsDir = new File(plugin.getDataFolder(), "libs");
			final File driverFile = new File(libsDir, file);
			if (driverFile.exists()) {
				plugin.getLogger().info("Loading " + file + "...");

				final URL jdbcUrl = driverFile.toURI().toURL();
				URLClassLoader classLoader = new URLClassLoader(new URL[]{jdbcUrl}, plugin.getClass().getClassLoader());
				Class<?> driverClass = Class.forName(classPath, true, classLoader);
				DriverManager.registerDriver((Driver) driverClass.getDeclaredConstructor().newInstance());

				plugin.getLogger().info(file + " was successfully loaded.");
			}
		} catch (Exception e) {
			plugin.getLogger().severe("Failed to load " + file + ": " + e.getMessage());
		}
	}
}
