package eki.ekimedia.config;

import org.jooq.DSLContext;
import org.jooq.conf.Settings;
import org.springframework.context.annotation.Configuration;

import eki.common.util.QueryLoggerListener;

@Configuration
public class JooqConfig {

	public JooqConfig(DSLContext context) {

		Settings settings = context.settings();
		org.jooq.Configuration configuration = context.configuration();

		settings.setExecuteLogging(Boolean.FALSE);
		settings.setRenderSchema(false);
		settings.setRenderFormatted(true);

		configuration.set(new QueryLoggerListener());
	}

}
