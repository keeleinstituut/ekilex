package eki.ekilex.manual;

import eki.ekilex.runner.TermekiFilePumpRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TermekiFilePump {

	private static Logger logger = LoggerFactory.getLogger(TermekiFilePump.class);

	public static void main(String[] args) {

		ConfigurableApplicationContext applicationContext;

		applicationContext = new ClassPathXmlApplicationContext("service-config.xml", "db-termeki-config.xml");
		TermekiFilePumpRunner filePump = applicationContext.getBean(TermekiFilePumpRunner.class);

		try {
			applicationContext.registerShutdownHook();
			filePump.importFiles();
		} catch (Exception e) {
			logger.error("Unexpected behaviour of the system", e);
		} finally {
			applicationContext.close();
		}
	}

}
