package eki.wordweb.service;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.jooq.tools.StringUtils;
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
import eki.common.constant.RequestOrigin;
import eki.common.data.ExceptionStat;
import eki.common.data.SearchStat;
import eki.wordweb.data.SearchValidation;
import eki.wordweb.data.WordsData;

@Component
public class StatDataCollector implements GlobalConstant {

	private static Logger logger = LoggerFactory.getLogger(StatDataCollector.class);

	@Value("${ekistat.service.enabled:false}")
	private boolean serviceEnabled;

	@Value("${ekistat.service.url}")
	private String serviceUrl;

	@Value("${ekistat.service.key}")
	private String serviceKey;

	private static final String EXCEPTION_URI = "/exception";

	private static final String SEARCH_URI = "/search";

	@Async
	public void postExceptionStat(Throwable exception) {

		if (!serviceEnabled || exception == null) {
			return;
		}

		String exceptionName = exception.getClass().getName();
		String exceptionMessage = exception.getMessage();
		ExceptionStat exceptionStat = new ExceptionStat(exceptionName, exceptionMessage);

		HttpHeaders headers = createHeaders();
		RestTemplate restTemplate = new RestTemplate();
		HttpEntity<ExceptionStat> exceptionStatEntity = new HttpEntity<>(exceptionStat, headers);
		try {
			restTemplate.postForObject(serviceUrl + EXCEPTION_URI, exceptionStatEntity, String.class);
		} catch (RestClientException e) {
			logger.error("posting exception stat data failed: {}", e.getMessage());
		}
	}

	@Async
	public void postSearchStat(SearchValidation searchValidation, WordsData wordsData, HttpServletRequest request, boolean isSearchForm, String searchMode) throws Exception {

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

		String sessionId = request.getSession().getId();
		String userAgent = request.getHeader("User-Agent");
		String referrerDomain = null;
		if (request.getHeader("referer") != null) {
			referrerDomain = new URI(request.getHeader("referer")).getHost();
		}
		String serverDomain = request.getServerName();

		RequestOrigin requestOrigin;
		if (isSearchForm) {
			requestOrigin = RequestOrigin.SEARCH;
		} else if (StringUtils.equals(serverDomain, referrerDomain)) {
			requestOrigin = RequestOrigin.INSIDE_NAVIGATION;
		} else {
			requestOrigin = RequestOrigin.OUTSIDE_NAVIGATION;
		}

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
		searchStat.setUserAgent(userAgent);
		searchStat.setReferrerDomain(referrerDomain);
		searchStat.setSessionId(sessionId);
		searchStat.setRequestOrigin(requestOrigin);

		HttpHeaders headers = createHeaders();
		RestTemplate restTemplate = new RestTemplate();
		HttpEntity<SearchStat> searchStatEntity = new HttpEntity<>(searchStat, headers);
		try {
			restTemplate.postForObject(serviceUrl + SEARCH_URI, searchStatEntity, String.class);
		} catch (RestClientException e) {
			logger.error("posting search stat data failed: {}", e.getMessage());
		}
	}

	private HttpHeaders createHeaders() {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.set(STAT_API_KEY_HEADER_NAME, serviceKey);
		return headers;
	}

}
