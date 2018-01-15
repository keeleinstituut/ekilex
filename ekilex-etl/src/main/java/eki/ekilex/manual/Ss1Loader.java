package eki.ekilex.manual;

import eki.common.util.ConsolePromptUtil;
import eki.ekilex.runner.Ss1LoaderRunner;
import eki.ekilex.service.MabService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Ss1Loader {

	private static Logger logger = LoggerFactory.getLogger(Ss1Loader.class);

	public static void main(String[] args) {

		ConfigurableApplicationContext applicationContext;
		applicationContext = new ClassPathXmlApplicationContext("service-config.xml", "db-config.xml");
		Ss1LoaderRunner runner = applicationContext.getBean(Ss1LoaderRunner.class);

		try {
			applicationContext.registerShutdownHook();

			String dataXmlFilePath = ConsolePromptUtil.promptDataFilePath("SS1 data file location? (/absolute/path/to/file.xml)");
			boolean isAddForms = ConsolePromptUtil.promptBooleanValue("Add forms? (y/n)");
			String mabFilePath = null;
			if (isAddForms) {
				mabFilePath = ConsolePromptUtil.promptDataFilePath("MAB data file location? (/absolute/path/to/file.xml)");
			}
			boolean isAddReporting = ConsolePromptUtil.promptBooleanValue("Generate import report files? (y/n)");
			String dataLang = "est";

			if (isAddForms) {
				MabService mabService = applicationContext.getBean(MabService.class);
				mabService.loadParadigms(mabFilePath, dataLang, isAddReporting);
			}
			runner.execute(dataXmlFilePath, isAddReporting);

		} catch (Exception e) {
			logger.error("Unexpected behaviour of the system", e);
		} finally {
			applicationContext.close();
		}
	}
}
