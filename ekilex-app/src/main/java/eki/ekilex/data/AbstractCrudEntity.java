package eki.ekilex.data;

import eki.common.data.AbstractDataObject;

public abstract class AbstractCrudEntity extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Boolean crudGrant;

	private Boolean readGrant;

	private Boolean subGrant;

	private Boolean anyGrant;

	public Boolean getCrudGrant() {
		return crudGrant;
	}

	public void setCrudGrant(Boolean crudGrant) {
		this.crudGrant = crudGrant;
	}

	public Boolean getReadGrant() {
		return readGrant;
	}

	public void setReadGrant(Boolean readGrant) {
		this.readGrant = readGrant;
	}

	public Boolean getSubGrant() {
		return subGrant;
	}

	public void setSubGrant(Boolean subGrant) {
		this.subGrant = subGrant;
	}

	public Boolean getAnyGrant() {
		return anyGrant;
	}

	public void setAnyGrant(Boolean anyGrant) {
		this.anyGrant = anyGrant;
	}
}
