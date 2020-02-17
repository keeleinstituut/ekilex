package eki.wordweb.web.util;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriUtils;

import eki.wordweb.constant.SystemConstant;
import eki.wordweb.constant.WebConstant;

@Component
public class WebUtil implements WebConstant, SystemConstant {

	public String composeSearchUri(String destinLangsStr, String datasetCodesStr, String searchMode, String word, Integer homonymNr) {
		String encodedWord = UriUtils.encode(word, SystemConstant.UTF_8);
		String encodedDatasetCodesStr = encodeSeparatedValuesStr(datasetCodesStr);
		String searchUri = SEARCH_URI + UNIF_URI + "/" + destinLangsStr + "/" + encodedDatasetCodesStr + "/" + searchMode + "/" + encodedWord;
		if (homonymNr != null) {
			searchUri += "/" + homonymNr;
		}
		return searchUri;
	}

	public String encodeSeparatedValuesStr(String separatedValuesStr) {
		String[] valuesArr = StringUtils.split(separatedValuesStr, UI_FILTER_VALUES_SEPARATOR);
		String encodedSeparatedValuesStr = Arrays.stream(valuesArr)
				.map(value -> UriUtils.encode(value, SystemConstant.UTF_8))
				.collect(Collectors.joining(String.valueOf(UI_FILTER_VALUES_SEPARATOR)));
		return encodedSeparatedValuesStr;
	}
}
