package eki.wordweb.data;

import eki.common.data.AbstractDataObject;

public class LanguageData extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private String code;

	private String codeIso2;

	private String label;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getCodeIso2() {
		return codeIso2;
	}

	public void setCodeIso2(String codeIso2) {
		this.codeIso2 = codeIso2;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

}
