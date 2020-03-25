package eki.ekilex.data.transform;

import eki.common.constant.Complexity;
import eki.common.data.AbstractDataObject;

public class WordLexemeMeaning extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long lexemeId;

	private Complexity complexity;

	private Long wordId;

	private String word;

	private String lang;

	private Integer homonymNr;

	private Long orderBy;

	private Long meaningId;

	private String datasetCode;

	public WordLexemeMeaning(Long lexemeId, Complexity complexity, Long wordId, String word, String lang, Integer homonymNr, Long orderBy, Long meaningId, String datasetCode) {

		this.lexemeId = lexemeId;
		this.complexity = complexity;
		this.wordId = wordId;
		this.word = word;
		this.lang = lang;
		this.homonymNr = homonymNr;
		this.orderBy = orderBy;
		this.meaningId = meaningId;
		this.datasetCode = datasetCode;
	}

	public Long getLexemeId() {
		return lexemeId;
	}

	public Complexity getComplexity() {
		return complexity;
	}

	public Long getWordId() {
		return wordId;
	}

	public String getWord() {
		return word;
	}

	public String getLang() {
		return lang;
	}

	public Integer getHomonymNr() {
		return homonymNr;
	}

	public Long getOrderBy() {
		return orderBy;
	}

	public Long getMeaningId() {
		return meaningId;
	}

	public String getDatasetCode() {
		return datasetCode;
	}
}
