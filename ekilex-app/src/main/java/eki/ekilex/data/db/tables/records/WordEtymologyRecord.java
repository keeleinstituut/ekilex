/*
 * This file is generated by jOOQ.
 */
package eki.ekilex.data.db.tables.records;


import eki.ekilex.data.db.tables.WordEtymology;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record8;
import org.jooq.Row8;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class WordEtymologyRecord extends UpdatableRecordImpl<WordEtymologyRecord> implements Record8<Long, Long, String, String, String, String, Boolean, Long> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>public.word_etymology.id</code>.
     */
    public void setId(Long value) {
        set(0, value);
    }

    /**
     * Getter for <code>public.word_etymology.id</code>.
     */
    public Long getId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>public.word_etymology.word_id</code>.
     */
    public void setWordId(Long value) {
        set(1, value);
    }

    /**
     * Getter for <code>public.word_etymology.word_id</code>.
     */
    public Long getWordId() {
        return (Long) get(1);
    }

    /**
     * Setter for <code>public.word_etymology.etymology_type_code</code>.
     */
    public void setEtymologyTypeCode(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>public.word_etymology.etymology_type_code</code>.
     */
    public String getEtymologyTypeCode() {
        return (String) get(2);
    }

    /**
     * Setter for <code>public.word_etymology.etymology_year</code>.
     */
    public void setEtymologyYear(String value) {
        set(3, value);
    }

    /**
     * Getter for <code>public.word_etymology.etymology_year</code>.
     */
    public String getEtymologyYear() {
        return (String) get(3);
    }

    /**
     * Setter for <code>public.word_etymology.comment</code>.
     */
    public void setComment(String value) {
        set(4, value);
    }

    /**
     * Getter for <code>public.word_etymology.comment</code>.
     */
    public String getComment() {
        return (String) get(4);
    }

    /**
     * Setter for <code>public.word_etymology.comment_prese</code>.
     */
    public void setCommentPrese(String value) {
        set(5, value);
    }

    /**
     * Getter for <code>public.word_etymology.comment_prese</code>.
     */
    public String getCommentPrese() {
        return (String) get(5);
    }

    /**
     * Setter for <code>public.word_etymology.is_questionable</code>.
     */
    public void setIsQuestionable(Boolean value) {
        set(6, value);
    }

    /**
     * Getter for <code>public.word_etymology.is_questionable</code>.
     */
    public Boolean getIsQuestionable() {
        return (Boolean) get(6);
    }

    /**
     * Setter for <code>public.word_etymology.order_by</code>.
     */
    public void setOrderBy(Long value) {
        set(7, value);
    }

    /**
     * Getter for <code>public.word_etymology.order_by</code>.
     */
    public Long getOrderBy() {
        return (Long) get(7);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<Long> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record8 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row8<Long, Long, String, String, String, String, Boolean, Long> fieldsRow() {
        return (Row8) super.fieldsRow();
    }

    @Override
    public Row8<Long, Long, String, String, String, String, Boolean, Long> valuesRow() {
        return (Row8) super.valuesRow();
    }

    @Override
    public Field<Long> field1() {
        return WordEtymology.WORD_ETYMOLOGY.ID;
    }

    @Override
    public Field<Long> field2() {
        return WordEtymology.WORD_ETYMOLOGY.WORD_ID;
    }

    @Override
    public Field<String> field3() {
        return WordEtymology.WORD_ETYMOLOGY.ETYMOLOGY_TYPE_CODE;
    }

    @Override
    public Field<String> field4() {
        return WordEtymology.WORD_ETYMOLOGY.ETYMOLOGY_YEAR;
    }

    @Override
    public Field<String> field5() {
        return WordEtymology.WORD_ETYMOLOGY.COMMENT;
    }

    @Override
    public Field<String> field6() {
        return WordEtymology.WORD_ETYMOLOGY.COMMENT_PRESE;
    }

    @Override
    public Field<Boolean> field7() {
        return WordEtymology.WORD_ETYMOLOGY.IS_QUESTIONABLE;
    }

    @Override
    public Field<Long> field8() {
        return WordEtymology.WORD_ETYMOLOGY.ORDER_BY;
    }

    @Override
    public Long component1() {
        return getId();
    }

    @Override
    public Long component2() {
        return getWordId();
    }

    @Override
    public String component3() {
        return getEtymologyTypeCode();
    }

    @Override
    public String component4() {
        return getEtymologyYear();
    }

    @Override
    public String component5() {
        return getComment();
    }

    @Override
    public String component6() {
        return getCommentPrese();
    }

    @Override
    public Boolean component7() {
        return getIsQuestionable();
    }

    @Override
    public Long component8() {
        return getOrderBy();
    }

    @Override
    public Long value1() {
        return getId();
    }

    @Override
    public Long value2() {
        return getWordId();
    }

    @Override
    public String value3() {
        return getEtymologyTypeCode();
    }

    @Override
    public String value4() {
        return getEtymologyYear();
    }

    @Override
    public String value5() {
        return getComment();
    }

    @Override
    public String value6() {
        return getCommentPrese();
    }

    @Override
    public Boolean value7() {
        return getIsQuestionable();
    }

    @Override
    public Long value8() {
        return getOrderBy();
    }

    @Override
    public WordEtymologyRecord value1(Long value) {
        setId(value);
        return this;
    }

    @Override
    public WordEtymologyRecord value2(Long value) {
        setWordId(value);
        return this;
    }

    @Override
    public WordEtymologyRecord value3(String value) {
        setEtymologyTypeCode(value);
        return this;
    }

    @Override
    public WordEtymologyRecord value4(String value) {
        setEtymologyYear(value);
        return this;
    }

    @Override
    public WordEtymologyRecord value5(String value) {
        setComment(value);
        return this;
    }

    @Override
    public WordEtymologyRecord value6(String value) {
        setCommentPrese(value);
        return this;
    }

    @Override
    public WordEtymologyRecord value7(Boolean value) {
        setIsQuestionable(value);
        return this;
    }

    @Override
    public WordEtymologyRecord value8(Long value) {
        setOrderBy(value);
        return this;
    }

    @Override
    public WordEtymologyRecord values(Long value1, Long value2, String value3, String value4, String value5, String value6, Boolean value7, Long value8) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        value7(value7);
        value8(value8);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached WordEtymologyRecord
     */
    public WordEtymologyRecord() {
        super(WordEtymology.WORD_ETYMOLOGY);
    }

    /**
     * Create a detached, initialised WordEtymologyRecord
     */
    public WordEtymologyRecord(Long id, Long wordId, String etymologyTypeCode, String etymologyYear, String comment, String commentPrese, Boolean isQuestionable, Long orderBy) {
        super(WordEtymology.WORD_ETYMOLOGY);

        setId(id);
        setWordId(wordId);
        setEtymologyTypeCode(etymologyTypeCode);
        setEtymologyYear(etymologyYear);
        setComment(comment);
        setCommentPrese(commentPrese);
        setIsQuestionable(isQuestionable);
        setOrderBy(orderBy);
    }
}
