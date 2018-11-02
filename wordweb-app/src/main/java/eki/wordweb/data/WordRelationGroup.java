package eki.wordweb.data;

import java.util.List;

import eki.common.data.AbstractDataObject;
import eki.common.data.Classifier;

public class WordRelationGroup extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Classifier wordRelType;

	private List<TypeWordRelation> relatedWords;

	public Classifier getWordRelType() {
		return wordRelType;
	}

	public void setWordRelType(Classifier wordRelType) {
		this.wordRelType = wordRelType;
	}

	public List<TypeWordRelation> getRelatedWords() {
		return relatedWords;
	}

	public void setRelatedWords(List<TypeWordRelation> relatedWords) {
		this.relatedWords = relatedWords;
	}

}
