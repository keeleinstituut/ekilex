package eki.ekilex.manual;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eki.ekilex.runner.LexemeMergerRunner;

public class LexemeMerger extends AbstractLoader {

	private static Logger logger = LoggerFactory.getLogger(LexemeMerger.class);

	public static void main(String[] args) {
		new LexemeMerger().execute(args);
	}

	@Override
	public void execute(String[] args) {
		try {
			initDefault();

			LexemeMergerRunner lexemeMergerRunner = getComponent(LexemeMergerRunner.class);
			String lexemeMergeName = confService.getMandatoryConfProperty("lex.merge.name");
			List<String> lexemeMergeDatasets = confService.getLexemeMergeDatasets();

			lexemeMergerRunner.deleteLexemeMergeData(lexemeMergeName);
			lexemeMergerRunner.execute(lexemeMergeName, lexemeMergeDatasets);

		} catch (Exception e) {
			logger.error("Unexpected behaviour of the system", e);
		} finally {
			shutdown();
		}
	}
}
