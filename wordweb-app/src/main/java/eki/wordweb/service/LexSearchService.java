package eki.wordweb.service;

import static java.lang.Math.max;

import java.util.ArrayList;
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

import eki.common.constant.FormMode;
import eki.common.constant.TargetContext;
import eki.wordweb.constant.SystemConstant;
import eki.wordweb.data.CollocationTuple;
import eki.wordweb.data.Form;
import eki.wordweb.data.Lexeme;
import eki.wordweb.data.LexemeDetailsTuple;
import eki.wordweb.data.LexemeMeaningTuple;
import eki.wordweb.data.Paradigm;
import eki.wordweb.data.Word;
import eki.wordweb.data.WordData;
import eki.wordweb.data.WordEtymTuple;
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

	private Map<String, String[]> languagesDatasetMap;

	@Override
	public void afterPropertiesSet() {

		languagesDatasetMap = new HashMap<>();
		languagesDatasetMap.put("est-est-detail", new String[] {"ss1", "kol"});
		languagesDatasetMap.put("est-est-simple", new String[] {"psv", "kol"});
		languagesDatasetMap.put("est-rus-detail", new String[] {"ev2"});
		languagesDatasetMap.put("est-rus-simple", new String[] {"qq2"});
		languagesDatasetMap.put("rus-est-detail", new String[] {"ev2"});
		languagesDatasetMap.put("rus-est-simple", new String[] {"qq2"});
	}

	@Transactional
	public WordsData getWords(String searchWord, String sourceLang, String destinLang, Integer homonymNr, String searchMode) {

		String[] datasets = getDatasets(sourceLang, destinLang, searchMode);
		String primaryDataset = datasets[0];
		List<Word> allWords = lexSearchDbService.getWords(searchWord, sourceLang, primaryDataset);
		boolean isForcedSearchMode = false;
		if (CollectionUtils.isEmpty(allWords) && StringUtils.equals(searchMode, SEARCH_MODE_SIMPLE)) {
			datasets = getDatasets(sourceLang, destinLang, SEARCH_MODE_DETAIL);
			primaryDataset = datasets[0];
			allWords = lexSearchDbService.getWords(searchWord, sourceLang, primaryDataset);
			if (CollectionUtils.isNotEmpty(allWords)) {
				searchMode = SEARCH_MODE_DETAIL;
				isForcedSearchMode = true;
			}
		}
		boolean resultsExist = CollectionUtils.isNotEmpty(allWords);
		conversionUtil.setAffixoidFlags(allWords);
		conversionUtil.composeHomonymWrapups(allWords, destinLang, datasets);
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

		String[] datasets = getDatasets(sourceLang, destinLang, SEARCH_MODE_DETAIL);
		String primaryDataset = datasets[0];
		Map<String, List<WordOrForm>> results = lexSearchDbService.getWordsByPrefix(wordPrefix, sourceLang, primaryDataset, limit);
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
		String[] datasets = getDatasets(sourceLang, destinLang, searchMode);
		TargetContext targetContext = null;
		if (StringUtils.equals(SEARCH_MODE_SIMPLE, searchMode)) {
			targetContext = TargetContext.SIMPLE;
		}
		Integer maxDisplayLevel = DEFAULT_MORPHOLOGY_MAX_DISPLAY_LEVEL;
		if (TargetContext.SIMPLE.equals(targetContext)) {
			maxDisplayLevel = SIMPLE_MORPHOLOGY_MAX_DISPLAY_LEVEL;
		}

		// queries and transformations
		Word word = lexSearchDbService.getWord(wordId);
		classifierUtil.applyClassifiers(word, displayLang);
		conversionUtil.setWordTypeFlags(word);
		List<WordEtymTuple> wordEtymTuples = lexSearchDbService.getWordEtymologyTuples(wordId);
		conversionUtil.composeWordEtymology(word, wordEtymTuples, displayLang);
		List<WordRelationTuple> wordRelationTuples = lexSearchDbService.getWordRelationTuples(wordId);
		conversionUtil.composeWordRelations(word, wordRelationTuples, datasets, displayLang);
		List<LexemeDetailsTuple> lexemeDetailsTuples = lexSearchDbService.getLexemeDetailsTuples(wordId, datasets);
		List<LexemeMeaningTuple> lexemeMeaningTuples = lexSearchDbService.getLexemeMeaningTuples(wordId, datasets);
		List<CollocationTuple> collocTuples = lexSearchDbService.getCollocations(wordId, datasets, targetContext);
		List<Lexeme> lexemes = conversionUtil.composeLexemes(word, lexemeDetailsTuples, lexemeMeaningTuples, collocTuples, sourceLang, destinLang, displayLang);
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
		String firstAvailableSoundFile = null;
		boolean isUnknownForm = false;
		if (MapUtils.isNotEmpty(paradigmFormsMap)) {
			Form firstAvailableWordForm = paradigmFormsMap.values().stream()
					.filter(forms -> forms.stream().anyMatch(form -> form.getMode().equals(FormMode.WORD)))
					.map(forms -> forms.stream().filter(form -> form.getMode().equals(FormMode.WORD)).findFirst().orElse(null))
					.findFirst().orElse(null);
			if (firstAvailableWordForm != null) {
				firstAvailableVocalForm = firstAvailableWordForm.getVocalForm();
				firstAvailableSoundFile = firstAvailableWordForm.getSoundFile();
				isUnknownForm = StringUtils.equals(UNKNOWN_FORM_CODE, firstAvailableWordForm.getMorphCode());
			}
		}

		WordData wordData = new WordData();
		wordData.setWord(word);
		wordData.setLexemes(lexemes);
		wordData.setParadigms(paradigms);
		wordData.setImageFiles(allImageFiles);
		wordData.setFirstAvailableVocalForm(firstAvailableVocalForm);
		wordData.setFirstAvailableSoundFile(firstAvailableSoundFile);
		wordData.setUnknownForm(isUnknownForm);
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

	private String[] getDatasets(String sourceLang, String destinLang, String searchMode) {
		String datasetKey = sourceLang + "-" + destinLang + "-" + searchMode;
		String[] datasets = languagesDatasetMap.get(datasetKey);
		return datasets;
	}
}
