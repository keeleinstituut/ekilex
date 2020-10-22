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
import org.springframework.web.util.UriComponentsBuilder;

import eki.wordweb.data.CorporaSentence;

@Component
public class CorporaServiceRus extends AbstractCorporaService {

	private static final Logger logger = LoggerFactory.getLogger(CorporaServiceRus.class);

	@Value("${corpora.service.rus.url:}")
	private String serviceUrl;

	@Value("${corpora.service.rus.corpname:}")
	private String corpName;

	@Value("${corpora.service.rus.username:}")
	private String userName;

	@Value("${corpora.service.rus.api.key:}")
	private String apiKey;

	private final String WORD_KEY = "word";

	@Cacheable(value = CACHE_KEY_CORPORA)
	public List<CorporaSentence> getSentences(String sentence) {

		URI corporaUrl = composeCorporaUrl(sentence);
		Map<String, Object> response = requestSentences(corporaUrl);
		return parseResponse(response);
	}

	private URI composeCorporaUrl(String sentence) {

		if (isBlank(serviceUrl) || isBlank(apiKey)) {
			return null;
		}

		String querySentence = parseSentenceToQueryString(sentence, WORD_KEY);
		return UriComponentsBuilder.fromUriString(serviceUrl)
				.queryParam("corpname", corpName)
				.queryParam("username", userName)
				.queryParam("api_key", apiKey)
				.queryParam("format", "json")
				.queryParam("viewmode", "sen")
				.queryParam("async", "0")
				.queryParam("pagesize", "15")
				.queryParam("q", "q" + querySentence)
				.encode(StandardCharsets.UTF_8)
				.build()
				.toUri();

	}

	private List<CorporaSentence> parseResponse(Map<String, Object> response) {

		List<CorporaSentence> sentences = new ArrayList<>();
		if (response.isEmpty() || response.containsKey("error")) {
			if (response.containsKey("error")) {
				logger.warn("Response returned error : {}", response.get("error"));
			}
			return sentences;
		}
		for (Map<String, Object> line : (List<Map<String, Object>>) response.get("Lines")) {
			CorporaSentence sentence = new CorporaSentence();
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
