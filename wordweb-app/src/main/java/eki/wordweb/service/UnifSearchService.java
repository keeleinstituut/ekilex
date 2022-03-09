package eki.wordweb.service;

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
import eki.wordweb.data.CollocationTuple;
import eki.wordweb.data.Form;
import eki.wordweb.data.LexemeWord;
import eki.wordweb.data.Meaning;
import eki.wordweb.data.Paradigm;
import eki.wordweb.data.SearchContext;
import eki.wordweb.data.SearchFilter;
import eki.wordweb.data.Word;
import eki.wordweb.data.WordData;
import eki.wordweb.data.WordEtymTuple;
import eki.wordweb.data.WordRelationsTuple;
import eki.wordweb.data.WordSearchElement;

@Component
public class UnifSearchService extends AbstractSearchService {

	@Transactional
	@Override
	public WordData getWordData(Long wordId, SearchFilter searchFilter) {

		// query params + common data
		SearchContext searchContext = getSearchContext(searchFilter);
		Complexity lexComplexity = searchContext.getLexComplexity();
		Map<String, Long> langOrderByMap = commonDataDbService.getLangOrderByMap();
		Locale displayLocale = languageContext.getDisplayLocale();
		String displayLang = languageContext.getDisplayLang();

		// word data
		Word word = searchDbService.getWord(wordId);
		String wordLang = word.getLang();
		classifierUtil.applyClassifiers(word, displayLang);
		wordConversionUtil.setWordTypeFlags(word);
		WordRelationsTuple wordRelationsTuple = searchDbService.getWordRelationsTuple(wordId);
		wordConversionUtil.composeWordRelations(word, wordRelationsTuple, langOrderByMap, lexComplexity, displayLocale, displayLang);
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

		List<LexemeWord> lexLexemes = lexemeWords.stream().filter(lexeme -> DatasetType.LEX.equals(lexeme.getDatasetType())).collect(Collectors.toList());
		List<LexemeWord> termLexemes = lexemeWords.stream().filter(lexeme -> DatasetType.TERM.equals(lexeme.getDatasetType()) && !StringUtils.equals(lexeme.getDatasetCode(), DATASET_LIMITED)).collect(Collectors.toList());
		List<LexemeWord> limTermLexemes = lexemeWords.stream().filter(lexeme -> DatasetType.TERM.equals(lexeme.getDatasetType()) && StringUtils.equals(lexeme.getDatasetCode(), DATASET_LIMITED)).collect(Collectors.toList());

		// lex conv
		if (CollectionUtils.isNotEmpty(lexLexemes)) {
			List<CollocationTuple> collocTuples = searchDbService.getCollocations(wordId);
			compensateNullWords(wordId, collocTuples);
			collocConversionUtil.compose(wordId, lexLexemes, collocTuples, searchContext, displayLang);
			lexemeConversionUtil.flagEmptyLexemes(lexLexemes);
			lexLexemes = lexLexemes.stream().filter(lexeme -> !lexeme.isEmptyLexeme()).collect(Collectors.toList());
			lexemeConversionUtil.sortLexemes(lexLexemes, DatasetType.LEX);
			lexemeLevelPreseUtil.combineLevels(lexLexemes);
		}

		// term conv
		if (CollectionUtils.isNotEmpty(termLexemes)) {
			lexemeConversionUtil.sortLexemes(termLexemes, DatasetType.TERM);
		}

		// lim term conv
		if (CollectionUtils.isNotEmpty(limTermLexemes)) {
			lexemeConversionUtil.sortLexemes(limTermLexemes, DatasetType.TERM);
		}

		// word common
		wordConversionUtil.composeCommon(word, lexemeWords);

		return composeWordData(word, forms, paradigms, lexLexemes, termLexemes, limTermLexemes);
	}

	@Transactional
	public WordSearchElement getLinkWord(String linkType, Long linkId, List<String> destinLangs, List<String> datasetCodes) {

		SearchContext searchContext = getSearchContext(destinLangs, datasetCodes);

		if (StringUtils.equals(ContentKey.MEANING_LINK, linkType)) {
			Long meaningId = Long.valueOf(linkId);
			WordSearchElement firstMeaningWord = searchDbService.getFirstMeaningWord(meaningId, searchContext);
			return firstMeaningWord;
		}
		if (StringUtils.equals(ContentKey.WORD_LINK, linkType)) {
			Long wordId = Long.valueOf(linkId);
			//TODO impl
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
