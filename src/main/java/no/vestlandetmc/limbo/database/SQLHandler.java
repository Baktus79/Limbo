package no.vestlandetmc.limbo.database;

import no.vestlandetmc.limbo.LimboPlugin;
import no.vestlandetmc.limbo.obj.CachePlayer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.UUID;

public class SQLHandler {

	public SQLHandler() {
	}

	public LinkedHashMap<UUID, CachePlayer> getAll() throws SQLException {
		final String sql = "SELECT * FROM limbo ORDER BY timestamp DESC";

		try (Connection connection = SqlPool.getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
			final ResultSet set = statement.executeQuery();
			final LinkedHashMap<UUID, CachePlayer> users = new LinkedHashMap<>();

			while (set.next()) {
				final String uuid = set.getString("uuid");
				final String staffuuid = set.getString("staffuuid");
				final long timestamp = set.getLong("timestamp");
				final long expire = set.getLong("expire");
				final String reason = set.getString("reason");

				final CachePlayer cache = new CachePlayer(UUID.fromString(uuid), UUID.fromString(staffuuid), timestamp, expire, reason);
				users.put(UUID.fromString(uuid), cache);

			}

			return users;

		} catch (final Exception e) {
			LimboPlugin.getPlugin().getLogger().severe(e.getMessage());
		}

		return null;

	}

	public Set<String> getAllPlayerNames() {
		final Set<String> tempSet = new HashSet<>();

		try (Connection connection = SqlPool.getDataSource().getConnection();
			 PreparedStatement statement = connection.prepareStatement("SELECT uuid FROM limbo");
			 ResultSet rs = statement.executeQuery()) {

			while (rs.next()) {
				tempSet.add(rs.getString("uuid"));
			}

		} catch (SQLException e) {
			LimboPlugin.getPlugin().getLogger().severe("Feil under lasting av spillernavn: " + e.getMessage());
		}

		return tempSet;
	}


	public CachePlayer getPlayer(UUID uuid) throws SQLException {
		final String sql = "SELECT * FROM limbo WHERE uuid=?";

		try (Connection connection = SqlPool.getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setString(1, uuid.toString());

			final ResultSet set = statement.executeQuery();

			CachePlayer cache = null;

			while (set.next()) {
				final String staffuuid = set.getString("staffuuid");
				final long timestamp = set.getLong("timestamp");
				final long expire = set.getLong("expire");
				final String reason = set.getString("reason");

				cache = new CachePlayer(uuid, UUID.fromString(staffuuid), timestamp, expire, reason);

			}

			return cache;

		} catch (final Exception e) {
			LimboPlugin.getPlugin().getLogger().severe(e.getMessage());
		}

		return null;

	}

	public void setUser(CachePlayer cache) throws SQLException {
		final String sql = "INSERT INTO limbo (uuid, staffuuid, timestamp, expire, reason) VALUES (?, ?, ?, ?, ?)";

		try (Connection connection = SqlPool.getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setString(1, cache.getUniqueId().toString());
			statement.setString(2, cache.getStaffUUID().toString());
			statement.setLong(3, cache.getTimestamp());
			statement.setLong(4, cache.getExpire());
			statement.setString(5, cache.getReason());

			statement.executeUpdate();

		} catch (final Exception e) {
			LimboPlugin.getPlugin().getLogger().severe(e.getMessage());
		}

	}

	public void deleteUser(UUID uuid) throws SQLException {
		final String sql = "DELETE FROM limbo WHERE uuid=?";

		try (Connection connection = SqlPool.getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setString(1, uuid.toString());
			statement.executeUpdate();

		} catch (final Exception e) {
			LimboPlugin.getPlugin().getLogger().severe(e.getMessage());
		}

	}

}
