package eki.ekilex.manual;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eki.ekilex.data.transform.Guid;
import eki.ekilex.runner.PsvLoaderRunner;
import eki.ekilex.service.MabService;

public class PsvLoader extends AbstractLoader {

	private static Logger logger = LoggerFactory.getLogger(PsvLoader.class);

	public static void main(String[] args) {
		new PsvLoader().execute();
	}

	@Override
	void execute() {
		try {
			initDefault();

			PsvLoaderRunner datasetRunner = getComponent(PsvLoaderRunner.class);
			MabService mabService = getComponent(MabService.class);
			String datasetCode = datasetRunner.getDataset();
			boolean doReports = doReports();

			// mab
			String mabFilePath = getMandatoryConfProperty("mab.data.file");
			mabService.loadParadigms(mabFilePath, doReports);

			// ps
			String psFilePath = getMandatoryConfProperty("psv.data.file");
			Map<String, List<Guid>> ssGuidMap = getSsGuidMapFor(datasetCode);
			datasetRunner.execute(psFilePath, ssGuidMap, doReports);

		} catch (Exception e) {
			logger.error("Unexpected behaviour of the system", e);
		} finally {
			shutdown();
		}
	}

}
