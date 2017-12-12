package eki.eve.config;

import org.jooq.DSLContext;
import org.jooq.conf.RenderNameStyle;
import org.jooq.conf.Settings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import eki.common.util.QueryLoggerListener;
import eki.common.util.QueryStopWatchListener;

@Configuration
public class JooqConfig {

	@Autowired
	public JooqConfig(DSLContext context) {

		Settings settings = context.settings();
		org.jooq.Configuration configuration = context.configuration();

		settings.setExecuteLogging(Boolean.FALSE);
		settings.setRenderSchema(false);
		settings.setRenderFormatted(true);
		settings.setRenderNameStyle(RenderNameStyle.AS_IS);

		configuration.set(new QueryLoggerListener(), new QueryStopWatchListener());
	}

}
