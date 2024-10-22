package eki.ekilex.cli;

import java.io.File;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import eki.common.util.ConsolePromptUtil;
import eki.ekilex.cli.runner.DatasetExporterRunner;
import eki.ekilex.service.TransportService;

@SpringBootApplication
@ComponentScan(basePackages = {
		"eki.common",
		"eki.ekilex.cli.config",
		"eki.ekilex.cli.runner",
		"eki.ekilex.service.core",
		"eki.ekilex.service.db",
		"eki.ekilex.service.util",
		"eki.ekilex.data"})
@EnableTransactionManagement
public class DatasetExporter implements CommandLineRunner {

	private static Logger logger = LoggerFactory.getLogger(DatasetExporter.class);

	private static final String ARG_KEY_DATASET = "dataset";

	private static final String ARG_KEY_EXPFOLDER = "expfolder";

	@Autowired
	private ConfigurableApplicationContext context;

	@Autowired
	private TransportService transportService;

	@Autowired
	private DatasetExporterRunner datasetExporterRunner;

	//mvn spring-boot:run -P dsexp -D spring-boot.run.profiles=<dev|prod> -D spring-boot.run.arguments="dataset=<code> expfolder="<export folder>"" 
	public static void main(String[] args) {
		logger.info("Application starting up");
		SpringApplication.run(DatasetExporter.class, args);
		logger.info("Application finished");
	}

	@Override
	public void run(String... args) throws Exception {

		String datasetCode = ConsolePromptUtil.getKeyValue(ARG_KEY_DATASET, args);
		String exportFolder = ConsolePromptUtil.getKeyValue(ARG_KEY_EXPFOLDER, args);
		boolean isOnlyPublic = false;
		if (StringUtils.isBlank(datasetCode)) {
			logger.warn("Please provide \"{}\" with arguments", ARG_KEY_DATASET);
			context.close();
			return;
		}
		if (StringUtils.isBlank(exportFolder)) {
			logger.warn("Please provide \"{}\" with arguments", ARG_KEY_EXPFOLDER);
			context.close();
			return;
		}
		boolean isValidFolderPath = isValidFolderPath(exportFolder);
		if (!isValidFolderPath) {
			logger.warn("Please provide valid \"{}\" with arguments", ARG_KEY_EXPFOLDER);
			context.close();
			return;
		}
		transportService.initialise();
		datasetExporterRunner.execute(datasetCode, isOnlyPublic, exportFolder);
		context.close();
	}

	private boolean isValidFolderPath(String folderPath) {
		File folder = new File(folderPath);
		boolean folderExists = folder.exists();
		if (!folderExists) {
			return false;
		}
		boolean isFile = folder.isFile();
		if (isFile) {
			return false;
		}
		return true;
	}
}
