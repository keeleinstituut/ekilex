package eki.ekilex.runner;

import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.postgresql.jdbc.PgArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import eki.common.constant.Complexity;
import eki.common.constant.DbConstant;
import eki.common.data.Count;

@Component
public class WordMergerRunner extends AbstractLoaderRunner implements DbConstant {

	private static Logger logger = LoggerFactory.getLogger(WordMergerRunner.class);

	private static final int DEFAULT_LEXEME_LEVEL = 1;

	private static final int DEFAULT_HOMONYM_NUMBER = 1;

	private static final String SQL_SELECT_WORD_JOIN_CANDIDATES_VALUE_AND_WORD_IDS = "sql/select_word_join_candidates_value_and_word_ids.sql";

	private static final String SQL_SELECT_WORD_HOMONYMS_WORD_IDS = "sql/select_word_homonyms_word_ids.sql";

	private String sqlSelectWordJoinCandidatesValueAndWordIds;

	private String sqlSelectWordHomonymsWordIds;

	private String sqlSelectWordLexemesMaxFirstLevel = "select max(lex.level1) from " + LEXEME + " lex where lex.word_id = :wordId";

	private String sqlSelectLexemeMeaningLexemeCount =
			"select count(l2.id) count from " + LEXEME + " l1, " + LEXEME + " l2 where l1.id = :lexemeId and l1.meaning_id = l2.meaning_id and l1.id != l2.id";

	private String sqlDeleteLexemeFreeforms =
			"delete from " + FREEFORM + " ff where ff.id in(select lff.freeform_id from " + LEXEME_FREEFORM + " lff where lff.lexeme_id = :lexemeId)";

	private String sqlDeleteDefinitionFreeforms =
			"delete from " + FREEFORM + " ff where ff.id in(select dff.freeform_id from " + DEFINITION_FREEFORM + " dff where dff.definition_id = :definitionId)";

	private String sqlDeleteMeaningFreeforms =
			"delete from " + FREEFORM + " ff where ff.id in(select mff.freeform_id from " + MEANING_FREEFORM + " mff where mff.meaning_id = :meaningId)";

	private String sqlSelectMeaningDefinitionIds = "select def.id from " + DEFINITION + " def where def.meaning_id = :meaningId";

	private List<String> excludedWordTypeCodes;

	@Override
	public String getDataset() {
		return "wordmerge";
	}

	@Override
	public Complexity getLexemeComplexity() {
		return null;
	}

	@Override
	public Complexity getDefinitionComplexity() {
		return null;
	}

	@Override
	public Complexity getFreeformComplexity() {
		return null;
	}

	@Override
	public void deleteDatasetData() {
	}

	@Override
	public void initialise() throws Exception {

		ClassLoader classLoader = this.getClass().getClassLoader();
		InputStream resourceFileInputStream;

		resourceFileInputStream = classLoader.getResourceAsStream(SQL_SELECT_WORD_JOIN_CANDIDATES_VALUE_AND_WORD_IDS);
		sqlSelectWordJoinCandidatesValueAndWordIds = getContent(resourceFileInputStream);

		resourceFileInputStream = classLoader.getResourceAsStream(SQL_SELECT_WORD_HOMONYMS_WORD_IDS);
		sqlSelectWordHomonymsWordIds = getContent(resourceFileInputStream);

		excludedWordTypeCodes = new ArrayList<>();
		excludedWordTypeCodes.add(WORD_TYPE_CODE_PREFIXOID);
		excludedWordTypeCodes.add(WORD_TYPE_CODE_SUFFIXOID);
		excludedWordTypeCodes.add(WORD_TYPE_CODE_ABBREVIATION);
	}

