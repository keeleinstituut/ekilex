package eki.ekilex.data.migra;

import eki.common.data.AbstractDataObject;

public class MigraForm extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long formId;

	private String value;

	private String morphCode;

	public Long getFormId() {
		return formId;
	}

	public void setFormId(Long formId) {
		this.formId = formId;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getMorphCode() {
		return morphCode;
	}

	public void setMorphCode(String morphCode) {
		this.morphCode = morphCode;
	}

}
