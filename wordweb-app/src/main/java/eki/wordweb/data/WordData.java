package eki.wordweb.data;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class WordData extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Word word;

	private List<Lexeme> lexemes;

	private List<Paradigm> paradigms;

	private List<String> imageFiles;

	private List<Relation> wordRelations;

	public Word getWord() {
		return word;
	}

	public void setWord(Word word) {
		this.word = word;
	}

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

	public List<Relation> getWordRelations() {
		return wordRelations;
	}

	public void setWordRelations(List<Relation> wordRelations) {
		this.wordRelations = wordRelations;
	}
}
