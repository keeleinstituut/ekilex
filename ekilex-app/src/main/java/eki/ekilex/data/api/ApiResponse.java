package eki.ekilex.data.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import eki.common.data.AbstractDataObject;

@JsonInclude(Include.NON_NULL)
public class ApiResponse extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private boolean success;

	private String message;

	private String tableName;

	private Long id;

	public ApiResponse() {
	}

	public ApiResponse(boolean success, String message) {
		this.success = success;
		this.message = message;
	}

	public ApiResponse(boolean success, String message, String tableName, Long id) {
		this.success = success;
		this.message = message;
		this.tableName = tableName;
		this.id = id;
	}

	public boolean isSuccess() {
		return success;
	}

	public String getMessage() {
		return message;
	}

	public String getTableName() {
		return tableName;
	}

	public Long getId() {
		return id;
	}

}
