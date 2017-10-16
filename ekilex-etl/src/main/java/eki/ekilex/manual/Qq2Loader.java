package eki.ekilex.manual;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import eki.common.util.ConsolePromptUtil;
import eki.ekilex.runner.Qq2LoaderRunner;

public class Qq2Loader {
	
	private static Logger logger = LoggerFactory.getLogger(Qq2Loader.class);

	public static void main(String[] args) {

		ConfigurableApplicationContext applicationContext = null;

		applicationContext = new ClassPathXmlApplicationContext("service-config.xml", "db-config.xml");
		Qq2LoaderRunner runner = applicationContext.getBean(Qq2LoaderRunner.class);

		try {
			applicationContext.registerShutdownHook();

			//  /projects/eki/data/dictionaries/qq2/qq22.xml
			String dataXmlFilePath = ConsolePromptUtil.promptDataFilePath("QQ2 type dictionary data file location? (/absolute/path/to/file.xml)");
			String dataLang = ConsolePromptUtil.promptStringValue("Dictionary language? (est/rus/eng/lat/...)");
			String[] datasets = new String[] {"qq2"};
			boolean doReports = ConsolePromptUtil.promptBooleanValue("Compose reports? (y/n)");

			runner.execute(dataXmlFilePath, dataLang, datasets, doReports);

		} catch (Exception e) {
			logger.error("Unexpected behaviour of the system", e);
		} finally {
			applicationContext.close();
		}
	}
}
