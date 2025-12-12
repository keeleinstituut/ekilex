package eki.ekilex.data.api;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import eki.common.data.AbstractDataObject;
import io.swagger.v3.oas.annotations.media.Schema;
@Schema(description = "Represents a shortened version of word details")
@JsonInclude(Include.NON_NULL)
public class Word extends AbstractDataObject {

	private static final long serialVersionUID = 1L;
	@Schema(example = "218160")
	private Long wordId;
	@Schema(hidden = true)
	private Long meaningId;
	@Schema(hidden = true)
	private String lexemeDataset;
	@Schema(example = "plov")
	private String value;
	@Schema(example = "plov")
	private String valuePrese;
	@Schema(example = "est")
	private String lang;
	@Schema(example = "1")
	private Integer homonymNr;
	@Schema(hidden = true)
	private String displayMorphCode;
	@Schema(hidden = true)
	private String genderCode;
	@Schema(hidden = true)
	private String aspectCode;
	@Schema(example = "[pl`off]")
	private String vocalForm;
	@Schema(example = "pl`ov")
	private String morphophonoForm;
	@Schema(example ="[\"eira hääldust\"]")
	private List<String> tags;
	@Schema(example = "true")
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
