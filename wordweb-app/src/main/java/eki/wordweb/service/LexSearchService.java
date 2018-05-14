package eki.wordweb.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

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
import eki.wordweb.data.WordOrForm;
import eki.wordweb.data.WordsData;
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
	public WordsData findWords(String searchWord, String sourceLang, String destinLang, Integer homonymNr) {

		String languagesDatasetKey = sourceLang + destinLang;
		String[] datasets = languagesDatasetMap.get(languagesDatasetKey);
		List<Word> allWords = lexSearchDbService.findWords(searchWord, sourceLang, datasets);
		conversionUtil.filterLanguageValues(allWords, destinLang);
		conversionUtil.selectHomonym(allWords, homonymNr);
		List<Word> fullMatchWords = allWords.stream().filter(word -> StringUtils.equalsIgnoreCase(word.getWord(), searchWord)).collect(Collectors.toList());
		if (CollectionUtils.isNotEmpty(fullMatchWords)) {
			List<String> formMatchWords = CollectionUtils.subtract(allWords, fullMatchWords).stream().map(word -> word.getWord()).distinct().collect(Collectors.toList());
			return new WordsData(fullMatchWords, formMatchWords);
		}
		return new WordsData(allWords, Collections.emptyList());
	}

	@Transactional
	public Map<String, List<String>> findWordsByPrefix(String wordPrefix, String sourceLang, String destinLang, int limit) {

		String languagesDatasetKey = sourceLang + destinLang;
		String[] datasets = languagesDatasetMap.get(languagesDatasetKey);
		Map<String, List<WordOrForm>> results = lexSearchDbService.findWordsByPrefix(wordPrefix, sourceLang, datasets, limit);
		Set<Entry<String, List<WordOrForm>>> resultsEntrySet = results.entrySet();
		Map<String, List<String>> searchResultCandidates = new HashMap<>();
		for (Entry<String, List<WordOrForm>> resultsEntry : resultsEntrySet) {
			String group = resultsEntry.getKey();
			List<WordOrForm> resultsList = resultsEntry.getValue();
			List<String> wordsOrForms = resultsList.stream().map(WordOrForm::getValue).collect(Collectors.toList());
			searchResultCandidates.put(group, wordsOrForms);
		}
		return searchResultCandidates;
	}

	@Transactional
	public WordData getWordData(Long wordId, String sourceLang, String destinLang, String displayLang) {

		String languagesDatasetKey = sourceLang + destinLang;
		String[] datasets = languagesDatasetMap.get(languagesDatasetKey);
		Word word = lexSearchDbService.getWord(wordId);
		conversionUtil.populateWordRelationClassifiers(word, displayLang);
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
		});
		WordData wordData = new WordData();
		wordData.setWord(word);
		wordData.setLexemes(lexemes);
		wordData.setParadigms(paradigms);
		wordData.setImageFiles(allImageFiles);
		return wordData;
	}
}
