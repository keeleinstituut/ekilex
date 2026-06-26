package eki.ekilex.data.conx;

import eki.common.data.AbstractDataObject;

public class Construct extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long id;

	private String nameSimple;

	private String nameDetail;

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

	public String getNameSimple() {
		return nameSimple;
	}

	public void setNameSimple(String nameSimple) {
		this.nameSimple = nameSimple;
	}

	public String getNameDetail() {
		return nameDetail;
	}

	public void setNameDetail(String nameDetail) {
		this.nameDetail = nameDetail;
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
