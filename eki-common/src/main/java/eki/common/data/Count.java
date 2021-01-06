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

	public synchronized long increment() {
		return this.value++;
	}

	public synchronized long increment(int byMuch) {
		return this.value += byMuch;
	}

	public synchronized long decrement() {
		return this.value--;
	}

	public synchronized long decrement(int byMuch) {
		return this.value -= byMuch;
	}

	public long getValue() {
		return this.value;
	}
}
