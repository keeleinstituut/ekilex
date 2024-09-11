package eki.ekilex.data;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import eki.common.constant.Complexity;
import eki.common.data.AbstractDataObject;

public class FreeForm extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long id;

	private Long parentId;

	private String freeformTypeCode;

	private Classifier freeformType;

	private String valueText;

	private String valuePrese;

	private Timestamp valueDate;

	private String lang;

	private Complexity complexity;

	private Long orderBy;

	private List<FreeForm> children = new ArrayList<>();

	private boolean isPublic;

	private String modifiedBy;

	private Timestamp modifiedOn;

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

	public String getFreeformTypeCode() {
		return freeformTypeCode;
	}

	public void setFreeformTypeCode(String freeformTypeCode) {
		this.freeformTypeCode = freeformTypeCode;
	}

	public Classifier getFreeformType() {
		return freeformType;
	}

	public void setFreeformType(Classifier freeformType) {
		this.freeformType = freeformType;
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

	public boolean isPublic() {
		return isPublic;
	}

	public void setPublic(boolean isPublic) {
		this.isPublic = isPublic;
	}

	public String getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public Timestamp getModifiedOn() {
		return modifiedOn;
	}

	public void setModifiedOn(Timestamp modifiedOn) {
		this.modifiedOn = modifiedOn;
	}

}
