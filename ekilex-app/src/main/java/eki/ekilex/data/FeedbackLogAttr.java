package eki.ekilex.data;

import eki.common.data.AbstractDataObject;

public class FeedbackLogAttr extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long id;

	private Long feedbackLogId;

	private String name;

	private String value;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getFeedbackLogId() {
		return feedbackLogId;
	}

	public void setFeedbackLogId(Long feedbackLogId) {
		this.feedbackLogId = feedbackLogId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
