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
		new Qq2Loader().execute();
	}

	@Override
	public void execute() {
		try {
			initDefault();

			Qq2LoaderRunner datasetRunner = getComponent(Qq2LoaderRunner.class);
			MabService mabService = getComponent(MabService.class);
			String datasetCode = datasetRunner.getDataset();
			boolean doReports = doReports();

			// mab
			String mabFilePath1 = getConfProperty("mab.data.file.1");
			String mabFilePath2 = getConfProperty("mab.data.file.2");
			mabService.loadParadigms(mabFilePath1, mabFilePath2, doReports);

			// qq2
			String qqFilePath = getMandatoryConfProperty("qq2.data.file");
			Map<String, List<Guid>> ssGuidMap = getSsGuidMapFor(datasetCode);
			datasetRunner.execute(qqFilePath, ssGuidMap, doReports);

		} catch (Exception e) {
			logger.error("Unexpected behaviour of the system", e);
		} finally {
			shutdown();
		}
	}
}
