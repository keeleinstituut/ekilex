package eki.wordweb.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
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
public class LexSearchService {

	@Autowired
	private LexSearchDbService lexSearchDbService;

	@Autowired
	private ConversionUtil conversionUtil;

	@Transactional
	public List<Word> findWords(String searchFilter) {

		List<Word> formMatchWords = lexSearchDbService.findWords(searchFilter);
		List<Word> fullMatchWords = formMatchWords.stream().filter(word -> StringUtils.equalsIgnoreCase(word.getWord(), searchFilter)).collect(Collectors.toList());
		if (CollectionUtils.isNotEmpty(fullMatchWords)) {
			return fullMatchWords;
		}
		return formMatchWords;
	}

	@Transactional
	public WordData getWordData(Long wordId, String displayLang) {

		List<LexemeMeaningTuple> lexemeMeaningTuples = lexSearchDbService.findLexemeMeaningTuples(wordId);
		List<LexemeDetailsTuple> lexemeDetailsTuples = lexSearchDbService.findLexemeDetailsTuples(wordId);
		List<Lexeme> lexemes = conversionUtil.composeLexemes(lexemeMeaningTuples, lexemeDetailsTuples, displayLang);
		Map<Long, List<Form>> paradigmFormsMap = lexSearchDbService.findWordForms(wordId);
		List<Paradigm> paradigms = conversionUtil.composeParadigms(paradigmFormsMap, displayLang);
		WordData wordData = new WordData();
		wordData.setLexemes(lexemes);
		wordData.setParadigms(paradigms);
		List<String> allImageFiles = new ArrayList<>();
		lexemes.forEach(l -> {
			if (l.getImageFiles() != null) {
				allImageFiles.addAll(l.getImageFiles());
			}
		});
		wordData.setImageFiles(allImageFiles);
		return wordData;
	}
}
