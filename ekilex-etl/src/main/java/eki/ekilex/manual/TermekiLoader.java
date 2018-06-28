package eki.ekilex.manual;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eki.common.util.ConsolePromptUtil;
import eki.ekilex.runner.TermekiRunner;

public class TermekiLoader extends AbstractLoader {

	private static Logger logger = LoggerFactory.getLogger(TermekiLoader.class);

	public static void main(String[] args) {
		new TermekiLoader().execute();
	}

	@Override
	void execute() {
		try {
			initWithTermeki();

			TermekiRunner datasetRunner = getComponent(TermekiRunner.class);

			boolean doBatchLoad = ConsolePromptUtil.promptBooleanValue("Load all termbases (y) or just single one (n) from TERMEKI ?");

			if (doBatchLoad) {
				String termbasesCsvFilePath = getMandatoryConfProperty("termeki.data.file");
				datasetRunner.batchLoad(termbasesCsvFilePath);
			} else {
				Integer termbaseId = ConsolePromptUtil.promptIntValue("Numeric ID of the termbase in TERMEKI ?");
				String ekilexCode = ConsolePromptUtil.promptStringValue("Dataset code in EKILEX ? (for example pol/lon/ett/...)");
				datasetRunner.execute(termbaseId, ekilexCode); // 2633923, "vlk"<- Veterinaarmeditsiin ja loomakasvatus
			}
		} catch (Exception e) {
			logger.error("Unexpected behaviour of the system", e);
		} finally {
			shutdown();
		}
	}

}
