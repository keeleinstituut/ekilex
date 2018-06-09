package eki.ekilex.data;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class Usage extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long id;

	private String value;

	private String lang;

	private String typeCode;

	private String typeValue;

	private String author;

	private String authorType;

	private String translator;

	private List<UsageTranslation> translations;

	private List<UsageDefinition> definitions;

	private List<RefLink> refLinks;

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

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getAuthorType() {
		return authorType;
	}

	public void setAuthorType(String authorType) {
		this.authorType = authorType;
	}

	public String getTranslator() {
		return translator;
	}

	public void setTranslator(String translator) {
		this.translator = translator;
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

	public List<RefLink> getRefLinks() {
		return refLinks;
	}

	public void setRefLinks(List<RefLink> refLinks) {
		this.refLinks = refLinks;
	}

}
