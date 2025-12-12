package eki.ekilex.data;

import eki.common.data.AbstractDataObject;
import io.swagger.v3.oas.annotations.media.Schema;

public class WordEtymRel extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long wordEtymRelId;
	@Schema(example = "537620")
	private Long relatedWordId;
	@Schema(example = "kopare")
	private String relatedWord;
	@Schema(example = "fin")
	private String relatedWordLang;

	private String commentPrese;
	@Schema(example = "false")
	private boolean questionable;
	@Schema(example = "false")
	private boolean compound;

	public Long getWordEtymRelId() {
		return wordEtymRelId;
	}

	public void setWordEtymRelId(Long wordEtymRelId) {
		this.wordEtymRelId = wordEtymRelId;
	}

	public Long getRelatedWordId() {
		return relatedWordId;
	}

	public void setRelatedWordId(Long relatedWordId) {
		this.relatedWordId = relatedWordId;
	}

	public String getRelatedWord() {
		return relatedWord;
	}

	public void setRelatedWord(String relatedWord) {
		this.relatedWord = relatedWord;
	}

	public String getRelatedWordLang() {
		return relatedWordLang;
	}

	public void setRelatedWordLang(String relatedWordLang) {
		this.relatedWordLang = relatedWordLang;
	}

	public String getCommentPrese() {
		return commentPrese;
	}

	public void setCommentPrese(String commentPrese) {
		this.commentPrese = commentPrese;
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

}
