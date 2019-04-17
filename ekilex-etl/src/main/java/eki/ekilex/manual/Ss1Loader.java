package eki.ekilex.manual;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eki.common.exception.DataLoadingException;
import eki.ekilex.runner.Ss1LoaderRunner;
import eki.ekilex.service.MabService;

public class Ss1Loader extends AbstractLoader {

	private static Logger logger = LoggerFactory.getLogger(Ss1Loader.class);

	public static void main(String[] args) {
		new Ss1Loader().execute(args);
	}

	@Override
	void execute(String[] args) {
		try {
			initDefault();

			Ss1LoaderRunner datasetRunner = getComponent(Ss1LoaderRunner.class);
			MabService mabService = getComponent(MabService.class);
			boolean doReports = confService.doReports();
			boolean isFullReload = confService.isFullReload();
			if (!isFullReload) {
				throw new DataLoadingException("Replacing SS data is not supported!");
			}

			// mab
			mabService.initialise(); //MAB must be loaded first!

			// ss
			String ssFilePath = confService.getMandatoryConfProperty("ss1.data.file");
			datasetRunner.execute(ssFilePath, doReports);

		} catch (Exception e) {
			logger.error("Unexpected behaviour of the system", e);
		} finally {
			shutdown();
		}
	}

}
