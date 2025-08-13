package eki.common.data;

public class AsWordResult extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private String value;

	private String valueAsWord;

	private boolean valueAsWordExists;

	public AsWordResult(String value, String valueAsWord, boolean valueAsWordExists) {
		this.value = value;
		this.valueAsWord = valueAsWord;
		this.valueAsWordExists = valueAsWordExists;
	}

	public String getValue() {
		return value;
	}

	public String getValueAsWord() {
		return valueAsWord;
	}

	public boolean isValueAsWordExists() {
		return valueAsWordExists;
	}

}
