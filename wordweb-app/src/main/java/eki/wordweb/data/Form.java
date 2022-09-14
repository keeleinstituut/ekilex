package eki.wordweb.data;

import java.util.List;

import eki.common.data.AbstractDataObject;
import eki.common.data.Classifier;

public class Form extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long formId;

	private Long paradigmId;

	private String wordClass;

	private String paradigmComment;

	private String inflectionType;

	private String inflectionTypeNr;

	private String morphGroup1;

	private String morphGroup2;

	private String morphGroup3;

	private Integer displayLevel;

	private String morphCode;

	private Classifier morph;

	private boolean morphExists;

	private boolean isQuestionable;

	private String value;

	private String valuePrese;

	private String formsWrapup;

	private List<String> components;

	private String displayForm;

	private String displayFormsWrapup;

	private String audioFile;

	private Integer orderBy;

	private Float formFreqValue;

	private Long formFreqRank;

	private Long formFreqRankMax;

	private int formFreqRankScaled;

	private Float morphFreqValue;

	private Long morphFreqRank;

	private Long morphFreqRankMax;

	private int morphFreqRankScaled;

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

	public String getWordClass() {
		return wordClass;
	}

	public void setWordClass(String wordClass) {
		this.wordClass = wordClass;
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

	public Float getFormFreqValue() {
		return formFreqValue;
	}

	public void setFormFreqValue(Float formFreqValue) {
		this.formFreqValue = formFreqValue;
	}

	public Long getFormFreqRank() {
		return formFreqRank;
	}

	public void setFormFreqRank(Long formFreqRank) {
		this.formFreqRank = formFreqRank;
	}

	public Long getFormFreqRankMax() {
		return formFreqRankMax;
	}

	public void setFormFreqRankMax(Long formFreqRankMax) {
		this.formFreqRankMax = formFreqRankMax;
	}

	public int getFormFreqRankScaled() {
		return formFreqRankScaled;
	}

	public void setFormFreqRankScaled(int formFreqRankScaled) {
		this.formFreqRankScaled = formFreqRankScaled;
	}

	public Float getMorphFreqValue() {
		return morphFreqValue;
	}

	public void setMorphFreqValue(Float morphFreqValue) {
		this.morphFreqValue = morphFreqValue;
	}

	public Long getMorphFreqRank() {
		return morphFreqRank;
	}

	public void setMorphFreqRank(Long morphFreqRank) {
		this.morphFreqRank = morphFreqRank;
	}

	public Long getMorphFreqRankMax() {
		return morphFreqRankMax;
	}

	public void setMorphFreqRankMax(Long morphFreqRankMax) {
		this.morphFreqRankMax = morphFreqRankMax;
	}

	public int getMorphFreqRankScaled() {
		return morphFreqRankScaled;
	}

	public void setMorphFreqRankScaled(int morphFreqRankScaled) {
		this.morphFreqRankScaled = morphFreqRankScaled;
	}

}
