package eki.ekilex.runner.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class EstermLoaderHelper {

	private static final String NUMBERED_BULLET_SEPARATOR = ".";

	private static final String LISTING_SYM = ";";

	private static final String REF_START_SYM = "[";

	private static final String REF_END_SYM = "]";

	private static final int MAX_SMALL_REF_LENGTH = 20;

	//TODO maybe? maybe not?
	public boolean isBulleted(String value) {
		value = StringUtils.substringAfter(value, REF_END_SYM);
		value = StringUtils.trim(value);
		String bulletValue = StringUtils.substringBefore(value, NUMBERED_BULLET_SEPARATOR);
		boolean isNumbered = StringUtils.isNumeric(bulletValue);
		return isNumbered;
	}

	public boolean isListing(String value) {
		value = StringUtils.trim(value);
		return StringUtils.equals(value, LISTING_SYM);
	}

	public String getContent(String value) {
		value = StringUtils.trim(value);
		if (isRefEnd(value)) {
			value = StringUtils.substringAfter(value, REF_END_SYM);
		}
		if (isRefStart(value)) {
			value = StringUtils.substringBeforeLast(value, REF_START_SYM);
		}
		value = StringUtils.trim(value);
		return value;
	}

	public boolean isRefStart(String value) {
		value = StringUtils.trim(value);
		boolean isRefStart = StringUtils.endsWith(value, REF_START_SYM);
		return isRefStart;
	}

	public boolean isRefEnd(String value) {
		value = StringUtils.trim(value);
		int refEndPos = StringUtils.indexOf(value, REF_END_SYM);
		int refStartPos = StringUtils.indexOf(value, REF_START_SYM);
		if (refStartPos < refEndPos) {
			return false;
		}
		boolean isRefEnd = StringUtils.contains(value, REF_END_SYM);
		return isRefEnd;
	}

	public String collectSmallRef(String value) {
		String smallRef = StringUtils.substringBefore(value, REF_END_SYM);
		return smallRef;
	}
}
