package eki.wordweb.data;

import eki.common.constant.DatasetType;
import eki.common.data.AbstractDataObject;

public class Dataset extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private String code;

	private DatasetType type;

	private String name;

	private String description;

	private String imageUrl;

	private boolean isSuperior;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public DatasetType getType() {
		return type;
	}

	public void setType(DatasetType type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public boolean isSuperior() {
		return isSuperior;
	}

	public void setSuperior(boolean isSuperior) {
		this.isSuperior = isSuperior;
	}

}
