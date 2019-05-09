package eki.wordweb.data;

import java.util.List;

import eki.common.constant.FormMode;
import eki.common.data.AbstractDataObject;
import eki.common.data.Classifier;

public class Form extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long formId;

	private Long paradigmId;

	private String inflectionType;

	private FormMode mode;

	private String morphGroup1;

	private String morphGroup2;

	private String morphGroup3;

	private Integer displayLevel;

	private String morphCode;

	private Classifier morph;

	private Boolean morphExists;

	private String form;

	private String formsWrapup;

	private List<String> components;

	private String displayForm;

	private String displayFormsWrapup;

	private String vocalForm;

	private String audioFile;

	private Integer orderBy;

	public Long getFormId() {
		return formId;
	}

	public void setFormId(Long formId) {
		this.formId = formId;
	}

	public Long getParadigmId() {
		return paradigmId;
	}

	public void setParadigmId(Long paradigmId) {
		this.paradigmId = paradigmId;
	}

	public String getInflectionType() {
		return inflectionType;
	}

	public void setInflectionType(String inflectionType) {
		this.inflectionType = inflectionType;
	}

	public FormMode getMode() {
		return mode;
	}

	public void setMode(FormMode mode) {
		this.mode = mode;
	}

	public String getMorphGroup1() {
		return morphGroup1;
	}

	public void setMorphGroup1(String morphGroup1) {
		this.morphGroup1 = morphGroup1;
	}

	public String getMorphGroup2() {
		return morphGroup2;
	}

	public void setMorphGroup2(String morphGroup2) {
		this.morphGroup2 = morphGroup2;
	}

	public String getMorphGroup3() {
		return morphGroup3;
	}

	public void setMorphGroup3(String morphGroup3) {
		this.morphGroup3 = morphGroup3;
	}

	public Integer getDisplayLevel() {
		return displayLevel;
	}

	public void setDisplayLevel(Integer displayLevel) {
		this.displayLevel = displayLevel;
	}

	public String getMorphCode() {
		return morphCode;
	}

	public void setMorphCode(String morphCode) {
		this.morphCode = morphCode;
	}

	public Classifier getMorph() {
		return morph;
	}

	public void setMorph(Classifier morph) {
		this.morph = morph;
	}

	public Boolean getMorphExists() {
		return morphExists;
	}

	public void setMorphExists(Boolean morphExists) {
		this.morphExists = morphExists;
	}

	public String getForm() {
		return form;
	}

	public void setForm(String form) {
		this.form = form;
	}

	public String getFormsWrapup() {
		return formsWrapup;
	}

	public void setFormsWrapup(String formsWrapup) {
		this.formsWrapup = formsWrapup;
	}

	public List<String> getComponents() {
		return components;
	}

	public void setComponents(List<String> components) {
		this.components = components;
	}

	public String getDisplayForm() {
		return displayForm;
	}

	public void setDisplayForm(String displayForm) {
		this.displayForm = displayForm;
	}

	public String getDisplayFormsWrapup() {
		return displayFormsWrapup;
	}

	public void setDisplayFormsWrapup(String displayFormsWrapup) {
		this.displayFormsWrapup = displayFormsWrapup;
	}

	public String getVocalForm() {
		return vocalForm;
	}

	public void setVocalForm(String vocalForm) {
		this.vocalForm = vocalForm;
	}

	public String getAudioFile() {
		return audioFile;
	}

	public void setAudioFile(String audioFile) {
		this.audioFile = audioFile;
	}

	public Integer getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(Integer orderBy) {
		this.orderBy = orderBy;
	}

}
