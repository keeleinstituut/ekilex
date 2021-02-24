package eki.common.exception;

public class OperationDeniedException extends Exception {

	private static final long serialVersionUID = 1L;

	public OperationDeniedException() {
		super();
	}

	public OperationDeniedException(String message) {
		super(message);
	}
}
