package eki.ekilex.manual;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eki.common.exception.DataLoadingException;
import eki.ekilex.runner.DbReInitialiserRunner;

public class DbReInitialiser extends AbstractLoader {

	private static Logger logger = LoggerFactory.getLogger(DbReInitialiser.class);

	public static void main(String[] args) {
		new DbReInitialiser().execute();
	}

	@Override
	public void execute() {
		try {
			initDefault();

			boolean isFullReload = isFullReload();
			if (!isFullReload) {
				throw new DataLoadingException("Erasing database is prohibited!");
			}

			DbReInitialiserRunner runner = getComponent(DbReInitialiserRunner.class);

			runner.execute();

		} catch (Exception e) {
			logger.error("Unexpected behaviour of the system", e);
		} finally {
			shutdown();
		}
	}
}
