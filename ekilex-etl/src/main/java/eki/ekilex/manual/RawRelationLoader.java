package eki.ekilex.manual;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eki.ekilex.runner.RawRelationLoaderRunner;

public class RawRelationLoader extends AbstractLoader {

	private static Logger logger = LoggerFactory.getLogger(RawRelationLoader.class);

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

			RawRelationLoaderRunner runner = getComponent(RawRelationLoaderRunner.class);
			String fileName = args[0];

			runner.execute(fileName);

		} catch (Exception e) {
			logger.error("Unexpected behaviour of the system", e);
		} finally {
			shutdown();
		}
	}

	public static void main(String[] args) {
		new RawRelationLoader().execute(args);
	}

}
