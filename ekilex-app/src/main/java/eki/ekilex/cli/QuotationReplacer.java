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

import eki.ekilex.cli.runner.QuotationReplacerRunner;

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
public class QuotationReplacer implements CommandLineRunner {

	private static Logger logger = LoggerFactory.getLogger(QuotationReplacer.class);

	@Autowired
	private ConfigurableApplicationContext context;

	@Autowired
	private QuotationReplacerRunner runner;

	//mvn spring-boot:run -P qure -D spring-boot.run.profiles=dev 
	public static void main(String[] args) {
		logger.info("Application starting up");
		System.setProperty("org.jooq.no-logo", "true");
		SpringApplication.run(QuotationReplacer.class, args);
		logger.info("Application finished");
	}

	@Override
	public void run(String... args) throws Exception {

		try {
			runner.execute();
		} catch (Exception e) {
			logger.error("Quotation replacer failed with", e);
		} finally {
			context.close();
		}
	}
}
