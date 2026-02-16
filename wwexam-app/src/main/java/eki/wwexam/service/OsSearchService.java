package eki.wwexam.service;

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
import eki.wwexam.constant.SystemConstant;
import eki.wwexam.data.WordSearchElement;
import eki.wwexam.data.WordsMatch;
import eki.wwexam.data.os.OsSearchResult;
import eki.wwexam.data.os.OsWord;
import eki.wwexam.service.db.OsDbService;
import eki.wwexam.service.util.OsConversionUtil;

@Component
public class OsSearchService implements GlobalConstant, SystemConstant {

	@Autowired
	private OsDbService osDbService;

	@Autowired
	private TextDecorationService textDecorationService;

	@Autowired
	private OsConversionUtil osConversionUtil;

	@Transactional
	public OsSearchResult search(String searchValue, Integer selectedHomonymNr) {

		boolean fiCollationExists = osDbService.fiCollationExists();
		boolean isCompoundSearch = false;

		List<OsWord> words = osDbService.getWords(searchValue, fiCollationExists);

		if (CollectionUtils.isEmpty(words)) {
			words = osDbService.getRelatedWords(searchValue);
			isCompoundSearch = CollectionUtils.isNotEmpty(words);
		}

		if (CollectionUtils.isNotEmpty(words)) {
			osConversionUtil.applyAllConversions(words);
			if (isCompoundSearch) {
				osConversionUtil.makeCompoundSearchSelection(words);
			} else {
				osConversionUtil.makeHomonymSearchSelection(words, searchValue, selectedHomonymNr);
			}
		}

		boolean isHomonymSearch = !isCompoundSearch;
		int resultCount = CollectionUtils.size(words);
		boolean resultExists = CollectionUtils.isNotEmpty(words);
		boolean singleResult = resultCount == 1;

		OsSearchResult searchResult = new OsSearchResult(
				words, isHomonymSearch, isCompoundSearch, resultExists, singleResult, resultCount);

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
		applySearchGroup(WORD_SEARCH_GROUP_WORD_REL_VALUE, results, "wordRelValues", searchResultCandidates);
		applySearchGroup(WORD_SEARCH_GROUP_WORD_REL_COMP, results, "wordRelComponents", searchResultCandidates);

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
