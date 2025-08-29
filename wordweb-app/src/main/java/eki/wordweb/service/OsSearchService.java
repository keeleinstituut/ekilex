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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.GlobalConstant;
import eki.common.service.TextDecorationService;
import eki.wordweb.constant.SystemConstant;
import eki.wordweb.data.WordSearchElement;
import eki.wordweb.data.WordsMatch;
import eki.wordweb.data.os.OsSearchResult;
import eki.wordweb.data.os.OsWord;
import eki.wordweb.service.db.OsDbService;
import eki.wordweb.service.util.ClassifierUtil;
import eki.wordweb.service.util.LanguageContext;
import eki.wordweb.service.util.OsConversionUtil;

@Component
public class OsSearchService implements GlobalConstant, SystemConstant {

	@Autowired
	private OsDbService osDbService;

	@Autowired
	private TextDecorationService textDecorationService;

	@Autowired
	private OsConversionUtil osConversionUtil;

	@Autowired
	private ClassifierUtil classifierUtil;

	@Autowired
	private LanguageContext languageContext;

	@Transactional
	public OsSearchResult search(String searchValue, Integer selectedHomonymNr) {

		String displayLang = languageContext.getDisplayLang();
		boolean fiCollationExists = osDbService.fiCollationExists();
		List<OsWord> words = osDbService.getWords(searchValue, fiCollationExists);
		OsWord selectedWord = null;

		if (CollectionUtils.isNotEmpty(words)) {
			osConversionUtil.applyGenericConversions(words, searchValue, selectedHomonymNr);
			Long wordId = osConversionUtil.getSelectedWordId(words);
			selectedWord = osDbService.getWord(wordId);
			classifierUtil.applyOsClassifiers(selectedWord, displayLang);
			osConversionUtil.applyWordRelationConversions(selectedWord);
			osConversionUtil.setWordTypeFlags(selectedWord);
			osConversionUtil.setContentExistsFlags(selectedWord);
		}

		int resultCount = CollectionUtils.size(words);
		boolean resultExists = CollectionUtils.isNotEmpty(words);
		boolean singleResult = resultCount == 1;

		OsSearchResult searchResult = new OsSearchResult(words, selectedWord, resultExists, singleResult, resultCount);

		return searchResult;
	}

	@Transactional
	public WordsMatch getWordsWithMask(String searchValue) {

		if (StringUtils.isBlank(searchValue)) {
			return new WordsMatch(Collections.emptyList(), false, false, 0);
		}
		if (!StringUtils.containsAny(searchValue, SEARCH_MASK_CHARS, SEARCH_MASK_CHAR)) {
			return new WordsMatch(Collections.emptyList(), false, false, 0);
		}
		return osDbService.getWordsWithMask(searchValue);
	}

	@Transactional
	public Map<String, List<String>> getWordsByInfixLev(String wordInfix, int limit) {

		String wordInfixUnaccent = textDecorationService.getValueAsWord(wordInfix);
		Map<String, List<WordSearchElement>> results = osDbService.getWordsByInfixLev(wordInfix, wordInfixUnaccent, limit);
		List<WordSearchElement> wordGroup = results.get(WORD_SEARCH_GROUP_WORD);
		if (CollectionUtils.isEmpty(wordGroup)) {
			wordGroup = new ArrayList<>();
		}
		List<String> infixWords = wordGroup.stream().map(WordSearchElement::getWordValue).distinct().collect(Collectors.toList());
		Map<String, List<String>> searchResultCandidates = new HashMap<>();
		searchResultCandidates.put("infixWords", infixWords);
		return searchResultCandidates;
	}
}
