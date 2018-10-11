package eki.ekilex.runner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import eki.common.constant.FormMode;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

@Component
public class VoiceFileUpdaterRunner extends AbstractLoaderRunner {

	private static Logger logger = LoggerFactory.getLogger(VoiceFileUpdaterRunner.class);

	// TODO: check over with client is this matching logic ok
	private final static String sqlFindWords = "select * from " + FORM + " where lower(value) = :word and mode = '" + FormMode.WORD.name() + "' and sound_file is null";
	private final static String sqlUpdateSoundFileNames = "update " + FORM + " set sound_file = :soundFileName where id in (:formIds)";

	@Override
	String getDataset() {
		return null;
	}

	@Override
	void initialise() throws Exception {
	}

	@Transactional
	public void update(String voiceFilesIndexFilePath) throws Exception {

		logger.debug("Start update sound file names...");
		long t1, t2;
		t1 = System.currentTimeMillis();

		List<String> lines = readFileLines(voiceFilesIndexFilePath);
		int numberOfFilesUpdated = 0;
		for (String line : lines) {
			String[] cells = line.split("\t");
			if (cells.length > 1) {
				String word = cells[0];
//				String language =  cells[1];
//				String vocalForm =  cells[2];
				String soundFileName =  cells[3];
				List<Long> matchingWordIds = findMatchingWords(word);
				if (!matchingWordIds.isEmpty()) {
					logger.debug("word : {}, sound file : {}", word, soundFileName);
					updateSoundFileName(matchingWordIds, soundFileName);
					numberOfFilesUpdated++;
				}
			}
		}

		t2 = System.currentTimeMillis();
		logger.debug("Number of files updated {}", numberOfFilesUpdated);
		logger.debug("Done in {} ms", (t2 - t1));
	}

	private List<Long> findMatchingWords(String word) throws Exception {

		Map<String, Object> params = new HashMap<>();
		params.put("word", word.toLowerCase());
		List<Map<String, Object>> wordObjects = basicDbService.queryList(sqlFindWords, params);
		return wordObjects.stream()
				.map(rec -> (Long)rec.get("id"))
				.collect(toList());
	}

	private void updateSoundFileName(List<Long> formIds, String soundFileName) {

		Map<String, Object> params = new HashMap<>();
		params.put("formIds", formIds);
		params.put("soundFileName", soundFileName);
		basicDbService.executeScript(sqlUpdateSoundFileNames, params);
	}

}
