package eki.wordweb.data;

import java.util.List;

import eki.common.data.AbstractDataObject;
import eki.wordweb.data.type.TypeWordRelation;

public class WordRelationsTuple extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long wordId;

	private List<TypeWordRelation> relatedWords;

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

	public List<TypeWordRelation> getWordGroupMembers() {
		return wordGroupMembers;
	}

	public void setWordGroupMembers(List<TypeWordRelation> wordGroupMembers) {
		this.wordGroupMembers = wordGroupMembers;
	}

}
