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

import eki.common.exception.ConsolePromptException;
import eki.common.util.ConsolePromptUtil;
import eki.ekilex.cli.runner.MaintenanceRunner;

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
public class Maintenancer implements CommandLineRunner {

	private static Logger logger = LoggerFactory.getLogger(Maintenancer.class);

	private static final String ARG_KEY_TASK = "task";

	private static final String TASK_NAME_RECALC_ACCENTS = "recacc";

	@Autowired
	private ConfigurableApplicationContext context;

	@Autowired
	private MaintenanceRunner runner;

	//mvn spring-boot:run -P maint -D spring-boot.run.profiles=<dev|prod> -D spring-boot.run.arguments="task="<name>""
	public static void main(String[] args) {
		logger.info("Application starting up");
		System.setProperty("org.jooq.no-logo", "true");
		SpringApplication.run(Maintenancer.class, args);
		logger.info("Application finished");
	}

	@Override
	public void run(String... args) throws Exception {

		String taskName = ConsolePromptUtil.getKeyValue(ARG_KEY_TASK, args);
		if (StringUtils.isBlank(taskName)) {
			logger.warn("Please provide \"{}\" name", ARG_KEY_TASK);
			context.close();
			return;
		}
		try {
			if (StringUtils.equals(taskName, TASK_NAME_RECALC_ACCENTS)) {
				runner.unifySymbolsAndRecalcAccents();
			} else {
				throw new ConsolePromptException("Unsupported task: " + taskName);
			}
		} catch (Exception e) {
			logger.error("Maintenancer failed with", e);
		} finally {
			context.close();
		}
	}
}
