package eki.ekilex.runner;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.Complexity;
import eki.common.data.Count;
import eki.common.service.TextDecorationService;
import eki.ekilex.data.transform.WordId;
import eki.ekilex.data.util.WordIdRowMapper;
import eki.ekilex.service.MergeService;
import eki.ekilex.service.ReportComposer;

@Component
public class LangBasedWordMergerRunner extends AbstractMergerRunner {

	private static Logger logger = LoggerFactory.getLogger(LangBasedWordMergerRunner.class);

	private static final String DISPLAY_FORM_STRESS_MARK = "\"";

	private static final String REPORT_WORD_MERGER_INVALID_STRESS = "word_merger_invalid_stress";

	private static final String REPORT_WORD_MERGER_INVALID_DISPLAY_FORM_STRESS = "word_merger_invalid_display_form_stress";

	private static final String REPORT_WORD_MERGER_INVALID_DISPLAY_FORM_VALUE = "word_merger_invalid_display_form_value";

	private static final String REPORT_WORD_MERGER_INVALID_VALUE_PRESE_STRESS = "word_merger_invalid_value_prese_stress";

	private static final String SQL_SELECT_MEANING_HOMONYM_WORD_IDS = "sql/select_meaning_homonym_word_ids.sql";

	private static final String SQL_SELECT_HOMONYM_WORD_IDS = "sql/select_homonym_word_ids.sql";

	private static final String SQL_EXISTS_WORD_TYPE_CODES = "sql/exists_word_type_codes.sql";

	private static final String SQL_NOT_EXISTS_WORD_TYPE_CODES = "sql/not_exists_word_type_codes.sql";

	private final String sqlSelectWordHomonymNumber = "select homonym_nr from " + WORD + " where id = :wordId";

	private final String sqlSelectWordStressData = "select f.id form_id, f.value, f.value_prese, f.display_form from " + FORM + " f, " + PARADIGM + " p"
			+ " where p.word_id = :wordId and f.paradigm_id = p.id and f.mode = 'WORD'";

	private final String sqlSelectWordHasForms = "select count(f.id) > 0 as word_has_forms from " + PARADIGM + " p, " + FORM + " f"
			+ " where p.word_id = :wordId and f.paradigm_id = p.id and f.mode = 'FORM'";

	private final String sqlDeleteParadigms = "delete from " + PARADIGM + " where paradigm.word_id = :wordId";

	private String sqlSelectMeaningHomonymWordIds;

	private String sqlSelectHomonymWordIds;

	private String sqlExistsWordTypeCodes;

	private String sqlNotExistsWordTypeCodes;

	private List<String> standaloneWordTypeCodes;

	private String wordLangCode;

	@Autowired
	private MergeService mergeService;

	@Autowired
	private TextDecorationService textDecorationService;

	@Override
	String getDataset() {
		return "langbasedwordmerge";
	}

	@Override
	Complexity getLexemeComplexity() {
		return null;
	}

	@Override
	Complexity getDefinitionComplexity() {
		return null;
	}

	@Override
	Complexity getFreeformComplexity() {
		return null;
	}

	@Override
	void deleteDatasetData() {
	}

	@Override
	void initialise() throws Exception {

		ClassLoader classLoader = this.getClass().getClassLoader();
		InputStream resourceFileInputStream;

		resourceFileInputStream = classLoader.getResourceAsStream(SQL_SELECT_MEANING_HOMONYM_WORD_IDS);
		sqlSelectMeaningHomonymWordIds = getContent(resourceFileInputStream);

		resourceFileInputStream = classLoader.getResourceAsStream(SQL_SELECT_HOMONYM_WORD_IDS);
		sqlSelectHomonymWordIds = getContent(resourceFileInputStream);

		resourceFileInputStream = classLoader.getResourceAsStream(SQL_EXISTS_WORD_TYPE_CODES);
		sqlExistsWordTypeCodes = getContent(resourceFileInputStream);

		resourceFileInputStream = classLoader.getResourceAsStream(SQL_NOT_EXISTS_WORD_TYPE_CODES);
		sqlNotExistsWordTypeCodes = getContent(resourceFileInputStream);

		standaloneWordTypeCodes = new ArrayList<>();
		standaloneWordTypeCodes.add(WORD_TYPE_CODE_PREFIXOID);
		standaloneWordTypeCodes.add(WORD_TYPE_CODE_SUFFIXOID);
		standaloneWordTypeCodes.add(WORD_TYPE_CODE_ABBREVIATION);
	}

