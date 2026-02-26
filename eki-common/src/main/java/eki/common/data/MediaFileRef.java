package eki.common.data;

public class MediaFileRef extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long id;

	private String filename;

	private String url;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
