package eki.wordweb.data;

import eki.common.data.AbstractDataObject;

public class CorpusSource extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private String displayName;

	private String sentenceTitle;

	private String sentenceUrl;

	private String tooltipHtml;

	public CorpusSource(String displayName, String sentenceTitle, String sentenceUrl) {
		this.displayName = displayName;
		this.sentenceTitle = sentenceTitle;
		this.sentenceUrl = sentenceUrl;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getDisplayName() {
		return displayName;
	}

	public String getSentenceTitle() {
		return sentenceTitle;
	}

	public String getSentenceUrl() {
		return sentenceUrl;
	}

	public String getTooltipHtml() {
		return tooltipHtml;
	}

	public void setTooltipHtml(String tooltipHtml) {
		this.tooltipHtml = tooltipHtml;
	}

}
