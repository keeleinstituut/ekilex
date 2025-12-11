package eki.ekilex.data;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class CollocMemberForm extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long wordId;

	private String wordValue;

	private Long formId;

	private String formValue;

	private String morphCode;

	private String morphValue;

	private List<CollocMemberMeaning> collocMemberMeanings;

	private boolean selected;

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

	public Long getFormId() {
		return formId;
	}

	public void setFormId(Long formId) {
		this.formId = formId;
	}

	public String getFormValue() {
		return formValue;
	}

	public void setFormValue(String formValue) {
		this.formValue = formValue;
	}

	public String getMorphCode() {
		return morphCode;
	}

	public void setMorphCode(String morphCode) {
		this.morphCode = morphCode;
	}

	public String getMorphValue() {
		return morphValue;
	}

	public void setMorphValue(String morphValue) {
		this.morphValue = morphValue;
	}

	public List<CollocMemberMeaning> getCollocMemberMeanings() {
		return collocMemberMeanings;
	}

	public void setCollocMemberMeanings(List<CollocMemberMeaning> collocMemberMeanings) {
		this.collocMemberMeanings = collocMemberMeanings;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

}
