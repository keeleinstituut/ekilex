package eki.ekilex.data;

import eki.common.data.AbstractDataObject;

public abstract class LangGroup extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private String lang;

	private boolean selected;

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

}
