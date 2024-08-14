package eki.ekilex.data;

import eki.common.constant.Complexity;
import eki.common.data.AbstractDataObject;

public class CreateItemRequest extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private String opCode;

	private Long id;

	private Long id2;

	private Long id3;

	private String value;

	private String value2;

	private String itemType;

	private String language;

	private String dataset;

	private Complexity complexity;

	private boolean isPublic;

	public String getOpCode() {
		return opCode;
	}

	public void setOpCode(String opCode) {
		this.opCode = opCode;
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

	public Long getId3() {
		return id3;
	}

	public void setId3(Long id3) {
		this.id3 = id3;
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

	public String getItemType() {
		return itemType;
	}

	public void setItemType(String itemType) {
		this.itemType = itemType;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getDataset() {
		return dataset;
	}

	public void setDataset(String dataset) {
		this.dataset = dataset;
	}

	public Complexity getComplexity() {
		return complexity;
	}

	public void setComplexity(Complexity complexity) {
		this.complexity = complexity;
	}

	public boolean isPublic() {
		return isPublic;
	}

	public void setPublic(boolean isPublic) {
		this.isPublic = isPublic;
	}

}
