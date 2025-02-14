package eki.common.data;

public class ValueCount extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private String value;

	private Integer count;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

}
