package eki.common.data;

public class ExceptionStat extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private String exceptionName;

	private String exceptionMessage;

	private String remoteHost;

	public ExceptionStat() {
	}

	public ExceptionStat(String exceptionName, String exceptionMessage, String remoteHost) {
		this.exceptionName = exceptionName;
		this.exceptionMessage = exceptionMessage;
		this.remoteHost = remoteHost;
	}

	public String getExceptionName() {
		return exceptionName;
	}

	public String getExceptionMessage() {
		return exceptionMessage;
	}

	public String getRemoteHost() {
		return remoteHost;
	}

}
