package eki.ekilex.manual;

import eki.ekilex.runner.AudioFileUpdaterRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AudioFileUpdaterLoader extends AbstractLoader {

	private static Logger logger = LoggerFactory.getLogger(AudioFileUpdaterLoader.class);

	public static void main(String[] args) {
		new AudioFileUpdaterLoader().execute(args);
	}

	@Override
	void execute(String[] args) {
		try {
			initDefault();

			AudioFileUpdaterRunner audioFileUpdaterRunner = getComponent(AudioFileUpdaterRunner.class);
			String audioFilesIndexFilePath = confService.getMandatoryConfProperty("audio.index.file");
			audioFileUpdaterRunner.update(audioFilesIndexFilePath);
		} catch (Exception e) {
			logger.error("Unexpected behaviour of the system", e);
		} finally {
			shutdown();
		}
	}

}
