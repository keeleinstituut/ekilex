package eki.ekilex.data;

import javax.persistence.Column;

import eki.common.data.AbstractDataObject;

public class Relation extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	@Column(name = "id")
	private Long id;

	@Column(name = "lexeme_id")
	private Long lexemeId;

	@Column(name = "meaning_id")
	private Long meaningId;

	@Column(name = "word_id")
	private Long wordId;

	@Column(name = "form_id")
	private Long formId;

	@Column(name = "word")
	private String word;

	@Column(name = "word_lang")
	private String wordLang;

	@Column(name = "rel_type_label")
	private String relationTypeLabel;

	@Column(name = "order_by")
	private Long orderBy;

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

	public Long getWordId() {
		return wordId;
	}

	public void setWordId(Long wordId) {
		this.wordId = wordId;
	}

	public Long getFormId() {
		return formId;
	}

	public void setFormId(Long formId) {
		this.formId = formId;
	}

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

	public Long getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(Long orderBy) {
		this.orderBy = orderBy;
	}
}
