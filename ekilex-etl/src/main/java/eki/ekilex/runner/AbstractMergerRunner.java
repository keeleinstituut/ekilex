package eki.ekilex.runner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import eki.common.constant.Complexity;
import eki.common.constant.DbConstant;
import eki.common.data.Count;
import eki.ekilex.data.transform.WordId;
import eki.ekilex.data.transform.WordLexemeMeaning;
import eki.ekilex.service.MergeService;

public abstract class AbstractMergerRunner extends AbstractLoaderRunner implements DbConstant {

	private static Logger logger = LoggerFactory.getLogger(AbstractMergerRunner.class);

	private static final int DEFAULT_LEXEME_LEVEL = 1;

	@Autowired
	protected MergeService mergeService;

	protected List<List<Long>> collectSimilarWordIdSets(Map<Long, WordId> simWordIdMap) {

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

	protected void appendSimWordIds(List<Long> srcSimWordIds, WordId currWordIdObj, Map<Long, WordId> simWordIdMap) {

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

	protected void joinLexemeData(Long targetWordId, Long sourceWordId, Map<String, Count> updateCountMap, Map<String, Count> deleteCountMap) throws Exception {

		List<WordLexemeMeaning> sourceWordLexemes = mergeService.getLexemesByWord(sourceWordId);
		for (WordLexemeMeaning sourceWordLexeme : sourceWordLexemes) {
			Long sourceWordLexemeId = sourceWordLexeme.getLexemeId();
			Long sourceWordLexemeMeaningId = sourceWordLexeme.getMeaningId();
			String sourceWordLexemeDatasetCode = sourceWordLexeme.getDatasetCode();

			Long targetWordLexemeId = mergeService.getLexemeId(targetWordId, sourceWordLexemeMeaningId, sourceWordLexemeDatasetCode);
			boolean lexemeExists = targetWordLexemeId != null;

			if (lexemeExists) {
				Complexity sourceWordLexemeComplexity = sourceWordLexeme.getComplexity();
				boolean isUpdateTargetLexemeToSimple = Complexity.SIMPLE.equals(sourceWordLexemeComplexity);
				mergeService.moveLexemesData(targetWordLexemeId, Collections.singletonList(sourceWordLexemeId), isUpdateTargetLexemeToSimple, updateCountMap);
				mergeService.deleteLexemes(Collections.singletonList(sourceWordLexemeId), deleteCountMap);
			} else {
				Integer currentMaxLevel = mergeService.getWordLexemesMaxFirstLevel(targetWordId);
				int level1 = currentMaxLevel + 1;
				mergeService.reassignLexemeToWordAndUpdateLevels(targetWordId, sourceWordLexemeId, level1, DEFAULT_LEXEME_LEVEL, updateCountMap);
			}
		}
	}

	protected void logCounts(String description, Map<String, Count> countMap) {
		logger.info(description);
		for (Map.Entry<String, Count> countEntry : countMap.entrySet()) {
			logger.info("{} : {}", countEntry.getKey(), countEntry.getValue().getValue());
		}
	}

}
