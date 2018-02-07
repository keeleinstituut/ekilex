package eki.ekilex.data;

import javax.persistence.Column;

import eki.common.data.AbstractDataObject;

public class Collocation extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	@Column(name = "colloc_id")
	private Long collocationId;

	@Column(name = "colloc_lexeme_id")
	private Long collocateLexemeId;

	@Column(name = "colloc_word_id")
	private Long collocateWordId;

	@Column(name = "collocation")
	private String value;

	@Column(name = "colloc_usages")
	private String[] collocationUsages;

	public Long getCollocationId() {
		return collocationId;
	}

	public void setCollocationId(Long collocationId) {
		this.collocationId = collocationId;
	}

	public Long getCollocateLexemeId() {
		return collocateLexemeId;
	}

	public void setCollocateLexemeId(Long collocateLexemeId) {
		this.collocateLexemeId = collocateLexemeId;
	}

	public Long getCollocateWordId() {
		return collocateWordId;
	}

	public void setCollocateWordId(Long collocateWordId) {
		this.collocateWordId = collocateWordId;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String[] getCollocationUsages() {
		return collocationUsages;
	}

	public void setCollocationUsages(String[] collocationUsages) {
		this.collocationUsages = collocationUsages;
	}

}
