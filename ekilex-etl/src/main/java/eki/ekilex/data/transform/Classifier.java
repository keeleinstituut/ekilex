package eki.ekilex.data.transform;

import eki.common.data.AbstractDataObject;

public class Classifier extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private String name;

	private String code;

	private String origin;

	private String parent;

	private String value;

	private String valueType;

	private String valueLang;

	private int order;

	private String key;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

	public String getParent() {
		return parent;
	}

	public void setParent(String parent) {
		this.parent = parent;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getValueType() {
		return valueType;
	}

	public void setValueType(String valueType) {
		this.valueType = valueType;
	}

	public String getValueLang() {
		return valueLang;
	}

	public void setValueLang(String valueLang) {
		this.valueLang = valueLang;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

}
