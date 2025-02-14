package eki.ekilex.client;

import static eki.common.constant.StatType.WW_SEARCH;

import java.util.Collections;

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
import eki.common.data.StatSearchFilter;
import eki.common.data.StatSearchResult;

@Component
public class EkistatClient implements GlobalConstant {

	private static final String STAT_SEARCH_URI = "/search";

	@Value("${ekistat.service.url}")
	private String serviceUrl;

	@Value("${ekistat.service.key}")
	private String serviceKey;

	public StatSearchResult getStatSearchResult(StatSearchFilter searchFilter) {

		String statSearchUrl = serviceUrl + STAT_SEARCH_URI;
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.set(STAT_API_KEY_HEADER_NAME, serviceKey);
		HttpEntity<String> entity = new HttpEntity<>(headers);

		String serviceUriWithParameters = UriComponentsBuilder
				.fromUriString(statSearchUrl)
				.queryParam("statType", WW_SEARCH)
				.queryParam("searchMode", searchFilter.getSearchMode())
				.queryParam("datasetCode", searchFilter.getDatasetCode())
				.queryParam("searchLang", searchFilter.getSearchLang())
				.queryParam("dateFrom", searchFilter.getDateFrom())
				.queryParam("dateUntil", searchFilter.getDateUntil())
				.queryParam("trustworthyOnly", searchFilter.isTrustworthyOnly())
				.toUriString();

		RestTemplate restTemplate = new RestTemplate();

		ResponseEntity<StatSearchResult> response = restTemplate.exchange(serviceUriWithParameters, HttpMethod.GET, entity, StatSearchResult.class);
		StatSearchResult searchResult = response.getBody();

		return searchResult;
	}
}
