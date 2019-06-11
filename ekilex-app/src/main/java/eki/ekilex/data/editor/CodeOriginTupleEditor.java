package eki.ekilex.data.editor;

import java.beans.PropertyEditorSupport;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import eki.ekilex.data.CodeOriginTuple;

@Component
public class CodeOriginTupleEditor  extends PropertyEditorSupport {

	private static final String CODE_ORIGIN_SEPARATOR = "|";

	@Override
	public String getAsText() {
		CodeOriginTuple tuple = (CodeOriginTuple)this.getValue();

		return tuple.getCode() + CODE_ORIGIN_SEPARATOR + tuple.getOrigin();

	}

	@Override
	public void setAsText(String code) throws IllegalArgumentException {
		CodeOriginTuple tuple = new CodeOriginTuple();

		if (StringUtils.isNotBlank(code) && code.contains(CODE_ORIGIN_SEPARATOR)) {
			tuple.setCode(StringUtils.substringBefore(code, CODE_ORIGIN_SEPARATOR));
			tuple.setOrigin(StringUtils.substringAfter(code, CODE_ORIGIN_SEPARATOR));
		}

		this.setValue(tuple);
	}
}
