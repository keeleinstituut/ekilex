package eki.ekilex.manual;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eki.ekilex.runner.EstermSourceLoaderRunner;

public class EstermSourceLoader extends AbstractLoader {

	private static Logger logger = LoggerFactory.getLogger(EstermSourceLoader.class);

	public static void main(String[] args) {
		new EstermSourceLoader().execute(args);
	}

	@Override
	void execute(String[] args) {
		try {
			initDefault();

			EstermSourceLoaderRunner datasetRunner = getComponent(EstermSourceLoaderRunner.class);

			boolean doReports = confService.doReports();
			String estFilePath = confService.getMandatoryConfProperty("est.data.file");
			datasetRunner.execute(estFilePath, doReports);

		} catch (Exception e) {
			logger.error("Unexpected behaviour of the system", e);
		} finally {
			shutdown();
		}
	}

}
