package eki.ekilex.data;

import java.sql.Timestamp;

import eki.common.constant.FreeformType;
import eki.common.constant.SourceType;
import eki.common.data.AbstractDataObject;

public class SourcePropertyTuple extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long sourceId;

	private SourceType sourceType;

	private String sourceName;

	private String sourceDescription;

	private String sourceComment;

	private boolean isSourcePublic;

	private Long sourcePropertyId;

	private FreeformType sourcePropertyType;

	private String sourcePropertyValueText;

	private Timestamp sourcePropertyValueDate;

	private boolean sourcePropertyMatch;

	public Long getSourceId() {
		return sourceId;
	}

	public void setSourceId(Long sourceId) {
		this.sourceId = sourceId;
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

	public String getSourceDescription() {
		return sourceDescription;
	}

	public void setSourceDescription(String sourceDescription) {
		this.sourceDescription = sourceDescription;
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

	public FreeformType getSourcePropertyType() {
		return sourcePropertyType;
	}

	public void setSourcePropertyType(FreeformType sourcePropertyType) {
		this.sourcePropertyType = sourcePropertyType;
	}

	public String getSourcePropertyValueText() {
		return sourcePropertyValueText;
	}

	public void setSourcePropertyValueText(String sourcePropertyValueText) {
		this.sourcePropertyValueText = sourcePropertyValueText;
	}

	public Timestamp getSourcePropertyValueDate() {
		return sourcePropertyValueDate;
	}

	public void setSourcePropertyValueDate(Timestamp sourcePropertyValueDate) {
		this.sourcePropertyValueDate = sourcePropertyValueDate;
	}

	public boolean isSourcePropertyMatch() {
		return sourcePropertyMatch;
	}

	public void setSourcePropertyMatch(boolean sourcePropertyMatch) {
		this.sourcePropertyMatch = sourcePropertyMatch;
	}
}
