package eki.wordweb.data;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class WordData extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private List<Lexeme> lexemes;

	private List<Paradigm> paradigms;

	private List<String> imageFiles;

	private List<CorporaSentence> sentences;

	public List<Lexeme> getLexemes() {
		return lexemes;
	}

	public void setLexemes(List<Lexeme> lexemes) {
		this.lexemes = lexemes;
	}

	public List<Paradigm> getParadigms() {
		return paradigms;
	}

	public void setParadigms(List<Paradigm> paradigms) {
		this.paradigms = paradigms;
	}

	public List<String> getImageFiles() {
		return imageFiles;
	}

	public void setImageFiles(List<String> imageFiles) {
		this.imageFiles = imageFiles;
	}

	public List<CorporaSentence> getSentences() {
		return sentences;
	}

	public void setSentences(List<CorporaSentence> sentences) {
		this.sentences = sentences;
	}

}
