package eki.ekilex.data;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class InexactSynMeaningCandidate extends AbstractDataObject { // TODO similar to InexactSynMeaning class, move together?

	private static final long serialVersionUID = 1L;

	private Long meaningId;

	private String datasetCode;

	private String datasetName;

	private List<WordLexeme> translationLangWords;

	private List<WordLexeme> targetLangWords;

	private List<Definition> definitions;

	public Long getMeaningId() {
		return meaningId;
	}

	public void setMeaningId(Long meaningId) {
		this.meaningId = meaningId;
	}

	public String getDatasetCode() {
		return datasetCode;
	}

	public void setDatasetCode(String datasetCode) {
		this.datasetCode = datasetCode;
	}

	public String getDatasetName() {
		return datasetName;
	}

	public void setDatasetName(String datasetName) {
		this.datasetName = datasetName;
	}

	public List<WordLexeme> getTranslationLangWords() {
		return translationLangWords;
	}

	public void setTranslationLangWords(List<WordLexeme> translationLangWords) {
		this.translationLangWords = translationLangWords;
	}

	public List<WordLexeme> getTargetLangWords() {
		return targetLangWords;
	}

	public void setTargetLangWords(List<WordLexeme> targetLangWords) {
		this.targetLangWords = targetLangWords;
	}

	public List<Definition> getDefinitions() {
		return definitions;
	}

	public void setDefinitions(List<Definition> definitions) {
		this.definitions = definitions;
	}
}
