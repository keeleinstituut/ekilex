package eki.wordweb.service;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.Complexity;
import eki.common.constant.DatasetType;
import eki.common.constant.FormMode;
import eki.common.service.util.LexemeLevelPreseUtil;
import eki.wordweb.constant.SystemConstant;
import eki.wordweb.constant.WebConstant;
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
import eki.wordweb.service.db.UnifSearchDbService;
import eki.wordweb.service.util.ClassifierUtil;
import eki.wordweb.service.util.CollocConversionUtil;
import eki.wordweb.service.util.EtymConversionUtil;
import eki.wordweb.service.util.LexemeConversionUtil;
import eki.wordweb.service.util.ParadigmConversionUtil;
import eki.wordweb.service.util.WordConversionUtil;

@Component
public class UnifSearchService implements SystemConstant, WebConstant {

	@Autowired
	private UnifSearchDbService unifSearchDbService;

	@Autowired
	private ClassifierUtil classifierUtil;

	@Autowired
	private WordConversionUtil wordConversionUtil;

	@Autowired
	private LexemeConversionUtil lexemeConversionUtil;

	@Autowired
	private CollocConversionUtil collocConversionUtil;

	@Autowired
	private EtymConversionUtil etymConversionUtil;

	@Autowired
	private ParadigmConversionUtil paradigmConversionUtil;

	@Autowired
	private LexemeLevelPreseUtil lexemeLevelPreseUtil;

