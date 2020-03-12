package eki.wordweb.data;

import java.util.List;

import eki.common.constant.Complexity;
import eki.common.constant.FreeformType;
import eki.common.data.AbstractDataObject;

public class TypeFreeform extends AbstractDataObject implements ComplexityType, SourceLinkType {

	private static final long serialVersionUID = 1L;

	private Long freeformId;

	private FreeformType type;

	private String value;

	private Complexity complexity;

	private List<TypeSourceLink> sourceLinks;

	@Override
	public Long getOwnerId() {
		return freeformId;
	}

	public Long getFreeformId() {
		return freeformId;
	}

	public void setFreeformId(Long freeformId) {
		this.freeformId = freeformId;
	}

	public FreeformType getType() {
		return type;
	}

	public void setType(FreeformType type) {
		this.type = type;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public Complexity getComplexity() {
		return complexity;
	}

	public void setComplexity(Complexity complexity) {
		this.complexity = complexity;
	}

	@Override
	public List<TypeSourceLink> getSourceLinks() {
		return sourceLinks;
	}

	@Override
	public void setSourceLinks(List<TypeSourceLink> sourceLinks) {
		this.sourceLinks = sourceLinks;
	}

}
