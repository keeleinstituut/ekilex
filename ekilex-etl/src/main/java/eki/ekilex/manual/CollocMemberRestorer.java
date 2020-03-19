package eki.ekilex.manual;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eki.ekilex.runner.CollocMemberRestorerRunner;

public class CollocMemberRestorer extends AbstractLoader {

	private static Logger logger = LoggerFactory.getLogger(CollocMemberRestorer.class);

	public static void main(String[] args) {
		new CollocMemberRestorer().execute(args);
	}

	@Override
	void execute(String[] args) {
		try {
			initDefault();

			CollocMemberRestorerRunner collocMemberRestorerRunner = getComponent(CollocMemberRestorerRunner.class);

			String kolFilePath = confService.getMandatoryConfProperty("kol.data.file");
			collocMemberRestorerRunner.initialise();
			collocMemberRestorerRunner.execute(kolFilePath);

		} catch (Exception e) {
			logger.error("Unexpected behaviour of the system", e);
		} finally {
			shutdown();
		}
	}

}
