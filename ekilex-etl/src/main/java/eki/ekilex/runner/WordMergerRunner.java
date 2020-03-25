package eki.ekilex.runner;

import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.postgresql.jdbc.PgArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import eki.common.constant.Complexity;
import eki.common.data.Count;

@Component
public class WordMergerRunner extends AbstractMergerRunner {

	private static Logger logger = LoggerFactory.getLogger(WordMergerRunner.class);

	private static final int DEFAULT_HOMONYM_NUMBER = 1;

	private static final String SQL_SELECT_WORD_JOIN_CANDIDATES_VALUE_AND_WORD_IDS = "sql/select_word_join_candidates_value_and_word_ids.sql";

	private static final String SQL_SELECT_WORD_HOMONYMS_WORD_IDS = "sql/select_word_homonyms_word_ids.sql";

	private String sqlSelectWordJoinCandidatesValueAndWordIds;

	private String sqlSelectWordHomonymsWordIds;

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

		Map<String, Count> updateCountMap = mergeService.getUpdateCountMap();
		Map<String, Count> deleteCountMap = mergeService.getDeleteCountMap();

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
					Long targetWordId = candidateWordIds.get(0);
					for (Long sourceWordId : candidateWordIds.subList(1, candidateCount)) {
						joinWords(targetWordId, sourceWordId, updateCountMap, deleteCountMap);
						candidateMergedWithAnotherCandidateCount.increment();
					}
					candidateMergedWithAnotherCandidateCount.increment();
				} else {
					candidateNotMergedCount.increment();
				}
			} else if (joinableHomonyms.size() == 1) {
				Long targetWordId = joinableHomonyms.get(0);
				for (Long sourceWordId : candidateWordIds) {
					joinWords(targetWordId, sourceWordId, updateCountMap, deleteCountMap);
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

		logCounts(">>>> Update counts are as following:", updateCountMap);
		logCounts(">>>> Delete counts are as following:", deleteCountMap);

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

	private void joinWords(Long targetWordId, Long sourceWordId, Map<String, Count> updateCountMap, Map<String, Count> deleteCountMap) throws Exception {

		mergeService.moveWordsData(targetWordId, Collections.singletonList(sourceWordId), updateCountMap);

		Map<String, Object> criteriaParamMap = new HashMap<>();
		criteriaParamMap.put("id", targetWordId);
		Map<String, Object> valueParamMap = new HashMap<>();
		valueParamMap.put("homonym_nr", DEFAULT_HOMONYM_NUMBER);
		basicDbService.update(WORD, criteriaParamMap, valueParamMap);

		joinLexemeData(targetWordId, sourceWordId, updateCountMap, deleteCountMap);

		basicDbService.delete(WORD, sourceWordId);
		deleteCountMap.get(WORD).increment(1);
	}

}
