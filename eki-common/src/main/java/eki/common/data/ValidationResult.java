package eki.common.data;

public class ValidationResult extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private boolean valid;

	private String messageKey;

	public ValidationResult(boolean valid) {
		super();
		this.valid = valid;
		this.messageKey = null;
	}

	public ValidationResult(boolean valid, String messageKey) {
		super();
		this.valid = valid;
		this.messageKey = messageKey;
	}

	public boolean isValid() {
		return valid;
	}

	public String getMessageKey() {
		return messageKey;
	}

}
