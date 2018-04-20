package eki.wordweb.data;

import eki.common.data.AbstractDataObject;
import eki.common.data.Classifier;

public class TypeMeaningRelation extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long meaningId;

	private Long lexemeId;

	private Long wordId;

	private String word;

	private String wordLang;

	private String meaningRelTypeCode;

	private Classifier meaningRelType;

	public Long getMeaningId() {
		return meaningId;
	}

	public void setMeaningId(Long meaningId) {
		this.meaningId = meaningId;
	}

	public Long getLexemeId() {
		return lexemeId;
	}

	public void setLexemeId(Long lexemeId) {
		this.lexemeId = lexemeId;
	}

	public Long getWordId() {
		return wordId;
	}

	public void setWordId(Long wordId) {
		this.wordId = wordId;
	}

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public String getWordLang() {
		return wordLang;
	}

	public void setWordLang(String wordLang) {
		this.wordLang = wordLang;
	}

	public String getMeaningRelTypeCode() {
		return meaningRelTypeCode;
	}

	public void setMeaningRelTypeCode(String meaningRelTypeCode) {
		this.meaningRelTypeCode = meaningRelTypeCode;
	}

	public Classifier getMeaningRelType() {
		return meaningRelType;
	}

	public void setMeaningRelType(Classifier meaningRelType) {
		this.meaningRelType = meaningRelType;
	}

}
