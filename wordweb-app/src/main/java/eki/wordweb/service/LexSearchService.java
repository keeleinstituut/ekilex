package eki.wordweb.service;

import static java.lang.Math.max;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.Complexity;
import eki.common.constant.FormMode;
import eki.wordweb.constant.SystemConstant;
import eki.wordweb.data.CollocationTuple;
import eki.wordweb.data.DataFilter;
import eki.wordweb.data.Form;
import eki.wordweb.data.Lexeme;
import eki.wordweb.data.LexemeMeaningTuple;
import eki.wordweb.data.Paradigm;
import eki.wordweb.data.TypeCollocMember;
import eki.wordweb.data.Word;
import eki.wordweb.data.WordData;
import eki.wordweb.data.WordEtymTuple;
import eki.wordweb.data.WordForm;
import eki.wordweb.data.WordOrForm;
import eki.wordweb.data.WordRelationTuple;
import eki.wordweb.data.WordsData;
import eki.wordweb.service.db.LexSearchDbService;
import eki.wordweb.service.util.ClassifierUtil;
import eki.wordweb.service.util.ConversionUtil;

@Component
public class LexSearchService implements InitializingBean, SystemConstant {

	private static final Integer DEFAULT_MORPHOLOGY_MAX_DISPLAY_LEVEL = 3;

	private static final Integer SIMPLE_MORPHOLOGY_MAX_DISPLAY_LEVEL = 2;

	@Autowired
	private LexSearchDbService lexSearchDbService;

	@Autowired
	private ClassifierUtil classifierUtil;

	@Autowired
	private ConversionUtil conversionUtil;

	@Override
	public void afterPropertiesSet() {
	}

	@Transactional
	public WordsData getWords(String searchWord, String sourceLang, String destinLang, Integer homonymNr, String searchMode) {

		DataFilter dataFilter = getDataFilter(sourceLang, destinLang, searchMode);
		List<Word> allWords = lexSearchDbService.getWords(searchWord, dataFilter);
		boolean isForcedSearchMode = false;
		if (CollectionUtils.isEmpty(allWords) && StringUtils.equals(searchMode, SEARCH_MODE_SIMPLE)) {
			dataFilter = getDataFilter(sourceLang, destinLang, SEARCH_MODE_DETAIL);
			allWords = lexSearchDbService.getWords(searchWord, dataFilter);
			if (CollectionUtils.isNotEmpty(allWords)) {
				searchMode = SEARCH_MODE_DETAIL;
				isForcedSearchMode = true;
			}
		}
		boolean resultsExist = CollectionUtils.isNotEmpty(allWords);
		conversionUtil.setAffixoidFlags(allWords);
		conversionUtil.composeHomonymWrapups(allWords, dataFilter);
		conversionUtil.selectHomonym(allWords, homonymNr);
		List<Word> fullMatchWords = allWords.stream().filter(word -> StringUtils.equalsIgnoreCase(word.getWord(), searchWord)).collect(Collectors.toList());
		if (CollectionUtils.isNotEmpty(fullMatchWords)) {
			List<String> formMatchWords = CollectionUtils.subtract(allWords, fullMatchWords).stream().map(Word::getWord).distinct().collect(Collectors.toList());
			boolean isSingleResult = CollectionUtils.size(fullMatchWords) == 1;
			return new WordsData(fullMatchWords, formMatchWords, searchMode, isForcedSearchMode, resultsExist, isSingleResult);
		}
		boolean isSingleResult = CollectionUtils.size(allWords) == 1;
		return new WordsData(allWords, Collections.emptyList(), searchMode, isForcedSearchMode, resultsExist, isSingleResult);
	}