	@Transactional
	public void execute(String wordLangCode, String excludedWordsFilePath, boolean doReports) throws Exception {

		this.wordLangCode = wordLangCode;
		this.doReports = doReports;
		if (doReports) {
			reportComposer = new ReportComposer(getDataset() + "_" + wordLangCode + "_loader", REPORT_WORD_MERGER_INVALID_STRESS, REPORT_WORD_MERGER_INVALID_DISPLAY_FORM_STRESS,
					REPORT_WORD_MERGER_INVALID_DISPLAY_FORM_VALUE, REPORT_WORD_MERGER_INVALID_VALUE_PRESE_STRESS);
		}

		boolean isLangCodeValid = validateLangCode();
		if (!isLangCodeValid) {
			logger.error("Provided lang code \"{}\" is not valid.", wordLangCode);
			return;
		}

		List<String> excludedWordValues = new ArrayList<>();
		boolean isFilePathProvided = excludedWordsFilePath != null;
		if (isFilePathProvided) {
			File excludedWordsTxtFile = new File(excludedWordsFilePath);
			if (!excludedWordsTxtFile.exists()) {
				logger.error("Excluded words file path is incorrect");
				return;
			}
			excludedWordValues = readFileLines(excludedWordsTxtFile);
		}

		start();

		Map<String, Count> updateCountMap = mergeService.getUpdateCountMap();
		Map<String, Count> deleteCountMap = mergeService.getDeleteCountMap();
		Count notValidStressCount = new Count();

		Map<Long, WordId> homonymWordIdMap;
		List<List<Long>> homonymWordIdSets;

		homonymWordIdMap = getMeaningHomonymWordIdMap(standaloneWordTypeCodes, WordTypeCriteria.NOT_EXISTS);
		homonymWordIdSets = collectSimilarWordIdSets(homonymWordIdMap);
		mergeWords(homonymWordIdSets, updateCountMap, deleteCountMap, notValidStressCount);

		homonymWordIdMap = getHomonymWordIdMap(excludedWordValues, standaloneWordTypeCodes, WordTypeCriteria.NOT_EXISTS);
		homonymWordIdSets = collectSimilarWordIdSets(homonymWordIdMap);
		mergeWords(homonymWordIdSets, updateCountMap, deleteCountMap, notValidStressCount);

		for (String standaloneWordTypeCode : standaloneWordTypeCodes) {
			homonymWordIdMap = getMeaningHomonymWordIdMap(Arrays.asList(standaloneWordTypeCode), WordTypeCriteria.EXISTS);
			homonymWordIdSets = collectSimilarWordIdSets(homonymWordIdMap);
			mergeWords(homonymWordIdSets, updateCountMap, deleteCountMap, notValidStressCount);

			homonymWordIdMap = getHomonymWordIdMap(excludedWordValues, Arrays.asList(standaloneWordTypeCode), WordTypeCriteria.EXISTS);
			homonymWordIdSets = collectSimilarWordIdSets(homonymWordIdMap);
			mergeWords(homonymWordIdSets, updateCountMap, deleteCountMap, notValidStressCount);
		}

		logCounts(">>>> Update counts are as following:", updateCountMap);
		logCounts(">>>> Delete counts are as following:", deleteCountMap);
		logger.info("{} words not merged because of different word stress", notValidStressCount.getValue());

		end();
	}

	private void mergeWords(List<List<Long>> wordIdSets, Map<String, Count> updateCountMap, Map<String, Count> deleteCountMap, Count notValidStressCount) throws Exception {

		boolean checkStress = StringUtils.equals(LANGUAGE_CODE_RUS, wordLangCode);
		logger.debug("Starting to process {} homonym word sets", wordIdSets.size());
		Count mergedWordsCount = new Count();

		for (List<Long> wordIds : wordIdSets) {
			Long targetWordId = Collections.min(wordIds, Comparator.comparing(wordId -> getWordHomonymNumber(wordId)));
			wordIds.remove(targetWordId);

			for (Long sourceWordId : wordIds) {
				if (checkStress) {
					WordStress targetWordStress = getWordStressData(targetWordId);
					WordStress sourceWordStress = getWordStressData(sourceWordId);
					boolean isValidStress = isValidWordStressAndMarkup(targetWordStress, sourceWordStress);

					if (!isValidStress) {
						if (doReports) {
							String wordValue = targetWordStress.getValue();
							appendToReport(doReports, REPORT_WORD_MERGER_INVALID_STRESS, wordValue);
						}
						notValidStressCount.increment();
						continue;
					}

					mergeWordStressAndMarkupData(targetWordStress, sourceWordStress);
				} else {
					mergeParadigmData(targetWordId, sourceWordId, updateCountMap);
				}
				mergeService.moveWordsData(targetWordId, Arrays.asList(sourceWordId), updateCountMap);
				mergeLexemeData(targetWordId, sourceWordId, updateCountMap, deleteCountMap);
				basicDbService.delete(WORD, sourceWordId);
				deleteCountMap.get(WORD).increment(1);
				mergedWordsCount.increment();
			}
		}
		logger.debug("{} words merged to another word", mergedWordsCount.getValue());
	}

