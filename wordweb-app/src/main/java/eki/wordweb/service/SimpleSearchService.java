package eki.wordweb.service;

import java.util.Arrays;
import java.util.Collections;
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
import eki.wordweb.data.WordRelationsTuple;
import eki.wordweb.service.db.AbstractSearchDbService;
import eki.wordweb.service.db.SimpleSearchDbService;

@Component
public class SimpleSearchService extends AbstractSearchService {

	@Autowired
	private SimpleSearchDbService simpleSearchDbService;

	@Override
	public AbstractSearchDbService getSearchDbService() {
		return simpleSearchDbService;
	}

	@Transactional
	@Override
	public WordData getWordData(Long wordId, SearchFilter searchFilter, String displayLang) {

		// query params
		DataFilter dataFilter = getDataFilter(searchFilter);
		Integer maxDisplayLevel = dataFilter.getMaxDisplayLevel();
		Complexity lexComplexity = dataFilter.getLexComplexity();

		// word data
		Word word = simpleSearchDbService.getWord(wordId);
		String wordLang = word.getLang();
		classifierUtil.applyClassifiers(word, displayLang);
		wordConversionUtil.setWordTypeFlags(word);
		WordRelationsTuple wordRelationsTuple = simpleSearchDbService.getWordRelationsTuple(wordId);
		wordConversionUtil.composeWordRelations(word, wordRelationsTuple, lexComplexity, displayLang);
		Map<Long, List<Form>> paradigmFormsMap = simpleSearchDbService.getWordForms(wordId, maxDisplayLevel);
		List<Paradigm> paradigms = paradigmConversionUtil.composeParadigms(word, paradigmFormsMap, displayLang);
		List<String> allRelatedWords = wordConversionUtil.collectAllRelatedWords(word);
		Map<String, Long> langOrderByMap = commonDataDbService.getLangOrderByMap();

		// lexeme data
		List<Lexeme> lexemes = simpleSearchDbService.getLexemes(wordId, dataFilter);
		List<LexemeMeaningTuple> lexemeMeaningTuples = simpleSearchDbService.getLexemeMeaningTuples(wordId);
		Map<Long, LexemeMeaningTuple> lexemeMeaningTupleMap = lexemeMeaningTuples.stream().collect(Collectors.toMap(LexemeMeaningTuple::getLexemeId, lexemeMeaningTuple -> lexemeMeaningTuple));

		if (CollectionUtils.isNotEmpty(lexemes)) {
			List<CollocationTuple> collocTuples = simpleSearchDbService.getCollocations(wordId);
			compensateNullWords(wordId, collocTuples);
			lexemeConversionUtil.compose(
					DatasetType.LEX, wordLang, lexemes, lexemeMeaningTupleMap,
					allRelatedWords, langOrderByMap, dataFilter, displayLang);
			lexemes = lexemes.stream().filter(lexeme -> !lexeme.isEmptyLexeme()).collect(Collectors.toList());
			collocConversionUtil.compose(wordId, lexemes, collocTuples, dataFilter, displayLang);
			lexemeLevelPreseUtil.combineLevels(lexemes);
		}

		// word common
		wordConversionUtil.composeCommon(word, lexemes);

		return composeWordData(word, paradigmFormsMap, paradigms, lexemes, Collections.emptyList());
	}

	@Override
	public DataFilter getDataFilter(SearchFilter searchFilter) {
		List<String> destinLangs = searchFilter.getDestinLangs();
		List<String> datasetCodes = Arrays.asList(DATASET_SSS);
		Complexity lexComplexity = Complexity.SIMPLE;
		DatasetType datasetType = DatasetType.LEX;
		Integer maxDisplayLevel = SIMPLE_MORPHOLOGY_MAX_DISPLAY_LEVEL;
		List<String> destinLangsClean = destinLangs.stream().filter(destinLang -> !StringUtils.equals(destinLang, DESTIN_LANG_ALL)).collect(Collectors.toList());
		DataFilter dataFilter = new DataFilter(datasetType, destinLangsClean, datasetCodes, lexComplexity, maxDisplayLevel);
		return dataFilter;
	}
}
