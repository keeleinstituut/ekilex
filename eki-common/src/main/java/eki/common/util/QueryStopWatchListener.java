package eki.common.util;

import org.jooq.ExecuteContext;
import org.jooq.ExecuteListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QueryStopWatchListener implements ExecuteListener {

	private static final Logger logger = LoggerFactory.getLogger(QueryStopWatchListener.class);

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
		logger.debug("Query executed at {} ms", exec);
	}

	@Override
	public void exception(ExecuteContext ctx) {
	}

	@Override
	public void warning(ExecuteContext ctx) {
	}

}
