package eki.wwexam.data;

import eki.common.data.AbstractDataObject;

public class StatServiceStatus extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private boolean serviceEnabled;

	private String serviceUrl;

	private String responseStatus;

	private Long wwSearchStatCount;

	private String exceptionMessage;

	public boolean isServiceEnabled() {
		return serviceEnabled;
	}

	public void setServiceEnabled(boolean serviceEnabled) {
		this.serviceEnabled = serviceEnabled;
	}

	public String getServiceUrl() {
		return serviceUrl;
	}

	public void setServiceUrl(String serviceUrl) {
		this.serviceUrl = serviceUrl;
	}

	public String getResponseStatus() {
		return responseStatus;
	}

	public void setResponseStatus(String responseStatus) {
		this.responseStatus = responseStatus;
	}

	public Long getWwSearchStatCount() {
		return wwSearchStatCount;
	}

	public void setWwSearchStatCount(Long wwSearchStatCount) {
		this.wwSearchStatCount = wwSearchStatCount;
	}

	public String getExceptionMessage() {
		return exceptionMessage;
	}

	public void setExceptionMessage(String exceptionMessage) {
		this.exceptionMessage = exceptionMessage;
	}
}
