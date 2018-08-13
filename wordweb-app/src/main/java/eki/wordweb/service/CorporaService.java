package eki.wordweb.service;

import eki.wordweb.data.CorporaSentence;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.json.JsonParser;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Service
public class CorporaService {

	private static final Logger logger = LoggerFactory.getLogger(CorporaService.class);

	@Value("${corpora.service.url:}")
	private String serviceUrl;

	@Cacheable(value = "corpora")
	public List<CorporaSentence> fetchSentences(String sentence) {

		Map<String, Object> response = fetch(sentence);
		return parseResponse(response);
	}

	private Map<String, Object> fetch(String sentence) {

		Map<String, Object> response = Collections.emptyMap();
		if (isNotEnabled()) {
			return response;
		}

		URI url = UriComponentsBuilder.fromUriString(serviceUrl)
				.queryParam("command", "query")
				.queryParam("corpus", "ETSKELL01,ETSKELL02,ETSKELL03,ETSKELL04,ETSKELL05,ETSKELL06,ETSKELL07,ETSKELL08,ETSKELL09")
				.queryParam("start", 0)
				.queryParam("end", 25)
				.queryParam("cqp", parseSentenceToQueryString(sentence))
				.queryParam("defaultcontext", "1+sentence")
				.queryParam("show", "pos")
				.build()
				.toUri();
		String responseAsString = doGetRequest(url);
		if (responseAsString != null) {
			//logger.debug(responseAsString);
			JsonParser jsonParser = JsonParserFactory.getJsonParser();
			response = jsonParser.parseMap(responseAsString);
		}
		return response;
	}

	private String parseSentenceToQueryString(String sentence) {
		String[] words = StringUtils.split(sentence, " ");
		if (words.length > 1) {
			List<String> items = new ArrayList<>();
			for (String word : words) {
				items.add("[word=\"" + word + "\" %c]");
			}
			return String.join(" ", items);
		} else {
			return "[lemma=\"" + sentence + "\"]";
		}
	}

	private List<CorporaSentence> parseResponse(Map<String, Object> response) {

		List<CorporaSentence> sentences = new ArrayList<>();
		if (response.isEmpty() || (response.containsKey("hits") && (int) response.get("hits") == 0) || !response.containsKey("kwic")) {
			return sentences;
		}
		for (Map<String, Object> kwic : (List<Map<String, Object>>) response.get("kwic")) {
			Map<String, Object> match = (Map<String, Object>) kwic.get("match");
			int startPos = (int) match.get("start");
			int endPos = (int) match.get("end");
			int index = 0;
			CorporaSentence sentence = new CorporaSentence();
			for (Map<String, Object> token : (List<Map<String, Object>>) kwic.get("tokens")) {
				String word = parseWord(token);
				if (index < startPos) {
					sentence.setLeftPart(sentence.getLeftPart() + word);
				} else if (index >= endPos) {
					sentence.setRightPart(sentence.getRightPart() + word);
				} else {
					sentence.setMiddlePart(sentence.getMiddlePart() + word);
				}
				index++;
			}
			sentences.add(sentence);
		}
		return sentences;
	}

	private String parseWord(Map<String, Object> token) {
		String word = (String) token.get("word");
		String pos = (String) token.get("pos");
		word = isPunctuation(pos) ? word : " " + word;
		return word;
	}

	private boolean isPunctuation(String word) {
		return "Z".equals(word);
	}

	private String doGetRequest(URI url) {

		HttpHeaders headers = new HttpHeaders();

		HttpEntity<String> entity = new HttpEntity<>(null, headers);
		RestTemplate restTemplate = new RestTemplate();

		logger.debug("Sending request to > {}", url.toString());
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
		return response.getBody();
	}

	private boolean isNotEnabled() {
		return isBlank(serviceUrl);
	}

}
