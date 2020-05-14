package eki.ekilex.data;

import eki.common.data.AbstractDataObject;

public class AbstractCrudEntity extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private boolean isCrudGranted;

	public boolean isCrudGranted() {
		return isCrudGranted;
	}

	public void setCrudGranted(boolean crudGranted) {
		isCrudGranted = crudGranted;
	}
}
