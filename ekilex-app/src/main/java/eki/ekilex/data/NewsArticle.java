package eki.ekilex.data;

import java.time.LocalDateTime;
import java.util.List;

import eki.common.constant.NewsArticleType;
import eki.common.data.AbstractDataObject;

public class NewsArticle extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long id;

	private LocalDateTime created;

	private NewsArticleType type;

	private String title;

	private String lang;

	private List<NewsSection> newsSections;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public LocalDateTime getCreated() {
		return created;
	}

	public void setCreated(LocalDateTime created) {
		this.created = created;
	}

	public NewsArticleType getType() {
		return type;
	}

	public void setType(NewsArticleType type) {
		this.type = type;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public List<NewsSection> getNewsSections() {
		return newsSections;
	}

	public void setNewsSections(List<NewsSection> newsSections) {
		this.newsSections = newsSections;
	}

}
