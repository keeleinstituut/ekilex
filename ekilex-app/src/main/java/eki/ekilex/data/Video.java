package eki.ekilex.data;

import eki.common.data.AbstractDataObject;

// TODO combine with Image class? - yogesh
public class Video extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long id;

	private String sourceUrl;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSourceUrl() {
		return sourceUrl;
	}

	public void setSourceUrl(String sourceUrl) {
		this.sourceUrl = sourceUrl;
	}
}
