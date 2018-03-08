package eki.ekilex.data;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class UsageMember extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long id;

	private String type;

	private String value;

	private String lang;

	private String author;

	private String translator;

	//currently only sources
	private List<RefLink> refLinks;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
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

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getTranslator() {
		return translator;
	}

	public void setTranslator(String translator) {
		this.translator = translator;
	}

	public List<RefLink> getRefLinks() {
		return refLinks;
	}

	public void setRefLinks(List<RefLink> refLinks) {
		this.refLinks = refLinks;
	}

}
