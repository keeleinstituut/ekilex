package eki.ekilex.manual;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import eki.common.util.ConsolePromptUtil;
import eki.ekilex.runner.XmlAnalyserRunner;

public class XmlAnalyser {
	
	private static Logger logger = LoggerFactory.getLogger(XmlAnalyser.class);

	public static void main(String[] args) {

		ConfigurableApplicationContext applicationContext = null;

		applicationContext = new ClassPathXmlApplicationContext("service-config.xml", "db-config.xml");
		XmlAnalyserRunner runner = applicationContext.getBean(XmlAnalyserRunner.class);

		try {
			applicationContext.registerShutdownHook();

			//  /projects/eki/data/dictionaries/qq2/qq22.xml
			String dataXmlFilePath = ConsolePromptUtil.promptDataFilePath("QQ2 type dictionary data file location? (/absolute/path/to/file.xml)");
			String xPathExpressionsStr = ConsolePromptUtil.promptStringValue("X-Path expressions? (/first/path,/second/path,...)");
			String[] xPathExpressions = StringUtils.split(xPathExpressionsStr, ',');

			runner.execute(dataXmlFilePath, xPathExpressions);

		} catch (Exception e) {
			logger.error("Unexpected behaviour of the system", e);
		} finally {
			applicationContext.close();
		}
	}
}
