package eki.ekilex.runner;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

@Component
public class AudioFileUpdaterRunner extends AbstractLoaderCommons implements InitializingBean {

	private static Logger logger = LoggerFactory.getLogger(AudioFileUpdaterRunner.class);

	private static final String SQL_SELECT_FORMS_OF_TYPE_NAME_OR_FOREIGN_PATH = "sql/select_forms_of_type_name_or_foreign.sql";

	private static final String SQL_UPDATE_FORM_AUDIO_FILE_NAME = "update " + FORM + " set audio_file = :audioFileName where id in (:formIds)";

	private String sqlSelectFormsOfTypeNameOrForeign;

	@Override
	public void afterPropertiesSet() throws Exception {

		ClassLoader classLoader = this.getClass().getClassLoader();
		InputStream resourceFileInputStream;

		resourceFileInputStream = classLoader.getResourceAsStream(SQL_SELECT_FORMS_OF_TYPE_NAME_OR_FOREIGN_PATH);
		sqlSelectFormsOfTypeNameOrForeign = getContent(resourceFileInputStream);
	}

	@Transactional
	public void update(String audioFilesIndexFilePath) throws Exception {

		logger.debug("Start update audio file names...");
		long t1, t2;
		t1 = System.currentTimeMillis();

		final String csvSeparatorStr = "" + CSV_SEPARATOR;
		List<String> lines = readFileLines(audioFilesIndexFilePath);
		int updatedAudioFileNameCount = 0;
		for (String line : lines) {
			String[] cells = StringUtils.splitByWholeSeparatorPreserveAllTokens(line, csvSeparatorStr);
			if (cells.length > 1) {
				String word = cells[0];
				String audioFileName = cells[3];
				List<Long> matchingWordFormIds = getMatchingWordFormIds(word);
				if (CollectionUtils.isNotEmpty(matchingWordFormIds)) {
					updateAudioFileName(matchingWordFormIds, audioFileName);
					updatedAudioFileNameCount++;
				}
			}
		}

		t2 = System.currentTimeMillis();
		logger.debug("Number of files updated {}", updatedAudioFileNameCount);
		logger.debug("Done in {} ms", (t2 - t1));
	}

	private List<Long> getMatchingWordFormIds(String word) throws Exception {

		Map<String, Object> params = new HashMap<>();
		params.put("word", word.toLowerCase());
		List<Long> formIds = basicDbService.queryList(sqlSelectFormsOfTypeNameOrForeign, params, Long.class);
		return formIds;
	}

	private void updateAudioFileName(List<Long> formIds, String audioFileName) {

		Map<String, Object> params = new HashMap<>();
		params.put("formIds", formIds);
		params.put("audioFileName", audioFileName);
		basicDbService.executeScript(SQL_UPDATE_FORM_AUDIO_FILE_NAME, params);
	}

}
