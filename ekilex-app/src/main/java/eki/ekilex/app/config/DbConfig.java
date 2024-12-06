package eki.ekilex.app.config;

import javax.sql.DataSource;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import eki.common.util.QueryLoggerListener;

@Configuration
public class DbConfig {

	@Bean("mainDataSourceProperties")
	@ConfigurationProperties("spring.datasource.main")
	protected DataSourceProperties mainDataSourceProperties() {
		return new DataSourceProperties();
	}

	@Bean("archDataSourceProperties")
	@ConfigurationProperties("spring.datasource.arch")
	protected DataSourceProperties archDataSourceProperties() {
		return new DataSourceProperties();
	}

	@Bean("mainDataSource")
	@Primary
	protected DataSource mainDataSource() {
		return mainDataSourceProperties()
				.initializeDataSourceBuilder()
				.build();
	}

	@Bean("mainNamedParameterJdbcTemplate")
	protected NamedParameterJdbcTemplate mainNamedParameterJdbcTemplate(@Qualifier("mainDataSource") DataSource mainDataSource) {
		return new NamedParameterJdbcTemplate(mainDataSource);
	}

	@Bean("archDataSource")
	protected DataSource archDataSource() {
		return archDataSourceProperties()
				.initializeDataSourceBuilder()
				.build();
	}

	@Bean("mainDb")
	protected DSLContext mainDslContext(@Qualifier("mainDataSource") DataSource mainDataSource) {

		DSLContext context = DSL.using(mainDataSource, SQLDialect.POSTGRES);

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
