package eki.ekilex.data;

import java.util.List;

import javax.persistence.Column;

import eki.common.constant.FormMode;
import eki.common.data.AbstractDataObject;

public class ParadigmFormTuple extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	@Column(name = "paradigm_id")
	private Long paradigmId;

	@Column(name = "inflection_type_nr")
	private String inflectionTypeNr;

	@Column(name = "form_id")
	private Long formId;

	@Column(name = "form")
	private String form;

	@Column(name = "mode")
	private FormMode mode;

	@Column(name = "components")
	private String[] components;

	@Column(name = "display_form")
	private String displayForm;

	@Column(name = "vocal_form")
	private String vocalForm;

	@Column(name = "morph_code")
	private String morphCode;

	@Column(name = "morph_value")
	private String morphValue;

	@Column(name = "form_frequencies")
	private List<String> formFrequencies;

	public Long getParadigmId() {
		return paradigmId;
	}

	public void setParadigmId(Long paradigmId) {
		this.paradigmId = paradigmId;
	}

	public String getInflectionTypeNr() {
		return inflectionTypeNr;
	}

	public void setInflectionTypeNr(String inflectionTypeNr) {
		this.inflectionTypeNr = inflectionTypeNr;
	}

	public Long getFormId() {
		return formId;
	}

	public void setFormId(Long formId) {
		this.formId = formId;
	}

	public String getForm() {
		return form;
	}

	public void setForm(String form) {
		this.form = form;
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

	public List<String> getFormFrequencies() {
		return formFrequencies;
	}

	public void setFormFrequencies(List<String> formFrequencies) {
		this.formFrequencies = formFrequencies;
	}

}
