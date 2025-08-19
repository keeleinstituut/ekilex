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

	private static final String URL_PLACEHOLDER_MEANING_ID = "{meaningId}";

	private static final String URL_PLACEHOLDER_WORD = "{word}";

	private static final String URL_PLACEHOLDER_LANG = "{lang}";

	@Value("${ekilex.limterm.details.url}")
	private String ekilexLimTermDetailsUrl;

	@Value("${eki.oldskool.rus.dict.url}")
	private String ekiOldskoolRusDictUrl;

	@Value("${skell.service.url}")
	private String skellServiceUrl;

	@Value("${iate.service.url}")
	private String iateServiceUrl;

	public boolean isMaskedSearchCrit(String searchWord) {
		if (StringUtils.containsAny(searchWord, SEARCH_MASK_CHARS, SEARCH_MASK_CHAR)) {
			return true;
		}
		return false;
	}

	public String composeDetailSearchUri(String destinLangsStr, String datasetCodesStr, String wordValue, Integer homonymNr, String lang) {
		String encodedWordValue = encode(wordValue);
		String encodedDatasetCodesStr = encodeSeparatedValuesStr(datasetCodesStr);
		String searchUri;
		if (homonymNr == null) {
			searchUri = StringUtils.join(SEARCH_URI, UNIF_URI, '/', destinLangsStr, '/', encodedDatasetCodesStr, '/', encodedWordValue);
		} else if (StringUtils.isBlank(lang)) {
			searchUri = StringUtils.join(SEARCH_URI, UNIF_URI, '/', destinLangsStr, '/', encodedDatasetCodesStr, '/', encodedWordValue);
		} else {
			searchUri = StringUtils.join(SEARCH_URI, UNIF_URI, '/', destinLangsStr, '/', encodedDatasetCodesStr, '/', encodedWordValue, '/', homonymNr, '/', lang);
		}
		return searchUri;
	}

	public String composeSimpleSearchUri(String destinLangsStr, String wordValue, Integer homonymNr, String lang) {
		String encodedWordValue = encode(wordValue);
		String searchUri;
		if (homonymNr == null) {
			searchUri = StringUtils.join(SEARCH_URI, LITE_URI, '/', destinLangsStr, '/', encodedWordValue);
		} else if (StringUtils.isBlank(lang)) {
			searchUri = StringUtils.join(SEARCH_URI, LITE_URI, '/', destinLangsStr, '/', encodedWordValue);
		} else {
			searchUri = StringUtils.join(SEARCH_URI, LITE_URI, '/', destinLangsStr, '/', encodedWordValue, '/', homonymNr, '/', lang);
		}
		return searchUri;
	}

	public String composeOsSearchUri(String searchValue, Integer homonymNr) {
		String encodedWordValue = encode(searchValue);
		String searchUri;
		if (homonymNr == null) {
			searchUri = StringUtils.join(SEARCH_URI, OS_URI, '/', encodedWordValue);
		} else {
			searchUri = StringUtils.join(SEARCH_URI, OS_URI, '/', encodedWordValue, '/', homonymNr);
		}
		return searchUri;
	}

	public String composeDatasetFirstLetterSearchUri(String datasetCode, Character firstLetter) {
		String uri = StringUtils.join(DATASET_HOME_URI, '/', datasetCode, '/', firstLetter);
		return uri;
	}

	public String composeEkilexLimTermDetailsUrl(Long meaningId) {
		String limTermDetailsUrl = new String(ekilexLimTermDetailsUrl);
		limTermDetailsUrl = StringUtils.replace(limTermDetailsUrl, URL_PLACEHOLDER_MEANING_ID, String.valueOf(meaningId));
		return limTermDetailsUrl;
	}

	public String getEkilexLimTermSearchUrl() {
		String limTermSearchUrl = new String(ekilexLimTermDetailsUrl);
		limTermSearchUrl = StringUtils.substringBefore(limTermSearchUrl, "?");
		return limTermSearchUrl;
	}

	public String composeIateSearchUrl(String wordValue, String langIso2) {
		String iateSearchdUrl = new String(iateServiceUrl);
		iateSearchdUrl = StringUtils.replace(iateSearchdUrl, URL_PLACEHOLDER_WORD, wordValue);
		iateSearchdUrl = StringUtils.replace(iateSearchdUrl, URL_PLACEHOLDER_LANG, langIso2);
		return iateSearchdUrl;
	}

	public String composeEkiOldskoolRusDictUrl(String wordValue) {
		String ekiDictSearchUrl = new String(ekiOldskoolRusDictUrl);
		ekiDictSearchUrl = StringUtils.replace(ekiDictSearchUrl, URL_PLACEHOLDER_WORD, wordValue);
		return ekiDictSearchUrl;
	}

	public String composeSkellSearchUrl(String wordValue, String langIso2) {
		String skellServiceUrlWithParams = new String(skellServiceUrl);
		skellServiceUrlWithParams = StringUtils.replace(skellServiceUrlWithParams, URL_PLACEHOLDER_WORD, wordValue);
		skellServiceUrlWithParams = StringUtils.replace(skellServiceUrlWithParams, URL_PLACEHOLDER_LANG, langIso2);
		return skellServiceUrlWithParams;
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
