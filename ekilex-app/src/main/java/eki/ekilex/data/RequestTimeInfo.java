package eki.ekilex.data;

import eki.common.data.AbstractDataObject;

public class RequestTimeInfo extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private long requestCount;

	private long requestTimeSum;

	private long requestTimeAvg;

	public RequestTimeInfo() {
		this.requestCount = 0;
		this.requestTimeSum = 0;
		this.requestTimeAvg = 0;
	}

	public long getRequestCount() {
		return requestCount;
	}

	public void setRequestCount(long requestCount) {
		this.requestCount = requestCount;
	}

	public long getRequestTimeSum() {
		return requestTimeSum;
	}

	public void setRequestTimeSum(long requestTimeSum) {
		this.requestTimeSum = requestTimeSum;
	}

	public long getRequestTimeAvg() {
		return requestTimeAvg;
	}

	public void setRequestTimeAvg(long requestTimeAvg) {
		this.requestTimeAvg = requestTimeAvg;
	}
}
