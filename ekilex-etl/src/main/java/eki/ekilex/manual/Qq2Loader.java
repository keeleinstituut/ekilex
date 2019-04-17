package eki.ekilex.manual;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eki.ekilex.data.transform.Guid;
import eki.ekilex.runner.Qq2LoaderRunner;
import eki.ekilex.service.MabService;

public class Qq2Loader extends AbstractLoader {

	private static Logger logger = LoggerFactory.getLogger(Qq2Loader.class);

	public static void main(String[] args) {
		new Qq2Loader().execute(args);
	}

	@Override
	public void execute(String[] args) {
		try {
			initDefault();

			Qq2LoaderRunner datasetRunner = getComponent(Qq2LoaderRunner.class);
			MabService mabService = getComponent(MabService.class);
			String datasetCode = datasetRunner.getDataset();
			boolean doReports = confService.doReports();
			boolean isFullReload = confService.isFullReload();

			// mab
			mabService.initialise(); //MAB must be loaded first!

			// qq2
			String qqFilePath = confService.getMandatoryConfProperty("qq2.data.file");
			Map<String, List<Guid>> ssGuidMap = confService.getSsGuidMapFor(datasetCode);
			if (!isFullReload) {
				datasetRunner.deleteDatasetData();
			}
			datasetRunner.execute(qqFilePath, ssGuidMap, doReports);

		} catch (Exception e) {
			logger.error("Unexpected behaviour of the system", e);
		} finally {
			shutdown();
		}
	}
}
