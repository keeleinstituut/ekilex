package eki.ekilex.runner;

import java.io.InputStream;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class DbReInitialiserRunner extends AbstractLoaderRunner {

	private static Logger logger = LoggerFactory.getLogger(DbReInitialiserRunner.class);

	@Override
	void initialise() throws Exception {

	}

	@Transactional
	public void execute() throws Exception {

		logger.debug("Re-initialising database...");

		final String scriptFilePath1 = "sql/create_tables.sql";
		final String scriptFilePath2 = "sql/classifier_data_manual.sql";
		final String scriptFilePath3 = "sql/classifier_data_autom.sql";

		long t1, t2;
		t1 = System.currentTimeMillis();

		ClassLoader classLoader = this.getClass().getClassLoader();
		InputStream resourceFileInputStream;
		String sqlScript;

		logger.debug("{}...", scriptFilePath1);
		resourceFileInputStream = classLoader.getResourceAsStream(scriptFilePath1);
		sqlScript = getContent(resourceFileInputStream);
		basicDbService.executeScript(sqlScript);

		logger.debug("{}...", scriptFilePath2);
		resourceFileInputStream = classLoader.getResourceAsStream(scriptFilePath2);
		sqlScript = getContent(resourceFileInputStream);
		basicDbService.executeScript(sqlScript);

		logger.debug("{}...", scriptFilePath3);
		resourceFileInputStream = classLoader.getResourceAsStream(scriptFilePath3);
		sqlScript = getContent(resourceFileInputStream);
		basicDbService.executeScript(sqlScript);

		t2 = System.currentTimeMillis();
		logger.debug("Done in {} ms", (t2 - t1));
	}
}
