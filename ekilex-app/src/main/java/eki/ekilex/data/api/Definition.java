package eki.ekilex.data.api;

import java.util.List;

import eki.common.data.AbstractDataObject;
import eki.ekilex.data.SourceLink;

// TODO should use common class
public class Definition extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long id;

	private String value;

	private String valuePrese;

	private String lang;

	private String definitionTypeCode;

	private List<SourceLink> sourceLinks;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public String getDefinitionTypeCode() {
		return definitionTypeCode;
	}

	public void setDefinitionTypeCode(String definitionTypeCode) {
		this.definitionTypeCode = definitionTypeCode;
	}

	public List<SourceLink> getSourceLinks() {
		return sourceLinks;
	}

	public void setSourceLinks(List<SourceLink> sourceLinks) {
		this.sourceLinks = sourceLinks;
	}
}
