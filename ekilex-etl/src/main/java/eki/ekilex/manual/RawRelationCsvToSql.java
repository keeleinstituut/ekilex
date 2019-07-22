package eki.ekilex.manual;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import eki.ekilex.runner.RawRelationsCsvToSqlRunner;

public class RawRelationCsvToSql {

	private static Logger logger = LoggerFactory.getLogger(RawRelationCsvToSql.class);

	public static void main(String[] args) {

		if (ArrayUtils.isEmpty(args)) {
			logger.error("--------------------------------------------");
			logger.error("Please specify the location of the csv file.");
			logger.error("--------------------------------------------");

			return;
		}

		String fileName = args[0];
		ConfigurableApplicationContext applicationContext = null;

		applicationContext = new ClassPathXmlApplicationContext("service-config.xml", "db-config.xml");
		RawRelationsCsvToSqlRunner runner = applicationContext.getBean(RawRelationsCsvToSqlRunner.class);

		runner.setInputFileFullPath(fileName);

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
