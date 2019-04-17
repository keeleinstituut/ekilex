package eki.ekilex.manual;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eki.ekilex.runner.MilitermSourceLoaderRunner;

public class MilitermSourceLoader extends AbstractLoader {

	private static Logger logger = LoggerFactory.getLogger(MilitermSourceLoader.class);

	public static void main(String[] args) {
		new MilitermSourceLoader().execute(args);
	}

	@Override
	void execute(String[] args) {
		try {
			initDefault();

			MilitermSourceLoaderRunner datasetRunner = getComponent(MilitermSourceLoaderRunner.class);
			boolean doReports = confService.doReports();
			String milFilePath1 = confService.getMandatoryConfProperty("mil.data.file.1");
			String milFilePath2 = confService.getMandatoryConfProperty("mil.data.file.2");
			datasetRunner.execute(milFilePath1, milFilePath2, doReports);
		} catch (Exception e) {
			logger.error("Unexpected behaviour of the system", e);
		} finally {
			shutdown();
		}
	}
}
