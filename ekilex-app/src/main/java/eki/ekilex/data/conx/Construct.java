package eki.ekilex.data.conx;

import eki.common.data.AbstractDataObject;

public class Construct extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long id;

	private String name;

	private String description;

	private String constructTypeCode;

	private String constructSubtypeCode;

	private String schematicityCode;

	private String proficiencyLevelCode;

	private String lang;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getConstructTypeCode() {
		return constructTypeCode;
	}

	public void setConstructTypeCode(String constructTypeCode) {
		this.constructTypeCode = constructTypeCode;
	}

	public String getConstructSubtypeCode() {
		return constructSubtypeCode;
	}

	public void setConstructSubtypeCode(String constructSubtypeCode) {
		this.constructSubtypeCode = constructSubtypeCode;
	}

	public String getSchematicityCode() {
		return schematicityCode;
	}

	public void setSchematicityCode(String schematicityCode) {
		this.schematicityCode = schematicityCode;
	}

	public String getProficiencyLevelCode() {
		return proficiencyLevelCode;
	}

	public void setProficiencyLevelCode(String proficiencyLevelCode) {
		this.proficiencyLevelCode = proficiencyLevelCode;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

}
