package eki.ekilex.data;

import eki.common.data.AbstractDataObject;
import eki.ekilex.constant.ResponseStatus;

public class MediaUploadResponse extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private ResponseStatus status;

	private String message;

	private String detailMessage;

	private String url;

	private String objectFilename;

	public ResponseStatus getStatus() {
		return status;
	}

	public void setStatus(ResponseStatus status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getDetailMessage() {
		return detailMessage;
	}

	public void setDetailMessage(String detailMessage) {
		this.detailMessage = detailMessage;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getObjectFilename() {
		return objectFilename;
	}

	public void setObjectFilename(String objectFilename) {
		this.objectFilename = objectFilename;
	}
}
