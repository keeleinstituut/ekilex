package eki.wordweb.service;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import eki.common.constant.Complexity;
import eki.common.constant.ContentKey;
import eki.common.constant.DatasetType;
import eki.wordweb.data.Form;
import eki.wordweb.data.LanguagesDatasets;
import eki.wordweb.data.LexemeWord;
import eki.wordweb.data.LinkedWordSearchElement;
import eki.wordweb.data.Meaning;
import eki.wordweb.data.Paradigm;
import eki.wordweb.data.SearchContext;
import eki.wordweb.data.SearchFilter;
import eki.wordweb.data.Word;
import eki.wordweb.data.WordCollocPosGroups;
import eki.wordweb.data.WordData;
import eki.wordweb.data.WordEtymTuple;
import eki.wordweb.data.WordRelationsTuple;

@Component
public class UnifSearchService extends AbstractSearchService {

	@Override
	public void composeFilteringSuggestions(SearchFilter searchFilter, LanguagesDatasets availableLanguagesDatasets) {

		List<String> filteringLanguageCodes = searchFilter.getDestinLangs();
		List<String> filteringDatasetCodes = searchFilter.getDatasetCodes();
		List<String> availableLanguageCodes = availableLanguagesDatasets.getLanguageCodes();
		List<String> availableDatasetCodes = availableLanguagesDatasets.getDatasetCodes();

		boolean isFilterAllLangs = filteringLanguageCodes.stream().anyMatch(code -> StringUtils.equals(code, DESTIN_LANG_ALL));
		boolean isFilterAllDatasets = filteringDatasetCodes.stream().anyMatch(code -> StringUtils.equals(code, DATASET_ALL));

		if (isFilterAllLangs) {
			availableLanguageCodes = Collections.emptyList();
		} else if (CollectionUtils.isNotEmpty(availableLanguageCodes)) {
			availableLanguageCodes = availableLanguageCodes.stream().filter(code -> !filteringLanguageCodes.contains(code)).collect(Collectors.toList());
		}
		if (isFilterAllDatasets) {
			availableDatasetCodes = Collections.emptyList();
		} else if (CollectionUtils.isNotEmpty(availableDatasetCodes)) {
			availableDatasetCodes = availableDatasetCodes.stream().filter(code -> !filteringDatasetCodes.contains(code)).collect(Collectors.toList());
		}
		boolean suggestionsExist = CollectionUtils.isNotEmpty(availableLanguageCodes) || CollectionUtils.isNotEmpty(availableDatasetCodes);

		availableLanguagesDatasets.setLanguageCodes(availableLanguageCodes);
		availableLanguagesDatasets.setDatasetCodes(availableDatasetCodes);
		availableLanguagesDatasets.setAvailableFiltersExist(suggestionsExist);
	}

