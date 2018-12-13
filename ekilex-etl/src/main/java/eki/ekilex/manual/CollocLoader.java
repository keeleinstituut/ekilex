package eki.ekilex.manual;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eki.ekilex.data.transform.Guid;
import eki.ekilex.runner.CollocLoaderRunner;

public class CollocLoader extends AbstractLoader {

	private static Logger logger = LoggerFactory.getLogger(CollocLoader.class);

	public static void main(String[] args) {
		new CollocLoader().execute(args);
	}

	@Override
	void execute(String[] args) {
		try {
			initDefault();

			CollocLoaderRunner datasetRunner = getComponent(CollocLoaderRunner.class);
			String datasetCode = datasetRunner.getDataset();
			boolean doReports = doReports();
			boolean isFullReload = isFullReload();

			String kolFilePath = getMandatoryConfProperty("kol.data.file");
			Map<String, List<Guid>> ssGuidMap = getSsGuidMapFor(datasetCode);
			if (!isFullReload) {
				datasetRunner.deleteDatasetData();
			}
			datasetRunner.execute(kolFilePath, ssGuidMap, doReports);

		} catch (Exception e) {
			logger.error("Unexpected behaviour of the system", e);
		} finally {
			shutdown();
		}
	}

}
