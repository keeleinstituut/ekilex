package eki.ekilex.cli;

import java.io.File;
import java.time.ZoneId;
import java.util.TimeZone;

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
import eki.ekilex.cli.runner.OdCompWordLoaderRunner;

@SpringBootApplication(scanBasePackages = {
		"eki.common",
		"eki.ekilex.cli.config",
		"eki.ekilex.cli.runner",
		"eki.ekilex.service.core",
		"eki.ekilex.service.cli",
		"eki.ekilex.service.db",
		"eki.ekilex.service.util",
		"eki.ekilex.data"}, exclude = {HibernateJpaAutoConfiguration.class})
@EnableTransactionManagement
public class OdCompWordLoader implements CommandLineRunner {

	private static Logger logger = LoggerFactory.getLogger(OdCompWordLoader.class);

	private static final String ARG_KEY_IMPFILE = "impfile";

	@Autowired
	private ConfigurableApplicationContext context;

	@Autowired
	private OdCompWordLoaderRunner runner;

	//mvn spring-boot:run -P odcwl -D spring-boot.run.profiles=<dev|prod> -D spring-boot.run.arguments="impfile="<import file path>"" 
	public static void main(String[] args) {
		logger.info("Application starting up");
		System.setProperty("org.jooq.no-logo", "true");
		TimeZone.setDefault(TimeZone.getTimeZone(ZoneId.of("Europe/Tallinn")));
		SpringApplication.run(OdCompWordLoader.class, args);
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
		boolean isValidFilePath = isValidFilePath(importFilePath);
		if (!isValidFilePath) {
			logger.warn("Please provide valid \"{}\" with arguments", ARG_KEY_IMPFILE);
			context.close();
			return;
		}
		try {
			runner.execute(importFilePath);
		} catch (Exception e) {
			logger.error("Word OD usage loader failed with", e);
		} finally {
			context.close();
		}
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
