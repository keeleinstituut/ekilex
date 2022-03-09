package eki.wordweb.service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;

import org.jooq.tools.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import eki.common.constant.GlobalConstant;
import eki.common.constant.RequestOrigin;
import eki.common.constant.StatType;
import eki.common.data.ExceptionStat;
import eki.common.data.SearchStat;
import eki.wordweb.data.SearchRequest;
import eki.wordweb.data.SearchValidation;
import eki.wordweb.data.WordsData;

@Component
public class StatDataCollector implements GlobalConstant {

	private static Logger logger = LoggerFactory.getLogger(StatDataCollector.class);

	private static final int REQUEST_TIMEOUT_SECONDS = 5;

	@Value("${ekistat.service.enabled:false}")
	private boolean serviceEnabled;

	@Value("${ekistat.service.url}")
	private String serviceUrl;

	@Value("${ekistat.service.key}")
	private String serviceKey;

	@Async
	public void postExceptionStat(Throwable exception) {

		if (!serviceEnabled || exception == null) {
			return;
		}

		String exceptionName = exception.getClass().getName();
		String exceptionMessage = exception.getMessage();
		ExceptionStat exceptionStat = new ExceptionStat(exceptionName, exceptionMessage);
		String url = serviceUrl + "/" + StatType.WW_EXCEPTION.name();

		try {
			postStat(url, exceptionStat);
		} catch (Exception e) {
			logger.error("Posting exception stat data failed.", e);
		}
	}

	@Async
	public void postSearchStat(SearchRequest searchRequest) throws Exception {

		if (!serviceEnabled) {
			return;
		}
		SearchValidation searchValidation = searchRequest.getSearchValidation();
		String searchWord = searchValidation.getSearchWord();
		Integer homonymNr = searchValidation.getHomonymNr();
		List<String> destinLangs = searchValidation.getDestinLangs();
		List<String> datasetCodes = searchValidation.getDatasetCodes();
		String searchUri = searchValidation.getSearchUri();
		WordsData wordsData = searchRequest.getWordsData();
		int resultCount = wordsData.getResultCount();
		boolean resultsExist = wordsData.isResultsExist();
		boolean isSingleResult = wordsData.isSingleResult();
		String sessionId = searchRequest.getSessionId();
		String userAgent = searchRequest.getUserAgent();
		String referer = searchRequest.getReferer();
		boolean isSearchForm = searchRequest.isSearchForm();
		String searchMode = searchRequest.getSearchMode();

		String referrerDomain = null;
		if (referer != null) {
			referrerDomain = new URI(referer).getHost();
		}
		String serverDomain = searchRequest.getServerDomain();

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
		String url = serviceUrl + "/" + StatType.WW_SEARCH.name();

		try {
			postStat(url, searchStat);
		} catch (Exception e) {
			logger.error("Posting search stat data failed.", e);
		}
	}

	public void postStat(String url, Object statObject) throws IOException, InterruptedException {

		ObjectMapper objectMapper = new ObjectMapper();
		String requestBody = objectMapper.writeValueAsString(statObject);
		HttpClient client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2).build();
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(url))
				.header(STAT_API_KEY_HEADER_NAME, serviceKey)
				.POST(HttpRequest.BodyPublishers.ofString(requestBody))
				.timeout(Duration.ofSeconds(REQUEST_TIMEOUT_SECONDS))
				.build();

		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
		if (response.statusCode() != HttpStatus.OK.value()) {
			logger.debug("Unexpected response status code {} when posting stat", response.statusCode());
		}
	}

}
