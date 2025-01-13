package eki.ekilex.service;

import static java.util.stream.Collectors.groupingBy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.exception.OperationDeniedException;
import eki.common.service.util.LexemeLevelPreseUtil;
import eki.ekilex.data.DatasetPermission;
import eki.ekilex.data.Definition;
import eki.ekilex.data.EkiUser;
import eki.ekilex.data.Meaning;
import eki.ekilex.data.MeaningRelation;
import eki.ekilex.data.SearchDatasetsRestriction;
import eki.ekilex.data.SearchFilter;
import eki.ekilex.data.Word;
import eki.ekilex.data.WordDescript;
import eki.ekilex.data.Lexeme;
import eki.ekilex.data.WordsResult;
import eki.ekilex.service.db.LexDataDbService;
import eki.ekilex.service.db.LexSearchDbService;
import eki.ekilex.service.db.LookupDbService;
import eki.ekilex.service.util.PermCalculator;

@Component
public abstract class AbstractWordSearchService extends AbstractSearchService {

	@Autowired
	protected LexSearchDbService lexSearchDbService;

	@Autowired
	protected LexDataDbService lexDataDbService;

	@Autowired
	protected LookupDbService lookupDbService;

	@Autowired
	protected LexemeLevelPreseUtil lexemeLevelPreseUtil;

	@Autowired
	protected PermCalculator permCalculator;

	@Transactional
	public WordsResult getWords(
			SearchFilter searchFilter, List<String> datasetCodes, List<String> tagNames, EkiUser user,
			int offset, int maxResultsLimit, boolean noLimit) throws Exception {

		String userRoleDatasetCode = null;
		if (user != null) {
			DatasetPermission userRole = user.getRecentRole();
			userRoleDatasetCode = userRole.getDatasetCode();
		}
		List<Word> words;
		int wordCount;
		if (!isValidSearchFilter(searchFilter)) {
			words = Collections.emptyList();
			wordCount = 0;
		} else {
			SearchDatasetsRestriction searchDatasetsRestriction = composeDatasetsRestriction(datasetCodes);
			words = lexSearchDbService.getWords(searchFilter, searchDatasetsRestriction, userRoleDatasetCode, tagNames, offset, maxResultsLimit, noLimit);
			wordCount = words.size();
			if ((!noLimit && wordCount == maxResultsLimit) || (offset > DEFAULT_OFFSET)) {
				wordCount = lexSearchDbService.countWords(searchFilter, searchDatasetsRestriction);
				if (CollectionUtils.isEmpty(words) && wordCount > 0) {
					int lastPageOffset = getLastPageOffset(wordCount);
					words = lexSearchDbService.getWords(searchFilter, searchDatasetsRestriction, userRoleDatasetCode, tagNames, lastPageOffset, maxResultsLimit, noLimit);
				}
			}
		}
		WordsResult result = new WordsResult();
		result.setWords(words);
		result.setTotalCount(wordCount);
		setPagingData(offset, maxResultsLimit, wordCount, result);

		return result;
	}

	@Transactional
	public WordsResult getWords(
			String searchFilter, List<String> datasetCodes, List<String> tagNames, EkiUser user,
			int offset, int maxResultsLimit, boolean noLimit) throws Exception {

		String userRoleDatasetCode = null;
		if (user != null) {
			DatasetPermission userRole = user.getRecentRole();
			userRoleDatasetCode = userRole.getDatasetCode();
		}
		return getWords(searchFilter, datasetCodes, tagNames, userRoleDatasetCode, offset, maxResultsLimit, noLimit);
	}

