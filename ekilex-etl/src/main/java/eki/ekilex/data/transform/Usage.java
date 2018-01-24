package eki.ekilex.data.transform;

import eki.common.data.AbstractDataObject;

public class Usage extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private String value;

	private String author;

	private String usageType;

	private String authorType;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getUsageType() {
		return usageType;
	}

	public void setUsageType(String usageType) {
		this.usageType = usageType;
	}

	public String getAuthorType() {
		return authorType;
	}

	public void setAuthorType(String authorType) {
		this.authorType = authorType;
	}
}
