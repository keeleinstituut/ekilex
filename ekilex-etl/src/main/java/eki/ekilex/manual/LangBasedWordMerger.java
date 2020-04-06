package eki.ekilex.manual;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eki.ekilex.runner.LangBasedWordMergerRunner;

public class LangBasedWordMerger extends AbstractLoader {

	private static Logger logger = LoggerFactory.getLogger(LangBasedWordMerger.class);

	public static void main(String[] args) {
		new LangBasedWordMerger().execute(args);
	}

	@Override
	public void execute(String[] args) {
		try {
			initDefault();

			if (ArrayUtils.isEmpty(args)) {
				logger.error("Please provide language code (required) and file path of word values to exclude (optional):");
				logger.error("<lang code>");
				logger.error("or");
				logger.error("<lang code> <excluded words file path>");
				return;
			}

			String wordLangCode = args[0];

			String excludedWordsFilePath = null;
			if (args.length == 2) {
				excludedWordsFilePath = args[1];
			}

			LangBasedWordMergerRunner langBasedWordMergerRunner = getComponent(LangBasedWordMergerRunner.class);
			boolean doReports = confService.doReports();
			langBasedWordMergerRunner.execute(wordLangCode, excludedWordsFilePath, doReports);
		} catch (Exception e) {
			logger.error("Unexpected behaviour of the system", e);
		} finally {
			shutdown();
		}
	}
}
