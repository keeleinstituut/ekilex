package eki.ekilex.manual;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eki.ekilex.runner.EstermSourceLoaderRunner;

public class EstermSourceLoader extends AbstractLoader {

	private static Logger logger = LoggerFactory.getLogger(EstermSourceLoader.class);

	public static void main(String[] args) {
		new EstermSourceLoader().execute();
	}

	@Override
	void execute() {
		try {
			initDefault();

			EstermSourceLoaderRunner datasetRunner = getComponent(EstermSourceLoaderRunner.class);

			boolean doReports = doReports();
			String estFilePath = getMandatoryConfProperty("est.data.file");
			datasetRunner.execute(estFilePath, doReports);

		} catch (Exception e) {
			logger.error("Unexpected behaviour of the system", e);
		} finally {
			shutdown();
		}
	}

}
