package eki.ekilex.data;

import java.sql.Timestamp;
import java.util.List;

import eki.common.constant.FreeformType;
import eki.common.data.AbstractDataObject;

public class SourceMember extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long id;

	private FreeformType type;

	private String valueText;

	private Timestamp valueDate;

	private boolean valueMatch;

	private List<SourceMember> children;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public FreeformType getType() {
		return type;
	}

	public void setType(FreeformType type) {
		this.type = type;
	}

	public String getValueText() {
		return valueText;
	}

	public void setValueText(String valueText) {
		this.valueText = valueText;
	}

	public Timestamp getValueDate() {
		return valueDate;
	}

	public void setValueDate(Timestamp valueDate) {
		this.valueDate = valueDate;
	}

	public boolean isValueMatch() {
		return valueMatch;
	}

	public void setValueMatch(boolean valueMatch) {
		this.valueMatch = valueMatch;
	}

	public List<SourceMember> getChildren() {
		return children;
	}

	public void setChildren(List<SourceMember> children) {
		this.children = children;
	}

}
