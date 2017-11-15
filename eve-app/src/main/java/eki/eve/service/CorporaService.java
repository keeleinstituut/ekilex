package eki.eve.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.json.JsonParser;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Collections;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Service
public class CorporaService {

	private static final Logger logger = LoggerFactory.getLogger(CorporaService.class);

	@Value("${corpora.service.url:}")
	private String serviceUrl;

	public Map<String, Object> fetch(String sentence) {

		Map<String, Object> response = Collections.emptyMap();
		if (isNotEnabled()) {
			return response;
		}

		URI url= UriComponentsBuilder.fromUriString(serviceUrl)
				.queryParam("command", "query")
				.queryParam("corpus", "COURSEBOOKALL")
				.queryParam("start", 0)
				.queryParam("end", 50)
				.queryParam("cqp", "[word=\"" + sentence + "\"]")
				.build()
				.toUri();
		String responseAsString = doGetRequest(url);
		if (responseAsString != null) {
			logger.debug(responseAsString);
			JsonParser jsonParser = JsonParserFactory.getJsonParser();
			response = jsonParser.parseMap(responseAsString);
		}
		return response;
	}

	private String doGetRequest(URI url) {

		HttpHeaders headers = new HttpHeaders();

		HttpEntity<String> entity = new HttpEntity<>(null, headers);
		RestTemplate restTemplate = new RestTemplate();

		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
		return response == null ? null : response.getBody();
	}

	private boolean isNotEnabled() {
		return isBlank(serviceUrl);
	}

}
