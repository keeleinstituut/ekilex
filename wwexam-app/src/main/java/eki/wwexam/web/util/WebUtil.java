package eki.wwexam.web.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriUtils;

import eki.common.constant.GlobalConstant;
import eki.wwexam.constant.SystemConstant;
import eki.wwexam.constant.WebConstant;

@Component
public class WebUtil implements WebConstant, SystemConstant, GlobalConstant {

	public boolean isMaskedSearchCrit(String searchWord) {
		if (StringUtils.containsAny(searchWord, SEARCH_MASK_CHARS, SEARCH_MASK_CHAR)) {
			return true;
		}
		return false;
	}

	public String composeSearchUri(String searchValue, Integer homonymNr) {
		String encodedWordValue = encode(searchValue);
		String searchUri;
		if (homonymNr == null) {
			searchUri = StringUtils.join(SEARCH_URI, '/', encodedWordValue);
		} else {
			searchUri = StringUtils.join(SEARCH_URI, '/', encodedWordValue, '/', homonymNr);
		}
		return searchUri;
	}

	private String encode(String value) {
		value = StringUtils.replace(value, "/", ENCODE_SYM_SLASH);
		value = StringUtils.replace(value, "\\", ENCODE_SYM_BACKSLASH);
		value = StringUtils.replace(value, "%", ENCODE_SYM_PERCENT);
		value = UriUtils.encode(value, UTF_8);
		return value;
	}
}
