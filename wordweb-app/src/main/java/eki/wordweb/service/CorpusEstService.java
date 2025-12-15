package eki.wordweb.service;

import static org.apache.commons.lang3.StringUtils.isBlank;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import eki.wordweb.data.CorpusSentence;

@Component
public class CorpusEstService extends AbstractRemoteRequestService {

	private static final String[] POS_PUNCTUATIONS = {"Z", "_Z_"};

	private static final String QUOTATION_MARK = "\"";

	private static final String[] SKIP_QUERY_POS_CODES = {"inda", "suf", "prf"};

	private static final String[] IGNORE_POS_CODES = {"vrm"};

	@Value("${corpus.service.est.url:}")
	private String serviceUrl;

	@Value("${corpus.service.est.corpname.detail:}")
	private String corpNameDetail;

	@Value("${corpus.service.est.corpname.simple:}")
	private String corpNameSimple;

	@Value("${corpus.service.est.word.key.detail:}")
	private String wordQueryParamKeyDetail;

	@Value("${corpus.service.est.word.key.simple:}")
	private String wordQueryParamKeySimple;

	@Value("#{${corpus.service.est.parameters}}")
	private MultiValueMap<String, String> defaultQueryParamsMap;

	@Value("#{${corpus.service.est.posmap}}")
	private Map<String, String> posMap;

	@Cacheable(value = CACHE_KEY_CORPUS, key = "{#root.methodName, #searchMode, #wordValue, #posCodes}")
	public List<CorpusSentence> getCorpusSentences(String searchMode, String wordValue, List<String> posCodes) {

		URI corpusUrl = composeCorpusUri(searchMode, wordValue, posCodes);
		Map<String, Object> response = requestGet(corpusUrl);
		List<CorpusSentence> corpusSentences = parseResponse(response);
		return corpusSentences;
	}

	private URI composeCorpusUri(String searchMode, String wordValue, List<String> posCodes) {

		if (isBlank(serviceUrl)) {
			return null;
		}

		boolean isSkipQuery = CollectionUtils.isNotEmpty(posCodes)
				&& CollectionUtils.containsAny(posCodes, SKIP_QUERY_POS_CODES);
		if (isSkipQuery) {
			return null;
		}

		String corpName = null;
		String queryString;
		if (StringUtils.equals(searchMode, SEARCH_MODE_DETAIL)) {
			corpName = corpNameDetail;
			queryString = composeQueryString(wordValue, posCodes, true);
		} else if (StringUtils.equals(searchMode, SEARCH_MODE_SIMPLE)) {
			corpName = corpNameSimple;
			queryString = composeQueryString(wordValue, null, false);
		} else {
			return null;
		}

		try {
			return UriComponentsBuilder
					.fromUriString(serviceUrl)
					.queryParam("corpus", corpName)
					.queryParam("cqp", queryString)
					.queryParams(defaultQueryParamsMap)
					.encode(StandardCharsets.UTF_8)
					.build()
					.toUri();
		} catch (Exception e) {
			// probably some hacky input anyway
			return null;
		}
	}

	private String composeQueryString(String wordValue, List<String> posCodes, boolean isPosQuery) {

		String[] wordValueTokens = StringUtils.split(wordValue, " ");
		List<String> queryParams = new ArrayList<>();

		for (String wordValueToken : wordValueTokens) {

			if (isPosQuery) {
				appendQueryParamWithPos(queryParams, wordValueToken, posCodes);
			} else {
				appendQueryParamWithoutPos(queryParams, wordValueToken);
			}
		}
		String queryString = StringUtils.join(queryParams, " ");
		return queryString;
	}

	private void appendQueryParamWithPos(List<String> queryParams, String wordValueToken, List<String> posCodes) {

		String queryParam;
		String queryPosCode;

		if (CollectionUtils.isEmpty(posCodes)) {

			queryPosCode = ".?";
			queryParam = "[" + composeQueryParamWithPos(wordValueToken, queryPosCode) + "]";
			queryParams.add(queryParam);

		} else {

			List<String> posQueryParams = new ArrayList<>();
			for (String posCode : posCodes) {

				if (ArrayUtils.contains(IGNORE_POS_CODES, posCode)) {
					queryPosCode = ".?";
				} else {
					queryPosCode = posMap.get(posCode);
				}
				if (StringUtils.isBlank(queryPosCode)) {
					queryPosCode = ".?";
				}
				queryParam = composeQueryParamWithPos(wordValueToken, queryPosCode);
				posQueryParams.add(queryParam);
			}
			queryParam = "[" + StringUtils.join(posQueryParams, "|") + "]";
			queryParams.add(queryParam);
		}
	}

	private String composeQueryParamWithPos(String wordValueToken, String queryPosCode) {
		return wordQueryParamKeyDetail + "=\"" + wordValueToken + "-" + queryPosCode + "\"";
	}

	private void appendQueryParamWithoutPos(List<String> queryParams, String wordValueToken) {

		String queryParam = "[" + wordQueryParamKeySimple + "=\"" + wordValueToken + "\"]";
		queryParams.add(queryParam);
	}

	private List<CorpusSentence> parseResponse(Map<String, Object> response) {

		List<CorpusSentence> sentences = new ArrayList<>();
		if (MapUtils.isEmpty(response)) {
			return sentences;
		}
		if (response.containsKey("hits") && (int) response.get("hits") == 0) {
			return sentences;
		}
		if (!response.containsKey("kwic")) {
			return sentences;
		}
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> kwics = (List<Map<String, Object>>) response.get("kwic");
		for (Map<String, Object> kwic : kwics) {
			@SuppressWarnings("unchecked")
			Map<String, Object> match = (Map<String, Object>) kwic.get("match");
			int middlePartStartPos = (int) match.get("start");
			int middlePartEndPos = (int) match.get("end");
			int currentWordPos = 0;
			boolean skipSpaceBeforeWord = false;
			CorpusSentence sentence = new CorpusSentence();
			@SuppressWarnings("unchecked")
			List<Map<String, Object>> tokens = (List<Map<String, Object>>) kwic.get("tokens");
			for (Map<String, Object> token : tokens) {
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
