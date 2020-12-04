package eki.wordweb.service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import eki.common.constant.GlobalConstant;
import eki.common.data.Count;
import eki.common.data.SearchStat;
import eki.wordweb.data.SearchValidation;
import eki.wordweb.data.WordsData;

@Component
public class StatDataCollector implements GlobalConstant {

	// TODO send exceptions to service and remove old logic - yogesh

	private static Logger logger = LoggerFactory.getLogger(StatDataCollector.class);

	private static final String TOTAL_SEARCH_COUNT_KEY = "*";

	private String startTime;

	private Map<String, Count> searchCountMap;

	private Map<String, Count> exceptionCountMap;

	@Value("${stat.service.enabled:false}")
	private boolean serviceEnabled;

	@Value("${stat.service.url}")
	private String serviceUrl;

	@Value("${stat.service.key}")
	private String serviceKey;

	public StatDataCollector() {
		DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
		this.startTime = dateFormat.format(new Date());
		this.searchCountMap = new HashMap<>();
		this.exceptionCountMap = new HashMap<>();
	}

	public synchronized void addSearchStat(List<String> destinLangs, String searchMode, boolean resultsExist, boolean isIeUser) {
		if (CollectionUtils.isEmpty(destinLangs)) {
			return;
		}
		if (StringUtils.isBlank(searchMode)) {
			return;
		}
		String searchCountKey = composeSearchCountKey(destinLangs, searchMode, resultsExist, isIeUser);
		Count langSearchCount = searchCountMap.get(searchCountKey);
		if (langSearchCount == null) {
			langSearchCount = new Count();
			searchCountMap.put(searchCountKey, langSearchCount);
		}
		langSearchCount.increment();
		Count totalSearchCount = searchCountMap.get(TOTAL_SEARCH_COUNT_KEY);
		if (totalSearchCount == null) {
			totalSearchCount = new Count();
			searchCountMap.put(TOTAL_SEARCH_COUNT_KEY, totalSearchCount);
		}
		totalSearchCount.increment();
	}

	private String composeSearchCountKey(List<String> destinLangs, String searchMode, boolean resultsExist, boolean isIeUser) {
		StringBuffer keyBuf = new StringBuffer();
		keyBuf.append(destinLangs.toString());
		keyBuf.append(' ');
		keyBuf.append(searchMode.toLowerCase());
		keyBuf.append(' ');
		if (resultsExist) {
			keyBuf.append("pos");
		} else {
			keyBuf.append("neg");
		}
		if (isIeUser) {
			keyBuf.append(' ');
			keyBuf.append("ie");
		}
		return keyBuf.toString();
	}

	public synchronized void addExceptionStat(Throwable exception) {
		if (exception == null) {
			return;
		}
		String exceptionName = exception.getClass().getName();
		Count exceptionCount = exceptionCountMap.get(exceptionName);
		if (exceptionCount == null) {
			exceptionCount = new Count();
			exceptionCountMap.put(exceptionName, exceptionCount);
		}
		exceptionCount.increment();
	}

	public String getStartTime() {
		return startTime;
	}

	public Map<String, Count> getSearchCountMap() {
		return searchCountMap;
	}

	public Map<String, Count> getExceptionCountMap() {
		return exceptionCountMap;
	}

	@Async
	public void postSearchStat(SearchValidation searchValidation, WordsData wordsData, String searchMode) {

		if (!serviceEnabled) {
			return;
		}

		String searchWord = searchValidation.getSearchWord();
		Integer homonymNr = searchValidation.getHomonymNr();
		List<String> destinLangs = searchValidation.getDestinLangs();
		List<String> datasetCodes = searchValidation.getDatasetCodes();
		String searchUri = searchValidation.getSearchUri();
		int resultCount = wordsData.getResultCount();
		boolean resultsExist = wordsData.isResultsExist();
		boolean isSingleResult = wordsData.isSingleResult();

		SearchStat searchStat = new SearchStat();
		searchStat.setSearchWord(searchWord);
		searchStat.setHomonymNr(homonymNr);
		searchStat.setSearchMode(searchMode);
		searchStat.setDestinLangs(destinLangs);
		searchStat.setDatasetCodes(datasetCodes);
		searchStat.setSearchUri(searchUri);
		searchStat.setResultCount(resultCount);
		searchStat.setResultsExist(resultsExist);
		searchStat.setSingleResult(isSingleResult);
		// TODO time

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.set(STAT_API_KEY_HEADER_NAME, serviceKey);

		RestTemplate restTemplate = new RestTemplate();

		HttpEntity<SearchStat> searchStatEntity = new HttpEntity<>(searchStat, headers);
		try {
			restTemplate.postForObject(serviceUrl, searchStatEntity, String.class);
		} catch (RestClientException e) {
			logger.error("posting search stat data failed: {}", e.getMessage());
		}
	}

}
