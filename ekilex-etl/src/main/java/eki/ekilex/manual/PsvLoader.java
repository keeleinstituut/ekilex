package eki.ekilex.manual;

import eki.common.util.ConsolePromptUtil;
import eki.ekilex.runner.PsvLoaderRunner;
import eki.ekilex.service.MabService;
import eki.ekilex.service.WordMatcherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class PsvLoader {

	private static Logger logger = LoggerFactory.getLogger(PsvLoader.class);

	public static void main(String[] args) {

		ConfigurableApplicationContext applicationContext;
		applicationContext = new ClassPathXmlApplicationContext("service-config.xml", "db-config.xml");
		PsvLoaderRunner runner = applicationContext.getBean(PsvLoaderRunner.class);

		try {
			applicationContext.registerShutdownHook();

			String dataXmlFilePath = ConsolePromptUtil.promptDataFilePath("PSV data file location? (/absolute/path/to/file.xml)");
			boolean isAddForms = ConsolePromptUtil.promptBooleanValue("Add forms? (y/n)");
			String mabFilePath = null;
			if (isAddForms) {
				mabFilePath = ConsolePromptUtil.promptDataFilePath("MAB data file location? (/absolute/path/to/file.xml)");
			}
			boolean isCombineDatasets = ConsolePromptUtil.promptBooleanValue("Combining PSV with QQ2 datset? (y/n)");
			String guidMappingFilePath = null;
			if (isCombineDatasets) {
				guidMappingFilePath = ConsolePromptUtil.promptDataFilePath("GUID mapping file location? (/absolute/path/to/file.dat)");
			}
			boolean isAddReporting = ConsolePromptUtil.promptBooleanValue("Generate import report files? (y/n)");
			String dataLang = "est";

			if (isAddForms) {
				MabService mabService = applicationContext.getBean(MabService.class);
				mabService.loadParadigms(mabFilePath, dataLang, isAddReporting);
			}
			if (isCombineDatasets) {
				WordMatcherService wordMatcherService = applicationContext.getBean(WordMatcherService.class);
				wordMatcherService.load(guidMappingFilePath);
			}
			runner.execute(dataXmlFilePath, isAddReporting);

		} catch (Exception e) {
			logger.error("Unexpected behaviour of the system", e);
		} finally {
			applicationContext.close();
		}
	}
}
