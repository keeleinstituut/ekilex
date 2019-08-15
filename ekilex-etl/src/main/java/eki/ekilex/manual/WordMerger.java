package eki.ekilex.manual;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eki.ekilex.runner.WordMergerRunner;

public class WordMerger extends AbstractLoader {

	private static Logger logger = LoggerFactory.getLogger(WordMerger.class);

	public static void main(String[] args) {
		new WordMerger().execute(args);
	}

	@Override
	public void execute(String[] args) {
		try {
			initDefault();

			WordMergerRunner wordMergerRunner = getComponent(WordMergerRunner.class);
			String mergedLexDatasetCode = confService.getMandatoryConfProperty("word.merge.merged.dataset");
			boolean doReports = confService.doReports();

			wordMergerRunner.execute(mergedLexDatasetCode, doReports);
		} catch (Exception e) {
			logger.error("Unexpected behaviour of the system", e);
		} finally {
			shutdown();
		}
	}
}
