package eki.ekilex.data.etym2;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class WordEtymGroupMember extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long id;

	private Long wordId;

	private String value;

	private String valuePrese;

	private String lang;

	private String langValue;

	private String etymologyYear;

	private boolean questionable;

	private List<WordEtymWord> variantWords;

	private List<WordEtymComment> comments;

	private List<WordEtymNote> notes;

	private List<WordEtymSourceLink> sourceLinks;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

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

	public String getLangValue() {
		return langValue;
	}

	public void setLangValue(String langValue) {
		this.langValue = langValue;
	}

	public String getEtymologyYear() {
		return etymologyYear;
	}

	public void setEtymologyYear(String etymologyYear) {
		this.etymologyYear = etymologyYear;
	}

	public boolean isQuestionable() {
		return questionable;
	}

	public void setQuestionable(boolean questionable) {
		this.questionable = questionable;
	}

	public List<WordEtymWord> getVariantWords() {
		return variantWords;
	}

	public void setVariantWords(List<WordEtymWord> variantWords) {
		this.variantWords = variantWords;
	}

	public List<WordEtymComment> getComments() {
		return comments;
	}

	public void setComments(List<WordEtymComment> comments) {
		this.comments = comments;
	}

	public List<WordEtymNote> getNotes() {
		return notes;
	}

	public void setNotes(List<WordEtymNote> notes) {
		this.notes = notes;
	}

	public List<WordEtymSourceLink> getSourceLinks() {
		return sourceLinks;
	}

	public void setSourceLinks(List<WordEtymSourceLink> sourceLinks) {
		this.sourceLinks = sourceLinks;
	}

}
