package eki.ekilex.data.transform;

import eki.common.data.AbstractDataObject;

public class Classifier extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private String ekiType;

	private String ekiName;

	private String ekiCode;

	private String ekiValue;

	private String ekiValueLang;

	private String lexName;

	private String lexCode;

	private String lexValue;

	private String lexValueLang;

	private String lexValueType;

	private int order;

	private String ekiKey;

	public String getEkiType() {
		return ekiType;
	}

	public void setEkiType(String ekiType) {
		this.ekiType = ekiType;
	}

	public String getEkiName() {
		return ekiName;
	}

	public void setEkiName(String ekiName) {
		this.ekiName = ekiName;
	}

	public String getEkiCode() {
		return ekiCode;
	}

	public void setEkiCode(String ekiCode) {
		this.ekiCode = ekiCode;
	}

	public String getEkiValue() {
		return ekiValue;
	}

	public void setEkiValue(String ekiValue) {
		this.ekiValue = ekiValue;
	}

	public String getEkiValueLang() {
		return ekiValueLang;
	}

	public void setEkiValueLang(String ekiValueLang) {
		this.ekiValueLang = ekiValueLang;
	}

	public String getLexName() {
		return lexName;
	}

	public void setLexName(String lexName) {
		this.lexName = lexName;
	}

	public String getLexCode() {
		return lexCode;
	}

	public void setLexCode(String lexCode) {
		this.lexCode = lexCode;
	}

	public String getLexValue() {
		return lexValue;
	}

	public void setLexValue(String lexValue) {
		this.lexValue = lexValue;
	}

	public String getLexValueLang() {
		return lexValueLang;
	}

	public void setLexValueLang(String lexValueLang) {
		this.lexValueLang = lexValueLang;
	}

	public String getLexValueType() {
		return lexValueType;
	}

	public void setLexValueType(String lexValueType) {
		this.lexValueType = lexValueType;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public String getEkiKey() {
		return ekiKey;
	}

	public void setEkiKey(String ekiKey) {
		this.ekiKey = ekiKey;
	}

}
