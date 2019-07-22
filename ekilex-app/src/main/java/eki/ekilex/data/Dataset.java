package eki.ekilex.data;

import java.util.List;

import eki.common.constant.DatasetType;
import eki.common.data.AbstractDataObject;

public class Dataset extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private String code;

	private String name;

	private String description;

	private boolean isVisible;

	private boolean isPublic;

	private List<String> origins;

	private List<Classifier> selectedDomains;

	private List<Classifier> selectedLanguages;

	private List<Classifier> selectedProcessStates;

	private DatasetType type;

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

	public List<Classifier> getSelectedDomains() {
		return selectedDomains;
	}

	public void setSelectedDomains(List<Classifier> selectedDomains) {
		this.selectedDomains = selectedDomains;
	}

	public List<Classifier> getSelectedLanguages() {
		return selectedLanguages;
	}

	public void setSelectedLanguages(List<Classifier> selectedLanguages) {
		this.selectedLanguages = selectedLanguages;
	}

	public List<Classifier> getSelectedProcessStates() {
		return selectedProcessStates;
	}

	public void setSelectedProcessStates(List<Classifier> selectedProcessStates) {
		this.selectedProcessStates = selectedProcessStates;
	}

	public List<String> getOrigins() {
		return origins;
	}

	public void setOrigins(List<String> origins) {
		this.origins = origins;
	}

	public DatasetType getType() {
		return type;
	}

	public void setType(DatasetType type) {
		this.type = type;
	}
}
