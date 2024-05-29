package eki.wordweb.data;

import java.time.LocalDateTime;
import java.util.List;

import eki.common.data.AbstractDataObject;

public class NewsArticle extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long newsArticleId;

	private LocalDateTime created;

	private String title;

	private List<String> newsSections;

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

	public List<String> getNewsSections() {
		return newsSections;
	}

	public void setNewsSections(List<String> newsSections) {
		this.newsSections = newsSections;
	}

}
