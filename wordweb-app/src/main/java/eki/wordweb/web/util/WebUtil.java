package eki.wordweb.web.util;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriUtils;

import eki.common.constant.GlobalConstant;
import eki.wordweb.constant.WebConstant;

@Component
public class WebUtil implements WebConstant, GlobalConstant {

	public String composeDetailSearchUri(String destinLangsStr, String datasetCodesStr, String word, Integer homonymNr) {
		String encodedWord = UriUtils.encode(word, UTF_8);
		String encodedDatasetCodesStr = encodeSeparatedValuesStr(datasetCodesStr);
		String searchUri = SEARCH_URI + UNIF_URI + "/" + destinLangsStr + "/" + encodedDatasetCodesStr + "/" + encodedWord;
		if (homonymNr != null) {
			searchUri += "/" + homonymNr;
		}
		return searchUri;
	}

	public String composeSimpleSearchUri(String destinLangsStr, String word, Integer homonymNr) {
		String encodedWord = UriUtils.encode(word, UTF_8);
		String searchUri = SEARCH_URI + LITE_URI + "/" + destinLangsStr + "/" + encodedWord;
		if (homonymNr != null) {
			searchUri += "/" + homonymNr;
		}
		return searchUri;
	}

	public String encodeSeparatedValuesStr(String separatedValuesStr) {
		String[] valuesArr = StringUtils.split(separatedValuesStr, UI_FILTER_VALUES_SEPARATOR);
		String encodedSeparatedValuesStr = Arrays.stream(valuesArr)
				.map(value -> UriUtils.encode(value, UTF_8))
				.collect(Collectors.joining(String.valueOf(UI_FILTER_VALUES_SEPARATOR)));
		return encodedSeparatedValuesStr;
	}
}
