package eki.wordweb.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import eki.common.constant.GlobalConstant;
import eki.common.service.TextDecorationService;
import eki.common.service.util.LexemeLevelPreseUtil;
import eki.wordweb.constant.SystemConstant;
import eki.wordweb.constant.WebConstant;
import eki.wordweb.data.CollocationTuple;
import eki.wordweb.data.Form;
import eki.wordweb.data.LanguagesDatasets;
import eki.wordweb.data.LexemeWord;
import eki.wordweb.data.Paradigm;
import eki.wordweb.data.SearchContext;
import eki.wordweb.data.SearchFilter;
import eki.wordweb.data.Word;
import eki.wordweb.data.WordData;
import eki.wordweb.data.WordForm;
import eki.wordweb.data.WordSearchElement;
import eki.wordweb.data.WordsData;
import eki.wordweb.data.WordsMatch;
import eki.wordweb.data.type.TypeCollocMember;
import eki.wordweb.service.db.CommonDataDbService;
import eki.wordweb.service.db.SearchDbService;
import eki.wordweb.service.util.ClassifierUtil;
import eki.wordweb.service.util.CollocConversionUtil;
import eki.wordweb.service.util.EtymConversionUtil;
import eki.wordweb.service.util.LanguageContext;
import eki.wordweb.service.util.LexemeConversionUtil;
import eki.wordweb.service.util.ParadigmConversionUtil;
import eki.wordweb.service.util.WordConversionUtil;

public abstract class AbstractSearchService implements SystemConstant, WebConstant, GlobalConstant {

	@Autowired
	protected TextDecorationService textDecorationService;

	@Autowired
	protected SearchDbService searchDbService;

	@Autowired
	protected CommonDataDbService commonDataDbService;

	@Autowired
	protected ClassifierUtil classifierUtil;

	@Autowired
	protected WordConversionUtil wordConversionUtil;

	@Autowired
	protected LexemeConversionUtil lexemeConversionUtil;

	@Autowired
	protected CollocConversionUtil collocConversionUtil;

	@Autowired
	protected EtymConversionUtil etymConversionUtil;

	@Autowired
	protected ParadigmConversionUtil paradigmConversionUtil;

	@Autowired
	protected LexemeLevelPreseUtil lexemeLevelPreseUtil;

	@Autowired
	protected LanguageContext languageContext;

	public abstract SearchContext getSearchContext(SearchFilter searchFilter);

	public abstract WordData getWordData(Long wordId, SearchFilter searchFilter);

	@Transactional
	public String getRandomWord() {
		return searchDbService.getRandomWord(LANGUAGE_CODE_EST);
	}

