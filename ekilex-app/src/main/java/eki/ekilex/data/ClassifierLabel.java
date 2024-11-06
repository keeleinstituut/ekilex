package eki.ekilex.data;

import eki.common.constant.ClassifierName;
import eki.common.data.AbstractDataObject;

public class ClassifierLabel extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private ClassifierName classifierName;

	private String origin;

	private String code;

	private String type;

	private String lang;

	private String value;

	private String labelEst;

	private String labelEng;

	private String labelRus;

	public ClassifierName getClassifierName() {
		return classifierName;
	}

	public void setClassifierName(ClassifierName classifierName) {
		this.classifierName = classifierName;
	}

	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getLabelEst() {
		return labelEst;
	}

	public void setLabelEst(String labelEst) {
		this.labelEst = labelEst;
	}

	public String getLabelEng() {
		return labelEng;
	}

	public void setLabelEng(String labelEng) {
		this.labelEng = labelEng;
	}

	public String getLabelRus() {
		return labelRus;
	}

	public void setLabelRus(String labelRus) {
		this.labelRus = labelRus;
	}

}
