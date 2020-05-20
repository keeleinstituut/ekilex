package eki.ekilex.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import eki.common.data.AbstractDataObject;

@JsonIgnoreProperties({"crudGrant", "readGrant", "subGrant", "anyGrant"})
public abstract class AbstractCrudEntity extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private boolean crudGrant;

	private boolean readGrant;

	private boolean subGrant;

	private boolean anyGrant;

	public boolean isCrudGrant() {
		return crudGrant;
	}

	public void setCrudGrant(boolean crudGrant) {
		this.crudGrant = crudGrant;
	}

	public boolean isReadGrant() {
		return readGrant;
	}

	public void setReadGrant(boolean readGrant) {
		this.readGrant = readGrant;
	}

	public boolean isSubGrant() {
		return subGrant;
	}

	public void setSubGrant(boolean subGrant) {
		this.subGrant = subGrant;
	}

	public boolean isAnyGrant() {
		return anyGrant;
	}

	public void setAnyGrant(boolean anyGrant) {
		this.anyGrant = anyGrant;
	}
}
