package eki.common.exception;

public class ConstraintViolationException extends Exception {

	private static final long serialVersionUID = 1L;

	public ConstraintViolationException(String message) {
		super(message);
	}
}
