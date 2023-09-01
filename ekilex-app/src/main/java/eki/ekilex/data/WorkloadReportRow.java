package eki.ekilex.data;

import eki.common.data.AbstractDataObject;

public class WorkloadReportRow extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private String userName;

	private int createdWordCount;

	private int createdLexemeCount;

	private int createdMeaningCount;

	private int updatedWordCount;

	private int updatedLexemeCount;

	private int updatedMeaningCount;

	private int deletedWordCount;

	private int deletedLexemeCount;

	private int deletedMeaningCount;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public int getCreatedWordCount() {
		return createdWordCount;
	}

	public void setCreatedWordCount(int createdWordCount) {
		this.createdWordCount = createdWordCount;
	}

	public int getCreatedLexemeCount() {
		return createdLexemeCount;
	}

	public void setCreatedLexemeCount(int createdLexemeCount) {
		this.createdLexemeCount = createdLexemeCount;
	}

	public int getCreatedMeaningCount() {
		return createdMeaningCount;
	}

	public void setCreatedMeaningCount(int createdMeaningCount) {
		this.createdMeaningCount = createdMeaningCount;
	}

	public int getUpdatedWordCount() {
		return updatedWordCount;
	}

	public void setUpdatedWordCount(int updatedWordCount) {
		this.updatedWordCount = updatedWordCount;
	}

	public int getUpdatedLexemeCount() {
		return updatedLexemeCount;
	}

	public void setUpdatedLexemeCount(int updatedLexemeCount) {
		this.updatedLexemeCount = updatedLexemeCount;
	}

	public int getUpdatedMeaningCount() {
		return updatedMeaningCount;
	}

	public void setUpdatedMeaningCount(int updatedMeaningCount) {
		this.updatedMeaningCount = updatedMeaningCount;
	}

	public int getDeletedWordCount() {
		return deletedWordCount;
	}

	public void setDeletedWordCount(int deletedWordCount) {
		this.deletedWordCount = deletedWordCount;
	}

	public int getDeletedLexemeCount() {
		return deletedLexemeCount;
	}

	public void setDeletedLexemeCount(int deletedLexemeCount) {
		this.deletedLexemeCount = deletedLexemeCount;
	}

	public int getDeletedMeaningCount() {
		return deletedMeaningCount;
	}

	public void setDeletedMeaningCount(int deletedMeaningCount) {
		this.deletedMeaningCount = deletedMeaningCount;
	}
}
