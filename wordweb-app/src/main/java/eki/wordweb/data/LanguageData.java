package eki.wordweb.data;

import eki.common.data.AbstractDataObject;

public class LanguageData extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private String code;

	private String codeIso2;

	private String label;

	private boolean defaultLang;

	private String imageName;

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

	public boolean isDefaultLang() {
		return defaultLang;
	}

	public void setDefaultLang(boolean defaultLang) {
		this.defaultLang = defaultLang;
	}

	public String getImageName() {
		return imageName;
	}

	public void setImageName(String imageName) {
		this.imageName = imageName;
	}

	public void setCode(String code, String codeIso2, String label) {
		this.code = code;
		this.codeIso2 = codeIso2;
		this.label = label;
	}

	public void setImage(String code, String imageName, String label) {
		this.code = code;
		this.imageName = imageName;
		this.label = label;
	}
}
