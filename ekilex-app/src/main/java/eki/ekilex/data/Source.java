package eki.ekilex.data;

import java.util.List;

import eki.common.constant.SourceType;

public class Source extends AbstractCrudEntity {

	private static final long serialVersionUID = 1L;

	private Long id;

	private SourceType type;

	private List<String> sourceNames;

	private List<SourceProperty> sourceProperties;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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
