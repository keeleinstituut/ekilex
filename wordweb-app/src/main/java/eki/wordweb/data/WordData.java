package eki.wordweb.data;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class WordData extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Word word;

	private List<Lexeme> lexLexemes;

	private List<Lexeme> termLexemes;

	private List<Lexeme> limTermLexemes;

	private List<Paradigm> paradigms;

	private String firstAvailableVocalForm;

	private String firstAvailableAudioFile;

	private boolean morphologyExists;

	private boolean relevantDataExists;

	private boolean lexemesExist;

	private boolean multipleLexLexemesExist;

	public Word getWord() {
		return word;
	}

	public void setWord(Word word) {
		this.word = word;
	}

	public List<Lexeme> getLexLexemes() {
		return lexLexemes;
	}

	public void setLexLexemes(List<Lexeme> lexLexemes) {
		this.lexLexemes = lexLexemes;
	}

	public List<Lexeme> getTermLexemes() {
		return termLexemes;
	}

	public void setTermLexemes(List<Lexeme> termLexemes) {
		this.termLexemes = termLexemes;
	}

	public List<Lexeme> getLimTermLexemes() {
		return limTermLexemes;
	}

	public void setLimTermLexemes(List<Lexeme> limTermLexemes) {
		this.limTermLexemes = limTermLexemes;
	}

	public List<Paradigm> getParadigms() {
		return paradigms;
	}

	public void setParadigms(List<Paradigm> paradigms) {
		this.paradigms = paradigms;
	}

	public String getFirstAvailableVocalForm() {
		return firstAvailableVocalForm;
	}

	public void setFirstAvailableVocalForm(String firstAvailableVocalForm) {
		this.firstAvailableVocalForm = firstAvailableVocalForm;
	}

	public String getFirstAvailableAudioFile() {
		return firstAvailableAudioFile;
	}

	public void setFirstAvailableAudioFile(String firstAvailableAudioFile) {
		this.firstAvailableAudioFile = firstAvailableAudioFile;
	}

	public boolean isMorphologyExists() {
		return morphologyExists;
	}

	public void setMorphologyExists(boolean morphologyExists) {
		this.morphologyExists = morphologyExists;
	}

	public boolean isRelevantDataExists() {
		return relevantDataExists;
	}

	public void setRelevantDataExists(boolean relevantDataExists) {
		this.relevantDataExists = relevantDataExists;
	}

	public boolean isLexemesExist() {
		return lexemesExist;
	}

	public void setLexemesExist(boolean lexemesExist) {
		this.lexemesExist = lexemesExist;
	}

	public boolean isMultipleLexLexemesExist() {
		return multipleLexLexemesExist;
	}

	public void setMultipleLexLexemesExist(boolean multipleLexLexemesExist) {
		this.multipleLexLexemesExist = multipleLexLexemesExist;
	}

}
