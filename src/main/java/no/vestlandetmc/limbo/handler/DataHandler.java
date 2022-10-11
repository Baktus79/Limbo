package no.vestlandetmc.limbo.handler;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import no.vestlandetmc.limbo.LimboPlugin;
import no.vestlandetmc.limbo.config.Messages;
import no.vestlandetmc.limbo.database.SQLHandler;
import no.vestlandetmc.limbo.obj.CachePlayer;

public class DataHandler {

	private final SQLHandler sql = new SQLHandler();
	private static HashMap<UUID, CachePlayer> LIMBO_CACHE = new HashMap<>();

	public DataHandler() {

	}

	/*
	 * Callback<Boolean> callback = new Callback<Boolean>() {
	 *     public void execute(Boolean b) {
	 *
	 *     }
	 * };
	 *
	 * isLimbo(uuid, callback)
	 */

	public HashMap<UUID, CachePlayer> getAllPlayers() {
		return LIMBO_CACHE;
	}

	public void isLimbo(UUID uuid, Callback<Boolean> callback) {
		final Runnable task = () -> {
			if(LIMBO_CACHE.containsKey(uuid)) { callback.execute(true); return; }

			try { if(sql.ifLimbo(uuid)) { callback.execute(true); return; }
			} catch (final SQLException e) { e.printStackTrace(); }

			callback.execute(false);
		};

		runAsync(task);
	}

	public boolean isLimbo(UUID uuid) {
		return LIMBO_CACHE.containsKey(uuid);
	}

	public boolean isEmpty() {
		return LIMBO_CACHE.isEmpty();
	}

	public void isExpired() {
		if(!LIMBO_CACHE.isEmpty()) {
			for(final Entry<UUID, CachePlayer> ce : LIMBO_CACHE.entrySet()) {
				final UUID uuid = ce.getKey();
				final CachePlayer cache = ce.getValue();
				final Player player = Bukkit.getPlayer(uuid);
				final long current = System.currentTimeMillis();

				if(player == null) {
					removePlayer(uuid);
					return;
				}

				if(current >= cache.getExpire() && cache.getExpire() != -1) {
					for (final Player onlinePlayer : Bukkit.getOnlinePlayers()) {
						player.getPlayer().showPlayer(LimboPlugin.getInstance(), onlinePlayer);
					}

					final Runnable task = () -> {
						try {
							sql.deleteUser(uuid);
						} catch (final SQLException e) {
							e.printStackTrace();
						}
					};

					removePlayer(uuid);
					runAsync(task);
				}
			}
		}
	}

	public boolean removePlayer(UUID uuid) {
		if(LIMBO_CACHE.containsKey(uuid)) {
			LIMBO_CACHE.remove(uuid);
			return true;
		} else { return false; }
	}

	public void getDBPlayer(UUID uuid, Callback<Boolean> callback) {
		final Runnable task = () -> {

			if(!LIMBO_CACHE.containsKey(uuid)) {
				CachePlayer cache = null;

				try { cache = sql.getPlayer(uuid); }
				catch (final SQLException e) { e.printStackTrace();	}

				if(cache == null) { callback.execute(false); }
				else {
					LIMBO_CACHE.put(uuid, cache);
					callback.execute(true);
				}

			} else { callback.execute(false); }

		};

		runAsync(task);
	}

	public boolean getDBPlayer(UUID uuid) {
		if(!LIMBO_CACHE.containsKey(uuid)) {
			CachePlayer cache = null;

			try { cache = sql.getPlayer(uuid); }
			catch (final SQLException e) { e.printStackTrace();	}

			if(cache == null) { return false; }

			LIMBO_CACHE.put(uuid, cache);

			return true;
		} else { return false; }
	}

	public void setPlayer(UUID uuid, UUID staffUUID, long timestamp, long expire, String reason) {
		final CachePlayer cache = new CachePlayer(uuid, staffUUID, timestamp, expire, reason);
		final Player player = Bukkit.getPlayer(uuid);

		if(player != null) { LIMBO_CACHE.put(uuid, cache); }

		final Runnable task = () -> {
			try { sql.setUser(cache); }
			catch (final SQLException e) { e.printStackTrace(); }
		};

		runAsync(task);
	}

	public static String reason(List<String> args, boolean temp) {
		int n;
		if(args.size() >= 2) {
			if(temp) { n = 2; }
			else { n = 1; }

			final StringBuilder message = new StringBuilder();
			for(int i = n; i < args.size(); i++){
				if(!args.get(i).equals("-s")) {	message.append(args.get(i) + " "); }
			}
			return message.toString();
		}
		return Messages.NO_REASON;
	}

	public static boolean isInt(String str) {
		try {
			Integer.parseInt(str);
			return true;
		} catch (final NumberFormatException e) {
			return false;
		}
	}

	public static long getTime(String time) {
		final String name = time.replaceAll("\\d", "");
		final int nr = Integer.parseInt(time.replaceAll("\\D", ""));

		long releaseTime = 0L;

		if (name.equalsIgnoreCase("months") || name.equalsIgnoreCase("month") || name.equalsIgnoreCase("mon")) {
			releaseTime = nr * 60 * 60 * 24 * 7 * 4 * 1000;
		} else if (name.equalsIgnoreCase("weeks") || name.equalsIgnoreCase("week") || name.equalsIgnoreCase("w")) {
			releaseTime = nr * 60 * 60 * 24 * 7 * 1000;
		} else if (name.equalsIgnoreCase("days") || name.equalsIgnoreCase("day") || name.equalsIgnoreCase("d")) {
			releaseTime = nr * 60 * 60 * 24 * 1000;
		} else if (name.equalsIgnoreCase("hours") || name.equalsIgnoreCase("hour") || name.equalsIgnoreCase("h")) {
			releaseTime = nr * 60 * 60 * 1000;
		} else if (name.equalsIgnoreCase("minutes") || name.equalsIgnoreCase("minute") || name.equalsIgnoreCase("min") || name.equalsIgnoreCase("m")) {
			releaseTime = nr * 60 * 1000;
		}

		return releaseTime;
	}

	public void runAsync(Runnable task) {
		Bukkit.getScheduler().runTaskAsynchronously(LimboPlugin.getInstance(), task);
	}

	public void runSync(Runnable task) {
		Bukkit.getScheduler().runTask(LimboPlugin.getInstance(), task);
	}
}
