package eki.wordweb.data;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class WordRelationTuple extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long wordId;

	private List<TypeWordRelation> relatedWords;

	private Long wordGroupId;

	private String wordRelTypeCode;

	private List<TypeWordRelation> wordGroupMembers;

	public Long getWordId() {
		return wordId;
	}

	public void setWordId(Long wordId) {
		this.wordId = wordId;
	}

	public List<TypeWordRelation> getRelatedWords() {
		return relatedWords;
	}

	public void setRelatedWords(List<TypeWordRelation> relatedWords) {
		this.relatedWords = relatedWords;
	}

	public Long getWordGroupId() {
		return wordGroupId;
	}

	public void setWordGroupId(Long wordGroupId) {
		this.wordGroupId = wordGroupId;
	}

	public String getWordRelTypeCode() {
		return wordRelTypeCode;
	}

	public void setWordRelTypeCode(String wordRelTypeCode) {
		this.wordRelTypeCode = wordRelTypeCode;
	}

	public List<TypeWordRelation> getWordGroupMembers() {
		return wordGroupMembers;
	}

	public void setWordGroupMembers(List<TypeWordRelation> wordGroupMembers) {
		this.wordGroupMembers = wordGroupMembers;
	}

}
