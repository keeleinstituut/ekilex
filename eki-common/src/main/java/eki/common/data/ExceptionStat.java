package eki.common.data;

public class ExceptionStat extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private String exceptionName;

	private String exceptionMessage;

	public ExceptionStat() {
	}

	public ExceptionStat(String exceptionName, String exceptionMessage) {
		this.exceptionName = exceptionName;
		this.exceptionMessage = exceptionMessage;
	}

	public String getExceptionName() {
		return exceptionName;
	}

	public String getExceptionMessage() {
		return exceptionMessage;
	}

}
