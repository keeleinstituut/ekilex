package eki.wordweb.web.controller;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriUtils;

import eki.wordweb.web.util.StatDataUtil;

public abstract class AbstractSearchController extends AbstractController {

	@Autowired
	protected StatDataUtil statDataUtil;

	protected boolean isSearchForm(Model model) {
		Boolean isSearchForm = (Boolean) model.asMap().get(SEARCH_FORM);
		return BooleanUtils.toBoolean(isSearchForm);
	}

	protected void setSearchFormAttribute(RedirectAttributes redirectAttributes, boolean isSearchForm) {
		redirectAttributes.addFlashAttribute(SEARCH_FORM, isSearchForm);
	}

	protected Integer nullSafe(String value) {
		if (StringUtils.isBlank(value)) {
			return null;
		}
		if (!StringUtils.isNumeric(value)) {
			return null;
		}
		return Integer.valueOf(value);
	}

	protected String cleanupMain(String value) {

		value = StringUtils.trim(value);
		value = StringUtils.remove(value, '%');
		if (StringUtils.isBlank(value)) {
			return value;
		}
		while (StringUtils.endsWith(value, "-")) {
			value = StringUtils.removeEnd(value, "-");
		}
		while (StringUtils.startsWith(value, "-")) {
			value = StringUtils.removeStart(value, "-");
		}
		if (StringUtils.length(value) > SEARCH_WORD_MAX_LENGTH) {
			value = StringUtils.left(value, SEARCH_WORD_MAX_LENGTH);
		}
		return value;
	}

	protected String cleanupBasic(String value) {

		value = StringUtils.trim(value);
		value = StringUtils.remove(value, '%');
		if (StringUtils.isBlank(value)) {
			return value;
		}
		if (StringUtils.length(value) > SEARCH_WORD_MAX_LENGTH) {
			value = StringUtils.left(value, SEARCH_WORD_MAX_LENGTH);
		}
		return value;
	}

	protected String cleanupMask(String searchWord) {

		if (StringUtils.isBlank(searchWord)) {
			return searchWord;
		}
		searchWord = StringUtils.trim(searchWord);
		char wildcardChar = SEARCH_MASK_CHARS.charAt(0);
		char[] searchWordChars = searchWord.toCharArray();
		StringBuilder buf = new StringBuilder();
		char prevChar = ' ';
		for (char currChar : searchWordChars) {
			if ((currChar == wildcardChar) && (currChar == prevChar)) {
				continue;
			}
			buf.append(currChar);
			prevChar = currChar;
		}
		searchWord = buf.toString();
		return searchWord;
	}

	protected boolean isValidMaskedSearch(String cleanMaskSearchWord) {

		if (StringUtils.isBlank(cleanMaskSearchWord)) {
			return false;
		}
		char searchMaskCharsChar = SEARCH_MASK_CHARS.charAt(0);
		char searchMaskCharChar = SEARCH_MASK_CHAR.charAt(0);
		String testSearchWord = cleanMaskSearchWord;
		testSearchWord = StringUtils.remove(testSearchWord, searchMaskCharsChar);
		testSearchWord = StringUtils.remove(testSearchWord, searchMaskCharChar);
		if (StringUtils.isBlank(testSearchWord)) {
			return false;
		}
		if (StringUtils.containsOnly(cleanMaskSearchWord, searchMaskCharsChar, searchMaskCharChar)) {
			return false;
		}
		return true;
	}

	protected String decode(String value) {

		if (StringUtils.isBlank(value)) {
			return value;
		}
		value = StringUtils.remove(value, '%');
		value = UriUtils.decode(value, UTF_8);
		value = StringUtils.replace(value, ENCODE_SYM_SLASH, "/");
		value = StringUtils.replace(value, ENCODE_SYM_BACKSLASH, "\\");
		value = StringUtils.replace(value, ENCODE_SYM_PERCENT, "%");
		return value;
	}

}
