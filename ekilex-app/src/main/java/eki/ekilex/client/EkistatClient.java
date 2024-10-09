package eki.ekilex.client;

import static eki.common.constant.StatType.WW_SEARCH;

import java.util.Collections;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import eki.common.constant.GlobalConstant;

@Component
public class EkistatClient implements GlobalConstant {

	@Value("${ekistat.service.url}")
	private String serviceUrl;

	@Value("${ekistat.service.key}")
	private String serviceKey;

	public Map<String, Integer> getSearchStat(String datasetCode, String lang, String searchMode, String resultsFrom, String resultsUntil) {

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.set(STAT_API_KEY_HEADER_NAME, serviceKey);
		HttpEntity<String> entity = new HttpEntity<>(headers);

		String serviceUriWithParameters = UriComponentsBuilder
				.fromUriString(serviceUrl)
				.queryParam("statType", WW_SEARCH)
				.queryParam("searchMode", searchMode)
				.queryParam("datasetCode", datasetCode)
				.queryParam("lang", lang)
				.queryParam("resultsFrom", resultsFrom)
				.queryParam("resultsUntil", resultsUntil)
				.toUriString();

		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<Map> response = restTemplate.exchange(serviceUriWithParameters, HttpMethod.GET, entity, Map.class);
		return response.getBody();
	}
}
