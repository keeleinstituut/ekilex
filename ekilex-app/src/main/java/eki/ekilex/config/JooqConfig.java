package eki.ekilex.config;

import org.jooq.DSLContext;
import org.jooq.conf.RenderNameStyle;
import org.jooq.tools.StopWatchListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JooqConfig {

	@Autowired
	public JooqConfig(DSLContext context) {
		context.settings().setRenderSchema(false);
		context.settings().setRenderFormatted(true);
		context.settings().setRenderNameStyle(RenderNameStyle.AS_IS);
		context.configuration().set(new StopWatchListener());
	}

}
