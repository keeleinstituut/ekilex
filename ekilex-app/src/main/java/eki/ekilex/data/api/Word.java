package eki.ekilex.data.api;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import eki.common.data.AbstractDataObject;

@JsonInclude(Include.NON_NULL)
public class Word extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long wordId;

	private Long meaningId;

	private String lexemeDataset;

	private String value;

	private String valuePrese;

	private String lang;

	private Integer homonymNr;

	private String displayMorphCode;

	private String genderCode;

	private String aspectCode;

	private String vocalForm;

	private String morphophonoForm;

	private List<String> tags;

	private boolean morphExists;

	public Long getWordId() {
		return wordId;
	}

	public void setWordId(Long wordId) {
		this.wordId = wordId;
	}

	public Long getMeaningId() {
		return meaningId;
	}

	public void setMeaningId(Long meaningId) {
		this.meaningId = meaningId;
	}

	public String getLexemeDataset() {
		return lexemeDataset;
	}

	public void setLexemeDataset(String lexemeDataset) {
		this.lexemeDataset = lexemeDataset;
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

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public Integer getHomonymNr() {
		return homonymNr;
	}

	public void setHomonymNr(Integer homonymNr) {
		this.homonymNr = homonymNr;
	}

	public String getDisplayMorphCode() {
		return displayMorphCode;
	}

	public void setDisplayMorphCode(String displayMorphCode) {
		this.displayMorphCode = displayMorphCode;
	}

	public String getGenderCode() {
		return genderCode;
	}

	public void setGenderCode(String genderCode) {
		this.genderCode = genderCode;
	}

	public String getAspectCode() {
		return aspectCode;
	}

	public void setAspectCode(String aspectCode) {
		this.aspectCode = aspectCode;
	}

	public String getVocalForm() {
		return vocalForm;
	}

	public void setVocalForm(String vocalForm) {
		this.vocalForm = vocalForm;
	}

	public String getMorphophonoForm() {
		return morphophonoForm;
	}

	public void setMorphophonoForm(String morphophonoForm) {
		this.morphophonoForm = morphophonoForm;
	}

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	public boolean isMorphExists() {
		return morphExists;
	}

	public void setMorphExists(boolean morphExists) {
		this.morphExists = morphExists;
	}
}