	@Transactional
	public Map<String, List<String>> getWordsByPrefix(String wordPrefix, String sourceLang, int limit) {

		Map<String, List<WordOrForm>> results = unifSearchDbService.getWordsByPrefix(wordPrefix, limit);
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
	public WordsData getWords(String searchWord, String sourceLang, String destinLang, Integer homonymNr, String searchMode) {

		DataFilter dataFilter = getDataFilter(searchWord, destinLang, searchMode);
		List<Word> allWords = unifSearchDbService.getWords(searchWord, dataFilter);
		boolean resultsExist = CollectionUtils.isNotEmpty(allWords);
		wordConversionUtil.setAffixoidFlags(allWords);
		wordConversionUtil.composeHomonymWrapups(allWords, dataFilter);
		wordConversionUtil.selectHomonym(allWords, homonymNr);
		List<Word> fullMatchWords = allWords.stream()
				.filter(word -> StringUtils.equalsIgnoreCase(word.getWord(), searchWord) || StringUtils.equalsIgnoreCase(word.getAsWord(), searchWord))
				.collect(Collectors.toList());
		if (CollectionUtils.isNotEmpty(fullMatchWords)) {
			List<String> formMatchWords = CollectionUtils.subtract(allWords, fullMatchWords).stream().map(Word::getWord).distinct().collect(Collectors.toList());
			boolean isSingleResult = CollectionUtils.size(fullMatchWords) == 1;
			return new WordsData(fullMatchWords, formMatchWords, searchMode, resultsExist, isSingleResult);
		}
		boolean isSingleResult = CollectionUtils.size(allWords) == 1;
		return new WordsData(allWords, Collections.emptyList(), searchMode, resultsExist, isSingleResult);
	}

	@Transactional
	public WordData getWordData(Long wordId, String sourceLang, String destinLang, String displayLang, String searchMode) {

		// query params
		DataFilter dataFilter = getDataFilter(sourceLang, destinLang, searchMode);
		Integer maxDisplayLevel = dataFilter.getMaxDisplayLevel();
		Complexity lexComplexity = dataFilter.getLexComplexity();

		// word data
		Word word = unifSearchDbService.getWord(wordId);
		String wordLang = word.getLang();
		classifierUtil.applyClassifiers(word, displayLang);
		wordConversionUtil.setWordTypeFlags(word);
		List<WordRelationTuple> wordRelationTuples = unifSearchDbService.getWordRelationTuples(wordId);
		wordConversionUtil.composeWordRelations(word, wordRelationTuples, lexComplexity, displayLang);
		List<WordEtymTuple> wordEtymTuples = unifSearchDbService.getWordEtymologyTuples(wordId);
		etymConversionUtil.composeWordEtymology(word, wordEtymTuples, displayLang);
		Map<Long, List<Form>> paradigmFormsMap = unifSearchDbService.getWordForms(wordId, maxDisplayLevel);
		List<Paradigm> paradigms = paradigmConversionUtil.composeParadigms(word, paradigmFormsMap, displayLang);
		List<String> allRelatedWords = wordConversionUtil.collectAllRelatedWords(word);
		Map<String, Long> langOrderByMap = classifierUtil.getLangOrderByMap();

		// lexeme data
		List<Lexeme> lexemes = unifSearchDbService.getLexemes(wordId, dataFilter);
		List<LexemeMeaningTuple> lexemeMeaningTuples = unifSearchDbService.getLexemeMeaningTuples(wordId, dataFilter);
		Map<DatasetType, List<Lexeme>> lexemeGroups = lexemes.stream().collect(Collectors.groupingBy(Lexeme::getDatasetType));

		// lex conv
		List<Lexeme> lexLexemes = lexemeGroups.get(DatasetType.LEX);
		if (CollectionUtils.isNotEmpty(lexLexemes)) {
			List<CollocationTuple> collocTuples = unifSearchDbService.getCollocations(wordId, lexComplexity);
			compensateNullWords(wordId, collocTuples);
			lexemeConversionUtil.enrich(wordLang, lexemes, lexemeMeaningTuples, allRelatedWords, langOrderByMap, lexComplexity, displayLang);
			collocConversionUtil.enrich(wordId, lexemes, collocTuples, dataFilter, displayLang);
			lexemeLevelPreseUtil.combineLevels(lexLexemes);
		}

		// term conv
		List<Lexeme> termLexemes = lexemeGroups.get(DatasetType.TERM);
		if (CollectionUtils.isNotEmpty(termLexemes)) {
			lexemeConversionUtil.enrich(wordLang, termLexemes, lexemeMeaningTuples, allRelatedWords, langOrderByMap, null, displayLang);
		}

		// resulting flags
		wordConversionUtil.composeCommon(word, lexemes);
		boolean lexResultsExist = CollectionUtils.isNotEmpty(lexemes);
		boolean multipleLexLexemesExist = CollectionUtils.size(lexLexemes) > 1;
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
		wordData.setLexLexemes(lexLexemes);
		wordData.setTermLexemes(termLexemes);
		wordData.setParadigms(paradigms);
		wordData.setFirstAvailableVocalForm(firstAvailableVocalForm);
		wordData.setFirstAvailableAudioFile(firstAvailableAudioFile);
		wordData.setUnknownForm(isUnknownForm);
		wordData.setLexResultsExist(lexResultsExist);
		wordData.setMultipleLexLexemesExist(multipleLexLexemesExist);
		return wordData;
	}

	private void compensateNullWords(Long wordId, List<CollocationTuple> collocTuples) {

		for (CollocationTuple tuple : collocTuples) {
			List<TypeCollocMember> collocMembers = tuple.getCollocMembers();
			for (TypeCollocMember collocMem : collocMembers) {
				if (StringUtils.isBlank(collocMem.getWord())) {
					String collocValue = tuple.getCollocValue();
					List<String> collocTokens = Arrays.asList(StringUtils.split(collocValue));
					List<WordForm> wordFormCandidates = unifSearchDbService.getWordFormCandidates(wordId, collocTokens);
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

	private DataFilter getDataFilter(String sourceLang, String destinLang, String searchMode) {
		Complexity lexComplexity = null;
		try {
			lexComplexity = Complexity.valueOf(searchMode.toUpperCase());
		} catch (Exception e) {
			//not interested
		}
		Integer maxDisplayLevel = DEFAULT_MORPHOLOGY_MAX_DISPLAY_LEVEL;
		if (Complexity.SIMPLE.equals(lexComplexity)) {
			maxDisplayLevel = SIMPLE_MORPHOLOGY_MAX_DISPLAY_LEVEL;
		}
		DataFilter dataFilter = new DataFilter(sourceLang, destinLang, lexComplexity, null, maxDisplayLevel);
		return dataFilter;
	}
}
