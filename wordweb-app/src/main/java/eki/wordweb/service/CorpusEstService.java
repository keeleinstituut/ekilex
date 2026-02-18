package eki.wordweb.service;

import static org.apache.commons.lang3.StringUtils.isBlank;

import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import eki.common.data.KeyValuePair;
import eki.wordweb.data.CorpusSentence;
import eki.wordweb.data.CorpusSource;
import eki.wordweb.web.util.ViewUtil;

@Component
public class CorpusEstService extends AbstractRemoteRequestService implements InitializingBean {

	private static final String CORPUS_NAME_MAP_FILE_PATH = "data/corpus_name_map.txt";

	private static final String[] POS_PUNCTUATIONS = {"Z", "_Z_"};

	private static final String QUOTATION_MARK = "\"";

	private static final String[] SKIP_QUERY_POS_CODES = {"inda", "suf", "prf"};

	private static final String[] IGNORE_POS_CODES = {"vrm"};

	@Autowired
	private ViewUtil viewUtil;

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
	private Map<String, String> defaultQueryParamsMap;

	@Value("#{${corpus.service.est.posmap}}")
	private Map<String, String> posMap;

	private Map<String, String> corpusNameMap;

	@Override
	public void afterPropertiesSet() throws Exception {

		ClassLoader classLoader = this.getClass().getClassLoader();
		InputStream resourceFileInputStream = classLoader.getResourceAsStream(CORPUS_NAME_MAP_FILE_PATH);
		List<String> resourceFileLines = IOUtils.readLines(resourceFileInputStream, UTF_8);
		resourceFileInputStream.close();

		corpusNameMap = resourceFileLines.stream()
				.filter(line -> StringUtils.isNotBlank(line))
				.map(line -> StringUtils.trim(line))
				.map(line -> {
					String[] keyValueParts = StringUtils.split(line, '\t');
					String key = StringUtils.trim(keyValueParts[0]);
					String value = StringUtils.trim(keyValueParts[1]);
					return new KeyValuePair<String, String>(key, value);
				})
				.collect(Collectors.toMap(KeyValuePair::getKey, KeyValuePair::getValue));
	}

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
			UriComponentsBuilder queryBuilder = UriComponentsBuilder
					.fromUriString(serviceUrl)
					.queryParam("corpus", corpName)
					.queryParam("cqp", queryString);

			for (Entry<String, String> queryParamEntry : defaultQueryParamsMap.entrySet()) {
				queryBuilder = queryBuilder.queryParam(queryParamEntry.getKey(), queryParamEntry.getValue());
			}

			return queryBuilder
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
		if (response.containsKey("hits")) {
			int hits = (int) response.get("hits");
			if (hits == 0) {
				return sentences;
			}
		}
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> kwics = (List<Map<String, Object>>) response.get("kwic");
		if (kwics == null) {
			return sentences;
		}

		for (Map<String, Object> kwic : kwics) {

			@SuppressWarnings("unchecked")
			Map<String, Object> match = (Map<String, Object>) kwic.get("match");
			@SuppressWarnings("unchecked")
			Map<String, Object> structs = (Map<String, Object>) kwic.get("structs");
			@SuppressWarnings("unchecked")
			List<Map<String, Object>> tokens = (List<Map<String, Object>>) kwic.get("tokens");

			CorpusSource corpusSource = composeSource(structs);
			CorpusSentence sentence = composeSentence(match, tokens);
			sentence.setSource(corpusSource);
			sentences.add(sentence);
		}
		return sentences;
	}

	private CorpusSource composeSource(Map<String, Object> structs) {

		if (structs == null) {
			return null;
		}

		String sentenceCorpus = (String) structs.get("sentence_corpus");
		String sentenceSrc = (String) structs.get("sentence_src");
		String sentenceTitle = (String) structs.get("sentence_title");
		String sentenceUrl = (String) structs.get("sentence_url");
		String displayName = null;
		CorpusSource corpusSource = null;

		if (StringUtils.isBlank(sentenceSrc)) {
			displayName = corpusNameMap.get(sentenceCorpus);
		} else {
			displayName = corpusNameMap.get(sentenceSrc);
		}
		if (StringUtils.contains(sentenceTitle, "_")) {
			sentenceTitle = StringUtils.replace(sentenceTitle, "_", " ");
		}
		sentenceTitle = handleEmptyValue(sentenceTitle);
		sentenceUrl = handleEmptyValue(sentenceUrl);
		if (StringUtils.isNotBlank(displayName)) {
			corpusSource = new CorpusSource(displayName, sentenceTitle, sentenceUrl);
			String tooltipHtml = viewUtil.getCorpusSourceTooltipHtml(corpusSource);
			corpusSource.setTooltipHtml(tooltipHtml);
		}
		return corpusSource;
	}

	private String handleEmptyValue(String value) {
		if (StringUtils.contains(value, "==NONE==")) {
			return null;
		}
		return value;
	}

	private CorpusSentence composeSentence(Map<String, Object> match, List<Map<String, Object>> tokens) {

		CorpusSentence sentence = new CorpusSentence();
		int middlePartStartIndex = (int) match.get("start");
		int middlePartEndIndex = (int) match.get("end");
		int currentWordIndex = 0;
		boolean isSkipSpace = false;

		for (Map<String, Object> token : tokens) {

			String wordValue = (String) token.get("word");
			String pos = (String) token.get("pos");
			boolean isPunctuation = ArrayUtils.contains(POS_PUNCTUATIONS, pos);
			if (currentWordIndex < middlePartStartIndex) {
				String leftPart = handleWordSpacing(sentence.getLeftPart(), wordValue, isPunctuation, isSkipSpace);
				sentence.setLeftPart(leftPart);
			} else if (currentWordIndex >= middlePartEndIndex) {
				String rightPart = handleWordSpacing(sentence.getRightPart(), wordValue, isPunctuation, isSkipSpace);
				sentence.setRightPart(rightPart);
			} else {
				String middlePart = handleWordSpacing(sentence.getMiddlePart(), wordValue, isPunctuation, isSkipSpace);
				sentence.setMiddlePart(middlePart);
			}
			isSkipSpace = StringUtils.equals(QUOTATION_MARK, wordValue);
			currentWordIndex++;
		}
		return sentence;
	}

	private String handleWordSpacing(String sentencePart, String wordValue, boolean isPunctuation, boolean isSkipSpace) {

		if (isPunctuation || isSkipSpace) {
			return sentencePart + wordValue;
		}
		return sentencePart + " " + wordValue;
	}

}
