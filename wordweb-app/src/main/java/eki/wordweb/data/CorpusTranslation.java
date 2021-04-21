package eki.wordweb.data;

import eki.common.data.AbstractDataObject;

public class CorpusTranslation extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private String sentence;

	private String translatedSentence;

	public CorpusTranslation() {
	}

	public CorpusTranslation(String sentence, String translatedSentence) {
		this.sentence = sentence;
		this.translatedSentence = translatedSentence;
	}

	public String getSentence() {
		return sentence;
	}

	public void setSentence(String sentence) {
		this.sentence = sentence;
	}

	public String getTranslatedSentence() {
		return translatedSentence;
	}

	public void setTranslatedSentence(String translatedSentence) {
		this.translatedSentence = translatedSentence;
	}
}
