package eki.ekilex.manual;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eki.common.exception.DataLoadingException;
import eki.ekilex.runner.MabLoaderRunner;

public class MabLoader extends AbstractLoader {

	private static Logger logger = LoggerFactory.getLogger(MabLoader.class);

	public static void main(String[] args) {
		new MabLoader().execute(args);
	}

	@Override
	public void execute(String[] args) {
		try {
			initDefault();

			MabLoaderRunner datasetRunner = getComponent(MabLoaderRunner.class);
			boolean doReports = doReports();
			boolean isFullReload = isFullReload();
			if (!isFullReload) {
				throw new DataLoadingException("Replacing MAB data is not supported!");
			}

			String[] mabDataFilePaths = getMabDataFilePaths();
			datasetRunner.execute(mabDataFilePaths, doReports);

		} catch (Exception e) {
			logger.error("Unexpected behaviour of the system", e);
		} finally {
			shutdown();
		}
	}
}
