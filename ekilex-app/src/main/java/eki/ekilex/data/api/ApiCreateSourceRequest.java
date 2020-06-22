package eki.ekilex.data.api;

import java.util.List;

import eki.common.constant.SourceType;
import eki.common.data.AbstractDataObject;
import eki.ekilex.data.SourceProperty;

public class ApiCreateSourceRequest extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private SourceType sourceType;

	private String sourceName;

	private List<SourceProperty> sourceProperties;

	public SourceType getSourceType() {
		return sourceType;
	}

	public void setSourceType(SourceType sourceType) {
		this.sourceType = sourceType;
	}

	public String getSourceName() {
		return sourceName;
	}

	public void setSourceName(String sourceName) {
		this.sourceName = sourceName;
	}

	public List<SourceProperty> getSourceProperties() {
		return sourceProperties;
	}

	public void setSourceProperties(List<SourceProperty> sourceProperties) {
		this.sourceProperties = sourceProperties;
	}

}
