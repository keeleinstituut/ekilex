package eki.wordweb.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.Complexity;
import eki.common.constant.DatasetType;
import eki.wordweb.data.CollocationTuple;
import eki.wordweb.data.DataFilter;
import eki.wordweb.data.Form;
import eki.wordweb.data.Lexeme;
import eki.wordweb.data.LexemeMeaningTuple;
import eki.wordweb.data.Paradigm;
import eki.wordweb.data.SearchFilter;
import eki.wordweb.data.Word;
import eki.wordweb.data.WordData;
import eki.wordweb.data.WordEtymTuple;
import eki.wordweb.data.WordRelationsTuple;
import eki.wordweb.service.db.AbstractSearchDbService;
import eki.wordweb.service.db.UnifSearchDbService;

@Component
public class UnifSearchService extends AbstractSearchService {

	@Autowired
	private UnifSearchDbService unifSearchDbService;

	@Override
	public AbstractSearchDbService getSearchDbService() {
		return unifSearchDbService;
	}

	@Transactional
	@Override
	public WordData getWordData(Long wordId, SearchFilter searchFilter, String displayLang) {

		// query params + common data
		DataFilter dataFilter = getDataFilter(searchFilter);
		Integer maxDisplayLevel = dataFilter.getMaxDisplayLevel();
		Complexity lexComplexity = dataFilter.getLexComplexity();
		Map<String, Long> langOrderByMap = commonDataDbService.getLangOrderByMap();

		// word data
		Word word = unifSearchDbService.getWord(wordId);
		String wordLang = word.getLang();
		classifierUtil.applyClassifiers(word, displayLang);
		wordConversionUtil.setWordTypeFlags(word);
		WordRelationsTuple wordRelationsTuple = unifSearchDbService.getWordRelationsTuple(wordId);
		wordConversionUtil.composeWordRelations(word, wordRelationsTuple, langOrderByMap, lexComplexity, displayLang);
		List<WordEtymTuple> wordEtymTuples = unifSearchDbService.getWordEtymologyTuples(wordId);
		etymConversionUtil.composeWordEtymology(word, wordEtymTuples, displayLang);
		Map<Long, List<Form>> paradigmFormsMap = unifSearchDbService.getWordForms(wordId, maxDisplayLevel);
		List<Paradigm> paradigms = paradigmConversionUtil.composeParadigms(word, paradigmFormsMap, displayLang);
		List<String> allRelatedWords = wordConversionUtil.collectAllRelatedWords(word);

		// lexeme data
		List<Lexeme> lexemes = unifSearchDbService.getLexemes(wordId, dataFilter);
		List<LexemeMeaningTuple> lexemeMeaningTuples = unifSearchDbService.getLexemeMeaningTuples(wordId);
		Map<Long, LexemeMeaningTuple> lexemeMeaningTupleMap = lexemeMeaningTuples.stream().collect(Collectors.toMap(LexemeMeaningTuple::getLexemeId, lexemeMeaningTuple -> lexemeMeaningTuple));
		Map<DatasetType, List<Lexeme>> lexemeGroups = lexemes.stream().collect(Collectors.groupingBy(Lexeme::getDatasetType));

		// lex conv
		List<Lexeme> lexLexemes = lexemeGroups.get(DatasetType.LEX);
		if (CollectionUtils.isNotEmpty(lexLexemes)) {
			List<CollocationTuple> collocTuples = unifSearchDbService.getCollocations(wordId);
			compensateNullWords(wordId, collocTuples);
			lexemeConversionUtil.compose(
					DatasetType.LEX, wordLang, lexLexemes, lexemeMeaningTupleMap,
					allRelatedWords, langOrderByMap, dataFilter, displayLang);
			collocConversionUtil.compose(wordId, lexLexemes, collocTuples, dataFilter, displayLang);
			lexemeLevelPreseUtil.combineLevels(lexLexemes);
			lexemeConversionUtil.flagEmptyLexemes(lexLexemes);
			lexLexemes = lexLexemes.stream().filter(lexeme -> !lexeme.isEmptyLexeme()).collect(Collectors.toList());
			lexemeConversionUtil.sortLexemes(lexLexemes, DatasetType.LEX);
		}

		// term conv
		List<Lexeme> termLexemes = lexemeGroups.get(DatasetType.TERM);
		if (CollectionUtils.isNotEmpty(termLexemes)) {
			lexemeConversionUtil.compose(
					DatasetType.TERM, wordLang, termLexemes, lexemeMeaningTupleMap,
					allRelatedWords, langOrderByMap, dataFilter, displayLang);
			lexemeConversionUtil.sortLexemes(termLexemes, DatasetType.TERM);
		}

		// word common
		wordConversionUtil.composeCommon(word, lexemes);

		return composeWordData(word, paradigmFormsMap, paradigms, lexLexemes, termLexemes);
	}

	@Override
	public DataFilter getDataFilter(SearchFilter searchFilter) {
		List<String> destinLangs = searchFilter.getDestinLangs();
		List<String> datasetCodes = searchFilter.getDatasetCodes();
		Complexity lexComplexity = Complexity.DETAIL;
		DatasetType datasetType = null;
		Integer maxDisplayLevel = DEFAULT_MORPHOLOGY_MAX_DISPLAY_LEVEL;
		List<String> destinLangsClean = destinLangs.stream().filter(destinLang -> !StringUtils.equals(destinLang, DESTIN_LANG_ALL)).collect(Collectors.toList());
		List<String> datasetCodesClean = datasetCodes.stream().filter(datasetCode -> !StringUtils.equals(datasetCode, DATASET_ALL)).collect(Collectors.toList());
		DataFilter dataFilter = new DataFilter(datasetType, destinLangsClean, datasetCodesClean, lexComplexity, maxDisplayLevel);
		return dataFilter;
	}
}
