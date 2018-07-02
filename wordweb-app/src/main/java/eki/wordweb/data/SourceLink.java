package eki.wordweb.data;

import eki.common.data.AbstractDataObject;

public class SourceLink extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private String type;

	private String name;

	private boolean translator;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isTranslator() {
		return translator;
	}

	public void setTranslator(boolean translator) {
		this.translator = translator;
	}

}
