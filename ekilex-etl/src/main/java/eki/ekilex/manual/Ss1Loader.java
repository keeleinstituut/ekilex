package eki.ekilex.manual;

import eki.common.util.ConsolePromptUtil;
import eki.ekilex.data.transform.Paradigm;
import eki.ekilex.runner.MabLoaderRunner;
import eki.ekilex.runner.Ss1LoaderRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

			Map<String, List<Paradigm>> wordParadigmsMap = new HashMap<>();
			if (isAddForms) {
				MabLoaderRunner mabRunner = applicationContext.getBean(MabLoaderRunner.class);
				wordParadigmsMap = mabRunner.execute(mabFilePath, dataLang);
			}
			runner.execute(dataXmlFilePath, wordParadigmsMap, isAddReporting);

		} catch (Exception e) {
			logger.error("Unexpected behaviour of the system", e);
		} finally {
			applicationContext.close();
		}
	}
}
