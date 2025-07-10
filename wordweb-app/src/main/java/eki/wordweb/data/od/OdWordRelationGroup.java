package eki.wordweb.data.od;

import java.util.List;

import eki.common.data.AbstractDataObject;
import eki.common.data.Classifier;

public class OdWordRelationGroup extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private String wordRelTypeCode;

	private Classifier wordRelType;

	private List<OdWordRelation> relatedWords;

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

	public List<OdWordRelation> getRelatedWords() {
		return relatedWords;
	}

	public void setRelatedWords(List<OdWordRelation> relatedWords) {
		this.relatedWords = relatedWords;
	}

}
