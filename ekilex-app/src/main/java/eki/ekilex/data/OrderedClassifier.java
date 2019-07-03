package eki.ekilex.data;

public class OrderedClassifier extends Classifier {

	private static final long serialVersionUID = 1L;

	private Long id;

	private Long orderBy;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(Long orderBy) {
		this.orderBy = orderBy;
	}
}
