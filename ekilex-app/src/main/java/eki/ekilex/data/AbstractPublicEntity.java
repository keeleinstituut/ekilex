package eki.ekilex.data;

public abstract class AbstractPublicEntity extends AbstractCrudEntity {

	private static final long serialVersionUID = 1L;

	private boolean isPublic;

	public boolean isPublic() {
		return isPublic;
	}

	public void setPublic(boolean isPublic) {
		this.isPublic = isPublic;
	}
}
