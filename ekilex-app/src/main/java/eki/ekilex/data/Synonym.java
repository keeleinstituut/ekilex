package eki.ekilex.data;

import java.util.List;

import eki.common.constant.SynonymType;
import eki.common.data.AbstractDataObject;

public class Synonym extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private SynonymType type;

	private Long meaningId;

	private Long relationId;

	List<SynWord> words;

	private String wordLang;

	private Float weight;

	private Long orderBy;

	public SynonymType getType() {
		return type;
	}

	public void setType(SynonymType type) {
		this.type = type;
	}

	public Long getMeaningId() {
		return meaningId;
	}

	public void setMeaningId(Long meaningId) {
		this.meaningId = meaningId;
	}

	public Long getRelationId() {
		return relationId;
	}

	public void setRelationId(Long relationId) {
		this.relationId = relationId;
	}

	public List<SynWord> getWords() {
		return words;
	}

	public void setWords(List<SynWord> words) {
		this.words = words;
	}

	public String getWordLang() {
		return wordLang;
	}

	public void setWordLang(String wordLang) {
		this.wordLang = wordLang;
	}

	public Float getWeight() {
		return weight;
	}

	public void setWeight(Float weight) {
		this.weight = weight;
	}

	public Long getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(Long orderBy) {
		this.orderBy = orderBy;
	}
}
