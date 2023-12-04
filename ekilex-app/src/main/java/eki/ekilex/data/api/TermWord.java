package eki.ekilex.data.api;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class TermWord extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long wordId;

	private Long lexemeId;

	private String value;

	private String lang;

	private List<String> wordTypeCodes;

	private String lexemeValueStateCode;

	private List<Freeform> lexemeNotes;

	private Boolean lexemePublicity;

	private List<SourceLink> lexemeSourceLinks;

	private List<String> lexemeTags;

	private List<Freeform> usages;

	public Long getWordId() {
		return wordId;
	}

	public void setWordId(Long wordId) {
		this.wordId = wordId;
	}

	public Long getLexemeId() {
		return lexemeId;
	}

	public void setLexemeId(Long lexemeId) {
		this.lexemeId = lexemeId;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public List<String> getWordTypeCodes() {
		return wordTypeCodes;
	}

	public void setWordTypeCodes(List<String> wordTypeCodes) {
		this.wordTypeCodes = wordTypeCodes;
	}

	public String getLexemeValueStateCode() {
		return lexemeValueStateCode;
	}

	public void setLexemeValueStateCode(String lexemeValueStateCode) {
		this.lexemeValueStateCode = lexemeValueStateCode;
	}

	public List<Freeform> getLexemeNotes() {
		return lexemeNotes;
	}

	public void setLexemeNotes(List<Freeform> lexemeNotes) {
		this.lexemeNotes = lexemeNotes;
	}

	public Boolean getLexemePublicity() {
		return lexemePublicity;
	}

	public void setLexemePublicity(Boolean lexemePublicity) {
		this.lexemePublicity = lexemePublicity;
	}

	public List<SourceLink> getLexemeSourceLinks() {
		return lexemeSourceLinks;
	}

	public void setLexemeSourceLinks(List<SourceLink> lexemeSourceLinks) {
		this.lexemeSourceLinks = lexemeSourceLinks;
	}

	public List<String> getLexemeTags() {
		return lexemeTags;
	}

	public void setLexemeTags(List<String> lexemeTags) {
		this.lexemeTags = lexemeTags;
	}

	public List<Freeform> getUsages() {
		return usages;
	}

	public void setUsages(List<Freeform> usages) {
		this.usages = usages;
	}
}
