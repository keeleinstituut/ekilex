package eki.ekilex.service.db.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import eki.common.constant.GlobalConstant;
import eki.ekilex.data.TypeWordRelMeaning;

@Component
public class JooqBugCompensator implements GlobalConstant {

	public void decodeWordMeaning(List<TypeWordRelMeaning> wordMeanings) {

		if (CollectionUtils.isEmpty(wordMeanings)) {
			return;
		}

		for (TypeWordRelMeaning wordMeaning : wordMeanings) {
			List<String> definitions = wordMeaning.getDefinitionValues();
			definitions = decode(definitions);
			wordMeaning.setDefinitionValues(definitions);
		}
	}

	private List<String> decode(List<String> encodedValues) {

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