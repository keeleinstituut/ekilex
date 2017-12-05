package eki.ekilex.manual;

import eki.common.util.ConsolePromptUtil;
import eki.ekilex.runner.TermekiRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TermekiLoader {

	private static Logger logger = LoggerFactory.getLogger(TermekiLoader.class);

	public static void main(String[] args) {

		ConfigurableApplicationContext applicationContext;

		applicationContext = new ClassPathXmlApplicationContext("service-config.xml", "db-config.xml", "db-termeki-config.xml");
		TermekiRunner runner = applicationContext.getBean(TermekiRunner.class);

		try {
			applicationContext.registerShutdownHook();

			boolean doBatchLoad = ConsolePromptUtil.promptBooleanValue("Load all termbases (y) or just single one (n) from TERMEKI ?");

			if (doBatchLoad) {
				String termbasesCsvFilePath = ConsolePromptUtil.promptDataFilePath("Termbases codes CSV file location? (/absolute/path/to/file.csv)");
				runner.batchLoad(termbasesCsvFilePath);
			} else {
				Integer termbaseId = ConsolePromptUtil.promptIntValue("Numeric ID of the termbase in TERMEKI ?");
				String ekilexCode = ConsolePromptUtil.promptStringValue("Dataset code in EKILEX ? (for example pol/lon/ett/...)");
				runner.execute(termbaseId, ekilexCode);  // 2633923, "vlk"<- Veterinaarmeditsiin ja loomakasvatus
			}
		} catch (Exception e) {
			logger.error("Unexpected behaviour of the system", e);
		} finally {
			applicationContext.close();
		}
	}
}
