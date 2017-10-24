package eki.ekilex.manual;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import eki.common.util.ConsolePromptUtil;
import eki.ekilex.runner.EstermLoaderRunner;

public class EstermLoader {

	private static Logger logger = LoggerFactory.getLogger(EstermLoader.class);

	public static void main(String[] args) {

		ConfigurableApplicationContext applicationContext = null;

		applicationContext = new ClassPathXmlApplicationContext("service-config.xml", "db-config.xml");
		EstermLoaderRunner runner = applicationContext.getBean(EstermLoaderRunner.class);

		try {
			applicationContext.registerShutdownHook();

			//  /projects/eki/data/dictionaries/esterm_reformat.xml
			String dataXmlFilePath = ConsolePromptUtil.promptDataFilePath("Esterm type dictionary data file location? (/absolute/path/to/file.xml)");
			String dataLang = ConsolePromptUtil.promptStringValue("Dictionary language? (est/rus/eng/lat/...)");
			String dataset = "est";

			runner.execute(dataXmlFilePath, dataLang, dataset);

		} catch (Exception e) {
			logger.error("Unexpected behaviour of the system", e);
		} finally {
			applicationContext.close();
		}
	}
}
