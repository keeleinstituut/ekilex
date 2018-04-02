package eki.wordweb.data;

import java.util.List;

import eki.common.data.AbstractDataObject;
import eki.common.data.Classifier;

public class Form extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long paradigmId;

	private Long formId;

	private String form;

	private String morphCode;

	private Classifier morph;

	private List<String> components;

	private String displayForm;

	private String vocalForm;

	private String soundFile;

	private boolean isWord;

	public Long getParadigmId() {
		return paradigmId;
	}

	public void setParadigmId(Long paradigmId) {
		this.paradigmId = paradigmId;
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

	public String getVocalForm() {
		return vocalForm;
	}

	public void setVocalForm(String vocalForm) {
		this.vocalForm = vocalForm;
	}

	public String getSoundFile() {
		return soundFile;
	}

	public void setSoundFile(String soundFile) {
		this.soundFile = soundFile;
	}

	public boolean isWord() {
		return isWord;
	}

	public void setWord(boolean isWord) {
		this.isWord = isWord;
	}

}
