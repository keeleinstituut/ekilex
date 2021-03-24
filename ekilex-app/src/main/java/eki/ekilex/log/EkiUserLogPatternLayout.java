package eki.ekilex.log;

import ch.qos.logback.classic.PatternLayout;

public class EkiUserLogPatternLayout extends PatternLayout {

	static {
		PatternLayout.defaultConverterMap.put("user", EkiUserLogConverter.class.getName());
	}
}
