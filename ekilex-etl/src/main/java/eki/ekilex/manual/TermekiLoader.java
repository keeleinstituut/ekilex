package eki.ekilex.manual;

import eki.ekilex.runner.TermekiRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TermekiLoader {

	private static Logger logger = LoggerFactory.getLogger(TermekiLoader.class);

	public static void main(String[] args) {

		ConfigurableApplicationContext applicationContext = null;

		applicationContext = new ClassPathXmlApplicationContext("service-config.xml", "db-config.xml", "db-termeki-config.xml");
		TermekiRunner runner = applicationContext.getBean(TermekiRunner.class);

		try {
			applicationContext.registerShutdownHook();
			runner.execute();
		} catch (Exception e) {
			logger.error("Unexpected behaviour of the system", e);
		} finally {
			applicationContext.close();
		}
	}
}
