package eki.wordweb.service;

import static org.apache.commons.lang3.StringUtils.isBlank;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
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

	private final String[] POS_PUNCTUATIONS = new String[] {"Z", "_Z_"};

	private final String QUOTATION_MARK = "\"";

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

		boolean isPosQuery = false;
		String corpName = null;
		String wordKey = null;
		if (StringUtils.equals(searchMode, SEARCH_MODE_DETAIL)) {
			isPosQuery = true;
			corpName = corpNameDetail;
			wordKey = wordKeyDetail;
		} else if (StringUtils.equals(searchMode, SEARCH_MODE_SIMPLE)) {
			isPosQuery = false;
			corpName = corpNameSimple;
			wordKey = wordKeySimple;
		}

		String querySentence = parseSentenceToQueryString(sentence, wordKey, isPosQuery);

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
			int middlePartStartPos = (int) match.get("start");
			int middlePartEndPos = (int) match.get("end");
			int currentWordPos = 0;
			boolean skipSpaceBeforeWord = false;
			CorporaSentence sentence = new CorporaSentence();
			for (Map<String, Object> token : (List<Map<String, Object>>) kwic.get("tokens")) {
				String word = parseWord(token, skipSpaceBeforeWord);
				if (currentWordPos < middlePartStartPos) {
					sentence.setLeftPart(sentence.getLeftPart() + word);
				} else if (currentWordPos >= middlePartEndPos) {
					sentence.setRightPart(sentence.getRightPart() + word);
				} else {
					sentence.setMiddlePart(sentence.getMiddlePart() + word);
				}
				if (currentWordPos == 0 || skipSpaceBeforeWord) {
					skipSpaceBeforeWord = StringUtils.equals(QUOTATION_MARK, word);
				}
				currentWordPos++;
			}
			sentences.add(sentence);
		}
		return sentences;
	}

	private String parseWord(Map<String, Object> token, boolean skipSpaceBeforeWord) {

		String word = (String) token.get("word");
		String pos = (String) token.get("pos");
		boolean isPunctuation = ArrayUtils.contains(POS_PUNCTUATIONS, pos);
		if (isPunctuation || skipSpaceBeforeWord) {
			return word;
		} else {
			return " " + word;
		}
	}

}
