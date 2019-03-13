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
			boolean doReports = doReports();
			boolean isFullReload = isFullReload();

			String milFilePath = getMandatoryConfProperty("mil.data.file");
			if (!isFullReload) {
				datasetRunner.deleteDatasetData();
			}
			datasetRunner.execute(milFilePath, doReports);
		} catch (Exception e) {
			logger.error("Unexpected behaviour of the system", e);
		} finally {
			shutdown();
		}
	}
}
