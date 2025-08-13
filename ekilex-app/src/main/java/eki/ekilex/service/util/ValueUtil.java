package eki.ekilex.service.util;

import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriUtils;

import eki.common.constant.GlobalConstant;
import eki.common.service.TextDecorationService;
import eki.ekilex.constant.SystemConstant;
import eki.ekilex.data.WordLexemeMeaningDetails;

@Component
public class ValueUtil implements SystemConstant, GlobalConstant {

	@Autowired
	private TextDecorationService textDecorationService;

	public String trimAndClean(String value) {

		String cleanValue = StringUtils.trim(value);
		if (StringUtils.isBlank(cleanValue)) {
			return cleanValue;
		}
		cleanValue = RegExUtils.replaceAll(cleanValue, "\\p{C}", "");
		if (StringUtils.isBlank(cleanValue)) {
			return cleanValue;
		}
		StringBuilder valueBuilder = new StringBuilder();
		char[] valueChars = cleanValue.toCharArray();
		char prevChar = 0;
		for (char valueChar : valueChars) {
			if (valueChar == '\r') {
				valueChar = ' ';
			}
			if (valueChar == '\n') {
				valueChar = ' ';
			}
			if (valueChar == '\t') {
				valueChar = ' ';
			}
			if (valueChar == '\u00A0') { // non-breaking space as unicode
				valueChar = ' ';
			}
			if (valueChar == 160) { // non-breaking space as char map code
				valueChar = ' ';
			}
			if (valueChar == ' ' && prevChar == ' ') {
				continue;
			}
			valueBuilder.append(valueChar);
			prevChar = valueChar;
		}
		cleanValue = valueBuilder.toString();
		cleanValue = StringUtils.trim(cleanValue);
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
		return textDecorationService.unifySymbols(value);
	}

	public String encode(String value) {
		value = StringUtils.replace(value, "/", ENCODE_SYM_SLASH);
		value = StringUtils.replace(value, "\\", ENCODE_SYM_BACKSLASH);
		value = StringUtils.replace(value, "%", ENCODE_SYM_PERCENT);
		value = UriUtils.encode(value, UTF_8);
		return value;
	}

	public String decode(String value) {
		value = UriUtils.decode(value, UTF_8);
		value = StringUtils.replace(value, ENCODE_SYM_SLASH, "/");
		value = StringUtils.replace(value, ENCODE_SYM_BACKSLASH, "\\");
		value = StringUtils.replace(value, ENCODE_SYM_PERCENT, "%");
		return value;
	}
}
