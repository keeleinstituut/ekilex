package eki.ekilex.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import eki.common.constant.GlobalConstant;
import eki.common.data.MediaFileContent;
import eki.common.data.MediaFileRef;

@Component
public class EkimediaClient implements GlobalConstant {

	private static final Logger logger = LoggerFactory.getLogger(EkimediaClient.class);

	private static final String MEDIA_CREATE_URI = "/create";

	private static final String MEDIA_DELETE_URI = "/delete";

	@Value("${ekimedia.service.url}")
	private String serviceUrl;

	@Value("${ekimedia.service.key}")
	private String serviceKey;

	public MediaFileRef createMediaFile(MediaFileContent mediaFileContent) {

		String filename = mediaFileContent.getFilename();
		byte[] content = mediaFileContent.getContent();

		String mediaCreateUrl = serviceUrl + MEDIA_CREATE_URI;
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
		requestHeaders.set(EKIMEDIA_API_KEY_HEADER_NAME, serviceKey);

		ByteArrayResource contentResource = new ByteArrayResource(content);
		MultiValueMap<String, Object> requestPartMap = new LinkedMultiValueMap<String, Object>();
		requestPartMap.add("filename", filename);
		requestPartMap.add("content", contentResource);
		HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<MultiValueMap<String, Object>>(requestPartMap, requestHeaders);

		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<MediaFileRef> response = restTemplate.exchange(mediaCreateUrl, HttpMethod.POST, requestEntity, MediaFileRef.class);
		MediaFileRef mediaFileRef = response.getBody();

		logger.debug("createMediaFile response: {}", mediaFileRef);

		return mediaFileRef;
	}

	public void deleteMediaFile(String filename) {

		String mediaDeleteUrl = serviceUrl + MEDIA_DELETE_URI;

		HttpHeaders headers = new HttpHeaders();
		headers.set(EKIMEDIA_API_KEY_HEADER_NAME, serviceKey);
		HttpEntity<String> entity = new HttpEntity<>(headers);

		String serviceUriWithParameters = UriComponentsBuilder
				.fromUriString(mediaDeleteUrl)
				.queryParam("objectFilename", filename)
				.toUriString();

		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> response = restTemplate.exchange(serviceUriWithParameters, HttpMethod.POST, entity, String.class);
		String responseMessage = response.getBody();

		logger.debug("deleteMediaFile response: {}", responseMessage);
	}
}
