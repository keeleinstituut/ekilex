package eki.wordweb.service.util;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import eki.wordweb.data.WordTypeData;
import eki.wordweb.data.type.TypeCollocMember;
import eki.wordweb.data.type.TypeDefinition;
import eki.wordweb.data.type.TypeFreeform;
import eki.wordweb.data.type.TypeUsage;

@Component
public class JooqBugCompensator {

	public void trimWordTypeData(List<? extends WordTypeData> list) {

		if (CollectionUtils.isEmpty(list)) {
			return;
		}

		String value;
		for (WordTypeData dataRow : list) {
			value = dataRow.getWord();
			value = StringUtils.trim(value);
			dataRow.setWord(value);
			value = dataRow.getWordPrese();
			value = StringUtils.trim(value);
			dataRow.setWordPrese(value);
		}
	}

	public void trimDefinitions(List<TypeDefinition> definitions) {

		if (CollectionUtils.isEmpty(definitions)) {
			return;
		}

		String value;
		for (TypeDefinition definition : definitions) {
			value = definition.getValue();
			value = StringUtils.trim(value);
			definition.setValue(value);
			value = definition.getValuePrese();
			value = StringUtils.trim(value);
			definition.setValuePrese(value);
		}
	}

	public void trimUsages(List<TypeUsage> usages) {

		if (CollectionUtils.isEmpty(usages)) {
			return;
		}

		String value;
		for (TypeUsage usage : usages) {
			value = usage.getUsage();
			value = StringUtils.trim(value);
			usage.setUsage(value);
			value = usage.getUsagePrese();
			value = StringUtils.trim(value);
			usage.setUsagePrese(value);
		}
	}

	public void trimCollocMembers(List<TypeCollocMember> collocMembers) {

		if (CollectionUtils.isEmpty(collocMembers)) {
			return;
		}

		String value;
		for (TypeCollocMember collocMember : collocMembers) {
			value = collocMember.getWord();
			value = StringUtils.trim(value);
			collocMember.setWord(value);
			value = collocMember.getForm();
			value = StringUtils.trim(value);
			collocMember.setForm(value);
		}
	}

	public void trimFreeforms(List<TypeFreeform> freeforms) {

		if (CollectionUtils.isEmpty(freeforms)) {
			return;
		}

		String value;
		for (TypeFreeform freeform : freeforms) {
			value = freeform.getValue();
			value = StringUtils.trim(value);
			freeform.setValue(value);
		}
	}
}
