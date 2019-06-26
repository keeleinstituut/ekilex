package eki.ekilex.manual;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import eki.ekilex.runner.RawRelationsCsvToSqlRunner;

public class RawRelationCsvToSql {

	private static Logger logger = LoggerFactory.getLogger(RawRelationCsvToSql.class);

	public static void main(String[] args) {

		ConfigurableApplicationContext applicationContext = null;

		applicationContext = new ClassPathXmlApplicationContext("service-config.xml", "db-config.xml");
		RawRelationsCsvToSqlRunner runner = applicationContext.getBean(RawRelationsCsvToSqlRunner.class);

		try {
			applicationContext.registerShutdownHook();

			runner.execute();

		} catch (Exception e) {
			logger.error("Unexpected behaviour of the system", e);
		} finally {
			applicationContext.close();
		}
	}

}
