package eki.ekilex.cli;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import eki.ekilex.cli.runner.WordStressCorrectorRunner;

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
public class WordStressCorrector implements CommandLineRunner {

	private static Logger logger = LoggerFactory.getLogger(WordStressCorrector.class);

	@Autowired
	private ConfigurableApplicationContext context;

	@Autowired
	private WordStressCorrectorRunner runner;

	//mvn spring-boot:run -P wscor -D spring-boot.run.profiles=<dev|prod>
	public static void main(String[] args) {
		logger.info("Application starting up");
		System.setProperty("org.jooq.no-logo", "true");
		SpringApplication.run(WordStressCorrector.class, args);
		logger.info("Application finished");
	}

	@Override
	public void run(String... args) throws Exception {

		runner.execute();
		context.close();
	}

}
