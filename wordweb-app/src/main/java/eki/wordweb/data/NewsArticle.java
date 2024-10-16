package eki.wordweb.data;

import java.time.LocalDateTime;

import eki.common.data.AbstractDataObject;

public class NewsArticle extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long newsArticleId;

	private LocalDateTime created;

	private String title;

	private String content;

	private String contentCut;

	public Long getNewsArticleId() {
		return newsArticleId;
	}

	public void setNewsArticleId(Long newsArticleId) {
		this.newsArticleId = newsArticleId;
	}

	public LocalDateTime getCreated() {
		return created;
	}

	public void setCreated(LocalDateTime created) {
		this.created = created;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getContentCut() {
		return contentCut;
	}

	public void setContentCut(String contentCut) {
		this.contentCut = contentCut;
	}

}
