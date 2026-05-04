package eki.ekilex.app.config;

import java.util.Properties;

import javax.sql.DataSource;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import eki.common.util.QueryLoggerListener;

@Configuration
public class DbConfig {

	private static Logger logger = LoggerFactory.getLogger(DbConfig.class);

	@Bean("mainDataSourceProperties")
	@ConfigurationProperties("spring.datasource.main")
	protected Properties mainDataSourceProperties() {
		return new Properties();
	}

	@Bean("archDataSourceProperties")
	@ConfigurationProperties("spring.datasource.arch")
	protected Properties archDataSourceProperties() {
		return new Properties();
	}

	@Bean("mainDataSource")
	@Primary
	protected DataSource mainDataSource(@Qualifier("mainDataSourceProperties") Properties mainDataSourceProperties) {

		HikariConfig dataSourceConfig = new HikariConfig(mainDataSourceProperties);
		HikariDataSource dataSource = new HikariDataSource(dataSourceConfig);
		logDataSourceConfig(dataSource, "Main");

		return dataSource;
	}

	@Bean("mainNamedParameterJdbcTemplate")
	protected NamedParameterJdbcTemplate mainNamedParameterJdbcTemplate(@Qualifier("mainDataSource") DataSource mainDataSource) {
		return new NamedParameterJdbcTemplate(mainDataSource);
	}

	@Bean("archDataSource")
	protected DataSource archDataSource(@Qualifier("archDataSourceProperties") Properties archDataSourceProperties) {

		HikariConfig dataSourceConfig = new HikariConfig(archDataSourceProperties);
		HikariDataSource dataSource = new HikariDataSource(dataSourceConfig);
		logDataSourceConfig(dataSource, "Arch");

		return dataSource;
	}

	private void logDataSourceConfig(HikariDataSource dataSource, String dataSourceAlias) {

		logger.info("{} data source params: \n"
				+ "\tjdbcUrl: {}\n"
				+ "\tusername: {}\n"
				+ "\tdriverClassName: {}\n"
				+ "\tconnectionInitSql: {}\n"
				+ "\tconnectionTimeout: {}\n"
				+ "\tminimumIdle: {}\n"
				+ "\tidleTimeout: {}\n"
				+ "\tkeepaliveTime: {}\n"
				+ "\tpoolName: {}\n"
				+ "\tmaximumPoolSize: {}",
				dataSourceAlias,
				dataSource.getJdbcUrl(),
				dataSource.getUsername(),
				dataSource.getDriverClassName(),
				dataSource.getConnectionInitSql(),
				dataSource.getConnectionTimeout(),
				dataSource.getMinimumIdle(),
				dataSource.getIdleTimeout(),
				dataSource.getKeepaliveTime(),
				dataSource.getPoolName(),
				dataSource.getMaximumPoolSize());
	}

	@Bean("mainDb")
	protected DSLContext mainDslContext(@Qualifier("mainDataSource") DataSource mainDataSource) {

		HikariDataSource hikariMainDataSource = (HikariDataSource) mainDataSource;

		DSLContext context = DSL.using(hikariMainDataSource, SQLDialect.POSTGRES);

		Settings settings = context.settings();
		settings.setExecuteLogging(Boolean.FALSE);
		settings.setRenderSchema(false);
		settings.setRenderFormatted(true);

		org.jooq.Configuration configuration = context.configuration();
		configuration.set(new QueryLoggerListener());

		return context;
	}

	@Bean("archDb")
	protected DSLContext archDslContext(@Qualifier("archDataSource") DataSource archDataSource) {

		DSLContext context = DSL.using(archDataSource, SQLDialect.POSTGRES);

		Settings settings = context.settings();
		settings.setExecuteLogging(Boolean.FALSE);
		settings.setRenderSchema(false);
		settings.setRenderFormatted(true);

		org.jooq.Configuration configuration = context.configuration();
		configuration.set(new QueryLoggerListener());

		return context;
	}
}
