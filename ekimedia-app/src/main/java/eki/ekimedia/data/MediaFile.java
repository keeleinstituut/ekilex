package eki.ekimedia.data;

import java.time.LocalDateTime;

import eki.common.data.AbstractDataObject;

public class MediaFile extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long id;

	private String origin;

	private LocalDateTime created;

	private String originalFilename;

	private String objectFilename;

	private String filenameExt;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

	public LocalDateTime getCreated() {
		return created;
	}

	public void setCreated(LocalDateTime created) {
		this.created = created;
	}

	public String getOriginalFilename() {
		return originalFilename;
	}

	public void setOriginalFilename(String originalFilename) {
		this.originalFilename = originalFilename;
	}

	public String getObjectFilename() {
		return objectFilename;
	}

	public void setObjectFilename(String objectFilename) {
		this.objectFilename = objectFilename;
	}

	public String getFilenameExt() {
		return filenameExt;
	}

	public void setFilenameExt(String filenameExt) {
		this.filenameExt = filenameExt;
	}

}
