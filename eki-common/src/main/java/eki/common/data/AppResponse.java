package eki.common.data;

public class AppResponse extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private String status;

	public AppResponse() {
	}

	public AppResponse(String status) {
		this.status = status;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