	private void mergeWordStressAndMarkupData(WordStress targetWordStress, WordStress sourceWordStress) throws Exception {

		String targetDisplayForm = targetWordStress.getDisplayForm();
		String targetValuePrese = targetWordStress.getValuePrese();
		Long targetFormId = targetWordStress.getFormId();

		String sourceDisplayForm = sourceWordStress.getDisplayForm();
		String sourceValuePrese = sourceWordStress.getValuePrese();

		if (sourceDisplayForm != null) {
			if (targetDisplayForm == null) {
				mergeService.updateFormDisplayForm(targetFormId, sourceDisplayForm);
			} else {
				boolean targetContainsStress = targetDisplayForm.contains(DISPLAY_FORM_STRESS_MARK);
				boolean sourceContainsStress = sourceDisplayForm.contains(DISPLAY_FORM_STRESS_MARK);
				if (!targetContainsStress && sourceContainsStress) {
					mergeService.updateFormDisplayForm(targetFormId, sourceDisplayForm);
				}
			}
		}

		if (!StringUtils.equals(targetValuePrese, sourceValuePrese)) {
			boolean isTargetWordDecorated = textDecorationService.isDecorated(targetValuePrese);
			boolean isSourceWordDecorated = textDecorationService.isDecorated(sourceValuePrese);
			if (!isTargetWordDecorated && isSourceWordDecorated) {
				mergeService.updateFormValuePrese(targetFormId, sourceValuePrese);
			}
		}
	}

	private void mergeParadigmData(Long targetWordId, Long sourceWordId, Map<String, Count> updateCountMap) throws Exception {

		boolean targetWordHasForms = wordHasForms(targetWordId);
		if (targetWordHasForms) {
			return;
		}
		boolean sourceWordHasForms = wordHasForms(sourceWordId);
		if (sourceWordHasForms) {
			moveParadigms(targetWordId, sourceWordId, updateCountMap);
		}
	}

	private void moveParadigms(Long targetWordId, Long sourceWordId, Map<String, Count> updateCountMap) throws Exception {

		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("wordId", targetWordId);
		basicDbService.executeScript(sqlDeleteParadigms, paramMap);

		Map<String, Object> criteriaParamMap = new HashMap<>();
		criteriaParamMap.put("word_id", sourceWordId);
		Map<String, Object> valueParamMap = new HashMap<>();
		valueParamMap.put("word_id", targetWordId);
		int updateCount = basicDbService.update(PARADIGM, criteriaParamMap, valueParamMap);

		updateCountMap.get(PARADIGM).increment(updateCount);
	}

	private boolean isValidWordStressAndMarkup(WordStress targetWordStress, WordStress sourceWordStress) throws Exception {

		String wordValue = targetWordStress.getValue();

		String targetDisplayForm = targetWordStress.getDisplayForm();
		String targetValuePrese = targetWordStress.getValuePrese();

		String sourceDisplayForm = sourceWordStress.getDisplayForm();
		String sourceValuePrese = sourceWordStress.getValuePrese();

		if (!StringUtils.equals(targetDisplayForm, sourceDisplayForm)) {
			if (targetDisplayForm != null && sourceDisplayForm != null) {
				boolean targetContainsStress = targetDisplayForm.contains(DISPLAY_FORM_STRESS_MARK);
				boolean sourceContainsStress = sourceDisplayForm.contains(DISPLAY_FORM_STRESS_MARK);
				if (targetContainsStress && sourceContainsStress) {
					appendToReport(doReports, REPORT_WORD_MERGER_INVALID_DISPLAY_FORM_STRESS, wordValue, targetDisplayForm, sourceDisplayForm);
					return false;
				} else if (!targetContainsStress && !sourceContainsStress) {
					appendToReport(doReports, REPORT_WORD_MERGER_INVALID_DISPLAY_FORM_VALUE, wordValue, targetDisplayForm, sourceDisplayForm);
					return false;
				}
			}
		}

		if (StringUtils.equals(targetValuePrese, sourceValuePrese)) {
			return true;
		}

		boolean isTargetWordDecorated = textDecorationService.isDecorated(targetValuePrese);
		boolean isSourceWordDecorated = textDecorationService.isDecorated(sourceValuePrese);
		if (isTargetWordDecorated && isSourceWordDecorated) {
			appendToReport(doReports, REPORT_WORD_MERGER_INVALID_VALUE_PRESE_STRESS, wordValue, targetValuePrese, sourceValuePrese);
			return false;
		}
		return true;
	}

