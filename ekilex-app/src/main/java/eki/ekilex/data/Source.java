package eki.ekilex.data;

import eki.common.constant.SourceType;

public class Source extends AbstractGrantEntity {

	private static final long serialVersionUID = 1L;

	private Long id;

	private String datasetCode;

	private SourceType type;

	private String name;

	private String value;

	private String valuePrese;

	private String comment;

	private boolean isPriority;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDatasetCode() {
		return datasetCode;
	}

	public void setDatasetCode(String datasetCode) {
		this.datasetCode = datasetCode;
	}

	public SourceType getType() {
		return type;
	}

	public void setType(SourceType type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getValuePrese() {
		return valuePrese;
	}

	public void setValuePrese(String valuePrese) {
		this.valuePrese = valuePrese;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public boolean isPriority() {
		return isPriority;
	}

	public void setPriority(boolean isPriority) {
		this.isPriority = isPriority;
	}
}
