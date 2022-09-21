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

/**
 * Based on the following:
 *
 * Add New Collection Metadata
 * POST /api/termservice/sync/collection
 *
 * Update Existing Collection Metadata
 * PUT /api/termservice/sync/collection
 *
 * Add New concept entry or Update Existing Concept Entries
 * POST /api/termservice/sync/collection/{collection_id}/entries
 *
 * Delete Concept Entry
 * DELETE /api/termservice/sync/collection/{collection_id}/entries/{entry_id}
 *
 * Delete Collection
 * DELETE /api/termservice/sync/collection/{collection_id}
 */
@Component
public class FedTermClient {

	private static final Logger logger = LoggerFactory.getLogger(FedTermClient.class);

	private static final int HTTP_POST = 1;

	private static final int HTTP_PUT = 2;

	private static final int HTTP_DELETE = 3;

	private static final String CONTENT_TYPE_JSON = "application/json";

	private static final String CONTENT_TYPE_XML = "application/xml";	

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
		String resultMessage = requestPostWithBody(createCollectionServiceUrl, collectionMessageJson, CONTENT_TYPE_JSON, HTTP_POST);

		logger.info("Creation of FedTerm collection for \"{}\" returned \"{}\"", datasetCode, resultMessage);

		boolean isNumeric = StringUtils.isNumeric(resultMessage);
		if (!isNumeric) {
			throw new RemoteServiceException("Failed to create FedTerm collection. Service returned: " + resultMessage);
		} else if (StringUtils.length(resultMessage) > 99) {
			throw new RemoteServiceException("Failed to create FedTerm collection. Service returned: " + resultMessage);
		}
		String fedTermCollectionId = new String(resultMessage);
		return fedTermCollectionId;
	}

	public void updateFedTermCollection(String datasetCode, String fedTermCollectionId, String collectionMessageJson) throws Exception {

		String updateCollectionServiceUrl = new String(fedTermServiceUrl);
		String resultMessage = requestPostWithBody(updateCollectionServiceUrl, collectionMessageJson, CONTENT_TYPE_JSON, HTTP_PUT);

		logger.info("Update of FedTerm collection for \"{} ({})\" returned \"{}\"", fedTermCollectionId, datasetCode, resultMessage);
	}

	public void deleteFedTermCollection(String datasetCode, String fedTermCollectionId) throws Exception {

		String deleteCollectionServiceUrl = new String(fedTermServiceUrl) + "/" + fedTermCollectionId;
		String resultMessage = requestPostWithBody(deleteCollectionServiceUrl, null, CONTENT_TYPE_JSON, HTTP_DELETE);

		logger.info("Deleting FedTerm collection \"{} ({})\" returned \n\t\"{}\"", fedTermCollectionId, datasetCode, resultMessage);
	}

	public void createOrUpdateFedTermConceptEntries(String datasetCode, String fedTermCollectionId, String conceptEntriesTbxMessageXml) throws Exception {

		String createOrUpdateConceptEntriesUrl = new String(fedTermServiceUrl) + "/" + fedTermCollectionId + "/entries";
		String resultMessage = requestPostWithBody(createOrUpdateConceptEntriesUrl, conceptEntriesTbxMessageXml, CONTENT_TYPE_XML, HTTP_POST);

		logger.info("Creating or updating of FedTerm concept entries for \"{} ({})\" returned \n\t\"{}\"", fedTermCollectionId, datasetCode, resultMessage);
	}

	private String requestPostWithBody(String serviceUrl, String messageBody, String contentType, int httpMethod) throws Exception {

		Builder httpRequestBuilder = HttpRequest.newBuilder(URI.create(serviceUrl));
		httpRequestBuilder = httpRequestBuilder.header("Content-Type", contentType);
		BodyPublisher bodyPublisher = null;
		HttpRequest request = null;
		if (HTTP_POST == httpMethod) {
			bodyPublisher = BodyPublishers.ofString(messageBody);
			request = httpRequestBuilder.POST(bodyPublisher).build();
		} else if (HTTP_PUT == httpMethod) {
			bodyPublisher = BodyPublishers.ofString(messageBody);
			request = httpRequestBuilder.PUT(bodyPublisher).build();
		} else if (HTTP_DELETE == httpMethod) {
			request = httpRequestBuilder.DELETE().build();
		}
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
