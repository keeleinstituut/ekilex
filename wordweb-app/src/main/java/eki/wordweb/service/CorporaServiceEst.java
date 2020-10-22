package eki.wordweb.service;

import static org.apache.commons.lang3.StringUtils.isBlank;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import eki.wordweb.data.CorporaSentence;

@Component
public class CorporaServiceEst extends AbstractCorporaService {

	@Value("${corpora.service.est.url:}")
	private String serviceUrl;

	@Value("${corpora.service.est.corpname.detail:}")
	private String corpNameDetail;

	@Value("${corpora.service.est.corpname.simple:}")
	private String corpNameSimple;

	@Value("${corpora.service.est.word.key.detail:}")
	private String wordKeyDetail;

	@Value("${corpora.service.est.word.key.simple:}")
	private String wordKeySimple;

	@Value("#{${corpora.service.est.parameters}}")
	private MultiValueMap<String, String> queryParametersMap;

	private final String POS_PUNCTUATION = "Z";

	@Cacheable(value = CACHE_KEY_CORPORA)
	public List<CorporaSentence> getSentences(String sentence, String searchMode) {

		URI corporaUrl = composeCorporaUrl(sentence, searchMode);
		Map<String, Object> response = requestSentences(corporaUrl);
		return parseResponse(response);
	}

	private URI composeCorporaUrl(String sentence, String searchMode) {

		if (isBlank(serviceUrl)) {
			return null;
		}

		String corpName = null;
		String wordKey = null;
		if (StringUtils.equals(searchMode, SEARCH_MODE_DETAIL)) {
			corpName = corpNameDetail;
			wordKey = wordKeyDetail;
		} else if (StringUtils.equals(searchMode, SEARCH_MODE_SIMPLE)) {
			corpName = corpNameSimple;
			wordKey = wordKeySimple;
		}
		String querySentence = parseSentenceToQueryString(sentence, wordKey);

		return UriComponentsBuilder.fromUriString(serviceUrl)
				.queryParam("corpus", corpName)
				.queryParam("cqp", querySentence)
				.queryParams(queryParametersMap)
				.encode(StandardCharsets.UTF_8)
				.build()
				.toUri();
	}

	private List<CorporaSentence> parseResponse(Map<String, Object> response) {

		List<CorporaSentence> sentences = new ArrayList<>();
		if (response.isEmpty() || (response.containsKey("hits") && (int) response.get("hits") == 0) || !response.containsKey("kwic")) {
			return sentences;
		}
		for (Map<String, Object> kwic : (List<Map<String, Object>>) response.get("kwic")) {
			Map<String, Object> match = (Map<String, Object>) kwic.get("match");
			int startPos = (int) match.get("start");
			int endPos = (int) match.get("end");
			int index = 0;
			CorporaSentence sentence = new CorporaSentence();
			for (Map<String, Object> token : (List<Map<String, Object>>) kwic.get("tokens")) {
				String word = parseWord(token);
				if (index < startPos) {
					sentence.setLeftPart(sentence.getLeftPart() + word);
				} else if (index >= endPos) {
					sentence.setRightPart(sentence.getRightPart() + word);
				} else {
					sentence.setMiddlePart(sentence.getMiddlePart() + word);
				}
				index++;
			}
			sentences.add(sentence);
		}
		return sentences;
	}

	private String parseWord(Map<String, Object> token) {

		String word = (String) token.get("word");
		String pos = (String) token.get("pos");
		boolean isPunctuation = StringUtils.equals(pos, POS_PUNCTUATION);
		word = isPunctuation ? word : " " + word;
		return word;
	}

}
