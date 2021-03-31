package eki.wordweb.data;

import java.util.List;

import eki.common.data.AbstractDataObject;
import eki.common.data.Classifier;
import eki.wordweb.data.type.TypeWordRelation;

public class WordGroup extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long wordGroupId;

	private String wordRelTypeCode;

	private Classifier wordRelType;

	private List<TypeWordRelation> wordGroupMembers;

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

	public Classifier getWordRelType() {
		return wordRelType;
	}

	public void setWordRelType(Classifier wordRelType) {
		this.wordRelType = wordRelType;
	}

	public List<TypeWordRelation> getWordGroupMembers() {
		return wordGroupMembers;
	}

	public void setWordGroupMembers(List<TypeWordRelation> wordGroupMembers) {
		this.wordGroupMembers = wordGroupMembers;
	}

}
