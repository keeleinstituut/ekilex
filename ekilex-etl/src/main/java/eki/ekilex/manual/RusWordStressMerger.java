package eki.ekilex.manual;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eki.ekilex.runner.RusWordStressMergerRunner;

public class RusWordStressMerger extends AbstractLoader {

	private static Logger logger = LoggerFactory.getLogger(RusWordStressMerger.class);

	public static void main(String[] args) {
		new RusWordStressMerger().execute(args);
	}

	@Override
	public void execute(String[] args) {
		try {
			initDefault();

			RusWordStressMergerRunner rusWordStressMergerRunner = getComponent(RusWordStressMergerRunner.class);
			boolean doReports = confService.doReports();

			rusWordStressMergerRunner.execute(doReports);
		} catch (Exception e) {
			logger.error("Unexpected behaviour of the system", e);
		} finally {
			shutdown();
		}
	}

}
