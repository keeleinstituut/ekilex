package eki.wordweb.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.service.TextDecorationService;
import eki.wordweb.data.ProtoSearchResult;
import eki.wordweb.service.db.ProtoDbService;

@Component
public class ProtoService {

	@Autowired
	protected TextDecorationService textDecorationService;

	@Autowired
	private ProtoDbService protoDbService;

	@Transactional
	public ProtoSearchResult searchByAlgo(String wordCrit, String algo) {

		Long t1, t2;
		t1 = System.currentTimeMillis();
		String wordCritClean = textDecorationService.unifySymbols(wordCrit);
		String wordCritUnaccent = textDecorationService.removeAccents(wordCritClean);
		List<String> words = new ArrayList<>();
		if (StringUtils.equals(algo, "infixlev")) {
			words = protoDbService.searchInfixLevenshteinOrder(wordCrit, wordCritUnaccent);
		} else if (StringUtils.equals(algo, "trigramsim")) {
			words = protoDbService.searchTrigramSimilarity(wordCrit, wordCritUnaccent);
		} else if (StringUtils.equals(algo, "levenshteindist")) {
			words = protoDbService.searchLevenshteinDistance(wordCrit, wordCritUnaccent);
		} else if (StringUtils.equals(algo, "levenshteinlessdist")) {
			words = protoDbService.searchLevenshteinLessDistance(wordCrit, wordCritUnaccent);
		} else if (StringUtils.equals(algo, "metaphonesim")) {
			words = protoDbService.searchMetaphoneMatchSimilarityOrder(wordCritClean, wordCritUnaccent);
		} else if (StringUtils.equals(algo, "daitchmokotoffsim")) {
			words = protoDbService.searchDaitchMokotoffSoundexMatchSimilarityOrder(wordCritClean, wordCritUnaccent);
		}
		words = words.stream().distinct().collect(Collectors.toList());
		t2 = System.currentTimeMillis();
		String logMessage = (t2 - t1) + " ms";

		ProtoSearchResult searchResult = new ProtoSearchResult();
		searchResult.setWords(words);
		searchResult.setLogMessage(logMessage);

		return searchResult;
	}
}
