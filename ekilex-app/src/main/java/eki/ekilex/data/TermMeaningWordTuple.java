package eki.ekilex.data;

import eki.common.data.AbstractDataObject;

public class TermMeaningWordTuple extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long meaningId;

	private String conceptId;

	private Long mainWordId;

	private String mainWord;

	private Integer mainWordHomonymNr;

	private String mainWordLang;

	private String mainWordDatasetCodesWrapup;

	private Long otherWordId;

	private String otherWord;

	private Integer otherWordHomonymNr;

	private String otherWordLang;

	private Long otherWordOrderBy;

	private String otherWordDatasetCodesWrapup;

	public Long getMeaningId() {
		return meaningId;
	}

	public void setMeaningId(Long meaningId) {
		this.meaningId = meaningId;
	}

	public String getConceptId() {
		return conceptId;
	}

	public void setConceptId(String conceptId) {
		this.conceptId = conceptId;
	}

	public Long getMainWordId() {
		return mainWordId;
	}

	public void setMainWordId(Long mainWordId) {
		this.mainWordId = mainWordId;
	}

	public String getMainWord() {
		return mainWord;
	}

	public void setMainWord(String mainWord) {
		this.mainWord = mainWord;
	}

	public Integer getMainWordHomonymNr() {
		return mainWordHomonymNr;
	}

	public void setMainWordHomonymNr(Integer mainWordHomonymNr) {
		this.mainWordHomonymNr = mainWordHomonymNr;
	}

	public String getMainWordLang() {
		return mainWordLang;
	}

	public void setMainWordLang(String mainWordLang) {
		this.mainWordLang = mainWordLang;
	}

	public String getMainWordDatasetCodesWrapup() {
		return mainWordDatasetCodesWrapup;
	}

	public void setMainWordDatasetCodesWrapup(String mainWordDatasetCodesWrapup) {
		this.mainWordDatasetCodesWrapup = mainWordDatasetCodesWrapup;
	}

	public Long getOtherWordId() {
		return otherWordId;
	}

	public void setOtherWordId(Long otherWordId) {
		this.otherWordId = otherWordId;
	}

	public String getOtherWord() {
		return otherWord;
	}

	public void setOtherWord(String otherWord) {
		this.otherWord = otherWord;
	}

	public Integer getOtherWordHomonymNr() {
		return otherWordHomonymNr;
	}

	public void setOtherWordHomonymNr(Integer otherWordHomonymNr) {
		this.otherWordHomonymNr = otherWordHomonymNr;
	}

	public String getOtherWordLang() {
		return otherWordLang;
	}

	public void setOtherWordLang(String otherWordLang) {
		this.otherWordLang = otherWordLang;
	}

	public Long getOtherWordOrderBy() {
		return otherWordOrderBy;
	}

	public void setOtherWordOrderBy(Long otherWordOrderBy) {
		this.otherWordOrderBy = otherWordOrderBy;
	}

	public String getOtherWordDatasetCodesWrapup() {
		return otherWordDatasetCodesWrapup;
	}

	public void setOtherWordDatasetCodesWrapup(String otherWordDatasetCodesWrapup) {
		this.otherWordDatasetCodesWrapup = otherWordDatasetCodesWrapup;
	}

}
