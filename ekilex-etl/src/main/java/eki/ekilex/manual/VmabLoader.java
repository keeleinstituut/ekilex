package eki.ekilex.manual;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eki.ekilex.runner.VmabLoaderRunner;

public class VmabLoader extends AbstractLoader {

	private static Logger logger = LoggerFactory.getLogger(VmabLoader.class);

	public static void main(String[] args) {
		new VmabLoader().execute();
	}

	@Override
	void execute() {
		try {
			initDefault();

			VmabLoaderRunner datasetRunner = getComponent(VmabLoaderRunner.class);
			boolean doReports = doReports();

			String vmabFilePath = getMandatoryConfProperty("vmab.data.file");
			datasetRunner.execute(vmabFilePath, doReports);

		} catch (Exception e) {
			logger.error("Unexpected behaviour of the system", e);
		} finally {
			shutdown();
		}
	}
}
