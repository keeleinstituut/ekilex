package eki.wordweb.data;

import eki.common.data.AbstractDataObject;

public class CorporaSentence extends AbstractDataObject {

	private String leftPart = "";
	private String middlePart = "";
	private String rightPart = "";

	public String getLeftPart() {
		return leftPart;
	}

	public void setLeftPart(String leftPart) {
		this.leftPart = leftPart;
	}

	public String getMiddlePart() {
		return middlePart;
	}

	public void setMiddlePart(String middlePart) {
		this.middlePart = middlePart;
	}

	public String getRightPart() {
		return rightPart;
	}

	public void setRightPart(String rightPart) {
		this.rightPart = rightPart;
	}

	public String getSentence() {
		return leftPart + middlePart + rightPart;
	}
}
