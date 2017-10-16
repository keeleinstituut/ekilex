package eki.ekilex.runner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class PsvLoaderRunner extends AbstractLoaderRunner {

	private static Logger logger = LoggerFactory.getLogger(PsvLoaderRunner.class);

	@Override
	void initialise() throws Exception {
	}

	public void execute(String dataXmlFilePath, String dataLang, String[] datasets) {
		logger.info("Starting import");
		long t1, t2;
		t1 = System.currentTimeMillis();

		t2 = System.currentTimeMillis();
		logger.debug("Done in {} ms", (t2 - t1));
	}
}
