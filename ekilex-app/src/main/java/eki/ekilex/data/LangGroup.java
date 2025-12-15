package eki.ekilex.data;

import eki.common.data.AbstractDataObject;
import io.swagger.v3.oas.annotations.media.Schema;

public abstract class LangGroup extends AbstractDataObject {

	private static final long serialVersionUID = 1L;
	@Schema(example = "est")
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
