package eki.ekilex.data.migra;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class MigraWord extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long id;

	private String value;

	private List<Long> lexemeIds;

	private List<Long> ekiLexemeIds;

	private List<String> datasetCodes;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public List<Long> getLexemeIds() {
		return lexemeIds;
	}

	public void setLexemeIds(List<Long> lexemeIds) {
		this.lexemeIds = lexemeIds;
	}

	public List<Long> getEkiLexemeIds() {
		return ekiLexemeIds;
	}

	public void setEkiLexemeIds(List<Long> ekiLexemeIds) {
		this.ekiLexemeIds = ekiLexemeIds;
	}

	public List<String> getDatasetCodes() {
		return datasetCodes;
	}

	public void setDatasetCodes(List<String> datasetCodes) {
		this.datasetCodes = datasetCodes;
	}

}
