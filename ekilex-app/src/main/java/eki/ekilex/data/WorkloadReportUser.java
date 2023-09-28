package eki.ekilex.data;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class WorkloadReportUser extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private String userName;

	private int count;

	private List<String> wordValues;

	private List<Long> ownerIds;

	private List<Long> lexSearchIds;

	private List<Long> termSearchIds;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public List<String> getWordValues() {
		return wordValues;
	}

	public void setWordValues(List<String> wordValues) {
		this.wordValues = wordValues;
	}

	public List<Long> getOwnerIds() {
		return ownerIds;
	}

	public void setOwnerIds(List<Long> ownerIds) {
		this.ownerIds = ownerIds;
	}

	public List<Long> getLexSearchIds() {
		return lexSearchIds;
	}

	public void setLexSearchIds(List<Long> lexSearchIds) {
		this.lexSearchIds = lexSearchIds;
	}

	public List<Long> getTermSearchIds() {
		return termSearchIds;
	}

	public void setTermSearchIds(List<Long> termSearchIds) {
		this.termSearchIds = termSearchIds;
	}
}