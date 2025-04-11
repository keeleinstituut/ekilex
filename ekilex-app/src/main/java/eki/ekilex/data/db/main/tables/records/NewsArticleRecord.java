/*
 * This file is generated by jOOQ.
 */
package eki.ekilex.data.db.main.tables.records;


import eki.ekilex.data.db.main.tables.NewsArticle;

import java.time.LocalDateTime;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record6;
import org.jooq.Row6;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class NewsArticleRecord extends UpdatableRecordImpl<NewsArticleRecord> implements Record6<Long, LocalDateTime, String, String, String, String> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>public.news_article.id</code>.
     */
    public void setId(Long value) {
        set(0, value);
    }

    /**
     * Getter for <code>public.news_article.id</code>.
     */
    public Long getId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>public.news_article.created</code>.
     */
    public void setCreated(LocalDateTime value) {
        set(1, value);
    }

    /**
     * Getter for <code>public.news_article.created</code>.
     */
    public LocalDateTime getCreated() {
        return (LocalDateTime) get(1);
    }

    /**
     * Setter for <code>public.news_article.type</code>.
     */
    public void setType(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>public.news_article.type</code>.
     */
    public String getType() {
        return (String) get(2);
    }

    /**
     * Setter for <code>public.news_article.title</code>.
     */
    public void setTitle(String value) {
        set(3, value);
    }

    /**
     * Getter for <code>public.news_article.title</code>.
     */
    public String getTitle() {
        return (String) get(3);
    }

    /**
     * Setter for <code>public.news_article.content</code>.
     */
    public void setContent(String value) {
        set(4, value);
    }

    /**
     * Getter for <code>public.news_article.content</code>.
     */
    public String getContent() {
        return (String) get(4);
    }

    /**
     * Setter for <code>public.news_article.lang</code>.
     */
    public void setLang(String value) {
        set(5, value);
    }

    /**
     * Getter for <code>public.news_article.lang</code>.
     */
    public String getLang() {
        return (String) get(5);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<Long> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record6 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row6<Long, LocalDateTime, String, String, String, String> fieldsRow() {
        return (Row6) super.fieldsRow();
    }

    @Override
    public Row6<Long, LocalDateTime, String, String, String, String> valuesRow() {
        return (Row6) super.valuesRow();
    }

    @Override
    public Field<Long> field1() {
        return NewsArticle.NEWS_ARTICLE.ID;
    }

    @Override
    public Field<LocalDateTime> field2() {
        return NewsArticle.NEWS_ARTICLE.CREATED;
    }

    @Override
    public Field<String> field3() {
        return NewsArticle.NEWS_ARTICLE.TYPE;
    }

    @Override
    public Field<String> field4() {
        return NewsArticle.NEWS_ARTICLE.TITLE;
    }

    @Override
    public Field<String> field5() {
        return NewsArticle.NEWS_ARTICLE.CONTENT;
    }

    @Override
    public Field<String> field6() {
        return NewsArticle.NEWS_ARTICLE.LANG;
    }

    @Override
    public Long component1() {
        return getId();
    }

    @Override
    public LocalDateTime component2() {
        return getCreated();
    }

    @Override
    public String component3() {
        return getType();
    }

    @Override
    public String component4() {
        return getTitle();
    }

    @Override
    public String component5() {
        return getContent();
    }

    @Override
    public String component6() {
        return getLang();
    }

    @Override
    public Long value1() {
        return getId();
    }

    @Override
    public LocalDateTime value2() {
        return getCreated();
    }

    @Override
    public String value3() {
        return getType();
    }

    @Override
    public String value4() {
        return getTitle();
    }

    @Override
    public String value5() {
        return getContent();
    }

    @Override
    public String value6() {
        return getLang();
    }

    @Override
    public NewsArticleRecord value1(Long value) {
        setId(value);
        return this;
    }

    @Override
    public NewsArticleRecord value2(LocalDateTime value) {
        setCreated(value);
        return this;
    }

    @Override
    public NewsArticleRecord value3(String value) {
        setType(value);
        return this;
    }

    @Override
    public NewsArticleRecord value4(String value) {
        setTitle(value);
        return this;
    }

    @Override
    public NewsArticleRecord value5(String value) {
        setContent(value);
        return this;
    }

    @Override
    public NewsArticleRecord value6(String value) {
        setLang(value);
        return this;
    }

    @Override
    public NewsArticleRecord values(Long value1, LocalDateTime value2, String value3, String value4, String value5, String value6) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached NewsArticleRecord
     */
    public NewsArticleRecord() {
        super(NewsArticle.NEWS_ARTICLE);
    }

    /**
     * Create a detached, initialised NewsArticleRecord
     */
    public NewsArticleRecord(Long id, LocalDateTime created, String type, String title, String content, String lang) {
        super(NewsArticle.NEWS_ARTICLE);

        setId(id);
        setCreated(created);
        setType(type);
        setTitle(title);
        setContent(content);
        setLang(lang);
    }
}
