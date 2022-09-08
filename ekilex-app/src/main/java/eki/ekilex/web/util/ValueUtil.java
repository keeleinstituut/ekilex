package eki.ekilex.web.util;

import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.service.TextDecorationService;
import eki.ekilex.constant.SystemConstant;
import eki.ekilex.data.WordLexemeMeaningDetails;

@Component
public class ValueUtil implements SystemConstant {

	@Autowired
	private TextDecorationService textDecorationService;

	private String trimAndClean(String value) {
		if (StringUtils.isBlank(value)) {
			return StringUtils.trim(value);
		}
		String cleanValue = StringUtils.trim(value);
		cleanValue = RegExUtils.replaceAll(cleanValue, "\\p{C}", "");
		StringBuilder valueBuilder = new StringBuilder();
		char[] valueChars = cleanValue.toCharArray();
		char prevChar = 0;
		for (char valueChar : valueChars) {
			if (valueChar == ' ' && prevChar == ' ') {
				continue;
			}
			if (valueChar == '\r') {
				continue;
			}
			if (valueChar == '\n') {
				continue;
			}
			if (valueChar == '\t') {
				continue;
			}
			valueBuilder.append(valueChar);
			prevChar = valueChar;
		}
		cleanValue = valueBuilder.toString();
		return cleanValue;
	}

	public String trimAndCleanAndRemoveHtmlAndLimit(String value) {
		value = trimAndClean(value);
		value = textDecorationService.removeHtmlAndSkipEkiElementMarkup(value);
		if (StringUtils.length(value) > MAX_TEXT_LENGTH_LIMIT) {
			value = StringUtils.substring(value, 0, MAX_TEXT_LENGTH_LIMIT);
		}
		return value;
	}

	public void trimAndCleanAndRemoveHtml(WordLexemeMeaningDetails details) {

		String wordValue = details.getWordValue();
		wordValue = trimAndCleanAndRemoveHtmlAndLimit(wordValue);
		details.setWordValue(wordValue);

		String wordValuePrese = details.getWordValuePrese();
		wordValuePrese = trimAndCleanAndRemoveHtmlAndLimit(wordValuePrese);
		details.setWordValuePrese(wordValuePrese);
	}

	public String unifyToApostrophe(String value) {
		return textDecorationService.unifyToApostrophe(value);
	}
}
