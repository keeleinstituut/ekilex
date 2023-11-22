package eki.ekilex.data;

import java.util.List;

import eki.common.constant.SourceType;
import eki.common.data.AbstractDataObject;

public class SourceRequest extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private SourceType type;

	private String name;

	private String valuePrese;

	private String comment;

	private boolean isPublic;

	private List<SourceProperty> properties;

	private Long id;

	private String opCode;

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

	public boolean isPublic() {
		return isPublic;
	}

	public void setPublic(boolean isPublic) {
		this.isPublic = isPublic;
	}

	public List<SourceProperty> getProperties() {
		return properties;
	}

	public void setProperties(List<SourceProperty> properties) {
		this.properties = properties;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getOpCode() {
		return opCode;
	}

	public void setOpCode(String opCode) {
		this.opCode = opCode;
	}
}