	@Transactional
	public void execute(String mergedLexDatasetCode, boolean doReports) throws Exception {

		this.doReports = doReports;
		start();

		Map<String, List<Long>> joinCandidates = getJoinCandidates(mergedLexDatasetCode);

		int wordCount = joinCandidates.values().stream()
				.mapToInt(List::size)
				.sum();
		int valueCount = joinCandidates.size();
		logger.debug("Merging {} candidate words with {} different word values", wordCount, valueCount);

		Count candidateMergedWithSuperHomonymCount = new Count();
		Count candidateMergedWithAnotherCandidateCount = new Count();
		Count candidateNotMergedCount = new Count();

		long wordValueCounter = 0;
		long progressIndicator = valueCount / Math.min(valueCount, 100);

		for (Map.Entry<String, List<Long>> joinCandidate : joinCandidates.entrySet()) {
			String wordValue = joinCandidate.getKey();
			List<Long> candidateWordIds = joinCandidate.getValue();

			List<Long> joinableHomonyms = getJoinableHomonyms(mergedLexDatasetCode, wordValue);
			if (joinableHomonyms.isEmpty()) {
				int candidateCount = candidateWordIds.size();
				boolean multipleCandidates = candidateCount > 1;
				if (multipleCandidates) {
					Long firstWordId = candidateWordIds.get(0);
					for (Long secondWordId : candidateWordIds.subList(1, candidateCount)) {
						joinWords(firstWordId, secondWordId);
						candidateMergedWithAnotherCandidateCount.increment();
					}
					candidateMergedWithAnotherCandidateCount.increment();
				} else {
					candidateNotMergedCount.increment();
				}
			} else if (joinableHomonyms.size() == 1) {
				Long firstWordId = joinableHomonyms.get(0);
				for (Long secondWordId : candidateWordIds) {
					joinWords(firstWordId, secondWordId);
					candidateMergedWithSuperHomonymCount.increment();
				}
			} else {
				candidateNotMergedCount.increment();
			}

			wordValueCounter++;
			if (wordValueCounter % progressIndicator == 0) {
				long progressPercent = wordValueCounter / progressIndicator;
				logger.debug("{}% - {} word values iterated", progressPercent, wordValueCounter);
			}
		}

		logger.debug("{} candidate words merged with {} dataset", candidateMergedWithSuperHomonymCount.getValue(), mergedLexDatasetCode);
		logger.debug("{} candidate words merged with another candidate", candidateMergedWithAnotherCandidateCount.getValue());
		logger.debug("{} candidate words not merged", candidateNotMergedCount.getValue());
		end();
	}

	private Map<String, List<Long>> getJoinCandidates(String mergedLexDatasetCode) throws SQLException {

		Map<String, List<Long>> joinCandidates = new HashMap<>();
		Map<String, Object> tableRowParamMap = new HashMap<>();
		tableRowParamMap.put("lang", LANGUAGE_CODE_EST);
		tableRowParamMap.put("datasetType", DATASET_TYPE_TERM);
		tableRowParamMap.put("includedDatasetCode", ETYMOLOGY_OWNER_DATASET_CODE);
		tableRowParamMap.put("excludedDatasetCode", mergedLexDatasetCode);
		tableRowParamMap.put("excludedWordTypeCodes", excludedWordTypeCodes);
		List<Map<String, Object>> valueAndWordIds = basicDbService.queryList(sqlSelectWordJoinCandidatesValueAndWordIds, tableRowParamMap);

		for (Map<String, Object> row : valueAndWordIds) {
			String value = (String) row.get("value");
			PgArray wordIdsArr = (PgArray) row.get("word_ids");
			Long[] wordIdsArray = (Long[]) wordIdsArr.getArray();
			List<Long> wordIdsList = Arrays.asList(wordIdsArray);
			joinCandidates.put(value, wordIdsList);
		}
		return joinCandidates;
	}

	private List<Long> getJoinableHomonyms(String mergedLexDatasetCode, String wordValue) {

		Map<String, Object> tableRowParamMap = new HashMap<>();
		tableRowParamMap.put("wordValue", wordValue);
		tableRowParamMap.put("lang", LANGUAGE_CODE_EST);
		tableRowParamMap.put("datasetCode", mergedLexDatasetCode);
		tableRowParamMap.put("excludedWordTypeCodes", excludedWordTypeCodes);
		List<Long> wordIds = basicDbService.queryList(sqlSelectWordHomonymsWordIds, tableRowParamMap, Long.class);
		return wordIds;
	}

