package eki.wordweb.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import eki.common.constant.ClassifierName;
import eki.wordweb.data.Relation;
import eki.wordweb.data.WordsData;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.wordweb.data.Form;
import eki.wordweb.data.Lexeme;
import eki.wordweb.data.LexemeDetailsTuple;
import eki.wordweb.data.LexemeMeaningTuple;
import eki.wordweb.data.Paradigm;
import eki.wordweb.data.Word;
import eki.wordweb.data.WordData;
import eki.wordweb.service.db.LexSearchDbService;
import eki.wordweb.service.util.ConversionUtil;

@Component
public class LexSearchService implements InitializingBean {

	@Autowired
	private LexSearchDbService lexSearchDbService;

	@Autowired
	private ConversionUtil conversionUtil;

	private Map<String, String[]> languagesDatasetMap;

	@Override
	public void afterPropertiesSet() throws Exception {

		languagesDatasetMap = new HashMap<>();
		languagesDatasetMap.put("estest", new String[] {"ss1", "psv"});
		languagesDatasetMap.put("estrus", new String[] {"qq2"});
		languagesDatasetMap.put("rusest", new String[] {"qq2"});
	}

	@Transactional
	public WordsData findWords(String searchFilter, String sourceLang, String destinLang) {

		String languagesDatasetKey = sourceLang + destinLang;
		String[] datasets = languagesDatasetMap.get(languagesDatasetKey);
		List<Word> allWords = lexSearchDbService.findWords(searchFilter, sourceLang, datasets);
		conversionUtil.filterLanguageValues(allWords, destinLang);
		List<Word> fullMatchWords = allWords.stream().filter(word -> StringUtils.equalsIgnoreCase(word.getWord(), searchFilter)).collect(Collectors.toList());
		if (CollectionUtils.isNotEmpty(fullMatchWords)) {
			List<String> formMatchWords = CollectionUtils.subtract(allWords, fullMatchWords).stream().map(word -> word.getWord()).distinct().collect(Collectors.toList());
			return new WordsData(fullMatchWords, formMatchWords);
		}
		return new WordsData(allWords, Collections.emptyList());
	}

	@Transactional
	public WordData getWordData(Long wordId, String sourceLang, String destinLang, String displayLang) {

		String languagesDatasetKey = sourceLang + destinLang;
		String[] datasets = languagesDatasetMap.get(languagesDatasetKey);
		Word word = lexSearchDbService.getWord(wordId);
		List<LexemeMeaningTuple> lexemeMeaningTuples = lexSearchDbService.findLexemeMeaningTuples(wordId, datasets);
		List<LexemeDetailsTuple> lexemeDetailsTuples = lexSearchDbService.findLexemeDetailsTuples(wordId, datasets);
		List<Lexeme> lexemes = conversionUtil.composeLexemes(lexemeMeaningTuples, lexemeDetailsTuples, sourceLang, destinLang, displayLang);
		Map<Long, List<Form>> paradigmFormsMap = lexSearchDbService.findWordForms(wordId);
		List<Paradigm> paradigms = conversionUtil.composeParadigms(paradigmFormsMap, displayLang);
		List<String> allImageFiles = new ArrayList<>();
		lexemes.forEach(lexeme -> {
			if (CollectionUtils.isNotEmpty(lexeme.getImageFiles())) {
				allImageFiles.addAll(lexeme.getImageFiles());
			}
			List<Relation> lexemeRelations = lexSearchDbService.findLexemeRelations(lexeme.getLexemeId());
			lexemeRelations = conversionUtil.compactRelationsByLabelAndType(lexemeRelations);
			conversionUtil.composeRelations(lexemeRelations, ClassifierName.LEX_REL_TYPE, displayLang);
			lexeme.setLexemeRelations(lexemeRelations);
		});
		List<Relation> wordRelations = lexSearchDbService.findWordRelations(wordId);
		conversionUtil.composeRelations(wordRelations, ClassifierName.WORD_REL_TYPE, displayLang);

		WordData wordData = new WordData();
		wordData.setWord(word);
		wordData.setLexemes(lexemes);
		wordData.setParadigms(paradigms);
		wordData.setImageFiles(allImageFiles);
		wordData.setWordRelations(wordRelations);
		return wordData;
	}
}
