package eki.wordweb.data;

import eki.common.data.AbstractDataObject;

public class WordPair extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long dataId1;

	private String word1;

	private Long dataId2;

	private String word2;

	private boolean synonym;

	public WordPair() {}

	public WordPair(Long dataId1, String word1, Long dataId2, String word2, boolean synonym) {
		this.dataId1 = dataId1;
		this.word1 = word1;
		this.dataId2 = dataId2;
		this.word2 = word2;
		this.synonym = synonym;
	}

	public Long getDataId1() {
		return dataId1;
	}

	public void setDataId1(Long dataId1) {
		this.dataId1 = dataId1;
	}

	public String getWord1() {
		return word1;
	}

	public void setWord1(String word1) {
		this.word1 = word1;
	}

	public Long getDataId2() {
		return dataId2;
	}

	public void setDataId2(Long dataId2) {
		this.dataId2 = dataId2;
	}

	public String getWord2() {
		return word2;
	}

	public void setWord2(String word2) {
		this.word2 = word2;
	}

	public boolean isSynonym() {
		return synonym;
	}

	public void setSynonym(boolean synonym) {
		this.synonym = synonym;
	}

}
