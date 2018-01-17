package eki.ekilex.data;

import javax.persistence.Column;

import eki.common.data.AbstractDataObject;

public class FormRelation extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	@Column(name = "paradigm_id")
	private Long paradigmId;

	@Column(name = "form1_id")
	private Long form1Id;

	@Column(name = "form1_value")
	private String form1Value;

	@Column(name = "form2_id")
	private Long form2Id;

	@Column(name = "form2_value")
	private String form2Value;

	@Column(name = "rel_type_code")
	private String relationTypeCode;

	@Column(name = "rel_type_label")
	private String relationTypeLabel;

	public Long getParadigmId() {
		return paradigmId;
	}

	public void setParadigmId(Long paradigmId) {
		this.paradigmId = paradigmId;
	}

	public Long getForm1Id() {
		return form1Id;
	}

	public void setForm1Id(Long form1Id) {
		this.form1Id = form1Id;
	}

	public String getForm1Value() {
		return form1Value;
	}

	public void setForm1Value(String form1Value) {
		this.form1Value = form1Value;
	}

	public Long getForm2Id() {
		return form2Id;
	}

	public void setForm2Id(Long form2Id) {
		this.form2Id = form2Id;
	}

	public String getForm2Value() {
		return form2Value;
	}

	public void setForm2Value(String form2Value) {
		this.form2Value = form2Value;
	}

	public String getRelationTypeCode() {
		return relationTypeCode;
	}

	public void setRelationTypeCode(String relationTypeCode) {
		this.relationTypeCode = relationTypeCode;
	}

	public String getRelationTypeLabel() {
		return relationTypeLabel;
	}

	public void setRelationTypeLabel(String relationTypeLabel) {
		this.relationTypeLabel = relationTypeLabel;
	}

}