	@Transactional
	public WordsResult getWords(
			String searchFilter, List<String> datasetCodes, List<String> tagNames, String userRoleDatasetCode,
			int offset, int maxResultsLimit, boolean noLimit) throws Exception {

		List<Word> words;
		int wordCount;
		if (StringUtils.isBlank(searchFilter)) {
			words = Collections.emptyList();
			wordCount = 0;
		} else if (StringUtils.containsOnly(searchFilter, SEARCH_MASK_CHARS)) {
			throw new OperationDeniedException("Please be more specific. Use other means to dump data");
		} else {
			SearchDatasetsRestriction searchDatasetsRestriction = composeDatasetsRestriction(datasetCodes);
			words = lexSearchDbService.getWords(searchFilter, searchDatasetsRestriction, userRoleDatasetCode, tagNames, offset, maxResultsLimit, noLimit);
			wordCount = words.size();
			if ((!noLimit && wordCount == maxResultsLimit) || (offset > DEFAULT_OFFSET)) {
				wordCount = lexSearchDbService.countWords(searchFilter, searchDatasetsRestriction);
				if (CollectionUtils.isEmpty(words) && wordCount > 0) {
					int lastPageOffset = getLastPageOffset(wordCount);
					words = lexSearchDbService.getWords(searchFilter, searchDatasetsRestriction, userRoleDatasetCode, tagNames, lastPageOffset, maxResultsLimit, noLimit);
				}
			}
		}
		WordsResult result = new WordsResult();
		result.setWords(words);
		result.setTotalCount(wordCount);
		setPagingData(offset, maxResultsLimit, wordCount, result);

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

		Map<Long, List<Lexeme>> wordLexemesMap = new HashMap<>();

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
				List<Lexeme> wordLexemes = wordLexemesMap.get(relWordId);
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

	public List<WordDescript> getWordCandidates(String wordValue, String language, String datasetCode, EkiUser user) {

		List<WordDescript> wordCandidates = new ArrayList<>();
		SearchDatasetsRestriction searchDatasetsRestriction = composeDatasetsRestriction(Collections.emptyList());
		List<Word> words = lookupDbService.getWords(wordValue, language);

		for (Word word : words) {

			Long wordId = word.getWordId();
			List<Lexeme> wordLexemes = lexSearchDbService.getWordLexemes(wordId, searchDatasetsRestriction, CLASSIF_LABEL_LANG_EST, CLASSIF_LABEL_TYPE_DESCRIP);

			wordLexemes.forEach(lexeme -> {
				Long meaningId = lexeme.getMeaningId();
				String lexemeDatasetCode = lexeme.getDatasetCode();
				List<Definition> definitions = commonDataDbService.getMeaningDefinitions(meaningId, lexemeDatasetCode, CLASSIF_LABEL_LANG_EST, CLASSIF_LABEL_TYPE_DESCRIP);
				permCalculator.filterVisibility(user, definitions);
				Meaning meaning = new Meaning();
				meaning.setMeaningId(meaningId);
				meaning.setDefinitions(definitions);
				lexeme.setMeaning(meaning);
				lexeme.setLexemeWord(word);
			});

			List<Lexeme> mainDatasetLexemes = wordLexemes.stream()
					.filter(lexeme -> StringUtils.equalsAny(lexeme.getDatasetCode(), datasetCode, DATASET_EKI))
					.sorted(Comparator.comparing(lexeme -> !StringUtils.equals(lexeme.getDatasetCode(), datasetCode)))
					.collect(Collectors.toList());

			List<Lexeme> secondaryDatasetLexemes = wordLexemes.stream()
					.filter(lexeme -> !StringUtils.equalsAny(lexeme.getDatasetCode(), datasetCode, DATASET_EKI))
					.collect(Collectors.toList());

			WordDescript wordCandidate = new WordDescript();
			wordCandidate.setWord(word);
			if (mainDatasetLexemes.isEmpty()) {
				wordCandidate.setMainDatasetLexemes(secondaryDatasetLexemes);
				wordCandidate.setSecondaryDatasetLexemes(new ArrayList<>());
			} else {
				boolean primaryDatasetLexemeExists = mainDatasetLexemes.get(0).getDatasetCode().equals(datasetCode);
				wordCandidate.setMainDatasetLexemes(mainDatasetLexemes);
				wordCandidate.setSecondaryDatasetLexemes(secondaryDatasetLexemes);
				wordCandidate.setPrimaryDatasetLexemeExists(primaryDatasetLexemeExists);
			}
			wordCandidates.add(wordCandidate);
		}

		return wordCandidates;
	}
}
