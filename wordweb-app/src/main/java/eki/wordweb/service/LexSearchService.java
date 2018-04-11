package eki.wordweb.service;

import java.util.ArrayList;
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
	public List<Word> findWords(String searchFilter, String sourceLang, String destinLang) {

		String languagesDatasetKey = sourceLang + destinLang;
		String[] datasets = languagesDatasetMap.get(languagesDatasetKey);
		List<Word> formMatchWords = lexSearchDbService.findWords(searchFilter, sourceLang, datasets);
		conversionUtil.filterLanguageValues(formMatchWords, destinLang);
		List<Word> fullMatchWords = formMatchWords.stream().filter(word -> StringUtils.equalsIgnoreCase(word.getWord(), searchFilter)).collect(Collectors.toList());
		if (CollectionUtils.isNotEmpty(fullMatchWords)) {
			return fullMatchWords;
		}
		return formMatchWords;
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
		});

		WordData wordData = new WordData();
		wordData.setWord(word);
		wordData.setLexemes(lexemes);
		wordData.setParadigms(paradigms);
		wordData.setImageFiles(allImageFiles);
		return wordData;
	}
}
