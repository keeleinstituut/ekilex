package eki.ekilex.data;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class LexemeDeleteConfirmation extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private boolean userRoleExist;

	private boolean showConfirmation;

	private boolean isWordDelete;

	private boolean isMeaningDelete;

	private List<String> lexemesWordValues;

	public boolean isUserRoleExist() {
		return userRoleExist;
	}

	public void setUserRoleExist(boolean userRoleExist) {
		this.userRoleExist = userRoleExist;
	}

	public boolean isShowConfirmation() {
		return showConfirmation;
	}

	public void setShowConfirmation(boolean showConfirmation) {
		this.showConfirmation = showConfirmation;
	}

	public boolean isWordDelete() {
		return isWordDelete;
	}

	public void setWordDelete(boolean wordDelete) {
		isWordDelete = wordDelete;
	}

	public boolean isMeaningDelete() {
		return isMeaningDelete;
	}

	public void setMeaningDelete(boolean meaningDelete) {
		isMeaningDelete = meaningDelete;
	}

	public List<String> getLexemesWordValues() {
		return lexemesWordValues;
	}

	public void setLexemesWordValues(List<String> lexemesWordValues) {
		this.lexemesWordValues = lexemesWordValues;
	}
}