	@Transactional
	public Map<String, List<String>> getWordsByInfixLev(String wordInfix, SearchFilter searchFilter, int limit) {

		SearchContext searchContext = getSearchContext(searchFilter);
		String wordInfixClean = textDecorationService.unifyToApostrophe(wordInfix);
		String wordInfixUnaccent = textDecorationService.removeAccents(wordInfixClean);
		Map<String, List<WordSearchElement>> results = searchDbService.getWordsByInfixLev(wordInfix, wordInfixUnaccent, searchContext, limit);
		List<WordSearchElement> wordGroup = results.get(WORD_SEARCH_GROUP_WORD);
		List<WordSearchElement> formGroup = results.get(WORD_SEARCH_GROUP_FORM);
		if (CollectionUtils.isEmpty(wordGroup)) {
			wordGroup = new ArrayList<>();
		}
		if (CollectionUtils.isEmpty(formGroup)) {
			formGroup = new ArrayList<>();
		}
		List<String> prefWords = wordGroup.stream().map(WordSearchElement::getWord).distinct().collect(Collectors.toList());
		List<String> formWords = formGroup.stream().map(WordSearchElement::getWord).distinct().collect(Collectors.toList());
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
	public WordsMatch getWordsWithMask(SearchFilter searchFilter) {

		String searchWord = searchFilter.getSearchWord();

		if (StringUtils.isBlank(searchWord)) {
			return new WordsMatch(Collections.emptyList(), 0, false, false);
		}
		if (!StringUtils.containsAny(searchWord, SEARCH_MASK_CHARS, SEARCH_MASK_CHAR)) {
			return new WordsMatch(Collections.emptyList(), 0, false, false);
		}

		SearchContext searchContext = getSearchContext(searchFilter);
		WordsMatch wordsMatch = searchDbService.getWordsWithMask(searchWord, searchContext);

		// TODO add filters recommendatiosn

		return wordsMatch;
	}

	@Transactional
	public WordsData getWords(SearchFilter searchFilter) {

		String searchWord = searchFilter.getSearchWord();
		Integer homonymNr = searchFilter.getHomonymNr();

		SearchContext searchContext = getSearchContext(searchFilter);
		List<Word> allWords = searchDbService.getWords(searchWord, searchContext, false);
		wordConversionUtil.setAffixoidFlags(allWords);
		wordConversionUtil.composeHomonymWrapups(allWords, searchContext);
		wordConversionUtil.selectHomonym(allWords, homonymNr);

		List<Word> wordMatchWords = allWords.stream()
				.filter(Word::isWordMatch)
				.collect(Collectors.toList());
		List<String> formMatchWordValues = allWords.stream()
				.filter(Word::isFormMatch)
				.map(Word::getWord)
				.distinct()
				.collect(Collectors.toList());
		boolean resultsExist = CollectionUtils.isNotEmpty(wordMatchWords);
		int resultCount = CollectionUtils.size(wordMatchWords);
		boolean isSingleResult = resultCount == 1;

		if (resultsExist) {
			return new WordsData(wordMatchWords, formMatchWordValues, resultCount, resultsExist, isSingleResult);
		}

		LanguagesDatasets availableLanguagesDatasets = searchDbService.getAvailableLanguagesDatasets(searchWord, searchContext.getLexComplexity());
		String displayLang = languageContext.getDisplayLang();
		composeFilteringSuggestions(searchFilter, availableLanguagesDatasets);
		classifierUtil.applyClassifiers(availableLanguagesDatasets, displayLang);

		return new WordsData(availableLanguagesDatasets);
	}

	private void composeFilteringSuggestions(SearchFilter searchFilter, LanguagesDatasets availableLanguagesDatasets) {

		List<String> filteringLanguageCodes = searchFilter.getDestinLangs();
		List<String> filteringDatasetCodes = searchFilter.getDatasetCodes();
		List<String> availableLanguageCodes = availableLanguagesDatasets.getLanguageCodes();
		List<String> availableDatasetCodes = availableLanguagesDatasets.getDatasetCodes();

		boolean isFilterAllLangs = filteringLanguageCodes.stream().anyMatch(code -> StringUtils.equals(code, DESTIN_LANG_ALL));
		boolean isFilterAllDatasets = filteringDatasetCodes.stream().anyMatch(code -> StringUtils.equals(code, DATASET_ALL));

		if (isFilterAllLangs) {
			availableLanguageCodes = Collections.emptyList();
		} else if (CollectionUtils.isNotEmpty(availableLanguageCodes)) {
			availableLanguageCodes = availableLanguageCodes.stream().filter(code -> !filteringLanguageCodes.contains(code)).collect(Collectors.toList());
		}
		if (isFilterAllDatasets) {
			availableDatasetCodes = Collections.emptyList();
		} else if (CollectionUtils.isNotEmpty(availableDatasetCodes)) {
			availableDatasetCodes = availableDatasetCodes.stream().filter(code -> !filteringDatasetCodes.contains(code)).collect(Collectors.toList());
		}
		boolean suggestionsExist = CollectionUtils.isNotEmpty(availableLanguageCodes) || CollectionUtils.isNotEmpty(availableDatasetCodes);

		availableLanguagesDatasets.setLanguageCodes(availableLanguageCodes);
		availableLanguagesDatasets.setDatasetCodes(availableDatasetCodes);
		availableLanguagesDatasets.setSuggestionsExist(suggestionsExist);
	}

	protected void compensateNullWords(Long wordId, List<CollocationTuple> collocTuples) {

		for (CollocationTuple tuple : collocTuples) {
			List<TypeCollocMember> collocMembers = tuple.getCollocMembers();
			for (TypeCollocMember collocMem : collocMembers) {
				if (StringUtils.isBlank(collocMem.getWord())) {
					String collocValue = tuple.getCollocValue();
					List<String> collocTokens = Arrays.asList(StringUtils.split(collocValue));
					List<WordForm> wordFormCandidates = searchDbService.getWordFormCandidates(wordId, collocTokens);
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

	protected WordData composeWordData(
			Word word,
			List<Form> forms,
			List<Paradigm> paradigms,
			List<LexemeWord> lexLexemes,
			List<LexemeWord> termLexemes,
			List<LexemeWord> limTermLexemes,
			SearchContext searchContext) {

		List<String> destinLangs = searchContext.getDestinLangs();
		boolean lexemesExist = CollectionUtils.isNotEmpty(lexLexemes) || CollectionUtils.isNotEmpty(termLexemes) || CollectionUtils.isNotEmpty(limTermLexemes);
		boolean relevantDataExists = lexemesExist || CollectionUtils.isNotEmpty(word.getRelatedWords());
		boolean multipleLexLexemesExist = CollectionUtils.size(lexLexemes) > 1;
		String firstAvailableAudioFile = null;
		boolean morphologyExists = false;
		boolean estHeadword = false;
		boolean rusHeadword = false;
		boolean rusContent = false;

		if (CollectionUtils.isNotEmpty(forms)) {
			Form firstAvailableWordForm = forms.stream()
					.filter(form -> !form.isQuestionable() && StringUtils.equals(word.getWord(), form.getValue()))
					.findFirst().orElse(null);
			if (firstAvailableWordForm != null) {
				firstAvailableAudioFile = firstAvailableWordForm.getAudioFile();
			}
		}
		if (CollectionUtils.isNotEmpty(paradigms)) {
			morphologyExists = paradigms.stream().anyMatch(paradigm -> StringUtils.isNotBlank(paradigm.getWordClass()));
		}
		estHeadword = StringUtils.equals(LANGUAGE_CODE_EST, word.getLang());
		rusHeadword = StringUtils.equals(LANGUAGE_CODE_RUS, word.getLang());
		rusContent = CollectionUtils.isEmpty(destinLangs) || destinLangs.contains(DESTIN_LANG_RUS);

		WordData wordData = new WordData();
		wordData.setWord(word);
		wordData.setLexLexemes(lexLexemes);
		wordData.setTermLexemes(termLexemes);
		wordData.setLimTermLexemes(limTermLexemes);
		wordData.setParadigms(paradigms);
		wordData.setFirstAvailableAudioFile(firstAvailableAudioFile);
		wordData.setMorphologyExists(morphologyExists);
		wordData.setRelevantDataExists(relevantDataExists);
		wordData.setLexemesExist(lexemesExist);
		wordData.setMultipleLexLexemesExist(multipleLexLexemesExist);
		wordData.setEstHeadword(estHeadword);
		wordData.setRusHeadword(rusHeadword);
		wordData.setRusContent(rusContent);

		return wordData;
	}
}
