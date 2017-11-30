package eki.ekilex.manual;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import eki.common.util.ConsolePromptUtil;
import eki.ekilex.runner.EstermSourceLoaderRunner;

public class EstermSourceLoader {

	private static Logger logger = LoggerFactory.getLogger(EstermSourceLoader.class);

	public static void main(String[] args) {

		ConfigurableApplicationContext applicationContext = null;

		applicationContext = new ClassPathXmlApplicationContext("service-config.xml", "db-config.xml");
		EstermSourceLoaderRunner runner = applicationContext.getBean(EstermSourceLoaderRunner.class);

		try {
			applicationContext.registerShutdownHook();

			//  /projects/eki/data/dictionaries/est/esterm.xml
			String dataXmlFilePath = ConsolePromptUtil.promptDataFilePath("Esterm type dictionary data file location? (/absolute/path/to/file.xml)");
			//boolean doReports = ConsolePromptUtil.promptBooleanValue("Compose reports? (y/n)");
			boolean doReports = false;

			runner.execute(dataXmlFilePath, doReports);

		} catch (Exception e) {
			logger.error("Unexpected behaviour of the system", e);
		} finally {
			applicationContext.close();
		}
	}
}
