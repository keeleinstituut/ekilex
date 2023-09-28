package eki.wordweb.web.util;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriUtils;

import eki.common.constant.GlobalConstant;
import eki.wordweb.constant.SystemConstant;
import eki.wordweb.constant.WebConstant;

@Component
public class WebUtil implements WebConstant, SystemConstant, GlobalConstant {

	private static final String MEANING_ID_URL_PLACEHOLDER = "{meaningId}";

	private static final String WORD_URL_PLACEHOLDER = "{word}";

	@Value("${ekilex.limterm.details.url}")
	private String ekilexLimTermDetailsUrl;

	@Value("${corpus.service.rus.url}")
	private String corpusServiceRusUrl;

	public boolean isMaskedSearchCrit(String searchWord) {
		if (StringUtils.containsAny(searchWord, SEARCH_MASK_CHARS, SEARCH_MASK_CHAR)) {
			return true;
		}
		return false;
	}

	public String composeDetailSearchUri(String destinLangsStr, String datasetCodesStr, String word, Integer homonymNr) {
		String encodedWord = encode(word);
		String encodedDatasetCodesStr = encodeSeparatedValuesStr(datasetCodesStr);
		String searchUri = StringUtils.join(SEARCH_URI, UNIF_URI, '/', destinLangsStr, '/', encodedDatasetCodesStr, '/', encodedWord);
		if ((homonymNr != null)) {
			searchUri += "/" + homonymNr;
		}
		return searchUri;
	}

	public String composeSimpleSearchUri(String destinLangsStr, String word, Integer homonymNr) {
		String encodedWord = encode(word);
		String searchUri = StringUtils.join(SEARCH_URI, LITE_URI, '/', destinLangsStr, '/', encodedWord);
		if (homonymNr != null) {
			searchUri += "/" + homonymNr;
		}
		return searchUri;
	}

	public String composeDatasetFirstLetterSearchUri(String datasetCode, Character firstLetter) {
		String uri = StringUtils.join(DATASET_HOME_URI, '/', datasetCode, '/', firstLetter);
		return uri;
	}

	public String composeEkilexLimTermDetailsUrl(Long meaningId) {
		String limTermDetailsUrl = StringUtils.replace(ekilexLimTermDetailsUrl, MEANING_ID_URL_PLACEHOLDER, String.valueOf(meaningId));
		return limTermDetailsUrl;
	}

	public String getEkilexLimTermSearchUrl() {
		String limTermSearchUrl = StringUtils.substringBefore(ekilexLimTermDetailsUrl, "?");
		return limTermSearchUrl;
	}

	public String composeRusCorpWordUrl(String word) {
		String rusCorpWordUrl = StringUtils.replace(corpusServiceRusUrl, WORD_URL_PLACEHOLDER, word);
		return rusCorpWordUrl;
	}

	private String encodeSeparatedValuesStr(String separatedValuesStr) {
		String[] valuesArr = StringUtils.split(separatedValuesStr, UI_FILTER_VALUES_SEPARATOR);
		String encodedSeparatedValuesStr = Arrays.stream(valuesArr)
				.map(value -> UriUtils.encode(value, UTF_8))
				.collect(Collectors.joining(String.valueOf(UI_FILTER_VALUES_SEPARATOR)));
		return encodedSeparatedValuesStr;
	}

	private String encode(String value) {
		value = StringUtils.replace(value, "/", ENCODE_SYM_SLASH);
		value = StringUtils.replace(value, "\\", ENCODE_SYM_BACKSLASH);
		value = StringUtils.replace(value, "%", ENCODE_SYM_PERCENT);
		value = UriUtils.encode(value, UTF_8);
		return value;
	}
}
