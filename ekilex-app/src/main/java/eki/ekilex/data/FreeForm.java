package eki.ekilex.data;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;

import eki.common.constant.FreeformType;
import eki.common.data.AbstractDataObject;

public class FreeForm extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	@Column(name = "id")
	private Long id;

	@Column(name = "type")
	private FreeformType type;

	@Column(name = "value_text")
	private String valueText;

	@Column(name = "value_date")
	private Timestamp valueDate;

	@Column(name = "order_by")
	private Long orderBy;

	private List<FreeForm> children = new ArrayList<>();

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

	public List<FreeForm> getChildren() {
		return children;
	}

	public void setChildren(List<FreeForm> children) {
		this.children = children;
	}

	public Long getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(Long orderBy) {
		this.orderBy = orderBy;
	}
}
