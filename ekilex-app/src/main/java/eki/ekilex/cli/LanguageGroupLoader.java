package eki.ekilex.cli;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import eki.common.util.ConsolePromptUtil;
import eki.ekilex.cli.runner.LanguageGroupLoaderRunner;

@SpringBootApplication(scanBasePackages = {
		"eki.common",
		"eki.ekilex.cli.config",
		"eki.ekilex.cli.runner",
		"eki.ekilex.client",
		"eki.ekilex.service.core",
		"eki.ekilex.service.cli",
		"eki.ekilex.service.db",
		"eki.ekilex.service.util",
		"eki.ekilex.data"}, exclude = {HibernateJpaAutoConfiguration.class})
@EnableTransactionManagement
public class LanguageGroupLoader implements CommandLineRunner {

	private static Logger logger = LoggerFactory.getLogger(LanguageGroupLoader.class);

	private static final String ARG_KEY_IMPFOLDER = "impfolder";

	@Autowired
	private ConfigurableApplicationContext context;

	@Autowired
	private LanguageGroupLoaderRunner runner;

	//mvn spring-boot:run -P langr -D spring-boot.run.profiles=dev -D spring-boot.run.arguments="impfolder="/projects/ekilex/etym"" 
	public static void main(String[] args) {
		logger.info("Application starting up");
		System.setProperty("org.jooq.no-logo", "true");
		SpringApplication.run(LanguageGroupLoader.class, args);
		logger.info("Application finished");
	}

	@Override
	public void run(String... args) throws Exception {

		String importFolderPath = ConsolePromptUtil.getKeyValue(ARG_KEY_IMPFOLDER, args);
		if (StringUtils.isBlank(importFolderPath)) {
			logger.warn("Please provide \"{}\" value", ARG_KEY_IMPFOLDER);
			context.close();
			return;
		}
		try {
			runner.execute(importFolderPath);
		} catch (Exception e) {
			logger.error("Language groups loader failed with", e);
		} finally {
			context.close();
		}
	}
}
