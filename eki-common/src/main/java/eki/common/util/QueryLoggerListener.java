package eki.common.util;

import org.jooq.Configuration;
import org.jooq.ExecuteContext;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultExecuteListener;
import org.jooq.tools.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QueryLoggerListener extends DefaultExecuteListener {

	private static final Logger logger = LoggerFactory.getLogger(QueryLoggerListener.class);

	private static final long PROLONGED_QUERY_TRESHOLD_MS = 1000;

	private static final long serialVersionUID = 1L;

	private long start;

	@Override
	public void start(ExecuteContext ctx) {
		start = System.currentTimeMillis();
	}

	@Override
	public void renderStart(ExecuteContext ctx) {
	}

	@Override
	public void renderEnd(ExecuteContext ctx) {
	}

	@Override
	public void prepareStart(ExecuteContext ctx) {
	}

	@Override
	public void prepareEnd(ExecuteContext ctx) {
	}

	@Override
	public void bindStart(ExecuteContext ctx) {
	}

	@Override
	public void bindEnd(ExecuteContext ctx) {
	}

	@Override
	public void executeStart(ExecuteContext ctx) {
	}

	@Override
	public void executeEnd(ExecuteContext ctx) {
	}

	@Override
	public void outStart(ExecuteContext ctx) {
	}

	@Override
	public void outEnd(ExecuteContext ctx) {
	}

	@Override
	public void fetchStart(ExecuteContext ctx) {
	}

	@Override
	public void resultStart(ExecuteContext ctx) {
	}

	@Override
	public void recordStart(ExecuteContext ctx) {
	}

	@Override
	public void recordEnd(ExecuteContext ctx) {
	}

	@Override
	public void resultEnd(ExecuteContext ctx) {
	}

	@Override
	public void fetchEnd(ExecuteContext ctx) {
	}

	@Override
	public void end(ExecuteContext ctx) {
		long end = System.currentTimeMillis();
		long exec = end - start;

		if (logger.isDebugEnabled()) {

			String queryStr = getLogContent(ctx);

			logger.debug("Query \n{}", queryStr);
			logger.debug("Executed in {} ms", exec);

		} else if (logger.isInfoEnabled()) {

			if (exec > PROLONGED_QUERY_TRESHOLD_MS) {

				String queryStr = getLogContent(ctx);

				logger.info("Prolonging query \n{}", queryStr);
				logger.info("Executed in {} ms", exec);

			}
		}
	}

	@Override
	public void exception(ExecuteContext ctx) {
	}

	@Override
	public void warning(ExecuteContext ctx) {
	}

	private String getLogContent(ExecuteContext ctx) {

		Configuration configuration = ctx.configuration();

		String[] batchSQL = ctx.batchSQL();
		if (ctx.query() != null) {

			String query = DSL.using(configuration).renderInlined(ctx.query());
			return query;

		} else if (ctx.routine() != null) {

			String routine = DSL.using(configuration).renderInlined(ctx.routine());
			return routine;

		} else if (!StringUtils.isBlank(ctx.sql())) {

			String sql = ctx.sql();
			return sql;

		} else if (batchSQL.length > 0) {

			if (batchSQL[batchSQL.length - 1] != null) {
				String sqls = org.apache.commons.lang3.StringUtils.join(batchSQL, ";\n");
				return sqls;
			}
		}
		return null;
	}
}
