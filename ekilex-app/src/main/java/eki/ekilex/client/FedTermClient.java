package eki.ekilex.client;

import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpRequest.Builder;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import eki.common.exception.RemoteServiceException;

@Component
public class FedTermClient {

	private static final Logger logger = LoggerFactory.getLogger(FedTermClient.class);

	@Value("${fedterm.service.url:null}")
	private String fedTermServiceUrl;

	@Value("${fedterm.auth.username:null}")
	private String fedTermAuthUsername;

	@Value("${fedterm.auth.password:null}")
	private String fedTermAuthPassword;

	public boolean isFedTermAccessEnabled() {
		return StringUtils.isNotBlank(fedTermServiceUrl)
				&& StringUtils.isNotBlank(fedTermAuthUsername)
				&& StringUtils.isNotBlank(fedTermAuthPassword);
	}

	public String createFedTermCollection(String datasetCode, String collectionMessageJson) throws Exception {

		String createCollectionServiceUrl = new String(fedTermServiceUrl);
		String contentType = "application/json";
		String fedTermCollectionId = requestPostWithBody(createCollectionServiceUrl, collectionMessageJson, contentType);

		logger.info("Creation of FedTerm collection for \"{}\" returned \"{}\"", datasetCode, fedTermCollectionId);

		boolean isNumeric = StringUtils.isNumeric(fedTermCollectionId);
		if (!isNumeric) {
			throw new RemoteServiceException("Failed to create FedTerm collection. Service returned: " + fedTermCollectionId);
		}
		if (StringUtils.length(fedTermCollectionId) > 99) {
			throw new RemoteServiceException("Failed to create FedTerm collection. Service returned: " + fedTermCollectionId);
		}
		return fedTermCollectionId;
	}

	public void createOrUpdateFedTermConceptEntries(String datasetCode, String fedTermCollectionId, String conceptEntriesTbxMessageXml) throws Exception {

		String createOrUpdateConceptEntriesUrl = new String(fedTermServiceUrl) + "/" + fedTermCollectionId + "/entries";
		String contentType = "application/xml";
		String conceptEntriesResult = requestPostWithBody(createOrUpdateConceptEntriesUrl, conceptEntriesTbxMessageXml, contentType);

		logger.info("Creating or updating of FedTerm concept entries for \"{} ({})\" returned \"\n\t{}\"", fedTermCollectionId, datasetCode, conceptEntriesResult);
	}

	private String requestPostWithBody(String serviceUrl, String messageBody, String contentType) throws Exception {

		Builder httpRequestBuilder = HttpRequest.newBuilder(URI.create(serviceUrl));
		httpRequestBuilder = httpRequestBuilder.header("Content-Type", contentType);
		BodyPublisher bodyPublisher = BodyPublishers.ofString(messageBody);
		HttpRequest request = httpRequestBuilder.POST(bodyPublisher).build();
		HttpClient client = HttpClient.newBuilder()
				.version(HttpClient.Version.HTTP_2)
				.authenticator(getAuthenticator())
				.build();
		HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
		String responseBody = response.body();
		int responseStatusCode = response.statusCode();
		if (responseStatusCode != 200) {
			throw new RemoteServiceException("Failed to access \"" + serviceUrl + "\". Service returned " + responseStatusCode + "; " + responseBody);
		}
		return responseBody;
	}

	private Authenticator getAuthenticator() {
		return new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(fedTermAuthUsername, fedTermAuthPassword.toCharArray());
			}
		};
	}
}
