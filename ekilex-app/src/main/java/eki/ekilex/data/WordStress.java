package eki.ekilex.data;

import eki.common.data.AbstractDataObject;

public class WordStress extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long formId;

	private String valuePrese;

	private String displayForm;

	public Long getFormId() {
		return formId;
	}

	public void setFormId(Long formId) {
		this.formId = formId;
	}

	public String getValuePrese() {
		return valuePrese;
	}

	public void setValuePrese(String valuePrese) {
		this.valuePrese = valuePrese;
	}

	public String getDisplayForm() {
		return displayForm;
	}

	public void setDisplayForm(String displayForm) {
		this.displayForm = displayForm;
	}
}
