package eki.ekilex.manual;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import eki.common.util.ConsolePromptUtil;
import eki.ekilex.runner.Qq2LoaderRunner;
import eki.ekilex.service.MabService;

public class Qq2Loader {

	private static Logger logger = LoggerFactory.getLogger(Qq2Loader.class);

	public static void main(String[] args) {

		ConfigurableApplicationContext applicationContext = null;

		applicationContext = new ClassPathXmlApplicationContext("service-config.xml", "db-config.xml");
		Qq2LoaderRunner qq2Runner = applicationContext.getBean(Qq2LoaderRunner.class);

		try {
			applicationContext.registerShutdownHook();

			// /projects/eki/data/dictionaries/qq2/qq23.xml
			// /projects/eki/data/dictionaries/mab/mab.xml

			String qq2FilePath = ConsolePromptUtil.promptDataFilePath("QQ2 type dictionary data file location? (/absolute/path/to/file.xml)");
			String dataLang = ConsolePromptUtil.promptStringValue("Dictionary language? (est/rus/eng/lat/...)");
			boolean isAddForms = ConsolePromptUtil.promptBooleanValue("Add forms? (y/n)");
			String mabFilePath = null;
			if (isAddForms) {
				mabFilePath = ConsolePromptUtil.promptDataFilePath("MAB data file location? (/absolute/path/to/file.xml)");
			}
			boolean doReports = ConsolePromptUtil.promptBooleanValue("Compose reports? (y/n)");

			if (isAddForms) {
				MabService mabService = applicationContext.getBean(MabService.class);
				mabService.loadParadigms(mabFilePath, dataLang, doReports);
			}
			qq2Runner.execute(qq2FilePath, dataLang, doReports);

		} catch (Exception e) {
			logger.error("Unexpected behaviour of the system", e);
		} finally {
			applicationContext.close();
		}
	}
}
