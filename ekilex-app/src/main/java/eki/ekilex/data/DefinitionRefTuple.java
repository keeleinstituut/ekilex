package eki.ekilex.data;

import javax.persistence.Column;

import eki.common.data.AbstractDataObject;

public class DefinitionRefTuple extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	@Column(name = "definition_id")
	private Long definitionId;

	@Column(name = "definition_value")
	private String definitionValue;

	@Column(name = "definition_order_by")
	private Long definitionOrderBy;

	@Column(name = "ref_link_id")
	private Long refLinkId;

	@Column(name = "ref_link_name")
	private String refLinkName;

	@Column(name = "ref_link_value")
	private String refLinkValue;

	public Long getDefinitionId() {
		return definitionId;
	}

	public void setDefinitionId(Long definitionId) {
		this.definitionId = definitionId;
	}

	public String getDefinitionValue() {
		return definitionValue;
	}

	public void setDefinitionValue(String definitionValue) {
		this.definitionValue = definitionValue;
	}

	public Long getDefinitionOrderBy() {
		return definitionOrderBy;
	}

	public void setDefinitionOrderBy(Long definitionOrderBy) {
		this.definitionOrderBy = definitionOrderBy;
	}

	public Long getRefLinkId() {
		return refLinkId;
	}

	public void setRefLinkId(Long refLinkId) {
		this.refLinkId = refLinkId;
	}

	public String getRefLinkName() {
		return refLinkName;
	}

	public void setRefLinkName(String refLinkName) {
		this.refLinkName = refLinkName;
	}

	public String getRefLinkValue() {
		return refLinkValue;
	}

	public void setRefLinkValue(String refLinkValue) {
		this.refLinkValue = refLinkValue;
	}

}
