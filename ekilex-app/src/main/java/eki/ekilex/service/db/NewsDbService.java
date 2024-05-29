package eki.ekilex.service.db;

import static eki.ekilex.data.db.Tables.LANGUAGE;
import static eki.ekilex.data.db.Tables.LANGUAGE_LABEL;
import static eki.ekilex.data.db.Tables.NEWS_ARTICLE;
import static eki.ekilex.data.db.Tables.NEWS_SECTION;

import java.util.List;

import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.JSON;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.GlobalConstant;
import eki.common.constant.NewsArticleType;
import eki.ekilex.constant.SystemConstant;
import eki.ekilex.data.db.tables.Language;
import eki.ekilex.data.db.tables.LanguageLabel;
import eki.ekilex.data.db.tables.NewsArticle;
import eki.ekilex.data.db.tables.NewsSection;

@Component
public class NewsDbService implements GlobalConstant, SystemConstant {

	@Autowired
	private DSLContext create;

	public List<eki.ekilex.data.NewsArticle> getNewsArticles() {

		NewsArticle na = NEWS_ARTICLE.as("na");
		NewsSection ns = NEWS_SECTION.as("ns");

		Field<JSON> nssf = DSL
				.select(DSL
						.jsonArrayAgg(DSL
								.jsonObject(
										DSL.key("id").value(ns.ID),
										DSL.key("newsArticleId").value(ns.NEWS_ARTICLE_ID),
										DSL.key("content").value(ns.CONTENT)))
						.orderBy(ns.ID))
				.from(ns)
				.where(ns.NEWS_ARTICLE_ID.eq(na.ID))
				.asField();

		return create
				.select(na.fields())
				.select(nssf.as("news_sections"))
				.from(na)
				.orderBy(na.CREATED.desc())
				.fetchInto(eki.ekilex.data.NewsArticle.class);
	}

	public eki.ekilex.data.NewsArticle getLatestNewsArticle(NewsArticleType newsArticleType, String langIso2) {

		NewsArticle na = NEWS_ARTICLE.as("na");
		NewsSection ns = NEWS_SECTION.as("ns");
		Language l = LANGUAGE.as("l");
		LanguageLabel ll = LANGUAGE_LABEL.as("ll");

		Field<JSON> nssf = DSL
				.select(DSL
						.jsonArrayAgg(DSL
								.jsonObject(
										DSL.key("id").value(ns.ID),
										DSL.key("newsArticleId").value(ns.NEWS_ARTICLE_ID),
										DSL.key("content").value(ns.CONTENT)))
						.orderBy(ns.ID))
				.from(ns)
				.where(ns.NEWS_ARTICLE_ID.eq(na.ID))
				.asField();

		return create
				.select(na.fields())
				.select(nssf.as("news_sections"))
				.from(na, l, ll)
				.where(
						na.TYPE.eq(newsArticleType.name())
								.and(na.LANG.eq(ll.CODE))
								.and(ll.TYPE.eq(CLASSIF_LABEL_TYPE_ISO2))
								.and(ll.LANG.eq(CLASSIF_LABEL_LANG_EST))
								.and(ll.VALUE.eq(langIso2)))
				.orderBy(na.CREATED.desc())
				.limit(1)
				.fetchOptionalInto(eki.ekilex.data.NewsArticle.class)
				.orElse(null);
	}

	public Long createNewsArticle(eki.ekilex.data.NewsArticle newsArticle) {

		return create
				.insertInto(
						NEWS_ARTICLE,
						NEWS_ARTICLE.TYPE,
						NEWS_ARTICLE.TITLE,
						NEWS_ARTICLE.LANG)
				.values(
						newsArticle.getType().name(),
						newsArticle.getTitle(),
						newsArticle.getLang())
				.returning(NEWS_ARTICLE.ID)
				.fetchOne()
				.getId();

	}

	public void updateNewsArticle(Long id, eki.ekilex.data.NewsArticle newsArticle) {

		create
				.update(NEWS_ARTICLE)
				.set(NEWS_ARTICLE.TYPE, newsArticle.getType().name())
				.set(NEWS_ARTICLE.TITLE, newsArticle.getTitle())
				.set(NEWS_ARTICLE.LANG, newsArticle.getLang())
				.where(NEWS_ARTICLE.ID.eq(id))
				.execute();
	}

	public void deleteNewsArticle(Long id) {

		create
				.deleteFrom(NEWS_ARTICLE)
				.where(NEWS_ARTICLE.ID.eq(id))
				.execute();
	}

	public Long createNewsSection(Long newsArticleId, eki.ekilex.data.NewsSection newsSection) {

		return create
				.insertInto(
						NEWS_SECTION,
						NEWS_SECTION.NEWS_ARTICLE_ID,
						NEWS_SECTION.CONTENT)
				.values(
						newsArticleId,
						newsSection.getContent())
				.returning(NEWS_SECTION.ID)
				.fetchOne()
				.getId();
	}

	public void updateNewsSection(Long id, eki.ekilex.data.NewsSection newsSection) {

		create
				.update(NEWS_SECTION)
				.set(NEWS_SECTION.CONTENT, newsSection.getContent())
				.where(NEWS_SECTION.ID.eq(id))
				.execute();
	}

	public void deleteNewsSection(Long id) {

		create
				.deleteFrom(NEWS_SECTION)
				.where(NEWS_SECTION.ID.eq(id))
				.execute();
	}

}
