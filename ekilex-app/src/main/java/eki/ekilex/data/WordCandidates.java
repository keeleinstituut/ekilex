package eki.ekilex.data;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class WordCandidates extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private LexemeVariantBean lexemeVariantBean;

	private List<WordDescript> words;

	public LexemeVariantBean getLexemeVariantBean() {
		return lexemeVariantBean;
	}

	public void setLexemeVariantBean(LexemeVariantBean lexemeVariantBean) {
		this.lexemeVariantBean = lexemeVariantBean;
	}

	public List<WordDescript> getWords() {
		return words;
	}

	public void setWords(List<WordDescript> words) {
		this.words = words;
	}

}
