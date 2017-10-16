package eki.common.data;

public class Count extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private int value;

	public Count(int value) {
		this.value = value;
	}

	public Count() {
		this.value = 0;
	}

	public void increment() {
		this.value++;
	}

	public void increment(int byMuch) {
		this.value += byMuch;
	}

	public int getValue() {
		return value;
	}
}
