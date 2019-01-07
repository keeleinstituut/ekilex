package eki.wordweb.service;

import eki.wordweb.data.CorporaSentence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Service
public class CorporaServiceRus extends CorporaService {

	private static final Logger logger = LoggerFactory.getLogger(CorporaServiceRus.class);

	@Value("${corpora.service.rus.url:}")
	private String serviceUrl;

	@Value("${corpora.service.rus.corpname:}")
	private String corpName;

	@Value("${corpora.service.rus.username:}")
	private String userName;

	@Value("${corpora.service.rus.api.key:}")
	private String apiKey;

	protected URI composeCorporaUri(String sentence) {

		if (isBlank(serviceUrl) || isBlank(apiKey)) {
			return null;
		} else {
			return UriComponentsBuilder.fromUriString(serviceUrl)
				.queryParam("corpname", corpName)
				.queryParam("username", userName)
				.queryParam("api_key", apiKey)
				.queryParam("format", "json")
				.queryParam("viewmode", "sentence")
				.queryParam("async", "0")
				.queryParam("pagesize", "15")
				.queryParam("q", "q"+parseSentenceToQueryString(sentence))
				.build()
				.toUri();
		}
	}

	protected List<CorporaSentence> parseResponse(Map<String, Object> response) {

		List<CorporaSentence> sentences = new ArrayList<>();
		if (response.isEmpty() || response.containsKey("error")) {
			if (response.containsKey("error")) {
				logger.debug("Response returned error : {}", response.get("error"));
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
		return listOfWordObjects.stream().map(w -> (String )w.get("str")).reduce("", (a, b) -> a + b);
	}

}
