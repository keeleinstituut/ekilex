package eki.ekilex.manual;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eki.ekilex.runner.EtymologyLoaderRunner;

public class EtymologyLoader extends AbstractLoader {

	private static Logger logger = LoggerFactory.getLogger(EtymologyLoader.class);

	public static void main(String[] args) {
		new EtymologyLoader().execute(args);
	}

	@Override
	void execute(String[] args) {
		try {
			initDefault();

			EtymologyLoaderRunner datasetRunner = getComponent(EtymologyLoaderRunner.class);
			boolean doReports = doReports();
			boolean isFullReload = isFullReload();

			String ssFilePath = getMandatoryConfProperty("ss1.data.file");
			if (!isFullReload) {
				datasetRunner.deleteDatasetData();
			}
			datasetRunner.execute(ssFilePath, doReports);

		} catch (Exception e) {
			logger.error("Unexpected behaviour of the system", e);
		} finally {
			shutdown();
		}

	}

}
