package eki.ekilex.data;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class Dataset extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private String code;

	private String name;

	private String description;

	private boolean isVisible;

	private boolean isPublic;

	private List<String> selectedLanguageCodes;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isVisible() {
		return isVisible;
	}

	public void setVisible(boolean visible) {
		isVisible = visible;
	}

	public boolean isPublic() {
		return isPublic;
	}

	public void setPublic(boolean aPublic) {
		isPublic = aPublic;
	}

	public List<String> getSelectedLanguageCodes() {
		return selectedLanguageCodes;
	}

	public void setSelectedLanguageCodes(List<String> selectedLanguageCodes) {
		this.selectedLanguageCodes = selectedLanguageCodes;
	}
}