	@Transactional
	@Override
	public WordData getWordData(Long wordId, SearchFilter searchFilter) {

		// query params + common data
		SearchContext searchContext = getSearchContext(searchFilter);
		Map<String, Long> langOrderByMap = commonDataDbService.getLangOrderByMap();
		Locale displayLocale = languageContext.getDisplayLocale();
		String displayLang = languageContext.getDisplayLang();

		// word data
		Word word = searchDbService.getWord(wordId);
		String wordLang = word.getLang();
		classifierUtil.applyClassifiers(word, displayLang);
		wordConversionUtil.setWordTypeFlags(word);
		WordRelationsTuple wordRelationsTuple = searchDbService.getWordRelationsTuple(wordId);
		wordConversionUtil.composeWordRelations(word, wordRelationsTuple, langOrderByMap, searchContext, displayLocale, displayLang);
		List<WordEtymTuple> wordEtymTuples = searchDbService.getWordEtymologyTuples(wordId);
		etymConversionUtil.composeWordEtymology(word, wordEtymTuples, displayLang);
		List<Form> forms = searchDbService.getWordForms(wordId, searchContext);
		List<Paradigm> paradigms = paradigmConversionUtil.composeParadigms(forms, displayLang);
		List<String> allRelatedWords = wordConversionUtil.collectAllRelatedWords(word);

		// lexeme data
		List<LexemeWord> flatDepthLexemes = searchDbService.getMeaningsLexemes(wordId, searchContext);
		lexemeConversionUtil.composeLexemes(wordLang, flatDepthLexemes, langOrderByMap, searchContext, displayLang);
		List<LexemeWord> lexemeWords = lexemeConversionUtil.arrangeHierarchy(wordId, flatDepthLexemes);
		List<Meaning> meanings = searchDbService.getMeanings(wordId);
		Map<Long, Meaning> lexemeMeaningMap = meanings.stream().collect(Collectors.toMap(Meaning::getLexemeId, meaning -> meaning));
		lexemeConversionUtil.composeMeanings(wordLang, lexemeWords, lexemeMeaningMap, allRelatedWords, langOrderByMap, searchContext, displayLang);

		List<LexemeWord> lexLexemes = lexemeWords.stream()
				.filter(lexeme -> DatasetType.LEX.equals(lexeme.getDatasetType()))
				.collect(Collectors.toList());
		List<LexemeWord> termLexemes = lexemeWords.stream()
				.filter(lexeme -> DatasetType.TERM.equals(lexeme.getDatasetType()) && !StringUtils.equals(lexeme.getDatasetCode(), DATASET_LIMITED))
				.collect(Collectors.toList());
		List<LexemeWord> limTermLexemes = lexemeWords.stream()
				.filter(lexeme -> DatasetType.TERM.equals(lexeme.getDatasetType()) && StringUtils.equals(lexeme.getDatasetCode(), DATASET_LIMITED))
				.collect(Collectors.toList());

		// lex conv
		if (CollectionUtils.isNotEmpty(lexLexemes)) {
			List<WordCollocPosGroups> wordCollocPosGroups = searchDbService.getWordCollocPosGroups(wordId);
			collocConversionUtil.composeDisplay(wordId, lexLexemes, wordCollocPosGroups, searchContext, displayLang);
			lexemeConversionUtil.flagEmptyLexemes(lexLexemes);
			lexLexemes = lexLexemes.stream().filter(lexeme -> !lexeme.isEmptyLexeme()).collect(Collectors.toList());
			lexemeConversionUtil.sortLexLexemes(lexLexemes);
			lexemeLevelPreseUtil.combineLevels(lexLexemes);
		}

		// term conv
		if (CollectionUtils.isNotEmpty(termLexemes)) {
			lexemeConversionUtil.sortTermLexemes(termLexemes, word);
		}

		// lim term conv
		if (CollectionUtils.isNotEmpty(limTermLexemes)) {
			lexemeConversionUtil.sortTermLexemes(limTermLexemes, word);
		}

		// word common
		wordConversionUtil.composeCommon(word, lexLexemes, termLexemes);

		return composeWordData(word, forms, paradigms, lexLexemes, termLexemes, limTermLexemes, searchContext);
	}

	@Transactional
	public LinkedWordSearchElement getLinkWord(String linkType, Long linkId, List<String> destinLangs, List<String> datasetCodes) {

		SearchContext searchContext = getSearchContext(destinLangs, datasetCodes);

		if (StringUtils.equals(ContentKey.MEANING_LINK, linkType)) {
			Long meaningId = Long.valueOf(linkId);
			LinkedWordSearchElement firstMeaningWord = searchDbService.getFirstMeaningWord(meaningId, searchContext);
			return firstMeaningWord;
		}
		if (StringUtils.equals(ContentKey.WORD_LINK, linkType)) {
			Long wordId = Long.valueOf(linkId);
			LinkedWordSearchElement word = searchDbService.getWordValue(wordId);
			return word;
		}
		return null;
	}

	@Override
	public SearchContext getSearchContext(SearchFilter searchFilter) {
		List<String> destinLangs = searchFilter.getDestinLangs();
		List<String> datasetCodes = searchFilter.getDatasetCodes();
		return getSearchContext(destinLangs, datasetCodes);
	}

	private SearchContext getSearchContext(List<String> destinLangs, List<String> datasetCodes) {
		Complexity lexComplexity = Complexity.DETAIL;
		DatasetType datasetType = null;
		Integer maxDisplayLevel = DEFAULT_MORPHOLOGY_MAX_DISPLAY_LEVEL;
		List<String> destinLangsClean = destinLangs.stream().filter(destinLang -> !StringUtils.equals(destinLang, DESTIN_LANG_ALL)).collect(Collectors.toList());
		List<String> datasetCodesClean = datasetCodes.stream().filter(datasetCode -> !StringUtils.equals(datasetCode, DATASET_ALL)).collect(Collectors.toList());
		boolean excludeQuestionable = false;
		boolean fiCollationExists = commonDataDbService.fiCollationExists();
		SearchContext searchContext = new SearchContext(datasetType, destinLangsClean, datasetCodesClean, lexComplexity, maxDisplayLevel, excludeQuestionable, fiCollationExists);
		return searchContext;
	}
}
