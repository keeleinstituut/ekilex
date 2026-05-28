package eki.ekilex.data.conx;

import eki.common.data.AbstractDataObject;

public class Sentence extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long id;

	private Long constructId;

	private String type;

	private String proficiencyLevelCode;

	private String value;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getConstructId() {
		return constructId;
	}

	public void setConstructId(Long constructId) {
		this.constructId = constructId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getProficiencyLevelCode() {
		return proficiencyLevelCode;
	}

	public void setProficiencyLevelCode(String proficiencyLevelCode) {
		this.proficiencyLevelCode = proficiencyLevelCode;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
