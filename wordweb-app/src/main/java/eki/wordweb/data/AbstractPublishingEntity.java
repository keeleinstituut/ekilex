package eki.wordweb.data;

import eki.common.data.AbstractDataObject;

public abstract class AbstractPublishingEntity extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private boolean isWwUnif;

	private boolean isWwLite;

	private boolean isWwOd;

	public boolean isWwUnif() {
		return isWwUnif;
	}

	public void setWwUnif(boolean isWwUnif) {
		this.isWwUnif = isWwUnif;
	}

	public boolean isWwLite() {
		return isWwLite;
	}

	public void setWwLite(boolean isWwLite) {
		this.isWwLite = isWwLite;
	}

	public boolean isWwOd() {
		return isWwOd;
	}

	public void setWwOd(boolean isWwOd) {
		this.isWwOd = isWwOd;
	}

}
