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

import eki.common.constant.Complexity;
import eki.common.constant.DatasetType;
import eki.wordweb.data.CollocationTuple;
import eki.wordweb.data.Form;
import eki.wordweb.data.LexemeWord;
import eki.wordweb.data.Meaning;
import eki.wordweb.data.Paradigm;
import eki.wordweb.data.SearchContext;
import eki.wordweb.data.SearchFilter;
import eki.wordweb.data.Word;
import eki.wordweb.data.WordData;
import eki.wordweb.data.WordRelationsTuple;

@Component
public class SimpleSearchService extends AbstractSearchService {

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
		List<Form> forms = searchDbService.getWordForms(wordId, searchContext);
		List<Paradigm> paradigms = paradigmConversionUtil.composeParadigms(forms, displayLang);
		List<String> allRelatedWords = wordConversionUtil.collectAllRelatedWords(word);

		// lexeme data
		List<LexemeWord> lexemeWords = searchDbService.getWordLexemes(wordId, searchContext);
		lexemeConversionUtil.composeLexemes(wordLang, lexemeWords, langOrderByMap, searchContext, displayLang);
		List<Meaning> meanings = searchDbService.getMeanings(wordId);
		Map<Long, Meaning> lexemeMeaningMap = meanings.stream().collect(Collectors.toMap(Meaning::getLexemeId, meaning -> meaning));
		lexemeConversionUtil.composeMeanings(wordLang, lexemeWords, lexemeMeaningMap, allRelatedWords, langOrderByMap, searchContext, displayLang);

		if (CollectionUtils.isNotEmpty(lexemeWords)) {
			List<CollocationTuple> collocTuples = searchDbService.getCollocations(wordId);
			compensateNullWords(wordId, collocTuples);
			collocConversionUtil.compose(wordId, lexemeWords, collocTuples, searchContext, displayLang);
			lexemeConversionUtil.flagEmptyLexemes(lexemeWords);
			lexemeWords = lexemeWords.stream().filter(lexeme -> !lexeme.isEmptyLexeme()).collect(Collectors.toList());
			lexemeConversionUtil.sortLexemes(lexemeWords, DatasetType.LEX);
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
		Complexity lexComplexity = Complexity.SIMPLE;
		DatasetType datasetType = DatasetType.LEX;
		Integer maxDisplayLevel = SIMPLE_MORPHOLOGY_MAX_DISPLAY_LEVEL;
		List<String> destinLangsClean = destinLangs.stream().filter(destinLang -> !StringUtils.equals(destinLang, DESTIN_LANG_ALL)).collect(Collectors.toList());
		boolean excludeQuestionable = true;
		boolean fiCollationExists = commonDataDbService.fiCollationExists();
		SearchContext searchContext = new SearchContext(datasetType, destinLangsClean, datasetCodes, lexComplexity, maxDisplayLevel, excludeQuestionable, fiCollationExists);
		return searchContext;
	}
}
