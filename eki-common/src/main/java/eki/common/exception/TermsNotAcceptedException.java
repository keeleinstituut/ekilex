package eki.common.exception;

public class TermsNotAcceptedException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public TermsNotAcceptedException() {
		super();
	}

	public TermsNotAcceptedException(String message) {
		super(message);
	}

}
