package eki.ekilex.data;

import eki.common.constant.FormMode;
import eki.common.data.AbstractDataObject;

public class ParadigmFormTuple extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long paradigmId;

	private String paradigmComment;

	private String inflectionType;

	private String inflectionTypeNr;

	private String wordClass;

	private Long formId;

	private String formValue;

	private String formValuePrese;

	private FormMode mode;

	private String[] components;

	private String displayForm;

	private String morphCode;

	private String morphValue;

	private String morphFrequency;

	private String formFrequency;

	public Long getParadigmId() {
		return paradigmId;
	}

	public void setParadigmId(Long paradigmId) {
		this.paradigmId = paradigmId;
	}

	public String getParadigmComment() {
		return paradigmComment;
	}

	public void setParadigmComment(String paradigmComment) {
		this.paradigmComment = paradigmComment;
	}

	public String getInflectionType() {
		return inflectionType;
	}

	public void setInflectionType(String inflectionType) {
		this.inflectionType = inflectionType;
	}

	public String getInflectionTypeNr() {
		return inflectionTypeNr;
	}

	public void setInflectionTypeNr(String inflectionTypeNr) {
		this.inflectionTypeNr = inflectionTypeNr;
	}

	public String getWordClass() {
		return wordClass;
	}

	public void setWordClass(String wordClass) {
		this.wordClass = wordClass;
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

	public String getFormValuePrese() {
		return formValuePrese;
	}

	public void setFormValuePrese(String formValuePrese) {
		this.formValuePrese = formValuePrese;
	}

	public FormMode getMode() {
		return mode;
	}

	public void setMode(FormMode mode) {
		this.mode = mode;
	}

	public String[] getComponents() {
		return components;
	}

	public void setComponents(String[] components) {
		this.components = components;
	}

	public String getDisplayForm() {
		return displayForm;
	}

	public void setDisplayForm(String displayForm) {
		this.displayForm = displayForm;
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

	public String getMorphFrequency() {
		return morphFrequency;
	}

	public void setMorphFrequency(String morphFrequency) {
		this.morphFrequency = morphFrequency;
	}

	public String getFormFrequency() {
		return formFrequency;
	}

	public void setFormFrequency(String formFrequency) {
		this.formFrequency = formFrequency;
	}

}
