package eki.ekilex.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import eki.common.constant.NewsArticleType;
import eki.ekilex.data.NewsArticle;
import eki.ekilex.service.db.NewsDbService;

@Component
public class NewsService {

	@Autowired
	private NewsDbService newsDbService;

	@Transactional
	public List<NewsArticle> getNewsArticles() {
		return newsDbService.getNewsArticles();
	}

	@Transactional
	public List<NewsArticle> getLatestNewsArticlesOfTypes() {

		Locale locale = LocaleContextHolder.getLocale();
		String lang = locale.getLanguage();
		List<NewsArticleType> newsArticleTypes = Arrays.asList(NewsArticleType.EKILEX, NewsArticleType.WORDWEB);
		List<NewsArticle> newsArticles = new ArrayList<>();
		for (NewsArticleType newsArticleType : newsArticleTypes) {
			NewsArticle newsArticle = newsDbService.getLatestNewsArticle(newsArticleType, lang);
			if (newsArticle == null) {
				continue;
			}
			newsArticles.add(newsArticle);
		}
		return newsArticles;
	}

	@Transactional(rollbackOn = Exception.class)
	public void saveNewsArticle(NewsArticle newsArticle) {

		Long newsArticleId = newsArticle.getId();
		if (newsArticleId == null) {
			newsArticleId = newsDbService.createNewsArticle(newsArticle);
		} else {
			newsDbService.updateNewsArticle(newsArticleId, newsArticle);
		}
	}

	@Transactional(rollbackOn = Exception.class)
	public void deleteNewsArticle(Long id) {
		newsDbService.deleteNewsArticle(id);
	}

}
