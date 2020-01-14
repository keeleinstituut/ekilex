package eki.wordweb.data;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class WordData extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Word word;

	private List<Lexeme> lexLexemes;

	private List<Lexeme> termLexemes;

	private List<Paradigm> paradigms;

	private String firstAvailableVocalForm;

	private String firstAvailableAudioFile;

	private boolean unknownForm;

	private boolean lexResultsExist;

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

	public boolean isUnknownForm() {
		return unknownForm;
	}

	public void setUnknownForm(boolean unknownForm) {
		this.unknownForm = unknownForm;
	}

	public boolean isLexResultsExist() {
		return lexResultsExist;
	}

	public void setLexResultsExist(boolean lexResultsExist) {
		this.lexResultsExist = lexResultsExist;
	}

	public boolean isMultipleLexLexemesExist() {
		return multipleLexLexemesExist;
	}

	public void setMultipleLexLexemesExist(boolean multipleLexLexemesExist) {
		this.multipleLexLexemesExist = multipleLexLexemesExist;
	}

}
