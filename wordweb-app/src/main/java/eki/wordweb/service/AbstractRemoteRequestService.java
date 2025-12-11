package eki.wordweb.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpRequest.Builder;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.Collections;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.json.JsonParser;
import org.springframework.boot.json.JsonParserFactory;

import eki.common.constant.GlobalConstant;
import eki.wordweb.constant.SystemConstant;

public abstract class AbstractRemoteRequestService implements SystemConstant, GlobalConstant {

	private static final Logger logger = LoggerFactory.getLogger(AbstractRemoteRequestService.class);

	private final int CONNECTION_TIMEOUT_SEC = 5;

	protected Map<String, Object> requestGet(URI requestUrl) {

		Map<String, Object> response = Collections.emptyMap();
		if (requestUrl == null) {
			return response;
		}

		String responseAsString = doGetRequest(requestUrl);
		if (responseAsString != null) {
			JsonParser jsonParser = JsonParserFactory.getJsonParser();
			response = jsonParser.parseMap(responseAsString);
		}
		return response;
	}

	private String doGetRequest(URI url) {

		String responseBody = null;
		logger.debug("Requesting > \"{}\"", url.toString());
		try {
			Builder httpRequestBuilder = HttpRequest.newBuilder(url);
			httpRequestBuilder = httpRequestBuilder.header("Accept", "*/*");
			HttpRequest request = httpRequestBuilder
					.GET()
					.timeout(Duration.ofSeconds(CONNECTION_TIMEOUT_SEC))
					.build();
			HttpClient client = HttpClient.newBuilder()
					.version(HttpClient.Version.HTTP_2)
					.connectTimeout(Duration.ofSeconds(CONNECTION_TIMEOUT_SEC))
					.build();
			HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
			int statusCode = response.statusCode();
			if (statusCode == 200) {
				responseBody = response.body();
			} else {
				logger.warn("Request returned HTTP{} with: \"{}\"", statusCode, responseBody);
			}
		} catch (Exception e) {
			logger.error("Error with requesting {}", url);
			logger.error(e.getMessage());
		}
		return responseBody;
	}

	protected String requestPostWithBody(String serviceUrl, String headerKey, String headerValue, String messageBody) {

		String responseBody = null;
		try {
			URI url = URI.create(serviceUrl);
			Builder httpRequestBuilder = HttpRequest.newBuilder(url);
			httpRequestBuilder = httpRequestBuilder.header("Content-Type", "application/json");
			if (StringUtils.isNotBlank(headerKey) && StringUtils.isNotBlank(headerValue)) {
				httpRequestBuilder = httpRequestBuilder.header(headerKey, headerValue);
			}
			BodyPublisher bodyPublisher = BodyPublishers.ofString(messageBody);
			HttpRequest request = httpRequestBuilder
					.POST(bodyPublisher)
					.timeout(Duration.ofSeconds(CONNECTION_TIMEOUT_SEC))
					.build();
			HttpClient client = HttpClient.newBuilder()
					.version(HttpClient.Version.HTTP_2)
					.connectTimeout(Duration.ofSeconds(CONNECTION_TIMEOUT_SEC))
					.build();
			HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
			responseBody = response.body();
			int statusCode = response.statusCode();
			if (statusCode == 200) {
				responseBody = response.body();
			} else {
				logger.warn("Request returned HTTP{} with: \"{}\"", statusCode, responseBody);
			}
		} catch (Exception e) {
			logger.error("Error with requesting {}", serviceUrl);
			logger.error(e.getMessage());
		}
		return responseBody;
	}
}
