package eki.ekilex.manual;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eki.ekilex.runner.FrequencyUpdateRunner;

public class FrequencyUpdater extends AbstractLoader {

	private static Logger logger = LoggerFactory.getLogger(FrequencyUpdater.class);

	public static void main(String[] args) {
		new FrequencyUpdater().execute(args);
	}

	@Override
	public void execute(String[] args) {
		try {
			initDefault();

			FrequencyUpdateRunner runner = getComponent(FrequencyUpdateRunner.class);

			String lexemeFrequencyFilePath = confService.getConfProperty("freq.lex.file");
			runner.executeLexemeFrequencyUpdate(lexemeFrequencyFilePath);

			String formFrequencyFilePath = confService.getConfProperty("freq.form.file");
			runner.executeFormFrequencyUpdate(formFrequencyFilePath);

		} catch (Exception e) {
			logger.error("Unexpected behaviour of the system", e);
		} finally {
			shutdown();
		}
	}
}
