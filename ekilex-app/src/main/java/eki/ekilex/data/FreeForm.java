package eki.ekilex.data;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import eki.common.constant.Complexity;
import eki.common.constant.FreeformType;
import eki.common.data.AbstractDataObject;

public class FreeForm extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long id;

	private Long parentId;

	private FreeformType type;

	private String valueText;

	private String valuePrese;

	private Timestamp valueDate;

	private String lang;

	private Complexity complexity;

	private Long orderBy;

	private List<FreeForm> children = new ArrayList<>();

	private Boolean isPublic;

	private String modifiedBy;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
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

	public String getValuePrese() {
		return valuePrese;
	}

	public void setValuePrese(String valuePrese) {
		this.valuePrese = valuePrese;
	}

	public Timestamp getValueDate() {
		return valueDate;
	}

	public void setValueDate(Timestamp valueDate) {
		this.valueDate = valueDate;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public Complexity getComplexity() {
		return complexity;
	}

	public void setComplexity(Complexity complexity) {
		this.complexity = complexity;
	}

	public Long getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(Long orderBy) {
		this.orderBy = orderBy;
	}

	public List<FreeForm> getChildren() {
		return children;
	}

	public void setChildren(List<FreeForm> children) {
		this.children = children;
	}

	public Boolean isPublic() {
		return isPublic;
	}

	public void setPublic(Boolean aPublic) {
		isPublic = aPublic;
	}

	public String getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}
}
