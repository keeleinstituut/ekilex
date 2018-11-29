package eki.ekilex.manual;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eki.ekilex.runner.Ss1LoaderRunner;
import eki.ekilex.service.MabService;

public class Ss1Loader extends AbstractLoader {

	private static Logger logger = LoggerFactory.getLogger(Ss1Loader.class);

	public static void main(String[] args) {
		new Ss1Loader().execute();
	}

	@Override
	void execute() {
		try {
			initDefault();

			Ss1LoaderRunner datasetRunner = getComponent(Ss1LoaderRunner.class);
			MabService mabService = getComponent(MabService.class);
			boolean doReports = doReports();

			// mab
			String[] mabDataFilePaths = getMabDataFilePaths();
			mabService.loadParadigms(mabDataFilePaths, doReports);

			// ss
			String ssFilePath = getMandatoryConfProperty("ss1.data.file");
			datasetRunner.execute(ssFilePath, doReports);

		} catch (Exception e) {
			logger.error("Unexpected behaviour of the system", e);
		} finally {
			shutdown();
		}
	}

}
