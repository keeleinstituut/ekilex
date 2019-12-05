package eki.ekilex.manual;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eki.ekilex.runner.MeaningMergerRunner;

public class MeaningMerger extends AbstractLoader {

	private static Logger logger = LoggerFactory.getLogger(MeaningMerger.class);

	//mvn exec:java -P<profile> -Dexec.args="<compound dataset>"
	public static void main(String[] args) {
		if (args.length == 0) {
			logger.warn("Please provide compound dataset code as an argument");
			return;
		}
		new MeaningMerger().execute(args);
	}

	@Override
	void execute(String[] args) {
		try {
			initDefault();

			MeaningMergerRunner meaningMergerRunner = getComponent(MeaningMergerRunner.class);
			boolean doReports = confService.doReports();
			String compoundDatasetCode = args[0];

			meaningMergerRunner.execute(compoundDatasetCode, doReports);

		} catch (Exception e) {
			logger.error("Unexpected behaviour of the system", e);
		} finally {
			shutdown();
		}
	}
}
