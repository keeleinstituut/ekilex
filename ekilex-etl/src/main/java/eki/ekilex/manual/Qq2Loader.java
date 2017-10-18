package eki.ekilex.manual;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import eki.common.util.ConsolePromptUtil;
import eki.ekilex.data.transform.Paradigm;
import eki.ekilex.runner.MabLoaderRunner;
import eki.ekilex.runner.Qq2LoaderRunner;

public class Qq2Loader {
	
	private static Logger logger = LoggerFactory.getLogger(Qq2Loader.class);

	public static void main(String[] args) {

		ConfigurableApplicationContext applicationContext = null;

		applicationContext = new ClassPathXmlApplicationContext("service-config.xml", "db-config.xml");
		Qq2LoaderRunner qq2Runner = applicationContext.getBean(Qq2LoaderRunner.class);
		MabLoaderRunner mabRunner = applicationContext.getBean(MabLoaderRunner.class);

		try {
			applicationContext.registerShutdownHook();

			//  /projects/eki/data/dictionaries/qq2/qq22.xml, /projects/eki/data/dictionaries/mab/mab.xml

			String qq2FilePath = ConsolePromptUtil.promptDataFilePath("QQ2 type dictionary data file location? (/absolute/path/to/file.xml)");
			String mabFilePath = null;
			String dataLang = ConsolePromptUtil.promptStringValue("Dictionary language? (est/rus/eng/lat/...)");
			boolean isAddForms = ConsolePromptUtil.promptBooleanValue("Add forms? (y/n)");
			if (isAddForms) {
				mabFilePath = ConsolePromptUtil.promptDataFilePath("MAB data file location? (/absolute/path/to/file.xml)");
			}
			String[] datasets = new String[] {"qq2"};
			boolean doReports = ConsolePromptUtil.promptBooleanValue("Compose reports? (y/n)");

			Map<String, List<Paradigm>> wordParadigmsMap = null;
			if (isAddForms) {
				wordParadigmsMap = mabRunner.execute(mabFilePath, dataLang);
			}
			qq2Runner.execute(qq2FilePath, dataLang, datasets, wordParadigmsMap, doReports);

		} catch (Exception e) {
			logger.error("Unexpected behaviour of the system", e);
		} finally {
			applicationContext.close();
		}
	}
}
