package eki.ekilex.manual;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eki.ekilex.runner.GameDataLoaderRunner;

public class GameDataLoader extends AbstractLoader {

	private static Logger logger = LoggerFactory.getLogger(GameDataLoader.class);

	public static void main(String[] args) {
		new GameDataLoader().execute();
	}

	@Override
	void execute() {
		try {
			initDefault();

			GameDataLoaderRunner dataRunner = getComponent(GameDataLoaderRunner.class);

			String nonWordsFilePath = getConfProperty("games.nonwords.file");
			dataRunner.execute(nonWordsFilePath);

		} catch (Exception e) {
			logger.error("Unexpected behaviour of the system", e);
		} finally {
			shutdown();
		}
	}

}
