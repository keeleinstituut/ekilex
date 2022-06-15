package eki.wordweb.service;

import static org.apache.commons.lang3.StringUtils.isBlank;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import eki.wordweb.data.CorpusSentence;

@Component
public class CorpusEstService extends AbstractCorpusService {

	@Value("${corpus.service.est.url:}")
	private String serviceUrl;

	@Value("${corpus.service.est.corpname.detail:}")
	private String corpNameDetail;

	@Value("${corpus.service.est.corpname.simple:}")
	private String corpNameSimple;

	@Value("${corpus.service.est.word.key.detail:}")
	private String wordKeyDetail;

	@Value("${corpus.service.est.word.key.simple:}")
	private String wordKeySimple;

	@Value("#{${corpus.service.est.parameters}}")
	private MultiValueMap<String, String> queryParametersMap;

	private final String[] POS_PUNCTUATIONS = new String[] {"Z", "_Z_"};

	private final String QUOTATION_MARK = "\"";

	@Cacheable(value = CACHE_KEY_CORPUS, key = "{#root.methodName, #wordValue, #searchMode}")
	public List<CorpusSentence> getSentences(String wordValue, String searchMode) {

		URI corpusUrl = composeCorpusUrl(wordValue, searchMode);
		Map<String, Object> response = requestSentences(corpusUrl);
		return parseResponse(response);
	}

	private URI composeCorpusUrl(String wordValue, String searchMode) {

		if (isBlank(serviceUrl)) {
			return null;
		}

		boolean isPosQuery = false;
		String corpName = null;
		String wordKey = null;
		if (StringUtils.equals(searchMode, SEARCH_MODE_DETAIL)) {
			isPosQuery = true;
			corpName = corpNameDetail;
			wordKey = wordKeyDetail;
		} else if (StringUtils.equals(searchMode, SEARCH_MODE_SIMPLE)) {
			corpName = corpNameSimple;
			wordKey = wordKeySimple;
		}

		String querySentence = parseWordValueToQueryString(wordValue, wordKey, isPosQuery);

		return UriComponentsBuilder.fromUriString(serviceUrl)
				.queryParam("corpus", corpName)
				.queryParam("cqp", querySentence)
				.queryParams(queryParametersMap)
				.encode(StandardCharsets.UTF_8)
				.build()
				.toUri();
	}

	private List<CorpusSentence> parseResponse(Map<String, Object> response) {

		List<CorpusSentence> sentences = new ArrayList<>();
		if (MapUtils.isEmpty(response)) {
			return sentences;
		}
		if (response.containsKey("hits") && (int) response.get("hits") == 0) {
			return sentences;
		}
		if (!response.containsKey("kwic")) {
			return sentences;
		}
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> kwics = (List<Map<String, Object>>) response.get("kwic");
		for (Map<String, Object> kwic : kwics) {
			@SuppressWarnings("unchecked")
			Map<String, Object> match = (Map<String, Object>) kwic.get("match");
			int middlePartStartPos = (int) match.get("start");
			int middlePartEndPos = (int) match.get("end");
			int currentWordPos = 0;
			boolean skipSpaceBeforeWord = false;
			CorpusSentence sentence = new CorpusSentence();
			@SuppressWarnings("unchecked")
			List<Map<String, Object>> tokens = (List<Map<String, Object>>) kwic.get("tokens");
			for (Map<String, Object> token : tokens) {
				String word = parseWord(token, skipSpaceBeforeWord);
				if (currentWordPos < middlePartStartPos) {
					sentence.setLeftPart(sentence.getLeftPart() + word);
				} else if (currentWordPos >= middlePartEndPos) {
					sentence.setRightPart(sentence.getRightPart() + word);
				} else {
					sentence.setMiddlePart(sentence.getMiddlePart() + word);
				}
				if (currentWordPos == 0 || skipSpaceBeforeWord) {
					skipSpaceBeforeWord = StringUtils.equals(QUOTATION_MARK, word);
				}
				currentWordPos++;
			}
			sentences.add(sentence);
		}
		return sentences;
	}

	private String parseWord(Map<String, Object> token, boolean skipSpaceBeforeWord) {

		String word = (String) token.get("word");
		String pos = (String) token.get("pos");
		boolean isPunctuation = ArrayUtils.contains(POS_PUNCTUATIONS, pos);
		if (isPunctuation || skipSpaceBeforeWord) {
			return word;
		} else {
			return " " + word;
		}
	}

}
