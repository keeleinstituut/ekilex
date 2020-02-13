package eki.ekilex.manual;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eki.common.util.ConsolePromptUtil;
import eki.ekilex.runner.DatasetExporterRunner;
import eki.ekilex.service.TransportService;

public class DatasetExporter extends AbstractLoader {

	private static Logger logger = LoggerFactory.getLogger(DatasetExporter.class);

	public static void main(String[] args) {
		new DatasetExporter().execute(args);
	}

	@Override
	void execute(String[] args) {
		try {
			initDefault();

			DatasetExporterRunner runner = getComponent(DatasetExporterRunner.class);
			TransportService transportService = getComponent(TransportService.class);
			transportService.initialise();

			String datasetCode = ConsolePromptUtil.promptStringValue("Please specify dataset to be exported? (dataset code)");
			boolean isOnlyPublic = ConsolePromptUtil.promptBooleanValue("Only public data? (y/n)");
			String datasetExportFolder = ConsolePromptUtil.promptDataFolderPath("Please specify folder for export file? (/path/of/file/)");

			runner.execute(datasetCode, isOnlyPublic, datasetExportFolder);

		} catch (Exception e) {
			logger.error("Unexpected behaviour of the system", e);
		} finally {
			shutdown();
		}
	}

}
