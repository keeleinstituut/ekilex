package eki.ekilex.manual;

import eki.common.util.ConsolePromptUtil;
import eki.ekilex.data.transform.Paradigm;
import eki.ekilex.runner.MabLoaderRunner;
import eki.ekilex.runner.PsvLoaderRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PsvLoader {

	private static Logger logger = LoggerFactory.getLogger(PsvLoader.class);

	public static void main(String[] args) {

		ConfigurableApplicationContext applicationContext;
		applicationContext = new ClassPathXmlApplicationContext("service-config.xml", "db-config.xml");
		PsvLoaderRunner runner = applicationContext.getBean(PsvLoaderRunner.class);

		try {
			applicationContext.registerShutdownHook();

			String dataXmlFilePath = ConsolePromptUtil.promptDataFilePath("PSV data file location? (/absolute/path/to/file.xml)");
			boolean isAddReporting = ConsolePromptUtil.promptBooleanValue("Generate import report files? (y/n)");

			boolean isAddForms = ConsolePromptUtil.promptBooleanValue("Add forms? (y/n)");
			Map<String, List<Paradigm>> wordParadigmsMap = new HashMap<>();
			if (isAddForms) {
				String mabFilePath = ConsolePromptUtil.promptDataFilePath("MAB data file location? (/absolute/path/to/file.xml)");
				MabLoaderRunner mabRunner = applicationContext.getBean(MabLoaderRunner.class);
				wordParadigmsMap = mabRunner.execute(mabFilePath, "est");
			}

			String dataset = "psv";
			runner.execute(dataXmlFilePath, dataset, wordParadigmsMap, isAddReporting);
		} catch (Exception e) {
			logger.error("Unexpected behaviour of the system", e);
		} finally {
			applicationContext.close();
		}
	}
}
