package eki.ekilex.manual;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eki.ekilex.runner.SimilarWordMergeRunner;

public class SimilarWordMerger extends AbstractLoader {

	private static Logger logger = LoggerFactory.getLogger(SimilarWordMerger.class);

	public static void main(String[] args) {
		new SimilarWordMerger().execute(args);
	}

	@Override
	void execute(String[] args) {
		try {
			initDefault();

			SimilarWordMergeRunner similarWordMergeRunner = getComponent(SimilarWordMergeRunner.class);

			similarWordMergeRunner.execute();

		} catch (Exception e) {
			logger.error("Unexpected behaviour of the system", e);
		} finally {
			shutdown();
		}
	}
}
