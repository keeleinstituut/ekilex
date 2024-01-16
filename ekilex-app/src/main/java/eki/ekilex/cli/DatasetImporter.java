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

import eki.common.service.TransportService;
import eki.common.util.ConsolePromptUtil;
import eki.ekilex.cli.runner.DatasetImporterRunner;

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
public class DatasetImporter implements CommandLineRunner {

	private static Logger logger = LoggerFactory.getLogger(DatasetImporter.class);

	private static final String ARG_KEY_IMPTYPE = "imptype";

	private static final String ARG_KEY_IMPLOAD = "impload";

	private static final String ARG_KEY_IMPFILE = "impfile";

	private static final String OP_CREATE = "create";

	private static final String OP_APPEND = "append";

	private static final String OP_MIN = "min";

	private static final String OP_MAX = "max";

	@Autowired
	private ConfigurableApplicationContext context;

	@Autowired
	private TransportService transportService;

	@Autowired
	private DatasetImporterRunner datasetImporterRunner;

	//mvn spring-boot:run -P dsimp -D spring-boot.run.profiles=<dev|prod> -D spring-boot.run.arguments="imptype=<create|append> impload=<min|max> impfile="<import file path>"" 
	public static void main(String[] args) {
		logger.info("Application starting up");
		System.setProperty("org.jooq.no-logo", "true");
		SpringApplication.run(DatasetImporter.class, args);
		logger.info("Application finished");
	}

	@Override
	public void run(String... args) throws Exception {

		String importType = ConsolePromptUtil.getKeyValue(ARG_KEY_IMPTYPE, args);
		String importLoad = ConsolePromptUtil.getKeyValue(ARG_KEY_IMPLOAD, args);
		String importFilePath = ConsolePromptUtil.getKeyValue(ARG_KEY_IMPFILE, args);
		if (StringUtils.isBlank(importType)) {
			logger.warn("Please provide \"{}\" with arguments", ARG_KEY_IMPTYPE);
			context.close();
			return;
		}
		if (StringUtils.isBlank(importLoad)) {
			logger.warn("Please provide \"{}\" with arguments", ARG_KEY_IMPLOAD);
			context.close();
			return;
		}
		if (StringUtils.isBlank(importFilePath)) {
			logger.warn("Please provide \"{}\" with arguments", ARG_KEY_IMPFILE);
			context.close();
			return;
		}
		boolean isCreate = StringUtils.equals(OP_CREATE, importType);
		boolean isAppend = StringUtils.equals(OP_APPEND, importType);
		boolean isLoadMin = StringUtils.equals(OP_MIN, importLoad);
		boolean isLoadMax = StringUtils.equals(OP_MAX, importLoad);
		boolean isValidFilePath = isValidFilePath(importFilePath);
		if (!isValidFilePath) {
			logger.warn("Please provide valid \"{}\" with arguments", ARG_KEY_IMPFILE);
			context.close();
			return;
		}
		transportService.initialise();
		datasetImporterRunner.execute(isCreate, isAppend, isLoadMin, isLoadMax, importFilePath);
		context.close();
	}

	private boolean isValidFilePath(String filePath) {
		File file = new File(filePath);
		boolean fileExists = file.exists();
		if (!fileExists) {
			return false;
		}
		boolean isFile = file.isFile();
		if (!isFile) {
			return false;
		}
		return true;
	}
}
