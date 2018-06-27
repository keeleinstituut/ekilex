package eki.ekilex.manual;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import eki.common.util.ConsolePromptUtil;
import eki.ekilex.runner.CollocLoaderRunner;

public class CollocLoader {

	private static Logger logger = LoggerFactory.getLogger(CollocLoader.class);

	public static void main(String[] args) {

		ConfigurableApplicationContext applicationContext = null;

		applicationContext = new ClassPathXmlApplicationContext("service-config.xml", "db-config.xml");
		CollocLoaderRunner runner = applicationContext.getBean(CollocLoaderRunner.class);

		try {
			applicationContext.registerShutdownHook();

			// /projects/eki/data/dictionaries/kol/kol-test_15-05-18.xml

			String dataXmlFilePath = ConsolePromptUtil.promptDataFilePath("Collocate data file location? (/absolute/path/to/file.xml)");
			boolean doReports = ConsolePromptUtil.promptBooleanValue("Compose reports? (y/n)");

			runner.execute(dataXmlFilePath, doReports);

		} catch (Exception e) {
			logger.error("Unexpected behaviour of the system", e);
		} finally {
			applicationContext.close();
		}
	}
}
