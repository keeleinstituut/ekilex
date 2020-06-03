package eki.ekilex.data;

import eki.common.constant.Complexity;
import eki.common.constant.ReferenceType;
import eki.common.data.AbstractDataObject;

public class DefSourceAndPublicNoteSourceTuple extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long definitionId;

	private Long definitionSourceLinkId;

	private ReferenceType definitionSourceLinkType;

	private String definitionSourceLinkName;

	private String definitionSourceLinkValue;

	private Long publicNoteId;

	private String publicNoteValueText;

	private String publicNoteValuePrese;

	private String publicNoteLang;

	private Complexity publicNoteComplexity;

	private boolean isPublicNotePublic;

	private Long publicNoteOrderBy;

	private Long publicNoteSourceLinkId;

	private ReferenceType publicNoteSourceLinkType;

	private String publicNoteSourceLinkName;

	private String publicNoteSourceLinkValue;

	public Long getDefinitionId() {
		return definitionId;
	}

	public void setDefinitionId(Long definitionId) {
		this.definitionId = definitionId;
	}

	public Long getDefinitionSourceLinkId() {
		return definitionSourceLinkId;
	}

	public void setDefinitionSourceLinkId(Long definitionSourceLinkId) {
		this.definitionSourceLinkId = definitionSourceLinkId;
	}

	public ReferenceType getDefinitionSourceLinkType() {
		return definitionSourceLinkType;
	}

	public void setDefinitionSourceLinkType(ReferenceType definitionSourceLinkType) {
		this.definitionSourceLinkType = definitionSourceLinkType;
	}

	public String getDefinitionSourceLinkName() {
		return definitionSourceLinkName;
	}

	public void setDefinitionSourceLinkName(String definitionSourceLinkName) {
		this.definitionSourceLinkName = definitionSourceLinkName;
	}

	public String getDefinitionSourceLinkValue() {
		return definitionSourceLinkValue;
	}

	public void setDefinitionSourceLinkValue(String definitionSourceLinkValue) {
		this.definitionSourceLinkValue = definitionSourceLinkValue;
	}

	public Long getPublicNoteId() {
		return publicNoteId;
	}

	public void setPublicNoteId(Long publicNoteId) {
		this.publicNoteId = publicNoteId;
	}

	public String getPublicNoteValueText() {
		return publicNoteValueText;
	}

	public void setPublicNoteValueText(String publicNoteValueText) {
		this.publicNoteValueText = publicNoteValueText;
	}

	public String getPublicNoteValuePrese() {
		return publicNoteValuePrese;
	}

	public void setPublicNoteValuePrese(String publicNoteValuePrese) {
		this.publicNoteValuePrese = publicNoteValuePrese;
	}

	public String getPublicNoteLang() {
		return publicNoteLang;
	}

	public void setPublicNoteLang(String publicNoteLang) {
		this.publicNoteLang = publicNoteLang;
	}

	public Complexity getPublicNoteComplexity() {
		return publicNoteComplexity;
	}

	public void setPublicNoteComplexity(Complexity publicNoteComplexity) {
		this.publicNoteComplexity = publicNoteComplexity;
	}

	public boolean isPublicNotePublic() {
		return isPublicNotePublic;
	}

	public void setPublicNotePublic(boolean publicNotePublic) {
		isPublicNotePublic = publicNotePublic;
	}

	public Long getPublicNoteOrderBy() {
		return publicNoteOrderBy;
	}

	public void setPublicNoteOrderBy(Long publicNoteOrderBy) {
		this.publicNoteOrderBy = publicNoteOrderBy;
	}

	public Long getPublicNoteSourceLinkId() {
		return publicNoteSourceLinkId;
	}

	public void setPublicNoteSourceLinkId(Long publicNoteSourceLinkId) {
		this.publicNoteSourceLinkId = publicNoteSourceLinkId;
	}

	public ReferenceType getPublicNoteSourceLinkType() {
		return publicNoteSourceLinkType;
	}

	public void setPublicNoteSourceLinkType(ReferenceType publicNoteSourceLinkType) {
		this.publicNoteSourceLinkType = publicNoteSourceLinkType;
	}

	public String getPublicNoteSourceLinkName() {
		return publicNoteSourceLinkName;
	}

	public void setPublicNoteSourceLinkName(String publicNoteSourceLinkName) {
		this.publicNoteSourceLinkName = publicNoteSourceLinkName;
	}

	public String getPublicNoteSourceLinkValue() {
		return publicNoteSourceLinkValue;
	}

	public void setPublicNoteSourceLinkValue(String publicNoteSourceLinkValue) {
		this.publicNoteSourceLinkValue = publicNoteSourceLinkValue;
	}
}
