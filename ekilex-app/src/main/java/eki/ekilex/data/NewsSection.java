package eki.ekilex.data;

import eki.common.data.AbstractDataObject;

public class NewsSection extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long id;

	private Long newsArticleId;

	private String content;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getNewsArticleId() {
		return newsArticleId;
	}

	public void setNewsArticleId(Long newsArticleId) {
		this.newsArticleId = newsArticleId;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

}
