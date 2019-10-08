package eki.wordweb.data;

import eki.common.constant.Complexity;
import eki.common.data.AbstractDataObject;
import eki.common.data.Classifier;

public class TypeLexemeRelation extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long lexemeId;

	private Long wordId;

	private String word;

	private String wordLang;

	private Integer homonymNr;

	private Complexity complexity;

	private String lexRelTypeCode;

	private Classifier lexRelType;

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

	public Integer getHomonymNr() {
		return homonymNr;
	}

	public void setHomonymNr(Integer homonymNr) {
		this.homonymNr = homonymNr;
	}

	public Complexity getComplexity() {
		return complexity;
	}

	public void setComplexity(Complexity complexity) {
		this.complexity = complexity;
	}

	public String getLexRelTypeCode() {
		return lexRelTypeCode;
	}

	public void setLexRelTypeCode(String lexRelTypeCode) {
		this.lexRelTypeCode = lexRelTypeCode;
	}

	public Classifier getLexRelType() {
		return lexRelType;
	}

	public void setLexRelType(Classifier lexRelType) {
		this.lexRelType = lexRelType;
	}
}
