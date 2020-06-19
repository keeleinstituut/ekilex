package eki.ekilex.data;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import eki.common.data.AbstractDataObject;

public class ApiEndpointDescription extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private List<String> uriPatterns;

	@JsonInclude(Include.NON_EMPTY)
	private List<String> parameters;

	public List<String> getUriPatterns() {
		return uriPatterns;
	}

	public void setUriPatterns(List<String> uriPatterns) {
		this.uriPatterns = uriPatterns;
	}

	public List<String> getParameters() {
		return parameters;
	}

	public void setParameters(List<String> parameters) {
		this.parameters = parameters;
	}

}
