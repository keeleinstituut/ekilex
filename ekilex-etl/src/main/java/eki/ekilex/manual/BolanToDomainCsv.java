package eki.ekilex.manual;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import eki.common.util.ConsolePromptUtil;
import eki.ekilex.constant.SystemConstant;
import eki.ekilex.runner.BolanToDomainCsvRunner;

public class BolanToDomainCsv implements SystemConstant {

	private static Logger logger = LoggerFactory.getLogger(BolanToDomainCsv.class);

	public static void main(String[] args) throws Exception {

		ConfigurableApplicationContext applicationContext = null;

		applicationContext = new ClassPathXmlApplicationContext("service-config.xml", "db-config.xml");
		BolanToDomainCsvRunner runner = applicationContext.getBean(BolanToDomainCsvRunner.class);

		try {
			applicationContext.registerShutdownHook();

			// /projects/eki/data/bolan
			String classifierXsdRootFolderPath = ConsolePromptUtil.promptDataFolderPath("EKI classifiers XSD files root folder? (/absolute/path/to/folder/)");

			runner.execute(classifierXsdRootFolderPath);

		} catch (Exception e) {
			logger.error("Unexpected behaviour of the system", e);
		} finally {
			applicationContext.close();
		}

	}

}
