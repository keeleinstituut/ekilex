package eki.ekilex.data;

import java.util.List;
import java.util.Map;

import eki.common.data.AbstractDataObject;

public class MeaningDeleteConfirmation extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private boolean userRoleExist;

	private boolean isMeaningDelete;

	private Map<Long, String[]> relatedMeaningsDatasetsMap;

	private List<String> wordValues;

	public boolean isUserRoleExist() {
		return userRoleExist;
	}

	public void setUserRoleExist(boolean userRoleExist) {
		this.userRoleExist = userRoleExist;
	}

	public boolean isMeaningDelete() {
		return isMeaningDelete;
	}

	public void setMeaningDelete(boolean meaningDelete) {
		isMeaningDelete = meaningDelete;
	}

	public Map<Long, String[]> getRelatedMeaningsDatasetsMap() {
		return relatedMeaningsDatasetsMap;
	}

	public void setRelatedMeaningsDatasetsMap(Map<Long, String[]> relatedMeaningsDatasetsMap) {
		this.relatedMeaningsDatasetsMap = relatedMeaningsDatasetsMap;
	}

	public List<String> getWordValues() {
		return wordValues;
	}

	public void setWordValues(List<String> wordValues) {
		this.wordValues = wordValues;
	}
}
