package eki.ekilex.manual;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import eki.common.util.ConsolePromptUtil;
import eki.ekilex.constant.SystemConstant;
import eki.ekilex.runner.XsdToClassifierCsvRunner;

public class XsdToClassifierCsv implements SystemConstant {

	private static Logger logger = LoggerFactory.getLogger(XsdToClassifierCsv.class);

	public static void main(String[] args) throws Exception {

		ConfigurableApplicationContext applicationContext = null;

		applicationContext = new ClassPathXmlApplicationContext("service-config.xml", "db-config.xml");
		XsdToClassifierCsvRunner runner = applicationContext.getBean(XsdToClassifierCsvRunner.class);

		try {
			applicationContext.registerShutdownHook();

			// /projects/eki/data/dictionaries/qq2/qq2_tyybid.xsd
			// /projects/eki/data/dictionaries/ps2/ps2_tyybid.xsd

			String classifierXsdFilePath;
			String operation = ConsolePromptUtil.promptStringValue("Load EKI classifiers (L) or verify existing mappings (V)");
			if (StringUtils.equals(operation, "L")) {
				classifierXsdFilePath = ConsolePromptUtil.promptDataFilePath("EKI classifiers XSD file location? (/absolute/path/to/file.xml)");
			} else if (StringUtils.equals(operation, "V")) {
				classifierXsdFilePath = null;
			} else {
				throw new Exception("Unknown operation \"" + operation + "\"");
			}

			runner.execute(classifierXsdFilePath);

		} catch (Exception e) {
			logger.error("Unexpected behaviour of the system", e);
		} finally {
			applicationContext.close();
		}

	}

}
