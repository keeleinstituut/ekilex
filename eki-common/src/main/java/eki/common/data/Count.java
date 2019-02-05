package eki.common.data;

public class Count extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private long value;

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

	public long getValue() {
		return value;
	}
}
