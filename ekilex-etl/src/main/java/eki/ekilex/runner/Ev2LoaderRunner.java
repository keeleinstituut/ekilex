package eki.ekilex.runner;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class Ev2LoaderRunner extends AbstractLoaderRunner {

	private static String dataLang = "est";

	private static Logger logger = LoggerFactory.getLogger(Ev2LoaderRunner.class);

	@Override
	void initialise() throws Exception {
	}

	@Override
	String getDataset() {
		return "ev2";
	}

	@Transactional
	public void execute(String dataXmlFilePath, boolean doReports) throws Exception {

		logger.debug("Loading EV2...");

		long t1, t2;
		t1 = System.currentTimeMillis();

		t2 = System.currentTimeMillis();
		logger.debug("Done loading in {} ms", (t2 - t1));
	}

}