	@Transactional
	public Map<String, List<String>> getWordsByPrefix(String wordPrefix, String sourceLang, String destinLang, int limit) {

		Map<String, List<WordOrForm>> results = lexSearchDbService.getWordsByPrefix(wordPrefix, sourceLang, limit);
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
			prefWords.forEach(formWords::remove);
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
	public WordData getWordData(Long wordId, String sourceLang, String destinLang, String displayLang, String searchMode) {

		// query params
		DataFilter dataFilter = getDataFilter(sourceLang, destinLang, searchMode);
		Integer maxDisplayLevel = dataFilter.getMaxDisplayLevel();
		Complexity lexComplexity = dataFilter.getLexComplexity();

		// queries and transformations
		Word word = lexSearchDbService.getWord(wordId);
		classifierUtil.applyClassifiers(word, displayLang);
		conversionUtil.setWordTypeFlags(word);
		List<WordEtymTuple> wordEtymTuples = lexSearchDbService.getWordEtymologyTuples(wordId);
		conversionUtil.composeWordEtymology(word, wordEtymTuples, displayLang);
		List<WordRelationTuple> wordRelationTuples = lexSearchDbService.getWordRelationTuples(wordId);
		conversionUtil.composeWordRelations(word, wordRelationTuples, lexComplexity, displayLang);
		List<Lexeme> lexemes = lexSearchDbService.getLexemes(wordId, dataFilter);
		List<LexemeMeaningTuple> lexemeMeaningTuples = lexSearchDbService.getLexemeMeaningTuples(wordId, dataFilter);
		List<CollocationTuple> collocTuples = lexSearchDbService.getCollocations(wordId, lexComplexity);
		compensateNullWords(wordId, collocTuples);
		conversionUtil.enrich(word, lexemes, lexemeMeaningTuples, collocTuples, dataFilter, displayLang);
		Map<Long, List<Form>> paradigmFormsMap = lexSearchDbService.getWordForms(wordId, maxDisplayLevel);
		List<Paradigm> paradigms = conversionUtil.composeParadigms(word, paradigmFormsMap, displayLang);
		List<String> allImageFiles = new ArrayList<>();
		lexemes.forEach(lexeme -> {
			if (CollectionUtils.isNotEmpty(lexeme.getImageFiles())) {
				allImageFiles.addAll(lexeme.getImageFiles());
			}
		});

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
		combineLevels(wordData.getLexemes());
		return wordData;
	}

	private void compensateNullWords(Long wordId, List<CollocationTuple> collocTuples) {

		for (CollocationTuple tuple : collocTuples) {
			List<TypeCollocMember> collocMembers = tuple.getCollocMembers();
			for (TypeCollocMember collocMem : collocMembers) {
				if (StringUtils.isBlank(collocMem.getWord())) {
					String collocValue = tuple.getCollocValue();
					List<String> collocTokens = Arrays.asList(StringUtils.split(collocValue));
					List<WordForm> wordFormCandidates = lexSearchDbService.getWordFormCandidates(wordId, collocTokens);
					if (CollectionUtils.isEmpty(wordFormCandidates)) {
						tuple.setInvalid(true);
						break;
					}
					WordForm firstAvailableReplacement = wordFormCandidates.get(0);
					collocMem.setWord(firstAvailableReplacement.getWord());
					collocMem.setForm(firstAvailableReplacement.getForm());
				}
			}
		}
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
									&& otherLexeme.getComplexity().equals(lexeme.getComplexity()))
					.count();
			if (nrOfLexemesWithSameLevel1 == 1) {
				levels = String.valueOf(lexeme.getLevel1());
			} else {
				long nrOfLexemesWithSameLevel2 = lexemes.stream()
						.filter(otherLexeme ->
								otherLexeme.getLevel1().equals(lexeme.getLevel1())
										&& otherLexeme.getLevel2().equals(lexeme.getLevel2())
										&& otherLexeme.getComplexity().equals(lexeme.getComplexity()))
						.count();
				if (nrOfLexemesWithSameLevel2 == 1) {
					int level2 = max(lexeme.getLevel2() - 1, 0);
					levels = lexeme.getLevel1() + (level2 == 0 ? "" : "." + level2);
				} else {
					int level3 = max(lexeme.getLevel3() - 1, 0);
					levels = lexeme.getLevel1() + "." + lexeme.getLevel2() + (level3 == 0 ? "" : "." + level3);
				}
			}
			lexeme.setLevels(levels);
		});
	}

	//falling back to dataset-based filtering, not cool at all...
	private DataFilter getDataFilter(String sourceLang, String destinLang, String searchMode) {
		Complexity lexComplexity = null;
		try {
			lexComplexity = Complexity.valueOf(searchMode.toUpperCase());
		} catch (Exception e) {
			//not interested
		}
		String complexityIndex = "";
		if (StringUtils.equals(sourceLang, "est") && StringUtils.equals(destinLang, "est")) {
			complexityIndex = "1";
		} else if (StringUtils.equals(sourceLang, "est") && StringUtils.equals(destinLang, "rus")) {
			complexityIndex = "2";
		} else if (StringUtils.equals(sourceLang, "rus") && StringUtils.equals(destinLang, "est")) {
			complexityIndex = "2";
		}
		Complexity dataComplexity = null;
		try {
			dataComplexity = Complexity.valueOf(lexComplexity.name() + complexityIndex);
		} catch (Exception e) {
			//not interested
		}
		Integer maxDisplayLevel = DEFAULT_MORPHOLOGY_MAX_DISPLAY_LEVEL;
		if (Complexity.SIMPLE.equals(lexComplexity)) {
			maxDisplayLevel = SIMPLE_MORPHOLOGY_MAX_DISPLAY_LEVEL;
		}
		DataFilter dataFilter = new DataFilter(sourceLang, destinLang, lexComplexity, dataComplexity, maxDisplayLevel);
		return dataFilter;
	}
}
