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

import eki.ekilex.cli.runner.SourceDatasetApplierRunner;

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
public class SourceDatasetApplier implements CommandLineRunner {

	private static Logger logger = LoggerFactory.getLogger(SourceDatasetApplier.class);

	@Autowired
	private ConfigurableApplicationContext context;

	@Autowired
	private SourceDatasetApplierRunner runner;

	//mvn spring-boot:run -P sdsa -D spring-boot.run.profiles=<dev|prod>
	public static void main(String[] args) {
		logger.info("Application starting up");
		System.setProperty("org.jooq.no-logo", "true");
		SpringApplication.run(SourceDatasetApplier.class, args);
		logger.info("Application finished");
	}

	@Override
	public void run(String... args) throws Exception {

		runner.execute();
		context.close();
	}

}
