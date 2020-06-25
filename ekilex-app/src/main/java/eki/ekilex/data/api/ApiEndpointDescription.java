package eki.ekilex.data.api;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import eki.common.data.AbstractDataObject;

public class ApiEndpointDescription extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	@JsonIgnore
	private int order;

	private List<String> uriPatterns;

	@JsonInclude(Include.NON_EMPTY)
	private List<String> pathVariables;

	@JsonInclude(Include.NON_EMPTY)
	private List<String> requestParameters;

	@JsonInclude(Include.NON_NULL)
	private String requestBody;

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public List<String> getUriPatterns() {
		return uriPatterns;
	}

	public void setUriPatterns(List<String> uriPatterns) {
		this.uriPatterns = uriPatterns;
	}

	public List<String> getPathVariables() {
		return pathVariables;
	}

	public void setPathVariables(List<String> pathVariables) {
		this.pathVariables = pathVariables;
	}

	public List<String> getRequestParameters() {
		return requestParameters;
	}

	public void setRequestParameters(List<String> requestParameters) {
		this.requestParameters = requestParameters;
	}

	public String getRequestBody() {
		return requestBody;
	}

	public void setRequestBody(String requestBody) {
		this.requestBody = requestBody;
	}

}
