package eki.ekilex.service;

import static eki.ekilex.constant.SystemConstant.CSV_SEPARATOR;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eki.common.constant.FormMode;
import eki.common.constant.TableName;
import eki.common.service.db.BasicDbService;

@Deprecated
@Service
public class WordMatcherService implements TableName {

	private static Logger logger = LoggerFactory.getLogger(WordMatcherService.class);

	private final static String sqlWordByGuidAndValue =
			"select w.* from " + WORD + " w join " + PARADIGM + " p on p.word_id = w.id join " + FORM + " f on f.paradigm_id = p.id join " + WORD_GUID + " g on g.word_id = w.id "
					+ "where g.guid = :guid and f.value = :wordValue and f.mode = '" + FormMode.WORD.name() + "'";
	private final static String sqlSelectWordsByValue =
			"select w.*, p.id as paradigm_id from " + WORD + " w join " + PARADIGM + " p on p.word_id = w.id join " + FORM + " f on f.paradigm_id = p.id "
					+ "where f.value = :wordValue and f.mode = '" + FormMode.WORD.name() + "'";

	@Autowired
	protected BasicDbService basicDbService;

	private boolean isEnabled;

	private Map<String, String> guidMappings = new HashMap<>();

	public Map<String, String> load(String pathToGuidCsvFile) throws Exception {

		isEnabled = true;
		guidMappings = new HashMap<>();
		List<String> lines;
		try (InputStream guidCsvFile = new FileInputStream(pathToGuidCsvFile)) {
			lines =  IOUtils.readLines(guidCsvFile, UTF_8);
		}
		logger.info("Loading GUID mappings");
		for (String line : lines) {
			String[] columns = StringUtils.split(line, CSV_SEPARATOR);
			if (columns != null && columns.length > 1) {
				String guid = columns[0].toLowerCase();
				String mappedGuid = columns[1].toLowerCase();
				guidMappings.put(guid, mappedGuid);
			}
		}
		logger.info("GUID mappings loaded, {} rows", lines.size());
		return guidMappings;
	}

	public Long getMatchingWordId(String wordGuid, String wordValue, List<String> wordForms) throws Exception {

		Long wordId = getWordIdByGuidAndValue(wordGuid, wordValue);
		if (wordId == null) {
			wordId = getWordIdByForms(wordValue, wordForms);
		}
		return wordId;
	}

	public Long getWordIdByGuidAndValue(String guid, String wordValue) throws Exception {

		if (isBlank(guid) || !isEnabled) {
			return null;
		}
		Map<String, Object> params = new HashMap<>();
		String mappedGuid = guidMappings.get(guid);
		params.put("guid", mappedGuid);
		params.put("wordValue", wordValue);
		try {
			Map<String, Object> wordObject = basicDbService.queryForMap(sqlWordByGuidAndValue, params);
			return wordObject == null ? null : (Long) wordObject.get("id");
		} catch (Exception e) {
			logger.debug("{}, {}", guid, wordValue);
//			throw e;
		}
		return null;
	}

	public Long getWordIdByForms(String wordValue, List<String> forms) throws Exception {

		if (!isEnabled) {
			return null;
		}
		Map<String, Object> params = new HashMap<>();
		params.put("wordValue", wordValue);
		List<Map<String, Object>> wordObjects = basicDbService.queryList(sqlSelectWordsByValue, params);
		if (wordObjects.isEmpty()) {
			return null;
		}
		if (wordObjects.size() == 1) {
			Long wordId = (Long) wordObjects.get(0).get("id");
			return wordId;
		}
		if (CollectionUtils.isEmpty(forms)) {
			return null;
		}
		int bestNrOfMaches = 0;
		Long wordId = null;
		for (Map<String, Object> wordObject : wordObjects) {
			params.clear();
			Long paradigmId = (Long) wordObject.get("paradigm_id");
			params.put("paradigm_id", paradigmId);
			List<Map<String, Object>> formObjects = basicDbService.selectAll(FORM, params);
			List<String> formValues = formObjects.stream().map(f -> (String)f.get("value")).collect(toList());
			int nrOfMatches = CollectionUtils.intersection(formValues, forms).size();
			if (nrOfMatches > bestNrOfMaches) {
				wordId = (Long) wordObject.get("id");
				bestNrOfMaches = nrOfMatches;
			}
		}
		return wordId;
	}

	public boolean isEnabled() {
		return isEnabled;
	}

	public Map<String, String> getGuidMappings() {
		return guidMappings;
	}

}