	private void joinWords(Long firstWordId, Long secondWordId) throws Exception {

		joinWordData(firstWordId, secondWordId);
		joinLexemeData(firstWordId, secondWordId);
		basicDbService.delete(WORD, secondWordId);
	}

	private void joinWordData(Long firstWordId, Long secondWordId) throws Exception {

		Map<String, Object> criteriaParamMap;
		Map<String, Object> valueParamMap;

		criteriaParamMap = new HashMap<>();
		criteriaParamMap.put("word1_id", secondWordId);
		valueParamMap = new HashMap<>();
		valueParamMap.put("word1_id", firstWordId);
		basicDbService.update(WORD_RELATION, criteriaParamMap, valueParamMap);

		criteriaParamMap = new HashMap<>();
		criteriaParamMap.put("word2_id", secondWordId);
		valueParamMap = new HashMap<>();
		valueParamMap.put("word2_id", firstWordId);
		basicDbService.update(WORD_RELATION, criteriaParamMap, valueParamMap);

		criteriaParamMap = new HashMap<>();
		criteriaParamMap.put("word_id", secondWordId);
		valueParamMap = new HashMap<>();
		valueParamMap.put("word_id", firstWordId);
		basicDbService.update(WORD_GROUP_MEMBER, criteriaParamMap, valueParamMap);

		criteriaParamMap = new HashMap<>();
		criteriaParamMap.put("word_id", secondWordId);
		valueParamMap = new HashMap<>();
		valueParamMap.put("word_id", firstWordId);
		basicDbService.update(WORD_ETYMOLOGY, criteriaParamMap, valueParamMap);

		criteriaParamMap = new HashMap<>();
		criteriaParamMap.put("related_word_id", secondWordId);
		valueParamMap = new HashMap<>();
		valueParamMap.put("related_word_id", firstWordId);
		basicDbService.update(WORD_ETYMOLOGY_RELATION, criteriaParamMap, valueParamMap);

		criteriaParamMap = new HashMap<>();
		criteriaParamMap.put("word_id", secondWordId);
		valueParamMap = new HashMap<>();
		valueParamMap.put("word_id", firstWordId);
		List<String> notExistsFields = new ArrayList<>();
		notExistsFields.add("word_type_code");
		basicDbService.updateIfNotExists(WORD_WORD_TYPE, criteriaParamMap, valueParamMap, notExistsFields);

		criteriaParamMap = new HashMap<>();
		criteriaParamMap.put("word_id", secondWordId);
		valueParamMap = new HashMap<>();
		valueParamMap.put("word_id", firstWordId);
		basicDbService.update(WORD_PROCESS_LOG, criteriaParamMap, valueParamMap);

		criteriaParamMap = new HashMap<>();
		criteriaParamMap.put("word_id", secondWordId);
		valueParamMap = new HashMap<>();
		valueParamMap.put("word_id", firstWordId);
		basicDbService.update(WORD_LIFECYCLE_LOG, criteriaParamMap, valueParamMap);

		criteriaParamMap = new HashMap<>();
		criteriaParamMap.put("id", firstWordId);
		valueParamMap = new HashMap<>();
		valueParamMap.put("homonym_nr", DEFAULT_HOMONYM_NUMBER);
		basicDbService.update(WORD, criteriaParamMap, valueParamMap);
	}