	private boolean wordHasForms(Long wordId) throws Exception {

		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("wordId", wordId);
		Map<String, Object> stressCompareMap = basicDbService.queryForMap(sqlSelectWordHasForms, paramMap);
		return (boolean) stressCompareMap.get("word_has_forms");
	}

	private WordStress getWordStressData(Long wordId) throws Exception {

		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("wordId", wordId);
		WordStress wordStress = basicDbService.getSingleResult(sqlSelectWordStressData, paramMap, new WordStressRowMapper());
		return wordStress;
	}

	private boolean validateLangCode() throws Exception {

		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("code", wordLangCode);
		Map<String, Object> langCodeResult = basicDbService.select(LANGUAGE, paramMap);
		return MapUtils.isNotEmpty(langCodeResult);
	}

	private Map<Long, WordId> getMeaningHomonymWordIdMap(List<String> wordTypeCodes, WordTypeCriteria wordTypeCriteria) throws Exception {

		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("wordLang", wordLangCode);

		String sql = sqlSelectMeaningHomonymWordIds;
		String sqlWordTypeCodes = null;
		if (WordTypeCriteria.EXISTS == wordTypeCriteria) {
			paramMap.put("includedWordTypeCodes", wordTypeCodes);
			sqlWordTypeCodes = sqlExistsWordTypeCodes;
		} else if (WordTypeCriteria.NOT_EXISTS == wordTypeCriteria) {
			paramMap.put("excludedWordTypeCodes", wordTypeCodes);
			sqlWordTypeCodes = sqlNotExistsWordTypeCodes;
		}
		sql = StringUtils.replace(sql, "{wordTypeCodesCondition}", sqlWordTypeCodes);

		List<WordId> meaningHomnymWordIds = basicDbService.getResults(sql, paramMap, new WordIdRowMapper());
		Map<Long, WordId> meaningHomonymWordIdMap = meaningHomnymWordIds.stream().collect(Collectors.toMap(WordId::getWordId, wordId -> wordId));
		return meaningHomonymWordIdMap;
	}

	private Map<Long, WordId> getHomonymWordIdMap(List<String> excludedWordValues, List<String> wordTypeCodes, WordTypeCriteria wordTypeCriteria) throws Exception {

		if (excludedWordValues.isEmpty()) {
			excludedWordValues.add("");
		}
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("wordLang", wordLangCode);
		paramMap.put("excludedWordValues", excludedWordValues);

		String sql = sqlSelectHomonymWordIds;
		String sqlWordTypeCodes = null;
		if (WordTypeCriteria.EXISTS == wordTypeCriteria) {
			paramMap.put("includedWordTypeCodes", wordTypeCodes);
			sqlWordTypeCodes = sqlExistsWordTypeCodes;
		} else if (WordTypeCriteria.NOT_EXISTS == wordTypeCriteria) {
			paramMap.put("excludedWordTypeCodes", wordTypeCodes);
			sqlWordTypeCodes = sqlNotExistsWordTypeCodes;
		}
		sql = StringUtils.replace(sql, "{wordTypeCodesCondition}", sqlWordTypeCodes);

		List<WordId> meaningHomnymWordIds = basicDbService.getResults(sql, paramMap, new WordIdRowMapper());
		Map<Long, WordId> meaningHomonymWordIdMap = meaningHomnymWordIds.stream().collect(Collectors.toMap(WordId::getWordId, wordId -> wordId));
		return meaningHomonymWordIdMap;
	}

	private Integer getWordHomonymNumber(Long wordId) {

		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("wordId", wordId);
		try {
			Map<String, Object> homonymNumberMap = basicDbService.queryForMap(sqlSelectWordHomonymNumber, paramMap);
			return (Integer) homonymNumberMap.get("homonym_nr");
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	private enum WordTypeCriteria {
		EXISTS, NOT_EXISTS
	}

}
