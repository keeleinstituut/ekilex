package eki.ekilex.data;

import java.util.List;

import eki.common.data.AbstractDataObject;

@Deprecated
public class WordEtym extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long wordEtymId;

	private String etymologyTypeCode;

	private String etymologyYear;

	private String comment;

	private boolean questionable;

	private List<SourceLink> wordEtymSourceLinks;

	private List<WordEtymRel> wordEtymRelations;

	public Long getWordEtymId() {
		return wordEtymId;
	}

	public void setWordEtymId(Long wordEtymId) {
		this.wordEtymId = wordEtymId;
	}

	public String getEtymologyTypeCode() {
		return etymologyTypeCode;
	}

	public void setEtymologyTypeCode(String etymologyTypeCode) {
		this.etymologyTypeCode = etymologyTypeCode;
	}

	public String getEtymologyYear() {
		return etymologyYear;
	}

	public void setEtymologyYear(String etymologyYear) {
		this.etymologyYear = etymologyYear;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public boolean isQuestionable() {
		return questionable;
	}

	public void setQuestionable(boolean questionable) {
		this.questionable = questionable;
	}

	public List<SourceLink> getWordEtymSourceLinks() {
		return wordEtymSourceLinks;
	}

	public void setWordEtymSourceLinks(List<SourceLink> wordEtymSourceLinks) {
		this.wordEtymSourceLinks = wordEtymSourceLinks;
	}

	public List<WordEtymRel> getWordEtymRelations() {
		return wordEtymRelations;
	}

	public void setWordEtymRelations(List<WordEtymRel> wordEtymRelations) {
		this.wordEtymRelations = wordEtymRelations;
	}

}
