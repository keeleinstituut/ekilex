package eki.ekilex.data;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class InexactSynonym extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private String translationLangWordValue;

	private Long translationLangWordId;

	private String inexactDefinitionValue;

	private Long meaningId;

	private List<WordLexeme> targetLangWords;

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

	public List<WordLexeme> getTargetLangWords() {
		return targetLangWords;
	}

	public void setTargetLangWords(List<WordLexeme> targetLangWords) {
		this.targetLangWords = targetLangWords;
	}
}
