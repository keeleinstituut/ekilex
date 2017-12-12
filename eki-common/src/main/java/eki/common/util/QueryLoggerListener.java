package eki.common.util;

import org.jooq.Configuration;
import org.jooq.ExecuteContext;
import org.jooq.ExecuteType;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultExecuteListener;
import org.jooq.tools.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QueryLoggerListener extends DefaultExecuteListener {

	private static final Logger logger = LoggerFactory.getLogger(QueryLoggerListener.class);

	private static final long serialVersionUID = 1L;

	@Override
	public void renderEnd(ExecuteContext context) {

		if (logger.isDebugEnabled()) {

			Configuration configuration = context.configuration();

			String[] batchSQL = context.batchSQL();
			if (context.query() != null) {

				String inlined = DSL.using(configuration).renderInlined(context.query());
				logger.debug("Executing query \n{}", inlined);

			} else if (context.routine() != null) {

				String inlined = DSL.using(configuration).renderInlined(context.routine());
				logger.debug("Calling routine \n{}", inlined);

			} else if (!StringUtils.isBlank(context.sql())) {

				if (context.type() == ExecuteType.BATCH) {
					logger.debug("Executing batch query \n{}", context.sql());
				} else {
					logger.debug("Executing query \n{}", context.sql());
				}

			} else if (batchSQL.length > 0) {

				if (batchSQL[batchSQL.length - 1] != null) {
					for (String sql : batchSQL) {
						logger.debug("Executing batch query \n{}", sql);
					}
				}
			}
		}
	}
}
