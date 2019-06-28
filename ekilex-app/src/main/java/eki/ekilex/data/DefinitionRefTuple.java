package eki.ekilex.data;

import java.util.List;

import eki.common.constant.Complexity;
import eki.common.constant.ReferenceType;
import eki.common.data.AbstractDataObject;

public class DefinitionRefTuple extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long definitionId;

	private String definitionValue;

	private String definitionLang;

	private Complexity definitionComplexity;

	private Long definitionOrderBy;

	private String definitionTypeCode;

	private List<String> definitionDatasetCodes;

	private Long sourceLinkId;

	private ReferenceType sourceLinkType;

	private String sourceLinkName;

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

	public Complexity getDefinitionComplexity() {
		return definitionComplexity;
	}

	public void setDefinitionComplexity(Complexity definitionComplexity) {
		this.definitionComplexity = definitionComplexity;
	}

	public Long getDefinitionOrderBy() {
		return definitionOrderBy;
	}

	public void setDefinitionOrderBy(Long definitionOrderBy) {
		this.definitionOrderBy = definitionOrderBy;
	}

	public String getDefinitionTypeCode() {
		return definitionTypeCode;
	}

	public void setDefinitionTypeCode(String definitionTypeCode) {
		this.definitionTypeCode = definitionTypeCode;
	}

	public List<String> getDefinitionDatasetCodes() {
		return definitionDatasetCodes;
	}

	public void setDefinitionDatasetCodes(List<String> definitionDatasetCodes) {
		this.definitionDatasetCodes = definitionDatasetCodes;
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
