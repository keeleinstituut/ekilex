package eki.ekimedia.data;

import eki.common.data.AbstractDataObject;

public class MediaFileUpload extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private String origin;

	private String originalFilename;

	private String filenameExt;

	private byte[] content;

	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

	public String getOriginalFilename() {
		return originalFilename;
	}

	public void setOriginalFilename(String originalFilename) {
		this.originalFilename = originalFilename;
	}

	public String getFilenameExt() {
		return filenameExt;
	}

	public void setFilenameExt(String filenameExt) {
		this.filenameExt = filenameExt;
	}

	public byte[] getContent() {
		return content;
	}

	public void setContent(byte[] content) {
		this.content = content;
	}

}
