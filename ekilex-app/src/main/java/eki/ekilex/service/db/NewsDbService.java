package eki.ekilex.service.db;

import static eki.ekilex.data.db.Tables.LANGUAGE;
import static eki.ekilex.data.db.Tables.LANGUAGE_LABEL;
import static eki.ekilex.data.db.Tables.NEWS_ARTICLE;

import java.util.List;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.GlobalConstant;
import eki.common.constant.NewsArticleType;
import eki.ekilex.constant.SystemConstant;
import eki.ekilex.data.db.tables.Language;
import eki.ekilex.data.db.tables.LanguageLabel;
import eki.ekilex.data.db.tables.NewsArticle;

@Component
public class NewsDbService implements GlobalConstant, SystemConstant {

	@Autowired
	private DSLContext create;

	public List<eki.ekilex.data.NewsArticle> getNewsArticles() {

		NewsArticle na = NEWS_ARTICLE.as("na");

		return create
				.selectFrom(na)
				.orderBy(na.CREATED.desc())
				.fetchInto(eki.ekilex.data.NewsArticle.class);
	}

	public eki.ekilex.data.NewsArticle getLatestNewsArticle(NewsArticleType newsArticleType, String langIso2) {

		NewsArticle na = NEWS_ARTICLE.as("na");
		Language l = LANGUAGE.as("l");
		LanguageLabel ll = LANGUAGE_LABEL.as("ll");

		return create
				.select(na.fields())
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
						NEWS_ARTICLE.CONTENT,
						NEWS_ARTICLE.LANG)
				.values(
						newsArticle.getType().name(),
						newsArticle.getTitle(),
						newsArticle.getContent(),
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
				.set(NEWS_ARTICLE.CONTENT, newsArticle.getContent())
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

}
