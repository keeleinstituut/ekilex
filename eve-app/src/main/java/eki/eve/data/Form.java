package eki.eve.data;

import javax.persistence.Column;
import java.util.function.Consumer;

public class Form extends DomainData {

	@Column(name = "id")
	private Long id;

	@Column(name = "value")
	private String value;

	@Column(name = "display_form")
	private String displayForm;

	@Column(name = "vocal_form")
	private String vocalForm;

	@Column(name = "morph_code")
	private String morphCode;

	@Column(name = "morph_value")
	private String morphValue;

	public Form() {
	}

	public Form(Consumer<Form> builder) {
		builder.accept(this);
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
}
