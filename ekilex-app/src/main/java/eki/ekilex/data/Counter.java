package eki.ekilex.data;

import eki.common.data.AbstractDataObject;

public class Counter extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private int count;

	public Counter() {
		count = 0;
	}

	public int incrementAndGet() {
		return count++;
	}

	public void reset() {
		count = 0;
	}
}
