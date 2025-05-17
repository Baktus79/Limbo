package no.vestlandetmc.limbo.handler;

import no.vestlandetmc.limbo.LimboPlugin;
import no.vestlandetmc.limbo.config.Messages;
import no.vestlandetmc.limbo.database.SQLHandler;
import no.vestlandetmc.limbo.obj.CachePlayer;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public class DataHandler {

	private final SQLHandler sql;
	private final HashMap<UUID, CachePlayer> LIMBO_CACHE = new HashMap<>();
	private final Set<String> PLAYER_NAMES = ConcurrentHashMap.newKeySet();

	public DataHandler(SQLHandler sql) {
		this.sql = sql;
	}

	public HashMap<UUID, CachePlayer> getAllPlayers() {
		return LIMBO_CACHE;
	}

	public void cachePlayerNames() {
		for (String uuidStr : sql.getAllPlayerNames()) {
			try {
				UUID uuid = UUID.fromString(uuidStr);
				OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
				String name = player.getName();

				if (name != null) {
					PLAYER_NAMES.add(name);
				}
			} catch (IllegalArgumentException e) {
				LimboPlugin.getPlugin().getLogger().warning("Invalid UUID in database: " + uuidStr);
			}
		}
	}

	public Set<String> getCachePlayerNames() {
		return PLAYER_NAMES;
	}

	public boolean isLimbo(UUID uuid) {
		return LIMBO_CACHE.containsKey(uuid);
	}

	public void isExpired() {
		if (!LIMBO_CACHE.isEmpty()) {
			for (final Entry<UUID, CachePlayer> ce : LIMBO_CACHE.entrySet()) {
				final UUID uuid = ce.getKey();
				final CachePlayer cache = ce.getValue();
				final Player player = Bukkit.getPlayer(uuid);
				final long current = System.currentTimeMillis();

				if (player == null) {
					removePlayer(uuid);
					return;
				}

				if (current >= cache.getExpire() && cache.getExpire() != -1) {
					for (final Player onlinePlayer : Bukkit.getOnlinePlayers()) {
						Objects.requireNonNull(player.getPlayer()).showPlayer(LimboPlugin.getPlugin(), onlinePlayer);
					}

					final Runnable task = () -> {
						try {
							sql.deleteUser(uuid);
						} catch (final SQLException e) {
							LimboPlugin.getPlugin().getLogger().severe(e.getMessage());
						}
					};

					removePlayer(uuid);
					runAsync(task);
				}
			}
		}
	}

	public void removePlayerCache(UUID uuid) {
		LIMBO_CACHE.remove(uuid);
	}

	public void removePlayer(UUID uuid) {
		final OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
		LIMBO_CACHE.remove(uuid);
		PLAYER_NAMES.remove(player.getName());

		try {
			this.sql.deleteUser(uuid);
		} catch (final SQLException e) {
			LimboPlugin.getPlugin().getLogger().severe(e.getMessage());
		}
	}

	public void getDBPlayer(UUID uuid, Callback<Boolean> callback) {
		final Runnable task = () -> {

			if (!LIMBO_CACHE.containsKey(uuid)) {
				CachePlayer cache = null;

				try {
					cache = sql.getPlayer(uuid);
				} catch (final SQLException e) {
					LimboPlugin.getPlugin().getLogger().severe(e.getMessage());
				}

				if (cache == null) {
					callback.execute(false);
				} else {
					LIMBO_CACHE.put(uuid, cache);
					callback.execute(true);
				}

			} else {
				callback.execute(false);
			}

		};

		runAsync(task);
	}

	public void setPlayer(UUID uuid, UUID staffUUID, long timestamp, long expire, String reason) {
		final CachePlayer cache = new CachePlayer(uuid, staffUUID, timestamp, expire, reason);
		final OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);

		if (player.getName() != null) {
			LIMBO_CACHE.put(uuid, cache);
			PLAYER_NAMES.add(player.getName());

			final Runnable task = () -> {
				try {
					sql.setUser(cache);
				} catch (final SQLException e) {
					LimboPlugin.getPlugin().getLogger().severe(e.getMessage());
				}
			};

			runAsync(task);
		}
	}

	public static String reason(List<String> args, boolean temp) {
		int n;
		if (args.size() >= 2) {
			if (temp) {
				n = 2;
			} else {
				n = 1;
			}

			final StringBuilder message = new StringBuilder();
			for (int i = n; i < args.size(); i++) {
				if (!args.get(i).equals("-s")) {
					message.append(args.get(i)).append(" ");
				}
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
			releaseTime = (long) nr * 60 * 60 * 24 * 7 * 4 * 1000;
		} else if (name.equalsIgnoreCase("weeks") || name.equalsIgnoreCase("week") || name.equalsIgnoreCase("w")) {
			releaseTime = (long) nr * 60 * 60 * 24 * 7 * 1000;
		} else if (name.equalsIgnoreCase("days") || name.equalsIgnoreCase("day") || name.equalsIgnoreCase("d")) {
			releaseTime = (long) nr * 60 * 60 * 24 * 1000;
		} else if (name.equalsIgnoreCase("hours") || name.equalsIgnoreCase("hour") || name.equalsIgnoreCase("h")) {
			releaseTime = (long) nr * 60 * 60 * 1000;
		} else if (name.equalsIgnoreCase("minutes") || name.equalsIgnoreCase("minute") || name.equalsIgnoreCase("min") || name.equalsIgnoreCase("m")) {
			releaseTime = (long) nr * 60 * 1000;
		}

		return releaseTime;
	}

	public void runAsync(Runnable task) {
		Bukkit.getScheduler().runTaskAsynchronously(LimboPlugin.getPlugin(), task);
	}

	public void runSync(Runnable task) {
		Bukkit.getScheduler().runTask(LimboPlugin.getPlugin(), task);
	}
}
