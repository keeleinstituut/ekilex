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
import eki.common.data.AsWordResult;
import eki.common.service.TextDecorationService;
import eki.common.service.util.LexemeLevelPreseUtil;
import eki.wordweb.constant.SystemConstant;
import eki.wordweb.constant.WebConstant;
import eki.wordweb.data.Form;
import eki.wordweb.data.LanguagesDatasets;
import eki.wordweb.data.LexemeWord;
import eki.wordweb.data.Paradigm;
import eki.wordweb.data.SearchContext;
import eki.wordweb.data.SearchFilter;
import eki.wordweb.data.Word;
import eki.wordweb.data.WordData;
import eki.wordweb.data.WordRelation;
import eki.wordweb.data.WordSearchElement;
import eki.wordweb.data.WordsData;
import eki.wordweb.data.WordsMatch;
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

	public abstract void composeFilteringSuggestions(SearchFilter searchFilter, LanguagesDatasets availableLanguagesDatasets);

	public abstract WordData getWordData(Long wordId, SearchFilter searchFilter);

	@Transactional
	public String getRandomWord() {
		return searchDbService.getRandomWord(LANGUAGE_CODE_EST);
	}

	@Transactional
	public Map<String, List<String>> getWordsByInfixLev(String wordInfix, SearchFilter searchFilter, int limit) {

		SearchContext searchContext = getSearchContext(searchFilter);
		String wordInfixUnaccent = textDecorationService.getValueAsWord(wordInfix);
		Map<String, List<WordSearchElement>> results = searchDbService.getWordsByInfixLev(wordInfix, wordInfixUnaccent, searchContext, limit);
		List<WordSearchElement> wordGroup = results.get(WORD_SEARCH_GROUP_WORD);
		List<WordSearchElement> formGroup = results.get(WORD_SEARCH_GROUP_FORM);
		if (CollectionUtils.isEmpty(wordGroup)) {
			wordGroup = new ArrayList<>();
		}
		if (CollectionUtils.isEmpty(formGroup)) {
			formGroup = new ArrayList<>();
		}
		List<String> prefWords = wordGroup.stream().map(WordSearchElement::getWordValue).distinct().collect(Collectors.toList());
		List<String> formWords = formGroup.stream().map(WordSearchElement::getWordValue).distinct().collect(Collectors.toList());
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
			return new WordsMatch(Collections.emptyList(), false, false, 0);
		}
		if (!StringUtils.containsAny(searchWord, SEARCH_MASK_CHARS, SEARCH_MASK_CHAR)) {
			return new WordsMatch(Collections.emptyList(), false, false, 0);
		}

		SearchContext searchContext = getSearchContext(searchFilter);
		WordsMatch wordsMatch = searchDbService.getWordsWithMask(searchWord, searchContext);

		return wordsMatch;
	}

	@Transactional
	public WordsData getWords(SearchFilter searchFilter) {

		String searchWord = searchFilter.getSearchWord();
		Integer homonymNr = searchFilter.getHomonymNr();
		String lang = searchFilter.getLang();
		AsWordResult asWordResult = textDecorationService.getAsWordResult(searchWord);
		String searchWordUnaccent;
		if (asWordResult.isValueAsWordExists()) {
			searchWordUnaccent = asWordResult.getValueAsWord();
		} else {
			searchWordUnaccent = asWordResult.getValue();
		}
		SearchContext searchContext = getSearchContext(searchFilter);
		List<Word> allWords = searchDbService.getWords(searchWordUnaccent, searchContext, false);
		wordConversionUtil.setWordTypeFlags(allWords);
		wordConversionUtil.composeHomonymWrapups(allWords, searchContext);
		wordConversionUtil.selectHomonymWithLang(allWords, homonymNr, lang);

		List<Word> wordMatchWords = allWords.stream()
				.filter(Word::isWordMatch)
				.collect(Collectors.toList());
		List<String> formMatchWordValues = allWords.stream()
				.filter(Word::isFormMatch)
				.map(Word::getValue)
				.distinct()
				.collect(Collectors.toList());

		boolean wordResultExists = CollectionUtils.isNotEmpty(wordMatchWords);
		boolean formResultExists = CollectionUtils.isNotEmpty(formMatchWordValues);
		boolean wordOrFormResultExists = wordResultExists || formResultExists;
		int wordResultCount = CollectionUtils.size(wordMatchWords);
		boolean isSingleResult = wordResultCount == 1;

		if (wordOrFormResultExists) {
			return new WordsData(wordMatchWords, wordResultExists, isSingleResult, wordResultCount, formMatchWordValues, formResultExists);
		}

		List<String> similarWordValues = searchDbService.getWordValuesByLevenshteinLess(searchWord, searchWordUnaccent, searchContext, ALT_WORDS_DISPLAY_LIMIT);
		boolean altResultExists = CollectionUtils.isNotEmpty(similarWordValues);

		if (altResultExists) {
			return new WordsData(similarWordValues, altResultExists);
		}

		LanguagesDatasets availableLanguagesDatasets = searchDbService.getAvailableLanguagesDatasets(searchWord, searchContext);
		composeFilteringSuggestions(searchFilter, availableLanguagesDatasets);
		String displayLang = languageContext.getDisplayLang();
		classifierUtil.applyClassifiers(availableLanguagesDatasets, displayLang);

		return new WordsData(availableLanguagesDatasets);
	}

	protected WordData composeWordData(
			Word word,
			List<Form> forms,
			List<Paradigm> paradigms,
			List<LexemeWord> lexLexemes,
			List<LexemeWord> termLexemes,
			List<LexemeWord> limTermLexemes,
			SearchContext searchContext) {

		List<String> skellCompatibleLangs = Arrays.asList(LANGUAGE_CODE_ENG, LANGUAGE_CODE_DEU, LANGUAGE_CODE_RUS);
		String headwordValue = word.getValue();
		String headwordLang = word.getLang();
		List<WordRelation> headwordRelatedWords = word.getRelatedWords();
		List<String> destinLangs = searchContext.getDestinLangs();

		boolean lexemesExist = CollectionUtils.isNotEmpty(lexLexemes) || CollectionUtils.isNotEmpty(termLexemes) || CollectionUtils.isNotEmpty(limTermLexemes);
		boolean relevantDataExists = lexemesExist || CollectionUtils.isNotEmpty(headwordRelatedWords);
		boolean multipleLexLexemesExist = CollectionUtils.size(lexLexemes) > 1;
		boolean estHeadword = StringUtils.equals(LANGUAGE_CODE_EST, headwordLang);
		boolean rusHeadword = StringUtils.equals(LANGUAGE_CODE_RUS, headwordLang);
		boolean rusContent = CollectionUtils.isEmpty(destinLangs) || destinLangs.contains(DESTIN_LANG_RUS);
		boolean skellCompatible = skellCompatibleLangs.contains(headwordLang);

		String firstAvailableAudioFile = null;
		boolean audioFileExists = false;
		boolean morphologyExists = false;
		boolean headwordOverflow = false;

		if (CollectionUtils.isNotEmpty(forms)) {
			firstAvailableAudioFile = forms.stream()
					.filter(form -> !form.isQuestionable()
							&& StringUtils.equals(headwordValue, form.getValue())
							&& StringUtils.isNotBlank(form.getAudioFile()))
					.map(Form::getAudioFile)
					.findFirst()
					.orElse(null);
			audioFileExists = StringUtils.isNotBlank(firstAvailableAudioFile);
		}
		if (CollectionUtils.isNotEmpty(paradigms)) {
			morphologyExists = paradigms.stream().anyMatch(paradigm -> StringUtils.isNotBlank(paradigm.getWordClass()));
		}

		if (StringUtils.length(headwordValue) >= WORD_OVERFLOW_LENGTH) {
			int naturalWrapperIndex = StringUtils.indexOfAny(headwordValue, ' ', '-');
			headwordOverflow = (naturalWrapperIndex < 0) || (naturalWrapperIndex >= WORD_OVERFLOW_LENGTH);
		}

		WordData wordData = new WordData();
		wordData.setWord(word);
		wordData.setLexLexemes(lexLexemes);
		wordData.setTermLexemes(termLexemes);
		wordData.setLimTermLexemes(limTermLexemes);
		wordData.setParadigms(paradigms);
		wordData.setFirstAvailableAudioFile(firstAvailableAudioFile);
		wordData.setAudioFileExists(audioFileExists);
		wordData.setMorphologyExists(morphologyExists);
		wordData.setRelevantDataExists(relevantDataExists);
		wordData.setLexemesExist(lexemesExist);
		wordData.setMultipleLexLexemesExist(multipleLexLexemesExist);
		wordData.setEstHeadword(estHeadword);
		wordData.setRusHeadword(rusHeadword);
		wordData.setRusContent(rusContent);
		wordData.setSkellCompatible(skellCompatible);
		wordData.setHeadwordOverflow(headwordOverflow);

		return wordData;
	}
}
