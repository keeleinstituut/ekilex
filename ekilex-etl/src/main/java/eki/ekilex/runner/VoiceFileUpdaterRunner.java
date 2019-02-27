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
public class VoiceFileUpdaterRunner extends AbstractLoaderCommons {

	private static Logger logger = LoggerFactory.getLogger(VoiceFileUpdaterRunner.class);

	private final static String sqlFindWords =
			"select distinct f.* "
					+ "from " + FORM + " f "
					+ "join " + PARADIGM + " p on p.id = f.paradigm_id "
					+ "join " + WORD + " w on w.id = p.word_id "
					+ "join " + LEXEME + " l on l.word_id = w.id "
					+ "where "
					+ "lower(f.value) = :word "
					+ "and f.mode = '" + FormMode.WORD.name() + "' "
					+ "and f.sound_file is null "
					+ "and (exists(select lp.id from " + LEXEME_POS + " lp where lp.lexeme_id = l.id and lp.pos_code = 'prop') "
							+ "or exists(select wt.id from " + WORD_WORD_TYPE + " wt where wt.word_id = w.id and wt.word_type_code = 'z'))";

	private final static String sqlUpdateSoundFileNames = "update " + FORM + " set sound_file = :soundFileName where id in (:formIds)";

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
