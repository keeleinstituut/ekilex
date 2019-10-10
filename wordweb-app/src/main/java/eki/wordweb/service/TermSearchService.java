package eki.wordweb.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.FormMode;
import eki.wordweb.constant.SystemConstant;
import eki.wordweb.constant.WebConstant;
import eki.wordweb.data.Form;
import eki.wordweb.data.Lexeme;
import eki.wordweb.data.LexemeMeaningTuple;
import eki.wordweb.data.Paradigm;
import eki.wordweb.data.Word;
import eki.wordweb.data.WordData;
import eki.wordweb.data.WordEtymTuple;
import eki.wordweb.data.WordOrForm;
import eki.wordweb.data.WordRelationTuple;
import eki.wordweb.data.WordsData;
import eki.wordweb.service.db.TermSearchDbService;
import eki.wordweb.service.util.ClassifierUtil;
import eki.wordweb.service.util.ConversionUtil;

@Component
public class TermSearchService implements SystemConstant, WebConstant {

	@Autowired
	private TermSearchDbService termSearchDbService;

	@Autowired
	private ClassifierUtil classifierUtil;

	@Autowired
	private ConversionUtil conversionUtil;

	@Transactional
	public WordsData getWords(String searchWord, Integer homonymNr) {

		List<Word> allWords = termSearchDbService.getWords(searchWord);
		boolean resultsExist = CollectionUtils.isNotEmpty(allWords);
		conversionUtil.setAffixoidFlags(allWords);
		conversionUtil.composeHomonymWrapups(allWords);
		conversionUtil.selectHomonym(allWords, homonymNr);
		boolean isSingleResult = CollectionUtils.size(allWords) == 1;
		return new WordsData(allWords, Collections.emptyList(), null, false, resultsExist, isSingleResult);
	}

	@Transactional
	public Map<String, List<String>> getWordsByPrefix(String wordPrefix, int limit) {

		Map<String, List<WordOrForm>> results = termSearchDbService.getWordsByPrefix(wordPrefix, limit);
		List<WordOrForm> prefWordsResult = results.get("prefWords");
		List<String> prefWords;
		if (CollectionUtils.isEmpty(prefWordsResult)) {
			prefWords = Collections.emptyList();
		} else {
			prefWords = prefWordsResult.stream().map(WordOrForm::getValue).collect(Collectors.toList());
		}
		Map<String, List<String>> searchResultCandidates = new HashMap<>();
		searchResultCandidates.put("prefWords", prefWords);
		searchResultCandidates.put("formWords", Collections.emptyList());
		return searchResultCandidates;
	}

	@Transactional
	public WordData getWordData(Long wordId, String displayLang) {

		Word word = termSearchDbService.getWord(wordId);
		classifierUtil.applyClassifiers(word, displayLang);
		conversionUtil.setWordTypeFlags(word);
		List<WordEtymTuple> wordEtymTuples = termSearchDbService.getWordEtymologyTuples(wordId);
		conversionUtil.composeWordEtymology(word, wordEtymTuples, displayLang);
		List<WordRelationTuple> wordRelationTuples = termSearchDbService.getWordRelationTuples(wordId);
		conversionUtil.composeWordRelations(word, wordRelationTuples, null, displayLang);
		List<Lexeme> lexemes = termSearchDbService.getLexemes(wordId);
		List<LexemeMeaningTuple> lexemeMeaningTuples = termSearchDbService.getLexemeMeaningTuples(wordId);
		conversionUtil.enrich(word, lexemes, lexemeMeaningTuples, displayLang);
		Map<Long, List<Form>> paradigmFormsMap = termSearchDbService.getWordForms(wordId, SIMPLE_MORPHOLOGY_MAX_DISPLAY_LEVEL);
		List<Paradigm> paradigms = conversionUtil.composeParadigms(word, paradigmFormsMap, displayLang);
		List<String> allImageFiles = conversionUtil.collectImages(lexemes);

		// resulting flags
		String firstAvailableVocalForm = null;
		String firstAvailableAudioFile = null;
		boolean isUnknownForm = false;
		if (MapUtils.isNotEmpty(paradigmFormsMap)) {
			Form firstAvailableWordForm = paradigmFormsMap.values().stream()
					.filter(forms -> forms.stream().anyMatch(form -> form.getMode().equals(FormMode.WORD)))
					.map(forms -> forms.stream().filter(form -> form.getMode().equals(FormMode.WORD)).findFirst().orElse(null))
					.findFirst().orElse(null);
			if (firstAvailableWordForm != null) {
				firstAvailableVocalForm = firstAvailableWordForm.getVocalForm();
				firstAvailableAudioFile = firstAvailableWordForm.getAudioFile();
				isUnknownForm = StringUtils.equals(UNKNOWN_FORM_CODE, firstAvailableWordForm.getMorphCode());
			}
		}

		WordData wordData = new WordData();
		wordData.setWord(word);
		wordData.setLexemes(lexemes);
		wordData.setParadigms(paradigms);
		wordData.setImageFiles(allImageFiles);
		wordData.setFirstAvailableVocalForm(firstAvailableVocalForm);
		wordData.setFirstAvailableAudioFile(firstAvailableAudioFile);
		wordData.setUnknownForm(isUnknownForm);
		return wordData;
	}
}
