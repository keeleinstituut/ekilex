package eki.ekilex.cli;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import eki.common.util.ConsolePromptUtil;
import eki.ekilex.cli.runner.LexemeProficiencyLevelImporterRunner;
import eki.ekilex.service.QueueHandlerService;
import eki.ekilex.service.ClassifierService;
import eki.ekilex.service.DatasetService;
import eki.ekilex.service.EmailService;
import eki.ekilex.service.MaintenanceService;
import eki.ekilex.service.PermissionService;
import eki.ekilex.service.QueueService;
import eki.ekilex.service.TagService;
import eki.ekilex.service.UserService;

@SpringBootApplication
@ComponentScan(basePackages = {
		"eki.common",
		"eki.ekilex.cli.config",
		"eki.ekilex.cli.runner",
		"eki.ekilex.service",
		"eki.ekilex.data"}, excludeFilters = {
				@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
						QueueService.class,
						QueueHandlerService.class,
						MaintenanceService.class,
						ClassifierService.class,
						DatasetService.class,
						EmailService.class,
						UserService.class,
						PermissionService.class,
						TagService.class})})
@EnableTransactionManagement
public class LexemeProficiencyLevelImporter implements CommandLineRunner {

	private static Logger logger = LoggerFactory.getLogger(LexemeProficiencyLevelImporter.class);

	private static final String ARG_KEY_IMPFILE = "impfile";

	@Autowired
	private ConfigurableApplicationContext context;

	@Autowired
	private LexemeProficiencyLevelImporterRunner runner;

	//mvn spring-boot:run -P plimp -D spring-boot.run.profiles=<dev|prod> -D spring-boot.run.arguments="impfile="<import file path>"" 
	public static void main(String[] args) {
		logger.info("Application starting up");
		System.setProperty("org.jooq.no-logo", "true");
		SpringApplication.run(LexemeProficiencyLevelImporter.class, args);
		logger.info("Application finished");
	}

	@Override
	public void run(String... args) throws Exception {

		String importFilePath = ConsolePromptUtil.getKeyValue(ARG_KEY_IMPFILE, args);
		if (StringUtils.isBlank(importFilePath)) {
			logger.warn("Please provide \"{}\" with arguments", ARG_KEY_IMPFILE);
			context.close();
			return;
		}
		runner.execute(importFilePath);
		context.close();
	}

}
