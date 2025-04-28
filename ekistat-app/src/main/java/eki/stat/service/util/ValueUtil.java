package eki.stat.service.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriUtils;

import eki.common.constant.GlobalConstant;

@Component
public class ValueUtil implements GlobalConstant {

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
