package eki.ekilex.data.api;

import eki.common.data.AbstractDataObject;

public class Form extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long id;

	private Long paradigmId;

	private String morphGroup1;

	private String morphGroup2;

	private String morphGroup3;

	private Integer displayLevel;

	private String morphCode;

	private boolean morphExists;

	private boolean isQuestionable;

	private String value;

	private String valuePrese;

	private String[] components;

	private String displayForm;

	private String audioFile;

	private Long orderBy;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getParadigmId() {
		return paradigmId;
	}

	public void setParadigmId(Long paradigmId) {
		this.paradigmId = paradigmId;
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

	public boolean isMorphExists() {
		return morphExists;
	}

	public void setMorphExists(boolean morphExists) {
		this.morphExists = morphExists;
	}

	public boolean isQuestionable() {
		return isQuestionable;
	}

	public void setQuestionable(boolean isQuestionable) {
		this.isQuestionable = isQuestionable;
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

	public String getAudioFile() {
		return audioFile;
	}

	public void setAudioFile(String audioFile) {
		this.audioFile = audioFile;
	}

	public Long getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(Long orderBy) {
		this.orderBy = orderBy;
	}

}
