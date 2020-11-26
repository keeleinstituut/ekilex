package eki.ekilex.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import eki.common.constant.FormMode;
import eki.common.data.AbstractDataObject;

@JsonIgnoreProperties({"displayMorphCode"})
public class Form extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long id;

	private String value;

	private String valuePrese;

	private FormMode mode;

	private String[] components;

	private String displayForm;

	private String vocalForm;

	private String morphCode;

	private String morphValue;

	private String morphFrequency;

	private String formFrequency;

	private boolean isDisplayMorphCode;

	public Form() {
	}

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

	public String getValuePrese() {
		return valuePrese;
	}

	public void setValuePrese(String valuePrese) {
		this.valuePrese = valuePrese;
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

	public String getVocalForm() {
		return vocalForm;
	}

	public void setVocalForm(String vocalForm) {
		this.vocalForm = vocalForm;
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

	public boolean isDisplayMorphCode() {
		return isDisplayMorphCode;
	}

	public void setDisplayMorphCode(boolean isDisplayMorphCode) {
		this.isDisplayMorphCode = isDisplayMorphCode;
	}

}
