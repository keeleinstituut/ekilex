package eki.ekilex.manual;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import eki.ekilex.runner.Qq2LoaderRunner;

public class Qq2Loader {
	
	private static Logger logger = LoggerFactory.getLogger(Qq2Loader.class);

	public static void main(String[] args) {

		ConfigurableApplicationContext applicationContext = null;

		applicationContext = new ClassPathXmlApplicationContext("service-config.xml", "db-config.xml");
		Qq2LoaderRunner runner = applicationContext.getBean(Qq2LoaderRunner.class);

		try {
			applicationContext.registerShutdownHook();

			final String dataXmlFilePath = "/projects/eki/data/dictionaries/qq2/qq21.xml";
			final String dataLang = "est";
			final String[] dataset = new String[] {"qq2"};

			runner.execute(dataXmlFilePath, dataLang, dataset);

		} catch (Exception e) {
			logger.error("Unexpected behaviour of the system", e);
		} finally {
			applicationContext.close();
		}
	}

}
