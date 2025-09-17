package eki.wordweb.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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
		List<OsWord> words;
		OsWord selectedWord = null;

		words = osDbService.getWords(searchValue, fiCollationExists);

		if (CollectionUtils.isEmpty(words)) {
			words = osDbService.getRelatedWords(searchValue, fiCollationExists);
		}

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
		Map<String, List<String>> searchResultCandidates = new HashMap<>();
		applySearchGroup(WORD_SEARCH_GROUP_WORD, results, "infixWords", searchResultCandidates);
		applySearchGroup(WORD_SEARCH_GROUP_WORD_RELATION, results, "infixWordRelations", searchResultCandidates);

		return searchResultCandidates;
	}

	private void applySearchGroup(
			String searchGroupName,
			Map<String, List<WordSearchElement>> searchGroupMap,
			String resultGroupName,
			Map<String, List<String>> resultsMap) {

		List<WordSearchElement> searchGroup;
		List<String> searchWords;
		searchGroup = searchGroupMap.get(searchGroupName);
		if (CollectionUtils.isNotEmpty(searchGroup)) {
			searchWords = searchGroup.stream()
					.map(WordSearchElement::getWordValue)
					.distinct()
					.collect(Collectors.toList());
			resultsMap.put(resultGroupName, searchWords);
		}
	}
}
