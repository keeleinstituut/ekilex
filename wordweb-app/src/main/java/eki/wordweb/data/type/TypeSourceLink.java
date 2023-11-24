package eki.wordweb.data.type;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import eki.common.constant.ReferenceOwner;
import eki.common.constant.ReferenceType;
import eki.common.data.AbstractDataObject;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class TypeSourceLink extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private ReferenceOwner refOwner;

	private Long ownerId;

	private Long sourceLinkId;

	@JsonProperty("source_link_type")
	private ReferenceType type;

	private String name;

	private String value;

	private Long orderBy;

	private Long sourceId;

	private List<String> sourceProps;

	public ReferenceOwner getRefOwner() {
		return refOwner;
	}

	public void setRefOwner(ReferenceOwner refOwner) {
		this.refOwner = refOwner;
	}

	public Long getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(Long ownerId) {
		this.ownerId = ownerId;
	}

	public Long getSourceLinkId() {
		return sourceLinkId;
	}

	public void setSourceLinkId(Long sourceLinkId) {
		this.sourceLinkId = sourceLinkId;
	}

	public ReferenceType getType() {
		return type;
	}

	public void setType(ReferenceType type) {
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

	public Long getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(Long orderBy) {
		this.orderBy = orderBy;
	}

	public Long getSourceId() {
		return sourceId;
	}

	public void setSourceId(Long sourceId) {
		this.sourceId = sourceId;
	}

	public List<String> getSourceProps() {
		return sourceProps;
	}

	public void setSourceProps(List<String> sourceProps) {
		this.sourceProps = sourceProps;
	}

}
