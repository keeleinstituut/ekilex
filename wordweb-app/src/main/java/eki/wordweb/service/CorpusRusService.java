package eki.wordweb.service;

import static org.apache.commons.lang3.StringUtils.isBlank;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import eki.wordweb.data.CorpusSentence;

@Component
public class CorpusRusService extends AbstractCorpusService {

	private static final Logger logger = LoggerFactory.getLogger(CorpusRusService.class);

	@Value("${corpus.service.rus.url:}")
	private String serviceUrl;

	@Value("${corpus.service.rus.corpname:}")
	private String corpName;

	@Value("${corpus.service.rus.word.key:}")
	private String wordKey;

	@Value("#{${corpus.service.rus.parameters}}")
	private MultiValueMap<String, String> queryParametersMap;

	@Value("${corpus.service.rus.username:}")
	private String userName;

	@Value("${corpus.service.rus.api.key:}")
	private String apiKey;

	private final boolean isPosQuery = false;

	@Cacheable(value = CACHE_KEY_CORPUS, key = "{#root.methodName, #wordValue}")
	public List<CorpusSentence> getSentences(String wordValue) {

		URI corpusUrl = composeCorpusUrl(wordValue);
		Map<String, Object> response = requestSentences(corpusUrl);
		return parseResponse(response);
	}

	private URI composeCorpusUrl(String wordValue) {

		if (isBlank(serviceUrl) || isBlank(apiKey)) {
			return null;
		}

		String querySentence = parseWordValueToQueryString(wordValue, wordKey, isPosQuery);

		return UriComponentsBuilder.fromUriString(serviceUrl)
				.queryParam("corpname", corpName)
				.queryParam("username", userName)
				.queryParam("api_key", apiKey)
				.queryParam("q", "q" + querySentence)
				.queryParams(queryParametersMap)
				.encode(StandardCharsets.UTF_8)
				.build()
				.toUri();
	}

	private List<CorpusSentence> parseResponse(Map<String, Object> response) {

		List<CorpusSentence> sentences = new ArrayList<>();
		if (response.isEmpty() || response.containsKey("error")) {
			if (response.containsKey("error")) {
				logger.warn("Response returned error : {}", response.get("error"));
			}
			return sentences;
		}
		for (Map<String, Object> line : (List<Map<String, Object>>) response.get("Lines")) {
			CorpusSentence sentence = new CorpusSentence();
			sentence.setLeftPart(composeSentence(line.get("Left")));
			sentence.setMiddlePart(composeSentence(line.get("Kwic")));
			sentence.setRightPart(composeSentence(line.get("Right")));
			sentences.add(sentence);
		}
		return sentences;
	}

	private String composeSentence(Object wordsList) {
		List<Map<String, Object>> listOfWordObjects = (List<Map<String, Object>>) wordsList;
		return listOfWordObjects.stream().map(w -> (String) w.get("str")).reduce("", (a, b) -> a + b);
	}

}
