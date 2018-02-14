package eki.ekilex.manual;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import eki.common.util.ConsolePromptUtil;
import eki.ekilex.runner.CollocEnricherRunner;

public class CollocEnricher {

	private static Logger logger = LoggerFactory.getLogger(CollocEnricher.class);

	public static void main(String[] args) {

		ConfigurableApplicationContext applicationContext = null;

		applicationContext = new ClassPathXmlApplicationContext("service-config.xml", "db-config.xml");
		CollocEnricherRunner runner = applicationContext.getBean(CollocEnricherRunner.class);

		try {
			applicationContext.registerShutdownHook();

			//  /projects/eki/data/dictionaries/kol/kol_22-01-18.xml
			String dataXmlFilePath = ConsolePromptUtil.promptDataFilePath("Collocate data file location? (/absolute/path/to/file.xml)");
			String dataLang = ConsolePromptUtil.promptStringValue("Dictionary language? (est/rus/eng/lat/...)");
			String targetDataset = ConsolePromptUtil.promptStringValue("Target dataset code? (qq2/psv/ss1/...)");
			boolean doReports = ConsolePromptUtil.promptBooleanValue("Compose reports? (y/n)");

			runner.execute(dataXmlFilePath, dataLang, targetDataset, doReports);

		} catch (Exception e) {
			logger.error("Unexpected behaviour of the system", e);
		} finally {
			applicationContext.close();
		}
	}
}
