package eki.wordweb.data;

import eki.common.data.AbstractDataObject;
import eki.common.data.Classifier;

public class Relation extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long targetId;

	private String label;

	private String relationTypeCode;

	private Classifier relationType;

	public Long getTargetId() {
		return targetId;
	}

	public void setTargetId(Long targetId) {
		this.targetId = targetId;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getRelationTypeCode() {
		return relationTypeCode;
	}

	public void setRelationTypeCode(String relationTypeCode) {
		this.relationTypeCode = relationTypeCode;
	}

	public Classifier getRelationType() {
		return relationType;
	}

	public void setRelationType(Classifier relationType) {
		this.relationType = relationType;
	}
}
