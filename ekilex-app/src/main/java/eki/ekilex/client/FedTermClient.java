package eki.ekilex.client;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpRequest.Builder;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import eki.common.data.OrderedMap;
import eki.common.exception.RemoteServiceException;
import eki.ekilex.data.Dataset;

@Component
public class FedTermClient {

	private static final Logger logger = LoggerFactory.getLogger(FedTermClient.class);

	@Value("${fedterm.service.url:null}")
	private String fedTermServiceUrl;

	@Value("${wordweb.dataset.url}")
	private String datasetLandingPageUrl;

	public boolean isFedTermAccessEnabled() {
		return StringUtils.isNotBlank(fedTermServiceUrl);
	}

	public String createFedTermCollection(Dataset dataset) throws Exception {

		String datasetCode = dataset.getCode();
		String datasetDescription = dataset.getDescription();
		datasetDescription = StringUtils.remove(datasetDescription, '\r');
		datasetDescription = StringUtils.remove(datasetDescription, '\n');
		String datasetLandingPageUrlWithDatasetCode = StringUtils.replace(datasetLandingPageUrl, "{datasetCode}", datasetCode);

		Map<String, Object> collectionMessageMap = new OrderedMap<>();
		collectionMessageMap.put("name", dataset.getName());
		collectionMessageMap.put("description", datasetDescription);
		collectionMessageMap.put("domainid", "36");
		collectionMessageMap.put("allowsUsesBesidesDGT", Boolean.TRUE);
		collectionMessageMap.put("appropriatnessForDSI", Boolean.TRUE);
		collectionMessageMap.put("attributionText", datasetDescription);
		collectionMessageMap.put("cpEmail", "eki@eki.ee");
		collectionMessageMap.put("cpName", "n/a");
		collectionMessageMap.put("cpOrganization", "Institute of the Estonian Language");
		collectionMessageMap.put("cpSurname", "n/a");
		collectionMessageMap.put("iprEmail", "eki@eki.ee");
		collectionMessageMap.put("iprName", "n/a");
		collectionMessageMap.put("iprOrganization", "Institute of the Estonian Language");
		collectionMessageMap.put("iprSurname", "n/a");
		collectionMessageMap.put("isPSI", Boolean.FALSE);
		collectionMessageMap.put("licence", "CC-BY-4.0");
		collectionMessageMap.put("originalName", dataset.getName());
		collectionMessageMap.put("originalNameLang", "et");
		collectionMessageMap.put("restrictionsOfUse", "No restrictions, you are welcome to use");
		collectionMessageMap.put("sourceURL", datasetLandingPageUrlWithDatasetCode);

		ObjectMapper objectMapper = new ObjectMapper();
		String collectionMessageJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(collectionMessageMap);

		String createCollectionServiceUrl = fedTermServiceUrl + "/termservice/sync/collection";
		String fedTermCollectionId = requestPostWithBody(createCollectionServiceUrl, collectionMessageJson);

		logger.info("Creation of FedTerm collection for \"{}\" returned \"{}\"", datasetCode, fedTermCollectionId);

		boolean isNumeric = StringUtils.isNumeric(fedTermCollectionId);
		if (!isNumeric) {
			throw new RemoteServiceException("Failed to create FedTerm collection. Service returned: " + fedTermCollectionId);
		}
		return fedTermCollectionId;
	}

	//TODO add auth
	private String requestPostWithBody(String serviceUrl, String messageBody) throws Exception {

		Builder httpRequestBuilder = HttpRequest.newBuilder(URI.create(serviceUrl));
		httpRequestBuilder = httpRequestBuilder.header("Content-Type", "application/json");
		BodyPublisher bodyPublisher = BodyPublishers.ofString(messageBody);
		HttpRequest request = httpRequestBuilder.POST(bodyPublisher).build();
		HttpClient client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2).build();
		HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
		if (response.statusCode() != 200) {
			throw new RemoteServiceException("Failed to access \"" + serviceUrl + "\". Service returned " + response.statusCode() + "; " + response.body());
		}
		String responseBody = response.body();
		return responseBody;
	}
}
