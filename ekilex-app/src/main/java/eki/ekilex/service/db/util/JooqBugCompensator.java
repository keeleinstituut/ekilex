package eki.ekilex.service.db.util;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import eki.common.service.util.AbstractJooqBugCompensator;
import eki.ekilex.data.TypeWordRelMeaning;

@Component
public class JooqBugCompensator extends AbstractJooqBugCompensator {

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
}