package eki.ekilex.data;

import java.util.List;

import eki.common.constant.Complexity;

public class Usage extends AbstractPublicEntity {

	private static final long serialVersionUID = 1L;

	private Long id;

	private String value;

	private String lang;

	private Complexity complexity;

	private Long orderBy;

	private String typeCode;

	private String typeValue;

	private List<UsageTranslation> translations;

	private List<UsageDefinition> definitions;

	private List<SourceLink> authors;

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

	public String getTypeCode() {
		return typeCode;
	}

	public void setTypeCode(String typeCode) {
		this.typeCode = typeCode;
	}

	public String getTypeValue() {
		return typeValue;
	}

	public void setTypeValue(String typeValue) {
		this.typeValue = typeValue;
	}

	public List<UsageTranslation> getTranslations() {
		return translations;
	}

	public void setTranslations(List<UsageTranslation> translations) {
		this.translations = translations;
	}

	public List<UsageDefinition> getDefinitions() {
		return definitions;
	}

	public void setDefinitions(List<UsageDefinition> definitions) {
		this.definitions = definitions;
	}

	public List<SourceLink> getAuthors() {
		return authors;
	}

	public void setAuthors(List<SourceLink> authors) {
		this.authors = authors;
	}

	public List<SourceLink> getSourceLinks() {
		return sourceLinks;
	}

	public void setSourceLinks(List<SourceLink> sourceLinks) {
		this.sourceLinks = sourceLinks;
	}

}
