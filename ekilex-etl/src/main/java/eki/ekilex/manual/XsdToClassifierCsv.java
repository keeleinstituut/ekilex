package eki.ekilex.manual;

import java.util.ArrayList;
import java.util.List;

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

			List<String> classifierXsdFilePaths;
			String operation = ConsolePromptUtil.promptStringValue("Load EKI classifiers (L) or verify existing mappings (V)");
			if (StringUtils.equals(operation, "L")) {
				
				String classifierXsdFilePathsStr = ConsolePromptUtil.promptStringValue(
						"comma separated list of EKI classifiers XSD file paths (/absolute/path/to/file1.xsd,/absolute/path/to/file2.xsd)");
				classifierXsdFilePathsStr = StringUtils.remove(classifierXsdFilePathsStr, ' ');
				String[] classifierXsdFilePathsArr = StringUtils.split(classifierXsdFilePathsStr, ',');
				classifierXsdFilePaths = new ArrayList<>();
				for (String classifierXsdFilePath : classifierXsdFilePathsArr) {
					classifierXsdFilePath = classifierXsdFilePath.trim();
					classifierXsdFilePaths.add(classifierXsdFilePath);
				}
			} else if (StringUtils.equals(operation, "V")) {
				classifierXsdFilePaths = null;
			} else {
				throw new Exception("Unknown operation \"" + operation + "\"");
			}

			runner.execute(classifierXsdFilePaths);

		} catch (Exception e) {
			logger.error("Unexpected behaviour of the system", e);
		} finally {
			applicationContext.close();
		}

	}

}
