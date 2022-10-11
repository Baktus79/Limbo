package no.vestlandetmc.limbo.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.UUID;

import no.vestlandetmc.limbo.obj.CachePlayer;

public class SQLHandler {

	public SQLHandler() {	}

	public boolean ifLimbo(UUID uuid) throws SQLException {
		final String sql = "SELECT uuid FROM limbo WHERE uuid=?";

		try(Connection connection = SqlPool.getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement(sql);) {
			statement.setString(1, uuid.toString());

			final ResultSet set = statement.executeQuery();

			if(set.getFetchSize() != 0) { return true; }

			if(connection != null)
				connection.close();

			if(statement != null)
				statement.close();

		} catch(final Exception e) {
			e.printStackTrace();
		}

		return false;

	}

	public long getExpire(UUID uuid) throws SQLException {
		final String sql = "SELECT expire FROM limbo WHERE uuid=?";

		try(Connection connection = SqlPool.getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement(sql);) {
			statement.setString(1, uuid.toString());

			final ResultSet set = statement.executeQuery();

			while (set.next()) {
				final long expire = set.getLong("expire");

				return expire;

			}

			if(connection != null)
				connection.close();

			if(statement != null)
				statement.close();

		} catch(final Exception e) {
			e.printStackTrace();
		}

		return 0;

	}

	public LinkedHashMap<UUID, CachePlayer> getAll() throws SQLException {
		final String sql = "SELECT * FROM limbo ORDER BY timestamp DESC";

		try(Connection connection = SqlPool.getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement(sql);) {
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

			if(connection != null)
				connection.close();

			if(statement != null)
				statement.close();

			return users;

		} catch(final Exception e) {
			e.printStackTrace();
		}

		return null;

	}

	public CachePlayer getPlayer(UUID uuid) throws SQLException {
		final String sql = "SELECT * FROM limbo WHERE uuid=?";

		try(Connection connection = SqlPool.getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement(sql);) {
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

			if(connection != null)
				connection.close();

			if(statement != null)
				statement.close();

			return cache;

		} catch(final Exception e) {
			e.printStackTrace();
		}

		return null;

	}

	public void setUser(CachePlayer cache) throws SQLException {
		final String sql = "INSERT INTO limbo (uuid, staffuuid, timestamp, expire, reason) VALUES (?, ?, ?, ?, ?)";

		try(Connection connection = SqlPool.getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement(sql);) {
			statement.setString(1, cache.getUniqueId().toString());
			statement.setString(2, cache.getStaffUUID().toString());
			statement.setLong(3, cache.getTimestamp());
			statement.setLong(4, cache.getExpire());
			statement.setString(5, cache.getReason());

			statement.executeUpdate();

			if(connection != null)
				connection.close();

			if(statement != null)
				statement.close();

		} catch(final Exception e) {
			e.printStackTrace();
		}

	}

	public boolean deleteUser(UUID uuid) throws SQLException {
		boolean complete;
		final String sql = "DELETE FROM limbo WHERE uuid=?";

		try(Connection connection = SqlPool.getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement(sql);) {
			statement.setString(1, uuid.toString());

			if(statement.executeUpdate() == 0) {
				complete = false;
			} else { complete = true; }


			if(connection != null)
				connection.close();

			if(statement != null)
				statement.close();

			return complete;

		} catch(final Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	public void removeExpired() throws SQLException {
		final String sql = "DELETE FROM limbo WHERE timestamp>expire";

		try(Connection connection = SqlPool.getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement(sql);) {

			if(connection != null)
				connection.close();

			if(statement != null)
				statement.close();

		} catch(final Exception e) {
			e.printStackTrace();
		}
	}

}
