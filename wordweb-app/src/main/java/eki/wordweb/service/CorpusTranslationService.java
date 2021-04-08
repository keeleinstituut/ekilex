package eki.wordweb.service;

import static org.apache.commons.lang3.StringUtils.isBlank;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class CorpusTranslationService extends AbstractCorpusService {

	@Value("${corpus.service.trans.url:}")
	private String serviceUrl;

	@Value("${corpus.service.trans.estrus.corpname:}")
	private String corpNameEstRus;

	private static final int RESULTS_LIMIT = 39;

	@Cacheable(value = CACHE_KEY_CORPUS, key = "{#root.methodName, #wordId, #wordLang, #wordValue}")
	public Map<String, String> getSentences(Long wordId, String wordLang, String wordValue) {

		URI corpusUrl = composeCorpusUrl(wordId, wordLang, wordValue);
		Map<String, Object> response = requestSentences(corpusUrl);
		Map<String, String> sentences = parseResponse(response);
		return sentences;
	}

	private URI composeCorpusUrl(Long wordId, String wordLang, String wordValue) {

		if (isBlank(serviceUrl)) {
			return null;
		}

		return UriComponentsBuilder.fromUriString(serviceUrl)
				.queryParam("corpus", corpNameEstRus)
				.queryParam("wordId", wordId)
				.queryParam("word", wordValue)
				.queryParam("lang", wordLang)
				.queryParam("limit", RESULTS_LIMIT)
				.encode(StandardCharsets.UTF_8)
				.build()
				.toUri();
	}

	private Map<String, String> parseResponse(Map<String, Object> response) {

		Map<String, String> sentences = new HashMap<>();
		if (response.isEmpty()) {
			return sentences;
		}

		for (Map<String, Object> examples : (List<Map<String, Object>>) response.get("examples")) {
			String sourceLangSentence = (String) examples.get("source");
			String targetLangSentence = (String) examples.get("target");
			sentences.put(sourceLangSentence, targetLangSentence);
		}
		return sentences;
	}

}
