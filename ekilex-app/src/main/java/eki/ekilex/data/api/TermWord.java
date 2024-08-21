package eki.ekilex.data.api;

import java.util.List;

import eki.common.data.AbstractDataObject;
import eki.ekilex.data.LexemeNote;
import eki.ekilex.data.SourceLink;
import eki.ekilex.data.Usage;

public class TermWord extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long wordId;

	private Long lexemeId;

	private String value;

	private String valuePrese;

	private String lang;

	private List<String> wordTypeCodes;

	private String lexemeValueStateCode;

	private boolean isPublic;

	private List<LexemeNote> lexemeNotes;

	private List<SourceLink> lexemeSourceLinks;

	private List<String> lexemeTags;

	private List<Usage> usages;

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

	public String getValuePrese() {
		return valuePrese;
	}

	public void setValuePrese(String valuePrese) {
		this.valuePrese = valuePrese;
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

	public boolean isPublic() {
		return isPublic;
	}

	public void setPublic(boolean isPublic) {
		this.isPublic = isPublic;
	}

	public List<LexemeNote> getLexemeNotes() {
		return lexemeNotes;
	}

	public void setLexemeNotes(List<LexemeNote> lexemeNotes) {
		this.lexemeNotes = lexemeNotes;
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

	public List<Usage> getUsages() {
		return usages;
	}

	public void setUsages(List<Usage> usages) {
		this.usages = usages;
	}

}
