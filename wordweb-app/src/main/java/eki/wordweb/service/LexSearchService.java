package eki.wordweb.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.wordweb.data.CollocationTuple;
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
		languagesDatasetMap.put("estest", new String[] {"ss1", "psv", "kol"});
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
		List<WordOrForm> prefWordsResult = results.get("prefWords");
		List<WordOrForm> formWordsResult = results.get("formWords");
		List<String> prefWords, formWords;
		if (CollectionUtils.isEmpty(prefWordsResult)) {
			prefWords = Collections.emptyList();
		} else {
			prefWords = prefWordsResult.stream().map(WordOrForm::getValue).collect(Collectors.toList());
		}
		if (CollectionUtils.isEmpty(formWordsResult)) {
			formWords = Collections.emptyList();
		} else {
			formWords = formWordsResult.stream().map(WordOrForm::getValue).collect(Collectors.toList());
		}
		if (CollectionUtils.isNotEmpty(prefWords)) {
			prefWords.forEach(prefWord -> formWords.remove(prefWord));
			int prefWordsCount = prefWords.size();
			int formWordsCount = formWords.size();
			int requiredPrefWordsCount = Math.min(prefWordsCount, limit - formWordsCount);
			prefWords = prefWords.subList(0, requiredPrefWordsCount);
		}
		Map<String, List<String>> searchResultCandidates = new HashMap<>();
		searchResultCandidates.put("prefWords", prefWords);
		searchResultCandidates.put("formWords", formWords);
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
		List<CollocationTuple> collocTuples = lexSearchDbService.findCollocations(wordId, datasets);
		List<Lexeme> lexemes = conversionUtil.composeLexemes(lexemeMeaningTuples, lexemeDetailsTuples, collocTuples, sourceLang, destinLang, displayLang);
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
		combineLevels(wordData.getLexemes());
		return wordData;
	}

	private void combineLevels(List<Lexeme> lexemes) {

		if (CollectionUtils.isEmpty(lexemes)) {
			return;
		}

		lexemes.forEach(lexeme -> {
			if (lexeme.getLevel1() == 0) {
				lexeme.setLevels(null);
				return;
			}
			String levels;
			long nrOfLexemesWithSameLevel1 = lexemes.stream()
					.filter(otherLexeme ->
							otherLexeme.getLevel1().equals(lexeme.getLevel1())
									&& StringUtils.equals(otherLexeme.getDatasetCode(), lexeme.getDatasetCode()))
					.count();
			if (nrOfLexemesWithSameLevel1 == 1) {
				levels = String.valueOf(lexeme.getLevel1());
			} else {
				long nrOfLexemesWithSameLevel2 = lexemes.stream()
						.filter(otherLexeme ->
								otherLexeme.getLevel1().equals(lexeme.getLevel1())
										&& otherLexeme.getLevel2().equals(lexeme.getLevel2())
										&& StringUtils.equals(otherLexeme.getDatasetCode(), lexeme.getDatasetCode()))
						.count();
				if (nrOfLexemesWithSameLevel2 == 1) {
					levels = lexeme.getLevel1() + "." + lexeme.getLevel2();
				} else {
					levels = lexeme.getLevel1() + "." + lexeme.getLevel2() + "." + lexeme.getLevel3();
				}
			}
			lexeme.setLevels(levels);
		});
	}

}
