package no.vestlandetmc.limbo.database;

import java.sql.Connection;
import java.sql.SQLException;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import no.vestlandetmc.limbo.LimboPlugin;
import no.vestlandetmc.limbo.config.Config;

public class SqlPool {

	private static HikariConfig cfg = new HikariConfig();
	private static HikariDataSource ds;

	public SqlPool() { }

	public static HikariDataSource getDataSource() throws SQLException {
		return ds;
	}

	public void initialize() throws SQLException {
		if(Config.SQLTYPE.equalsIgnoreCase("mysql")) {
			cfg.addDataSourceProperty("cachePrepStmts", "true");
			cfg.addDataSourceProperty("prepStmtCacheSize", "250");
			cfg.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
			cfg.addDataSourceProperty("useServerPrepStmts", "true");
			cfg.addDataSourceProperty("useLocalSessionState", "true");
			cfg.addDataSourceProperty("rewriteBatchedStatements", "true");
			cfg.addDataSourceProperty("cacheResultSetMetadata", "true");
			cfg.addDataSourceProperty("cacheServerConfiguration", "true");
			cfg.addDataSourceProperty("elideSetAutoCommits", "true");
			cfg.addDataSourceProperty("maintainTimeStats", "false");
			cfg.addDataSourceProperty("requireSSL", Config.ENABLE_SSL);
			cfg.addDataSourceProperty("user", Config.USER);
			cfg.addDataSourceProperty("password", Config.PASSWORD);
			cfg.setJdbcUrl("jdbc:mysql://" + Config.HOST + ":" + Config.PORT + "/" + Config.DATABASE);
		}

		else if(Config.SQLTYPE.equalsIgnoreCase("mariadb")) {
			cfg.setDataSourceClassName("org.mariadb.jdbc.MariaDbDataSource");
			cfg.addDataSourceProperty("serverName", Config.HOST);
			cfg.addDataSourceProperty("port", Config.PORT);
			cfg.addDataSourceProperty("databaseName", Config.DATABASE);
			cfg.addDataSourceProperty("user", Config.USER);
			cfg.addDataSourceProperty("password", Config.PASSWORD);
		}

		else if(Config.SQLTYPE.equalsIgnoreCase("sqlite")) {
			final String folder = LimboPlugin.getInstance().getDataFolder().getPath();
			cfg.setJdbcUrl("jdbc:sqlite:" + folder + "/data.db");
		}

		else { return; }

		cfg.setMaximumPoolSize(Config.MAX_POOL);
		cfg.setConnectionTimeout(Config.CON_TIMEOUT);
		cfg.setMaxLifetime(Config.CON_LIFETIME);

		ds = new HikariDataSource(cfg);

		final String sql = "CREATE TABLE IF NOT EXISTS limbo("
				+ "id INTEGER AUTO_INCREMENT PRIMARY KEY,"
				+ "uuid TEXT,"
				+ "staffuuid TEXT,"
				+ "timestamp BIGINT,"
				+ "expire BIGINT,"
				+ "reason TEXT"
				+ ")";

		Connection con;
		con = getDataSource().getConnection();
		con.createStatement().execute(sql);

		if(con != null)
			con.close();
	}
}
