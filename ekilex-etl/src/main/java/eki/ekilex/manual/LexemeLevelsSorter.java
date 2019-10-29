package eki.ekilex.manual;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eki.ekilex.runner.LexemeLevelsSorterRunner;

public class LexemeLevelsSorter extends AbstractLoader {

	private static Logger logger = LoggerFactory.getLogger(LexemeLevelsSorter.class);

	public static void main(String[] args) {
		new LexemeLevelsSorter().execute(args);
	}

	@Override
	void execute(String[] args) {
		try {
			initDefault();
			LexemeLevelsSorterRunner lexemeLevelsSorterRunner = getComponent(LexemeLevelsSorterRunner.class);
			boolean doReports = confService.doReports();
			lexemeLevelsSorterRunner.execute(doReports);
		} catch (Exception e) {
			logger.error("Unexpected behaviour of the system", e);
		} finally {
			shutdown();
		}
	}
}
