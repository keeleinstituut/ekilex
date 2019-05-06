package eki.wordweb.data;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class WordEtymology extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long wordId;

	private String comment;

	private String etymWrapup;

	private List<String> etymLevelsWrapup;

	public Long getWordId() {
		return wordId;
	}

	public void setWordId(Long wordId) {
		this.wordId = wordId;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getEtymWrapup() {
		return etymWrapup;
	}

	public void setEtymWrapup(String etymWrapup) {
		this.etymWrapup = etymWrapup;
	}

	public List<String> getEtymLevelsWrapup() {
		return etymLevelsWrapup;
	}

	public void setEtymLevelsWrapup(List<String> etymLevelsWrapup) {
		this.etymLevelsWrapup = etymLevelsWrapup;
	}

}
