package eki.common.util;

import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

public final class CodeGenerator {

	public static String generateUniqueId() {
		String code = UUID.randomUUID().toString();
		code = StringUtils.remove(code, '-');
		return code;
	}

	public static String generateTimestampCode() {
		return String.valueOf(System.currentTimeMillis());
	}

}
