package eki.common.exception;

public class NotSingleResultQueryException extends Exception {

	private static final long serialVersionUID = 1L;

	public NotSingleResultQueryException() {
		super();
	}

	public NotSingleResultQueryException(String message) {
		super(message);
	}
}
