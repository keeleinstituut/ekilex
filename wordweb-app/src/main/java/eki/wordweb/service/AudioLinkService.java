package eki.wordweb.service;

import java.net.URI;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class AudioLinkService extends AbstractRemoteRequestService {

	private static final String SERVICE_ID_SPEECH = "speech";

	private static final String SERVICE_ID_MORPHO = "morpho";

	@Value("${server.servlet.context-path:}")
	private String contextPath;

	@Value("${speech.synthesizer.service.url:}")
	private String speechSynthesizerUrl;

	@Value("${morpho.synthesizer.service.url:}")
	private String morphoSynthesizerUrl;

	public String getAudioLink(String text, String serviceId) {

		if (StringUtils.equals(serviceId, SERVICE_ID_SPEECH)) {
			if (StringUtils.isBlank(speechSynthesizerUrl)) {
				return null;
			}
			return getAudioLinkFromService(text, speechSynthesizerUrl);
		}
		if (StringUtils.equals(serviceId, SERVICE_ID_MORPHO)) {
			if (StringUtils.isBlank(morphoSynthesizerUrl)) {
				return null;
			}
			return getAudioLinkFromService(text, morphoSynthesizerUrl);
		}
		return null;
	}

	private String getAudioLinkFromService(String text, String serviceUrl) {

		URI url = UriComponentsBuilder
				.fromUriString(speechSynthesizerUrl)
				.queryParam("haal", 15) // 14 <- female voice
				.queryParam("tekst", text)
				.build()
				.toUri();
		Map<String, Object> response = request(url);
		String mp3Url = (String) response.get("mp3url");
		return mp3Url;
	}
}
