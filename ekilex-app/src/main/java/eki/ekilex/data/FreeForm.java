package eki.ekilex.data;

import java.sql.Timestamp;
import java.util.List;

import eki.common.constant.Complexity;

public class FreeForm extends AbstractCreateUpdateEntity {

	private static final long serialVersionUID = 1L;

	private Long id;

	private Long parentId;

	private String freeformTypeCode;

	private String freeformTypeValue;

	private String valueText;

	private String valuePrese;

	private Timestamp valueDate;

	private String lang;

	private Complexity complexity;

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

	public String getValueText() {
		return valueText;
	}

	public void setValueText(String valueText) {
		this.valueText = valueText;
	}

	public String getValuePrese() {
		return valuePrese;
	}

	public void setValuePrese(String valuePrese) {
		this.valuePrese = valuePrese;
	}

	public Timestamp getValueDate() {
		return valueDate;
	}

	public void setValueDate(Timestamp valueDate) {
		this.valueDate = valueDate;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public Complexity getComplexity() {
		return complexity;
	}

	public void setComplexity(Complexity complexity) {
		this.complexity = complexity;
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
