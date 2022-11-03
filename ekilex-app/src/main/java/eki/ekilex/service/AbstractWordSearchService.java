package eki.ekilex.service;

import static java.util.stream.Collectors.groupingBy;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.exception.OperationDeniedException;
import eki.common.service.util.LexemeLevelPreseUtil;
import eki.ekilex.data.DatasetPermission;
import eki.ekilex.data.MeaningRelation;
import eki.ekilex.data.SearchDatasetsRestriction;
import eki.ekilex.data.SearchFilter;
import eki.ekilex.data.Word;
import eki.ekilex.data.WordLexeme;
import eki.ekilex.data.WordsResult;
import eki.ekilex.service.db.LexSearchDbService;
import eki.ekilex.service.db.LookupDbService;

@Component
public abstract class AbstractWordSearchService extends AbstractSearchService {

	@Autowired
	protected LexSearchDbService lexSearchDbService;

	@Autowired
	protected LookupDbService lookupDbService;

	@Autowired
	protected LexemeLevelPreseUtil lexemeLevelPreseUtil;

	@Transactional
	public WordsResult getWords(
			SearchFilter searchFilter, List<String> datasetCodes, DatasetPermission userRole, List<String> tagNames, int offset,
			int maxResultsLimit, boolean noLimit) throws Exception {

		List<Word> words;
		int wordCount;
		if (!isValidSearchFilter(searchFilter)) {
			words = Collections.emptyList();
			wordCount = 0;
		} else {
			SearchDatasetsRestriction searchDatasetsRestriction = composeDatasetsRestriction(datasetCodes);
			words = lexSearchDbService.getWords(searchFilter, searchDatasetsRestriction, userRole, tagNames, offset, maxResultsLimit, noLimit);
			wordCount = words.size();
			if ((!noLimit && wordCount == maxResultsLimit) || offset > DEFAULT_OFFSET) {
				wordCount = lexSearchDbService.countWords(searchFilter, searchDatasetsRestriction);
			}
		}
		WordsResult result = new WordsResult();
		result.setWords(words);
		result.setTotalCount(wordCount);

		boolean showPaging = wordCount > maxResultsLimit;
		result.setShowPaging(showPaging);
		if (showPaging) {
			setPagingData(offset, maxResultsLimit, wordCount, result);
		}
		return result;
	}

	@Transactional
	public WordsResult getWords(
			String searchFilter, List<String> datasetCodes, DatasetPermission userRole, List<String> tagNames, int offset, int maxResultsLimit, boolean noLimit) throws Exception {

		List<Word> words;
		int wordCount;
		if (StringUtils.isBlank(searchFilter)) {
			words = Collections.emptyList();
			wordCount = 0;
		} else if (StringUtils.containsOnly(searchFilter, QUERY_MULTIPLE_CHARACTERS_SYM)) {
			throw new OperationDeniedException("Please be more specific. Use other means to dump data");
		} else {
			SearchDatasetsRestriction searchDatasetsRestriction = composeDatasetsRestriction(datasetCodes);
			words = lexSearchDbService.getWords(searchFilter, searchDatasetsRestriction, userRole, tagNames, offset, maxResultsLimit, noLimit);
			wordCount = words.size();
			if ((!noLimit && wordCount == maxResultsLimit) || offset > DEFAULT_OFFSET) {
				wordCount = lexSearchDbService.countWords(searchFilter, searchDatasetsRestriction);
			}
		}
		WordsResult result = new WordsResult();
		result.setWords(words);
		result.setTotalCount(wordCount);

		boolean showPaging = wordCount > maxResultsLimit;
		result.setShowPaging(showPaging);
		if (showPaging) {
			setPagingData(offset, maxResultsLimit, wordCount, result);
		}
		return result;
	}

	public int countWords(String searchFilter, List<String> selectedDatasetCodes) {
		if (StringUtils.isBlank(searchFilter)) {
			return 0;
		}
		SearchDatasetsRestriction searchDatasetsRestriction = composeDatasetsRestriction(selectedDatasetCodes);
		int count = lexSearchDbService.countWords(searchFilter, searchDatasetsRestriction);
		return count;
	}

	public void appendLexemeLevels(List<MeaningRelation> synMeaningRelations) {

		Map<Long, List<WordLexeme>> wordLexemesMap = new HashMap<>();

		List<Long> repetitiveWordIds = synMeaningRelations.stream()
				.collect(groupingBy(MeaningRelation::getWordId, Collectors.counting()))
				.entrySet().stream()
				.filter(wordIdCountEntry -> wordIdCountEntry.getValue() > 1L)
				.map(Map.Entry::getKey)
				.collect(Collectors.toList());

		synMeaningRelations.forEach(relation -> {
			Long relWordId = relation.getWordId();
			Long relLexemeId = relation.getLexemeId();
			if (repetitiveWordIds.contains(relWordId)) {
				List<WordLexeme> wordLexemes = wordLexemesMap.get(relWordId);
				if (wordLexemes == null) {
					wordLexemes = lookupDbService.getWordLexemesLevels(relWordId);
					lexemeLevelPreseUtil.combineLevels(wordLexemes);
					wordLexemesMap.put(relWordId, wordLexemes);
				}
				String relLexemeLevels = wordLexemes.stream().filter(lexeme -> lexeme.getLexemeId().equals(relLexemeId)).findFirst().get().getLevels();
				relation.setLexemeLevels(relLexemeLevels);
			}
		});
	}
}
