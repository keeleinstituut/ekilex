package eki.wordweb.data;

import java.util.List;
import java.util.Map;

import eki.common.data.AbstractDataObject;
import eki.common.data.Classifier;

public class WordRelationGroup extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Classifier wordRelType;

	private List<WordRelation> relatedWords;

	private Map<String, List<WordRelation>> relatedWordsByLang;

	private boolean asList;

	private boolean asMap;

	private boolean empty;

	private boolean collapsible;

	public Classifier getWordRelType() {
		return wordRelType;
	}

	public void setWordRelType(Classifier wordRelType) {
		this.wordRelType = wordRelType;
	}

	public List<WordRelation> getRelatedWords() {
		return relatedWords;
	}

	public void setRelatedWords(List<WordRelation> relatedWords) {
		this.relatedWords = relatedWords;
	}

	public Map<String, List<WordRelation>> getRelatedWordsByLang() {
		return relatedWordsByLang;
	}

	public void setRelatedWordsByLang(Map<String, List<WordRelation>> relatedWordsByLang) {
		this.relatedWordsByLang = relatedWordsByLang;
	}

	public boolean isAsList() {
		return asList;
	}

	public void setAsList(boolean asList) {
		this.asList = asList;
	}

	public boolean isAsMap() {
		return asMap;
	}

	public void setAsMap(boolean asMap) {
		this.asMap = asMap;
	}

	public boolean isEmpty() {
		return empty;
	}

	public void setEmpty(boolean empty) {
		this.empty = empty;
	}

	public boolean isCollapsible() {
		return collapsible;
	}

	public void setCollapsible(boolean collapsible) {
		this.collapsible = collapsible;
	}

}
