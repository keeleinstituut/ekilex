package eki.ekilex.data;

import eki.common.data.AbstractDataObject;

public class UserMessage extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private String successMessageKey;

	private String warningMessageKey;

	public String getSuccessMessageKey() {
		return successMessageKey;
	}

	public void setSuccessMessageKey(String successMessageKey) {
		this.successMessageKey = successMessageKey;
	}

	public String getWarningMessageKey() {
		return warningMessageKey;
	}

	public void setWarningMessageKey(String warningMessageKey) {
		this.warningMessageKey = warningMessageKey;
	}
}
