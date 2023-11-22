package eki.common.data;

public class IntWrap extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private int value;

	public IntWrap() {
		this.value = 0;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public void setMax(int otherValue) {
		if (otherValue > value) {
			this.value = otherValue;
		}
	}
}
