package eki.wordweb.data.type;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import eki.common.constant.ReferenceType;
import eki.common.data.AbstractDataObject;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class TypeSourceLink extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long sourceLinkId;

	@JsonProperty("source_link_type")
	private ReferenceType sourceLinkType;

	private String sourceLinkName;

	private Long orderBy;

	private Long sourceId;

	private String sourceName;

	private String sourceValue;

	private String sourceValuePrese;

	@JsonProperty("is_source_public")
	private boolean isSourcePublic;

	public Long getSourceLinkId() {
		return sourceLinkId;
	}

	public void setSourceLinkId(Long sourceLinkId) {
		this.sourceLinkId = sourceLinkId;
	}

	public ReferenceType getSourceLinkType() {
		return sourceLinkType;
	}

	public void setSourceLinkType(ReferenceType sourceLinkType) {
		this.sourceLinkType = sourceLinkType;
	}

	public String getSourceLinkName() {
		return sourceLinkName;
	}

	public void setSourceLinkName(String sourceLinkName) {
		this.sourceLinkName = sourceLinkName;
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

	public String getSourceName() {
		return sourceName;
	}

	public void setSourceName(String sourceName) {
		this.sourceName = sourceName;
	}

	public String getSourceValue() {
		return sourceValue;
	}

	public void setSourceValue(String sourceValue) {
		this.sourceValue = sourceValue;
	}

	public String getSourceValuePrese() {
		return sourceValuePrese;
	}

	public void setSourceValuePrese(String sourceValuePrese) {
		this.sourceValuePrese = sourceValuePrese;
	}

	public boolean isSourcePublic() {
		return isSourcePublic;
	}

	public void setSourcePublic(boolean sourcePublic) {
		isSourcePublic = sourcePublic;
	}
}
