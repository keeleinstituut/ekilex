package eki.ekilex.data;

import eki.common.data.AbstractDataObject;

public class WordVariant extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long headwordLexemeId;

	private String variantTypeCode;

	private String wordValue;

	private boolean forceHomonym;

	public Long getHeadwordLexemeId() {
		return headwordLexemeId;
	}

	public void setHeadwordLexemeId(Long headwordLexemeId) {
		this.headwordLexemeId = headwordLexemeId;
	}

	public String getVariantTypeCode() {
		return variantTypeCode;
	}

	public void setVariantTypeCode(String variantTypeCode) {
		this.variantTypeCode = variantTypeCode;
	}

	public String getWordValue() {
		return wordValue;
	}

	public void setWordValue(String wordValue) {
		this.wordValue = wordValue;
	}

	public boolean isForceHomonym() {
		return forceHomonym;
	}

	public void setForceHomonym(boolean forceHomonym) {
		this.forceHomonym = forceHomonym;
	}

}
