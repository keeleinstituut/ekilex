package eki.ekilex.data;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class WordEtymNode extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long wordId;

	private String word;

	private String lang;

	private String etymologyTypeCode;

	private String etymYear;

	private boolean questionable;

	private boolean compound;

	private String comment;

	private int level;

	private List<WordEtymNode> children;

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

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public String getEtymologyTypeCode() {
		return etymologyTypeCode;
	}

	public void setEtymologyTypeCode(String etymologyTypeCode) {
		this.etymologyTypeCode = etymologyTypeCode;
	}

	public String getEtymYear() {
		return etymYear;
	}

	public void setEtymYear(String etymYear) {
		this.etymYear = etymYear;
	}

	public boolean isQuestionable() {
		return questionable;
	}

	public void setQuestionable(boolean questionable) {
		this.questionable = questionable;
	}

	public boolean isCompound() {
		return compound;
	}

	public void setCompound(boolean compound) {
		this.compound = compound;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public List<WordEtymNode> getChildren() {
		return children;
	}

	public void setChildren(List<WordEtymNode> children) {
		this.children = children;
	}

}
