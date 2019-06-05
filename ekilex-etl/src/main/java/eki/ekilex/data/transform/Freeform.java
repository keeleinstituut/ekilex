package eki.ekilex.data.transform;

import java.sql.Timestamp;

import eki.common.constant.FreeformType;
import eki.common.data.AbstractDataObject;

public class Freeform extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long freeformId;

	private Long parentId;

	private FreeformType type;

	private String valueText;

	private String valuePrese;

	private Timestamp valueDate;

	private Float valueNumber;

	private String[] valueArray;

	private String classifName;

	private String classifCode;

	private String langCode;

	private Long orderBy;

	public Long getFreeformId() {
		return freeformId;
	}

	public void setFreeformId(Long freeformId) {
		this.freeformId = freeformId;
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

	public Float getValueNumber() {
		return valueNumber;
	}

	public void setValueNumber(Float valueNumber) {
		this.valueNumber = valueNumber;
	}

	public String[] getValueArray() {
		return valueArray;
	}

	public void setValueArray(String[] valueArray) {
		this.valueArray = valueArray;
	}

	public String getClassifName() {
		return classifName;
	}

	public void setClassifName(String classifName) {
		this.classifName = classifName;
	}

	public String getClassifCode() {
		return classifCode;
	}

	public void setClassifCode(String classifCode) {
		this.classifCode = classifCode;
	}

	public String getLangCode() {
		return langCode;
	}

	public void setLangCode(String langCode) {
		this.langCode = langCode;
	}

	public Long getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(Long orderBy) {
		this.orderBy = orderBy;
	}

}
