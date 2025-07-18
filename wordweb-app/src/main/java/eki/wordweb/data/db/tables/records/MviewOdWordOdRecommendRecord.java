/*
 * This file is generated by jOOQ.
 */
package eki.wordweb.data.db.tables.records;


import eki.wordweb.data.db.tables.MviewOdWordOdRecommend;

import org.jooq.Field;
import org.jooq.Record6;
import org.jooq.Row6;
import org.jooq.impl.TableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class MviewOdWordOdRecommendRecord extends TableRecordImpl<MviewOdWordOdRecommendRecord> implements Record6<Long, Long, String, String, String, String> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>public.mview_od_word_od_recommend.word_id</code>.
     */
    public void setWordId(Long value) {
        set(0, value);
    }

    /**
     * Getter for <code>public.mview_od_word_od_recommend.word_id</code>.
     */
    public Long getWordId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>public.mview_od_word_od_recommend.word_od_recommend_id</code>.
     */
    public void setWordOdRecommendId(Long value) {
        set(1, value);
    }

    /**
     * Getter for <code>public.mview_od_word_od_recommend.word_od_recommend_id</code>.
     */
    public Long getWordOdRecommendId() {
        return (Long) get(1);
    }

    /**
     * Setter for <code>public.mview_od_word_od_recommend.value</code>.
     */
    public void setValue(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>public.mview_od_word_od_recommend.value</code>.
     */
    public String getValue() {
        return (String) get(2);
    }

    /**
     * Setter for <code>public.mview_od_word_od_recommend.value_prese</code>.
     */
    public void setValuePrese(String value) {
        set(3, value);
    }

    /**
     * Getter for <code>public.mview_od_word_od_recommend.value_prese</code>.
     */
    public String getValuePrese() {
        return (String) get(3);
    }

    /**
     * Setter for <code>public.mview_od_word_od_recommend.opt_value</code>.
     */
    public void setOptValue(String value) {
        set(4, value);
    }

    /**
     * Getter for <code>public.mview_od_word_od_recommend.opt_value</code>.
     */
    public String getOptValue() {
        return (String) get(4);
    }

    /**
     * Setter for <code>public.mview_od_word_od_recommend.opt_value_prese</code>.
     */
    public void setOptValuePrese(String value) {
        set(5, value);
    }

    /**
     * Getter for <code>public.mview_od_word_od_recommend.opt_value_prese</code>.
     */
    public String getOptValuePrese() {
        return (String) get(5);
    }

    // -------------------------------------------------------------------------
    // Record6 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row6<Long, Long, String, String, String, String> fieldsRow() {
        return (Row6) super.fieldsRow();
    }

    @Override
    public Row6<Long, Long, String, String, String, String> valuesRow() {
        return (Row6) super.valuesRow();
    }

    @Override
    public Field<Long> field1() {
        return MviewOdWordOdRecommend.MVIEW_OD_WORD_OD_RECOMMEND.WORD_ID;
    }

    @Override
    public Field<Long> field2() {
        return MviewOdWordOdRecommend.MVIEW_OD_WORD_OD_RECOMMEND.WORD_OD_RECOMMEND_ID;
    }

    @Override
    public Field<String> field3() {
        return MviewOdWordOdRecommend.MVIEW_OD_WORD_OD_RECOMMEND.VALUE;
    }

    @Override
    public Field<String> field4() {
        return MviewOdWordOdRecommend.MVIEW_OD_WORD_OD_RECOMMEND.VALUE_PRESE;
    }

    @Override
    public Field<String> field5() {
        return MviewOdWordOdRecommend.MVIEW_OD_WORD_OD_RECOMMEND.OPT_VALUE;
    }

    @Override
    public Field<String> field6() {
        return MviewOdWordOdRecommend.MVIEW_OD_WORD_OD_RECOMMEND.OPT_VALUE_PRESE;
    }

    @Override
    public Long component1() {
        return getWordId();
    }

    @Override
    public Long component2() {
        return getWordOdRecommendId();
    }

    @Override
    public String component3() {
        return getValue();
    }

    @Override
    public String component4() {
        return getValuePrese();
    }

    @Override
    public String component5() {
        return getOptValue();
    }

    @Override
    public String component6() {
        return getOptValuePrese();
    }

    @Override
    public Long value1() {
        return getWordId();
    }

    @Override
    public Long value2() {
        return getWordOdRecommendId();
    }

    @Override
    public String value3() {
        return getValue();
    }

    @Override
    public String value4() {
        return getValuePrese();
    }

    @Override
    public String value5() {
        return getOptValue();
    }

    @Override
    public String value6() {
        return getOptValuePrese();
    }

    @Override
    public MviewOdWordOdRecommendRecord value1(Long value) {
        setWordId(value);
        return this;
    }

    @Override
    public MviewOdWordOdRecommendRecord value2(Long value) {
        setWordOdRecommendId(value);
        return this;
    }

    @Override
    public MviewOdWordOdRecommendRecord value3(String value) {
        setValue(value);
        return this;
    }

    @Override
    public MviewOdWordOdRecommendRecord value4(String value) {
        setValuePrese(value);
        return this;
    }

    @Override
    public MviewOdWordOdRecommendRecord value5(String value) {
        setOptValue(value);
        return this;
    }

    @Override
    public MviewOdWordOdRecommendRecord value6(String value) {
        setOptValuePrese(value);
        return this;
    }

    @Override
    public MviewOdWordOdRecommendRecord values(Long value1, Long value2, String value3, String value4, String value5, String value6) {
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
     * Create a detached MviewOdWordOdRecommendRecord
     */
    public MviewOdWordOdRecommendRecord() {
        super(MviewOdWordOdRecommend.MVIEW_OD_WORD_OD_RECOMMEND);
    }

    /**
     * Create a detached, initialised MviewOdWordOdRecommendRecord
     */
    public MviewOdWordOdRecommendRecord(Long wordId, Long wordOdRecommendId, String value, String valuePrese, String optValue, String optValuePrese) {
        super(MviewOdWordOdRecommend.MVIEW_OD_WORD_OD_RECOMMEND);

        setWordId(wordId);
        setWordOdRecommendId(wordOdRecommendId);
        setValue(value);
        setValuePrese(valuePrese);
        setOptValue(optValue);
        setOptValuePrese(optValuePrese);
    }
}
