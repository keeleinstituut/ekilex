package eki.wordweb.data;

import eki.common.data.AbstractDataObject;

public class CorpusSentence extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private String leftPart;

	private String middlePart;

	private String rightPart;

	private CorpusSource source;

	public CorpusSentence() {
		leftPart = "";
		middlePart = "";
		rightPart = "";
	}

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

	public CorpusSource getSource() {
		return source;
	}

	public void setSource(CorpusSource source) {
		this.source = source;
	}

}
