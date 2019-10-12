package eki.ekilex.manual;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eki.ekilex.runner.RawRelationsCsvToSqlGeneratorRunner;

public class RawRelationCsvToSqlGenerator extends AbstractLoader {

	private static Logger logger = LoggerFactory.getLogger(RawRelationCsvToSqlGenerator.class);

	@Override
	public void execute(String[] args) {
		try {
			initDefault();

			if (ArrayUtils.isEmpty(args)) {
				logger.error("--------------------------------------------");
				logger.error("Please specify the location of the csv file.");
				logger.error("--------------------------------------------");

				return;
			}

			RawRelationsCsvToSqlGeneratorRunner runner = getComponent(RawRelationsCsvToSqlGeneratorRunner.class);
			String fileName = args[0];

			runner.execute(fileName);

		} catch (Exception e) {
			logger.error("Unexpected behaviour of the system", e);
		} finally {
			shutdown();
		}
	}

	public static void main(String[] args) {
		new RawRelationCsvToSqlGenerator().execute(args);
	}

}
