package eki.wordweb.service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import eki.common.constant.Complexity;
import eki.common.constant.DatasetType;
import eki.wordweb.data.CollocationTuple;
import eki.wordweb.data.DataFilter;
import eki.wordweb.data.Form;
import eki.wordweb.data.Lexeme;
import eki.wordweb.data.LexemeMeaningTuple;
import eki.wordweb.data.SearchFilter;
import eki.wordweb.data.Paradigm;
import eki.wordweb.data.Word;
import eki.wordweb.data.WordData;
import eki.wordweb.data.WordRelationsTuple;

@Component
public class SimpleSearchService extends AbstractSearchService {

	@Transactional
	@Override
	public WordData getWordData(Long wordId, SearchFilter searchFilter, String displayLang) {

		// query params + common data
		DataFilter dataFilter = getDataFilter(searchFilter);
		Integer maxDisplayLevel = dataFilter.getMaxDisplayLevel();
		Complexity lexComplexity = dataFilter.getLexComplexity();
		Map<String, Long> langOrderByMap = commonDataDbService.getLangOrderByMap();

		// word data
		Word word = searchDbService.getWord(wordId);
		String wordLang = word.getLang();
		classifierUtil.applyClassifiers(word, displayLang);
		wordConversionUtil.setWordTypeFlags(word);
		WordRelationsTuple wordRelationsTuple = searchDbService.getWordRelationsTuple(wordId);
		wordConversionUtil.composeWordRelations(word, wordRelationsTuple, langOrderByMap, lexComplexity, displayLang);
		List<Form> forms = searchDbService.getWordForms(wordId, maxDisplayLevel);
		List<Paradigm> paradigms = paradigmConversionUtil.composeParadigms(forms, DISPLAY_LANG);
		List<String> allRelatedWords = wordConversionUtil.collectAllRelatedWords(word);

		// lexeme data
		List<Lexeme> lexemes = searchDbService.getLexemes(wordId, dataFilter);
		List<LexemeMeaningTuple> lexemeMeaningTuples = searchDbService.getLexemeMeaningTuples(wordId);
		Map<Long, LexemeMeaningTuple> lexemeMeaningTupleMap = lexemeMeaningTuples.stream().collect(Collectors.toMap(LexemeMeaningTuple::getLexemeId, lexemeMeaningTuple -> lexemeMeaningTuple));
		lexemeConversionUtil.compose(wordLang, lexemes, lexemeMeaningTupleMap, allRelatedWords, langOrderByMap, dataFilter, displayLang);

		if (CollectionUtils.isNotEmpty(lexemes)) {
			List<CollocationTuple> collocTuples = searchDbService.getCollocations(wordId);
			compensateNullWords(wordId, collocTuples);
			collocConversionUtil.compose(wordId, lexemes, collocTuples, dataFilter, displayLang);
			lexemeConversionUtil.flagEmptyLexemes(lexemes);
			lexemes = lexemes.stream().filter(lexeme -> !lexeme.isEmptyLexeme()).collect(Collectors.toList());
			lexemeConversionUtil.sortLexemes(lexemes, DatasetType.LEX);
			lexemeLevelPreseUtil.combineLevels(lexemes);
		}

		// word common
		wordConversionUtil.composeCommon(word, lexemes);

		return composeWordData(word, forms, paradigms, lexemes, Collections.emptyList(), Collections.emptyList());
	}

	@Override
	public DataFilter getDataFilter(SearchFilter searchFilter) {
		List<String> destinLangs = searchFilter.getDestinLangs();
		List<String> datasetCodes = Arrays.asList(DATASET_SSS);
		Complexity lexComplexity = Complexity.SIMPLE;
		DatasetType datasetType = DatasetType.LEX;
		Integer maxDisplayLevel = SIMPLE_MORPHOLOGY_MAX_DISPLAY_LEVEL;
		List<String> destinLangsClean = destinLangs.stream().filter(destinLang -> !StringUtils.equals(destinLang, DESTIN_LANG_ALL)).collect(Collectors.toList());
		boolean fiCollationExists = commonDataDbService.fiCollationExists();
		DataFilter dataFilter = new DataFilter(datasetType, destinLangsClean, datasetCodes, lexComplexity, maxDisplayLevel, fiCollationExists);
		return dataFilter;
	}
}
