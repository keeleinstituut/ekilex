package eki.ekilex.data;

import eki.common.data.AbstractDataObject;

public class MeaningMediaRequest extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long meaningId;

	private Long meaningImageId;

	private Long meaningMediaId;

	private String url;

	private String title;

	private String objectFilename;

	public Long getMeaningId() {
		return meaningId;
	}

	public void setMeaningId(Long meaningId) {
		this.meaningId = meaningId;
	}

	public Long getMeaningImageId() {
		return meaningImageId;
	}

	public void setMeaningImageId(Long meaningImageId) {
		this.meaningImageId = meaningImageId;
	}

	public Long getMeaningMediaId() {
		return meaningMediaId;
	}

	public void setMeaningMediaId(Long meaningMediaId) {
		this.meaningMediaId = meaningMediaId;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getObjectFilename() {
		return objectFilename;
	}

	public void setObjectFilename(String objectFilename) {
		this.objectFilename = objectFilename;
	}
}
