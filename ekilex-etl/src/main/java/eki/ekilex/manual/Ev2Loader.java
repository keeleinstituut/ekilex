package eki.ekilex.manual;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eki.ekilex.data.transform.Guid;
import eki.ekilex.runner.Ev2LoaderRunner;
import eki.ekilex.service.MabService;

public class Ev2Loader extends AbstractLoader {

	private static Logger logger = LoggerFactory.getLogger(Ev2Loader.class);

	public static void main(String[] args) {
		new Ev2Loader().execute(args);
	}

	@Override
	void execute(String[] args) {
		try {
			initDefault();

			Ev2LoaderRunner datasetRunner = getComponent(Ev2LoaderRunner.class);
			MabService mabService = getComponent(MabService.class);
			String datasetCode = datasetRunner.getDataset();
			boolean doReports = doReports();
			boolean isFullReload = isFullReload();

			// mab
			String[] mabDataFilePaths = getMabDataFilePaths();
			mabService.loadParadigms(mabDataFilePaths, doReports);

			// ev2
			String evFilePath1 = getMandatoryConfProperty("ev2.data.file.1");
			String evFilePath2 = getMandatoryConfProperty("ev2.data.file.2");
			Map<String, List<Guid>> ssGuidMap = getSsGuidMapFor(datasetCode);
			if (!isFullReload) {
				datasetRunner.deleteDatasetData();
			}
			datasetRunner.execute(evFilePath1, evFilePath2, ssGuidMap, doReports);

		} catch (Exception e) {
			logger.error("Unexpected behaviour of the system", e);
		} finally {
			shutdown();
		}
	}

}
