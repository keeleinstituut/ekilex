package eki.ekilex.data;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class InexactSynonym extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private String translationLangWordValue;

	private Long translationLangWordId;

	private String translationLang;

	private String inexactDefinitionValue;

	private Long meaningId;

	private Long wordId;

	private Long relationId;

	private List<SimpleWord> targetLangWords;

	private Long orderBy;

	public String getTranslationLangWordValue() {
		return translationLangWordValue;
	}

	public void setTranslationLangWordValue(String translationLangWordValue) {
		this.translationLangWordValue = translationLangWordValue;
	}

	public Long getTranslationLangWordId() {
		return translationLangWordId;
	}

	public void setTranslationLangWordId(Long translationLangWordId) {
		this.translationLangWordId = translationLangWordId;
	}

	public String getTranslationLang() {
		return translationLang;
	}

	public void setTranslationLang(String translationLang) {
		this.translationLang = translationLang;
	}

	public String getInexactDefinitionValue() {
		return inexactDefinitionValue;
	}

	public void setInexactDefinitionValue(String inexactDefinitionValue) {
		this.inexactDefinitionValue = inexactDefinitionValue;
	}

	public Long getMeaningId() {
		return meaningId;
	}

	public void setMeaningId(Long meaningId) {
		this.meaningId = meaningId;
	}

	public Long getWordId() {
		return wordId;
	}

	public void setWordId(Long wordId) {
		this.wordId = wordId;
	}

	public Long getRelationId() {
		return relationId;
	}

	public void setRelationId(Long relationId) {
		this.relationId = relationId;
	}

	public List<SimpleWord> getTargetLangWords() {
		return targetLangWords;
	}

	public void setTargetLangWords(List<SimpleWord> targetLangWords) {
		this.targetLangWords = targetLangWords;
	}

	public Long getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(Long orderBy) {
		this.orderBy = orderBy;
	}
}
