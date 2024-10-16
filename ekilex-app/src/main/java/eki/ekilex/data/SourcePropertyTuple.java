package eki.ekilex.data;

import eki.common.constant.SourceType;
import eki.common.data.AbstractDataObject;

public class SourcePropertyTuple extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long sourceId;

	private String sourceDatasetCode;

	private SourceType sourceType;

	private String sourceName;

	private String sourceValue;

	private String sourceValuePrese;

	private String sourceComment;

	private boolean isSourcePublic;

	private Long sourcePropertyId;

	private String sourcePropertyTypeCode;

	private String sourcePropertyValue;

	private boolean sourcePropertyMatch;

	public Long getSourceId() {
		return sourceId;
	}

	public void setSourceId(Long sourceId) {
		this.sourceId = sourceId;
	}

	public String getSourceDatasetCode() {
		return sourceDatasetCode;
	}

	public void setSourceDatasetCode(String sourceDatasetCode) {
		this.sourceDatasetCode = sourceDatasetCode;
	}

	public SourceType getSourceType() {
		return sourceType;
	}

	public void setSourceType(SourceType sourceType) {
		this.sourceType = sourceType;
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

	public String getSourceComment() {
		return sourceComment;
	}

	public void setSourceComment(String sourceComment) {
		this.sourceComment = sourceComment;
	}

	public boolean isSourcePublic() {
		return isSourcePublic;
	}

	public void setSourcePublic(boolean sourcePublic) {
		isSourcePublic = sourcePublic;
	}

	public Long getSourcePropertyId() {
		return sourcePropertyId;
	}

	public void setSourcePropertyId(Long sourcePropertyId) {
		this.sourcePropertyId = sourcePropertyId;
	}

	public String getSourcePropertyTypeCode() {
		return sourcePropertyTypeCode;
	}

	public void setSourcePropertyTypeCode(String sourcePropertyTypeCode) {
		this.sourcePropertyTypeCode = sourcePropertyTypeCode;
	}

	public String getSourcePropertyValue() {
		return sourcePropertyValue;
	}

	public void setSourcePropertyValue(String sourcePropertyValue) {
		this.sourcePropertyValue = sourcePropertyValue;
	}

	public boolean isSourcePropertyMatch() {
		return sourcePropertyMatch;
	}

	public void setSourcePropertyMatch(boolean sourcePropertyMatch) {
		this.sourcePropertyMatch = sourcePropertyMatch;
	}
}
