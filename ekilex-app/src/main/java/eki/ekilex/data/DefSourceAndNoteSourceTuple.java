package eki.ekilex.data;

import eki.common.constant.Complexity;
import eki.common.constant.ReferenceType;
import eki.common.data.AbstractDataObject;

public class DefSourceAndNoteSourceTuple extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long definitionId;

	private Long definitionSourceLinkId;

	private ReferenceType definitionSourceLinkType;

	private String definitionSourceLinkName;

	private Long definitionSourceId;

	private String definitionSourceName;

	private Long noteId;

	private String noteValueText;

	private String noteValuePrese;

	private String noteLang;

	private Complexity noteComplexity;

	private boolean isNotePublic;

	private Long noteOrderBy;

	private Long noteSourceLinkId;

	private ReferenceType noteSourceLinkType;

	private String noteSourceLinkName;

	private Long noteSourceId;

	private String noteSourceName;

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

	public Long getDefinitionSourceId() {
		return definitionSourceId;
	}

	public void setDefinitionSourceId(Long definitionSourceId) {
		this.definitionSourceId = definitionSourceId;
	}

	public String getDefinitionSourceName() {
		return definitionSourceName;
	}

	public void setDefinitionSourceName(String definitionSourceName) {
		this.definitionSourceName = definitionSourceName;
	}

	public Long getNoteId() {
		return noteId;
	}

	public void setNoteId(Long noteId) {
		this.noteId = noteId;
	}

	public String getNoteValueText() {
		return noteValueText;
	}

	public void setNoteValueText(String noteValueText) {
		this.noteValueText = noteValueText;
	}

	public String getNoteValuePrese() {
		return noteValuePrese;
	}

	public void setNoteValuePrese(String noteValuePrese) {
		this.noteValuePrese = noteValuePrese;
	}

	public String getNoteLang() {
		return noteLang;
	}

	public void setNoteLang(String noteLang) {
		this.noteLang = noteLang;
	}

	public Complexity getNoteComplexity() {
		return noteComplexity;
	}

	public void setNoteComplexity(Complexity noteComplexity) {
		this.noteComplexity = noteComplexity;
	}

	public boolean isNotePublic() {
		return isNotePublic;
	}

	public void setNotePublic(boolean notePublic) {
		isNotePublic = notePublic;
	}

	public Long getNoteOrderBy() {
		return noteOrderBy;
	}

	public void setNoteOrderBy(Long noteOrderBy) {
		this.noteOrderBy = noteOrderBy;
	}

	public Long getNoteSourceLinkId() {
		return noteSourceLinkId;
	}

	public void setNoteSourceLinkId(Long noteSourceLinkId) {
		this.noteSourceLinkId = noteSourceLinkId;
	}

	public ReferenceType getNoteSourceLinkType() {
		return noteSourceLinkType;
	}

	public void setNoteSourceLinkType(ReferenceType noteSourceLinkType) {
		this.noteSourceLinkType = noteSourceLinkType;
	}

	public String getNoteSourceLinkName() {
		return noteSourceLinkName;
	}

	public void setNoteSourceLinkName(String noteSourceLinkName) {
		this.noteSourceLinkName = noteSourceLinkName;
	}

	public Long getNoteSourceId() {
		return noteSourceId;
	}

	public void setNoteSourceId(Long noteSourceId) {
		this.noteSourceId = noteSourceId;
	}

	public String getNoteSourceName() {
		return noteSourceName;
	}

	public void setNoteSourceName(String noteSourceName) {
		this.noteSourceName = noteSourceName;
	}
}
