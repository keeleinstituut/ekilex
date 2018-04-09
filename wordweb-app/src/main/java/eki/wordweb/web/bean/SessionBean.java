package eki.wordweb.web.bean;

import eki.common.data.AbstractDataObject;

public class SessionBean extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private String sourceLang;

	private String destinLang;

	public String getSourceLang() {
		return sourceLang;
	}

	public void setSourceLang(String sourceLang) {
		this.sourceLang = sourceLang;
	}

	public String getDestinLang() {
		return destinLang;
	}

	public void setDestinLang(String destinLang) {
		this.destinLang = destinLang;
	}

}
