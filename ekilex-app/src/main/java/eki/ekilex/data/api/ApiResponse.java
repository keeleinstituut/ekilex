package eki.ekilex.data.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import eki.common.data.AbstractDataObject;

@JsonInclude(Include.NON_NULL)
public class ApiResponse extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private boolean success;

	private String message;

	private Long id;

	public ApiResponse() {
	}

	public ApiResponse(boolean success, String message) {
		this.success = success;
		this.message = message;
	}

	public ApiResponse(boolean success) {
		this.success = success;
	}

	public ApiResponse(boolean success, Long id) {
		this.success = success;
		this.id = id;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
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

}
