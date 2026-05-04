package eki.ekilex.data;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class WordEtymNode extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long wordId;

	private String wordValue;

	private String wordLang;

	private String etymologyTypeCode;

	private String etymologyYear;

	private boolean questionable;

	private boolean compound;

	private String commentPrese;

	private List<WordEtymSourceLink> sourceLinks;

	private List<WordEtymSyn> meaningWords;

	private List<WordEtymNode> children;

	private int level;

	public Long getWordId() {
		return wordId;
	}

	public void setWordId(Long wordId) {
		this.wordId = wordId;
	}

	public String getWordValue() {
		return wordValue;
	}

	public void setWordValue(String wordValue) {
		this.wordValue = wordValue;
	}

	public String getWordLang() {
		return wordLang;
	}

	public void setWordLang(String wordLang) {
		this.wordLang = wordLang;
	}

	public String getEtymologyTypeCode() {
		return etymologyTypeCode;
	}

	public void setEtymologyTypeCode(String etymologyTypeCode) {
		this.etymologyTypeCode = etymologyTypeCode;
	}

	public String getEtymologyYear() {
		return etymologyYear;
	}

	public void setEtymologyYear(String etymologyYear) {
		this.etymologyYear = etymologyYear;
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

	public String getCommentPrese() {
		return commentPrese;
	}

	public void setCommentPrese(String commentPrese) {
		this.commentPrese = commentPrese;
	}

	public List<WordEtymSourceLink> getSourceLinks() {
		return sourceLinks;
	}

	public void setSourceLinks(List<WordEtymSourceLink> sourceLinks) {
		this.sourceLinks = sourceLinks;
	}

	public List<WordEtymSyn> getMeaningWords() {
		return meaningWords;
	}

	public void setMeaningWords(List<WordEtymSyn> meaningWords) {
		this.meaningWords = meaningWords;
	}

	public List<WordEtymNode> getChildren() {
		return children;
	}

	public void setChildren(List<WordEtymNode> children) {
		this.children = children;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

}
