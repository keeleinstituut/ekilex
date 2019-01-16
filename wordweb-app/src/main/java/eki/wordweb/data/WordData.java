package eki.wordweb.data;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class WordData extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Word word;

	private List<Lexeme> lexemes;

	private List<Paradigm> paradigms;

	private List<String> imageFiles;

	private String firstAvailableVocalForm;

	private String firstAvailableSoundFile;

	private boolean prefixoid;

	private boolean suffixoid;

	private boolean abbreviationWord;

	private boolean unknownForm;

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

	public String getFirstAvailableVocalForm() {
		return firstAvailableVocalForm;
	}

	public void setFirstAvailableVocalForm(String firstAvailableVocalForm) {
		this.firstAvailableVocalForm = firstAvailableVocalForm;
	}

	public String getFirstAvailableSoundFile() {
		return firstAvailableSoundFile;
	}

	public void setFirstAvailableSoundFile(String firstAvailableSoundFile) {
		this.firstAvailableSoundFile = firstAvailableSoundFile;
	}

	public boolean isPrefixoid() {
		return prefixoid;
	}

	public void setPrefixoid(boolean prefixoid) {
		this.prefixoid = prefixoid;
	}

	public boolean isSuffixoid() {
		return suffixoid;
	}

	public void setSuffixoid(boolean suffixoid) {
		this.suffixoid = suffixoid;
	}

	public boolean isAbbreviationWord() {
		return abbreviationWord;
	}

	public void setAbbreviationWord(boolean abbreviationWord) {
		this.abbreviationWord = abbreviationWord;
	}

	public boolean isUnknownForm() {
		return unknownForm;
	}

	public void setUnknownForm(boolean unknownForm) {
		this.unknownForm = unknownForm;
	}
}
