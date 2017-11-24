package eki.ekilex.manual;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import eki.common.util.ConsolePromptUtil;
import eki.ekilex.runner.LenochToDomainCsvRunner;

public class LenochToDomainCsv {

	private static Logger logger = LoggerFactory.getLogger(LenochToDomainCsv.class);

	public static void main(String[] args) {

		ConfigurableApplicationContext applicationContext = null;

		applicationContext = new ClassPathXmlApplicationContext("service-config.xml", "db-config.xml");
		LenochToDomainCsvRunner runner = applicationContext.getBean(LenochToDomainCsvRunner.class);

		try {
			applicationContext.registerShutdownHook();

			// /projects/eki/data/valdkond-lenoch.csv

			String sourceCsvFilePath = ConsolePromptUtil.promptDataFilePath("Lenoch source CSV path (/absolute/path/to/file.csv)");

			runner.execute(sourceCsvFilePath);

		} catch (Exception e) {
			logger.error("Unexpected behaviour of the system", e);
		} finally {
			applicationContext.close();
		}

	}

}
