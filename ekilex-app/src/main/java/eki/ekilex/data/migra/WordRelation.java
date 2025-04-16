package eki.ekilex.data.migra;

import eki.common.data.AbstractDataObject;

public class WordRelation extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long wordId1;

	private Long wordId2;

	private String relationTypeCode;

	public WordRelation(Long wordId1, Long wordId2, String relationTypeCode) {
		super();
		this.wordId1 = wordId1;
		this.wordId2 = wordId2;
		this.relationTypeCode = relationTypeCode;
	}

	public Long getWordId1() {
		return wordId1;
	}

	public Long getWordId2() {
		return wordId2;
	}

	public String getRelationTypeCode() {
		return relationTypeCode;
	}

}
