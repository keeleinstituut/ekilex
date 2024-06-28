package eki.ekilex.data;

public class SourceAndSourceLinkRequest extends Source {

	private static final long serialVersionUID = 1L;

	private Long sourceOwnerId;

	private String sourceOwnerName;

	public Long getSourceOwnerId() {
		return sourceOwnerId;
	}

	public void setSourceOwnerId(Long sourceOwnerId) {
		this.sourceOwnerId = sourceOwnerId;
	}

	public String getSourceOwnerName() {
		return sourceOwnerName;
	}

	public void setSourceOwnerName(String sourceOwnerName) {
		this.sourceOwnerName = sourceOwnerName;
	}

}
