package eki.ekilex.data;

import eki.common.data.AbstractDataObject;

public class WordStress extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long wordId;

	private String valuePrese;

	private String displayForm;

	private boolean stressExists;

	private boolean morphExists;

	public Long getWordId() {
		return wordId;
	}

	public void setWordId(Long wordId) {
		this.wordId = wordId;
	}

	public String getValuePrese() {
		return valuePrese;
	}

	public void setValuePrese(String valuePrese) {
		this.valuePrese = valuePrese;
	}

	public String getDisplayForm() {
		return displayForm;
	}

	public void setDisplayForm(String displayForm) {
		this.displayForm = displayForm;
	}

	public boolean isStressExists() {
		return stressExists;
	}

	public void setStressExists(boolean stressExists) {
		this.stressExists = stressExists;
	}

	public boolean isMorphExists() {
		return morphExists;
	}

	public void setMorphExists(boolean morphExists) {
		this.morphExists = morphExists;
	}

}
