package eki.ekilex.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import eki.common.data.AbstractDataObject;
import io.swagger.v3.oas.annotations.media.Schema;

@JsonIgnoreProperties({"displayMorphCode"})
public class Form extends AbstractDataObject {

	private static final long serialVersionUID = 1L;
	@Schema(example = "12526010")
	private Long id;
	@Schema(example = "kobarat")
	private String value;
	@Schema(example = "kobara<eki-form>t</eki-form>")
	private String valuePrese;
	@Schema(example = "null")
	private String[] components;
	@Schema(example = "kobara[t")
	private String displayForm;
	@Schema(example = "SgP")
	private String morphCode;
	@Schema(example = "ainsuse osastav")
	private String morphValue;
	@Schema(example = "null")
	private String morphFrequency;
	@Schema(example = "null")
	private String formFrequency;
	@Schema(hidden = true)
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

	public boolean isDisplayMorphCode() {
		return isDisplayMorphCode;
	}

	public void setDisplayMorphCode(boolean isDisplayMorphCode) {
		this.isDisplayMorphCode = isDisplayMorphCode;
	}

}
