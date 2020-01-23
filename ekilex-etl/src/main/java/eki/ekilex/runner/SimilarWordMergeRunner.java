package eki.ekilex.runner;

import java.io.InputStream;
import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import eki.common.constant.Complexity;
import eki.common.constant.DbConstant;
import eki.common.data.AbstractDataObject;
import eki.common.data.Count;
import eki.ekilex.data.transform.WordLexemeMeaning;
import eki.ekilex.service.MergeService;

@Component
public class SimilarWordMergeRunner extends AbstractLoaderRunner implements DbConstant {

	private static Logger logger = LoggerFactory.getLogger(SimilarWordMergeRunner.class);

	private static final String SQL_SELECT_SIMLAR_WORD_IDS = "sql/select_similar_word_ids.sql";

	private String sqlSelectSimilarWordIds;

	@Autowired
	private MergeService mergeService;

	@Override
	public String getDataset() {
		return "similarwordmerge";
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
	public void deleteDatasetData() throws Exception {
	}

	@Override
	public void initialise() throws Exception {

		ClassLoader classLoader = this.getClass().getClassLoader();
		InputStream resourceFileInputStream;

		resourceFileInputStream = classLoader.getResourceAsStream(SQL_SELECT_SIMLAR_WORD_IDS);
		sqlSelectSimilarWordIds = getContent(resourceFileInputStream);
	}

	@Transactional
	public void execute() throws Exception {

		start();

		Map<Long, WordId> simWordIdMap = getSimWordIdMap();
		List<List<Long>> simWordIdSets = collectSimilarWordIdSets(simWordIdMap);

		int simWordSetCount = simWordIdSets.size();

		logger.debug("Similar word sets: {}", simWordSetCount);

		Map<String, Count> updateCountMap = mergeService.getUpdateCountMap();
		Map<String, Count> deleteCountMap = mergeService.getDeleteCountMap();

		List<Long> deleteWordIds = new ArrayList<>();

		Count similarWordSetMergeCount = new Count();
		Count similarWordMergeCount = new Count();

		long simWordSetCounter = 0;
		long progressIndicator = simWordSetCount / Math.min(simWordSetCount, 100);

		for (List<Long> simWordIdSet : simWordIdSets) {

			List<WordLexemeMeaning> allLexemes = mergeService.getLexemesByWords(simWordIdSet);
			WordLexemeMeaning firstLexeme = allLexemes.get(0);
			if (StringUtils.equals(LANGUAGE_CODE_EST, firstLexeme.getLang())) {
				//handled elsewhere
				continue;
			}

			allLexemes = allLexemes.stream().sorted(Comparator.comparing(WordLexemeMeaning::getOrderBy)).collect(Collectors.toList());
			WordLexemeMeaning targetLexeme = allLexemes.stream().filter(lexeme -> lexeme.getComplexity().equals(Complexity.DETAIL)).findFirst().orElse(null);
			if (targetLexeme == null) {
				targetLexeme = allLexemes.get(0);
			}

			// merge lexemes
			Long targetLexemeId = targetLexeme.getLexemeId();
			List<WordLexemeMeaning> sourceLexemes = allLexemes.stream().filter(lexeme -> !lexeme.getLexemeId().equals(targetLexemeId)).collect(Collectors.toList());
			if (CollectionUtils.isNotEmpty(sourceLexemes)) {
				resolveLexemes(targetLexeme, sourceLexemes, updateCountMap, deleteCountMap);
			}

			// merge words
			Long targetWordId = targetLexeme.getWordId();
			List<Long> sourceWordIds = allLexemes.stream().map(WordLexemeMeaning::getWordId).filter(wordId -> !wordId.equals(targetWordId)).collect(Collectors.toList());
			if (CollectionUtils.isNotEmpty(sourceWordIds)) {
				mergeService.moveWordsData(targetWordId, sourceWordIds, updateCountMap);
				deleteWordIds.addAll(sourceWordIds);
			}

			similarWordSetMergeCount.increment();
			similarWordMergeCount.increment(simWordIdSet.size());

			// progress
			simWordSetCounter++;
			if (simWordSetCounter % progressIndicator == 0) {
				long progressPercent = simWordSetCounter / progressIndicator;
				logger.debug("{}% - {} meanings iterated", progressPercent, simWordSetCounter);
			}
		}

		deleteWordIds = deleteWordIds.stream().distinct().collect(Collectors.toList());
		List<Long> failedDeleteWordIds = new ArrayList<>();
		logger.debug("About to delete {} words", deleteWordIds.size());
		deleteWords(deleteWordIds, failedDeleteWordIds, deleteCountMap);
		logger.info(">>>> Failed word delete count: {}", failedDeleteWordIds.size());

		logger.info(">>>> Update counts are as following:");
		for (Entry<String, Count> updateCountEntry : updateCountMap.entrySet()) {
			logger.info("{} : {}", updateCountEntry.getKey(), updateCountEntry.getValue().getValue());
		}

		logger.info(">>>> Delete counts are as following:");
		for (Entry<String, Count> deleteCountEntry : deleteCountMap.entrySet()) {
			logger.info("{} : {}", deleteCountEntry.getKey(), deleteCountEntry.getValue().getValue());
		}

		logger.info(">>>> Similar word set merge count: {}", similarWordSetMergeCount.getValue());
		logger.info(">>>> Total similar word merge count: {}", similarWordMergeCount.getValue());

		end();
	}

	private List<List<Long>> collectSimilarWordIdSets(Map<Long, WordId> simWordIdMap) {

		for (WordId wordIdObj : simWordIdMap.values()) {
			Long wordId = wordIdObj.getWordId();
			List<Long> combSimWordIds = new ArrayList<>();
			combSimWordIds.add(wordId);
			appendSimWordIds(combSimWordIds, wordIdObj, simWordIdMap);
			if (combSimWordIds.size() > 1) {
				combSimWordIds = combSimWordIds.stream().sorted().collect(Collectors.toList());
			}
			wordIdObj.setSimWordIds(combSimWordIds);
		}
		List<List<Long>> simWordIdsSets = simWordIdMap.values().stream().map(WordId::getSimWordIds).distinct().collect(Collectors.toList());
		return simWordIdsSets;
	}

	private void appendSimWordIds(List<Long> srcSimWordIds, WordId currWordIdObj, Map<Long, WordId> simWordIdMap) {

		List<Long> currSimWordIds = currWordIdObj.getSimWordIds();
		for (Long currSimWordId : currSimWordIds) {
			if (srcSimWordIds.contains(currSimWordId)) {
				continue;
			}
			srcSimWordIds.add(currSimWordId);
			WordId currSimWordIdObj = simWordIdMap.get(currSimWordId);
			appendSimWordIds(srcSimWordIds, currSimWordIdObj, simWordIdMap);
		}
	}

	private Map<Long, WordId> getSimWordIdMap() throws Exception {
		Map<String, Object> paramMap = new HashMap<>();
		List<WordId> simWordIds = basicDbService.getResults(sqlSelectSimilarWordIds, paramMap, new WordIdRowMapper());
		Map<Long, WordId> simWordIdMap = simWordIds.stream().collect(Collectors.toMap(WordId::getWordId, wordId -> wordId));
		return simWordIdMap;
	}

	private void resolveLexemes(WordLexemeMeaning targetLexeme, List<WordLexemeMeaning> sourceLexemes, Map<String, Count> updateCountMap, Map<String, Count> deleteCountMap) throws Exception {

		Long targetWordId = targetLexeme.getWordId();
		Long targetWordMeaningId = targetLexeme.getMeaningId();
		Long targetWordLexemeId = targetLexeme.getLexemeId();

		Map<Long, List<WordLexemeMeaning>> sourceLexemesByMeaningsMap = sourceLexemes.stream().collect(Collectors.groupingBy(WordLexemeMeaning::getMeaningId));
		for (Entry<Long, List<WordLexemeMeaning>> sourceLexemesByMeaningsEntry : sourceLexemesByMeaningsMap.entrySet()) {
			Long sourceMeaningId = sourceLexemesByMeaningsEntry.getKey();
			List<WordLexemeMeaning> sourceMeaningLexemes = sourceLexemesByMeaningsEntry.getValue();
			if (sourceMeaningId.equals(targetWordMeaningId)) {
				//move lexemes data to word lexeme target, delete lexemes
				List<Long> sourceLexemeIds = sourceMeaningLexemes.stream()
						.map(WordLexemeMeaning::getLexemeId)
						.collect(Collectors.toList());
				boolean isUpdateTargetLexemeToSimple = sourceMeaningLexemes.stream().anyMatch(lexeme -> Complexity.SIMPLE.equals(lexeme.getComplexity()));
				mergeService.moveLexemesData(targetWordLexemeId, sourceLexemeIds, isUpdateTargetLexemeToSimple, updateCountMap);
				mergeService.deleteLexemes(sourceLexemeIds, deleteCountMap);
			} else if (sourceMeaningLexemes.size() > 1) {
				//pick meaning lexeme target, move lexemes data to target, delete lexemes, reassign meaning lexeme
				WordLexemeMeaning targetMeaningLexeme = sourceMeaningLexemes.get(0);
				Long targetMeaningLexemeId = targetMeaningLexeme.getLexemeId();
				List<Long> sourceMeaningLexemeIds = sourceMeaningLexemes.stream()
						.map(WordLexemeMeaning::getLexemeId)
						.filter(lexemeId -> !lexemeId.equals(targetMeaningLexemeId))
						.collect(Collectors.toList());
				boolean isUpdateTargetLexemeToSimple = sourceMeaningLexemes.stream().anyMatch(lexeme -> Complexity.SIMPLE.equals(lexeme.getComplexity()));
				mergeService.moveLexemesData(targetMeaningLexemeId, sourceMeaningLexemeIds, isUpdateTargetLexemeToSimple, updateCountMap);
				mergeService.deleteLexemes(sourceMeaningLexemeIds, deleteCountMap);
				mergeService.reassignLexemeToWord(targetWordId, targetMeaningLexemeId, updateCountMap);
			} else {
				//reassign this single lexeme
				WordLexemeMeaning sourceMeaningLexeme = sourceMeaningLexemes.get(0);
				Long sourceLexemeId = sourceMeaningLexeme.getLexemeId();
				mergeService.reassignLexemeToWord(targetWordId, sourceLexemeId, updateCountMap);
			}
		}
	}

	private void deleteWords(
			List<Long> sourceWordIds,
			List<Long> allFailedDeleteWordIds,
			Map<String, Count> deleteCountMap) throws Exception {

		for (Long sourceWordId : sourceWordIds) {
			boolean isSuccessfulDelete = mergeService.deleteWord(sourceWordId, deleteCountMap);
			if (!isSuccessfulDelete) {
				allFailedDeleteWordIds.add(sourceWordId);
			}
		}
	}

	class WordId extends AbstractDataObject {

		private static final long serialVersionUID = 1L;

		private Long wordId;

		private List<Long> simWordIds;

		public WordId(Long wordId, List<Long> similarWordIds) {
			this.wordId = wordId;
			this.simWordIds = similarWordIds;
		}

		public Long getWordId() {
			return wordId;
		}

		public void setWordId(Long wordId) {
			this.wordId = wordId;
		}

		public List<Long> getSimWordIds() {
			return simWordIds;
		}

		public void setSimWordIds(List<Long> simWordIds) {
			this.simWordIds = simWordIds;
		}

	}

	class WordIdRowMapper implements RowMapper<WordId> {

		@Override
		public WordId mapRow(ResultSet rs, int rowNum) throws SQLException {

			Long wordId = rs.getObject("word_id", Long.class);
			Array simWordIdsPgArr = rs.getArray("sim_word_ids");
			Long[] simWordIdsArr = (Long[]) simWordIdsPgArr.getArray();
			List<Long> simWordIds = Arrays.asList(simWordIdsArr);
			return new WordId(wordId, simWordIds);
		}
	}
}
