package eki.ekilex.data.api;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class TermWord extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long wordId;

	private String value;

	private String lang;

	private List<String> wordTypeCodes;

	private String lexemeValueStateCode;

	private List<TermFreeform> lexemeNotes;

	private Boolean isLexemePublic;

	private List<TermFreeform> usages;

	public Long getWordId() {
		return wordId;
	}

	public void setWordId(Long wordId) {
		this.wordId = wordId;
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

	public List<TermFreeform> getLexemeNotes() {
		return lexemeNotes;
	}

	public void setLexemeNotes(List<TermFreeform> lexemeNotes) {
		this.lexemeNotes = lexemeNotes;
	}

	public Boolean getLexemePublic() {
		return isLexemePublic;
	}

	public void setLexemePublic(Boolean lexemePublic) {
		isLexemePublic = lexemePublic;
	}

	public List<TermFreeform> getUsages() {
		return usages;
	}

	public void setUsages(List<TermFreeform> usages) {
		this.usages = usages;
	}
}
