package eki.wordweb.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import eki.common.constant.GlobalConstant;
import eki.common.constant.StatType;
import eki.common.data.ExceptionStat;
import eki.common.data.SearchStat;
import eki.common.exception.RemoteServiceException;
import eki.wordweb.constant.WebConstant;
import eki.wordweb.data.StatServiceStatus;

@Component
public class StatDataCollector implements GlobalConstant, WebConstant {

	private static Logger logger = LoggerFactory.getLogger(StatDataCollector.class);

	private static final int REQUEST_TIMEOUT_SECONDS = 5;

	private static final String STAT_CREATE_URI = "/create";

	private static final String STAT_COUNT_URI = "/count";

	@Value("${ekistat.service.enabled:false}")
	private boolean serviceEnabled;

	@Value("${ekistat.service.url}")
	private String ekistatServiceUrl;

	@Value("${ekistat.service.key}")
	private String ekistatServiceKey;

	public StatServiceStatus getStatServiceStatus() {

		StatServiceStatus statServiceStatus = new StatServiceStatus();
		statServiceStatus.setServiceEnabled(serviceEnabled);
		statServiceStatus.setServiceUrl(ekistatServiceUrl);
		if (serviceEnabled) {
			try {
				long wwSearchStatCount = getWwSearchStatCount();
				statServiceStatus.setWwSearchStatCount(wwSearchStatCount);
				statServiceStatus.setResponseStatus("OK");
			} catch (Exception e) {
				statServiceStatus.setExceptionMessage(e.getMessage());
				statServiceStatus.setResponseStatus("ERROR");
				logger.error("Stat service status error: ", e);
			}
		}
		return statServiceStatus;
	}

	@Async
	public void postExceptionStat(Throwable exception) {

		if (!serviceEnabled || exception == null) {
			return;
		}

		String exceptionName = exception.getClass().getName();
		String exceptionMessage = exception.getMessage();
		ExceptionStat exceptionStat = new ExceptionStat(exceptionName, exceptionMessage);
		String statCreateUrl = getStatCreateUri(StatType.WW_EXCEPTION);

		try {
			postRequest(statCreateUrl, exceptionStat);
		} catch (Exception e) {
			logger.error("Posting exception stat data failed.", e.getMessage());
		}
	}

	@Async
	public void postSearchStat(SearchStat searchStat) throws Exception {

		if (!serviceEnabled) {
			return;
		}
		String statCreateUrl = getStatCreateUri(StatType.WW_SEARCH);
		try {
			postRequest(statCreateUrl, searchStat);
		} catch (Exception e) {
			logger.error("Posting search stat data failed.", e.getMessage());
		}
	}

	private void postRequest(String url, Object statObject) throws Exception {

		ObjectMapper objectMapper = new ObjectMapper();
		String requestBody = objectMapper.writeValueAsString(statObject);
		HttpClient client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2).build();
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(url))
				.header(STAT_API_KEY_HEADER_NAME, ekistatServiceKey)
				.POST(HttpRequest.BodyPublishers.ofString(requestBody))
				.timeout(Duration.ofSeconds(REQUEST_TIMEOUT_SECONDS))
				.build();

		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
		if (response.statusCode() != HttpStatus.OK.value()) {
			logger.debug("Unexpected response status code {} when posting stat", response.statusCode());
		}
	}

	private long getWwSearchStatCount() throws Exception {

		HttpClient client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2).build();
		String statCountUrl = ekistatServiceUrl + STAT_COUNT_URI;
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(statCountUrl))
				.header(STAT_API_KEY_HEADER_NAME, ekistatServiceKey)
				.GET()
				.timeout(Duration.ofSeconds(REQUEST_TIMEOUT_SECONDS))
				.build();

		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
		int statusCode = response.statusCode();
		if (statusCode != HttpStatus.OK.value()) {
			throw new RemoteServiceException("Unexpected response status code " + statusCode + " when getting ww stat count");
		}
		long wwSearchStatCount = Long.parseLong(response.body());
		return wwSearchStatCount;
	}

	private String getStatCreateUri(StatType statType) {
		return ekistatServiceUrl + STAT_CREATE_URI + "/" + statType.name();
	}
}
