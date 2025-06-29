/*
 * This file is generated by jOOQ.
 */
package eki.ekilex.data.db.main.tables.records;


import eki.ekilex.data.db.main.tables.ViewOdWordRelationIdx;

import org.jooq.Field;
import org.jooq.Record5;
import org.jooq.Row5;
import org.jooq.impl.TableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class ViewOdWordRelationIdxRecord extends TableRecordImpl<ViewOdWordRelationIdxRecord> implements Record5<Long, String, Long, String, String> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>public.view_od_word_relation_idx.word_id</code>.
     */
    public void setWordId(Long value) {
        set(0, value);
    }

    /**
     * Getter for <code>public.view_od_word_relation_idx.word_id</code>.
     */
    public Long getWordId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>public.view_od_word_relation_idx.word_rel_type_code</code>.
     */
    public void setWordRelTypeCode(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>public.view_od_word_relation_idx.word_rel_type_code</code>.
     */
    public String getWordRelTypeCode() {
        return (String) get(1);
    }

    /**
     * Setter for <code>public.view_od_word_relation_idx.related_word_id</code>.
     */
    public void setRelatedWordId(Long value) {
        set(2, value);
    }

    /**
     * Getter for <code>public.view_od_word_relation_idx.related_word_id</code>.
     */
    public Long getRelatedWordId() {
        return (Long) get(2);
    }

    /**
     * Setter for <code>public.view_od_word_relation_idx.value</code>.
     */
    public void setValue(String value) {
        set(3, value);
    }

    /**
     * Getter for <code>public.view_od_word_relation_idx.value</code>.
     */
    public String getValue() {
        return (String) get(3);
    }

    /**
     * Setter for <code>public.view_od_word_relation_idx.value_as_word</code>.
     */
    public void setValueAsWord(String value) {
        set(4, value);
    }

    /**
     * Getter for <code>public.view_od_word_relation_idx.value_as_word</code>.
     */
    public String getValueAsWord() {
        return (String) get(4);
    }

    // -------------------------------------------------------------------------
    // Record5 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row5<Long, String, Long, String, String> fieldsRow() {
        return (Row5) super.fieldsRow();
    }

    @Override
    public Row5<Long, String, Long, String, String> valuesRow() {
        return (Row5) super.valuesRow();
    }

    @Override
    public Field<Long> field1() {
        return ViewOdWordRelationIdx.VIEW_OD_WORD_RELATION_IDX.WORD_ID;
    }

    @Override
    public Field<String> field2() {
        return ViewOdWordRelationIdx.VIEW_OD_WORD_RELATION_IDX.WORD_REL_TYPE_CODE;
    }

    @Override
    public Field<Long> field3() {
        return ViewOdWordRelationIdx.VIEW_OD_WORD_RELATION_IDX.RELATED_WORD_ID;
    }

    @Override
    public Field<String> field4() {
        return ViewOdWordRelationIdx.VIEW_OD_WORD_RELATION_IDX.VALUE;
    }

    @Override
    public Field<String> field5() {
        return ViewOdWordRelationIdx.VIEW_OD_WORD_RELATION_IDX.VALUE_AS_WORD;
    }

    @Override
    public Long component1() {
        return getWordId();
    }

    @Override
    public String component2() {
        return getWordRelTypeCode();
    }

    @Override
    public Long component3() {
        return getRelatedWordId();
    }

    @Override
    public String component4() {
        return getValue();
    }

    @Override
    public String component5() {
        return getValueAsWord();
    }

    @Override
    public Long value1() {
        return getWordId();
    }

    @Override
    public String value2() {
        return getWordRelTypeCode();
    }

    @Override
    public Long value3() {
        return getRelatedWordId();
    }

    @Override
    public String value4() {
        return getValue();
    }

    @Override
    public String value5() {
        return getValueAsWord();
    }

    @Override
    public ViewOdWordRelationIdxRecord value1(Long value) {
        setWordId(value);
        return this;
    }

    @Override
    public ViewOdWordRelationIdxRecord value2(String value) {
        setWordRelTypeCode(value);
        return this;
    }

    @Override
    public ViewOdWordRelationIdxRecord value3(Long value) {
        setRelatedWordId(value);
        return this;
    }

    @Override
    public ViewOdWordRelationIdxRecord value4(String value) {
        setValue(value);
        return this;
    }

    @Override
    public ViewOdWordRelationIdxRecord value5(String value) {
        setValueAsWord(value);
        return this;
    }

    @Override
    public ViewOdWordRelationIdxRecord values(Long value1, String value2, Long value3, String value4, String value5) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached ViewOdWordRelationIdxRecord
     */
    public ViewOdWordRelationIdxRecord() {
        super(ViewOdWordRelationIdx.VIEW_OD_WORD_RELATION_IDX);
    }

    /**
     * Create a detached, initialised ViewOdWordRelationIdxRecord
     */
    public ViewOdWordRelationIdxRecord(Long wordId, String wordRelTypeCode, Long relatedWordId, String value, String valueAsWord) {
        super(ViewOdWordRelationIdx.VIEW_OD_WORD_RELATION_IDX);

        setWordId(wordId);
        setWordRelTypeCode(wordRelTypeCode);
        setRelatedWordId(relatedWordId);
        setValue(value);
        setValueAsWord(valueAsWord);
    }
}
