package eki.wordweb.service;

import static org.apache.commons.lang3.StringUtils.isBlank;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import eki.wordweb.data.CorpusTranslation;

@Component
public class CorpusTranslationService extends AbstractRemoteRequestService {

	@Value("${corpus.service.trans.url:}")
	private String serviceUrl;

	@Value("${corpus.service.trans.estrus.corpname:}")
	private String corpNameEstRus;

	private static final int RESULTS_LIMIT = 39;

	@Cacheable(value = CACHE_KEY_CORPUS, key = "{#root.methodName, #wordId, #wordValue, #wordLang}")
	public List<CorpusTranslation> getCorpusTranslations(Long wordId, String wordValue, String wordLang) {

		URI corpusUrl = composeCorpusUri(wordId, wordValue, wordLang);
		Map<String, Object> response = requestGet(corpusUrl);
		List<CorpusTranslation> translations = parseResponse(response);
		return translations;
	}

	private URI composeCorpusUri(Long wordId, String wordValue, String wordLang) {

		if (isBlank(serviceUrl)) {
			return null;
		}

		try {
			return UriComponentsBuilder
					.fromUriString(serviceUrl)
					.queryParam("corpus", corpNameEstRus)
					.queryParam("wordId", wordId)
					.queryParam("word", wordValue)
					.queryParam("lang", wordLang)
					.queryParam("limit", RESULTS_LIMIT)
					.encode(StandardCharsets.UTF_8)
					.build()
					.toUri();
		} catch (Exception e) {
			// probably some hacky input anyway
			return null;
		}
	}

	private List<CorpusTranslation> parseResponse(Map<String, Object> response) {

		List<CorpusTranslation> translations = new ArrayList<>();
		if (MapUtils.isEmpty(response)) {
			return translations;
		}

		@SuppressWarnings("unchecked")
		List<Map<String, Object>> examples = (List<Map<String, Object>>) response.get("examples");

		for (Map<String, Object> example : examples) {
			String sourceLangSentence = (String) example.get("source");
			String targetLangSentence = (String) example.get("target");
			translations.add(new CorpusTranslation(sourceLangSentence, targetLangSentence));
		}
		return translations;
	}

}
