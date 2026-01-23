package eki.ekilex.data;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class WordVariantCandidates extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private WordVariant wordVariant;

	private List<WordDescript> words;

	public WordVariant getWordVariant() {
		return wordVariant;
	}

	public void setWordVariant(WordVariant wordVariant) {
		this.wordVariant = wordVariant;
	}

	public List<WordDescript> getWords() {
		return words;
	}

	public void setWords(List<WordDescript> words) {
		this.words = words;
	}

}
