package eki.ekilex.manual;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eki.common.util.ConsolePromptUtil;
import eki.ekilex.runner.DatasetImporterRunner;
import eki.ekilex.service.TransportService;

public class DatasetImporter extends AbstractLoader {

	private static Logger logger = LoggerFactory.getLogger(DatasetImporter.class);

	public static void main(String[] args) {
		new DatasetImporter().execute(args);
	}

	@Override
	void execute(String[] args) {
		try {
			initDefault();

			DatasetImporterRunner runner = getComponent(DatasetImporterRunner.class);
			TransportService transportService = getComponent(TransportService.class);
			transportService.initialize();

			String sourceDatasetCode = ConsolePromptUtil.promptStringValue("Please specify original dataset code to be imported?");
			String targetDatasetCode = ConsolePromptUtil.promptStringValue("Please specify target dataset code? (can be same as original if no longer exists or new one)");
			String importFilePath = ConsolePromptUtil.promptDataFilePath("Please specify import file path? (/path/to/file.zip)");

			runner.execute(sourceDatasetCode, targetDatasetCode, importFilePath);

		} catch (Exception e) {
			logger.error("Unexpected behaviour of the system", e);
		} finally {
			shutdown();
		}
	}
}
