package eki.common.data;

public class CodeValue extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private String code;

	private String value;

	public CodeValue() {
	}

	public CodeValue(String code, String value) {
		this.code = code;
		this.value = value;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
