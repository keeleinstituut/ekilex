/*
 * This file is generated by jOOQ.
 */
package eki.ekilex.data.db.tables.records;


import eki.ekilex.data.db.tables.ViewWwWordEtymology;
import eki.ekilex.data.db.udt.records.TypeWordEtymRelationRecord;

import org.jooq.Field;
import org.jooq.Record12;
import org.jooq.Row12;
import org.jooq.impl.TableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class ViewWwWordEtymologyRecord extends TableRecordImpl<ViewWwWordEtymologyRecord> implements Record12<Long, Long, Long, String, String, String[], String, String, String, Boolean, Long, TypeWordEtymRelationRecord[]> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>public.view_ww_word_etymology.word_id</code>.
     */
    public void setWordId(Long value) {
        set(0, value);
    }

    /**
     * Getter for <code>public.view_ww_word_etymology.word_id</code>.
     */
    public Long getWordId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>public.view_ww_word_etymology.word_etym_id</code>.
     */
    public void setWordEtymId(Long value) {
        set(1, value);
    }

    /**
     * Getter for <code>public.view_ww_word_etymology.word_etym_id</code>.
     */
    public Long getWordEtymId() {
        return (Long) get(1);
    }

    /**
     * Setter for <code>public.view_ww_word_etymology.word_etym_word_id</code>.
     */
    public void setWordEtymWordId(Long value) {
        set(2, value);
    }

    /**
     * Getter for <code>public.view_ww_word_etymology.word_etym_word_id</code>.
     */
    public Long getWordEtymWordId() {
        return (Long) get(2);
    }

    /**
     * Setter for <code>public.view_ww_word_etymology.word</code>.
     */
    public void setWord(String value) {
        set(3, value);
    }

    /**
     * Getter for <code>public.view_ww_word_etymology.word</code>.
     */
    public String getWord() {
        return (String) get(3);
    }

    /**
     * Setter for <code>public.view_ww_word_etymology.word_lang</code>.
     */
    public void setWordLang(String value) {
        set(4, value);
    }

    /**
     * Getter for <code>public.view_ww_word_etymology.word_lang</code>.
     */
    public String getWordLang() {
        return (String) get(4);
    }

    /**
     * Setter for <code>public.view_ww_word_etymology.meaning_words</code>.
     */
    public void setMeaningWords(String[] value) {
        set(5, value);
    }

    /**
     * Getter for <code>public.view_ww_word_etymology.meaning_words</code>.
     */
    public String[] getMeaningWords() {
        return (String[]) get(5);
    }

    /**
     * Setter for <code>public.view_ww_word_etymology.etymology_type_code</code>.
     */
    public void setEtymologyTypeCode(String value) {
        set(6, value);
    }

    /**
     * Getter for <code>public.view_ww_word_etymology.etymology_type_code</code>.
     */
    public String getEtymologyTypeCode() {
        return (String) get(6);
    }

    /**
     * Setter for <code>public.view_ww_word_etymology.etymology_year</code>.
     */
    public void setEtymologyYear(String value) {
        set(7, value);
    }

    /**
     * Getter for <code>public.view_ww_word_etymology.etymology_year</code>.
     */
    public String getEtymologyYear() {
        return (String) get(7);
    }

    /**
     * Setter for <code>public.view_ww_word_etymology.word_etym_comment</code>.
     */
    public void setWordEtymComment(String value) {
        set(8, value);
    }

    /**
     * Getter for <code>public.view_ww_word_etymology.word_etym_comment</code>.
     */
    public String getWordEtymComment() {
        return (String) get(8);
    }

    /**
     * Setter for <code>public.view_ww_word_etymology.word_etym_is_questionable</code>.
     */
    public void setWordEtymIsQuestionable(Boolean value) {
        set(9, value);
    }

    /**
     * Getter for <code>public.view_ww_word_etymology.word_etym_is_questionable</code>.
     */
    public Boolean getWordEtymIsQuestionable() {
        return (Boolean) get(9);
    }

    /**
     * Setter for <code>public.view_ww_word_etymology.word_etym_order_by</code>.
     */
    public void setWordEtymOrderBy(Long value) {
        set(10, value);
    }

    /**
     * Getter for <code>public.view_ww_word_etymology.word_etym_order_by</code>.
     */
    public Long getWordEtymOrderBy() {
        return (Long) get(10);
    }

    /**
     * Setter for <code>public.view_ww_word_etymology.word_etym_relations</code>.
     */
    public void setWordEtymRelations(TypeWordEtymRelationRecord[] value) {
        set(11, value);
    }

    /**
     * Getter for <code>public.view_ww_word_etymology.word_etym_relations</code>.
     */
    public TypeWordEtymRelationRecord[] getWordEtymRelations() {
        return (TypeWordEtymRelationRecord[]) get(11);
    }

    // -------------------------------------------------------------------------
    // Record12 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row12<Long, Long, Long, String, String, String[], String, String, String, Boolean, Long, TypeWordEtymRelationRecord[]> fieldsRow() {
        return (Row12) super.fieldsRow();
    }

    @Override
    public Row12<Long, Long, Long, String, String, String[], String, String, String, Boolean, Long, TypeWordEtymRelationRecord[]> valuesRow() {
        return (Row12) super.valuesRow();
    }

    @Override
    public Field<Long> field1() {
        return ViewWwWordEtymology.VIEW_WW_WORD_ETYMOLOGY.WORD_ID;
    }

    @Override
    public Field<Long> field2() {
        return ViewWwWordEtymology.VIEW_WW_WORD_ETYMOLOGY.WORD_ETYM_ID;
    }

    @Override
    public Field<Long> field3() {
        return ViewWwWordEtymology.VIEW_WW_WORD_ETYMOLOGY.WORD_ETYM_WORD_ID;
    }

    @Override
    public Field<String> field4() {
        return ViewWwWordEtymology.VIEW_WW_WORD_ETYMOLOGY.WORD;
    }

    @Override
    public Field<String> field5() {
        return ViewWwWordEtymology.VIEW_WW_WORD_ETYMOLOGY.WORD_LANG;
    }

    @Override
    public Field<String[]> field6() {
        return ViewWwWordEtymology.VIEW_WW_WORD_ETYMOLOGY.MEANING_WORDS;
    }

    @Override
    public Field<String> field7() {
        return ViewWwWordEtymology.VIEW_WW_WORD_ETYMOLOGY.ETYMOLOGY_TYPE_CODE;
    }

    @Override
    public Field<String> field8() {
        return ViewWwWordEtymology.VIEW_WW_WORD_ETYMOLOGY.ETYMOLOGY_YEAR;
    }

    @Override
    public Field<String> field9() {
        return ViewWwWordEtymology.VIEW_WW_WORD_ETYMOLOGY.WORD_ETYM_COMMENT;
    }

    @Override
    public Field<Boolean> field10() {
        return ViewWwWordEtymology.VIEW_WW_WORD_ETYMOLOGY.WORD_ETYM_IS_QUESTIONABLE;
    }

    @Override
    public Field<Long> field11() {
        return ViewWwWordEtymology.VIEW_WW_WORD_ETYMOLOGY.WORD_ETYM_ORDER_BY;
    }

    @Override
    public Field<TypeWordEtymRelationRecord[]> field12() {
        return ViewWwWordEtymology.VIEW_WW_WORD_ETYMOLOGY.WORD_ETYM_RELATIONS;
    }

    @Override
    public Long component1() {
        return getWordId();
    }

    @Override
    public Long component2() {
        return getWordEtymId();
    }

    @Override
    public Long component3() {
        return getWordEtymWordId();
    }

    @Override
    public String component4() {
        return getWord();
    }

    @Override
    public String component5() {
        return getWordLang();
    }

    @Override
    public String[] component6() {
        return getMeaningWords();
    }

    @Override
    public String component7() {
        return getEtymologyTypeCode();
    }

    @Override
    public String component8() {
        return getEtymologyYear();
    }

    @Override
    public String component9() {
        return getWordEtymComment();
    }

    @Override
    public Boolean component10() {
        return getWordEtymIsQuestionable();
    }

    @Override
    public Long component11() {
        return getWordEtymOrderBy();
    }

    @Override
    public TypeWordEtymRelationRecord[] component12() {
        return getWordEtymRelations();
    }

    @Override
    public Long value1() {
        return getWordId();
    }

    @Override
    public Long value2() {
        return getWordEtymId();
    }

    @Override
    public Long value3() {
        return getWordEtymWordId();
    }

    @Override
    public String value4() {
        return getWord();
    }

    @Override
    public String value5() {
        return getWordLang();
    }

    @Override
    public String[] value6() {
        return getMeaningWords();
    }

    @Override
    public String value7() {
        return getEtymologyTypeCode();
    }

    @Override
    public String value8() {
        return getEtymologyYear();
    }

    @Override
    public String value9() {
        return getWordEtymComment();
    }

    @Override
    public Boolean value10() {
        return getWordEtymIsQuestionable();
    }

    @Override
    public Long value11() {
        return getWordEtymOrderBy();
    }

    @Override
    public TypeWordEtymRelationRecord[] value12() {
        return getWordEtymRelations();
    }

    @Override
    public ViewWwWordEtymologyRecord value1(Long value) {
        setWordId(value);
        return this;
    }

    @Override
    public ViewWwWordEtymologyRecord value2(Long value) {
        setWordEtymId(value);
        return this;
    }

    @Override
    public ViewWwWordEtymologyRecord value3(Long value) {
        setWordEtymWordId(value);
        return this;
    }

    @Override
    public ViewWwWordEtymologyRecord value4(String value) {
        setWord(value);
        return this;
    }

    @Override
    public ViewWwWordEtymologyRecord value5(String value) {
        setWordLang(value);
        return this;
    }

    @Override
    public ViewWwWordEtymologyRecord value6(String[] value) {
        setMeaningWords(value);
        return this;
    }

    @Override
    public ViewWwWordEtymologyRecord value7(String value) {
        setEtymologyTypeCode(value);
        return this;
    }

    @Override
    public ViewWwWordEtymologyRecord value8(String value) {
        setEtymologyYear(value);
        return this;
    }

    @Override
    public ViewWwWordEtymologyRecord value9(String value) {
        setWordEtymComment(value);
        return this;
    }

    @Override
    public ViewWwWordEtymologyRecord value10(Boolean value) {
        setWordEtymIsQuestionable(value);
        return this;
    }

    @Override
    public ViewWwWordEtymologyRecord value11(Long value) {
        setWordEtymOrderBy(value);
        return this;
    }

    @Override
    public ViewWwWordEtymologyRecord value12(TypeWordEtymRelationRecord[] value) {
        setWordEtymRelations(value);
        return this;
    }

    @Override
    public ViewWwWordEtymologyRecord values(Long value1, Long value2, Long value3, String value4, String value5, String[] value6, String value7, String value8, String value9, Boolean value10, Long value11, TypeWordEtymRelationRecord[] value12) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        value7(value7);
        value8(value8);
        value9(value9);
        value10(value10);
        value11(value11);
        value12(value12);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached ViewWwWordEtymologyRecord
     */
    public ViewWwWordEtymologyRecord() {
        super(ViewWwWordEtymology.VIEW_WW_WORD_ETYMOLOGY);
    }

    /**
     * Create a detached, initialised ViewWwWordEtymologyRecord
     */
    public ViewWwWordEtymologyRecord(Long wordId, Long wordEtymId, Long wordEtymWordId, String word, String wordLang, String[] meaningWords, String etymologyTypeCode, String etymologyYear, String wordEtymComment, Boolean wordEtymIsQuestionable, Long wordEtymOrderBy, TypeWordEtymRelationRecord[] wordEtymRelations) {
        super(ViewWwWordEtymology.VIEW_WW_WORD_ETYMOLOGY);

        setWordId(wordId);
        setWordEtymId(wordEtymId);
        setWordEtymWordId(wordEtymWordId);
        setWord(word);
        setWordLang(wordLang);
        setMeaningWords(meaningWords);
        setEtymologyTypeCode(etymologyTypeCode);
        setEtymologyYear(etymologyYear);
        setWordEtymComment(wordEtymComment);
        setWordEtymIsQuestionable(wordEtymIsQuestionable);
        setWordEtymOrderBy(wordEtymOrderBy);
        setWordEtymRelations(wordEtymRelations);
    }
}
