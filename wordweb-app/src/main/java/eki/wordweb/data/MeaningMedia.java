package eki.wordweb.data;

public class MeaningMedia extends AbstractPublishingEntity {

	private static final long serialVersionUID = 1L;

	private Long id;

	private Long meaningId;

	private String url;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getMeaningId() {
		return meaningId;
	}

	public void setMeaningId(Long meaningId) {
		this.meaningId = meaningId;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
