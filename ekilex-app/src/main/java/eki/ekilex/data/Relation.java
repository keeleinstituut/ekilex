package eki.ekilex.data;

import javax.persistence.Column;

import eki.common.data.AbstractDataObject;

public class Relation extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	@Column(name = "word")
	private String word;

	@Column(name = "word_lang")
	private String wordLang;

	@Column(name = "rel_type_label")
	private String relationTypeLabel;

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public String getWordLang() {
		return wordLang;
	}

	public void setWordLang(String wordLang) {
		this.wordLang = wordLang;
	}

	public String getRelationTypeLabel() {
		return relationTypeLabel;
	}

	public void setRelationTypeLabel(String relationTypeLabel) {
		this.relationTypeLabel = relationTypeLabel;
	}

}
