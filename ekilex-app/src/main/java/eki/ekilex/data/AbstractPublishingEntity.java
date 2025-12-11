package eki.ekilex.data;

import eki.common.data.AbstractDataObject;
import io.swagger.v3.oas.annotations.media.Schema;

public abstract class AbstractPublishingEntity extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private boolean isPublic;
	@Schema(description = "Helper field for web views")
	private boolean isWwUnif;
	@Schema(description = "Helper field for web views")
	private boolean isWwLite;
	@Schema(description = "Helper field for web views")
	private boolean isWwOs;

	public boolean isPublic() {
		return isPublic;
	}

	public void setPublic(boolean isPublic) {
		this.isPublic = isPublic;
	}

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

	public boolean isWwOs() {
		return isWwOs;
	}

	public void setWwOs(boolean isWwOs) {
		this.isWwOs = isWwOs;
	}

}
