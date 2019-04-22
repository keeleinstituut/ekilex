package eki.ekilex.manual;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eki.ekilex.data.transform.Guid;
import eki.ekilex.data.transform.Mnr;
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
			boolean doReports = confService.doReports();
			boolean isFullReload = confService.isFullReload();

			// mab
			mabService.initialise(); //MAB must be loaded first!

			// ev2
			String evFilePath1 = confService.getMandatoryConfProperty("ev2.data.file.1");
			String evFilePath2 = confService.getMandatoryConfProperty("ev2.data.file.2");
			Map<String, List<Guid>> ssGuidMap = confService.getSsGuidMapFor(datasetCode);
			Map<String, List<Mnr>> ssMnrMap = confService.getSsMnrMap();
			if (!isFullReload) {
				datasetRunner.deleteDatasetData();
			}
			datasetRunner.execute(evFilePath1, evFilePath2, ssGuidMap, ssMnrMap, doReports);

		} catch (Exception e) {
			logger.error("Unexpected behaviour of the system", e);
		} finally {
			shutdown();
		}
	}

}
