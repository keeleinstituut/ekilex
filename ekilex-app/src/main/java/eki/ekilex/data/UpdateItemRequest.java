package eki.ekilex.data;

import java.math.BigDecimal;

import eki.common.data.AbstractDataObject;

//lots of properties just in case. should unify usage to less
public class UpdateItemRequest extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private String target;

	private String opCode;

	private String opName;

	private Long id;

	private Long id2;

	private String code;

	private Integer index;

	private String value;

	private String value2;

	private String currentValue;

	private BigDecimal numberValue;

	private String language;

	private boolean selected;

	private boolean isPublic;

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public String getOpCode() {
		return opCode;
	}

	public void setOpCode(String opCode) {
		this.opCode = opCode;
	}

	public String getOpName() {
		return opName;
	}

	public void setOpName(String opName) {
		this.opName = opName;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getId2() {
		return id2;
	}

	public void setId2(Long id2) {
		this.id2 = id2;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Integer getIndex() {
		return index;
	}

	public void setIndex(Integer index) {
		this.index = index;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getValue2() {
		return value2;
	}

	public void setValue2(String value2) {
		this.value2 = value2;
	}

	public String getCurrentValue() {
		return currentValue;
	}

	public void setCurrentValue(String currentValue) {
		this.currentValue = currentValue;
	}

	public BigDecimal getNumberValue() {
		return numberValue;
	}

	public void setNumberValue(BigDecimal numberValue) {
		this.numberValue = numberValue;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public boolean isPublic() {
		return isPublic;
	}

	public void setPublic(boolean isPublic) {
		this.isPublic = isPublic;
	}
}
