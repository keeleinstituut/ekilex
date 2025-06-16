package eki.wordweb.data;

import java.util.List;

public class Definition extends AbstractPublishingEntity implements SourceLinkType, LangType {

	private static final long serialVersionUID = 1L;

	private Long id;

	private Long lexemeId;

	private Long meaningId;

	private String value;

	private String valuePrese;

	private String lang;

	private List<Note> notes;

	private List<SourceLink> sourceLinks;

	private boolean subDataExists;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getLexemeId() {
		return lexemeId;
	}

	public void setLexemeId(Long lexemeId) {
		this.lexemeId = lexemeId;
	}

	public Long getMeaningId() {
		return meaningId;
	}

	public void setMeaningId(Long meaningId) {
		this.meaningId = meaningId;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getValuePrese() {
		return valuePrese;
	}

	public void setValuePrese(String valuePrese) {
		this.valuePrese = valuePrese;
	}

	@Override
	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public List<Note> getNotes() {
		return notes;
	}

	public void setNotes(List<Note> notes) {
		this.notes = notes;
	}

	@Override
	public List<SourceLink> getSourceLinks() {
		return sourceLinks;
	}

	@Override
	public void setSourceLinks(List<SourceLink> sourceLinks) {
		this.sourceLinks = sourceLinks;
	}

	public boolean isSubDataExists() {
		return subDataExists;
	}

	public void setSubDataExists(boolean subDataExists) {
		this.subDataExists = subDataExists;
	}

}