	private void joinLexemeData(Long firstWordId, Long secondWordId) throws Exception {

		Map<String, Object> paramMap;

		paramMap = new HashMap<>();
		paramMap.put("word_id", secondWordId);
		List<Map<String, Object>> secondWordLexemes = basicDbService.selectAll(LEXEME, paramMap);
		for (Map<String, Object> secondWordLexeme : secondWordLexemes) {
			Long secondWordLexemeId = (Long) secondWordLexeme.get("id");
			Long secondWordLexemeMeaningId = (Long) secondWordLexeme.get("meaning_id");
			String secondWordLexemeDatasetCode = (String) secondWordLexeme.get("dataset_code");

			paramMap = new HashMap<>();
			paramMap.put("word_id", firstWordId);
			paramMap.put("meaning_id", secondWordLexemeMeaningId);
			paramMap.put("dataset_code", secondWordLexemeDatasetCode);
			Map<String, Object> firstWordLexeme = basicDbService.select(LEXEME, paramMap);
			boolean lexemeExists = firstWordLexeme != null;

			if (lexemeExists) {
				// TODO kas selline olukord tekib siin? või ainult liideses
				boolean isOnlyLexemeForMeaning = isOnlyLexemeForMeaning(secondWordLexemeId);
				deleteLexeme(secondWordLexemeId);
				if (isOnlyLexemeForMeaning) {
					deleteMeaning(secondWordLexemeMeaningId);
				}
			} else {
				paramMap = new HashMap<>();
				paramMap.put("wordId", firstWordId);
				Map<String, Object> maxLevelMap = basicDbService.queryForMap(sqlSelectWordLexemesMaxFirstLevel, paramMap);
				Integer currentMaxLevel = (Integer) maxLevelMap.get("max");
				int level1 = currentMaxLevel + 1;
				updateLexemeWordIdAndLevels(secondWordLexemeId, firstWordId, level1, DEFAULT_LEXEME_LEVEL, DEFAULT_LEXEME_LEVEL);
			}
		}
	}

	public boolean isOnlyLexemeForMeaning(Long lexemeId) throws Exception {

		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("lexemeId", lexemeId);
		Map<String, Object> countMap = basicDbService.queryForMap(sqlSelectLexemeMeaningLexemeCount, paramMap);
		Long count = (Long) countMap.get("count");
		return count == 0;
	}

	private void updateLexemeWordIdAndLevels(Long lexemeId, Long wordId, int level1, int level2, int level3) throws Exception {

		Map<String, Object> criteriaParamMap = new HashMap<>();
		criteriaParamMap.put("id", lexemeId);

		Map<String, Object> valueParamMap = new HashMap<>();
		valueParamMap.put("word_id", wordId);
		valueParamMap.put("level1", level1);
		valueParamMap.put("level2", level2);
		valueParamMap.put("level3", level3);

		basicDbService.update(LEXEME, criteriaParamMap, valueParamMap);
	}

	private void deleteLexeme(Long lexemeId) {

		deleteLexemeFreeforms(lexemeId);
		basicDbService.delete(LEXEME, lexemeId);
	}

	private void deleteLexemeFreeforms(Long lexemeId) {

		Map<String, Object> params = new HashMap<>();
		params.put("lexemeId", lexemeId);
		basicDbService.executeScript(sqlDeleteLexemeFreeforms, params);
	}

	private void deleteMeaningFreeforms(Long meaningId) {

		Map<String, Object> params = new HashMap<>();
		params.put("meaningId", meaningId);
		basicDbService.executeScript(sqlDeleteMeaningFreeforms, params);
	}

	private void deleteDefinitionFreeforms(Long definitionId) {

		Map<String, Object> params = new HashMap<>();
		params.put("definitionId", definitionId);
		basicDbService.executeScript(sqlDeleteDefinitionFreeforms, params);
	}

	private void deleteMeaning(Long meaningId) {

		Map<String, Object> params = new HashMap<>();
		params.put("meaningId", meaningId);
		List<Long> definitionIds = basicDbService.queryList(sqlSelectMeaningDefinitionIds, params, Long.class);
		for (Long definitionId : definitionIds) {
			deleteDefinitionFreeforms(definitionId);
			basicDbService.delete(DEFINITION, definitionId);
		}

		deleteMeaningFreeforms(meaningId);
		basicDbService.delete(MEANING, meaningId);
	}

}
