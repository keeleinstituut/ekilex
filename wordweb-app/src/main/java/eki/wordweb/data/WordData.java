package eki.wordweb.data;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class WordData extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Word word;

	private List<LexemeWord> lexLexemes;

	private List<LexemeWord> termLexemes;

	private List<LexemeWord> limTermLexemes;

	private List<Paradigm> paradigms;

	private Long linkedLexemeId;

	private String firstAvailableAudioFile;

	private boolean morphologyExists;

	private boolean relevantDataExists;

	private boolean lexemesExist;

	private boolean multipleLexLexemesExist;

	private boolean estHeadword;

	private boolean rusHeadword;

	private boolean rusContent;

	private boolean skellCompatible;

	public Word getWord() {
		return word;
	}

	public void setWord(Word word) {
		this.word = word;
	}

	public List<LexemeWord> getLexLexemes() {
		return lexLexemes;
	}

	public void setLexLexemes(List<LexemeWord> lexLexemes) {
		this.lexLexemes = lexLexemes;
	}

	public List<LexemeWord> getTermLexemes() {
		return termLexemes;
	}

	public void setTermLexemes(List<LexemeWord> termLexemes) {
		this.termLexemes = termLexemes;
	}

	public List<LexemeWord> getLimTermLexemes() {
		return limTermLexemes;
	}

	public void setLimTermLexemes(List<LexemeWord> limTermLexemes) {
		this.limTermLexemes = limTermLexemes;
	}

	public List<Paradigm> getParadigms() {
		return paradigms;
	}

	public void setParadigms(List<Paradigm> paradigms) {
		this.paradigms = paradigms;
	}

	public Long getLinkedLexemeId() {
		return linkedLexemeId;
	}

	public void setLinkedLexemeId(Long linkedLexemeId) {
		this.linkedLexemeId = linkedLexemeId;
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

	public boolean isEstHeadword() {
		return estHeadword;
	}

	public void setEstHeadword(boolean estHeadword) {
		this.estHeadword = estHeadword;
	}

	public boolean isRusHeadword() {
		return rusHeadword;
	}

	public void setRusHeadword(boolean rusHeadword) {
		this.rusHeadword = rusHeadword;
	}

	public boolean isRusContent() {
		return rusContent;
	}

	public void setRusContent(boolean rusContent) {
		this.rusContent = rusContent;
	}

	public boolean isSkellCompatible() {
		return skellCompatible;
	}

	public void setSkellCompatible(boolean skellCompatible) {
		this.skellCompatible = skellCompatible;
	}

}
