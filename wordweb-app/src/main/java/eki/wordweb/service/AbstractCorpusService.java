package eki.wordweb.service;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.json.JsonParser;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import eki.common.constant.GlobalConstant;
import eki.wordweb.constant.SystemConstant;

public abstract class AbstractCorpusService implements SystemConstant, GlobalConstant {

	private static final Logger logger = LoggerFactory.getLogger(AbstractCorpusService.class);

	protected Map<String, Object> requestSentences(URI corpusUrl) {

		Map<String, Object> response = Collections.emptyMap();
		if (isNotEnabled(corpusUrl)) {
			return response;
		}

		String responseAsString = doGetRequest(corpusUrl);
		if (responseAsString != null) {
			JsonParser jsonParser = JsonParserFactory.getJsonParser();
			response = jsonParser.parseMap(responseAsString);
		}
		return response;
	}

	protected String parseWordValueToQueryString(String wordValue, String wordQueryKey, boolean isPosQuery) {

		String[] words = StringUtils.split(wordValue, " ");
		List<String> wordQuerys = new ArrayList<>();
		for (String word : words) {
			String wordQuery = createWordQueryString(word, wordQueryKey, isPosQuery);
			wordQuerys.add(wordQuery);
		}
		String wordsQueryString = String.join(" ", wordQuerys);
		return wordsQueryString;
	}

	private String createWordQueryString(String word, String wordQueryKey, boolean isPosQuery) {

		if (isPosQuery) {
			return "[" + wordQueryKey + "=\"" + word + "-.?\"]";
		} else {
			return "[" + wordQueryKey + "=\"" + word + "\"]";
		}
	}

	private String doGetRequest(URI url) {

		HttpHeaders headers = new HttpHeaders();

		HttpEntity<String> entity = new HttpEntity<>(null, headers);
		RestTemplate restTemplate = new RestTemplate();

		logger.debug("Sending request to > {}", url.toString());
		try {
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
			return response.getBody();
		} catch (Exception e) {
			logger.error("Error with requesting {}", url);
			logger.error(e.getMessage());
			return null;
		}
	}

	private boolean isNotEnabled(URI corpusUrl) {
		return corpusUrl == null;
	}

}
