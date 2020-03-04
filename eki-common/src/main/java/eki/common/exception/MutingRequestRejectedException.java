package eki.common.exception;

import org.springframework.security.web.firewall.RequestRejectedException;

public class MutingRequestRejectedException extends RequestRejectedException {

	private static final long serialVersionUID = 1L;

	public MutingRequestRejectedException(String message) {
		super(message);
	}

	@Override
	public synchronized Throwable fillInStackTrace() {
		return this;
	}

}
