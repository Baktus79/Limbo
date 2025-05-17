package no.vestlandetmc.limbo.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import no.vestlandetmc.limbo.LimboPlugin;
import no.vestlandetmc.limbo.config.Config;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

public class SqlPool {

	private static HikariDataSource ds;

	public SqlPool() {
	}

	public static HikariDataSource getDataSource() throws SQLException {
		return ds;
	}

	public void initialize() throws SQLException {
		final HikariConfig cfg = new HikariConfig();

		if (Config.SQLTYPE.equalsIgnoreCase("mysql")) {
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
			cfg.setDriverClassName("com.mysql.cj.jdbc.Driver");
			cfg.setJdbcUrl("jdbc:mysql://" + Config.HOST + ":" + Config.PORT + "/" + Config.DATABASE);
		} else if (Config.SQLTYPE.equalsIgnoreCase("mariadb")) {
			cfg.setJdbcUrl("jdbc:mariadb://" + Config.HOST + ":" + Config.PORT + "/" + Config.DATABASE);
			cfg.setUsername(Config.USER);
			cfg.setPassword(Config.PASSWORD);
			cfg.setDriverClassName("no.vestlandetmc.limbo.libs.mariadb.Driver");
		} else {
			final File dbFile = new File(LimboPlugin.getPlugin().getDataFolder(), "data.db");
			cfg.setJdbcUrl("jdbc:sqlite:" + dbFile.getAbsolutePath());
		}

		cfg.setMaximumPoolSize(Config.SQLTYPE.equalsIgnoreCase("mysql") || Config.SQLTYPE.equalsIgnoreCase("mariadb") ? Config.MAX_POOL : 1);
		cfg.setConnectionTimeout(Config.CON_TIMEOUT);
		cfg.setMaxLifetime(Config.CON_LIFETIME);

		ds = new HikariDataSource(cfg);

		final String sql = """
				CREATE TABLE IF NOT EXISTS limbo (
					id INTEGER AUTO_INCREMENT PRIMARY KEY,
					uuid TEXT,
					staffuuid TEXT,
					timestamp BIGINT,
					expire BIGINT,
					reason TEXT
				)
				""";

		try (Connection con = getDataSource().getConnection()) {
			con.createStatement().execute(sql);
		}
	}
}
