package eki.ekilex.runner;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
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
import eki.ekilex.data.transform.WordId;
import eki.ekilex.data.util.WordIdRowMapper;
import eki.ekilex.service.MergeService;

@Component
public class LangBasedWordMergerRunner extends AbstractMergerRunner {

	private static Logger logger = LoggerFactory.getLogger(LangBasedWordMergerRunner.class);

	private static final String SQL_SELECT_MEANING_HOMONYM_WORD_IDS = "sql/select_meaning_homonym_word_ids.sql";

	private static final String SQL_SELECT_HOMONYM_WORD_IDS = "sql/select_homonym_word_ids.sql";

	private static final String SQL_SELECT_IS_SAME_WORD_STRESS = "sql/select_is_same_word_stress.sql";

	private static final String SQL_EXISTS_WORD_TYPE_CODES = "sql/exists_word_type_codes.sql";

	private static final String SQL_NOT_EXISTS_WORD_TYPE_CODES = "sql/not_exists_word_type_codes.sql";

	private final String sqlSelectWordHomonymNumber = "select homonym_nr from " + WORD + " where id = :wordId";

	private String sqlSelectMeaningHomonymWordIds;

	private String sqlSelectHomonymWordIds;

	private String sqlSelectIsSameWordStress;

	private String sqlExistsWordTypeCodes;

	private String sqlNotExistsWordTypeCodes;

	private List<String> standaloneWordTypeCodes;

	private String wordLangCode;

	@Autowired
	private MergeService mergeService;

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

		resourceFileInputStream = classLoader.getResourceAsStream(SQL_SELECT_IS_SAME_WORD_STRESS);
		sqlSelectIsSameWordStress = getContent(resourceFileInputStream);

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
	public void execute(String wordLangCode, String excludedWordsFilePath) throws Exception {

		this.wordLangCode = wordLangCode;

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
			homonymWordIdMap = getMeaningHomonymWordIdMap(Collections.singletonList(standaloneWordTypeCode), WordTypeCriteria.EXISTS);
			homonymWordIdSets = collectSimilarWordIdSets(homonymWordIdMap);
			mergeWords(homonymWordIdSets, updateCountMap, deleteCountMap, notValidStressCount);

			homonymWordIdMap = getHomonymWordIdMap(excludedWordValues, Collections.singletonList(standaloneWordTypeCode), WordTypeCriteria.EXISTS);
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
		Count joinedWordsCount = new Count();


		for (List<Long> wordIds : wordIdSets) {

			Long targetWordId = Collections.min(wordIds, Comparator.comparing(wordId -> getWordHomonymNumber(wordId)));
			wordIds.remove(targetWordId);

			for (Long sourceWordId : wordIds) {
				if (checkStress) {
					boolean isValidStress = compareStress(targetWordId, sourceWordId);
					if (!isValidStress) {
						notValidStressCount.increment();
						continue;
					}
				}
				joinWordData(targetWordId, sourceWordId, updateCountMap);
				joinLexemeData(targetWordId, sourceWordId, updateCountMap, deleteCountMap);
				basicDbService.delete(WORD, sourceWordId);
				deleteCountMap.get(WORD).increment(1);
				joinedWordsCount.increment();
			}
		}
		logger.debug("{} words merged to another word", joinedWordsCount.getValue());
	}

	private void joinWordData(Long targetWordId, Long sourceWordId, Map<String, Count> updateCountMap) throws Exception {
		mergeService.moveWordsData(targetWordId, Collections.singletonList(sourceWordId), updateCountMap);
		mergeService.moveDisplayForm(targetWordId, sourceWordId);
	}

	private boolean compareStress(Long targetWordId, Long sourceWordId) throws Exception {

		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("wordId1", targetWordId);
		paramMap.put("wordId2", sourceWordId);
		Map<String, Object> stressCompareMap = basicDbService.queryForMap(sqlSelectIsSameWordStress, paramMap);
		return (boolean) stressCompareMap.get("is_same_word_stress");
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
			excludedWordValues.add(""); // maybe there is better solution?
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
