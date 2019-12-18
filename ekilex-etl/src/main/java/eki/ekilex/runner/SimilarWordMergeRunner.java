package eki.ekilex.runner;

import java.io.InputStream;
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
import org.springframework.stereotype.Component;

import eki.common.constant.Complexity;
import eki.common.constant.DbConstant;
import eki.common.data.Count;
import eki.ekilex.data.transform.WordLexemeMeaning;
import eki.ekilex.service.MergeService;

@Component
public class SimilarWordMergeRunner extends AbstractLoaderRunner implements DbConstant {

	private static Logger logger = LoggerFactory.getLogger(SimilarWordMergeRunner.class);

	private static final String SQL_SELECT_SIMLAR_WORD_MEANING_IDS = "sql/select_similar_word_meaning_ids.sql";

	private String sqlSelectSimilarWordMeaningIds;

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

		resourceFileInputStream = classLoader.getResourceAsStream(SQL_SELECT_SIMLAR_WORD_MEANING_IDS);
		sqlSelectSimilarWordMeaningIds = getContent(resourceFileInputStream);
	}

	@Transactional
	public void execute() throws Exception {

		start();

		List<Long> similarWordMeaningIds = getSimilarWordMeaningIds();
		int meaningCount = similarWordMeaningIds.size();

		logger.debug("Meanings with similar words: {}", meaningCount);

		Map<String, Count> updateCountMap = mergeService.getUpdateCountMap();
		Map<String, Count> deleteCountMap = mergeService.getDeleteCountMap();

		Count similarWordMergeCount = new Count();

		long meaningCounter = 0;
		long progressIndicator = meaningCount / Math.min(meaningCount, 100);

		for (Long meaningId : similarWordMeaningIds) {

			List<WordLexemeMeaning> combSsLexemes = mergeService.getLexemes(meaningId);
			Map<String, List<WordLexemeMeaning>> mergingLexemesByWordMap = combSsLexemes.stream().collect(Collectors.groupingBy(lexeme -> lexeme.getWord() + "-" + lexeme.getLang()));

			for (List<WordLexemeMeaning> mergingLexemes : mergingLexemesByWordMap.values()) {
				List<WordLexemeMeaning> sortedMergingLexemes = mergingLexemes.stream().sorted(Comparator.comparing(WordLexemeMeaning::getHomonymNr)).collect(Collectors.toList());
				WordLexemeMeaning firstMergingLexeme = sortedMergingLexemes.get(0);
				if (StringUtils.equals(LANGUAGE_CODE_EST, firstMergingLexeme.getLang())) {
					//handled elsewhere
					continue;
				} else {
					Long targetLexemeId = firstMergingLexeme.getLexemeId();
					List<Long> sourceMergingLexemeIds = mergingLexemes.stream()
							.map(WordLexemeMeaning::getLexemeId).filter(mergingLexemeId -> !mergingLexemeId.equals(targetLexemeId))
							.collect(Collectors.toList());
					boolean isUpdateTargetLexemeToSimple = mergingLexemes.stream().anyMatch(lexeme -> Complexity.SIMPLE.equals(lexeme.getComplexity()));
					if (CollectionUtils.isNotEmpty(sourceMergingLexemeIds)) {
						mergeService.moveLexemes(targetLexemeId, sourceMergingLexemeIds, isUpdateTargetLexemeToSimple, updateCountMap);
						mergeService.deleteLexemes(sourceMergingLexemeIds, deleteCountMap);
						similarWordMergeCount.increment();
					}
				}
			}

			// progress
			meaningCounter++;
			if (meaningCounter % progressIndicator == 0) {
				long progressPercent = meaningCounter / progressIndicator;
				logger.debug("{}% - {} meanings iterated", progressPercent, meaningCounter);
			}
		}

		logger.info(">>>> Update counts are as following:");
		for (Entry<String, Count> updateCountEntry : updateCountMap.entrySet()) {
			logger.info("{} : {}", updateCountEntry.getKey(), updateCountEntry.getValue().getValue());
		}

		logger.info(">>>> Delete counts are as following:");
		for (Entry<String, Count> deleteCountEntry : deleteCountMap.entrySet()) {
			logger.info("{} : {}", deleteCountEntry.getKey(), deleteCountEntry.getValue().getValue());
		}

		logger.info(">>>> Similar word merge count: {}", similarWordMergeCount.getValue());

		end();
	}

	private List<Long> getSimilarWordMeaningIds() throws Exception {
		Map<String, Object> paramMap = new HashMap<>();
		List<Long> meaningIds = basicDbService.queryList(sqlSelectSimilarWordMeaningIds, paramMap, Long.class);
		return meaningIds;
	}
}
