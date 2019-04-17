package eki.ekilex.manual;

import eki.ekilex.runner.MilitermLoaderRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MilitermLoader extends AbstractLoader {

	private static Logger logger = LoggerFactory.getLogger(MilitermLoader.class);

	public static void main(String[] args) {
		new MilitermLoader().execute(args);
	}

	@Override
	void execute(String[] args) {
		try {
			initDefault();

			MilitermLoaderRunner datasetRunner = getComponent(MilitermLoaderRunner.class);
			boolean doReports = confService.doReports();
			boolean isFullReload = confService.isFullReload();

			String milFilePath1 = confService.getMandatoryConfProperty("mil.data.file.1");
			String milFilePath2 = confService.getMandatoryConfProperty("mil.data.file.2");
			if (!isFullReload) {
				datasetRunner.deleteDatasetData();
			}
			datasetRunner.execute(milFilePath1, milFilePath2, doReports);
		} catch (Exception e) {
			logger.error("Unexpected behaviour of the system", e);
		} finally {
			shutdown();
		}
	}

}
