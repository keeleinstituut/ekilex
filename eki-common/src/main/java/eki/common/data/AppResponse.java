package eki.common.data;

public class AppResponse extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private String status;

	private String messageKey;

	private String messageValue;

	public AppResponse() {
		this.status = null;
		this.messageKey = null;
	}

	public AppResponse(String status) {
		this.status = status;
		this.messageKey = null;
	}

	public AppResponse(String status, String messageKey) {
		this.status = status;
		this.messageKey = messageKey;
	}

	public String getStatus() {
		return status;
	}

	public String getMessageKey() {
		return messageKey;
	}

	public String getMessageValue() {
		return messageValue;
	}

	public void setMessageValue(String messageValue) {
		this.messageValue = messageValue;
	}

}
