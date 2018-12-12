package eki.ekilex.manual;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eki.ekilex.runner.EstermLoaderRunner;

public class EstermLoader extends AbstractLoader {

	private static Logger logger = LoggerFactory.getLogger(EstermLoader.class);

	public static void main(String[] args) {
		new EstermLoader().execute(args);
	}

	@Override
	void execute(String[] args) {
		try {
			initDefault();

			EstermLoaderRunner datasetRunner = getComponent(EstermLoaderRunner.class);

			boolean doReports = doReports();
			boolean isFullReload = isFullReload();

			String estFilePath = getMandatoryConfProperty("est.data.file");
			if (!isFullReload) {
				datasetRunner.deleteDatasetData();
			}
			datasetRunner.execute(estFilePath, doReports);

		} catch (Exception e) {
			logger.error("Unexpected behaviour of the system", e);
		} finally {
			shutdown();
		}
	}
}
