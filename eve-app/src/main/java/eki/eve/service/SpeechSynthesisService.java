package eki.eve.service;

import eki.eve.data.Word;
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

@Service
public class SpeechSynthesisService {

	public String urlToSoundSource(Word word) {
		if (!"est".equals(word.getLanguage())) {
			return null;
		}
		URI url= UriComponentsBuilder.fromUriString("http://heliraamat.eki.ee/syntees/koduleht.php")
				.queryParam("haal", 15)
				.queryParam("tekst", word.getValue())
				.build()
				.toUri();
		String responseAsString = doGetRequest(url);
		Map<String, Object> response = Collections.emptyMap();
		if (responseAsString != null) {
			JsonParser jsonParser = JsonParserFactory.getJsonParser();
			response = jsonParser.parseMap(responseAsString);
		}
		return response.containsKey("mp3url") ? (String) response.get("mp3url") : null;
	}

	private String doGetRequest(URI url) {
		HttpHeaders headers = new HttpHeaders();

		HttpEntity<String> entity = new HttpEntity<>(null, headers);
		RestTemplate restTemplate = new RestTemplate();

		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
		return response == null ? null : response.getBody();
	}

}
