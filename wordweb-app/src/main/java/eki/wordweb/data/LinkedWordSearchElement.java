package eki.wordweb.data;

public class LinkedWordSearchElement extends WordSearchElement {

	private static final long serialVersionUID = 1L;

	private Long lexemeId;

	private Long meaningId;

	public Long getLexemeId() {
		return lexemeId;
	}

	public void setLexemeId(Long lexemeId) {
		this.lexemeId = lexemeId;
	}

	public Long getMeaningId() {
		return meaningId;
	}

	public void setMeaningId(Long meaningId) {
		this.meaningId = meaningId;
	}

}
