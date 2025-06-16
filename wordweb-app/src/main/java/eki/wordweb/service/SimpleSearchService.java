package eki.wordweb.service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import eki.common.constant.DatasetType;
import eki.wordweb.data.Form;
import eki.wordweb.data.LanguagesDatasets;
import eki.wordweb.data.LexemeWord;
import eki.wordweb.data.Meaning;
import eki.wordweb.data.Paradigm;
import eki.wordweb.data.SearchContext;
import eki.wordweb.data.SearchFilter;
import eki.wordweb.data.Word;
import eki.wordweb.data.WordCollocPosGroups;
import eki.wordweb.data.WordData;
import eki.wordweb.data.WordRelationsTuple;

@Component
public class SimpleSearchService extends AbstractSearchService {

	@Override
	public void composeFilteringSuggestions(SearchFilter searchFilter, LanguagesDatasets availableLanguagesDatasets) {

		List<String> filteringLanguageCodes = searchFilter.getDestinLangs();
		List<String> availableLanguageCodes = availableLanguagesDatasets.getLanguageCodes();

		boolean isFilterAllLangs = filteringLanguageCodes.stream().anyMatch(code -> StringUtils.equals(code, DESTIN_LANG_ALL));

		if (isFilterAllLangs) {
			availableLanguageCodes = Collections.emptyList();
		} else if (CollectionUtils.isNotEmpty(availableLanguageCodes)) {
			availableLanguageCodes = availableLanguageCodes.stream().filter(code -> !filteringLanguageCodes.contains(code)).collect(Collectors.toList());
		}
		boolean suggestionsExist = CollectionUtils.isNotEmpty(availableLanguageCodes);

		availableLanguagesDatasets.setLanguageCodes(availableLanguageCodes);
		availableLanguagesDatasets.setDatasetCodes(Collections.emptyList());
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
		WordRelationsTuple wordRelationsTuple = searchDbService.getWordRelationsTuple(wordId);
		List<Form> forms = searchDbService.getWordForms(wordId, searchContext);
		List<Paradigm> paradigms = paradigmConversionUtil.composeParadigms(forms, displayLang);
		List<String> allRelatedWords = wordConversionUtil.collectAllRelatedWords(word);

		// lexeme data
		List<LexemeWord> lexemeWords = searchDbService.getWordLexemes(wordId, searchContext);
		List<Meaning> meanings = searchDbService.getMeanings(wordId);

		// data transformations
		wordConversionUtil.setWordTypeFlags(word);
		wordConversionUtil.composeWordRelations(word, wordRelationsTuple, langOrderByMap, searchContext, displayLocale, displayLang);
		lexemeConversionUtil.composeLexemes(wordLang, lexemeWords, langOrderByMap, searchContext, displayLang);
		lexemeConversionUtil.composeMeanings(wordLang, lexemeWords, meanings, allRelatedWords, langOrderByMap, searchContext, displayLang);
		wordConversionUtil.filterWordRelationsBySynonyms(word, lexemeWords);

		if (CollectionUtils.isNotEmpty(lexemeWords)) {
			List<WordCollocPosGroups> wordCollocPosGroups = searchDbService.getWordCollocPosGroups(wordId);
			collocConversionUtil.composeDisplay(wordId, lexemeWords, wordCollocPosGroups, searchContext, displayLang);
			lexemeConversionUtil.flagEmptyLexemes(lexemeWords);
			lexemeWords = lexemeWords.stream().filter(lexeme -> !lexeme.isEmptyLexeme()).collect(Collectors.toList());
			lexemeConversionUtil.sortLexLexemes(lexemeWords);
			lexemeLevelPreseUtil.combineLevels(lexemeWords);
		}

		// word common
		wordConversionUtil.composeCommon(word, lexemeWords, null);

		return composeWordData(word, forms, paradigms, lexemeWords, Collections.emptyList(), Collections.emptyList(), searchContext);
	}

	@Override
	public SearchContext getSearchContext(SearchFilter searchFilter) {

		List<String> destinLangs = searchFilter.getDestinLangs();
		List<String> datasetCodes = Arrays.asList(DATASET_EKI);
		DatasetType datasetType = DatasetType.LEX;
		Integer maxDisplayLevel = SIMPLE_MORPHOLOGY_MAX_DISPLAY_LEVEL;
		List<String> destinLangsClean = destinLangs.stream()
				.filter(destinLang -> !StringUtils.equals(destinLang, DESTIN_LANG_ALL))
				.collect(Collectors.toList());;
		if (CollectionUtils.isNotEmpty(destinLangsClean)) {
			destinLangsClean.add(lANGUAGE_CODE_MUL);
		}
		boolean excludeQuestionable = true;
		boolean fiCollationExists = commonDataDbService.fiCollationExists();
		SearchContext searchContext = new SearchContext(datasetType, destinLangsClean, datasetCodes, maxDisplayLevel, excludeQuestionable, fiCollationExists);
		searchContext.setWwLite(true);
		return searchContext;
	}
}
