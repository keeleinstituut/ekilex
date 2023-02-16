package eki.ekilex.constant;

public enum ApplicationStatus {

	NEW(1), APPROVED(2), REJECTED(3);

	private int order;

	ApplicationStatus(int order) {
		this.order = order;
	}

	public int getOrder() {
		return order;
	}
}
