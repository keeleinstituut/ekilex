package eki.wordweb.service.db;

import static eki.wordweb.data.db.Tables.MVIEW_WW_NEWS_ARTICLE;
import static eki.wordweb.data.db.Tables.MVIEW_WW_NEW_WORD_MENU;
import static eki.wordweb.data.db.Tables.MVIEW_WW_WORD_SUGGESTION;

import java.util.List;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import eki.common.constant.GlobalConstant;
import eki.common.constant.NewsArticleType;
import eki.wordweb.constant.SystemConstant;
import eki.wordweb.data.NewWord;
import eki.wordweb.data.NewsArticle;
import eki.wordweb.data.WordSuggestion;
import eki.wordweb.data.db.tables.MviewWwNewWordMenu;
import eki.wordweb.data.db.tables.MviewWwNewsArticle;
import eki.wordweb.data.db.tables.MviewWwWordSuggestion;

@Component
public class AncillaryDataDbService implements SystemConstant, GlobalConstant {

	@Autowired
	private DSLContext create;

	@Cacheable(value = CACHE_KEY_GENERIC, key = "{#root.methodName, #lang}")
	public NewsArticle getLatestWordwebNewsArticle(String lang) {

		MviewWwNewsArticle na = MVIEW_WW_NEWS_ARTICLE.as("na");

		return create
				.selectFrom(na)
				.where(
						na.TYPE.eq(NewsArticleType.WORDWEB.name())
								.and(na.LANG.eq(lang)))
				.orderBy(na.CREATED.desc())
				.limit(1)
				.fetchOptionalInto(NewsArticle.class)
				.orElse(null);
	}

	@Cacheable(value = CACHE_KEY_GENERIC, key = "{#root.methodName, #lang}")
	public List<NewsArticle> getWordwebNewsArticles(String lang) {

		MviewWwNewsArticle na = MVIEW_WW_NEWS_ARTICLE.as("na");

		return create
				.selectFrom(na)
				.where(
						na.TYPE.eq(NewsArticleType.WORDWEB.name())
								.and(na.LANG.eq(lang)))
				.orderBy(na.CREATED.desc())
				.fetchInto(NewsArticle.class);
	}

	@Cacheable(value = CACHE_KEY_GENERIC, key = "#root.methodName")
	public List<NewWord> getNewWords() {

		MviewWwNewWordMenu nw = MVIEW_WW_NEW_WORD_MENU.as("nw");

		return create
				.selectFrom(nw)
				.orderBy(nw.REG_YEAR.desc(), nw.WORD_ID.desc())
				.fetchInto(NewWord.class);
	}

	@Cacheable(value = CACHE_KEY_GENERIC, key = "#root.methodName")
	public List<WordSuggestion> getWordSuggestions() {

		MviewWwWordSuggestion ws = MVIEW_WW_WORD_SUGGESTION.as("ws");

		return create
				.selectFrom(ws)
				.orderBy(ws.PUBLICATION_TIME.desc())
				.fetchInto(WordSuggestion.class);
	}
}
