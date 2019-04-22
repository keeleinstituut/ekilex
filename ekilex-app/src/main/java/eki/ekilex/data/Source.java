package eki.ekilex.data;

import java.util.List;

import eki.common.constant.SourceType;
import eki.common.data.AbstractDataObject;

public class Source extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long sourceId;

	private SourceType type;

	private List<String> sourceNames;

	private List<SourceProperty> sourceProperties;

	public Long getSourceId() {
		return sourceId;
	}

	public void setSourceId(Long sourceId) {
		this.sourceId = sourceId;
	}

	public SourceType getType() {
		return type;
	}

	public void setType(SourceType type) {
		this.type = type;
	}

	public List<String> getSourceNames() {
		return sourceNames;
	}

	public void setSourceNames(List<String> sourceNames) {
		this.sourceNames = sourceNames;
	}

	public List<SourceProperty> getSourceProperties() {
		return sourceProperties;
	}

	public void setSourceProperties(List<SourceProperty> sourceProperties) {
		this.sourceProperties = sourceProperties;
	}

}
