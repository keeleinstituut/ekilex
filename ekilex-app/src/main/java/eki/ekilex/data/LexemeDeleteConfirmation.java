package eki.ekilex.data;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class LexemeDeleteConfirmation extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private boolean userRoleExist;

	private boolean showConfirmation;

	private boolean isWordDelete;

	private boolean isMeaningDelete;

	private boolean isCollocMemberDelete;

	private List<String> lexemesWordValues;

	private List<String> lexemeCollocValues;

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

	public void setWordDelete(boolean isWordDelete) {
		this.isWordDelete = isWordDelete;
	}

	public boolean isMeaningDelete() {
		return isMeaningDelete;
	}

	public void setMeaningDelete(boolean isMeaningDelete) {
		this.isMeaningDelete = isMeaningDelete;
	}

	public boolean isCollocMemberDelete() {
		return isCollocMemberDelete;
	}

	public void setCollocMemberDelete(boolean isCollocMemberDelete) {
		this.isCollocMemberDelete = isCollocMemberDelete;
	}

	public List<String> getLexemesWordValues() {
		return lexemesWordValues;
	}

	public void setLexemesWordValues(List<String> lexemesWordValues) {
		this.lexemesWordValues = lexemesWordValues;
	}

	public List<String> getLexemeCollocValues() {
		return lexemeCollocValues;
	}

	public void setLexemeCollocValues(List<String> lexemeCollocValues) {
		this.lexemeCollocValues = lexemeCollocValues;
	}

}
