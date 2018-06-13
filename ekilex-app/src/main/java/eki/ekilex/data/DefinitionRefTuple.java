package eki.ekilex.data;

import javax.persistence.Column;

import eki.common.constant.ReferenceType;
import eki.common.data.AbstractDataObject;

public class DefinitionRefTuple extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	@Column(name = "definition_id")
	private Long definitionId;

	@Column(name = "definition_value")
	private String definitionValue;

	@Column(name = "definition_lang")
	private String definitionLang;

	@Column(name = "definition_order_by")
	private Long definitionOrderBy;

	@Column(name = "source_link_id")
	private Long sourceLinkId;

	@Column(name = "source_link_type")
	private ReferenceType sourceLinkType;

	@Column(name = "source_link_name")
	private String sourceLinkName;

	@Column(name = "source_link_value")
	private String sourceLinkValue;

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

	public String getDefinitionLang() {
		return definitionLang;
	}

	public void setDefinitionLang(String definitionLang) {
		this.definitionLang = definitionLang;
	}

	public Long getDefinitionOrderBy() {
		return definitionOrderBy;
	}

	public void setDefinitionOrderBy(Long definitionOrderBy) {
		this.definitionOrderBy = definitionOrderBy;
	}

	public Long getSourceLinkId() {
		return sourceLinkId;
	}

	public void setSourceLinkId(Long sourceLinkId) {
		this.sourceLinkId = sourceLinkId;
	}

	public ReferenceType getSourceLinkType() {
		return sourceLinkType;
	}

	public void setSourceLinkType(ReferenceType sourceLinkType) {
		this.sourceLinkType = sourceLinkType;
	}

	public String getSourceLinkName() {
		return sourceLinkName;
	}

	public void setSourceLinkName(String sourceLinkName) {
		this.sourceLinkName = sourceLinkName;
	}

	public String getSourceLinkValue() {
		return sourceLinkValue;
	}

	public void setSourceLinkValue(String sourceLinkValue) {
		this.sourceLinkValue = sourceLinkValue;
	}

}
