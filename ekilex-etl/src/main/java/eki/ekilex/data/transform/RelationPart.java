package eki.ekilex.data.transform;

import eki.common.data.AbstractDataObject;

public class RelationPart extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long meaningId;

	private String relatedTerm;

	private String lang;

	public Long getMeaningId() {
		return meaningId;
	}

	public void setMeaningId(Long meaningId) {
		this.meaningId = meaningId;
	}

	public String getRelatedTerm() {
		return relatedTerm;
	}

	public void setRelatedTerm(String relatedTerm) {
		this.relatedTerm = relatedTerm;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}
}
