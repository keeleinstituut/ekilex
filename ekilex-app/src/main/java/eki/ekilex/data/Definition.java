package eki.ekilex.data;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class Definition extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long id;

	private String value;

	private String lang;

	private Long orderBy;

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

	public Long getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(Long orderBy) {
		this.orderBy = orderBy;
	}

	public List<RefLink> getRefLinks() {
		return refLinks;
	}

	public void setRefLinks(List<RefLink> refLinks) {
		this.refLinks = refLinks;
	}

}
