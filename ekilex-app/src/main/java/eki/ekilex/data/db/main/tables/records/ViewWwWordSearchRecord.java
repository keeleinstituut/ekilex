/*
 * This file is generated by jOOQ.
 */
package eki.ekilex.data.db.main.tables.records;


import eki.ekilex.data.db.main.tables.ViewWwWordSearch;
import eki.ekilex.data.db.main.udt.records.TypeLangComplexityRecord;

import org.jooq.Field;
import org.jooq.Record6;
import org.jooq.Row6;
import org.jooq.impl.TableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class ViewWwWordSearchRecord extends TableRecordImpl<ViewWwWordSearchRecord> implements Record6<String, String, String, String[], Long, TypeLangComplexityRecord[]> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>public.view_ww_word_search.sgroup</code>.
     */
    public void setSgroup(String value) {
        set(0, value);
    }

    /**
     * Getter for <code>public.view_ww_word_search.sgroup</code>.
     */
    public String getSgroup() {
        return (String) get(0);
    }

    /**
     * Setter for <code>public.view_ww_word_search.word</code>.
     */
    public void setWord(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>public.view_ww_word_search.word</code>.
     */
    public String getWord() {
        return (String) get(1);
    }

    /**
     * Setter for <code>public.view_ww_word_search.crit</code>.
     */
    public void setCrit(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>public.view_ww_word_search.crit</code>.
     */
    public String getCrit() {
        return (String) get(2);
    }

    /**
     * Setter for <code>public.view_ww_word_search.langs_filt</code>.
     */
    public void setLangsFilt(String[] value) {
        set(3, value);
    }

    /**
     * Getter for <code>public.view_ww_word_search.langs_filt</code>.
     */
    public String[] getLangsFilt() {
        return (String[]) get(3);
    }

    /**
     * Setter for <code>public.view_ww_word_search.lang_order_by</code>.
     */
    public void setLangOrderBy(Long value) {
        set(4, value);
    }

    /**
     * Getter for <code>public.view_ww_word_search.lang_order_by</code>.
     */
    public Long getLangOrderBy() {
        return (Long) get(4);
    }

    /**
     * Setter for <code>public.view_ww_word_search.lang_complexities</code>.
     */
    public void setLangComplexities(TypeLangComplexityRecord[] value) {
        set(5, value);
    }

    /**
     * Getter for <code>public.view_ww_word_search.lang_complexities</code>.
     */
    public TypeLangComplexityRecord[] getLangComplexities() {
        return (TypeLangComplexityRecord[]) get(5);
    }

    // -------------------------------------------------------------------------
    // Record6 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row6<String, String, String, String[], Long, TypeLangComplexityRecord[]> fieldsRow() {
        return (Row6) super.fieldsRow();
    }

    @Override
    public Row6<String, String, String, String[], Long, TypeLangComplexityRecord[]> valuesRow() {
        return (Row6) super.valuesRow();
    }

    @Override
    public Field<String> field1() {
        return ViewWwWordSearch.VIEW_WW_WORD_SEARCH.SGROUP;
    }

    @Override
    public Field<String> field2() {
        return ViewWwWordSearch.VIEW_WW_WORD_SEARCH.WORD;
    }

    @Override
    public Field<String> field3() {
        return ViewWwWordSearch.VIEW_WW_WORD_SEARCH.CRIT;
    }

    @Override
    public Field<String[]> field4() {
        return ViewWwWordSearch.VIEW_WW_WORD_SEARCH.LANGS_FILT;
    }

    @Override
    public Field<Long> field5() {
        return ViewWwWordSearch.VIEW_WW_WORD_SEARCH.LANG_ORDER_BY;
    }

    @Override
    public Field<TypeLangComplexityRecord[]> field6() {
        return ViewWwWordSearch.VIEW_WW_WORD_SEARCH.LANG_COMPLEXITIES;
    }

    @Override
    public String component1() {
        return getSgroup();
    }

    @Override
    public String component2() {
        return getWord();
    }

    @Override
    public String component3() {
        return getCrit();
    }

    @Override
    public String[] component4() {
        return getLangsFilt();
    }

    @Override
    public Long component5() {
        return getLangOrderBy();
    }

    @Override
    public TypeLangComplexityRecord[] component6() {
        return getLangComplexities();
    }

    @Override
    public String value1() {
        return getSgroup();
    }

    @Override
    public String value2() {
        return getWord();
    }

    @Override
    public String value3() {
        return getCrit();
    }

    @Override
    public String[] value4() {
        return getLangsFilt();
    }

    @Override
    public Long value5() {
        return getLangOrderBy();
    }

    @Override
    public TypeLangComplexityRecord[] value6() {
        return getLangComplexities();
    }

    @Override
    public ViewWwWordSearchRecord value1(String value) {
        setSgroup(value);
        return this;
    }

    @Override
    public ViewWwWordSearchRecord value2(String value) {
        setWord(value);
        return this;
    }

    @Override
    public ViewWwWordSearchRecord value3(String value) {
        setCrit(value);
        return this;
    }

    @Override
    public ViewWwWordSearchRecord value4(String[] value) {
        setLangsFilt(value);
        return this;
    }

    @Override
    public ViewWwWordSearchRecord value5(Long value) {
        setLangOrderBy(value);
        return this;
    }

    @Override
    public ViewWwWordSearchRecord value6(TypeLangComplexityRecord[] value) {
        setLangComplexities(value);
        return this;
    }

    @Override
    public ViewWwWordSearchRecord values(String value1, String value2, String value3, String[] value4, Long value5, TypeLangComplexityRecord[] value6) {
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
     * Create a detached ViewWwWordSearchRecord
     */
    public ViewWwWordSearchRecord() {
        super(ViewWwWordSearch.VIEW_WW_WORD_SEARCH);
    }

    /**
     * Create a detached, initialised ViewWwWordSearchRecord
     */
    public ViewWwWordSearchRecord(String sgroup, String word, String crit, String[] langsFilt, Long langOrderBy, TypeLangComplexityRecord[] langComplexities) {
        super(ViewWwWordSearch.VIEW_WW_WORD_SEARCH);

        setSgroup(sgroup);
        setWord(word);
        setCrit(crit);
        setLangsFilt(langsFilt);
        setLangOrderBy(langOrderBy);
        setLangComplexities(langComplexities);
    }
}
