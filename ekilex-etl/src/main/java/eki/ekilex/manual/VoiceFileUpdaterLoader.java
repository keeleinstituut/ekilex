package eki.ekilex.manual;

import eki.ekilex.runner.VoiceFileUpdaterRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class VoiceFileUpdaterLoader extends AbstractLoader {

	private static Logger logger = LoggerFactory.getLogger(VoiceFileUpdaterLoader.class);

	public static void main(String[] args) {
		new VoiceFileUpdaterLoader().execute(args);
	}

	@Override
	void execute(String[] args) {
		try {
			initDefault();

			VoiceFileUpdaterRunner voiceFileUpdaterRunner = getComponent(VoiceFileUpdaterRunner.class);
			String voiceFilesIndexFilePath = confService.getMandatoryConfProperty("voice.index.file");
			voiceFileUpdaterRunner.update(voiceFilesIndexFilePath);
		} catch (Exception e) {
			logger.error("Unexpected behaviour of the system", e);
		} finally {
			shutdown();
		}
	}

}
