package eki.ekilex.cli;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import eki.ekilex.cli.runner.MediaServerTestbenchRunner;

// TODO remove after media server full integration
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
public class MediaServerTestbench implements CommandLineRunner {

	private static Logger logger = LoggerFactory.getLogger(MediaServerTestbench.class);

	@Autowired
	private ConfigurableApplicationContext context;

	@Autowired
	private MediaServerTestbenchRunner runner;

	//mvn spring-boot:run -P msrvt -D spring-boot.run.profiles=dev 
	public static void main(String[] args) {
		logger.info("Application starting up");
		System.setProperty("org.jooq.no-logo", "true");
		SpringApplication.run(MediaServerTestbench.class, args);
		logger.info("Application finished");
	}

	@Override
	public void run(String... args) throws Exception {

		try {
			runner.execute();
		} catch (Exception e) {
			logger.error("Error", e);
		} finally {
			context.close();
		}
	}
}
