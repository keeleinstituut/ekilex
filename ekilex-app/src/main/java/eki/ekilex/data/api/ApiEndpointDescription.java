package eki.ekilex.data.api;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import eki.common.data.AbstractDataObject;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;


@Schema(description = "Describes a single Ekilex API endpoint with its technical details")
public class ApiEndpointDescription extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	@Schema(description = "Display order of the endpoint in documentation", hidden = true)
	@JsonIgnore
	private int order;
	@Schema(description = "HTTP method used by the endpoint (e.g., GET, POST, PUT, DELETE)", example = "GET")
	private String requestMethod;

	@ArraySchema(
			arraySchema = @Schema(description = "List of URI patterns associated with the endpoint"),
			schema = @Schema(type = "string", example = "/api/word/{id}")
	)
	private List<String> uriPatterns;

	@ArraySchema(
			arraySchema = @Schema(description = "List of path variables in the URI, including their names and types"),
			schema = @Schema(type = "string", example = "id::java.lang.Long")
	)
	@JsonInclude(Include.NON_EMPTY)
	private List<String> pathVariables;
	@ArraySchema(
			arraySchema = @Schema(description = "List of query parameters accepted by the endpoint, including names and types"),
			schema = @Schema(type = "string", example = "[\"lang::java.lang.String\"]")
	)
	@JsonInclude(Include.NON_EMPTY)
	private List<String> requestParameters;

	@Schema(description = "Type of the request body object, if applicable",
			example = "eki.ekilex.data.WordCreateRequest")
	@JsonInclude(Include.NON_NULL)
	private String requestBody;

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public String getRequestMethod() {
		return requestMethod;
	}

	public void setRequestMethod(String requestMethod) {
		this.requestMethod = requestMethod;
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
