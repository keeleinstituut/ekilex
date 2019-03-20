package eki.ekilex.runner.util;

import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class TermLoaderHelper {

	private static final String NUMBERED_BULLET_SEPARATOR = ".";

	private static final String LISTING_SYM = ";";

	private static final String REF_START_SYM = "[";

	private static final String REF_END_SYM = "]";

	private static final String[] REPLACED_SYMS = new String[] {"<", ">", "&", "%"};

	public boolean isReplacedContent(String value) {
		if (StringUtils.length(value) > 1) {
			return false;
		}
		return Arrays.stream(REPLACED_SYMS).anyMatch(sym -> StringUtils.equals(sym, value));
	}

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
		if (refEndPos == -1) {
			return false;
		}
		int refStartPos = StringUtils.indexOf(value, REF_START_SYM);
		if ((refStartPos > -1) && (refStartPos < refEndPos)) {
			return false;
		}
		return true;
	}

	public String collectMinorRef(String value) {
		if (!isRefEnd(value)) {
			return null;
		}
		String minorRef = StringUtils.substringBefore(value, REF_END_SYM);
		return minorRef;
	}

	public String cleanupResidue(String value) {
		value = StringUtils.trim(value);
		if (StringUtils.endsWith(value, LISTING_SYM)) {
			value = StringUtils.substringBeforeLast(value, LISTING_SYM);
		}
		if (StringUtils.startsWith(value, LISTING_SYM)) {
			value = StringUtils.substringAfter(value, LISTING_SYM);
		}
		return value;
	}
}
