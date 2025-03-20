package eki.ekilex.data;

import eki.common.data.AbstractDataObject;
import eki.ekilex.constant.ResponseStatus;

public class Response extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private ResponseStatus status;

	private String message;

	private Long id;

	private Long id2;

	public ResponseStatus getStatus() {
		return status;
	}

	public void setStatus(ResponseStatus status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getId2() {
		return id2;
	}

	public void setId2(Long id2) {
		this.id2 = id2;
	}
}
