package eki.ekilex.data;

import java.util.List;

public class Freeform extends AbstractCreateUpdateEntity implements ValueAndPrese {

	private static final long serialVersionUID = 1L;

	private Long id;

	private Long parentId;

	private String freeformTypeCode;

	private String freeformTypeValue;

	private String value;

	private String valuePrese;

	private String lang;

	private Long orderBy;

	private List<SourceLink> sourceLinks;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	public String getFreeformTypeCode() {
		return freeformTypeCode;
	}

	public void setFreeformTypeCode(String freeformTypeCode) {
		this.freeformTypeCode = freeformTypeCode;
	}

	public String getFreeformTypeValue() {
		return freeformTypeValue;
	}

	public void setFreeformTypeValue(String freeformTypeValue) {
		this.freeformTypeValue = freeformTypeValue;
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

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public Long getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(Long orderBy) {
		this.orderBy = orderBy;
	}

	public List<SourceLink> getSourceLinks() {
		return sourceLinks;
	}

	public void setSourceLinks(List<SourceLink> sourceLinks) {
		this.sourceLinks = sourceLinks;
	}

}
