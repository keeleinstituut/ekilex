package eki.common.service.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import eki.common.constant.GlobalConstant;

public abstract class AbstractJooqBugCompensator implements GlobalConstant {

	protected List<String> decode(List<String> encodedValues) {

		if (encodedValues == null) {
			return null;
		}

		List<String> decodedValues = new ArrayList<>();
		for (String value : encodedValues) {
			value = decode(value);
			decodedValues.add(value);
		}
		return decodedValues;
	}

	private String decode(String encodedValue) {

		encodedValue = StringUtils.replace(encodedValue, ENCODE_SYM_QUOTATION, "\"");
		return encodedValue;
	}

}
