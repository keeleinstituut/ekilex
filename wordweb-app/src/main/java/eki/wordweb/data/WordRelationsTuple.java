package eki.wordweb.data;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class WordRelationsTuple extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long wordId;

	private List<WordRelation> relatedWords;

	private List<WordRelation> wordGroupMembers;

	public Long getWordId() {
		return wordId;
	}

	public void setWordId(Long wordId) {
		this.wordId = wordId;
	}

	public List<WordRelation> getRelatedWords() {
		return relatedWords;
	}

	public void setRelatedWords(List<WordRelation> relatedWords) {
		this.relatedWords = relatedWords;
	}

	public List<WordRelation> getWordGroupMembers() {
		return wordGroupMembers;
	}

	public void setWordGroupMembers(List<WordRelation> wordGroupMembers) {
		this.wordGroupMembers = wordGroupMembers;
	}

}
