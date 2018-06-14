package eki.ekilex.manual;

import eki.common.util.ConsolePromptUtil;
import eki.ekilex.runner.Ev2LoaderRunner;
import eki.ekilex.service.MabService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Ev2Loader {

	private static Logger logger = LoggerFactory.getLogger(Ev2Loader.class);

	public static void main(String[] args) {

		ConfigurableApplicationContext applicationContext;
		applicationContext = new ClassPathXmlApplicationContext("service-config.xml", "db-config.xml");
		Ev2LoaderRunner runner = applicationContext.getBean(Ev2LoaderRunner.class);

		try {
			applicationContext.registerShutdownHook();

			String ev21DataXmlFilePath = ConsolePromptUtil.promptDataFilePath("EV2-1 data file location? (/absolute/path/to/file.xml)");
			String ev22DataXmlFilePath = ConsolePromptUtil.promptDataFilePath("EV2-2 data file location? (/absolute/path/to/file.xml)");
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
			logger.info("Processing EV2-1...");
			runner.execute(ev21DataXmlFilePath, isAddReporting);
			logger.info("Processing EV2-2...");
			runner.execute(ev22DataXmlFilePath, isAddReporting);
		} catch (Exception e) {
			logger.error("Unexpected behaviour of the system", e);
		} finally {
			applicationContext.close();
		}
	}

}
