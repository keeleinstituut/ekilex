package eki.ekilex.manual;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import eki.common.util.ConsolePromptUtil;
import eki.ekilex.runner.MilitermToDomainCsvRunner;

public class MilitermToDomainCsv {

	private static Logger logger = LoggerFactory.getLogger(MilitermToDomainCsv.class);

	public static void main(String[] args) {

		ConfigurableApplicationContext applicationContext = null;

		applicationContext = new ClassPathXmlApplicationContext("service-config.xml", "db-config.xml");
		MilitermToDomainCsvRunner runner = applicationContext.getBean(MilitermToDomainCsvRunner.class);

		try {
			applicationContext.registerShutdownHook();

			// /projects/eki/data/valdkond-militerm.csv

			String sourceCsvFilePath = ConsolePromptUtil.promptDataFilePath("Militerm source CSV path (/absolute/path/to/file.csv)");

			runner.execute(sourceCsvFilePath);

		} catch (Exception e) {
			logger.error("Unexpected behaviour of the system", e);
		} finally {
			applicationContext.close();
		}

	}

}
