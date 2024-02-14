package eki.ekilex.log;

import ch.qos.logback.classic.PatternLayout;

public class EkiUserLogPatternLayout extends PatternLayout {

	static {
		PatternLayout.DEFAULT_CONVERTER_MAP.put("user", EkiUserLogConverter.class.getName());
	}
}
